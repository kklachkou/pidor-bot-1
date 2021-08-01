package by.kobyzau.tg.bot.pbot.tasks;

import by.kobyzau.tg.bot.pbot.collectors.ReceiveUpdateCollector;
import by.kobyzau.tg.bot.pbot.handlers.update.UpdateHandler;
import by.kobyzau.tg.bot.pbot.program.logger.Logger;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.test.util.ReflectionTestUtils;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.Arrays;

import static by.kobyzau.tg.bot.pbot.handlers.update.UpdateHandlerStage.COMMAND;
import static by.kobyzau.tg.bot.pbot.handlers.update.UpdateHandlerStage.SYSTEM;
import static by.kobyzau.tg.bot.pbot.handlers.update.UpdateHandlerStage.VALIDATE;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class UpdateProcessorTaskTest {

  @InjectMocks public UpdateProcessorTask task;

  @Mock private ReceiveUpdateCollector updateCollector;

  @Mock private UpdateHandler handler1;
  @Mock private UpdateHandler handler2;
  @Mock private UpdateHandler handler3;

  @Mock private Logger logger;

  private final Update update = new Update();

  @Test
  public void processTaskTest() {
    // given
    ReflectionTestUtils.setField(
            task, "updateHandlers", Arrays.asList(handler3, handler1, handler2));
    doReturn(VALIDATE).when(handler1).getStage();
    doReturn(COMMAND).when(handler2).getStage();
    doReturn(SYSTEM).when(handler3).getStage();
    doReturn(false).when(handler1).handleUpdate(update);
    doReturn(true).when(handler2).handleUpdate(update);
    doReturn(true).when(handler3).handleUpdate(update);

    task.init();
    doReturn(update).doReturn(null).when(updateCollector).poll();

    //when
    task.processTask();

    //then
    verify(handler1, times(1)).handleUpdate(update);
    verify(handler2, times(1)).handleUpdate(update);
    verify(handler3, times(0)).handleUpdate(update);
  }
}
