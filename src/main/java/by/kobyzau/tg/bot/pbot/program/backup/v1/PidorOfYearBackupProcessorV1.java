package by.kobyzau.tg.bot.pbot.program.backup.v1;

import by.kobyzau.tg.bot.pbot.model.PidorOfYear;
import by.kobyzau.tg.bot.pbot.repository.pidorofyear.PidorOfYearRepository;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class PidorOfYearBackupProcessorV1 implements BackupProcessorV1 {

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
    json.put("CHAT_ID", data.getChatId());
    json.put("USER_ID", data.getPlayerTgId());
    json.put("YEAR", String.valueOf(data.getYear()));
    return json;
  }

  @Override
  public void restoreFromBackup(JSONArray jsonArray) {
    for (int i = 0; i < jsonArray.length(); i++) {
      JSONObject pidorData = jsonArray.getJSONObject(i);
      if (!pidorData.has("CHAT_ID") || !pidorData.has("USER_ID") || !pidorData.has("YEAR")) {
        continue;
      }
      long chatId = pidorData.getLong("CHAT_ID");
      int tgId = pidorData.getInt("USER_ID");
      int year = pidorData.getInt("YEAR");

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
