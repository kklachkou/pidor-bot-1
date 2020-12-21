package by.kobyzau.tg.bot.pbot.service.impl;

import by.kobyzau.tg.bot.pbot.collectors.BotActionCollector;
import by.kobyzau.tg.bot.pbot.handlers.update.schedule.CalendarSchedule;
import by.kobyzau.tg.bot.pbot.handlers.update.schedule.ScheduledItem;
import by.kobyzau.tg.bot.pbot.model.CustomDailyUserData;
import by.kobyzau.tg.bot.pbot.repository.custom.CustomDailyDataRepository;
import by.kobyzau.tg.bot.pbot.service.ElectionService;
import by.kobyzau.tg.bot.pbot.service.PidorService;
import by.kobyzau.tg.bot.pbot.util.DateUtil;
import by.kobyzau.tg.bot.pbot.util.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class ElectionServiceImpl implements ElectionService {

  @Autowired private CalendarSchedule calendarSchedule;
  @Autowired private CustomDailyDataRepository dailyDataRepository;

  @Autowired private PidorService pidorService;
  @Autowired private BotActionCollector botActionCollector;

  @Override
  public boolean isElectionDay(LocalDate localDate) {
    return calendarSchedule.getItem(localDate) == ScheduledItem.ELECTION;
  }

  @Override
  public boolean canUserVote(long chatId, int userId) {
    return dailyDataRepository.getByChatAndDate(chatId, DateUtil.now()).stream()
        .noneMatch(p -> p.getPlayerTgId() == userId);
  }

  @Override
  public int getNumToVote(long chatId) {
    int numPidors = pidorService.getByChat(chatId).size();
    if (numPidors <= 5) {
      return numPidors;
    }
    return (int) (numPidors * 0.8);
  }

  @Override
  public int getNumVotes(long chatId, LocalDate date) {
    return dailyDataRepository.getByChatAndDate(chatId, date).size();
  }

  @Override
  public int getNumVotes(long chatId, LocalDate date, int userId) {
    return (int)
        dailyDataRepository.getByChatAndDate(chatId, date).stream()
            .map(CustomDailyUserData::getData)
            .map(id -> StringUtil.parseInt(id, 0))
            .filter(id -> id == userId)
            .count();
  }

  @Override
  public void saveVote(long chatId, int calledUserId, int targetUserId) {
    CustomDailyUserData data = new CustomDailyUserData();
    data.setPlayerTgId(calledUserId);
    data.setChatId(chatId);
    data.setLocalDate(DateUtil.now());
    data.setType(CustomDailyUserData.Type.ELECTION_VOTE);
    data.setData(String.valueOf(targetUserId));
    dailyDataRepository.create(data);
  }
}
