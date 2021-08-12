package by.kobyzau.tg.bot.pbot.handlers.command.handler;

import by.kobyzau.tg.bot.pbot.collectors.BotActionCollector;
import by.kobyzau.tg.bot.pbot.handlers.command.Command;
import by.kobyzau.tg.bot.pbot.handlers.command.handler.schedule.ScheduleDayTextItem;
import by.kobyzau.tg.bot.pbot.program.text.BoldText;
import by.kobyzau.tg.bot.pbot.program.text.DayNameText;
import by.kobyzau.tg.bot.pbot.program.text.IntText;
import by.kobyzau.tg.bot.pbot.program.text.NewLineText;
import by.kobyzau.tg.bot.pbot.program.text.ParametizedText;
import by.kobyzau.tg.bot.pbot.program.text.SimpleText;
import by.kobyzau.tg.bot.pbot.program.text.Text;
import by.kobyzau.tg.bot.pbot.program.text.TextBuilder;
import by.kobyzau.tg.bot.pbot.program.text.collector.ToTextCollector;
import by.kobyzau.tg.bot.pbot.util.DateUtil;
import by.kobyzau.tg.bot.pbot.util.StringUtil;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Message;

@Component
public class ScheduleCommandHandler implements CommandHandler {

  @Autowired private BotActionCollector botActionCollector;

  @Autowired private List<ScheduleDayTextItem> dayTextItemList;

  @Override
  public void processCommand(Message message, String text) {
    int days = StringUtil.parseInt(text, 8);
    long chatId = message.getChatId();
    LocalDate date = DateUtil.now();
    for (int i = 0; i < days; i++) {
      final LocalDate fixedDate = date;
      List<Text> items =
          dayTextItemList.stream()
              .map(d -> d.getTextItem(chatId, fixedDate))
              .filter(Optional::isPresent)
              .map(Optional::get)
              .collect(Collectors.toList());
      if (items.isEmpty()) {
        botActionCollector.text(
            chatId,
            new TextBuilder()
                .append(
                    new BoldText(
                        new ParametizedText(
                            "{0} ({1}-е):",
                            new DayNameText(fixedDate.getDayOfWeek().getValue()),
                            new IntText(fixedDate.getDayOfMonth()))))
                .append(new NewLineText())
                .append(new SimpleText("ничего такого")));
      } else {
        Text itemsText =
            items.stream()
                .map(t -> new TextBuilder(new SimpleText(" - ")).append(t))
                .collect(new ToTextCollector(new NewLineText()));

        botActionCollector.text(
            chatId,
            new TextBuilder()
                .append(
                    new ParametizedText(
                        "{0} ({1}-е):",
                        new DayNameText(fixedDate.getDayOfWeek().getValue()),
                        new IntText(fixedDate.getDayOfMonth())))
                .append(new NewLineText())
                .append(itemsText));
      }

      date = date.plusDays(1);
    }
  }

  @Override
  public Command getCommand() {
    return Command.SCHEDULE;
  }
}
