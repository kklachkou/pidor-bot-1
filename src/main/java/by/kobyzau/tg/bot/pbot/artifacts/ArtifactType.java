package by.kobyzau.tg.bot.pbot.artifacts;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ArtifactType {
  // Cleans in the end of the day
  PIDOR_MAGNET(
      "Пидорский магнит", "\uD83E\uDDF2", "Притягивает пидоров и увеличивает шансы стать пидором", false),
  // Cleans on second chance
  SECOND_CHANCE(
      "Боевой Пидор-маг",
      "\uD83E\uDDD9\uD83C\uDFFB\u200D♂",
      "Позволяет кинуть кубик 2й раз. Он заставит всех забыть твой худший бросок", true),
  // Cleans on election, exclude finalize
  SILENCE(
      "Немота", "\uD83D\uDE4A", "В день 'Не пидор я' и пидор-выборов не могу говорить до 12:00", false),
  // Cleans on election finalize
  RICOCHET(
      "Рикошет",
      "\uD83D\uDEE1",
      "В день выборов, проголосовавший за тебя, с вероятностью 50% получит сам голос вместо тебя", true);

  private final String name;
  private final String emoji;
  private final String desc;
  private final boolean bonus;
}
