package by.kobyzau.tg.bot.pbot.program.backup.v2;

import by.kobyzau.tg.bot.pbot.model.PidorOfYear;
import by.kobyzau.tg.bot.pbot.repository.pidorofyear.PidorOfYearRepository;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class PidorOfYearBackupProcessorV2 implements BackupProcessorV2 {

  private static final String CHAT_KEY = "c";
  private static final String USER_KEY = "u";
  private static final String YEAR_KEY = "y";
  @Autowired private PidorOfYearRepository repository;

  @Override
  public String getType() {
    return "PIDOR_OF_YEAR";
  }

  @Override
  public JSONArray getData() {
    JSONArray allData = new JSONArray();
    repository.getAll().stream().map(this::toJson).forEach(allData::put);
    return allData;
  }

  private JSONObject toJson(PidorOfYear data) {
    JSONObject json = new JSONObject();
    json.put(CHAT_KEY, data.getChatId());
    json.put(USER_KEY, data.getPlayerTgId());
    json.put(YEAR_KEY, String.valueOf(data.getYear()));
    return json;
  }

  @Override
  public void restoreFromBackup(JSONArray jsonArray) {
    int length = jsonArray.length();
    for (int i = 0; i < length; i++) {
      JSONObject pidorData = jsonArray.getJSONObject(i);
      if (!pidorData.has(CHAT_KEY) || !pidorData.has(USER_KEY) || !pidorData.has(YEAR_KEY)) {
        continue;
      }
      long chatId = pidorData.getLong(CHAT_KEY);
      long tgId = pidorData.getLong(USER_KEY);
      int year = pidorData.getInt(YEAR_KEY);

      Optional<PidorOfYear> pidorOfYear =
          repository.getPidorOfYearByChat(chatId).stream()
              .filter(p -> p.getYear() == year)
              .findFirst();
      if (!pidorOfYear.isPresent()) {
        PidorOfYear newPidorOfYear = new PidorOfYear();
        newPidorOfYear.setChatId(chatId);
        newPidorOfYear.setPlayerTgId(tgId);
        newPidorOfYear.setYear(year);
        repository.create(newPidorOfYear);
      }
    }
  }
}
