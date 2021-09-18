package by.kobyzau.tg.bot.pbot.handlers.command.handler;

import by.kobyzau.tg.bot.pbot.artifacts.ArtifactType;
import by.kobyzau.tg.bot.pbot.bots.FeedbackBot;
import by.kobyzau.tg.bot.pbot.collectors.BotActionCollector;
import by.kobyzau.tg.bot.pbot.handlers.command.Command;
import by.kobyzau.tg.bot.pbot.model.dto.AppVersionDto;
import by.kobyzau.tg.bot.pbot.program.text.*;
import by.kobyzau.tg.bot.pbot.service.github.GithubService;
import by.kobyzau.tg.bot.pbot.util.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@Profile("prod")
public class HelpCommandHandler implements CommandHandler {

  @Value("${app.admin.userId}")
  private long adminUserId;

  @Autowired private GithubService githubService;

  @Autowired(required = false)
  private FeedbackBot feedbackBot;

  @Autowired private BotActionCollector botActionCollector;

  @Override
  public void processCommand(Message message, String text) {
    botActionCollector.text(message.getChatId(), buildHelpMessage(message.getChatId()));
  }

  private Text buildHelpMessage(long chatId) {
    AppVersionDto appVersion = githubService.getAppVersion();
    return new TextBuilder(new SimpleText("Что умеет данный бот:"))
        .append(new NewLineText())
        .append(new NewLineText())
        .append(buildCommandsMessage(Command.Category.ACTION))
        .append(new NewLineText())
        .append(buildCommandsMessage(Command.Category.INFO))
        .append(new NewLineText())
        .append(buildArtefactsMessage())
        .append(new NewLineText())
        .append(
            new SimpleText(
                "Написать предложение/сообщить об ошибке - @"
                    + Optional.ofNullable(feedbackBot)
                        .map(FeedbackBot::getBotUsername)
                        .orElse("None")))
        .append(new NewLineText())
        .append(new NewLineText())
        .append(
            new ItalicText(
                new ParametizedText(
                    "Версия {0} {1}{2}",
                    new SimpleText(appVersion.getNumber()),
                    new SimpleText(appVersion.getName()),
                    StringUtil.isBlank(appVersion.getDesc()) || chatId != adminUserId
                        ? new EmptyText()
                        : new TextBuilder()
                            .append(new NewLineText())
                            .append(new SimpleText(appVersion.getDesc())))));
  }

  private Text buildCommandsMessage(Command.Category category) {
    TextBuilder tb = new TextBuilder();
    tb.append(new SimpleText("\t"))
        .append(new BoldText(category.getName()))
        .append(new NewLineText());
    Arrays.stream(Command.values())
        .filter(c -> c.getCategory() == category)
        .filter(this::filterCommand)
        .sorted(Comparator.comparing(Command::getOrder))
        .map(this::buildCommandMessage)
        .forEach(tb::append);
    return tb;
  }

  private Text buildCommandMessage(Command command) {
    return new TextBuilder(new SimpleText("\t\t▫️ /"))
        .append(new SimpleText(command.getName()))
        .append(new SpaceText())
        .append(new NotBlankText(command.getDesc()))
        .append(new NewLineText());
  }

  private Text buildArtefactsMessage() {
    return new SimpleText(
        Arrays.asList(ArtifactType.values()).stream()
            .map(
                a ->
                    new ParametizedText(
                        "{0} {1}: {2}",
                        new ItalicText(a.getName()),
                        new SimpleText(a.getEmoji()),
                        new SimpleText(a.getDesc())))
            .map(ParametizedText::text)
            .collect(Collectors.joining("\n")));
  }

  protected boolean filterCommand(Command command) {
    if (Command.NONE == command) {
      return false;
    }
    return StringUtil.isNotBlank(command.getDesc());
  }

  @Override
  public Command getCommand() {
    return Command.HELP;
  }
}
