package by.kobyzau.tg.bot.pbot.program.backup.v1;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import by.kobyzau.tg.bot.pbot.model.DailyPidor;
import by.kobyzau.tg.bot.pbot.program.backup.DailyPidorBackupProcessor;
import by.kobyzau.tg.bot.pbot.repository.dailypidor.DailyPidorRepository;

@Component
public class DailyPidorBackupProcessorV1 implements DailyPidorBackupProcessor {

  @Autowired private DailyPidorRepository dailyPidorRepository;

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
  public void parseData(JSONArray jsonArray) {}
}
