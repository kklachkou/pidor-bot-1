package by.kobyzau.tg.bot.pbot.handlers.command.handler;

import by.kobyzau.tg.bot.pbot.artifacts.ArtifactType;
import by.kobyzau.tg.bot.pbot.artifacts.service.UserArtifactService;
import by.kobyzau.tg.bot.pbot.collectors.BotActionCollector;
import by.kobyzau.tg.bot.pbot.games.election.stat.ElectionStatPrinter;
import by.kobyzau.tg.bot.pbot.handlers.command.Command;
import by.kobyzau.tg.bot.pbot.model.Pidor;
import by.kobyzau.tg.bot.pbot.model.dto.VoteInlineMessageInlineDto;
import by.kobyzau.tg.bot.pbot.program.text.ParametizedText;
import by.kobyzau.tg.bot.pbot.program.text.RandomText;
import by.kobyzau.tg.bot.pbot.program.text.SimpleText;
import by.kobyzau.tg.bot.pbot.program.text.pidor.ShortNamePidorText;
import by.kobyzau.tg.bot.pbot.service.ChatSettingsService;
import by.kobyzau.tg.bot.pbot.service.ElectionService;
import by.kobyzau.tg.bot.pbot.service.PidorService;
import by.kobyzau.tg.bot.pbot.tg.ChatAction;
import by.kobyzau.tg.bot.pbot.tg.action.ReplyKeyboardBotAction;
import by.kobyzau.tg.bot.pbot.tg.sticker.StickerType;
import by.kobyzau.tg.bot.pbot.util.CollectionUtil;
import by.kobyzau.tg.bot.pbot.util.DateUtil;
import by.kobyzau.tg.bot.pbot.util.PidorUtil;
import by.kobyzau.tg.bot.pbot.util.StringUtil;
import by.kobyzau.tg.bot.pbot.util.helper.DateHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.time.LocalDateTime;
import java.util.*;

import static by.kobyzau.tg.bot.pbot.model.dto.SerializableInlineType.VOTE;
import static by.kobyzau.tg.bot.pbot.service.ChatSettingsService.ChatCheckboxSettingType.ELECTION_HIDDEN;

@Component
public class ElectionCommandHandler implements CommandHandler {

  @Autowired private PidorService pidorService;

  @Autowired private ElectionService electionService;

  @Autowired private BotActionCollector botActionCollector;

  @Autowired private ElectionStatPrinter fullWithNumLeftElectionStatPrinter;
  @Autowired private ElectionStatPrinter anotherNamesWithNumLeftElectionStatPrinter;
  @Autowired private ChatSettingsService chatSettingsService;
  @Autowired private UserArtifactService userArtifactService;
  @Autowired private DateHelper dateHelper;

  private final Map<String, LocalDateTime> lastCallsCache = new HashMap<>();

  @Override
  public void processCommand(Message message, String text) {
    long chatId = message.getChatId();
    long callerId = message.getFrom().getId();
    if (!electionService.canUserVote(chatId, callerId)) {
      botActionCollector.text(chatId, new SimpleText("???? ?????? ??????????????????"), message.getMessageId());
      if (chatSettingsService.isEnabled(ELECTION_HIDDEN, chatId)) {
        anotherNamesWithNumLeftElectionStatPrinter.printInfo(chatId);
      } else {
        fullWithNumLeftElectionStatPrinter.printInfo(chatId);
      }
      return;
    }
    String cacheKey = chatId + ":" + callerId;
    LocalDateTime lastCall = lastCallsCache.get(cacheKey);
    if (lastCall != null && lastCall.plusSeconds(10).isAfter(DateUtil.currentTime())) {
      botActionCollector.text(
          chatId, new SimpleText("???? ?????? ??????????, ?????????????? ???????? ????????????"), message.getMessageId());
      return;
    }
    lastCallsCache.put(cacheKey, DateUtil.currentTime());
    Optional<Pidor> callerPidor = pidorService.getPidor(chatId, callerId);
    if (!callerPidor.isPresent()) {
      botActionCollector.text(
          chatId, new SimpleText("?????????????? ??????????????????????????????"), message.getMessageId());
      return;
    }
    List<Pidor> pidors = CollectionUtil.getRandomList(pidorService.getByChat(chatId));
    if (pidors.size() < 2) {
      botActionCollector.text(chatId, new SimpleText("?? ???????? ?????????????? ???????? ?????????? ?????? ???????? ????????"));
      botActionCollector.wait(chatId, 1, ChatAction.TYPING);
      botActionCollector.sticker(chatId, StickerType.SAD.getRandom());
      return;
    }
    if (hasSilenceArtifact(chatId, message.getFrom().getId())) {
      botActionCollector.text(chatId, new SimpleText("\uD83D\uDE49"), message.getMessageId());
      return;
    }
    Map<Long, String> anonymousNames = PidorUtil.getAnonymousNames(pidors, DateUtil.now());
    InlineKeyboardMarkup.InlineKeyboardMarkupBuilder keyboardMarkupBuilder =
        InlineKeyboardMarkup.builder();
    String requestId = UUID.randomUUID().toString().substring(VOTE.getIdSize());
    pidors.stream()
        .filter(p -> p.getTgId() != callerId)
        .filter(p -> StringUtil.isNotBlank(new ShortNamePidorText(p).text()))
        .limit(15)
        .map(
            p ->
                InlineKeyboardButton.builder()
                    .text(
                        "- "
                            + (hasBlindnessArtifact(chatId, message.getFrom().getId())
                                ? new SimpleText(anonymousNames.getOrDefault(p.getId(), "????????????"))
                                : new ShortNamePidorText(p)))
                    .callbackData(
                        StringUtil.serialize(
                            new VoteInlineMessageInlineDto(requestId, p.getTgId())))
                    .build())
        .map(Collections::singletonList)
        .forEach(keyboardMarkupBuilder::keyboardRow);

    botActionCollector.add(
        new ReplyKeyboardBotAction(
            chatId,
            new ParametizedText(
                new RandomText(
                    "{0}, ???? ??????, ???????? ???? ???????????????? ???????????????",
                    "{0}, ???????????????? ???????????? ???????",
                    "{0}, ???? ???????? ???? ???????????? ?????????????????????",
                    "{0}, ???????? ???? ???????????????? ???????????????",
                    "?????????????????? ?????? {0}",
                    "{0}, ???? ??????, ???????? ???? ???????????????? ???????????????"),
                new ShortNamePidorText(callerPidor.get())),
            keyboardMarkupBuilder.build(),
            message.getMessageId()));
  }

  private boolean hasSilenceArtifact(long chatId, long userId) {
    return userArtifactService.getUserArtifact(chatId, userId, ArtifactType.SILENCE).isPresent()
        && dateHelper.currentTime().getHour() < 12;
  }

  private boolean hasBlindnessArtifact(long chatId, long userId) {
    return userArtifactService.getUserArtifact(chatId, userId, ArtifactType.BLINDNESS).isPresent();
  }

  @Override
  public Command getCommand() {
    return Command.ELECTION;
  }
}
