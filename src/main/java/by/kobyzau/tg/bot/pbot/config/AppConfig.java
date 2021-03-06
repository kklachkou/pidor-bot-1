package by.kobyzau.tg.bot.pbot.config;

import by.kobyzau.tg.bot.pbot.program.executor.LoggerExecutor;
import by.kobyzau.tg.bot.pbot.program.logger.Logger;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

@Configuration
@PropertySource(value = {"/application.yaml"})
public class AppConfig {

  @Value("${app.threadNum.tasks}")
  private int tasksThreadNum;

  @Value("${app.threadNum.updatePidor}")
  private int updatePidorThreadNum;

  @Autowired private Logger logger;

  @Bean("taskExecutor")
  public Executor getTaskExecutor() {
    ThreadFactory threadFactory =
        new ThreadFactoryBuilder().setNameFormat("my-fixed-thread-%d").build();
    return new LoggerExecutor(Executors.newFixedThreadPool(tasksThreadNum, threadFactory), logger);
  }

  @Bean("updatePidorExecutor")
  public Executor getUpdatePidorExecutor() {
    ThreadFactory threadFactory =
        new ThreadFactoryBuilder().setNameFormat("update-pidor-%d").build();
    return new LoggerExecutor(Executors.newFixedThreadPool(updatePidorThreadNum, threadFactory), logger);
  }

  @Bean("asyncPidorBotSenderExecutor")
  public Executor getAsyncPidorBotSenderExecutor() {
    ThreadFactory threadFactory =
        new ThreadFactoryBuilder().setNameFormat("p-bot-sender-%d").build();
    return new LoggerExecutor(Executors.newFixedThreadPool(25, threadFactory), logger);
  }

  @Bean("cachedExecutor")
  public Executor getCachedExecutor() {
    ThreadFactory threadFactory =
        new ThreadFactoryBuilder().setNameFormat("my-cached-thread-%d").build();
    return new LoggerExecutor(Executors.newCachedThreadPool(threadFactory), logger);
  }

  @Bean("loggerExecutor")
  public Executor getLoggerExecutor() {
    ThreadFactory threadFactory =
        new ThreadFactoryBuilder().setNameFormat("my-logger-thread-%d").build();
    return Executors.newCachedThreadPool(threadFactory);
  }

  @Bean("sendMessagesExecutor")
  public Executor getSendMessagesExecutor() {
    ThreadFactory threadFactory =
        new ThreadFactoryBuilder()
            .setNameFormat("my-send-messages-thread-%d")
            .setPriority(Thread.MAX_PRIORITY)
            .build();
    return new LoggerExecutor(Executors.newCachedThreadPool(threadFactory), logger);
  }
}
