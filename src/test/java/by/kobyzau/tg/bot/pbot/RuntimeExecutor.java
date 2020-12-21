package by.kobyzau.tg.bot.pbot;

import java.util.concurrent.Executor;

public class RuntimeExecutor implements Executor {

  @Override
  public void execute(Runnable runnable) {
    runnable.run();
  }
}
