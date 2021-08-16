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

  SETTINGS(
      "settings",
      "Настройки бота",
      "Управление тонкими настройками бота, включение и выключение функционала."
          + " Изменения настроек может быть не моментальным (в течении 24х часов)",
      Category.ACTION,
      9),

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
  PIDOR_REG(
      "i_am_pidor",
      "Регистрация в игре",
      "Регистрация в игре в качестве пидора",
      Category.ACTION,
      11),
  YEARLY_STAT("yearly_stat", "Fill in Yearly Stat"),
  CURRENT_TIME("time", "Print Server Time"),
  QUESTIONNAIRE("questionnaire", "Send Questionnaire"),
  STICKERS("stickers", "Show Stickers"),
  GIF("gif", "Show Gif"),
  STAT("stat", "Get Statistic"),
  NOTIFY("notify", "Send notification"),
  TEST("test", "Test command"),
  CLEANUP("cleanup", "Start cleanup"),
  SYSTEM_CHECK("system_check", "Start System Checks"),
  SQL("sql", "Execute sql"),
  LOGGER("logger", "Set Logger Level"),
  VERSION("version", "Send version info"),
  GAME(""),
  HOT_POTATOES(""),
  ELECTION(""),

  BACKUP("backup");

  private final String name;
  private final String shortDesc;
  private final String desc;
  private final Category category;
  private final int order;

  Command(String name) {
    this(name, null, null, Category.NONE, Integer.MAX_VALUE);
  }

  Command(String name, String shortDesc) {
    this(name, shortDesc, null, Category.NONE, Integer.MAX_VALUE);
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
