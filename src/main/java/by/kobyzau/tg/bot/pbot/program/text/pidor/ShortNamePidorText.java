package by.kobyzau.tg.bot.pbot.program.text.pidor;

import by.kobyzau.tg.bot.pbot.artifacts.ArtifactType;
import by.kobyzau.tg.bot.pbot.model.Pidor;
import by.kobyzau.tg.bot.pbot.model.PidorMark;
import by.kobyzau.tg.bot.pbot.program.text.Text;
import by.kobyzau.tg.bot.pbot.program.text.TrimmedText;
import by.kobyzau.tg.bot.pbot.util.TGUtil;

import java.util.*;

public class ShortNamePidorText implements Text {

  private final Pidor pidor;

  public ShortNamePidorText(Pidor pidor) {
    this.pidor = pidor;
  }

  @Override
  public String text() {
    Text fullName = new TrimmedText(TGUtil.escapeHTML(pidor.getFullName()));
    Text nickname = new TrimmedText(TGUtil.escapeHTML(pidor.getNickname()));
    StringBuilder sb = new StringBuilder();
    sb.append(fullName);
    if (!nickname.text().isEmpty()) {
      sb.append(" (").append(nickname).append(")");
    }
    Set<ArtifactType> artifacts =
        Optional.of(pidor).map(Pidor::getArtifacts).orElseGet(Collections::emptySet);
    for (ArtifactType artifact : artifacts) {
      sb.append(" ").append(artifact.getEmoji());
    }
    List<PidorMark> pidorMarks =
        Optional.of(pidor).map(Pidor::getPidorMarks).orElseGet(Collections::emptyList);
    for (PidorMark pidorMark : pidorMarks) {
      sb.append(" ").append(pidorMark.getEmoji());
    }
    return sb.toString();
  }

  @Override
  public String toString() {
    return text();
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o instanceof Text) {
      return text().equals(((Text) o).text());
    }
    return false;
  }

  @Override
  public int hashCode() {
    return Objects.hash(text());
  }
}
