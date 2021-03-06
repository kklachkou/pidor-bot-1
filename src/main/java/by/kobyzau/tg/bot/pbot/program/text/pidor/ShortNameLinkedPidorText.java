package by.kobyzau.tg.bot.pbot.program.text.pidor;

import by.kobyzau.tg.bot.pbot.artifacts.ArtifactType;
import by.kobyzau.tg.bot.pbot.model.Pidor;
import by.kobyzau.tg.bot.pbot.model.PidorMark;
import by.kobyzau.tg.bot.pbot.program.text.*;
import by.kobyzau.tg.bot.pbot.util.TGUtil;

import java.util.*;

public class ShortNameLinkedPidorText implements Text {

  private final Pidor pidor;

  public ShortNameLinkedPidorText(Pidor pidor) {
    this.pidor = pidor;
  }

  @Override
  public String text() {
    Text fullName = new TrimmedText(TGUtil.escapeHTML(pidor.getFullName()));
    Text nickname = new TrimmedText(TGUtil.escapeHTML(pidor.getNickname()));
    TextBuilder textBuilder = new TextBuilder();
    if (!nickname.text().isEmpty()) {
      textBuilder.append(nickname);
    } else {
      textBuilder.append(fullName);
    }
    Set<ArtifactType> artifacts =
        Optional.of(pidor).map(Pidor::getArtifacts).orElseGet(Collections::emptySet);
    for (ArtifactType artifact : artifacts) {
      textBuilder.append(new SpaceText()).append(new SimpleText(artifact.getEmoji()));
    }
    List<PidorMark> pidorMarks =
        Optional.of(pidor).map(Pidor::getPidorMarks).orElseGet(Collections::emptyList);
    for (PidorMark pidorMark : pidorMarks) {
      textBuilder.append(new SpaceText()).append(new SimpleText(pidorMark.getEmoji()));
    }
    return new UserLinkText(pidor.getTgId(), textBuilder).text();
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
