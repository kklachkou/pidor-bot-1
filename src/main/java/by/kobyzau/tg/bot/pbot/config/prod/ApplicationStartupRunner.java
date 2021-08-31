package by.kobyzau.tg.bot.pbot.config.prod;

import by.kobyzau.tg.bot.pbot.bots.Bot;
import by.kobyzau.tg.bot.pbot.bots.FeedbackBot;
import by.kobyzau.tg.bot.pbot.collectors.BotActionCollector;
import by.kobyzau.tg.bot.pbot.handlers.command.sync.CommandSyncer;
import by.kobyzau.tg.bot.pbot.model.dto.AppVersionDto;
import by.kobyzau.tg.bot.pbot.program.logger.Logger;
import by.kobyzau.tg.bot.pbot.program.text.ParametizedText;
import by.kobyzau.tg.bot.pbot.program.text.SimpleText;
import by.kobyzau.tg.bot.pbot.program.tokens.AccessTokenHolderFactory;
import by.kobyzau.tg.bot.pbot.program.tokens.TokenType;
import by.kobyzau.tg.bot.pbot.service.github.GithubService;
import by.kobyzau.tg.bot.pbot.util.DateUtil;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.telegram.telegraph.ExecutorOptions;
import org.telegram.telegraph.TelegraphContext;
import org.telegram.telegraph.TelegraphContextInitializer;

import javax.sql.DataSource;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.sql.Connection;
import java.sql.Statement;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@Component
@Profile("prod")
@Order(Ordered.HIGHEST_PRECEDENCE)
public class ApplicationStartupRunner implements ApplicationRunner {

  @Autowired(required = false)
  private FeedbackBot feedbackBot;

  @Autowired private Environment env;

  @Autowired private DataSource dataSource;

  @Autowired private Bot bot;

  @Autowired private CommandSyncer commandSyncer;
  @Autowired private GithubService githubService;

  @Value("${app.admin.userId}")
  private long adminUserId;

  @Autowired private Logger logger;

  @Autowired private BotActionCollector botActionCollector;

  @Autowired private AccessTokenHolderFactory tokenHolderFactory;

  @Override
  public void run(ApplicationArguments args) {
    AppVersionDto appVersion = githubService.getAppVersion();
    logger.info(
        "\u2B50\u2B50\u2B50\u2B50\u2B50\nStarting Pidor Bot...\nVersion "
            + appVersion.getNumber()
            + " "
            + appVersion.getName()
            + "\n"
            + appVersion.getDesc());
    initDatabase();
    TelegraphContextInitializer.init();
    TelegraphContext.registerInstance(ExecutorOptions.class, new ExecutorOptions());
    bot.botConnect();
    if (feedbackBot != null) {
      feedbackBot.botConnect();
    }
    commandSyncer.sync();
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
    if (DateUtil.isNewYearTime()
        && Arrays.stream(env.getActiveProfiles()).noneMatch("new-year"::equals)) {
      botActionCollector.text(
          adminUserId, new SimpleText("!!!new-year profile need to be activated"));
      logger.warn("!!!new-year profile need to be activated");
    }
    if (!DateUtil.isNewYearTime() && Arrays.asList(env.getActiveProfiles()).contains("new-year")) {
      botActionCollector.text(
          adminUserId, new SimpleText("!!!new-year profile need to be deactivated"));
      logger.warn("!!!new-year profile need to be deactivated");
    }
    logger.info("Bot started " + bot.getBotUsername());
  }

  private void initDatabase() {
    List<String> queries = getSql();
    logger.debug("Found " + queries.size() + " queries");
    try (Connection c = dataSource.getConnection();
        Statement s = c.createStatement()) {
      for (String query : queries) {
        s.addBatch(query);
      }
      s.executeBatch();
    } catch (Exception e) {
      throw new RuntimeException("Cannot execute runtime sql", e);
    }
  }

  private List<String> getSql() {
    InputStream is = getClass().getClassLoader().getResourceAsStream("sql.txt");
    Objects.requireNonNull(is, "Cannot find sql file");
    try {
      return Arrays.asList(
          String.join("", IOUtils.readLines(is, Charset.defaultCharset())).split(";"));
    } catch (Exception e) {
      throw new RuntimeException("Cannot read sql file", e);
    }
  }
}
