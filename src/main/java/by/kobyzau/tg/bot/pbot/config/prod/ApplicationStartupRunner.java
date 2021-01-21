package by.kobyzau.tg.bot.pbot.config.prod;

import by.kobyzau.tg.bot.pbot.bots.Bot;
import by.kobyzau.tg.bot.pbot.collectors.BotActionCollector;
import by.kobyzau.tg.bot.pbot.handlers.command.sync.CommandSyncer;
import by.kobyzau.tg.bot.pbot.program.Version;
import by.kobyzau.tg.bot.pbot.program.logger.Logger;
import by.kobyzau.tg.bot.pbot.program.text.*;
import by.kobyzau.tg.bot.pbot.program.tokens.AccessTokenHolderFactory;
import by.kobyzau.tg.bot.pbot.program.tokens.TokenType;
import by.kobyzau.tg.bot.pbot.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Component
@Profile("prod")
public class ApplicationStartupRunner implements ApplicationRunner {

  @Autowired
  private Environment env;

  @Autowired private Bot bot;

  @Autowired private CommandSyncer commandSyncer;

  @Value("${app.admin.userId}")
  private int adminUserId;

  @Value("${app.version}")
  private String version;

  @Value("${spring.datasource.url}")
  private String url;

  @Value("${spring.datasource.username}")
  private String username;

  @Value("${spring.datasource.password}")
  private String password;

  @Autowired private Logger logger;

  @Autowired private BotActionCollector botActionCollector;

  @Autowired private AccessTokenHolderFactory tokenHolderFactory;

  @Override
  public void run(ApplicationArguments args) {
    logger.info(
        "\u2B50\u2B50\u2B50\u2B50\u2B50\nSpring Boot started\nVersion "
            + new ShortDateText(Version.getLast().getRelease())
            + ": "
            + version);
    bot.botConnect();
    logger.info("Bot started " + bot.getBotUsername());
    commandSyncer.sync();
    logger.info(
        new TextBuilder(new SimpleText("Credentials: "))
            .append(new NewLineText())
            .append(new NewLineText())
            .append(new SimpleText(url))
            .append(new NewLineText())
            .append(new NewLineText())
            .append(new SimpleText(username))
            .append(new NewLineText())
            .append(new NewLineText())
            .append(new SimpleText(password))
            .text());
    Arrays.stream(TokenType.values())
        .map(tokenHolderFactory::getTokenHolder)
        .map(
            h ->
                new ParametizedText(
                    "Token {0} has value {1}",
                    new SimpleText(h.getType().name()), new SimpleText(h.getToken())))
        .map(ParametizedText::text)
        .forEach(logger::info);
    botActionCollector.text(adminUserId, new SimpleText("App is ready"));
    if (DateUtil.isNewYearTime() && Arrays.stream(env.getActiveProfiles()).noneMatch("new-year"::equals)) {
      botActionCollector.text(adminUserId, new SimpleText("!!!new-year profile need to be activated"));
      logger.warn("!!!new-year profile need to be activated");
    }
    if (!DateUtil.isNewYearTime() && Arrays.asList(env.getActiveProfiles()).contains("new-year")) {
      botActionCollector.text(adminUserId, new SimpleText("!!!new-year profile need to be deactivated"));
      logger.warn("!!!new-year profile need to be deactivated");
    }
  }
}
