package by.kobyzau.tg.bot.pbot.service.impl;

import by.kobyzau.tg.bot.pbot.model.Pidor;
import by.kobyzau.tg.bot.pbot.model.PidorOfYear;
import by.kobyzau.tg.bot.pbot.repository.pidor.PidorRepository;
import by.kobyzau.tg.bot.pbot.repository.pidorofyear.PidorOfYearRepository;
import by.kobyzau.tg.bot.pbot.service.PidorService;
import by.kobyzau.tg.bot.pbot.service.TelegramService;
import by.kobyzau.tg.bot.pbot.util.DateUtil;
import by.kobyzau.tg.bot.pbot.util.TGUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.User;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class PidorServiceImpl implements PidorService {

  @Autowired private PidorRepository pidorRepository;
  @Autowired private PidorOfYearRepository pidorOfYearRepository;

  @Autowired private TelegramService telegramService;

  @Override
  public Optional<Pidor> getPidor(long chatId, int userId) {
    return pidorRepository
        .getByChatAndPlayerTgId(chatId, userId)
        .filter(p -> TGUtil.isChatMember(telegramService.getChatMember(chatId, p.getTgId())))
        .map(this::setPidorOfLastYearMark);
  }

  @Override
  public Pidor createPidor(long chatId, User user) {
    Pidor pidor = new Pidor();
    pidor.setTgId(user.getId());
    pidor.setChatId(chatId);
    pidor.setUsername(TGUtil.getUsername(user));
    pidor.setUsernameLastUpdated(DateUtil.now());
    pidor.setFullName(TGUtil.getFullName(user));
    long id = pidorRepository.create(pidor);
    return pidorRepository.get(id);
  }

  @Override
  public void updatePidor(Pidor pidor) {
    pidorRepository.update(pidor);
  }

  @Override
  public List<Pidor> getByChat(long chatId) {
    return pidorRepository.getByChat(chatId).stream()
        .filter(p -> TGUtil.isChatMember(telegramService.getChatMember(chatId, p.getTgId())))
        .sorted(Comparator.comparing(Pidor::getFullName))
        .map(this::setPidorOfLastYearMark)
        .collect(Collectors.toList());
  }

  @Override
  public Optional<Pidor> getPidorOfLastYear(long chatId) {
    int year = DateUtil.now().getYear() - 1;
    return pidorOfYearRepository.getPidorOfYearByChat(chatId).stream()
        .filter(p -> p.getYear() == year)
        .findFirst()
        .map(PidorOfYear::getPlayerTgId)
        .flatMap(id -> getPidor(chatId, id));
  }

  private Pidor setPidorOfLastYearMark(Pidor pidor) {
    if (isPidorOfLastYear(pidor)) {
      pidor.setPidorOfYear(true);
    }
    return pidor;
  }

  private boolean isPidorOfLastYear(Pidor pidor) {
    if (pidor == null) {
      return false;
    }
    int year = DateUtil.now().getYear() - 1;
    return pidorOfYearRepository.getPidorOfYearByChat(pidor.getChatId()).stream()
        .filter(p -> p.getYear() == year)
        .findFirst()
        .map(PidorOfYear::getPlayerTgId)
        .filter(id -> id == pidor.getTgId())
        .isPresent();
  }
}
