package by.kobyzau.tg.bot.pbot.sender.async.runner;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public abstract class DefaultSenderRunner implements SenderRunner {

  private static final int THREAD_LIVE_IN_PENDING = 3;

  private final AtomicInteger numIterationsWithPending = new AtomicInteger(0);
  private final Lock lock = new ReentrantLock();
  private volatile RunnerState state;

  public DefaultSenderRunner() {
    this.state = RunnerState.WORKING;
  }

  @Override
  public RunnerState getState() {
    return state;
  }

  @Override
  public boolean applyState(RunnerState state) {
    try {
      lock.lock();
      switch (this.state) {
        case DEAD:
          return state == RunnerState.DEAD;
        case WORKING:
          switch (state) {
            case WORKING:
              this.numIterationsWithPending.set(0);
              return true;
            case PENDING:
              this.numIterationsWithPending.set(0);
              this.state = RunnerState.PENDING;
              return true;
            case DEAD:
              return false;
          }
        case PENDING:
          switch (state) {
            case WORKING:
              this.numIterationsWithPending.set(0);
              this.state = RunnerState.WORKING;
              return true;
            case DEAD:
              if (numIterationsWithPending.get() > THREAD_LIVE_IN_PENDING) {
                this.state = RunnerState.DEAD;
                return true;
              }
              return false;
            case PENDING:
              if (this.numIterationsWithPending.get() < 0) {
                this.numIterationsWithPending.set(0);
              }
              this.numIterationsWithPending.incrementAndGet();
              return true;
          }
      }
    } finally {
      lock.unlock();
    }
    return false;
  }
}
