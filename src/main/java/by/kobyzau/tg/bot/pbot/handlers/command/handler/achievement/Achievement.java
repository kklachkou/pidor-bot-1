package by.kobyzau.tg.bot.pbot.handlers.command.handler.achievement;

import java.util.Optional;

import by.kobyzau.tg.bot.pbot.program.text.Text;

public interface Achievement {

  Optional<Text> getAchievementInfo(long chatId);
}
