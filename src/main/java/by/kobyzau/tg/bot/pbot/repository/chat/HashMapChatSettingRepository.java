package by.kobyzau.tg.bot.pbot.repository.chat;

import by.kobyzau.tg.bot.pbot.model.ChatSetting;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

@Component
@Profile("dev")
public class HashMapChatSettingRepository implements ChatSettingRepository {

  private final Map<Long, ChatSetting> map = new HashMap<>();

  @Override
  public Optional<ChatSetting> getByChatId(long chatId) {
    return getAll().stream().filter(c -> c.getChatId() == chatId).findFirst();
  }

  @Override
  public long create(ChatSetting obj) {
    long newId = getNewId();
    obj.setId(newId);
    map.put(newId, obj);
    return newId;
  }

  @Override
  public void update(ChatSetting obj) {
    map.put(obj.getId(), obj);
  }

  @Override
  public ChatSetting get(long id) {
    return map.get(id);
  }

  @Override
  public List<ChatSetting> getAll() {
    return map.values().stream().filter(Objects::nonNull).collect(Collectors.toList());
  }

  @Override
  public void delete(long id) {
    map.remove(id);
  }

  private synchronized long getNewId() {
    long newId = map.keySet().stream().max(Long::compareTo).orElse(1L) + 1;
    map.put(newId, null);
    return newId;
  }
}
