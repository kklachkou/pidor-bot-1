package by.kobyzau.tg.bot.pbot.program.backup.v2;

import by.kobyzau.tg.bot.pbot.model.DailyPidor;
import by.kobyzau.tg.bot.pbot.repository.dailypidor.DailyPidorRepository;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.Optional;

@Component
public class DailyPidorBackupProcessorV2 implements BackupProcessorV2 {

  private static final String CHAT_KEY = "C";
  private static final String PIDOR_KEY = "P";
  private static final String DATE_KEY = "D";
  private static final String CALLER_KEY = "CR";
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
    json.put(CHAT_KEY, dailyPidor.getChatId());
    json.put(PIDOR_KEY, dailyPidor.getPlayerTgId());
    json.put(DATE_KEY, dailyPidor.getLocalDate().toString());
    json.put(CALLER_KEY, dailyPidor.getCaller());
    return json;
  }

  @Override
  public void restoreFromBackup(JSONArray jsonArray) {
    int length = jsonArray.length();
    for (int i = 0; i < length; i++) {
      JSONObject pidorData = jsonArray.getJSONObject(i);
      if (!pidorData.has(CHAT_KEY) || !pidorData.has(PIDOR_KEY) || !pidorData.has(DATE_KEY)) {
        continue;
      }
      long chatId = pidorData.getLong(CHAT_KEY);
      long tgId = pidorData.getLong(PIDOR_KEY);
      LocalDate date = LocalDate.parse(pidorData.getString(DATE_KEY));
      Long caller = pidorData.has(CALLER_KEY) ? pidorData.getLong(CALLER_KEY) : null;

      Optional<DailyPidor> dailyPidor = dailyPidorRepository.getByChatAndDate(chatId, date);
      if (!dailyPidor.isPresent()) {
        DailyPidor newDaily = new DailyPidor();
        newDaily.setChatId(chatId);
        newDaily.setCaller(caller);
        newDaily.setPlayerTgId(tgId);
        newDaily.setLocalDate(date);
        dailyPidorRepository.create(newDaily);
      }
    }
  }
}
