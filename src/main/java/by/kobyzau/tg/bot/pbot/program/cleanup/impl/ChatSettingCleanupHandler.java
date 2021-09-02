package by.kobyzau.tg.bot.pbot.program.cleanup.impl;

import by.kobyzau.tg.bot.pbot.model.ChatSetting;
import by.kobyzau.tg.bot.pbot.program.cleanup.CleanupHandler;
import by.kobyzau.tg.bot.pbot.repository.chat.ChatSettingRepository;
import by.kobyzau.tg.bot.pbot.repository.pidor.PidorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ChatSettingCleanupHandler implements CleanupHandler {
  private final ChatSettingRepository chatSettingRepository;
  private final PidorRepository pidorRepository;

  @Override
  public void cleanup() {
    for (ChatSetting chatSetting : chatSettingRepository.getAll()) {
      boolean canCleanup = pidorRepository.getByChat(chatSetting.getChatId()).isEmpty();
      if (canCleanup) {
        chatSettingRepository.delete(chatSetting.getId());
      }
    }
  }
}
