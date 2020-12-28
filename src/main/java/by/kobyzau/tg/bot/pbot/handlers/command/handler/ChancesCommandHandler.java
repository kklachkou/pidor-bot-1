package by.kobyzau.tg.bot.pbot.handlers.command.handler;

import by.kobyzau.tg.bot.pbot.collectors.BotActionCollector;
import by.kobyzau.tg.bot.pbot.handlers.command.Command;
import by.kobyzau.tg.bot.pbot.model.Pair;
import by.kobyzau.tg.bot.pbot.model.Pidor;
import by.kobyzau.tg.bot.pbot.program.text.*;
import by.kobyzau.tg.bot.pbot.program.text.pidor.FullNamePidorText;
import by.kobyzau.tg.bot.pbot.service.PidorChanceService;
import by.kobyzau.tg.bot.pbot.service.PidorService;
import by.kobyzau.tg.bot.pbot.tg.ChatAction;
import by.kobyzau.tg.bot.pbot.tg.sticker.GifFile;
import by.kobyzau.tg.bot.pbot.util.CollectionUtil;
import by.kobyzau.tg.bot.pbot.util.DateUtil;
import by.kobyzau.tg.bot.pbot.util.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.util.List;

@Component
public class ChancesCommandHandler implements CommandHandler {

  @Autowired private PidorChanceService pidorChanceService;
  @Autowired private PidorService pidorService;
  @Autowired private BotActionCollector botActionCollector;

  @Value("${bot.token}")
  private String botToken;

  @Override
  public void processCommand(Message message, String text) {
    long chatId = message.getChatId();

    botActionCollector.text(chatId, new SimpleText("Расчитываем шансы стать пидором года..."));
    if (CollectionUtil.isEmpty(pidorService.getByChat(chatId))) {
      botActionCollector.text(
          chatId, new RandomText("Упс, а пидоров то нет", "Так никто не зареган"));
      return;
    }
    botActionCollector.wait(chatId, 1, ChatAction.UPLOAD_PHOTO);
    botActionCollector.animation(
        chatId, GifFile.CALCULATION.getRandom(StringUtil.substringBefore(botToken, ":")));
    botActionCollector.typing(chatId);
    List<Pair<Pidor, Double>> pairs =
        pidorChanceService.calcChances(chatId, DateUtil.now().getYear());
    botActionCollector.typing(chatId);
    TextBuilder textBuilder = new TextBuilder();
    for (Pair<Pidor, Double> pidorChance : pairs) {
      double chance = pidorChance.getRight();
      textBuilder
          .append(
              new ParametizedText(
                  "\u26A1 {0} - {1}%",
                  new FullNamePidorText(pidorChance.getLeft()), new DoubleText(chance)))
          .append(new NewLineText());
    }
    botActionCollector.text(chatId, textBuilder);
  }

  @Override
  public Command getCommand() {
    return Command.CHANCES;
  }
}
