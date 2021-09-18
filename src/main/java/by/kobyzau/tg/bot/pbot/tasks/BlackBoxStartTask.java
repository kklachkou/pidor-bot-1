package by.kobyzau.tg.bot.pbot.tasks;

import by.kobyzau.tg.bot.pbot.artifacts.service.UserArtifactService;
import by.kobyzau.tg.bot.pbot.collectors.BotActionCollector;
import by.kobyzau.tg.bot.pbot.model.dto.OpenBlackBoxDto;
import by.kobyzau.tg.bot.pbot.program.logger.Logger;
import by.kobyzau.tg.bot.pbot.program.text.SimpleText;
import by.kobyzau.tg.bot.pbot.service.TelegramService;
import by.kobyzau.tg.bot.pbot.tg.action.SimpleStickerBotAction;
import by.kobyzau.tg.bot.pbot.tg.sticker.StickerType;
import by.kobyzau.tg.bot.pbot.util.DateUtil;
import by.kobyzau.tg.bot.pbot.util.StringUtil;
import by.kobyzau.tg.bot.pbot.util.helper.DateHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendSticker;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.time.DayOfWeek;
import java.util.Collections;
import java.util.concurrent.Executor;

@Component("blackBoxStartTask")
public class BlackBoxStartTask implements Task {

  @Autowired private TelegramService telegramService;
  @Autowired private UserArtifactService userArtifactService;

  @Autowired private BotActionCollector botActionCollector;

  @Autowired private Logger logger;
  @Autowired private DateHelper dateHelper;

  @Autowired
  @Qualifier("taskExecutor")
  private Executor executor;

  @Override
  public void processTask() {
    logger.debug("\uD83D\uDCC6 Task " + this.getClass().getSimpleName() + " is started");
    telegramService.getChatIds().forEach(chatId -> executor.execute(() -> sendBlackBox(chatId)));
  }

  private void sendBlackBox(long chatId) {
    userArtifactService.clearUserArtifacts(chatId);
    DayOfWeek dayOfWeek = dateHelper.now().getDayOfWeek();
    if (dayOfWeek != DayOfWeek.WEDNESDAY
        && dayOfWeek != DayOfWeek.SUNDAY
        && dayOfWeek != DayOfWeek.SATURDAY) {
      return;
    }
    botActionCollector.text(
        chatId,
        new SimpleText(
            "Это <b>черный ящик</b>!"
                + "\nДостанеться он только ОДНОМУ!"
                + "\nВнутри лежит артефакт, бонус или анти-бонус"));
    botActionCollector.add(
        new SimpleStickerBotAction(
            chatId,
            SendSticker.builder()
                .chatId(String.valueOf(chatId))
                .sticker(new InputFile(StickerType.GIFT.getRandom()))
                .disableNotification(DateUtil.sleepTime())
                .replyMarkup(
                    InlineKeyboardMarkup.builder()
                        .keyboardRow(
                            Collections.singletonList(
                                InlineKeyboardButton.builder()
                                    .text("Открыть подарок")
                                    .callbackData(StringUtil.serialize(new OpenBlackBoxDto()))
                                    .build()))
                        .build())
                .build()));
  }
}
