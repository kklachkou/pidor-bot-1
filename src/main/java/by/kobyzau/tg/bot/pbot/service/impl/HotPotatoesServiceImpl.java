package by.kobyzau.tg.bot.pbot.service.impl;

import by.kobyzau.tg.bot.pbot.handlers.update.schedule.CalendarSchedule;
import by.kobyzau.tg.bot.pbot.handlers.update.schedule.ScheduledItem;
import by.kobyzau.tg.bot.pbot.model.HotPotatoTaker;
import by.kobyzau.tg.bot.pbot.model.Pidor;
import by.kobyzau.tg.bot.pbot.repository.potato.PotatoTakerRepository;
import by.kobyzau.tg.bot.pbot.service.HotPotatoesService;
import by.kobyzau.tg.bot.pbot.service.PidorService;
import by.kobyzau.tg.bot.pbot.util.DateUtil;
import by.kobyzau.tg.bot.pbot.util.HotPotatoUtil;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class HotPotatoesServiceImpl implements HotPotatoesService {
  @Autowired private PotatoTakerRepository potatoTakerRepository;
  @Autowired private PidorService pidorService;

  @Autowired private CalendarSchedule calendarSchedule;
  @Autowired private HotPotatoUtil hotPotatoUtil;

  @Override
  public boolean isHotPotatoesDay(long chatId, LocalDate localDate) {
    return calendarSchedule.getItem(chatId, localDate) == ScheduledItem.POTATOES;
  }

  @Override
  public Optional<Pidor> getLastTaker(LocalDate localDate, long chatId) {
    return potatoTakerRepository.getByChatAndDate(chatId, localDate).stream()
        .max(Comparator.comparing(HotPotatoTaker::getDeadline).thenComparing(HotPotatoTaker::getId))
        .flatMap(t -> pidorService.getPidor(t.getChatId(), t.getPlayerTgId()));
  }

  @Override
  public Optional<LocalDateTime> getLastTakerDeadline(LocalDate localDate, long chatId) {
    return potatoTakerRepository.getByChatAndDate(chatId, localDate).stream()
        .max(Comparator.comparing(HotPotatoTaker::getDeadline).thenComparing(HotPotatoTaker::getId))
        .map(HotPotatoTaker::getDeadline);
  }

  @Override
  public LocalDateTime setNewTaker(Pidor pidor) {
    LocalDateTime deadline = hotPotatoUtil.getDeadline(DateUtil.currentTime());
    HotPotatoTaker potatoTaker = new HotPotatoTaker();
    potatoTaker.setChatId(pidor.getChatId());
    potatoTaker.setDate(DateUtil.now());
    potatoTaker.setPlayerTgId(pidor.getTgId());
    potatoTaker.setDeadline(deadline);
    potatoTakerRepository.create(potatoTaker);
    return deadline;
  }
}
