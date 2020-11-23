package by.kobyzau.tg.bot.pbot.handlers.command.handler.achievement;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import by.kobyzau.tg.bot.pbot.model.DailyPidor;
import by.kobyzau.tg.bot.pbot.model.Pidor;
import by.kobyzau.tg.bot.pbot.program.selection.Selection;
import by.kobyzau.tg.bot.pbot.program.selection.SimpleSelection;
import by.kobyzau.tg.bot.pbot.program.text.NewLineText;
import by.kobyzau.tg.bot.pbot.program.text.ParametizedText;
import by.kobyzau.tg.bot.pbot.program.text.SimpleText;
import by.kobyzau.tg.bot.pbot.program.text.Text;
import by.kobyzau.tg.bot.pbot.program.text.TextBuilder;
import by.kobyzau.tg.bot.pbot.program.text.pidor.FullNamePidorText;
import by.kobyzau.tg.bot.pbot.repository.dailypidor.DailyPidorRepository;
import by.kobyzau.tg.bot.pbot.service.PidorService;
import by.kobyzau.tg.bot.pbot.util.DateUtil;
import by.kobyzau.tg.bot.pbot.util.PidorUtil;

@Component
public class PidorOfTheYearAchievement implements Achievement {

  private final PidorService pidorService;

  private final DailyPidorRepository dailyPidorRepository;

  @Autowired
  public PidorOfTheYearAchievement(
      PidorService pidorService, DailyPidorRepository dailyPidorRepository) {
    this.pidorService = pidorService;
    this.dailyPidorRepository = dailyPidorRepository;
  }

  @Override
  public Optional<Text> getAchievementInfo(long chatId) {
    List<DailyPidor> dailyPidors = dailyPidorRepository.getByChat(chatId);
    OptionalInt minYear =
        dailyPidors.stream()
            .map(DailyPidor::getLocalDate)
            .filter(Objects::nonNull)
            .mapToInt(LocalDate::getYear)
            .min();
    if (!minYear.isPresent()) {
      return Optional.empty();
    }
    int yearFrom = minYear.getAsInt();
    int yearTo = DateUtil.now().getYear() - 1;
    if (yearFrom > yearTo) {
      return Optional.empty();
    }
    Text firstPlace = new SimpleText("\uD83E\uDD47 ");
    Text secondPlace = new SimpleText("\uD83E\uDD48 ");
    Text thirdPlace = new SimpleText("\uD83E\uDD49 ");
    TextBuilder text = new TextBuilder();
    Selection<Optional<Text>> places =
        new SimpleSelection<>(firstPlace, secondPlace, thirdPlace).map(Optional::ofNullable);

    text.append(new SimpleText("Были пидором года:")).append(new NewLineText());
    while (yearFrom <= yearTo) {
      final int currentYear = yearTo;
      getPidorOfTheYear(chatId, currentYear, dailyPidors)
          .ifPresent(
              p ->
                  text.append(new NewLineText())
                      .append(places.next().orElse(null))
                      .append(
                          new ParametizedText(
                              "{0} - Пидор {1} года",
                              new FullNamePidorText(p),
                              new SimpleText(String.valueOf(currentYear)))));
      yearTo--;
    }

    return Optional.of(text);
  }

  private Optional<Pidor> getPidorOfTheYear(long chatId, int year, List<DailyPidor> dailyPidors) {
    List<DailyPidor> dailyOfTheYear =
        dailyPidors.stream()
            .filter(dp -> dp.getLocalDate() != null)
            .filter(dp -> dp.getLocalDate().getYear() == year)
            .collect(Collectors.toList());
    return PidorUtil.getTopPidorTgId(dailyOfTheYear)
        .flatMap(id -> pidorService.getPidor(chatId, id));
  }
}
