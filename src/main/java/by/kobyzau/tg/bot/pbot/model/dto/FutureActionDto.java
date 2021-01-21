package by.kobyzau.tg.bot.pbot.model.dto;

import by.kobyzau.tg.bot.pbot.service.FutureActionService;

public class FutureActionDto {

  private FutureActionService.FutureActionType type;
  private String data;

  public FutureActionDto() {
  }

  public FutureActionDto(FutureActionService.FutureActionType type, String data) {
    this.type = type;
    this.data = data;
  }

  public FutureActionService.FutureActionType getType() {
    return type;
  }

  public void setType(FutureActionService.FutureActionType type) {
    this.type = type;
  }

  public String getData() {
    return data;
  }

  public void setData(String data) {
    this.data = data;
  }
}
