package by.kobyzau.tg.bot.pbot.service.impl;

import by.kobyzau.tg.bot.pbot.model.CustomDailyUserData;
import by.kobyzau.tg.bot.pbot.model.dto.FutureActionDto;
import by.kobyzau.tg.bot.pbot.repository.custom.CustomDailyDataRepository;
import by.kobyzau.tg.bot.pbot.service.FutureActionService;
import by.kobyzau.tg.bot.pbot.util.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class FutureActionServiceImpl implements FutureActionService {

  private static final int DEFAULT_ID = 0;
  @Autowired private CustomDailyDataRepository repository;

  @Override
  public List<String> getFutureActionData(FutureActionType type, LocalDate date) {
    return repository.getAll().stream()
        .filter(c -> c.getType() == CustomDailyUserData.Type.FUTURE_ACTION)
        .filter(c -> date.isEqual(c.getLocalDate()) || date.isAfter(c.getLocalDate()))
        .map(CustomDailyUserData::getData)
        .map(d -> StringUtil.deserialize(d, FutureActionDto.class))
        .filter(Optional::isPresent)
        .map(Optional::get)
        .filter(f -> f.getType() == type)
        .map(FutureActionDto::getData)
        .collect(Collectors.toList());
  }

  @Override
  public void saveFutureActionData(FutureActionType type, LocalDate date, String data) {
    FutureActionDto futureActionDto = new FutureActionDto();
    futureActionDto.setType(type);
    futureActionDto.setData(data);
    CustomDailyUserData customDailyUserData = new CustomDailyUserData();
    customDailyUserData.setData(StringUtil.serialize(futureActionDto));
    customDailyUserData.setChatId(DEFAULT_ID);
    customDailyUserData.setPlayerTgId(DEFAULT_ID);
    customDailyUserData.setLocalDate(date);
    customDailyUserData.setType(CustomDailyUserData.Type.FUTURE_ACTION);
    repository.create(customDailyUserData);
  }

  @Override
  public void removeFutureData(FutureActionType type, LocalDate date) {
    List<CustomDailyUserData> customUserDataList =
        repository.getAll().stream()
            .filter(c -> c.getType() == CustomDailyUserData.Type.FUTURE_ACTION)
            .filter(c -> date.isEqual(c.getLocalDate()) || date.isAfter(c.getLocalDate()))
            .collect(Collectors.toList());
    for (CustomDailyUserData customUserData : customUserDataList) {
      String data = customUserData.getData();
      Optional<FutureActionDto> futureActionDto =
          StringUtil.deserialize(data, FutureActionDto.class);
      if (futureActionDto.isPresent() && futureActionDto.get().getType() == type) {
        repository.delete(customUserData.getId());
      }
    }
  }
}
