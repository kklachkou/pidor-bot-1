package by.kobyzau.tg.bot.pbot.repository.chat;

import by.kobyzau.tg.bot.pbot.model.ChatSetting;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Component
@Profile("prod")
@RequiredArgsConstructor
public class DBChatSettingRepository implements ChatSettingRepository {

  private final IChatSettingRepository repository;

  @Override
  public Optional<ChatSetting> getByChatId(long chatId) {
    return repository.findByChatId(chatId);
  }

  @Override
  public long create(ChatSetting obj) {
    return repository.save(obj).getId();
  }

  @Override
  public void update(ChatSetting obj) {
    repository.save(obj);
  }

  @Override
  public ChatSetting get(long id) {
    return repository.findById(id).orElse(null);
  }

  @Override
  public List<ChatSetting> getAll() {
    return StreamSupport.stream(repository.findAll().spliterator(), false)
        .collect(Collectors.toList());
  }

  @Override
  public void delete(long id) {
    repository.deleteById(id);
  }
}
