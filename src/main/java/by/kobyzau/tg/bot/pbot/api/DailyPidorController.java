package by.kobyzau.tg.bot.pbot.api;

import by.kobyzau.tg.bot.pbot.api.validator.AccessHeaderValidator;
import by.kobyzau.tg.bot.pbot.model.DailyPidor;
import by.kobyzau.tg.bot.pbot.model.Pidor;
import by.kobyzau.tg.bot.pbot.program.text.pidor.ShortNamePidorText;
import by.kobyzau.tg.bot.pbot.repository.dailypidor.DailyPidorRepository;
import by.kobyzau.tg.bot.pbot.service.PidorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
public class DailyPidorController {

  @Autowired private DailyPidorRepository dailyPidorRepository;
  @Autowired private PidorService pidorService;
  @Autowired private AccessHeaderValidator accessHeaderValidator;

  @GetMapping("/chats/{chatId}/daily-pidors")
  public Object getDailyPidors(@RequestHeader("api-token") String token, @PathVariable("chatId") long chatId) {
    accessHeaderValidator.assertAssess(token);
    List<DailyPidor> dailyPidors = dailyPidorRepository.getByChat(chatId);
    return "["
        + dailyPidors.stream()
            .sorted(Comparator.comparing(DailyPidor::getLocalDate).reversed())
            .map(this::toDto)
            .map(DailyPidorDTO::toString)
            .collect(Collectors.joining(","))
        + "]";
  }

  @DeleteMapping("/daily-pidors/{id}")
  public void deleteDailyPidor(@RequestHeader("api-token") String token, @PathVariable long id) {
    accessHeaderValidator.assertAssess(token);
    dailyPidorRepository.delete(id);
  }

  private DailyPidorDTO toDto(DailyPidor dailyPidor) {
    DailyPidorDTO dto = new DailyPidorDTO();
    dto.setId(dailyPidor.getId());
    dto.setChatId(dailyPidor.getChatId());
    Optional<Pidor> pidor =
        pidorService.getPidor(dailyPidor.getChatId(), dailyPidor.getPlayerTgId());
    if (pidor.isPresent()) {
      dto.setPidor(new ShortNamePidorText(pidor.get()).text());
    } else {
      dto.setPidor("Pidor ID: " + dailyPidor.getPlayerTgId());
    }

    dto.setDate(dailyPidor.getLocalDate());
    return dto;
  }

  public static class DailyPidorDTO {
    private Long id;
    private Long chatId;
    private LocalDate date;
    private String pidor;

    public Long getId() {
      return id;
    }

    public void setId(Long id) {
      this.id = id;
    }

    public Long getChatId() {
      return chatId;
    }

    public void setChatId(Long chatId) {
      this.chatId = chatId;
    }

    public LocalDate getDate() {
      return date;
    }

    public void setDate(LocalDate date) {
      this.date = date;
    }

    public String getPidor() {
      return pidor;
    }

    public void setPidor(String pidor) {
      this.pidor = pidor;
    }

    @Override
    public String toString() {
      return "{"
          + "\"id\": "
          + id
          + ", \"chatId\": "
          + chatId
          + ", \"date\": \""
          + date
          + "\", \"pidor\": \""
          + pidor
          + "\"}";
    }
  }
}
