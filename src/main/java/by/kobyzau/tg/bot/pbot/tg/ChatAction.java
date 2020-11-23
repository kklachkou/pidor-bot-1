package by.kobyzau.tg.bot.pbot.tg;

public enum ChatAction {
  TYPING("typing"),
  UPLOAD_PHOTO("upload_photo"),
  UPLOAD_DOC("upload_document");

  private final String action;

  ChatAction(String action) {
    this.action = action;
  }

  public String getAction() {
    return action;
  }
}
