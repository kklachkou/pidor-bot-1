package by.kobyzau.tg.bot.pbot.handlers.command.handler.pidor.datebased.onetime;

import by.kobyzau.tg.bot.pbot.collectors.BotActionCollector;
import by.kobyzau.tg.bot.pbot.handlers.command.handler.pidor.datebased.OneTimePidorFunnyAction;
import by.kobyzau.tg.bot.pbot.model.Pidor;
import by.kobyzau.tg.bot.pbot.program.text.ParametizedText;
import by.kobyzau.tg.bot.pbot.program.text.SimpleText;
import by.kobyzau.tg.bot.pbot.program.text.Text;
import by.kobyzau.tg.bot.pbot.program.text.pidor.FullNamePidorText;
import by.kobyzau.tg.bot.pbot.program.text.pidor.ShortNamePidorText;
import by.kobyzau.tg.bot.pbot.service.TelegramService;
import by.kobyzau.tg.bot.pbot.tg.ChatAction;
import by.kobyzau.tg.bot.pbot.tg.action.PingMessageWrapperBotAction;
import by.kobyzau.tg.bot.pbot.tg.action.SendStickerBotAction;
import by.kobyzau.tg.bot.pbot.tg.sticker.StickerType;
import by.kobyzau.tg.bot.pbot.util.StringUtil;
import by.kobyzau.tg.bot.pbot.util.TGUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Chat;

import java.time.LocalDate;
import java.util.Optional;

@Component
public class HiMikeOneTimePidorFunnyAction implements OneTimePidorFunnyAction {

  @Autowired private BotActionCollector botActionCollector;
  @Autowired private TelegramService telegramService;

  @Override
  public LocalDate getDate() {
    return LocalDate.of(2020, 11, 22);
  }

  @Override
  public void processFunnyAction(long chatId, Pidor pidorOfTheDay) {
    Text chatName = getChatName(chatId);
    botActionCollector.text(
        chatId, new SimpleText("Варшава: Алло! Добрый день, Ник. Как наши дела?"));
    botActionCollector.wait(chatId, ChatAction.TYPING);
    botActionCollector.text(
        chatId,
        new ParametizedText(
            "Берлин: Вроде бы всё по плану...Материалы по {0} готовы."
                + " Будут переданы в администрацию Пидор-бота. Ожидаем его заявления",
            new ShortNamePidorText(pidorOfTheDay)));
    botActionCollector.wait(chatId, ChatAction.TYPING);
    botActionCollector.text(chatId, new SimpleText("Варшава: Ориентация точно подтверждается?"));
    botActionCollector.wait(chatId, ChatAction.TYPING);
    botActionCollector.text(
        chatId,
        new SimpleText(
            "Берлин: Послушай, Майк, в данном случае это не так важно... Идёт война... А во время войны всякие методы хороши."));
    botActionCollector.wait(chatId, ChatAction.TYPING);
    botActionCollector.text(
        chatId,
        new ParametizedText(
            "Варшава: Согласен, надо отбить охоту Путину сунуть нос в дела '{0}'..."
                + " Самый эффективный путь - утопить его в проблемах России, а их не мало!"
                + " Тем более с ближайшее время у них выборы, день голосования в регионах России.",
            chatName));
    botActionCollector.wait(chatId, ChatAction.TYPING);
    botActionCollector.text(
        chatId,
        new ParametizedText("Берлин: Этим и занимаемся... А как ваши дела в '{0}'?", chatName));
    botActionCollector.wait(chatId, ChatAction.TYPING);
    botActionCollector.text(
        chatId,
        new ParametizedText(
            "Варшава: Если честно, не очень."
                + " Пидор {0} оказался крепким орешком."
                + " Они профиссионалы и организованы. Понятно, их поддерживает Россия."
                + " Чиновники и военные верны Пидору дня."
                + " Пока работаем. Остальное при встрече, не по телефону.",
            new FullNamePidorText(pidorOfTheDay)));
    botActionCollector.wait(chatId, ChatAction.TYPING);
    botActionCollector.text(
        chatId, new SimpleText("Берлин: Да-да, понимаю, тогда до встречи, пока"));
    botActionCollector.wait(chatId, ChatAction.TYPING);
    botActionCollector.text(chatId, new SimpleText("Варшава: Пока"));

    botActionCollector.wait(chatId, ChatAction.TYPING);
    Optional<StickerType> pidorSticker = StickerType.getPidorSticker(pidorOfTheDay.getSticker());
    if (pidorSticker.isPresent()) {
      botActionCollector.add(
          new PingMessageWrapperBotAction(
              new SendStickerBotAction(chatId, pidorSticker.get().getRandom())));
      botActionCollector.wait(chatId, ChatAction.TYPING);
    }
    botActionCollector.sticker(chatId, StickerType.PIDOR.getRandom());
  }

  private Text getChatName(long chatId) {
    return telegramService
        .getChat(chatId)
        .map(Chat::getTitle)
        .filter(StringUtil::isNotBlank)
        .map(TGUtil::escapeHTML)
        .map(SimpleText::new)
        .orElse(new SimpleText("Беларуси"));
  }
}
