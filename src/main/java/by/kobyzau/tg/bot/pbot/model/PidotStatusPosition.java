package by.kobyzau.tg.bot.pbot.model;

import java.util.List;

public class PidotStatusPosition {

  private int numWins;
  private Pidor primaryPidor;
  private List<Pidor> secondaryPidors;

  public int getNumWins() {
    return numWins;
  }

  public void setNumWins(int numWins) {
    this.numWins = numWins;
  }

  public Pidor getPrimaryPidor() {
    return primaryPidor;
  }

  public void setPrimaryPidor(Pidor primaryPidor) {
    this.primaryPidor = primaryPidor;
  }

  public List<Pidor> getSecondaryPidors() {
    return secondaryPidors;
  }

  public void setSecondaryPidors(List<Pidor> secondaryPidors) {
    this.secondaryPidors = secondaryPidors;
  }


}
