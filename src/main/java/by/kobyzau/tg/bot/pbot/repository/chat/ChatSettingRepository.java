package by.kobyzau.tg.bot.pbot.repository.chat;

import by.kobyzau.tg.bot.pbot.model.ChatSetting;
import by.kobyzau.tg.bot.pbot.repository.CrudRepository;

import java.util.Optional;

public interface ChatSettingRepository extends CrudRepository<ChatSetting> {

  Optional<ChatSetting> getByChatId(long chatId);
}
