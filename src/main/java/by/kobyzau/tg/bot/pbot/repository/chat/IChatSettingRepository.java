package by.kobyzau.tg.bot.pbot.repository.chat;

import by.kobyzau.tg.bot.pbot.model.ChatSetting;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface IChatSettingRepository extends CrudRepository<ChatSetting, Long> {

  @Query("FROM ChatSetting WHERE chatId = ?1")
  Optional<ChatSetting> findByChatId(long chatId);
}
