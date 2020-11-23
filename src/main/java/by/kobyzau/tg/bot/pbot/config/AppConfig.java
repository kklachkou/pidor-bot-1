package by.kobyzau.tg.bot.pbot.config;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

@Configuration
@PropertySource(value = {"/application.properties"})
public class AppConfig {

  @Value("${app.threadNum.sendMessages}")
  private int sendMessagesThreadNum;

  @Value("${app.threadNum.tasks}")
  private int tasksThreadNum;

  @Bean("taskExecutor")
  public Executor getTaskExecutor() {
    ThreadFactory threadFactory =
        new ThreadFactoryBuilder().setNameFormat("my-fixed-thread-%d").build();
    return Executors.newFixedThreadPool(tasksThreadNum, threadFactory);
  }

  @Bean("cachedExecutor")
  public Executor getCachedExecutor() {
    ThreadFactory threadFactory =
        new ThreadFactoryBuilder().setNameFormat("my-cached-thread-%d").build();
    return Executors.newCachedThreadPool(threadFactory);
  }

  @Bean("sendMessagesExecutor")
  public Executor getSendMessagesExecutor() {
    ThreadFactory threadFactory =
        new ThreadFactoryBuilder()
            .setNameFormat("my-send-messages-thread-%d")
            .setPriority(Thread.MAX_PRIORITY)
            .build();
    return Executors.newFixedThreadPool(sendMessagesThreadNum, threadFactory);
  }
}
