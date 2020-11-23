package by.kobyzau.tg.bot.pbot.handlers.command.handler;

import by.kobyzau.tg.bot.pbot.collectors.BotActionCollector;
import by.kobyzau.tg.bot.pbot.handlers.command.Command;
import by.kobyzau.tg.bot.pbot.handlers.command.handler.achievement.Achievement;
import by.kobyzau.tg.bot.pbot.program.selection.ConsistentSelection;
import by.kobyzau.tg.bot.pbot.program.selection.Selection;
import by.kobyzau.tg.bot.pbot.program.text.SimpleText;
import by.kobyzau.tg.bot.pbot.program.text.Text;
import by.kobyzau.tg.bot.pbot.program.text.TextBuilder;
import by.kobyzau.tg.bot.pbot.tg.ChatAction;
import by.kobyzau.tg.bot.pbot.tg.sticker.StickerType;
import by.kobyzau.tg.bot.pbot.util.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class AchievementCommandHandler implements CommandHandler {

  @Autowired private List<Achievement> achievements;

  @Autowired private BotActionCollector botActionCollector;

  private Selection<Text> calcMessages =
      new ConsistentSelection<>(
              "Анализирую данные...",
              "Формирую рейтинги...",
              "Составляю списки...",
              "Произвожу расчёты...")
          .map(SimpleText::new);

  @Override
  public void processCommand(Message message, String text) {
    long chatId = message.getChatId();
    botActionCollector.text(chatId, calcMessages.next());
    botActionCollector.wait(chatId, 2, ChatAction.TYPING);
    botActionCollector.text(chatId, calcMessages.next());
    botActionCollector.wait(chatId, 2, ChatAction.TYPING);
    List<Text> achievementInfos =
        achievements.stream()
            .map(a -> a.getAchievementInfo(StringUtil.parseLong(text, message.getChatId())))
            .filter(Optional::isPresent)
            .map(Optional::get)
            .collect(Collectors.toList());
    if (achievementInfos.isEmpty()) {
      botActionCollector.text(chatId, new SimpleText("В этом чате нету достижений:("));
      botActionCollector.wait(chatId, 2, ChatAction.TYPING);
      botActionCollector.sticker(chatId, StickerType.SAD.getRandom());
      return;
    }
    botActionCollector.text(chatId, new SimpleText("Вот список всех достижений:"));
    for (Text achievementInfo : achievementInfos) {
      botActionCollector.wait(chatId,  ChatAction.TYPING);
      botActionCollector.text(
          chatId, new TextBuilder(new SimpleText("\t\t\u26A1\u26A1\u26A1"), achievementInfo));
      botActionCollector.wait(chatId, 1, ChatAction.TYPING);
      botActionCollector.sticker(chatId, StickerType.COOL.getRandom());
    }
  }

  @Override
  public Command getCommand() {
    return Command.ACHIEVEMENT;
  }
}
