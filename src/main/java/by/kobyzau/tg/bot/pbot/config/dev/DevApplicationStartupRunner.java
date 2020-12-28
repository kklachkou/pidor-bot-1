package by.kobyzau.tg.bot.pbot.config.dev;

import by.kobyzau.tg.bot.pbot.bots.Bot;
import by.kobyzau.tg.bot.pbot.collectors.BotActionCollector;
import by.kobyzau.tg.bot.pbot.handlers.command.sync.CommandSyncer;
import by.kobyzau.tg.bot.pbot.program.logger.Logger;
import by.kobyzau.tg.bot.pbot.program.text.ParametizedText;
import by.kobyzau.tg.bot.pbot.program.text.SimpleText;
import by.kobyzau.tg.bot.pbot.program.tokens.AccessTokenHolderFactory;
import by.kobyzau.tg.bot.pbot.program.tokens.TokenType;
import by.kobyzau.tg.bot.pbot.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Component
@Profile("dev")
public class DevApplicationStartupRunner implements ApplicationRunner {


  @Autowired
  private Environment env;

  @Autowired private Bot bot;

  @Value("${app.version}")
  private String version;

  @Value("${app.admin.userId}")
  private int adminUserId;

  @Autowired private Logger logger;

  @Autowired private AccessTokenHolderFactory tokenHolderFactory;

  @Autowired
  @Qualifier("AllCommandsSyncer")
  private CommandSyncer commandSyncer;

  @Autowired
  private BotActionCollector botActionCollector;

  @Override
  public void run(ApplicationArguments args) {
    logger.info("\u2B50\u2B50\u2B50\u2B50\u2B50\nDev DevApp started\nVersion " + version);
    bot.botConnect();
    logger.info("Bot started : " + bot.getBotUsername());
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
    botActionCollector.text(adminUserId, new SimpleText("Dev App is ready"));
    if (DateUtil.isNewYearTime() && Arrays.stream(env.getActiveProfiles()).noneMatch("new-year"::equals)){
      botActionCollector.text(adminUserId, new SimpleText("!!!new-year profile need to be activated"));
      logger.warn("!!!new-year profile need to be activated");
    }
  }
}
