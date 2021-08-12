package by.kobyzau.tg.bot.pbot.handlers.command.handler;

import by.kobyzau.tg.bot.pbot.collectors.BotActionCollector;
import by.kobyzau.tg.bot.pbot.handlers.command.Command;
import by.kobyzau.tg.bot.pbot.model.Pidor;
import by.kobyzau.tg.bot.pbot.model.dto.HotPotatoDto;
import by.kobyzau.tg.bot.pbot.program.text.BoldText;
import by.kobyzau.tg.bot.pbot.program.text.ParametizedText;
import by.kobyzau.tg.bot.pbot.program.text.TimeLeftText;
import by.kobyzau.tg.bot.pbot.program.text.pidor.FullNamePidorText;
import by.kobyzau.tg.bot.pbot.program.text.pidor.ShortNamePidorText;
import by.kobyzau.tg.bot.pbot.service.BotService;
import by.kobyzau.tg.bot.pbot.service.HotPotatoesService;
import by.kobyzau.tg.bot.pbot.service.PidorService;
import by.kobyzau.tg.bot.pbot.tg.action.SendMessageBotAction;
import by.kobyzau.tg.bot.pbot.util.CollectionUtil;
import by.kobyzau.tg.bot.pbot.util.DateUtil;
import by.kobyzau.tg.bot.pbot.util.HotPotatoUtil;
import by.kobyzau.tg.bot.pbot.util.StringUtil;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

@Component
public class HotPotatoesCommandHandler implements CommandHandler {
  @Autowired private PidorService pidorService;
  @Autowired private HotPotatoesService hotPotatoesService;
  @Autowired BotActionCollector botActionCollector;
  @Autowired private HotPotatoUtil hotPotatoUtil;

  @Override
  public void processCommand(Message message, String text) {
    long chatId = message.getChatId();
    Optional<Pidor> lastTaker = hotPotatoesService.getLastTaker(DateUtil.now(), chatId);
    Optional<LocalDateTime> lastTakerDeadline =
        hotPotatoesService.getLastTakerDeadline(DateUtil.now(), chatId);
    InlineKeyboardMarkup inlineMarkup =
        InlineKeyboardMarkup.builder()
            .keyboardRow(
                Collections.singletonList(
                    InlineKeyboardButton.builder()
                        .text("Перекинуть картошечку")
                        .callbackData(StringUtil.serialize(new HotPotatoDto()))
                        .build()))
            .build();
    if (lastTaker.isPresent() && lastTakerDeadline.isPresent()) {
      botActionCollector.add(
          new SendMessageBotAction(
                  chatId,
                  new ParametizedText(
                      hotPotatoUtil.getPotatoesTakerMessage(),
                      new ShortNamePidorText(lastTaker.get()),
                      new BoldText(
                          new TimeLeftText(DateUtil.currentTime(), lastTakerDeadline.get()))))
              .withReplyMarkup(inlineMarkup));
    } else {
      List<Pidor> pidors = pidorService.getByChat(chatId);
      Pidor newPidor = CollectionUtil.getRandomValue(pidors);
      LocalDateTime newDeadline = hotPotatoesService.setNewTaker(newPidor);
      botActionCollector.add(
          new SendMessageBotAction(
                  chatId,
                  new ParametizedText(
                      hotPotatoUtil.getPotatoesTakerMessage(),
                      new FullNamePidorText(newPidor),
                      new BoldText(new TimeLeftText(DateUtil.currentTime(), newDeadline))))
              .withReplyMarkup(inlineMarkup));
    }
  }

  @Override
  public Command getCommand() {
    return Command.HOT_POTATOES;
  }
}
