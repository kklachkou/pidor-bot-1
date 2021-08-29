package by.kobyzau.tg.bot.pbot.sender.async.runner;

import by.kobyzau.tg.bot.pbot.sender.BotApiMethodCallback;
import by.kobyzau.tg.bot.pbot.sender.methods.SendMethod;
import lombok.Data;

public interface SenderRunner extends Runnable {

  void add(Param<?> botAction);

  RunnerState getState();

  boolean applyState(RunnerState state);

  enum RunnerState {
    PENDING,
    WORKING,
    DEAD
  }

  @Data
  class Param<P> {
    private SendMethod<P> method;
    private BotApiMethodCallback<P> callback;

    public Param(SendMethod<P> method) {
      this.method = method;
    }

    public Param(SendMethod<P> method, BotApiMethodCallback<P> callback) {
      this.method = method;
      this.callback = callback;
    }
  }
}
