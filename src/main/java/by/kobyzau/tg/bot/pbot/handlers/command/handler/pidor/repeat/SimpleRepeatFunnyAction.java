package by.kobyzau.tg.bot.pbot.handlers.command.handler.pidor.repeat;

import by.kobyzau.tg.bot.pbot.collectors.BotActionCollector;
import by.kobyzau.tg.bot.pbot.model.Pidor;
import by.kobyzau.tg.bot.pbot.program.selection.ConsistentSelection;
import by.kobyzau.tg.bot.pbot.program.selection.Selection;
import by.kobyzau.tg.bot.pbot.program.text.SimpleText;
import by.kobyzau.tg.bot.pbot.program.text.Text;
import by.kobyzau.tg.bot.pbot.tg.ChatAction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class SimpleRepeatFunnyAction implements RepeatFunnyAction {

  @Autowired private BotActionCollector botActionCollector;

  private final Map<Integer, String> map;
  private final Selection<Text> finalMessage =
      new ConsistentSelection<>(
              "Я ПРОСТО НЕ МОГУ ПОВЕРИТЬ В ЭТО!!!",
              "КТО-НИБУДЬ ОСТАНОВИТЕ ЕГО!!!",
              "КАК ОН ЭТО ДЕЛАЕТ!!!",
              "КТО-НИБУДЬ ОСТАНОВИТЕ ЕГО!!!",
              "Воу воу воу, полегче!!!",
              "Я просто не могу поверить в это")
          .map(SimpleText::new);

  public SimpleRepeatFunnyAction() {
    map = new HashMap<>();
    map.put(0, "<b>SUPER KILL!!!</b>");
    map.put(2, "<b>DOUBLE KILL!!!</b>");
    map.put(3, "<b>KILLING SPREE!!!</b>");
    map.put(4, "<b>DOMINATING!!!</b>");
    map.put(5, "<b>MEGA KILL!!!</b>");
    map.put(6, "<b>UNSTOPPABLE!!!</b>");
  }

  @Override
  public void processFunnyAction(long chatId, int numWins, Pidor pidorOfTheDay) {
    String message = map.getOrDefault(numWins, map.get(0));
    botActionCollector.wait(chatId, ChatAction.TYPING);
    botActionCollector.text(chatId, new SimpleText(message));
    botActionCollector.wait(chatId, ChatAction.TYPING);
    botActionCollector.text(chatId, finalMessage.next());
  }
}
