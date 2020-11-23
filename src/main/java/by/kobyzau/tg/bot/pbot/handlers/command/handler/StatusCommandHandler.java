package by.kobyzau.tg.bot.pbot.handlers.command.handler;

import by.kobyzau.tg.bot.pbot.collectors.BotActionCollector;
import by.kobyzau.tg.bot.pbot.handlers.command.Command;
import by.kobyzau.tg.bot.pbot.model.PidorStatus;
import by.kobyzau.tg.bot.pbot.model.PidotStatusPosition;
import by.kobyzau.tg.bot.pbot.program.selection.ConsistentSelection;
import by.kobyzau.tg.bot.pbot.program.selection.Selection;
import by.kobyzau.tg.bot.pbot.program.text.SimpleText;
import by.kobyzau.tg.bot.pbot.program.text.Text;
import by.kobyzau.tg.bot.pbot.program.text.pidor.FullNamePidorText;
import by.kobyzau.tg.bot.pbot.service.PidorStatusService;
import by.kobyzau.tg.bot.pbot.util.CollectionUtil;
import by.kobyzau.tg.bot.pbot.util.DateUtil;
import by.kobyzau.tg.bot.pbot.util.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class StatusCommandHandler implements CommandHandler {

  @Autowired private BotActionCollector botActionCollector;

  @Autowired private PidorStatusService pidorStatusService;

  private final Selection<String> infoMessage =
      new ConsistentSelection<>(
          "Вот информация за год", "Вот что у меня есть на вас", "Это то, что есть у меня");

  private final Selection<String> secondaryPidorsMessage =
      new ConsistentSelection<>(
          "И нюхают его попец", "И делят с ним общую бутылку", "И на том же месте", "А также");

  private final Selection<String> secondaryPidorMessage =
      new ConsistentSelection<>(
          "И нюхает его попец", "И делит с ним общую бутылку", "И на том же месте", "А также");

  @Override
  public void processCommand(Message message, String text) {
    int year = StringUtil.parseInt(text, DateUtil.now().getYear());
    botActionCollector.typing(message.getChatId());
    PidorStatus pidorStatus = pidorStatusService.getPidorStatus(message.getChatId(), year);
    String m = getMessage(pidorStatus);
    botActionCollector.text(message.getChatId(), new SimpleText(m));
  }

  private String getMessage(PidorStatus pidorStatus) {
    List<PidotStatusPosition> pidorStatusPositions = pidorStatus.getPidorStatusPositions();
    if (CollectionUtil.isEmpty(pidorStatusPositions)) {
      return "В этом году нету педиков";
    }
    StringBuilder sb = new StringBuilder();
    int index = 1;
    sb.append(infoMessage.next());
    sb.append(":\n\n");
    for (PidotStatusPosition pidorStatusPosition : pidorStatusPositions) {
      if (pidorStatusPosition.getNumWins() == 0) {
        if (CollectionUtil.size(pidorStatusPositions) == 1) {
          sb.append("Пока что все натуралы");
          continue;
        } else {
          sb.append("И в конце список каких то натуралов:\n");
        }

        String list =
            pidorStatusPosition.getSecondaryPidors().stream()
                .map(FullNamePidorText::new)
                .map(Text::text)
                .collect(Collectors.joining("\n"));
        sb.append(list);
      } else {
        displayPidorPosition(sb, index++, pidorStatusPosition);
      }
    }
    return sb.toString();
  }

  private void displayPidorPosition(
      StringBuilder sb, int num, PidotStatusPosition pidotStatusPosition) {

    switch (num) {
      case 1:
        sb.append("\uD83E\uDD47 ");
        break;
      case 2:
        sb.append("\uD83E\uDD48 ");
        break;
      case 3:
        sb.append("\uD83E\uDD49 ");
        break;
      default:
        sb.append(num).append(") ");
    }
    sb.append(new FullNamePidorText(pidotStatusPosition.getPrimaryPidor()));
    sb.append(" - ").append(pidotStatusPosition.getNumWins()).append(" раз(а)");

    if (CollectionUtil.isNotEmpty(pidotStatusPosition.getSecondaryPidors())) {
      String message =
          CollectionUtil.size(pidotStatusPosition.getSecondaryPidors()) == 1
              ? secondaryPidorMessage.next()
              : secondaryPidorsMessage.next();

      sb.append("\n\t\t\t").append(message).append(": ");
      sb.append(
          pidotStatusPosition.getSecondaryPidors().stream()
              .map(FullNamePidorText::new)
              .map(text -> "\n\t\t\t - " + text)
              .collect(Collectors.joining()));
    }
    sb.append("\n\n");
  }

  @Override
  public Command getCommand() {
    return Command.STATUS;
  }
}
