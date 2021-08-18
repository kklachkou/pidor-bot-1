package by.kobyzau.tg.bot.pbot.handlers.update.impl.game;

import by.kobyzau.tg.bot.pbot.bots.Bot;
import by.kobyzau.tg.bot.pbot.bots.game.election.ElectionFinalizer;
import by.kobyzau.tg.bot.pbot.collectors.BotActionCollector;
import by.kobyzau.tg.bot.pbot.games.election.stat.ElectionStatPrinter;
import by.kobyzau.tg.bot.pbot.handlers.update.UpdateHandler;
import by.kobyzau.tg.bot.pbot.handlers.update.UpdateHandlerStage;
import by.kobyzau.tg.bot.pbot.model.Pidor;
import by.kobyzau.tg.bot.pbot.model.dto.SerializableInlineType;
import by.kobyzau.tg.bot.pbot.model.dto.VoteInlineMessageInlineDto;
import by.kobyzau.tg.bot.pbot.program.logger.Logger;
import by.kobyzau.tg.bot.pbot.program.selection.ConsistentSelection;
import by.kobyzau.tg.bot.pbot.program.selection.Selection;
import by.kobyzau.tg.bot.pbot.program.text.RandomText;
import by.kobyzau.tg.bot.pbot.program.text.SimpleText;
import by.kobyzau.tg.bot.pbot.program.text.Text;
import by.kobyzau.tg.bot.pbot.repository.dailypidor.DailyPidorRepository;
import by.kobyzau.tg.bot.pbot.service.ChatSettingsService;
import by.kobyzau.tg.bot.pbot.service.ElectionService;
import by.kobyzau.tg.bot.pbot.service.PidorService;
import by.kobyzau.tg.bot.pbot.tg.action.EditMessageBotAction;
import by.kobyzau.tg.bot.pbot.util.DateUtil;
import by.kobyzau.tg.bot.pbot.util.StringUtil;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.Executor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;

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
  @Autowired private Bot bot;
  @Autowired private Logger logger;

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
      try {
        bot.execute(
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
                .build());
      } catch (Exception e) {
        logger.error("Cannot answer callback", e);
      }
      return true;
    }
    if (!electionService.canUserVote(prevMessage.getChatId(), calledUser.getId())) {
      try {
        bot.execute(
            AnswerCallbackQuery.builder()
                .callbackQueryId(callbackQuery.getId())
                .showAlert(true)
                .text("Ты уже голосовал")
                .cacheTime(10)
                .build());
      } catch (Exception e) {
        logger.error("Cannot answer callback", e);
      }
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
      electionService.saveVote(chatId, calledPidor.get().getTgId(), targetPidor.get().getTgId());
      try {
        bot.execute(
            AnswerCallbackQuery.builder()
                .callbackQueryId(callbackQuery.getId())
                .showAlert(true)
                .text("Твой голос засчитан")
                .cacheTime(10)
                .build());
      } catch (Exception e) {
        logger.error("Cannot answer callback", e);
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

  private boolean needToFinalize(long chatId) {
    LocalDate now = DateUtil.now();
    long pidorNumber = pidorService.getByChat(chatId).size();
    int numPidorsToPlay = electionService.getNumToVote(chatId);
    long numVotes = electionService.getNumVotes(chatId, now);
    return (pidorNumber == numVotes || numVotes >= numPidorsToPlay) && !hasPidorOfTheDay(chatId);
  }
}
