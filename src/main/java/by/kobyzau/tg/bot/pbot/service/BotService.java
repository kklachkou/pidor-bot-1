package by.kobyzau.tg.bot.pbot.service;

public interface BotService {

    boolean canPinMessage(long chatId);

    boolean isChatValid(long chatId);

    boolean isBotPartOfChat(long chatId);

    void unpinLastBotMessage(long chatId);
}
