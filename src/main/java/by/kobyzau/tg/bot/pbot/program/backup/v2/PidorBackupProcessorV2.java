package by.kobyzau.tg.bot.pbot.program.backup.v2;

import by.kobyzau.tg.bot.pbot.model.Pidor;
import by.kobyzau.tg.bot.pbot.repository.pidor.PidorRepository;
import by.kobyzau.tg.bot.pbot.util.DateUtil;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class PidorBackupProcessorV2 implements BackupProcessorV2 {

  private static final String CHAT_KEY = "C";
  private static final String TG_KEY = "T";
  private static final String NICKNAME_KEY = "N";
  private static final String USERNAME_KEY = "U";
  private static final String STICKER_KEY = "S";
  private static final String FULL_NAME_KEY = "F";
  private static final String UPDATED_KEY = "UU";
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
    json.put(CHAT_KEY, pidor.getChatId());
    json.put(TG_KEY, pidor.getTgId());
    json.put(NICKNAME_KEY, pidor.getNickname());
    json.put(USERNAME_KEY, pidor.getUsername());
    json.put(STICKER_KEY, pidor.getSticker());
    json.put(FULL_NAME_KEY, pidor.getFullName());
    json.put(
        UPDATED_KEY,
        pidor.getUsernameLastUpdated() != null ? pidor.getUsernameLastUpdated().toString() : null);
    return json;
  }

  @Override
  public void restoreFromBackup(JSONArray jsonArray) {
    int length = jsonArray.length();

    for (int i = 0; i < length; i++) {
      JSONObject pidorData = jsonArray.getJSONObject(i);
      if (!pidorData.has(CHAT_KEY) || !pidorData.has(TG_KEY) || !pidorData.has(FULL_NAME_KEY)) {
        continue;
      }
      long chatId = pidorData.getLong(CHAT_KEY);
      long tgId = pidorData.getLong(TG_KEY);
      String fullName = pidorData.getString(FULL_NAME_KEY);
      String nickname = pidorData.has(NICKNAME_KEY) ? pidorData.getString(NICKNAME_KEY) : null;
      String username = pidorData.has(USERNAME_KEY) ? pidorData.getString(USERNAME_KEY) : null;
      String sticker = pidorData.has(STICKER_KEY) ? pidorData.getString(STICKER_KEY) : null;
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
