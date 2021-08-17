package by.kobyzau.tg.bot.pbot.program.backup.v2;

import by.kobyzau.tg.bot.pbot.model.PidorYearlyStat;
import by.kobyzau.tg.bot.pbot.repository.yearlystat.PidorYearlyStatRepository;
import java.time.LocalDate;
import java.util.Optional;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class PidorYearlyStatBackupProcessorV2 implements BackupProcessorV2 {

  private static final String ID_KEY = "ID";
  private static final String CHAT_KEY = "C";
  private static final String YEAR_KEY = "Y";
  private static final String USER_KEY = "U";
  private static final String COUNT_KEY = "CC";
  private static final String LAST_DATE_KEY = "L";

  @Autowired private PidorYearlyStatRepository repository;

  @Override
  public String getType() {
    return "PIDOR_YEARLY";
  }

  @Override
  public JSONArray getData() {
    JSONArray allData = new JSONArray();
    repository.getAll().stream().map(this::toJson).forEach(allData::put);
    return allData;
  }

  private JSONObject toJson(PidorYearlyStat stat) {
    JSONObject json = new JSONObject();
    json.put(ID_KEY, stat.getId());
    json.put(CHAT_KEY, stat.getChatId());
    json.put(USER_KEY, stat.getPlayerTgId());
    json.put(YEAR_KEY, stat.getYear());
    json.put(COUNT_KEY, stat.getCount());
    json.put(LAST_DATE_KEY, stat.getLastDate().toString());
    return json;
  }

  @Override
  public void restoreFromBackup(JSONArray jsonArray) {
    int length = jsonArray.length();
    for (int i = 0; i < length; i++) {
      JSONObject pidorData = jsonArray.getJSONObject(i);
      if (!pidorData.has(ID_KEY)
          || !pidorData.has(CHAT_KEY)
          || !pidorData.has(USER_KEY)
          || !pidorData.has(YEAR_KEY)
          || !pidorData.has(COUNT_KEY)
          || !pidorData.has(LAST_DATE_KEY)) {
        continue;
      }
      long chatId = pidorData.getLong(CHAT_KEY);
      long userId = pidorData.getLong(USER_KEY);
      int year = pidorData.getInt(YEAR_KEY);
      int count = pidorData.getInt(COUNT_KEY);
      LocalDate date = LocalDate.parse(pidorData.getString(LAST_DATE_KEY));

      Optional<PidorYearlyStat> existed =
          repository.getByChatId(chatId).stream()
              .filter(c -> c.getPlayerTgId() == userId)
              .filter(c -> c.getYear() == year)
              .findFirst();
      if (!existed.isPresent()) {
        PidorYearlyStat newStat = new PidorYearlyStat();
        newStat.setChatId(chatId);
        newStat.setPlayerTgId(userId);
        newStat.setYear(year);
        newStat.setCount(count);
        newStat.setLastDate(date);
        repository.create(newStat);
      }
    }
  }
}
