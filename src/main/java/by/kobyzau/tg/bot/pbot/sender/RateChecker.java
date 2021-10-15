package by.kobyzau.tg.bot.pbot.sender;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

@Component
public class RateChecker {

  private static final int CACHE_SECONDS = 120;
  private static final int CHAT_LIMIT_PER_MINUTE = 20;
  private static final int BOT_LIMIT_PER_SECOND = 30;
  private final Map<Long, List<Long>> executions = new HashMap<>();
  private final Lock lock = new ReentrantLock();

  public boolean canSendMessage(long chatId) {
    lock.lock();
    try {
      cleanOldExecutions(chatId);
      if (!isExceedTimeLimit(chatId)) {
        logTime(chatId);
        return true;
      }
    } finally {
      lock.unlock();
    }
    return false;
  }

  private void cleanOldExecutions(long chatId) {
    long startTime = System.currentTimeMillis() - CACHE_SECONDS * 1000;
    List<Long> chatExecutions = executions.get(chatId);
    if (chatExecutions == null) {
      return;
    }
    List<Long> newChatExecutions =
        chatExecutions.stream().filter(e -> e > startTime).collect(Collectors.toList());
    executions.put(chatId, newChatExecutions);
  }

  private boolean isExceedTimeLimit(long chatId) {
    long currentTime = System.currentTimeMillis();
    boolean isExceedBotLimit =
        executions.values().stream()
                .flatMap(Collection::stream)
                .filter(l -> l >= (currentTime - 1200))
                .count()
            >= BOT_LIMIT_PER_SECOND;
    if (isExceedBotLimit) {
      return true;
    }
    List<Long> chatExecutions = executions.getOrDefault(chatId, Collections.emptyList());
    boolean inLastSecond = chatExecutions.stream().anyMatch(l -> l >= (currentTime - 1500));
    if (inLastSecond) {
      return true;
    }
    return chatExecutions.stream().filter(l -> l >= (currentTime - 1050 * 60)).count()
        >= CHAT_LIMIT_PER_MINUTE;
  }

  private void logTime(long chatId) {
    List<Long> chatExecutions = executions.getOrDefault(chatId, new ArrayList<>());
    chatExecutions.add(System.currentTimeMillis());
    executions.put(chatId, chatExecutions);
  }
}
