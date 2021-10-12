package by.kobyzau.tg.bot.pbot.sender;

import by.kobyzau.tg.bot.pbot.util.ThreadUtil;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

import static org.junit.Assert.assertTrue;

@Ignore
@Slf4j
public class RateCheckerThreadTest {

  private RateChecker rateChecker;
  private static final int NUM_THREADS = 60;
  private static final int NUM_CHATS = 50;
  private static final int NUM_MESSAGES = 30;
  private static final Map<Long, List<Long>> executions = new HashMap<>();

  @Before
  public void init() {
    rateChecker = new RateChecker();
  }
  @Test
  @SneakyThrows
  public void checkRatesCachedExecutor() {
    ExecutorService executorService = Executors.newCachedThreadPool();
    List<Future<?>> futures = new ArrayList<>();
    long startTime = System.currentTimeMillis();
    for (int i = 0; i < NUM_CHATS; i++) {
      futures.add(executorService.submit(new SenderRunnable(i)));
    }
    for (Future<?> future : futures) {
      while (!future.isDone()) {
        ThreadUtil.sleep(10);
      }
    }
    log.info(
            "All messages are sent in {} seconds", (System.currentTimeMillis() - startTime) / 1000);
    for (long i = 0; i < NUM_CHATS; i++) {
      List<Long> chatExecution = executions.get(i);
      log.info(
              "Chat {} sent all messages in {} seconds",
              i,
              (chatExecution.stream().max(Long::compare).orElse(0L)
                      - chatExecution.stream().min(Long::compare).orElse(0L))
                      / 1000);
    }

    verifyChatLimits();
  }

  @Test
  @SneakyThrows
  public void checkRatesFixedExecutor() {
    ExecutorService executorService = Executors.newFixedThreadPool(NUM_THREADS);
    List<Future<?>> futures = new ArrayList<>();
    long startTime = System.currentTimeMillis();
    for (int i = 0; i < NUM_CHATS; i++) {
      futures.add(executorService.submit(new SenderRunnable(i)));
    }
    for (Future<?> future : futures) {
      while (!future.isDone()) {
        ThreadUtil.sleep(10);
      }
    }
    log.info(
        "All messages are sent in {} seconds", (System.currentTimeMillis() - startTime) / 1000);
    for (long i = 0; i < NUM_CHATS; i++) {
      List<Long> chatExecution = executions.get(i);
      log.info(
          "Chat {} sent all messages in {} seconds",
          i,
          (chatExecution.stream().max(Long::compare).orElse(0L)
                  - chatExecution.stream().min(Long::compare).orElse(0L))
              / 1000);
    }

    verifyChatLimits();
  }

  @Test
  @SneakyThrows
  public void checkRatesThreads() {
    List<Thread> threads = new ArrayList<>();
    for (int i = 0; i < NUM_CHATS; i++) {
      threads.add(new Thread(new SenderRunnable(i), "sender-" + i));
    }
    long startTime = System.currentTimeMillis();
    threads.forEach(Thread::start);
    for (Thread thread : threads) {
      thread.join();
    }
    log.info(
        "All messages are sent in {} seconds", (System.currentTimeMillis() - startTime) / 1000);
    for (long i = 0; i < NUM_CHATS; i++) {
      List<Long> chatExecution = executions.get(i);
      log.info(
          "Chat {} sent all messages in {} seconds",
          i,
          (chatExecution.stream().max(Long::compare).orElse(0L)
                  - chatExecution.stream().min(Long::compare).orElse(0L))
              / 1000);
    }
    verifyChatLimits();
  }

  private void verifyChatLimits() {
    List<Long> allExecutions =
        executions.values().stream()
            .flatMap(Collection::stream)
            .sorted()
            .collect(Collectors.toList());
    for (long executionTime : allExecutions) {
      assertTrue(
          "[30 per second] "
              + executionTime
              + " - "
              + (executionTime + 1000)
              + ": "
              + allExecutions.stream()
                  .filter(e -> e >= executionTime)
                  .filter(e -> e <= executionTime + 1000)
                  .count(),
          allExecutions.stream()
                  .filter(e -> e >= executionTime)
                  .filter(e -> e <= executionTime + 1000)
                  .count()
              <= 30);
      assertTrue(
          "[30 per second] "
              + (executionTime - 1000)
              + " - "
              + executionTime
              + ": "
              + allExecutions.stream()
                  .filter(e -> e <= executionTime)
                  .filter(e -> e >= executionTime - 1000)
                  .count(),
          allExecutions.stream()
                  .filter(e -> e <= executionTime)
                  .filter(e -> e >= executionTime - 1000)
                  .count()
              <= 30);
    }

    for (long i = 0; i < NUM_CHATS; i++) {
      List<Long> chatExecutions =
          executions.getOrDefault(i, Collections.emptyList()).stream()
              .sorted()
              .collect(Collectors.toList());
      for (int message = 1; message < chatExecutions.size() - 1; message++) {
        long messageTime = chatExecutions.get(message);
        assertTrue(
            "[1 per second] Chat " + i,
            Math.abs(messageTime - chatExecutions.get(message - 1)) > 1000);
        assertTrue(
            "[1 per second] Chat " + i,
            Math.abs(messageTime - chatExecutions.get(message + 1)) > 1000);
        assertTrue(
            "[20 per minute] Chat " + i,
            chatExecutions.stream()
                    .filter(e -> e <= messageTime)
                    .filter(e -> e >= messageTime - 60 * 1000)
                    .count()
                <= 20);
        assertTrue(
            "[20 per minute] Chat " + i,
            chatExecutions.stream()
                    .filter(e -> e >= messageTime)
                    .filter(e -> e <= messageTime + 60 * 1000)
                    .count()
                <= 20);
      }
    }
  }

  @RequiredArgsConstructor
  private class SenderRunnable implements Runnable {

    private final long chatId;

    @Override
    public void run() {
      log.info("Start {}", chatId);
      int messageIndex = 1;
      for (int i = 0; i < NUM_MESSAGES; i++) {
        if (i % 2 == 0) {
          ThreadUtil.sleep(2000);
          continue;
        }
        while (!rateChecker.canSendMessage(chatId)) {}
        logExecution(chatId, messageIndex++);
      }
      log.info("End {}", chatId);
    }
  }

  private synchronized void logExecution(long chatId, int message) {
    long time = System.currentTimeMillis();
    log.info("Chat {} sent message {}: {}", chatId, message, time);
    List<Long> chatExecutions = executions.getOrDefault(chatId, new ArrayList<>());
    chatExecutions.add(time);
    executions.put(chatId, chatExecutions);
  }
}
