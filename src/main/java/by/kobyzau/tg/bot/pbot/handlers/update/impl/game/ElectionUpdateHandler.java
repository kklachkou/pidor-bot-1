package by.kobyzau.tg.bot.pbot.handlers.update.impl.game;

import by.kobyzau.tg.bot.pbot.artifacts.ArtifactType;
import by.kobyzau.tg.bot.pbot.artifacts.service.UserArtifactService;
import by.kobyzau.tg.bot.pbot.bots.game.election.ElectionFinalizer;
import by.kobyzau.tg.bot.pbot.collectors.BotActionCollector;
import by.kobyzau.tg.bot.pbot.games.election.stat.ElectionStatPrinter;
import by.kobyzau.tg.bot.pbot.handlers.update.UpdateHandler;
import by.kobyzau.tg.bot.pbot.handlers.update.UpdateHandlerStage;
import by.kobyzau.tg.bot.pbot.model.Pidor;
import by.kobyzau.tg.bot.pbot.model.dto.SerializableInlineType;
import by.kobyzau.tg.bot.pbot.model.dto.VoteInlineMessageInlineDto;
import by.kobyzau.tg.bot.pbot.program.selection.ConsistentSelection;
import by.kobyzau.tg.bot.pbot.program.selection.Selection;
import by.kobyzau.tg.bot.pbot.program.text.ParametizedText;
import by.kobyzau.tg.bot.pbot.program.text.RandomText;
import by.kobyzau.tg.bot.pbot.program.text.SimpleText;
import by.kobyzau.tg.bot.pbot.program.text.Text;
import by.kobyzau.tg.bot.pbot.program.text.pidor.ShortNamePidorText;
import by.kobyzau.tg.bot.pbot.repository.dailypidor.DailyPidorRepository;
import by.kobyzau.tg.bot.pbot.sender.BotSender;
import by.kobyzau.tg.bot.pbot.service.ChatSettingsService;
import by.kobyzau.tg.bot.pbot.service.ElectionService;
import by.kobyzau.tg.bot.pbot.service.PidorService;
import by.kobyzau.tg.bot.pbot.tg.action.EditMessageBotAction;
import by.kobyzau.tg.bot.pbot.util.DateUtil;
import by.kobyzau.tg.bot.pbot.util.StringUtil;
import by.kobyzau.tg.bot.pbot.util.helper.CollectionHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.Executor;

import static by.kobyzau.tg.bot.pbot.sender.methods.SendMethod.method;
import static by.kobyzau.tg.bot.pbot.service.ChatSettingsService.ChatCheckboxSettingType.ELECTION_HIDDEN;

@Component
public class ElectionUpdateHandler implements UpdateHandler {

  @Autowired private ElectionService electionService;
  @Autowired private BotActionCollector botActionCollector;
  @Autowired private PidorService pidorService;
  @Autowired private DailyPidorRepository dailyPidorRepository;
  @Autowired private ElectionFinalizer electionFinalizer;
  @Autowired private ElectionStatPrinter fullWithNumLeftElectionStatPrinter;
  @Autowired private ElectionStatPrinter anotherNamesWithNumLeftElectionStatPrinter;
  @Autowired private ChatSettingsService chatSettingsService;
  @Autowired private UserArtifactService userArtifactService;
  @Autowired private BotSender directPidorBotSender;
  @Autowired private CollectionHelper collectionHelper;

  @Autowired
  @Qualifier("cachedExecutor")
  private Executor executor;

  @Value("${bot.pidor.username}")
  private String botUserName;

  private final Selection<Text> emoji =
      new ConsistentSelection<>("\uD83D\uDC7B", "\uD83D\uDCA9", "\uD83D\uDC80", "\uD83C\uDF83")
          .map(SimpleText::new);

  private final Set<String> handledRequests = new HashSet<>();

  @Override
  public UpdateHandlerStage getStage() {
    return UpdateHandlerStage.GAME;
  }

  @Override
  public boolean handleUpdate(Update update) {
    CallbackQuery callbackQuery = update.getCallbackQuery();
    if (callbackQuery == null) {
      return false;
    }
    Message prevMessage = callbackQuery.getMessage();
    User calledUser = callbackQuery.getFrom();
    Optional<VoteInlineMessageInlineDto> data =
        StringUtil.deserialize(callbackQuery.getData(), VoteInlineMessageInlineDto.class);
    if (prevMessage == null
        || calledUser == null
        || prevMessage.getFrom() == null
        || !data.isPresent()
        || !Objects.equals(SerializableInlineType.VOTE.getIndex(), data.get().getIndex())
        || handledRequests.contains(data.get().getId())
        || !botUserName.equalsIgnoreCase(prevMessage.getFrom().getUserName())) {
      return false;
    }
    if (!electionService.isElectionDay(prevMessage.getChatId(), DateUtil.now())) {
      return false;
    }
    Message replyToMessage = prevMessage.getReplyToMessage();
    if (replyToMessage == null
        || !Objects.equals(calledUser.getId(), replyToMessage.getFrom().getId())) {
      directPidorBotSender.send(
          prevMessage.getChatId(),
          method(
              AnswerCallbackQuery.builder()
                  .callbackQueryId(callbackQuery.getId())
                  .showAlert(true)
                  .text(
                      new RandomText(
                              "Не трогай чужие биллютени",
                              "Это не твой биллютень",
                              "Используй для голосования свой биллютень")
                          .text())
                  .cacheTime(10)
                  .build()));
      return true;
    }
    if (!electionService.canUserVote(prevMessage.getChatId(), calledUser.getId())) {
      directPidorBotSender.send(
          prevMessage.getChatId(),
          method(
              AnswerCallbackQuery.builder()
                  .callbackQueryId(callbackQuery.getId())
                  .showAlert(true)
                  .text("Ты уже голосовал")
                  .cacheTime(10)
                  .build()));
      removeInlineMessage(prevMessage);
      return true;
    }
    handledRequests.add(data.get().getId());
    long chatId = prevMessage.getChatId();
    removeInlineMessage(prevMessage);
    if (hasPidorOfTheDay(chatId)) {
      return true;
    }
    Optional<Pidor> targetPidor = pidorService.getPidor(chatId, data.get().getTargetId());
    Optional<Pidor> calledPidor = pidorService.getPidor(chatId, calledUser.getId());
    if (targetPidor.isPresent() && calledPidor.isPresent()) {
      if (isRicochet(chatId, targetPidor.get().getTgId())) {
        botActionCollector.text(
            chatId,
            new ParametizedText(
                "И голос отрикошетил в {0}\uD83D\uDE00", new ShortNamePidorText(calledPidor.get())),
            prevMessage.getMessageId());
        electionService.saveVote(chatId, calledPidor.get().getTgId(), calledPidor.get().getTgId());
        directPidorBotSender.send(
            chatId,
            method(
                AnswerCallbackQuery.builder()
                    .callbackQueryId(callbackQuery.getId())
                    .showAlert(true)
                    .text("Извини, но произошел рикошет" + ArtifactType.RICOCHET.getEmoji())
                    .cacheTime(10)
                    .build()));
      } else {
        if (hasSuperVote(chatId, calledPidor.get().getTgId())) {
          electionService.saveSuperVote(
              chatId, calledPidor.get().getTgId(), targetPidor.get().getTgId());
        } else {
          electionService.saveVote(
              chatId, calledPidor.get().getTgId(), targetPidor.get().getTgId());
        }
        directPidorBotSender.send(
            chatId,
            method(
                AnswerCallbackQuery.builder()
                    .callbackQueryId(callbackQuery.getId())
                    .showAlert(true)
                    .text("Твой голос засчитан")
                    .cacheTime(10)
                    .build()));
      }

      if (needToFinalize(chatId)) {
        executor.execute(() -> electionFinalizer.finalize(chatId));
      } else {
        if (chatSettingsService.isEnabled(ELECTION_HIDDEN, chatId)) {
          anotherNamesWithNumLeftElectionStatPrinter.printInfo(chatId);
        } else {
          fullWithNumLeftElectionStatPrinter.printInfo(chatId);
        }
      }
      return true;
    }
    return true;
  }

  private boolean hasPidorOfTheDay(long chatId) {
    return dailyPidorRepository.getByChatAndDate(chatId, DateUtil.now()).isPresent();
  }

  private void removeInlineMessage(Message message) {
    botActionCollector.add(new EditMessageBotAction(message, emoji.next()));
  }

  private boolean isRicochet(long chatId, long targetUserId) {
    return userArtifactService
            .getUserArtifact(chatId, targetUserId, ArtifactType.RICOCHET)
            .isPresent()
        && collectionHelper.getRandomValue(Arrays.asList(true, false));
  }

  private boolean hasSuperVote(long chatId, long userId) {
    return userArtifactService.getUserArtifact(chatId, userId, ArtifactType.SUPER_VOTE).isPresent();
  }

  private boolean needToFinalize(long chatId) {
    LocalDate now = DateUtil.now();
    long pidorNumber = pidorService.getByChat(chatId).size();
    int numPidorsToPlay = electionService.getNumToVote(chatId);
    long numVotes = electionService.getNumVotes(chatId, now);
    return (pidorNumber == numVotes || numVotes >= numPidorsToPlay) && !hasPidorOfTheDay(chatId);
  }
}
