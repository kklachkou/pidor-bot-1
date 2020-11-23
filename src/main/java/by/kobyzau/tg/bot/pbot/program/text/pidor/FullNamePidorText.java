package by.kobyzau.tg.bot.pbot.program.text.pidor;

import by.kobyzau.tg.bot.pbot.model.Pidor;
import by.kobyzau.tg.bot.pbot.program.text.NotBlankText;
import by.kobyzau.tg.bot.pbot.program.text.Text;
import by.kobyzau.tg.bot.pbot.program.text.TrimmedText;
import by.kobyzau.tg.bot.pbot.util.TGUtil;

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
    return sb.toString();
  }

  @Override
  public String toString() {
    return text();
  }
}
