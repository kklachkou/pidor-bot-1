package by.kobyzau.tg.bot.pbot.tasks;

import by.kobyzau.tg.bot.pbot.collectors.BotActionCollector;
import by.kobyzau.tg.bot.pbot.model.Pidor;
import by.kobyzau.tg.bot.pbot.model.PidorOfYear;
import by.kobyzau.tg.bot.pbot.model.PidorStatus;
import by.kobyzau.tg.bot.pbot.model.PidotStatusPosition;
import by.kobyzau.tg.bot.pbot.program.logger.Logger;
import by.kobyzau.tg.bot.pbot.program.text.IntText;
import by.kobyzau.tg.bot.pbot.program.text.ItalicText;
import by.kobyzau.tg.bot.pbot.program.text.ParametizedText;
import by.kobyzau.tg.bot.pbot.program.text.SimpleText;
import by.kobyzau.tg.bot.pbot.program.text.pidor.ShortNameLinkedPidorText;
import by.kobyzau.tg.bot.pbot.repository.pidorofyear.PidorOfYearRepository;
import by.kobyzau.tg.bot.pbot.service.PidorStatusService;
import by.kobyzau.tg.bot.pbot.service.TelegramService;
import by.kobyzau.tg.bot.pbot.tg.ChatAction;
import by.kobyzau.tg.bot.pbot.tg.action.PingMessageWrapperBotAction;
import by.kobyzau.tg.bot.pbot.tg.action.SendMessageBotAction;
import by.kobyzau.tg.bot.pbot.tg.sticker.StickerType;
import by.kobyzau.tg.bot.pbot.util.CollectionUtil;
import by.kobyzau.tg.bot.pbot.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.Executor;

@Component("pidorOfYearTask")
public class PidorOfTheYearTask implements Task {

  @Autowired private TelegramService telegramService;

  @Autowired private BotActionCollector botActionCollector;

  @Autowired private Logger logger;

  @Autowired private PidorOfYearRepository pidorOfYearRepository;

  @Autowired private PidorStatusService pidorStatusService;

  @Autowired
  @Qualifier("taskExecutor")
  private Executor executor;

  @Override
  public void processTask() {
    logger.info("\uD83D\uDCC6 Task " + this.getClass().getSimpleName() + " is started");
    telegramService
        .getChatIds()
        .forEach(chatId -> executor.execute(() -> handlePidorOfTheYear(chatId)));
  }

  private void handlePidorOfTheYear(long chatId) {
    Optional<Pidor> pidorOfTheYear = getPidorOfTheYear(chatId);

    if (!pidorOfTheYear.isPresent()) {
      return;
    }
    botActionCollector.typing(chatId);
    savePidorOfTheYear(pidorOfTheYear.get());
    handlePidorOfTheYearForOnePerson(chatId, pidorOfTheYear.get());
  }

  private void handlePidorOfTheYearForOnePerson(long chatId, Pidor pidor) {
    botActionCollector.text(chatId, new SimpleText("Ну что"));

    botActionCollector.wait(chatId, ChatAction.TYPING);
    botActionCollector.text(chatId, new SimpleText("Этот нелегкий год подходит к концу"));

    botActionCollector.wait(chatId, ChatAction.TYPING);
    botActionCollector.text(
        chatId, new SimpleText("Хотелось бы, чтобы в этот день была бы хоть какая интрига"));

    botActionCollector.wait(chatId, ChatAction.TYPING);
    botActionCollector.text(
        chatId, new SimpleText("Чтобы вы ломали голову, кто же будет Пидором Года"));

    botActionCollector.wait(chatId, ChatAction.TYPING);
    botActionCollector.text(
        chatId,
        new ParametizedText(
            "Но судьба для {0} выбрала иной путь", new ShortNameLinkedPidorText(pidor)));

    botActionCollector.wait(chatId, ChatAction.TYPING);
    botActionCollector.text(chatId, new SimpleText("Но нет времени грустить!"));

    botActionCollector.wait(chatId, ChatAction.TYPING);
    botActionCollector.text(chatId, new SimpleText("У нас праздник!"));

    botActionCollector.wait(chatId, 2, ChatAction.TYPING);
    botActionCollector.sticker(chatId, StickerType.CONGRATULATION.getRandom());

    botActionCollector.wait(chatId, ChatAction.TYPING);
    botActionCollector.text(
        chatId,
        new ParametizedText("{0} - ты лучший из всех нас!", new ShortNameLinkedPidorText(pidor)));

    botActionCollector.wait(chatId, 2, ChatAction.TYPING);
    botActionCollector.sticker(chatId, StickerType.NEW_YEAR.getRandom());

    botActionCollector.wait(chatId, ChatAction.TYPING);
    botActionCollector.text(
        chatId,
        new ParametizedText(
            "{0} - твоя упругая попка покорила моё сердце!", new ShortNameLinkedPidorText(pidor)));

    botActionCollector.wait(chatId, 2, ChatAction.TYPING);
    botActionCollector.sticker(chatId, StickerType.CONGRATULATION.getRandom());

    botActionCollector.wait(chatId, ChatAction.TYPING);
    botActionCollector.text(
        chatId,
        new ParametizedText(
            "{0} - этот год посвящен лишь тебе !", new ShortNameLinkedPidorText(pidor)));

    botActionCollector.wait(chatId, 2, ChatAction.TYPING);
    botActionCollector.sticker(chatId, StickerType.NEW_YEAR.getRandom());

    botActionCollector.wait(chatId, ChatAction.TYPING);
    botActionCollector.add(
        new PingMessageWrapperBotAction(
            new SendMessageBotAction(
                chatId,
                new ParametizedText(
                    "{0} - ты наш главный Пидор этого года!",
                    new ShortNameLinkedPidorText(pidor)))));

    botActionCollector.wait(chatId, 2, ChatAction.TYPING);
    botActionCollector.sticker(chatId, StickerType.CONGRATULATION.getRandom());

    botActionCollector.wait(chatId, ChatAction.TYPING);
    botActionCollector.text(
        chatId,
        new ParametizedText(
            "{0} - твоё новое боевое звание - {1}!",
            new ShortNameLinkedPidorText(pidor),
            new ItalicText(
                new ParametizedText("Пидор года {0}", new IntText(DateUtil.now().getYear())))));

    botActionCollector.wait(chatId, 2, ChatAction.TYPING);
    botActionCollector.sticker(chatId, StickerType.LOVE.getRandom());

    botActionCollector.wait(chatId, 2, ChatAction.TYPING);
    botActionCollector.sticker(chatId, StickerType.NEW_YEAR.getRandom());

    botActionCollector.wait(chatId, 2, ChatAction.TYPING);
    botActionCollector.sticker(chatId, StickerType.LOVE.getRandom());
  }

  private Optional<Pidor> getPidorOfTheYear(long chatId) {
    PidorStatus pidorStatus = pidorStatusService.getPidorStatus(chatId, DateUtil.now().getYear());
    List<PidotStatusPosition> pidorStatusPositions = pidorStatus.getPidorStatusPositions();
    if (CollectionUtil.isEmpty(pidorStatusPositions)) {
      return Optional.empty();
    }
    return Optional.ofNullable(pidorStatusPositions.get(0).getPrimaryPidor());
  }

  private void savePidorOfTheYear(Pidor pidor) {
    PidorOfYear pidorOfYear = new PidorOfYear();
    pidorOfYear.setPlayerTgId(pidor.getTgId());
    pidorOfYear.setChatId(pidor.getChatId());
    pidorOfYear.setYear(DateUtil.now().getYear());
    pidorOfYearRepository.create(pidorOfYear);
  }
}
