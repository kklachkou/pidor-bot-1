package by.kobyzau.tg.bot.pbot.program.text.pidor;

import by.kobyzau.tg.bot.pbot.model.Pidor;
import by.kobyzau.tg.bot.pbot.model.PidorMark;
import by.kobyzau.tg.bot.pbot.program.text.NotBlankText;
import by.kobyzau.tg.bot.pbot.program.text.Text;
import by.kobyzau.tg.bot.pbot.program.text.TrimmedText;
import by.kobyzau.tg.bot.pbot.util.TGUtil;

import java.util.Optional;

public class FullNamePidorText implements Text {

  private final Pidor pidor;

  public FullNamePidorText(Pidor pidor) {
    this.pidor = pidor;
  }

  @Override
  public String text() {
    Text username = new TrimmedText(TGUtil.escapeHTML(pidor.getUsername()));
    Text fullName = new TrimmedText(TGUtil.escapeHTML(pidor.getFullName()));
    Text nickname = new TrimmedText(TGUtil.escapeHTML(pidor.getNickname()));
    StringBuilder sb = new StringBuilder();
    if (username.isEmpty()) {
      sb.append(new UserLinkText(pidor.getTgId(), fullName));
      if (!nickname.text().isEmpty()) {
        sb.append(" (").append(nickname).append(")");
      }
    } else {
      sb.append("@").append(username);
      sb.append(" (").append(new NotBlankText(nickname, fullName)).append(")");
    }
    if (isPidorOfYear()) {
      sb.append(" \uD83D\uDC51");
    }
    if (isPidorOfDay()) {
      sb.append(" \uD83D\uDC13");
    }
    return sb.toString();
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

  @Override
  public String toString() {
    return text();
  }
}
