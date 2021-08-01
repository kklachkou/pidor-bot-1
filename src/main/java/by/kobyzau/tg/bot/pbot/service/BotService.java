package by.kobyzau.tg.bot.pbot.service;

import org.telegram.telegrambots.meta.api.objects.Chat;

public interface BotService {

    boolean canPinMessage(long chatId);

    boolean canDeleteMessage(long chatId);

    boolean isChatValid(long chatId);

    boolean isChatValid(Chat chat);

    boolean isBotPartOfChat(long chatId);

    void unpinLastBotMessage(long chatId);
}
