package by.kobyzau.tg.bot.pbot.program.backup.v1;

import by.kobyzau.tg.bot.pbot.model.DailyPidor;
import by.kobyzau.tg.bot.pbot.repository.dailypidor.DailyPidorRepository;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.Optional;

@Component
public class DailyPidorBackupProcessorV1 implements BackupProcessorV1 {

  @Autowired private DailyPidorRepository dailyPidorRepository;

  @Override
  public String getType() {
    return "DAILY_PIDOR";
  }

  @Override
  public JSONArray getData() {
    JSONArray allData = new JSONArray();
    dailyPidorRepository.getAll().stream().map(this::toJson).forEach(allData::put);

    return allData;
  }

  private JSONObject toJson(DailyPidor dailyPidor) {
    JSONObject json = new JSONObject();
    json.put("CHAT_ID", dailyPidor.getChatId());
    json.put("PIDOR_TG_ID", dailyPidor.getPlayerTgId());
    json.put("LOCAL_DATE", dailyPidor.getLocalDate().toString());
    return json;
  }

  @Override
  public void restoreFromBackup(JSONArray jsonArray) {
    for (int i = 0; i < jsonArray.length(); i++) {
      JSONObject pidorData = jsonArray.getJSONObject(i);
      if (!pidorData.has("CHAT_ID")
          || !pidorData.has("PIDOR_TG_ID")
          || !pidorData.has("LOCAL_DATE")) {
        continue;
      }
      long chatId = pidorData.getLong("CHAT_ID");
      int tgId = pidorData.getInt("PIDOR_TG_ID");
      LocalDate date = LocalDate.parse(pidorData.getString("LOCAL_DATE"));

      Optional<DailyPidor> dailyPidor = dailyPidorRepository.getByChatAndDate(chatId, date);
      if (!dailyPidor.isPresent()) {
        DailyPidor newDaily = new DailyPidor();
        newDaily.setChatId(chatId);
        newDaily.setPlayerTgId(tgId);
        newDaily.setLocalDate(date);
        dailyPidorRepository.create(newDaily);
      }
    }
  }
}
