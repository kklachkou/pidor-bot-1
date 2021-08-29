package by.kobyzau.tg.bot.pbot.sender.async.runner;

import by.kobyzau.tg.bot.pbot.program.logger.Logger;
import by.kobyzau.tg.bot.pbot.sender.BotApiMethodCallback;
import by.kobyzau.tg.bot.pbot.sender.methods.SendMethod;
import by.kobyzau.tg.bot.pbot.util.ThreadUtil;
import lombok.SneakyThrows;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.telegram.telegrambots.meta.bots.AbsSender;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static by.kobyzau.tg.bot.pbot.sender.async.runner.SenderRunner.RunnerState.*;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class AsyncGroupSenderRunnerTest {

  @Mock private AbsSender sender;
  @Mock private Logger logger;

  private AsyncGroupSenderRunner runner;

  @Before
  public void init() {
    this.runner = new AsyncGroupSenderRunner(sender, logger);
  }

  @Test(timeout = 5000)
  @SneakyThrows
  public void run_checkGroupLimit_hasLimit() {
    // given
    int methodsNum = 8;
    int expectedPassed = 7;
    ExecutorService executorService = Executors.newSingleThreadExecutor();
    List<SendMethod<?>> sendMethodList = new ArrayList<>();
    for (int i = 0; i < methodsNum; i++) {
      SendMethod<?> sendMethod = mock(SendMethod.class, "sendMethod" + i);
      doReturn("sendmessage").when(sendMethod).getMethod();
      sendMethodList.add(sendMethod);
      runner.add(new SenderRunner.Param<>(sendMethod));
    }

    // when
    executorService.execute(() -> runner.run());
    ThreadUtil.sleep(4000);
    executorService.shutdown();

    // then
    for (int i = 0; i < expectedPassed; i++) {
      verify(sendMethodList.get(i)).send(sender);
    }
    for (int i = expectedPassed; i < methodsNum; i++) {
      verify(sendMethodList.get(i), times(0)).send(sender);
    }
  }

  @Test(timeout = 2000)
  @SneakyThrows
  public void run_checkGroupLimit_withoutLimit() {
    // given
    int methodsNum = 100;
    ExecutorService executorService = Executors.newSingleThreadExecutor();
    List<SendMethod<?>> sendMethodList = new ArrayList<>();
    for (int i = 0; i < methodsNum; i++) {
      SendMethod<?> sendMethod = mock(SendMethod.class, "sm" + i);
      doReturn("unknown").when(sendMethod).getMethod();
      sendMethodList.add(sendMethod);
      runner.add(new SenderRunner.Param<>(sendMethod));
    }

    // when
    executorService.execute(() -> runner.run());
    ThreadUtil.sleep(1000);
    executorService.shutdown();

    // then
    for (int i = 0; i < methodsNum; i++) {
      verify(sendMethodList.get(i)).send(sender);
    }
  }

  @Test(timeout = 2600)
  public void run_completedWithoutItems() {
    // when
    runner.run();
  }

  @Test
  @SneakyThrows
  public void run_send_callback() {
    // given
    String response = "response";
    ExecutorService executorService = Executors.newSingleThreadExecutor();
    SendMethod<String> sendMethod = mock(SendMethod.class);
    doReturn(response).when(sendMethod).send(sender);
    doReturn("unknown").when(sendMethod).getMethod();
    BotApiMethodCallback<String> callback = mock(BotApiMethodCallback.class);
    runner.add(new SenderRunner.Param<>(sendMethod, callback));

    // when
    executorService.execute(() -> runner.run());
    ThreadUtil.sleep(500);
    executorService.shutdown();

    // then
    verify(callback).handleResult(response);
  }

  @Test
  public void applyState_test() {
    assertTrue(runner.applyState(WORKING));
    assertTrue(runner.applyState(WORKING));
    assertFalse(runner.applyState(DEAD));
    assertTrue(runner.applyState(PENDING));
    assertTrue(runner.applyState(WORKING));
    assertTrue(runner.applyState(PENDING));
    assertFalse(runner.applyState(DEAD));
    assertTrue(runner.applyState(PENDING));
    assertFalse(runner.applyState(DEAD));
    assertTrue(runner.applyState(PENDING));
    assertFalse(runner.applyState(DEAD));
    assertTrue(runner.applyState(PENDING));
    assertFalse(runner.applyState(DEAD));
    assertTrue(runner.applyState(PENDING));
    assertTrue(runner.applyState(DEAD));
    assertTrue(runner.applyState(DEAD));
    assertFalse(runner.applyState(PENDING));
    assertFalse(runner.applyState(WORKING));
  }
}
