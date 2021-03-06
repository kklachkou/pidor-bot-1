package by.kobyzau.tg.bot.pbot.service;

public interface ChatSettingsService {

  String ENABLED_ICON = "✅";
  String DISABLED_ICON = "☑";

  boolean isEnabled(ChatCheckboxSettingType type, long chatId);

  void setEnabled(ChatCheckboxSettingType type, long chatId, boolean enabled);

  boolean willBeChanged(ChatCheckboxSettingType type, long chatId);

  enum ChatCheckboxSettingType {
    AUTO_REGISTER_USERS(
        false,
        "Авто-регистрация Пидоров",
        "Автоматически регистрирует человека при любом его сообщении в чате",
        ENABLED_ICON,
        DISABLED_ICON,
        true),
    ELECTION_HIDDEN(
        false,
        "Анонимные выборы",
        "Если включено - никто не узнает о твоём выборе в день голосования",
        ENABLED_ICON,
        DISABLED_ICON,
        false),
    GAMES_FREQUENT(
        true,
        "Частота игр",
        "Если часто - игры (кубик, дартс, баскетбол, чур не я) будут проходить 2-3 раза в неделю."
            + " Если редко - 1-2 раза в неделю. Настройка будет применена в течении 24х часов.",
        "(часто)",
        "(редко)",
        true),
    ELECTION_FREQUENT(
        true,
        "Частота выборов",
        "если включено - выборы будут занимать каждую неделю. Если выключено - через неделю. Настройка будет применена в течении 24х часов.",
        "(часто)",
        "(редко)",
        true);

    private final boolean changedInFuture;
    private final String name;
    private final String description;
    private final String enabledMark;
    private final String disabledMark;
    private final boolean defaultValue;

    ChatCheckboxSettingType(
        boolean changedInFuture,
        String name,
        String description,
        String enabledMark,
        String disabledMark,
        boolean defaultValue) {
      this.changedInFuture = changedInFuture;
      this.name = name;
      this.description = description;
      this.enabledMark = enabledMark;
      this.disabledMark = disabledMark;
      this.defaultValue = defaultValue;
    }

    public boolean isChangedInFuture() {
      return changedInFuture;
    }

    public String getName() {
      return name;
    }

    public String getEnabledMark() {
      return enabledMark;
    }

    public String getDisabledMark() {
      return disabledMark;
    }

    public String getDescription() {
      return description;
    }

    public boolean getDefaultValue() {
      return defaultValue;
    }
  }
}
