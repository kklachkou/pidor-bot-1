package by.kobyzau.tg.bot.pbot.tasks;

import by.kobyzau.tg.bot.pbot.bots.Bot;
import by.kobyzau.tg.bot.pbot.collectors.BotActionCollector;
import by.kobyzau.tg.bot.pbot.program.logger.Logger;
import by.kobyzau.tg.bot.pbot.program.text.NewLineText;
import by.kobyzau.tg.bot.pbot.program.text.ParametizedText;
import by.kobyzau.tg.bot.pbot.program.text.SimpleText;
import by.kobyzau.tg.bot.pbot.program.text.TextBuilder;
import by.kobyzau.tg.bot.pbot.service.TelegramService;
import by.kobyzau.tg.bot.pbot.util.DateUtil;
import by.kobyzau.tg.bot.pbot.util.TGUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.GetMe;
import org.telegram.telegrambots.meta.api.methods.groupadministration.GetChatMember;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.api.objects.chatmember.ChatMember;

import java.util.Optional;
import java.util.concurrent.Executor;

@Component("askForPermissionTask")
public class AskForPermissionTask implements Task {

  @Autowired private BotActionCollector botActionCollector;
  @Autowired private TelegramService telegramService;
  @Autowired private Logger logger;
  @Autowired private Bot bot;

  @Autowired
  @Qualifier("taskExecutor")
  private Executor executor;

  @Override
  public void processTask() {
    if (DateUtil.now().getDayOfMonth() == 10) {
      telegramService
          .getChatIds()
          .forEach(chatId -> executor.execute(() -> askForPinPermission(chatId)));
      return;
    }
    if (DateUtil.now().getDayOfMonth() == 20) {
      telegramService
          .getChatIds()
          .forEach(chatId -> executor.execute(() -> askForOtherMessagesPermission(chatId)));
      return;
    }
  }

  private void askForPinPermission(long chatId) {
    boolean canPinMessages = getBotMember(chatId).filter(TGUtil::canPinMessage).isPresent();
    if (!canPinMessages) {
      logger.info("\uD83D\uDCC6 Asking Pin permission of " + chatId);
      botActionCollector.text(
          chatId,
          new SimpleText(
              "Администратору чата привет! Если дать мне права на закрепление сообщений, будет намного удобнее следить за тем, кто сегодня пидор или какая игра сегодня проходит"));
      botActionCollector.text(
          chatId,
          new TextBuilder(
                  new SimpleText(
                      "Если чат приватный, бота также нужно сделать администратором чата:("))
              .append(new NewLineText())
              .append(new NewLineText())
              .append(
                  new ParametizedText(
                      "Так сказал телеграмм: https://core.telegram.org/bots/api#pinchatmessage")));
      botActionCollector.text(
          chatId,
          new SimpleText(
              "Но не переживай, можете не давать администратору никаких прав, кроме права закрепления сообщений"));
    }
  }

  private void askForOtherMessagesPermission(long chatId) {
    boolean canSendOtherMessages =
        getBotMember(chatId).filter(TGUtil::canSendOtherMessages).isPresent();
    if (!canSendOtherMessages) {
      logger.info("\uD83D\uDCC6 Asking Other Messages permission of " + chatId);
      botActionCollector.text(
          chatId,
          new SimpleText(
              "Администратору чата привет!"
                  + " Дай мне, пожалуйста права на отправку Стикетов и GIF сообщений и будет намного веселее"));
    }
  }

  private Optional<ChatMember> getBotMember(long chatId) {
    try {
      User botUser = bot.execute(GetMe.builder().build());
      ChatMember botMember =
          bot.execute(
              GetChatMember.builder()
                  .chatId(String.valueOf(chatId))
                  .userId(botUser.getId())
                  .build());
      return Optional.of(botMember);
    } catch (Exception e) {
      return Optional.empty();
    }
  }
}
