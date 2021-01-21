package by.kobyzau.tg.bot.pbot.tasks;

import by.kobyzau.tg.bot.pbot.model.Pidor;
import by.kobyzau.tg.bot.pbot.program.logger.Logger;
import by.kobyzau.tg.bot.pbot.service.BotService;
import by.kobyzau.tg.bot.pbot.service.PidorService;
import by.kobyzau.tg.bot.pbot.service.TelegramService;
import by.kobyzau.tg.bot.pbot.util.TGUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.ChatMember;
import org.telegram.telegrambots.meta.api.objects.User;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.Executor;

@Component("updateUsernameTask")
public class UpdateUsernameTask implements Task {

  @Autowired private TelegramService telegramService;

  @Autowired private PidorService pidorService;

  @Autowired private Logger logger;
  @Autowired private BotService botService;

  @Autowired
  @Qualifier("taskExecutor")
  private Executor executor;

  @Override
  public void processTask() {
    logger.debug("\uD83D\uDCC6 Task " + this.getClass().getSimpleName() + " is started");
    List<Long> chatIds = telegramService.getChatIds();
    for (Long chatId : chatIds) {
      if (botService.isChatValid(chatId)) {
        executor.execute(() -> pidorService.getByChat(chatId).forEach(this::updateUsername));
      }
    }
  }

  private void updateUsername(Pidor pidor) {
    try {
      Optional<ChatMember> chatMember =
          telegramService.getChatMember(pidor.getChatId(), pidor.getTgId());
      if (chatMember.isPresent()) {
        User user = chatMember.get().getUser();
        pidor.setFullName(TGUtil.getFullName(user));
        pidor.setUsername(user.getUserName());
        pidorService.updatePidor(pidor);
      }
    } catch (Exception e) {
      logger.error("Cannot update username for " + pidor, e);
    }
  }
}
