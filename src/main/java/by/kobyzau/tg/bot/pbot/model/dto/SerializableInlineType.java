package by.kobyzau.tg.bot.pbot.model.dto;

public enum SerializableInlineType {
  VOTE(1, 30),
  SETTING_ROOT(2, 20),
  CLOSE_INLINE(3, 20),
  STAT(4, 20),
  FEEDBACK(5, 20),
  HOT_POTATOES(6, 20),
  QUESTIONNAIRE(7, 20)
  ;

  private final int index;
  private final int idSize;

  SerializableInlineType(int index, int idSize) {
    this.index = index;
    this.idSize = idSize;
  }

  public int getIndex() {
    return index;
  }

  public int getIdSize() {
    return idSize;
  }
}
