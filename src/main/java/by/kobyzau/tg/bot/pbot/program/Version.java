package by.kobyzau.tg.bot.pbot.program;

import by.kobyzau.tg.bot.pbot.artifacts.ArtifactType;
import by.kobyzau.tg.bot.pbot.handlers.command.Command;
import by.kobyzau.tg.bot.pbot.program.text.*;
import by.kobyzau.tg.bot.pbot.tg.sticker.StickerType;

import java.time.LocalDate;

public enum Version {
  VERSION_12_4(
          LocalDate.of(2021, 10, 15),
          new TextBuilder(new BoldText("И ещё больше артефактов!"))
                  .append(new NewLineText())
                  .append(new NewLineText())
                  .append(new SimpleText("Новый артефакт:"))
                  .append(new NewLineText())
                  .append(
                          new ParametizedText(
                                  "\t\t{0} {1} {2}: {3}",
                                  new SimpleText(
                                          ArtifactType.SUPER_VOTE.isBonus() ? "\uD83D\uDC4D" : "\uD83D\uDC4E"),
                                  new ItalicText(ArtifactType.SUPER_VOTE.getName()),
                                  new SimpleText(ArtifactType.SUPER_VOTE.getEmoji()),
                                  new SimpleText(ArtifactType.SUPER_VOTE.getDesc())))
                  .append(new NewLineText())
                  .append(new NewLineText())
                  .append(new SimpleText("Теперь выборы как в жизни!"))),
  VERSION_12_2(
      LocalDate.of(2021, 9, 21),
      new TextBuilder(new BoldText("Ещё больше артефактов!"))
          .append(new NewLineText())
          .append(new NewLineText())
          .append(new SimpleText("Новые виды артефактов:"))
          .append(new NewLineText())
          .append(
              new ParametizedText(
                  "\t\t{0} {1} {2}: {3}",
                  new SimpleText(
                      ArtifactType.ANTI_PIDOR.isBonus() ? "\uD83D\uDC4D" : "\uD83D\uDC4E"),
                  new ItalicText(ArtifactType.ANTI_PIDOR.getName()),
                  new SimpleText(ArtifactType.ANTI_PIDOR.getEmoji()),
                  new SimpleText(ArtifactType.ANTI_PIDOR.getDesc())))
          .append(new NewLineText())
          .append(
              new ParametizedText(
                  "\t\t{0} {1} {2}: {3}",
                  new SimpleText(
                      ArtifactType.HELL_FIRE.isBonus() ? "\uD83D\uDC4D" : "\uD83D\uDC4E"),
                  new ItalicText(ArtifactType.HELL_FIRE.getName()),
                  new SimpleText(ArtifactType.HELL_FIRE.getEmoji()),
                  new SimpleText(ArtifactType.HELL_FIRE.getDesc())))
          .append(new NewLineText())
          .append(
              new ParametizedText(
                  "\t\t{0} {1} {2}: {3}",
                  new SimpleText(
                      ArtifactType.BLINDNESS.isBonus() ? "\uD83D\uDC4D" : "\uD83D\uDC4E"),
                  new ItalicText(ArtifactType.BLINDNESS.getName()),
                  new SimpleText(ArtifactType.BLINDNESS.getEmoji()),
                  new SimpleText(ArtifactType.BLINDNESS.getDesc())))
          .append(new NewLineText())
          .append(new NewLineText())
          .append(new SimpleText("Информацию об артефактах можно узнать по команде /help"))),
  VERSION_12_1(
      LocalDate.of(2021, 9, 21),
      new TextBuilder(new BoldText("Больше артефактов!"))
          .append(new NewLineText())
          .append(new NewLineText())
          .append(new SimpleText("Новые виды артефактов:"))
          .append(new NewLineText())
          .append(
              new ParametizedText(
                  "\t\t{0} {1} {2}: {3}",
                  new SimpleText(ArtifactType.RICOCHET.isBonus() ? "\uD83D\uDC4D" : "\uD83D\uDC4E"),
                  new ItalicText(ArtifactType.RICOCHET.getName()),
                  new SimpleText(ArtifactType.RICOCHET.getEmoji()),
                  new SimpleText(ArtifactType.RICOCHET.getDesc())))
          .append(new NewLineText())
          .append(
              new ParametizedText(
                  "\t\t{0} {1} {2}: {3}",
                  new SimpleText(ArtifactType.SILENCE.isBonus() ? "\uD83D\uDC4D" : "\uD83D\uDC4E"),
                  new ItalicText(ArtifactType.SILENCE.getName()),
                  new SimpleText(ArtifactType.SILENCE.getEmoji()),
                  new SimpleText(ArtifactType.SILENCE.getDesc())))
          .append(new NewLineText())
          .append(new NewLineText())
          .append(new SimpleText("Теперь людей с артифактами станет больше!"))
          .append(new NewLineText())
          .append(
              new ParametizedText(
                  new SimpleText(
                      "{0} теперь выпадает каждый день и его может открыть сразу несколько человек!"),
                  new ItalicText("Черный ящик🎁")))
          .append(new NewLineText())
          .append(new NewLineText())
          .append(new SimpleText("Информацию об артефактах можно узнать по команде /help"))),
  VERSION_12(
      LocalDate.of(2021, 9, 20),
      new TextBuilder(new BoldText("Артефакты!"))
          .append(new NewLineText())
          .append(
              new SimpleText(
                  "Теперь можно находить артефакты. Они могут давать бонусы или анти-бонусы"))
          .append(new NewLineText())
          .append(new NewLineText())
          .append(new SimpleText("Первые виды артефактов:"))
          .append(new NewLineText())
          .append(
              new ParametizedText(
                  "\t- {0} {1}: {2}",
                  new ItalicText(ArtifactType.SECOND_CHANCE.getName()),
                  new SimpleText(ArtifactType.SECOND_CHANCE.getEmoji()),
                  new SimpleText(ArtifactType.SECOND_CHANCE.getDesc())))
          .append(new NewLineText())
          .append(
              new ParametizedText(
                  "\t- {0} {1}: {2}",
                  new ItalicText(ArtifactType.PIDOR_MAGNET.getName()),
                  new SimpleText(ArtifactType.PIDOR_MAGNET.getEmoji()),
                  new SimpleText(ArtifactType.PIDOR_MAGNET.getDesc())))
          .append(new NewLineText())
          .append(new NewLineText())
          .append(new SimpleText("Текущие способы получить артефакт:"))
          .append(new NewLineText())
          .append(
              new ParametizedText(
                  new SimpleText(
                      "\t- {0}! Пару раз в неделю бот будет присылать черный ящик. Открыть его сможет лишь один человек."
                          + " В ящике будет либо бонус, либо анти-бонус. Решать тебе, идти на риск или нет"),
                  new ItalicText("Черный ящик🎁")))
          .append(new NewLineText())
          .append(new NewLineText())
          .append(
              new SimpleText(
                  "Артефакты действуют лишь 1 раз на следующий день."
                      + " Наличие артефакта можно проверить по emoji рядом с именем"))),
  VERSION_11(
      LocalDate.of(2021, 8, 14),
      new TextBuilder(new BoldText("- Горячая Картошечка!"))
          .append(new NewLineText())
          .append(
              new SimpleText(
                  "Новая игра - Горячая Картошечка. Лови картошечку и пытайся её перекинуть другому."))
          .append(new NewLineText())
          .append(new SimpleText("Если передержишь горячую картошечку - станешь пидором дня."))
          .append(new NewLineText())
          .append(new SimpleText("Следи за событиями через /schedule"))),
  VERSION_8(
      LocalDate.of(2021, 4, 24),
      new TextBuilder(new SimpleText("- Отзывы/предложения!"))
          .append(new NewLineText())
          .append(
              new SimpleText(
                  "- Теперь вы можете прислать пожелания, предложения, отзывы либо сообщить об ошибке в бота @FeedbackPidorBot"))),
  VERSION_5(
      LocalDate.of(2021, 1, 29),
      new TextBuilder(new SimpleText("- Давай свою оценку!"))
          .append(new NewLineText())
          .append(
              new SimpleText(
                  "- Реагируйте на различные события. Например - на назначение пидора дня"))),
  VERSION_4(
      LocalDate.of(2021, 1, 15),
      new TextBuilder(new SimpleText("- GDPR"))
          .append(new NewLineText())
          .append(new NewLineText())
          .append(
              new SimpleText(
                  "- Если кто-то не активничает в чате больше недели - я исключу его из игры до первой его активности."
                      + " Весь его прогресс сохраниться"))
          .append(new NewLineText())
          .append(new NewLineText())
          .append(new SimpleText("- Отключена новогодняя тема"))
          .append(new NewLineText())
          .append(new NewLineText())
          .append(new SimpleText("- Добавлены настройки бота /" + Command.SETTINGS.getName())),
      StickerType.NEW_YEAR),

  VERSION_3(
      LocalDate.of(2020, 12, 27),
      new TextBuilder(new SimpleText("- Новый год рядом! Новогодняя тема Пидор-Бота"))
          .append(new NewLineText())
          .append(new NewLineText())
          .append(
              new SimpleText("- Теперь всегда видно, кто был последним пидором дня. Ищи петуха:)"))
          .append(new NewLineText())
          .append(new NewLineText())
          .append(new SimpleText("- Повышена точность определения шансов стать пидором года")),
      StickerType.NEW_YEAR),

  VERSION_2(
      LocalDate.of(2020, 12, 20),
      new TextBuilder(
              new ParametizedText(
                  "- День выборов! День отсасина и пидор-викторина были объеденены в одно большое событие - {0}",
                  new BoldText("Выборы Пидора Дня")))
          .append(new NewLineText())
          .append(
              new SimpleText(
                  "Голосуй за потенциального кандидата. Чем больше у кого-либо голосов, тем выше шанс, что он станет пидором дня!"))
          .append(new NewLineText())
          .append(new SimpleText("Следи за событиями через команду /schedule"))
          .append(new NewLineText())
          .append(new NewLineText())
          .append(
              new SimpleText(
                  "- Команда /game была перенесена в команду /pidor. Если сегодня проходит игра, следить за результатами можно будет через команду /pidor"))),

  VERSION_1_10(
      LocalDate.of(2020, 11, 22),
      new TextBuilder(new SimpleText("- Изменен процесс игры Отссассин"))
          .append(new NewLineText())
          .append(new SimpleText("Следи за тем какая будет игра через команду /schedule"))
          .append(new NewLineText())
          .append(new NewLineText())
          .append(new SimpleText("- Добавлена новая команда /reg_pidor"))
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
  private final StickerType sticker;

  Version(LocalDate release, Text description) {
    this.release = release;
    this.description = description;
    this.sticker = StickerType.LOVE;
  }

  Version(LocalDate release, Text description, StickerType sticker) {
    this.release = release;
    this.description = description;
    this.sticker = sticker;
  }

  public StickerType getSticker() {
    return sticker;
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
