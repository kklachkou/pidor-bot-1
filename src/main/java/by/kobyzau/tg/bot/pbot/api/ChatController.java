package by.kobyzau.tg.bot.pbot.api;

import by.kobyzau.tg.bot.pbot.api.validator.AccessHeaderValidator;
import by.kobyzau.tg.bot.pbot.model.Pidor;
import by.kobyzau.tg.bot.pbot.repository.pidor.PidorRepository;
import by.kobyzau.tg.bot.pbot.service.TelegramService;
import by.kobyzau.tg.bot.pbot.util.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.telegram.telegrambots.meta.api.objects.Chat;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/chats")
public class ChatController {

  @Autowired private TelegramService telegramService;

  @Autowired private PidorRepository pidorRepository;

  @Autowired private AccessHeaderValidator accessHeaderValidator;

  @GetMapping("/")
  public Object getChats(@RequestHeader("api-token") String token) {
    accessHeaderValidator.assertAssess(token);
    List<Long> chatIds = telegramService.getChatIds();
    return "["
        + chatIds.stream().map(this::toDto).map(ChatDTO::toString).collect(Collectors.joining(","))
        + "]";
  }

  @DeleteMapping("/{id}")
  public void deleteChat(@RequestHeader("api-token") String token, @PathVariable long id) {
    accessHeaderValidator.assertAssess(token);
    pidorRepository.getByChat(id).stream().map(Pidor::getId).forEach(pidorRepository::delete);
  }

  private ChatDTO toDto(long chatId) {
    ChatDTO dto = new ChatDTO();
    dto.setId(chatId);
    Optional<Chat> chat = telegramService.getChat(chatId);
    if (!chat.isPresent()) {
      dto.setActive(false);
      return dto;
    }
    telegramService.getChatMemberCount(chatId).ifPresent(dto::setMembers);
    if (chat.get().getPermissions() != null) {
      dto.setSendPermission(chat.get().getPermissions().getCanSendMessages());
    }
    StringBuilder displayName = new StringBuilder();
    if (StringUtil.isNotBlank(chat.get().getFirstName())) {
      displayName.append("FN: ").append(chat.get().getFirstName());
    }
    if (StringUtil.isNotBlank(chat.get().getUserName())) {
      displayName.append("UN: ").append(chat.get().getUserName());
    }
    if (StringUtil.isNotBlank(chat.get().getTitle())) {
      displayName.append("TT: ").append(chat.get().getTitle());
    }
    dto.setDisplayName(displayName.toString());
    dto.setActive(true);
    return dto;
  }

  public static class ChatDTO {
    private long id;
    private String displayName;
    private Boolean sendPermission;
    private Boolean isActive;
    private int members;

    public long getId() {
      return id;
    }

    public void setId(long id) {
      this.id = id;
    }

    public Boolean getSendPermission() {
      return sendPermission;
    }

    public void setSendPermission(Boolean sendPermission) {
      this.sendPermission = sendPermission;
    }

    public String getDisplayName() {
      return displayName;
    }

    public void setDisplayName(String displayName) {
      this.displayName = displayName;
    }

    public Boolean getActive() {
      return isActive;
    }

    public void setActive(Boolean active) {
      isActive = active;
    }

    public int getMembers() {
      return members;
    }

    public void setMembers(int members) {
      this.members = members;
    }

    @Override
    public String toString() {
      return "{"
          + "\"id\": "
          + id
          + ", \"displayName\": \""
          + displayName
          + ", \"members\": \""
          + members
          + "\", \"isActive\": "
          + isActive
          + '}';
    }
  }
}
