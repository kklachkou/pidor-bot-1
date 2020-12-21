package by.kobyzau.tg.bot.pbot.program.backup.v1;

import by.kobyzau.tg.bot.pbot.model.PidorOfYear;
import by.kobyzau.tg.bot.pbot.program.backup.PidorOfYearBackupProcessor;
import by.kobyzau.tg.bot.pbot.repository.pidorofyear.PidorOfYearRepository;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class PidorOfYearBackupProcessorV1 implements PidorOfYearBackupProcessor {


    @Autowired
    private PidorOfYearRepository repository;

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
    public void parseData(JSONArray jsonArray) {

    }
}
