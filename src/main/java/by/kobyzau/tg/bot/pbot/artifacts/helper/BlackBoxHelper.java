package by.kobyzau.tg.bot.pbot.artifacts.helper;

import by.kobyzau.tg.bot.pbot.artifacts.ArtifactType;
import by.kobyzau.tg.bot.pbot.model.Pidor;
import by.kobyzau.tg.bot.pbot.model.dto.SerializableInlineType;
import by.kobyzau.tg.bot.pbot.service.PidorService;
import by.kobyzau.tg.bot.pbot.util.DateUtil;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Component
@NoArgsConstructor
@AllArgsConstructor
public class BlackBoxHelper {

  @Autowired private PidorService pidorService;

  private final Map<LocalDate, List<HandledRequest>> handledRequests = new ConcurrentHashMap<>();

  public int getNumArtifactsPerDay(long chatId) {
    int numPidors = pidorService.getByChat(chatId).size();
    if (numPidors <= 2) {
      return 1;
    }
    if (numPidors > 10) {
      return 5;
    }
    return numPidors / 2;
  }

  public String getRequestId() {
    return UUID.randomUUID()
        .toString()
        .substring(SerializableInlineType.OPEN_BLACK_BOX.getIdSize());
  }

  public List<ArtifactType> getArtifactsForBox(long chatId) {
    List<ArtifactType> artifactTypeList = new ArrayList<>(Arrays.asList(ArtifactType.values()));
    artifactTypeList.add(ArtifactType.PIDOR_MAGNET);
    boolean hasAntiPidor =
        pidorService.getByChat(chatId).stream()
            .map(Pidor::getArtifacts)
            .flatMap(Collection::stream)
            .anyMatch(a -> a == ArtifactType.ANTI_PIDOR);
    if (hasAntiPidor) {
      artifactTypeList.remove(ArtifactType.ANTI_PIDOR);
    }
    return artifactTypeList;
  }

  public boolean checkRequest(long chatId, long userId, String requestId) {
    List<HandledRequest> handledRequests =
        this.handledRequests.getOrDefault(DateUtil.now(), new ArrayList<>());
    boolean wasHandled =
        handledRequests.stream()
            .filter(h -> h.chatId == chatId)
            .anyMatch(h -> h.id.equalsIgnoreCase(requestId));
    if (wasHandled) {
      return true;
    }
    handledRequests.add(new HandledRequest(chatId, userId, requestId));
    this.handledRequests.put(DateUtil.now(), handledRequests);
    return false;
  }

  public boolean isUserOpenedBox(long chatId, long userId) {
    return this.handledRequests.getOrDefault(DateUtil.now(), Collections.emptyList()).stream()
        .filter(h -> h.chatId == chatId)
        .anyMatch(h -> h.userId == userId);
  }

  public int getHandledNum(long chatId) {
    return (int)
        this.handledRequests.getOrDefault(DateUtil.now(), Collections.emptyList()).stream()
            .filter(h -> h.chatId == chatId)
            .count();
  }

  @Data
  @RequiredArgsConstructor
  private static class HandledRequest {
    private final long chatId;
    private final long userId;
    private final String id;
  }
}
