package by.kobyzau.tg.bot.pbot.service;

import by.kobyzau.tg.bot.pbot.model.CustomDailyUserData;
import by.kobyzau.tg.bot.pbot.model.dto.FutureActionDto;
import by.kobyzau.tg.bot.pbot.repository.custom.CustomDailyDataRepository;
import by.kobyzau.tg.bot.pbot.service.impl.FutureActionServiceImpl;
import by.kobyzau.tg.bot.pbot.util.StringUtil;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@RunWith(MockitoJUnitRunner.class)
public class FutureActionServiceTest {

  @Mock private CustomDailyDataRepository repository;
  @InjectMocks private FutureActionService service = new FutureActionServiceImpl();

  @Test
  public void getFutureActionData() {
    // given
    FutureActionService.FutureActionType type = FutureActionService.FutureActionType.DEFAULT;
    LocalDate date = LocalDate.of(2020, 10, 11);
    List<CustomDailyUserData> dataList = new ArrayList<>();
    dataList.add(
        getCustomDailyUserData(
            CustomDailyUserData.Type.FUTURE_ACTION,
            date,
            StringUtil.serialize(new FutureActionDto(type, "correct today"))));
    dataList.add(
        getCustomDailyUserData(
            CustomDailyUserData.Type.FUTURE_ACTION,
            date.minusDays(1),
            StringUtil.serialize(new FutureActionDto(type, "correct yesterday"))));

    dataList.add(
        getCustomDailyUserData(
            CustomDailyUserData.Type.FUTURE_ACTION,
            date,
            StringUtil.serialize(
                new FutureActionDto(
                    FutureActionService.FutureActionType.ENABLE_SETTING, "another future type"))));
    dataList.add(
        getCustomDailyUserData(
            CustomDailyUserData.Type.FUTURE_ACTION,
            date.plusDays(1),
            StringUtil.serialize(new FutureActionDto(type, "tomorrow"))));
    dataList.add(
        getCustomDailyUserData(
            CustomDailyUserData.Type.FUTURE_ACTION, date, "Another custom data"));
    dataList.add(
        getCustomDailyUserData(
            CustomDailyUserData.Type.CHAT_CHECKBOX_SETTING,
            date,
            StringUtil.serialize(new FutureActionDto(type, "another custom type"))));
    Mockito.doReturn(dataList).when(repository).getAll();

    // when
    List<String> futureActionData = service.getFutureActionData(type, date);

    // then
    Assert.assertEquals(Arrays.asList("correct today", "correct yesterday"), futureActionData);
  }

  private CustomDailyUserData getCustomDailyUserData(
      CustomDailyUserData.Type type, LocalDate date, String data) {
    CustomDailyUserData dailyUserData = new CustomDailyUserData();
    dailyUserData.setType(type);
    dailyUserData.setLocalDate(date);
    dailyUserData.setData(data);
    return dailyUserData;
  }
}
