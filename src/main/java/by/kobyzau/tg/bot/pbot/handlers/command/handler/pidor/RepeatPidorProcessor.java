package by.kobyzau.tg.bot.pbot.handlers.command.handler.pidor;

import by.kobyzau.tg.bot.pbot.handlers.command.handler.pidor.repeat.RepeatFunnyAction;
import by.kobyzau.tg.bot.pbot.model.DailyPidor;
import by.kobyzau.tg.bot.pbot.model.Pidor;
import by.kobyzau.tg.bot.pbot.repository.dailypidor.DailyPidorRepository;
import by.kobyzau.tg.bot.pbot.util.CollectionUtil;
import by.kobyzau.tg.bot.pbot.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Component
public class RepeatPidorProcessor {

  @Autowired private DailyPidorRepository dailyPidorRepository;

  @Autowired private List<RepeatFunnyAction> repeatFunnyActions;

  public void checkPidorRepeat(Pidor pidorOfTheDay) {
    long chatId = pidorOfTheDay.getChatId();
    LocalDate date = DateUtil.now();
    int numWins = 1;
    for (int i = 0; i < 10; i++) {
      date = date.minusDays(1);
      Optional<DailyPidor> dailyPidor = dailyPidorRepository.getByChatAndDate(chatId, date);
      if (!dailyPidor.isPresent()) {
        return;
      }
      if (dailyPidor.get().getPlayerTgId() == pidorOfTheDay.getTgId()) {
        numWins++;
      } else {
        break;
      }
    }
    if (numWins >= 2) {
      CollectionUtil.getRandomValue(repeatFunnyActions)
          .processFunnyAction(chatId, numWins, pidorOfTheDay);
    }
  }
}
