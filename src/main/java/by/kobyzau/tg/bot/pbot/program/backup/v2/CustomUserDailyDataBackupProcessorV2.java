package by.kobyzau.tg.bot.pbot.program.backup.v2;

import by.kobyzau.tg.bot.pbot.model.CustomDailyUserData;
import by.kobyzau.tg.bot.pbot.repository.custom.CustomDailyDataRepository;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class CustomUserDailyDataBackupProcessorV2 implements BackupProcessorV2 {

  private static final String ID_KEY = "ID";
  private static final String CHAT_KEY = "C";
  private static final String DATE_KEY = "D";
  private static final String DATA_KEY = "DD";
  private static final String USER_KEY = "U";
  private static final String TYPE_KEY = "T";

  @Autowired private CustomDailyDataRepository repository;

  @Override
  public String getType() {
    return "CUSTOM_DAILY";
  }

  @Override
  public JSONArray getData() {
    JSONArray allData = new JSONArray();
    repository.getAll().stream().map(this::toJson).forEach(allData::put);
    return allData;
  }

  private JSONObject toJson(CustomDailyUserData data) {
    JSONObject json = new JSONObject();
    json.put(ID_KEY, data.getId());
    json.put(USER_KEY, data.getPlayerTgId());
    json.put(CHAT_KEY, data.getChatId());
    json.put(DATE_KEY, data.getLocalDate().toString());
    json.put(DATA_KEY, data.getData());
    json.put(TYPE_KEY, data.getType());
    return json;
  }

  @Override
  public void restoreFromBackup(JSONArray jsonArray) {
    int length = jsonArray.length();

    for (int i = 0; i < length; i++) {
      JSONObject pidorData = jsonArray.getJSONObject(i);
      if (!pidorData.has(ID_KEY)
          || !pidorData.has(USER_KEY)
          || !pidorData.has(CHAT_KEY)
          || !pidorData.has(DATE_KEY)
          || !pidorData.has(DATA_KEY)
          || !pidorData.has(TYPE_KEY)) {
        continue;
      }
      long id = pidorData.getLong(ID_KEY);
      long chatId = pidorData.getLong(CHAT_KEY);
      long userId = pidorData.getLong(USER_KEY);
      LocalDate date = LocalDate.parse(pidorData.getString(DATE_KEY));
      String data = pidorData.getString(DATA_KEY);
      CustomDailyUserData.Type type =
          CustomDailyUserData.Type.valueOf(pidorData.getString(TYPE_KEY));

      if (repository.get(id) == null) {
        CustomDailyUserData object = new CustomDailyUserData();
        object.setChatId(chatId);
        object.setPlayerTgId(userId);
        object.setLocalDate(date);
        object.setData(data);
        object.setType(type);
        repository.create(object);
      }
    }
  }
}
