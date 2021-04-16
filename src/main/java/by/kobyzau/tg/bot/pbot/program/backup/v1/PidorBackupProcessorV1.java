package by.kobyzau.tg.bot.pbot.program.backup.v1;

import by.kobyzau.tg.bot.pbot.model.Pidor;
import by.kobyzau.tg.bot.pbot.repository.pidor.PidorRepository;
import by.kobyzau.tg.bot.pbot.util.DateUtil;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class PidorBackupProcessorV1 implements BackupProcessorV1 {

  @Autowired private PidorRepository pidorRepository;

  @Override
  public String getType() {
    return "PIDOR";
  }

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
    json.put("STICKER", pidor.getSticker());
    json.put("FULL_NAME", pidor.getFullName());
    json.put(
        "USERNAME_LAST_UPDATED",
        pidor.getUsernameLastUpdated() != null ? pidor.getUsernameLastUpdated().toString() : null);
    return json;
  }

  @Override
  public void restoreFromBackup(JSONArray jsonArray) {
    for (int i = 0; i < jsonArray.length(); i++) {
      JSONObject pidorData = jsonArray.getJSONObject(i);
      if (!pidorData.has("CHAT_ID") || !pidorData.has("TG_ID") || !pidorData.has("FULL_NAME")) {
        continue;
      }
      long chatId = pidorData.getLong("CHAT_ID");
      int tgId = pidorData.getInt("TG_ID");
      String fullName = pidorData.getString("FULL_NAME");
      String nickname = pidorData.has("NICKNAME") ? pidorData.getString("NICKNAME") : null;
      String username = pidorData.has("USERNAME") ? pidorData.getString("USERNAME") : null;
      String sticker = pidorData.has("STICKER") ? pidorData.getString("STICKER") : null;
      Optional<Pidor> pidor = pidorRepository.getByChatAndPlayerTgId(chatId, tgId);
      if (!pidor.isPresent()) {
        Pidor newPidor = new Pidor();
        newPidor.setChatId(chatId);
        newPidor.setTgId(tgId);
        newPidor.setFullName(fullName);
        newPidor.setNickname(nickname);
        newPidor.setUsername(username);
        newPidor.setSticker(sticker);
        newPidor.setUsernameLastUpdated(DateUtil.now());
        pidorRepository.create(newPidor);
      }
    }
  }
}
