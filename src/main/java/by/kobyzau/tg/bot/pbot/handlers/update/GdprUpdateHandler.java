package by.kobyzau.tg.bot.pbot.handlers.update;

import by.kobyzau.tg.bot.pbot.collectors.BotActionCollector;
import by.kobyzau.tg.bot.pbot.model.Pidor;
import by.kobyzau.tg.bot.pbot.model.dto.GdprMessageDto;
import by.kobyzau.tg.bot.pbot.service.ChatSettingsService;
import by.kobyzau.tg.bot.pbot.service.FutureActionService;
import by.kobyzau.tg.bot.pbot.service.PidorService;
import by.kobyzau.tg.bot.pbot.util.DateUtil;
import by.kobyzau.tg.bot.pbot.util.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.Executor;

import static by.kobyzau.tg.bot.pbot.service.ChatSettingsService.ChatCheckboxSettingType.GDPR_MESSAGE_ENABLED;
import static by.kobyzau.tg.bot.pbot.service.FutureActionService.FutureActionType.GDPR_MESSAGE;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class GdprUpdateHandler implements UpdateHandler {

  private Set<String> gdprKeywords =
      new HashSet<>(Arrays.asList("митинг", "лукаш", "ябацк", "флаг", "бчб"));

  @Autowired private BotActionCollector botActionCollector;
  @Autowired private ChatSettingsService chatSettingsService;
  @Autowired private PidorService pidorService;
  @Autowired private FutureActionService futureActionService;

  @Autowired
  @Qualifier("cachedExecutor")
  private Executor executor;

  @Override
  public boolean handleUpdate(Update update) {
    if (update.hasMessage()) {
      Message message = update.getMessage();
      executor.execute(() -> gdpr(message));
    }
    return false;
  }

  private void gdpr(Message message) {
    checkUser(message);
    checkFutureDeleteMessage(message);
  }

  private void checkFutureDeleteMessage(Message message) {
    if (message.hasText()
        && chatSettingsService.isEnabled(GDPR_MESSAGE_ENABLED, message.getChatId())) {
      String text = message.getText().trim().toLowerCase();
      if (gdprKeywords.stream().anyMatch(text::contains)) {
        GdprMessageDto dto = new GdprMessageDto(message.getChatId(), message.getMessageId());
        futureActionService.saveFutureActionData(
            GDPR_MESSAGE, DateUtil.now().plusDays(1), StringUtil.serialize(dto));
      }
    }
  }

  private void checkUser(Message message) {
    User user = message.getFrom();
    if (user == null) {
      return;
    }
    Optional<Pidor> pidor = pidorService.getPidor(message.getChatId(), user.getId());
    if (pidor.isPresent()) {
      pidor.get().setUsernameLastUpdated(DateUtil.now());
      pidorService.updatePidor(pidor.get());
    }
  }
}
