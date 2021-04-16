package by.kobyzau.tg.bot.pbot.program.backup;

import java.util.HashMap;
import java.util.Map;

public class BackupProgress {
  private final Map<String, String> statePerType = new HashMap<>();

  public void addState(String type, String state) {
    statePerType.put(type, state);
  }

  public Map<String, String> getStatePerType() {
    return statePerType;
  }
}
