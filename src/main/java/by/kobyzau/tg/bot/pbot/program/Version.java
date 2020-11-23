package by.kobyzau.tg.bot.pbot.program;

import by.kobyzau.tg.bot.pbot.handlers.command.Command;
import by.kobyzau.tg.bot.pbot.program.text.*;

import java.time.LocalDate;

public enum Version {
  VERSION_1_10(
      LocalDate.of(2020, 11, 22),
      new TextBuilder(new SimpleText("- Изменен процесс игры Отссассин"))
          .append(new NewLineText())
          .append(new SimpleText("Следи за тем какая будет игра через команду /schedule"))
          .append(new NewLineText())
          .append(new NewLineText())
          .append(
              new ParametizedText(
                  "- Добавлена новая команда /{0}", new SimpleText(Command.REG_PIDOR.getName())))
          .append(
              new SimpleText(
                  "Отправь эту команду в ответ на сообщение человека, который сам никак не зарегистритуеться в игре"))
          .append(new NewLineText())
          .append(new SimpleText("Зарегистрируй тихоню:)"))),
  VERSION_1_9(
      LocalDate.of(2020, 11, 5),
      new TextBuilder(new ParametizedText("- Футбол! Новая игра - {0}", new SimpleText("⚽")))
          .append(new NewLineText())
          .append(new SimpleText("Следи за тем какая будет игра через команду /schedule"))
          .append(new NewLineText())
          .append(new NewLineText())
          .append(
              new SimpleText(
                  "- Исправлено колличество людей, принимающих участие в игре. Теперь 80% участников чата должны принять участие в игре"))),
  VERSION_1_8(
      LocalDate.of(2020, 11, 3),
      new TextBuilder(
              new SimpleText(
                  "Будь первым! Теперь во время любых игр лишь половина людей сможет исключить себя из игры."))
          .append(new NewLineText())
          .append(new SimpleText("Далее бот выберет пидора дня из оставшихся людей"))
          .append(new NewLineText())
          .append(new NewLineText())
          .append(
              new SimpleText(
                  "Добавлено новое секретное событие. Надеюсь ты достаточно внимательный"))),
  VERSION_1_7(
      LocalDate.of(2020, 10, 25),
      new TextBuilder(new ParametizedText("Новое событие - день от{0}на", new BoldText("саси")))
          .append(new NewLineText())
          .append(
              new SimpleText(
                  "В этот день ты можешь заказать кого-либо, чтобы он стал пидором дня."
                      + " Однако, заказав кого-либо, ты рискуешь сам стать пидором дня"))
          .append(new NewLineText())
          .append(
              new ParametizedText(
                  "Когда будет данный день ты можешь узнать по команде /{0}",
                  new SimpleText(Command.SCHEDULE.getName()))));

  private final LocalDate release;
  private final Text description;

  Version(LocalDate release, Text description) {
    this.release = release;
    this.description = description;
  }

  public LocalDate getRelease() {
    return release;
  }

  public Text getDescription() {
    return description;
  }

  public static Version getLast() {
    return values()[0];
  }
}
