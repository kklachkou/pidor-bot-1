package by.kobyzau.tg.bot.pbot.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum PidorMark {
  PIDOR_OF_YEAR("\uD83D\uDC51"),
  LAST_PIDOR_OF_DAY("\uD83D\uDC13"),
  COVID("\uD83E\uDDA0");

  private final String emoji;
}
