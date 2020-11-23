package by.kobyzau.tg.bot.pbot.program.backup.v1;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import by.kobyzau.tg.bot.pbot.model.Pidor;
import by.kobyzau.tg.bot.pbot.program.backup.PidorBackupProcessor;
import by.kobyzau.tg.bot.pbot.repository.pidor.PidorRepository;

@Component
public class PidorBackupProcessorV1 implements PidorBackupProcessor {

  @Autowired private PidorRepository pidorRepository;

  @Override
  public JSONArray getData() {
    JSONArray allData = new JSONArray();
    pidorRepository.getAll().stream().map(this::toJson).forEach(allData::put);

    return allData;
  }

  private JSONObject toJson(Pidor pidor) {
    JSONObject json = new JSONObject();
    json.put("CHAT_ID", pidor.getChatId());
    json.put("TG_ID", pidor.getTgId());
    json.put("NICKNAME", pidor.getNickname());
    json.put("USERNAME", pidor.getUsername());
    json.put("FULL_NAME", pidor.getFullName());
    json.put(
        "USERNAME_LAST_UPDATED",
        pidor.getUsernameLastUpdated() != null ? pidor.getUsernameLastUpdated().toString() : null);
    return json;
  }

  @Override
  public void parseData(JSONArray jsonArray) {}
}
