package by.kobyzau.tg.bot.pbot.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AppVersionDto {
  private String number;
  public String name;
  private String desc;
}
