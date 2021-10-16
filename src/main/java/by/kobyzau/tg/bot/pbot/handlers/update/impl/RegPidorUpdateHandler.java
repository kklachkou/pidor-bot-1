package by.kobyzau.tg.bot.pbot.handlers.update.impl;

import by.kobyzau.tg.bot.pbot.collectors.BotActionCollector;
import by.kobyzau.tg.bot.pbot.handlers.update.UpdateHandler;
import by.kobyzau.tg.bot.pbot.handlers.update.UpdateHandlerStage;
import by.kobyzau.tg.bot.pbot.model.Pidor;
import by.kobyzau.tg.bot.pbot.program.logger.Logger;
import by.kobyzau.tg.bot.pbot.program.printer.UserPrinter;
import by.kobyzau.tg.bot.pbot.program.selection.ConsistentSelection;
import by.kobyzau.tg.bot.pbot.program.selection.Selection;
import by.kobyzau.tg.bot.pbot.program.text.ParametizedText;
import by.kobyzau.tg.bot.pbot.program.text.pidor.FullNamePidorText;
import by.kobyzau.tg.bot.pbot.service.ChatSettingsService;
import by.kobyzau.tg.bot.pbot.service.PidorService;
import by.kobyzau.tg.bot.pbot.service.TelegramService;
import by.kobyzau.tg.bot.pbot.tg.ChatAction;
import by.kobyzau.tg.bot.pbot.tg.sticker.StickerType;
import by.kobyzau.tg.bot.pbot.util.TGUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.api.objects.chatmember.ChatMember;

import java.util.Optional;

import static by.kobyzau.tg.bot.pbot.service.ChatSettingsService.ChatCheckboxSettingType.AUTO_REGISTER_USERS;

@Component
public class RegPidorUpdateHandler implements UpdateHandler {
  @Autowired private PidorService pidorService;
  @Autowired private BotActionCollector botActionCollector;
  @Autowired private TelegramService telegramService;
  @Autowired private ChatSettingsService chatSettingsService;
  @Autowired private Logger logger;
  private final Selection<String> catchedPidorMessage =
      new ConsistentSelection<>(
          "Ага, попался, {0}\nТеперь ты в игре, хочет ты этого или нет",
          "{0}, зря ты заговорил\nТеперь ты в игре",
          "Добавлю ка я {0} в игру",
          "{0} - добро пожаловать в семью!",
          "Приветствуйте нового члена клана - {0}",
          "Теперь {0} участвует в игре");

  @Override
  public UpdateHandlerStage getStage() {
    return UpdateHandlerStage.INVOKE_USER;
  }

  @Override
  public boolean handleUpdate(Update update) {
    if (!update.hasMessage()) {
      return false;
    }

    return updateUser(update.getMessage().getChat(), update.getMessage().getFrom());
  }

  private boolean updateUser(Chat chat, User user) {
    long chatId = chat.getId();
    if (!chatSettingsService.isEnabled(AUTO_REGISTER_USERS, chatId)) {
      return false;
    }
    Boolean bot = user.getIsBot();
    if (bot) {
      return false;
    }
    Optional<Pidor> existingPidor = pidorService.getPidor(chatId, user.getId());
    if (existingPidor.isPresent()) {
      return false;
    }
    Optional<ChatMember> chatMember = telegramService.getChatMember(chatId, user.getId());
    if (!chatMember.isPresent() || !TGUtil.isChatMember(chatMember)) {
      return false;
    }
    botActionCollector.typing(chatId);
    logger.info(
        "\uD83D\uDC66\uD83C\uDFFB Creating new Pidor "
            + chatMember.get().getStatus()
            + " for chat "
            + chatId
            + ":"
            + chat.getTitle()
            + ":\n\n"
            + new UserPrinter(user));

    Pidor pidor = pidorService.createPidor(chatId, user);
    botActionCollector.text(
        chatId, new ParametizedText(catchedPidorMessage.next(), new FullNamePidorText(pidor)));
    botActionCollector.wait(chatId, 1, ChatAction.TYPING);
    botActionCollector.sticker(chatId, StickerType.LOL.getRandom());
    return true;
  }
}
