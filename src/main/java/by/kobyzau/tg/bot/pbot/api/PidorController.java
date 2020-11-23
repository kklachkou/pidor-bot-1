package by.kobyzau.tg.bot.pbot.api;

import by.kobyzau.tg.bot.pbot.api.validator.AccessHeaderValidator;
import by.kobyzau.tg.bot.pbot.model.Pidor;
import by.kobyzau.tg.bot.pbot.program.text.pidor.FullNamePidorText;
import by.kobyzau.tg.bot.pbot.repository.pidor.PidorRepository;
import by.kobyzau.tg.bot.pbot.service.TelegramService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
public class PidorController {

  @Autowired private TelegramService telegramService;

  @Autowired private PidorRepository pidorRepository;

  @Autowired private AccessHeaderValidator accessHeaderValidator;

  @GetMapping("/chats/{chatId}/pidors")
  public Object getPidors(@RequestHeader("api-token") String token, @PathVariable("chatId") long chatId) {
    accessHeaderValidator.assertAssess(token);
    List<Pidor> pidors = pidorRepository.getByChat(chatId);
    return "["
        + pidors.stream().map(this::toDto).map(PidorDTO::toString).collect(Collectors.joining(","))
        + "]";
  }

  @DeleteMapping("/{id}")
  public void deleteChat(@RequestHeader("api-token") String token, @PathVariable long id) {
    accessHeaderValidator.assertAssess(token);
    pidorRepository.getByChat(id).stream().map(Pidor::getId).forEach(pidorRepository::delete);
  }

  private PidorDTO toDto(Pidor pidor) {
    return new PidorDTO(pidor.getId(), pidor.getTgId(), new FullNamePidorText(pidor).text());
  }

  public static class PidorDTO {
    private long id;
    private int tgId;
    private String displayName;

    public PidorDTO(long id, int tgId, String displayName) {
      this.id = id;
      this.tgId = tgId;
      this.displayName = displayName;
    }

    @Override
    public String toString() {
      return "{"
          + "\"id\": "
          + id
          + ", \"displayName\": \""
          + displayName
          + "\", \"tgId\": "
          + tgId
          + '}';
    }
  }
}
