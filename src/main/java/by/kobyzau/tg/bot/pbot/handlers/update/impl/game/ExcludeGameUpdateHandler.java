package by.kobyzau.tg.bot.pbot.handlers.update.impl.game;

import by.kobyzau.tg.bot.pbot.artifacts.ArtifactType;
import by.kobyzau.tg.bot.pbot.artifacts.service.UserArtifactService;
import by.kobyzau.tg.bot.pbot.bots.game.exclude.ExcludeFinalizer;
import by.kobyzau.tg.bot.pbot.collectors.BotActionCollector;
import by.kobyzau.tg.bot.pbot.handlers.update.UpdateHandler;
import by.kobyzau.tg.bot.pbot.handlers.update.UpdateHandlerStage;
import by.kobyzau.tg.bot.pbot.model.ExcludeGameUserValue;
import by.kobyzau.tg.bot.pbot.model.Pidor;
import by.kobyzau.tg.bot.pbot.program.selection.ConsistentSelection;
import by.kobyzau.tg.bot.pbot.program.selection.Selection;
import by.kobyzau.tg.bot.pbot.program.text.IntText;
import by.kobyzau.tg.bot.pbot.program.text.ParametizedText;
import by.kobyzau.tg.bot.pbot.program.text.RandomText;
import by.kobyzau.tg.bot.pbot.program.text.SimpleText;
import by.kobyzau.tg.bot.pbot.program.text.pidor.ShortNamePidorText;
import by.kobyzau.tg.bot.pbot.repository.dailypidor.DailyPidorRepository;
import by.kobyzau.tg.bot.pbot.service.BotService;
import by.kobyzau.tg.bot.pbot.service.ExcludeGameService;
import by.kobyzau.tg.bot.pbot.service.PidorService;
import by.kobyzau.tg.bot.pbot.tg.ChatAction;
import by.kobyzau.tg.bot.pbot.util.DateUtil;
import by.kobyzau.tg.bot.pbot.util.StringUtil;
import by.kobyzau.tg.bot.pbot.util.helper.DateHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.Executor;
import java.util.stream.Collectors;

@Component
public class ExcludeGameUpdateHandler implements UpdateHandler {

  @Autowired private ExcludeGameService excludeGameService;
  @Autowired private BotActionCollector botActionCollector;
  @Autowired private PidorService pidorService;
  @Autowired private DailyPidorRepository dailyPidorRepository;
  @Autowired private BotService botService;
  @Autowired private UserArtifactService userArtifactService;
  @Autowired private DateHelper dateHelper;
  @Autowired private ExcludeFinalizer excludeFinalizer;

  @Autowired
  @Qualifier("cachedExecutor")
  private Executor executor;

  private Selection<String> happyWords =
      new ConsistentSelection<>(
          "{0}, ?????????????? ???? ???? ???????????? ??????????????",
          "{0}, ????????????????????, ?????????????? ???? ??????????????",
          "{0} - ??????????????, ???? ??????????",
          "{0} - ???? ??????????! ??????????, ???? - ??????????????????",
          "{0} - ?? ???????? ??????????",
          "{0} - ??????????????",
          "{0} - ???? ?????? ????????????????, ???? ?????????????? ???? ???? ???????????? ?????????????? ??????");

  private static final List<String> NUM_LEFT_TEXT =
      Arrays.asList("???????????????? {0}", "?????? {0}", "???????? ?????? {0}-??");

  @Override
  public UpdateHandlerStage getStage() {
    return UpdateHandlerStage.GAME;
  }

  @Override
  public boolean handleUpdate(Update update) {
    if (!validateUpdate(update)) {
      return false;
    }
    long chatId = update.getMessage().getChatId();
    if (!excludeGameService.isExcludeGameDay(chatId, DateUtil.now())) {
      return false;
    }
    if (!isExcludeWord(update)) {
      return false;
    }
    if (hasPidorOfTheDay(chatId)) {
      return false;
    }
    long userId = update.getMessage().getFrom().getId();

    if (hasSilenceArtifact(chatId, userId)) {
      botActionCollector.text(
          chatId, new SimpleText("\uD83D\uDE49"), update.getMessage().getMessageId());
      return true;
    }
    Pidor pidor = pidorService.getPidor(chatId, userId).orElseThrow(IllegalStateException::new);
    botActionCollector.typing(update.getMessage().getChatId());
    Optional<ExcludeGameUserValue> userValue =
        excludeGameService.getExcludeGameUserValue(chatId, userId, DateUtil.now());
    if (userValue.isPresent()) {
      botActionCollector.wait(chatId, 1, ChatAction.TYPING);
      botActionCollector.text(
          chatId,
          new ParametizedText("{0}, ???? ?????????? ??????????????????????", new ShortNamePidorText(pidor)),
          update.getMessage().getMessageId());
    } else {
      processExcludeWord(pidor, update.getMessage());
    }
    return true;
  }

  private boolean hasSilenceArtifact(long chatId, long userId) {
    return userArtifactService.getUserArtifact(chatId, userId, ArtifactType.SILENCE).isPresent()
        && dateHelper.currentTime().getHour() < 12;
  }

  private void processExcludeWord(Pidor pidor, Message message) {
    long chatId = pidor.getChatId();
    saveValue(pidor);
    botActionCollector.text(
        chatId,
        new ParametizedText(happyWords.next(), new ShortNamePidorText(pidor)),
        message.getMessageId());
    if (excludeGameService.needToFinalize(chatId)) {
      botActionCollector.wait(chatId, ChatAction.TYPING);
      botActionCollector.text(chatId, new SimpleText("?????? ???? ??????????, ?????? ??????????!"));
      botActionCollector.typing(chatId);
      executor.execute(() -> excludeFinalizer.finalize(chatId));
    } else {
      List<Pidor> pidors = pidorService.getByChat(chatId);
      Set<Long> playedIds =
          excludeGameService.getExcludeGameUserValues(chatId, DateUtil.now()).stream()
              .map(ExcludeGameUserValue::getPlayerTgId)
              .collect(Collectors.toSet());
      List<Pidor> notPlayedPidors =
          pidors.stream()
              .filter(p -> !playedIds.contains(p.getTgId()))
              .collect(Collectors.toList());
      botActionCollector.wait(chatId, 1, ChatAction.TYPING);
      botActionCollector.text(
          chatId,
          new ParametizedText(new RandomText(NUM_LEFT_TEXT), new IntText(notPlayedPidors.size())));
    }
  }

  private void saveValue(Pidor pidor) {
    ExcludeGameUserValue gameUserValue = new ExcludeGameUserValue();
    gameUserValue.setChatId(pidor.getChatId());
    gameUserValue.setLocalDate(DateUtil.now());
    gameUserValue.setPlayerTgId(pidor.getTgId());
    excludeGameService.saveExcludeGameUserValue(gameUserValue);
  }

  private boolean isExcludeWord(Update update) {
    String keyword = excludeGameService.getWordOfTheDay(DateUtil.now());
    return Optional.ofNullable(update)
        .map(Update::getMessage)
        .map(Message::getText)
        .map(StringUtil::trim)
        .map(String::toLowerCase)
        .filter(keyword.toLowerCase()::equals)
        .isPresent();
  }

  private boolean hasPidorOfTheDay(long chatId) {
    return dailyPidorRepository.getByChatAndDate(chatId, DateUtil.now()).isPresent();
  }

  private boolean validateUpdate(Update update) {
    if (!update.hasMessage()) {
      return false;
    }
    Message message = update.getMessage();
    if (!message.hasText()) {
      return false;
    }
    return botService.isChatValid(message.getChat())
        && pidorService.getPidor(message.getChatId(), message.getFrom().getId()).isPresent();
  }
}
