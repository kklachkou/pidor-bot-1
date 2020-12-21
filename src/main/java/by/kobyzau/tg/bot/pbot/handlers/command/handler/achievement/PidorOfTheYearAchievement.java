package by.kobyzau.tg.bot.pbot.handlers.command.handler.achievement;

import by.kobyzau.tg.bot.pbot.model.Pidor;
import by.kobyzau.tg.bot.pbot.model.PidorOfYear;
import by.kobyzau.tg.bot.pbot.program.selection.Selection;
import by.kobyzau.tg.bot.pbot.program.selection.SimpleSelection;
import by.kobyzau.tg.bot.pbot.program.text.*;
import by.kobyzau.tg.bot.pbot.program.text.pidor.FullNamePidorText;
import by.kobyzau.tg.bot.pbot.repository.pidorofyear.PidorOfYearRepository;
import by.kobyzau.tg.bot.pbot.service.PidorService;
import by.kobyzau.tg.bot.pbot.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.OptionalInt;

@Component
public class PidorOfTheYearAchievement implements Achievement {

  private final PidorService pidorService;

  private final PidorOfYearRepository pidorOfYearRepository;

  @Autowired
  public PidorOfTheYearAchievement(
      PidorService pidorService, PidorOfYearRepository pidorOfYearRepository) {
    this.pidorService = pidorService;
    this.pidorOfYearRepository = pidorOfYearRepository;
  }

  @Override
  public Optional<Text> getAchievementInfo(long chatId) {
    OptionalInt minYear =
        pidorOfYearRepository.getAll().stream()
            .filter(p -> p.getChatId() == chatId)
            .mapToInt(PidorOfYear::getYear)
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
      getPidorOfTheYear(chatId, currentYear)
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

  private Optional<Pidor> getPidorOfTheYear(long chatId, int year) {
    return pidorOfYearRepository.getAll().stream()
        .filter(p -> p.getYear() == year)
        .filter(p -> p.getChatId() == chatId)
        .findFirst()
        .map(PidorOfYear::getPlayerTgId)
        .flatMap(id -> pidorService.getPidor(chatId, id));
  }
}
