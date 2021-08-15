package by.kobyzau.tg.bot.pbot.handlers.update;

import org.springframework.core.Ordered;

public enum UpdateHandlerStage implements Ordered {
  VALIDATE,
  TRACE_USER,
  COMMAND,
  CALLBACK,
  GAME,
  INVOKE_USER,
  SYSTEM;

  @Override
  public int getOrder() {
    return ordinal();
  }
}
