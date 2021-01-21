package by.kobyzau.tg.bot.pbot.model.dto;

import by.kobyzau.tg.bot.pbot.service.ChatSettingsService;
import by.kobyzau.tg.bot.pbot.util.StringUtil;
import org.junit.Test;
import org.mockito.internal.matchers.LessOrEqual;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import static by.kobyzau.tg.bot.pbot.model.dto.SerializableInlineType.SETTING_ROOT;
import static org.junit.Assert.*;

public class CheckboxSettingCommandInlineDtoTest {

  @Test
  public void serizlize_maxLength64() {
    // given

    String id = UUID.randomUUID().toString().substring(SETTING_ROOT.getIdSize());
    List<CheckboxSettingCommandInlineDto> dtoList =
        Arrays.stream(ChatSettingsService.ChatCheckboxSettingType.values())
            .map(c -> new CheckboxSettingCommandInlineDto(id, c))
            .collect(Collectors.toList());

    // when
    List<String> serialized =
        dtoList.stream().map(StringUtil::serialize).collect(Collectors.toList());

    // then
    for (String s : serialized) {
      assertThat("JSON: \n" + s, s.getBytes().length, new LessOrEqual<>(64));
    }
  }

  @Test
  public void deserialize_eq() {
    // given
    String id = UUID.randomUUID().toString().substring(SETTING_ROOT.getIdSize());
    CheckboxSettingCommandInlineDto dto =
        new CheckboxSettingCommandInlineDto(id, ChatSettingsService.ChatCheckboxSettingType.GAMES_FREQUENT);

    // when
    String json = StringUtil.serialize(dto);
    Optional<CheckboxSettingCommandInlineDto> deserialized =
        StringUtil.deserialize(json, CheckboxSettingCommandInlineDto.class);

    // then
    assertTrue(deserialized.isPresent());
    assertEquals(dto, deserialized.get());
    assertEquals(SETTING_ROOT.getIndex(), deserialized.get().getIndex());
  }
}
