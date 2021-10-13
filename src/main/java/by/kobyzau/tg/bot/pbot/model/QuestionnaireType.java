package by.kobyzau.tg.bot.pbot.model;

import java.util.Arrays;
import java.util.List;

public enum QuestionnaireType {
  LOCATION(
      "Страна",
      "Из какой ты страны?",
      true,
      "Беларусь",
      "Россия",
      "Украина",
      "Польша",
      "Литва",
      "Другая (ЕС)",
      "Вообще другая"),
  BEST_GAMES_EMOJI(
      "Игры ⚽\uD83C\uDFC0\uD83C\uDFB2\uD83C\uDFB3\ud83c\udfaf",
      "Оцените игры-эмоджи (кубик, футбол,...)",
      true,
      "⭐⭐⭐",
      "⭐⭐",
      "⭐"),
  BEST_GAMES_EXCLUDE("Чур не я", "Оцените игру 'Чур не я'", true, "⭐⭐⭐", "⭐⭐", "⭐"),
  BEST_GAMES_ELECTION("Пидор-выборы", "Оцените игру 'Пидор-Выборы'", true, "⭐⭐⭐", "⭐⭐", "⭐"),
  BEST_GAMES_POTATO(
      "Горячая картошечка", "Оцените игру 'Горячая картошечка'", true, "⭐⭐⭐", "⭐⭐", "⭐"),
  ;

  private final String name;
  private final String question;
  private boolean showResults;
  private final List<String> options;

  QuestionnaireType(String name, String question, boolean showResults, String... options) {
    this.name = name;
    this.question = question;
    this.options = Arrays.asList(options);
  }

  public String getName() {
    return name;
  }

  public String getQuestion() {
    return question;
  }

  public List<String> getOptions() {
    return options;
  }

  public boolean isShowResults() {
    return showResults;
  }
}
