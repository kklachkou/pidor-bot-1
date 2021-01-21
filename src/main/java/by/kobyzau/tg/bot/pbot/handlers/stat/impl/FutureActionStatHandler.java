package by.kobyzau.tg.bot.pbot.handlers.stat.impl;

import by.kobyzau.tg.bot.pbot.collectors.BotActionCollector;
import by.kobyzau.tg.bot.pbot.handlers.stat.StatHandler;
import by.kobyzau.tg.bot.pbot.model.StatType;
import by.kobyzau.tg.bot.pbot.program.text.NewLineText;
import by.kobyzau.tg.bot.pbot.program.text.ParametizedText;
import by.kobyzau.tg.bot.pbot.program.text.SimpleText;
import by.kobyzau.tg.bot.pbot.program.text.TextBuilder;
import by.kobyzau.tg.bot.pbot.service.FutureActionService;
import by.kobyzau.tg.bot.pbot.tg.ChatAction;
import by.kobyzau.tg.bot.pbot.util.TGUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Component
public class FutureActionStatHandler implements StatHandler {

  @Autowired private BotActionCollector botActionCollector;

  @Autowired private FutureActionService futureActionService;

  @Override
  public void printStat(long chatId) {
    for (FutureActionService.FutureActionType type :
        FutureActionService.FutureActionType.values()) {
      List<String> futureActionData =
          futureActionService.getFutureActionData(type, LocalDate.of(2100, 1, 1));
      TextBuilder textBuilder =
          new TextBuilder()
              .append(new SimpleText(type.name()))
              .append(new NewLineText())
              .append(new NewLineText());
      for (String data : futureActionData) {
        textBuilder
            .append(new NewLineText())
            .append(new NewLineText())
            .append(new ParametizedText("<pre>{0}</pre>", new SimpleText(TGUtil.escapeHTML(data))));
      }
      botActionCollector.wait(chatId, 1, ChatAction.TYPING);
      botActionCollector.text(chatId, textBuilder);
    }
  }

  @Override
  public StatType getType() {
    return StatType.FUTURE_ACTIONS;
  }
}
