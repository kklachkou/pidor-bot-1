package by.kobyzau.tg.bot.pbot.collectors;

import by.kobyzau.tg.bot.pbot.program.text.Text;
import by.kobyzau.tg.bot.pbot.tg.ChatAction;
import by.kobyzau.tg.bot.pbot.tg.action.BotAction;

public interface BotActionCollector {

  void add(BotAction<?> botAction);

  void wait(long chatId, int seconds, ChatAction chatAction);

  void wait(long chatId, ChatAction chatAction);

  void typing(long chatId);

  /** @deprecated Use {@link BotActionCollector#text(long, Text)} instead */
  @Deprecated
  void collectHTMLMessage(long chatId, String message);

  void text(long chatId, Text message);

  void text(long chatId, Text message, Integer replyToMessage);

  void chatAction(long chatId, ChatAction chatAction);

  void sticker(long chatId, String sticker);

  void animation(long chatId, String fileId);

  void photo(long chatId, String photo);
}
