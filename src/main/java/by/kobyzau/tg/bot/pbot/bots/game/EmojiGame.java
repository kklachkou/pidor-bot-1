package by.kobyzau.tg.bot.pbot.bots.game;

import java.time.LocalDate;

public interface EmojiGame {

  String getEmoji();

  EmojiGameType getType();

  boolean isDateToGame(LocalDate date);

  EmojiGameResult getResult(long chatId, int userValue);

  void printIntro(long chatId);
}
