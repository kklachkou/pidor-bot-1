package by.kobyzau.tg.bot.pbot.program.text.pidor;

import by.kobyzau.tg.bot.pbot.model.Pidor;
import by.kobyzau.tg.bot.pbot.model.PidorMark;
import by.kobyzau.tg.bot.pbot.program.text.SimpleText;
import by.kobyzau.tg.bot.pbot.program.text.Text;
import by.kobyzau.tg.bot.pbot.program.text.TextBuilder;
import by.kobyzau.tg.bot.pbot.program.text.TrimmedText;
import by.kobyzau.tg.bot.pbot.util.TGUtil;

import java.util.Objects;
import java.util.Optional;

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
    if (isPidorOfYear()) {
      textBuilder.append(new SimpleText(" \uD83D\uDC51"));
    }
    if (isPidorOfDay()) {
      textBuilder.append(new SimpleText(" \uD83D\uDC13"));
    }
    if (hasCovid()) {
      textBuilder.append(new SimpleText(" \uD83E\uDDA0"));
    }
    return new UserLinkText(pidor.getTgId(), textBuilder).text();
  }

  private boolean isPidorOfYear() {
    return Optional.ofNullable(pidor)
            .map(Pidor::getPidorMarks)
            .filter(m -> m.contains(PidorMark.PIDOR_OF_YEAR))
            .isPresent();
  }

  private boolean isPidorOfDay() {
    return Optional.ofNullable(pidor)
            .map(Pidor::getPidorMarks)
            .filter(m -> m.contains(PidorMark.LAST_PIDOR_OF_DAY))
            .isPresent();
  }

  private boolean hasCovid() {
    return Optional.ofNullable(pidor)
            .map(Pidor::getPidorMarks)
            .filter(m -> m.contains(PidorMark.COVID))
            .isPresent();
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
