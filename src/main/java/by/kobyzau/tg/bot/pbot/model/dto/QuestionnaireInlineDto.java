package by.kobyzau.tg.bot.pbot.model.dto;

import by.kobyzau.tg.bot.pbot.model.QuestionnaireType;
import com.fasterxml.jackson.annotation.JsonProperty;

public class QuestionnaireInlineDto extends SerializableInlineObject {

  @JsonProperty("t")
  private QuestionnaireType type;
  @JsonProperty("o")
  private Integer option;

  public QuestionnaireInlineDto() {
    super(SerializableInlineType.QUESTIONNAIRE);
  }

  public QuestionnaireInlineDto(QuestionnaireType type, int option) {
    this();
    this.type = type;
    this.option = option;
  }

  public Integer getOption() {
    return option;
  }

  public void setOption(Integer option) {
    this.option = option;
  }

  public QuestionnaireType getType() {
    return type;
  }

  public void setType(QuestionnaireType type) {
    this.type = type;
  }
}
