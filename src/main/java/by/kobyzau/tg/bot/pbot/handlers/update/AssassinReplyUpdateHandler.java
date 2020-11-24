package by.kobyzau.tg.bot.pbot.handlers.update;

import by.kobyzau.tg.bot.pbot.bots.game.EmojiGameType;
import by.kobyzau.tg.bot.pbot.collectors.BotActionCollector;
import by.kobyzau.tg.bot.pbot.handlers.command.handler.pidor.PidorFunnyAction;
import by.kobyzau.tg.bot.pbot.handlers.update.schedule.CalendarSchedule;
import by.kobyzau.tg.bot.pbot.handlers.update.schedule.ScheduledItem;
import by.kobyzau.tg.bot.pbot.model.DailyPidor;
import by.kobyzau.tg.bot.pbot.model.Pidor;
import by.kobyzau.tg.bot.pbot.model.dto.AssassinInlineMessageDto;
import by.kobyzau.tg.bot.pbot.program.logger.Logger;
import by.kobyzau.tg.bot.pbot.program.selection.ConsistentSelection;
import by.kobyzau.tg.bot.pbot.program.selection.Selection;
import by.kobyzau.tg.bot.pbot.program.text.RandomText;
import by.kobyzau.tg.bot.pbot.program.text.SimpleText;
import by.kobyzau.tg.bot.pbot.program.text.Text;
import by.kobyzau.tg.bot.pbot.repository.dailypidor.DailyPidorRepository;
import by.kobyzau.tg.bot.pbot.service.BotService;
import by.kobyzau.tg.bot.pbot.service.PidorService;
import by.kobyzau.tg.bot.pbot.tg.ChatAction;
import by.kobyzau.tg.bot.pbot.tg.action.DicePostActionWrapperBotAction;
import by.kobyzau.tg.bot.pbot.tg.action.EditMessageBotAction;
import by.kobyzau.tg.bot.pbot.tg.sticker.StickerType;
import by.kobyzau.tg.bot.pbot.util.CollectionUtil;
import by.kobyzau.tg.bot.pbot.util.DateUtil;
import by.kobyzau.tg.bot.pbot.util.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;

import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.Executor;

import static by.kobyzau.tg.bot.pbot.handlers.update.UpdateHandler.ASSASSIN_ORDER;

@Component
@Order(ASSASSIN_ORDER)
public class AssassinReplyUpdateHandler implements UpdateHandler {

  @Autowired private CalendarSchedule calendarSchedule;
  @Autowired private List<PidorFunnyAction> pidorFunnyActions;
  @Autowired private BotActionCollector botActionCollector;
  @Autowired private PidorService pidorService;
  @Autowired private DailyPidorRepository dailyPidorRepository;
  @Autowired private Logger logger;
  @Autowired private BotService botService;

  @Autowired
  @Qualifier("cachedExecutor")
  private Executor executor;

  @Value("${bot.username}")
  private String botUserName;

  private final Selection<Text> emoji =
      new ConsistentSelection<>("\uD83D\uDC7B", "\uD83D\uDCA9", "\uD83D\uDC80", "\uD83C\uDF83")
          .map(SimpleText::new);

  private final Set<String> handledRequests = new HashSet<>();

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
        || !Objects.equals(calledUser.getId(), data.get().getcId())
        || handledRequests.contains(data.get().getId())
        || !botUserName.equalsIgnoreCase(prevMessage.getFrom().getUserName())) {
      return false;
    }
    handledRequests.add(data.get().getId());
    long chatId = prevMessage.getChatId();
    removeInlineMessage(prevMessage);
    if (hasPidorOfTheDay(chatId)) {
      return true;
    }
    Optional<Pidor> targetPidor = pidorService.getPidor(chatId, data.get().gettId());
    Optional<Pidor> calledPidor = pidorService.getPidor(chatId, calledUser.getId());
    if (targetPidor.isPresent() && calledPidor.isPresent()) {
      sendDice(chatId, targetPidor.get(), calledPidor.get(), prevMessage.getMessageId());
      try {
        Thread.sleep(1000);
      } catch (InterruptedException e) {
        logger.error("Cannot sleep", e);
      }
      return true;
    }
    return false;
  }

  private void sendDice(long chatId, Pidor targetPidor, Pidor calledPidor, Integer replyMessage) {
    botActionCollector.wait(chatId, 1, ChatAction.TYPING);
    botActionCollector.add(
        new DicePostActionWrapperBotAction(
            chatId,
            EmojiGameType.DICE,
            dice -> handleSuck(chatId, targetPidor, calledPidor, replyMessage, dice)));
  }

  private void handleSuck(
      long chatId, Pidor targetPidor, Pidor calledPidor, Integer replyMessage, int dice) {
    logger.debug("Assassin dice is " + dice);
    botActionCollector.wait(chatId, 2, ChatAction.TYPING);
    if (dice <= 3) {
      botActionCollector.text(
          chatId,
          new RandomText(
              "Ну, как-нибудь в другой раз",
              "Значит пока никто не пидор",
              "Неа, не вышло",
              "Видать боги не твоей стороне",
              "Лол, давай в другой раз",
              "Повезло, пока никто не пидор"),
          replyMessage);
      botActionCollector.wait(chatId, 1, ChatAction.TYPING);
      botActionCollector.sticker(chatId, StickerType.SAD.getRandom());
      return;
    }
    if (hasPidorOfTheDay(chatId)) {
      return;
    }
    if (dice == 6) {
      botActionCollector.text(
          chatId,
          new RandomText(
              "Тебе не повезло, ведь пидором станет не он, а ты...",
              "Судя по кубику, пидором будет не он, а ты...",
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
    } else {
      botActionCollector.text(
          chatId,
          new RandomText(
              "Иисус на твоей стороне, поехали!",
              "Хорошо, так уж и быть. Заказ принят",
              "Спасибо, цель определена. А теперь подвинься, моя очередь",
              "Великий Кубик сказал, что быть ему пидором, так уж и быть!"),
          replyMessage);
      botActionCollector.wait(chatId, ChatAction.TYPING);
      makePidor(targetPidor);
    }
  }

  private void makePidor(Pidor pidor) {
    saveDailyPidor(pidor);
    long chatId = pidor.getChatId();
    botService.unpinLastBotMessage(chatId);
    CollectionUtil.getRandomValue(pidorFunnyActions).processFunnyAction(chatId, pidor);
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
    botActionCollector.add(new EditMessageBotAction(message, emoji.next()));
  }

  @Override
  public boolean test(LocalDate localDate) {
    return calendarSchedule.getItem(localDate) == ScheduledItem.ASSASSIN;
  }
}
