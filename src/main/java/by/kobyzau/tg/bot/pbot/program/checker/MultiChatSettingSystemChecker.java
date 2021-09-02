package by.kobyzau.tg.bot.pbot.program.checker;

import by.kobyzau.tg.bot.pbot.model.ChatSetting;
import by.kobyzau.tg.bot.pbot.program.logger.Logger;
import by.kobyzau.tg.bot.pbot.repository.chat.ChatSettingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class MultiChatSettingSystemChecker implements SystemChecker {
  private final ChatSettingRepository chatSettingRepository;
  private final Logger logger;

  @Override
  public void check() {
    List<ChatSetting> settings = chatSettingRepository.getAll();
    List<Long> chatIds = settings.stream().map(ChatSetting::getChatId).collect(Collectors.toList());
    Set<Long> checkedChatIds = new HashSet<>();
    for (Long chatId : chatIds) {
      if (!checkedChatIds.add(chatId)) {
        logger.warn("! #CHECKER\nChat has multiple settings: " + chatId);
      }
    }
  }
}
