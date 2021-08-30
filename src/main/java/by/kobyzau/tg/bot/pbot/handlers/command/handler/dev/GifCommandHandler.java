package by.kobyzau.tg.bot.pbot.handlers.command.handler.dev;

import by.kobyzau.tg.bot.pbot.bots.PidorBot;
import by.kobyzau.tg.bot.pbot.collectors.BotActionCollector;
import by.kobyzau.tg.bot.pbot.handlers.command.Command;
import by.kobyzau.tg.bot.pbot.handlers.command.handler.CommandHandler;
import by.kobyzau.tg.bot.pbot.program.text.SimpleText;
import by.kobyzau.tg.bot.pbot.tg.action.SendAnimationBotAction;
import by.kobyzau.tg.bot.pbot.tg.sticker.GifFile;
import by.kobyzau.tg.bot.pbot.util.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.games.Animation;

import java.util.Optional;

@Component
public class GifCommandHandler implements CommandHandler {

  @Autowired private PidorBot pidorBot;

  @Autowired private BotActionCollector botActionCollector;

  @Override
  public void processCommand(Message message, String text) {
    long chatId = message.getChatId();
    Message replyToMessage = message.getReplyToMessage();
    if (replyToMessage != null && replyToMessage.hasAnimation()) {
      Animation animation = replyToMessage.getAnimation();
      botActionCollector.text(
          chatId, new SimpleText(animation.getFileId()), replyToMessage.getMessageId());
      return;
    }
    String botId = StringUtil.substringBefore(pidorBot.getBotToken(), ":");
    Optional<GifFile> gif = GifFile.parseGif(text);
    if (gif.isPresent()) {
      for (String fileId : gif.get().getFiles(botId)) {
        botActionCollector.collectHTMLMessage(chatId, "GIF " + fileId);

        botActionCollector.add(new SendAnimationBotAction(chatId, fileId));
      }
    } else {
      for (GifFile fileGif : GifFile.values()) {
        botActionCollector.collectHTMLMessage(chatId, "File Type " + fileGif.name());
      }
    }
  }

  @Override
  public Command getCommand() {
    return Command.GIF;
  }
}
