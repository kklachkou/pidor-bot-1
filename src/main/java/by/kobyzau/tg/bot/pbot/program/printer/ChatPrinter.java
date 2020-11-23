package by.kobyzau.tg.bot.pbot.program.printer;

import by.kobyzau.tg.bot.pbot.util.StringUtil;
import by.kobyzau.tg.bot.pbot.util.TGUtil;
import org.telegram.telegrambots.meta.api.objects.Chat;

public class ChatPrinter {

    private final Chat chat;
    private final int level;

    public ChatPrinter(Chat chat) {
        this(chat, 0);
    }

    public ChatPrinter(Chat chat, int level) {
        this.chat = chat;
        this.level = level;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        String firstName = TGUtil.escapeHTML(chat.getFirstName());
        Long id = chat.getId();
        String userName = TGUtil.escapeHTML(chat.getUserName());
        String title = TGUtil.escapeHTML(chat.getTitle());
        final int newLevel = level +2;
        sb.append(StringUtil.repeat("\t", level)).append("<i>Chat</i>:\n");
        if (id != null) {
            sb.append(StringUtil.repeat("\t", newLevel)).append("<i>id:</i> ");
            sb.append(id);
            sb.append("\n");
        }
        if (StringUtil.isNotBlank(firstName)) {
            sb.append(StringUtil.repeat("\t", newLevel)).append("<i>firstName:</i> ");
            sb.append(firstName);
            sb.append("\n");
        }
        if (StringUtil.isNotBlank(userName)) {
            sb.append(StringUtil.repeat("\t", newLevel)).append("<i>userName:</i> ");
            sb.append(userName);
            sb.append("\n");
        }
        if (StringUtil.isNotBlank(title)) {
            sb.append(StringUtil.repeat("\t", newLevel)).append("<i>title:</i> ");
            sb.append(title);
            sb.append("\n");
        }
        return sb.toString();
    }
}
