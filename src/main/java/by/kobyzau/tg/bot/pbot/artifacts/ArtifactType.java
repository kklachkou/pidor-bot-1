package by.kobyzau.tg.bot.pbot.artifacts;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ArtifactType {
  // Cleans in the end of the day
  PIDOR_MAGNET(
      "Пидорский магнит",
      "\uD83E\uDDF2",
      "Притягивает пидоров и увеличивает шансы стать пидором",
      false),
  // Cleans on second chance
  SECOND_CHANCE(
      "Боевой Пидор-маг",
      "\uD83E\uDDD9\uD83C\uDFFB\u200D♂",
      "Позволяет кинуть emoji 2й раз. Он заставит всех забыть твой худший бросок",
      true),
  // Cleans on election, exclude, dice finalize
  SILENCE(
      "Немота",
      "\uD83D\uDE4A",
      "В день 'Не пидор я' и пидор-выборов не могу говорить до 12:00",
      false),
  // Cleans on election finalize
  BLINDNESS(
      "Слепота", "\uD83D\uDE48", "В день пидор-выборов ты не видишь за кого голосуешь", false),
  // Cleans on election finalize
  RICOCHET(
      "Рикошет",
      "\uD83D\uDEE1",
      "В день выборов, проголосовавший за тебя, с вероятностью 50% получит сам голос вместо тебя",
      true),
  // Cleans in the end of the day
  ANTI_PIDOR(
      "Анти-пидорин",
      "\uD83D\uDC8A",
      "Лекарство от пидороства существует! С этой таблеткой ты гарантированно не будешь пидором!",
      true),
  // Cleans on potato end
  HELL_FIRE("Адское пламя", "\uD83D\uDD25", "Горячая картошечка сгорает намного быстрее", false),
  // After election
  SUPER_VOTE(
      "Вброс",
      "\uD83C\uDDF7\uD83C\uDDFA",
      "Ты бросаешь целую пачку биллютеней в день пидор-выборов. Один твой голос стоит 5-х. Совсем как в жизни!",
      true);

  private final String name;
  private final String emoji;
  private final String desc;
  private final boolean bonus;
}
