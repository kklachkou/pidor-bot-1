package by.kobyzau.tg.bot.pbot.artifacts;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ArtifactType {
  PIDOR_MAGNET(
      "Пидорский магнит", "\uD83E\uDDF2", "Притягивает пидоров и увеличивает шансы стать пидором"),
  SECOND_CHANCE(
      "Боевой Пидор-маг",
      "\uD83E\uDDD9\uD83C\uDFFB\u200D♂",
      "Позволяет кинуть кубик 2й раз. Он заставит всех забыть твой худший бросок");

  private final String name;
  private final String emoji;
  private final String desc;
}
