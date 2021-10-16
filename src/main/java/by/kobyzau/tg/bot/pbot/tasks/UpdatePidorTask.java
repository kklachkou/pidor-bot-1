package by.kobyzau.tg.bot.pbot.tasks;

import by.kobyzau.tg.bot.pbot.model.Pidor;
import by.kobyzau.tg.bot.pbot.program.logger.Logger;
import by.kobyzau.tg.bot.pbot.program.text.LongText;
import by.kobyzau.tg.bot.pbot.program.text.ParametizedText;
import by.kobyzau.tg.bot.pbot.program.text.pidor.FullNamePidorText;
import by.kobyzau.tg.bot.pbot.service.PidorService;
import by.kobyzau.tg.bot.pbot.service.TelegramService;
import by.kobyzau.tg.bot.pbot.util.TGUtil;
import by.kobyzau.tg.bot.pbot.util.ThreadUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.api.objects.chatmember.ChatMember;
import org.telegram.telegrambots.meta.api.objects.chatmember.ChatMemberAdministrator;
import org.telegram.telegrambots.meta.api.objects.chatmember.ChatMemberMember;
import org.telegram.telegrambots.meta.api.objects.chatmember.ChatMemberOwner;
import org.telegram.telegrambots.meta.api.objects.chatmember.ChatMemberRestricted;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.Executor;

@Component("updatePidorTask")
public class UpdatePidorTask implements Task {

  @Autowired private TelegramService telegramService;

  @Autowired private PidorService pidorService;

  @Autowired private Logger logger;

  @Autowired
  @Qualifier("updatePidorExecutor")
  private Executor executor;

  @Override
  public void processTask() {
    logger.debug("\uD83D\uDCC6 Task " + this.getClass().getSimpleName() + " is started");
    List<Long> chatIds = pidorService.getChatIds();
    for (Long chatId : chatIds) {
      executor.execute(() -> pidorService.getByChat(chatId).forEach(this::updatePidor));
    }
  }

  private void updatePidor(Pidor pidor) {
    ThreadUtil.sleep(200);
    Optional<User> telegramUser = getTelegramUser(pidor);
    if (telegramUser.isPresent()) {
      pidor.setFullName(TGUtil.getFullName(telegramUser.get()));
      pidor.setUsername(telegramUser.get().getUserName());
      pidorService.updatePidor(pidor);
    } else {
      logger.info(
          new ParametizedText(
                  "Deleting pidor {0} {1} from chat {2}",
                  new LongText(pidor.getTgId()),
                  new FullNamePidorText(pidor),
                  new LongText(pidor.getChatId()))
              .text());
      pidorService.deletePidor(pidor.getChatId(), pidor.getTgId());
    }
  }

  private Optional<User> getTelegramUser(Pidor pidor) {
    Optional<ChatMember> chatMember =
        telegramService.getChatMember(pidor.getChatId(), pidor.getTgId());
    User user = null;
    if (chatMember.isPresent()) {
      String status = chatMember.get().getStatus();
      switch (status) {
        case "administrator":
          ChatMemberAdministrator chatMemberAdministrator =
              (ChatMemberAdministrator) chatMember.get();
          user = chatMemberAdministrator.getUser();
          break;

        case "creator":
          ChatMemberOwner chatMemberOwner = (ChatMemberOwner) chatMember.get();
          user = chatMemberOwner.getUser();
          break;
        case "member":
          ChatMemberMember chatMemberMember = (ChatMemberMember) chatMember.get();
          user = chatMemberMember.getUser();
          break;
        case "restricted":
          ChatMemberRestricted chatMemberRestricted = (ChatMemberRestricted) chatMember.get();
          user = chatMemberRestricted.getUser();
          break;
      }
    }
    return Optional.ofNullable(user);
  }
}
