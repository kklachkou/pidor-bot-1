package by.kobyzau.tg.bot.pbot.handlers.update;

import by.kobyzau.tg.bot.pbot.collectors.BotActionCollector;
import by.kobyzau.tg.bot.pbot.handlers.command.Command;
import by.kobyzau.tg.bot.pbot.handlers.command.ParsedCommand;
import by.kobyzau.tg.bot.pbot.handlers.command.handler.pidor.PidorFunnyAction;
import by.kobyzau.tg.bot.pbot.handlers.command.parser.CommandParser;
import by.kobyzau.tg.bot.pbot.handlers.update.schedule.CalendarSchedule;
import by.kobyzau.tg.bot.pbot.handlers.update.schedule.ScheduledItem;
import by.kobyzau.tg.bot.pbot.model.DailyPidor;
import by.kobyzau.tg.bot.pbot.model.Pidor;
import by.kobyzau.tg.bot.pbot.model.dto.AssassinInlineMessageDto;
import by.kobyzau.tg.bot.pbot.program.text.RandomText;
import by.kobyzau.tg.bot.pbot.repository.dailypidor.DailyPidorRepository;
import by.kobyzau.tg.bot.pbot.service.BotService;
import by.kobyzau.tg.bot.pbot.service.PidorService;
import by.kobyzau.tg.bot.pbot.tg.ChatAction;
import by.kobyzau.tg.bot.pbot.tg.action.EditMessageReplyMarkupBotAction;
import by.kobyzau.tg.bot.pbot.tg.sticker.StickerType;
import by.kobyzau.tg.bot.pbot.util.CollectionUtil;
import by.kobyzau.tg.bot.pbot.util.DateUtil;
import by.kobyzau.tg.bot.pbot.util.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.*;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.Executor;

import static by.kobyzau.tg.bot.pbot.handlers.update.UpdateHandler.ASSASSIN_ORDER;

@Component
@Order(ASSASSIN_ORDER)
public class AssassinReplyUpdateHandler implements UpdateHandler {

  @Autowired private CalendarSchedule calendarSchedule;
  @Autowired private List<PidorFunnyAction> pidorFunnyActions;
  @Autowired private CommandParser commandParser;
  @Autowired private BotActionCollector botActionCollector;
  @Autowired private PidorService pidorService;
  @Autowired private DailyPidorRepository dailyPidorRepository;
  @Autowired private BotService botService;

  @Autowired
  @Qualifier("cachedExecutor")
  private Executor executor;

  @Value("${bot.username}")
  private String botUserName;

  @Override
  public boolean handleUpdate(Update update) {
    CallbackQuery callbackQuery = update.getCallbackQuery();
    if (callbackQuery == null) {
      return false;
    }
    Message prevMessage = callbackQuery.getMessage();
    User calledUser = callbackQuery.getFrom();
    Optional<AssassinInlineMessageDto> data =
        StringUtil.deserialize(callbackQuery.getData(), AssassinInlineMessageDto.class);
    if (prevMessage == null
        || calledUser == null
        || prevMessage.getFrom() == null
        || !data.isPresent()
        || !Objects.equals(calledUser.getId(), data.get().getCalledUserId())
        || !botUserName.equalsIgnoreCase(prevMessage.getFrom().getUserName())) {
      return false;
    }
    long chatId = prevMessage.getChatId();
    removeInlineMessage(prevMessage);
    if (hasPidorOfTheDay(chatId)) {
      return true;
    }
    Optional<Pidor> targetPidor = pidorService.getPidor(chatId, data.get().getTargetUserId());
    Optional<Pidor> calledPidor = pidorService.getPidor(chatId, calledUser.getId());
    if (targetPidor.isPresent() && calledPidor.isPresent()) {
      handleSuck(chatId, targetPidor.get(), calledPidor.get(), prevMessage.getMessageId());
      return true;
    }
    return false;
  }

  private void handleSuck(long chatId, Pidor targetPidor, Pidor calledPidor, Integer replyMessage) {
    botActionCollector.wait(chatId,1, ChatAction.TYPING);
    if (suck()) {
      if (suckTarget()) {
        botActionCollector.text(
            chatId,
            new RandomText(
                "Заказ принят!",
                "Хорошо, так уж и быть. Заказ принят",
                "Спасибо, цель определена. А теперь подвинься, моя очередь",
                "Если только ты думаешь, что так будет правильно...Я беру этот заказ"),
            replyMessage);
        botActionCollector.wait(chatId, ChatAction.TYPING);
        makePidor(targetPidor);
      } else {
        botActionCollector.text(
            chatId,
            new RandomText(
                "Тебе не повезло, ведь пидором станет не он, а ты...",
                "Ты мне не нравишься, так что я лучше сделаю тебя пидором",
                "Это очень заманчиво, но я лучше сделаю пидором тебя",
                "Ты недостаточно заплатил, так что пидором будешь ты сам",
                "Извини, он мне заплатил в 2 раза больше, так что пидором будешь ты сам",
                "Произошёл рикошет. Извини, но пидором окажешься ты сам"),
            replyMessage);
        botActionCollector.wait(chatId, ChatAction.TYPING);
        botActionCollector.sticker(chatId, StickerType.LOL.getRandom());
        botActionCollector.wait(chatId, ChatAction.TYPING);
        makePidor(calledPidor);
      }
    } else {
      botActionCollector.text(
          chatId,
          new RandomText(
              "Я промазал", "Повезло, я промазал", "Я промахнулся", "Повезло, пока никто не пидор"),
          replyMessage);
      botActionCollector.wait(chatId, ChatAction.TYPING);
      botActionCollector.sticker(chatId, StickerType.SAD.getRandom());
      botActionCollector.text(
          chatId, new RandomText("Давай ещё раз", "Может повторим?", "Можно попробовать еще раз"));
    }
  }

  private boolean suck() {
    return CollectionUtil.getRandomValue(Arrays.asList(true, false));
  }

  private boolean suckTarget() {
    return CollectionUtil.getRandomValue(Arrays.asList(true, true, true, true, false));
  }

  private void makePidor(Pidor pidor) {
    saveDailyPidor(pidor);
    long chatId = pidor.getChatId();
    botService.unpinLastBotMessage(chatId);
    CollectionUtil.getRandomValue(pidorFunnyActions).processFunnyAction(chatId, pidor);
  }

  private Optional<Pidor> getPidorUser(Message message) {
    long chatId = message.getChatId();
    for (MessageEntity entity : message.getEntities()) {
      if (entity.getUser() != null) {
        Optional<Pidor> pidor = pidorService.getPidor(chatId, entity.getUser().getId());
        if (pidor.isPresent()) {
          return pidor;
        }
      } else if ("mention".equalsIgnoreCase(entity.getType())) {
        Optional<Pidor> pidor =
            pidorService.getByChat(chatId).stream()
                .filter(p -> p.getUsername() != null)
                .filter(p -> p.getUsername().equalsIgnoreCase(entity.getText()))
                .findFirst();
        ;
        if (pidor.isPresent()) {
          return pidor;
        }
      }
    }
    ParsedCommand parsedCommand = commandParser.parseCommand(message.getText());
    String username = parsedCommand.getText();
    if (StringUtil.isNotBlank(username)) {
      final String formattedUsername = username.trim().replaceAll("@", "");
      return pidorService.getByChat(chatId).stream()
          .filter(p -> p.getUsername() != null)
          .filter(p -> formattedUsername.equalsIgnoreCase(p.getUsername()))
          .findFirst();
    }

    return Optional.empty();
  }

  private Optional<Command> getCommand(Update update) {
    if (!update.hasMessage()) {
      return Optional.empty();
    }
    Message message = update.getMessage();
    if (message == null) {
      return Optional.empty();
    }
    if (!message.hasText()) {
      return Optional.empty();
    }
    ParsedCommand parsedCommand = commandParser.parseCommand(message.getText());
    return Optional.ofNullable(parsedCommand).map(ParsedCommand::getCommand);
  }

  private void saveDailyPidor(Pidor pidor) {
    DailyPidor dailyPidor = new DailyPidor();
    dailyPidor.setChatId(pidor.getChatId());
    dailyPidor.setPlayerTgId(pidor.getTgId());
    dailyPidor.setLocalDate(DateUtil.now());
    dailyPidorRepository.create(dailyPidor);
  }

  private boolean hasPidorOfTheDay(long chatId) {
    return dailyPidorRepository.getByChatAndDate(chatId, DateUtil.now()).isPresent();
  }

  private void removeInlineMessage(Message message) {
    botActionCollector.add(
        new EditMessageReplyMarkupBotAction(
            message.getChatId(),
            message.getMessageId(),
            InlineKeyboardMarkup.builder().clearKeyboard().build()));
  }

  @Override
  public boolean test(LocalDate localDate) {
    return calendarSchedule.getItem(localDate) == ScheduledItem.ASSASSIN;
  }
}
