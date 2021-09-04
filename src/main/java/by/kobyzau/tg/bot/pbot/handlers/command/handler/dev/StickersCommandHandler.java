package by.kobyzau.tg.bot.pbot.handlers.command.handler.dev;

import by.kobyzau.tg.bot.pbot.collectors.BotActionCollector;
import by.kobyzau.tg.bot.pbot.handlers.command.Command;
import by.kobyzau.tg.bot.pbot.handlers.command.handler.CommandHandler;
import by.kobyzau.tg.bot.pbot.program.text.SimpleText;
import by.kobyzau.tg.bot.pbot.tg.action.SendStickerBotAction;
import by.kobyzau.tg.bot.pbot.tg.sticker.StickerType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.stickers.Sticker;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class StickersCommandHandler implements CommandHandler {

  private final BotActionCollector botActionCollector;

  @Override
  public void processCommand(Message message, String text) {
    long chatId = message.getChatId();
    Message replyToMessage = message.getReplyToMessage();
    if (replyToMessage != null && replyToMessage.hasSticker()) {
      Sticker sticker = replyToMessage.getSticker();
      botActionCollector.text(
          chatId, new SimpleText(sticker.getFileId()), replyToMessage.getMessageId());
      return;
    }
    Optional<StickerType> sticker = StickerType.parseSticker(text);
    if (sticker.isPresent()) {
      for (String stickerId : sticker.get().getStickers()) {
        botActionCollector.text(chatId, new SimpleText("Sticker\uD83D\uDC47 " + stickerId));
        botActionCollector.add(new SendStickerBotAction(chatId, stickerId));
      }
    } else {
      for (StickerType stickerType : StickerType.values()) {
        botActionCollector.text(chatId, new SimpleText("Sticker Type " + stickerType));
      }
    }
  }

  @Override
  public Command getCommand() {
    return Command.STICKERS;
  }
}
