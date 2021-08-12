package by.kobyzau.tg.bot.pbot.service;

import java.time.LocalDate;
import java.util.List;

public interface FutureActionService {

  List<String> getFutureActionData(FutureActionType type, LocalDate date);

  void saveFutureActionData(FutureActionType type, LocalDate date, String data);

  void removeFutureData(FutureActionType type, LocalDate date);

  enum FutureActionType {
    ENABLE_SETTING, DEFAULT
  }
}
