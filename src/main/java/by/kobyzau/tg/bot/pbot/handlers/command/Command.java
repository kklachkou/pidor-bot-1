package by.kobyzau.tg.bot.pbot.handlers.command;

import by.kobyzau.tg.bot.pbot.util.StringUtil;

import java.util.Optional;
import java.util.stream.Stream;

public enum Command {
  NONE(""),
  PIDOR(
      "pidor",
      "Ежедревное участие в игре",
      "Команда для запуска поиска пидора дня",
      Category.ACTION,
      1),
  STATUS(
      "stats",
      "Статистика за год",
      "Узнать статистику за год. Введя '/stats 2017' можно узнать статистику за 2017 год",
      Category.INFO,
      2),
  CHANCES(
      "chances",
      "Калькулятор шанса стать пидором года",
      "Расчитывает вероятность стать пидором года",
      Category.INFO,
      3),
  GAME(
      "game",
      "Информация по сегодняшней игре",
      "Показывает информацию по сегодняшней игре",
      Category.INFO,
      4),
  HELP("help", "Помощь по боту", "Выводит список комманд бота", Category.INFO, 5),
  MY_STATUS(
      "my_status",
      "Узнать всю информацию о себе",
      "Указыват информацию о твоих пидорских днях",
      Category.INFO,
      7),
  SCHEDULE(
      "schedule",
      "Активности на ближайшие пару дней",
      "Выводит список активностей, которые должны случится в течении следующих нескольких дней",
      Category.INFO,
      8),

  ACHIEVEMENT(
      "achievements", "Список достяжений", "Выводит список всех достижений", Category.INFO, 8),

  NICKNAME(
      "nickname",
      "Ввести псевдоним",
      "Ввести свой псевдоним."
          + " Необходимо ввести команду вместе с никнеймом в одном сообщении."
          + " Чтобы удалить псевдоним, нужно ввести CLEAR в качестве псевдонима",
      Category.ACTION,
      9),
  REG_PIDOR(
      "reg_pidor",
      "Регистрация пидора",
      "Регистрация пидора, который не хочет сам участвовать в игре."
          + " Отправь эту команду вместе с упоминанием человека через @. Но увы, некоторых пользователей бот не сможет увидеть и так",
      Category.ACTION,
      10),
  PIDOR_REG(
      "i_am_pidor",
      "Регистрация в игре",
      "Регистрация в игре в качестве пидора",
      Category.ACTION,
      11),
  CALLER(
      "callers",
      "Число вызовов бота",
      "Показывает кто как часто призывал пидор-бота",
      Category.INFO,
      12),
  INLINE("inline"),
  CURRENT_TIME("time"),
  STICKERS("stickers"),
  GIF("gif"),
  NOTIFY("notify"),
  TEST("test"),
  CLEANUP("cleanup"),
  VERSION("version"),
  SQL("sql"),
  LOGGER("logger"),
  FULL_INFO("full_info"),
  BACKUP("backup");

  private final String name;
  private final String shortDesc;
  private final String desc;
  private final Category category;
  private final int order;

  Command(String name) {
    this(name, null, null, Category.NONE, Integer.MAX_VALUE);
  }

  Command(String name, String shortDesc, String desc, Category category, int order) {
    this.name = name;
    this.shortDesc = shortDesc;
    this.desc = desc;
    this.order = order;
    this.category = category;
  }

  public static Optional<Command> parseCommand(String command) {
    if (StringUtil.isBlank(command)) {
      return Optional.empty();
    }
    String formatName = formatName(command);
    return Stream.of(values()).filter(c -> c.name.equalsIgnoreCase(formatName)).findFirst();
  }

  public String getShortDesc() {
    return shortDesc;
  }

  public Category getCategory() {
    return category;
  }

  public String getDesc() {
    return desc;
  }

  public int getOrder() {
    return order;
  }

  public String getName() {
    return name;
  }

  private static String formatName(String s) {
    return StringUtil.trim(s).toLowerCase();
  }

  public enum Category {
    INFO("Информация"),
    ACTION("Действие"),
    NONE("");
    private final String name;

    Category(String name) {
      this.name = name;
    }

    public String getName() {
      return name;
    }
  }
}
