package by.kobyzau.tg.bot.pbot.handlers.command.handler;

import by.kobyzau.tg.bot.pbot.collectors.BotActionCollector;
import by.kobyzau.tg.bot.pbot.handlers.command.Command;
import by.kobyzau.tg.bot.pbot.model.Pair;
import by.kobyzau.tg.bot.pbot.model.Pidor;
import by.kobyzau.tg.bot.pbot.program.text.RandomText;
import by.kobyzau.tg.bot.pbot.program.text.SimpleText;
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
    List<Pair<Pidor, Integer>> pairs =
        pidorChanceService.calcChances(chatId, DateUtil.now().getYear());
    botActionCollector.typing(chatId);
    StringBuilder sb = new StringBuilder();
    for (Pair<Pidor, Integer> pidorChance : pairs) {
      int chance = pidorChance.getRight();
      sb.append("\u26A1 ");
      sb.append(new FullNamePidorText(pidorChance.getLeft()));
      sb.append(" - ");
      sb.append(chance);
      sb.append("%\n");
    }
    botActionCollector.text(chatId, new SimpleText(sb.toString()));
  }

  @Override
  public Command getCommand() {
    return Command.CHANCES;
  }
}
