package by.kobyzau.tg.bot.pbot.program.text.pidor;

import by.kobyzau.tg.bot.pbot.model.Pidor;
import by.kobyzau.tg.bot.pbot.program.text.Text;
import by.kobyzau.tg.bot.pbot.program.text.TrimmedText;
import by.kobyzau.tg.bot.pbot.util.TGUtil;

import java.util.Objects;

public class ShortNameLinkedPidorText implements Text {

    private final Pidor pidor;

    public ShortNameLinkedPidorText(Pidor pidor) {
        this.pidor = pidor;
    }

    @Override
    public String text() {
        Text fullName = new TrimmedText(TGUtil.escapeHTML(pidor.getFullName()));
        Text nickname = new TrimmedText(TGUtil.escapeHTML(pidor.getNickname()));
        if (!nickname.text().isEmpty()) {
            return new UserLinkText(pidor.getTgId(), nickname).text();
        } else {
            return new UserLinkText(pidor.getTgId(), fullName).text();
        }
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
