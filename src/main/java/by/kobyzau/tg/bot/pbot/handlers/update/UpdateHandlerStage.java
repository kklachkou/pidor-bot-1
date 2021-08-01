package by.kobyzau.tg.bot.pbot.handlers.update;

import org.springframework.core.Ordered;

public enum UpdateHandlerStage implements Ordered {
  VALIDATE(0),
  COMMAND(1),
  GAME(2),
  INVOKE_USER(3),
  CALLBACK(4),
  SYSTEM(5);

  private final int order;

  UpdateHandlerStage(int order) {
    this.order = order;
  }

  @Override
  public int getOrder() {
    return order;
  }
}
