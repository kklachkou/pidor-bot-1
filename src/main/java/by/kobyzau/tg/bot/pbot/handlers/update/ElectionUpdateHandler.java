package by.kobyzau.tg.bot.pbot.handlers.update;

import by.kobyzau.tg.bot.pbot.bots.game.election.ElectionFinalizer;
import by.kobyzau.tg.bot.pbot.collectors.BotActionCollector;
import by.kobyzau.tg.bot.pbot.model.Pidor;
import by.kobyzau.tg.bot.pbot.model.dto.VoteInlineMessageDto;
import by.kobyzau.tg.bot.pbot.program.selection.ConsistentSelection;
import by.kobyzau.tg.bot.pbot.program.selection.Selection;
import by.kobyzau.tg.bot.pbot.program.text.SimpleText;
import by.kobyzau.tg.bot.pbot.program.text.Text;
import by.kobyzau.tg.bot.pbot.repository.dailypidor.DailyPidorRepository;
import by.kobyzau.tg.bot.pbot.service.ElectionService;
import by.kobyzau.tg.bot.pbot.service.PidorService;
import by.kobyzau.tg.bot.pbot.tg.action.EditMessageBotAction;
import by.kobyzau.tg.bot.pbot.util.DateUtil;
import by.kobyzau.tg.bot.pbot.util.ElectionStatPrinter;
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
import java.util.HashSet;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.Executor;

@Component
@Order(UpdateHandler.ELECTION_ORDER)
public class ElectionUpdateHandler implements UpdateHandler {

  @Autowired private ElectionService electionService;
  @Autowired private BotActionCollector botActionCollector;
  @Autowired private PidorService pidorService;
  @Autowired private DailyPidorRepository dailyPidorRepository;
  @Autowired private ElectionFinalizer electionFinalizer;
  @Autowired private ElectionStatPrinter electionStatPrinter;

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
    Optional<VoteInlineMessageDto> data =
        StringUtil.deserialize(callbackQuery.getData(), VoteInlineMessageDto.class);
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
      electionService.saveVote(chatId, calledPidor.get().getTgId(), targetPidor.get().getTgId());
      botActionCollector.text(
          chatId, new SimpleText("Твой голос засчитан"), prevMessage.getMessageId());
      if (needToFinalize(chatId)) {
        executor.execute(() -> electionFinalizer.finalize(chatId));
      } else {
        electionStatPrinter.printInfo(chatId, true);
      }
      return true;
    }
    return true;
  }

  private boolean hasPidorOfTheDay(long chatId) {
    return dailyPidorRepository.getByChatAndDate(chatId, DateUtil.now()).isPresent();
  }

  private void removeInlineMessage(Message message) {
    botActionCollector.add(new EditMessageBotAction(message, emoji.next()));
  }

  private boolean needToFinalize(long chatId) {
    LocalDate now = DateUtil.now();
    long pidorNumber = pidorService.getByChat(chatId).size();
    int numPidorsToPlay = electionService.getNumToVote(chatId);
    long numVotes = electionService.getNumVotes(chatId, now);
    return (pidorNumber == numVotes || numVotes >= numPidorsToPlay) && !hasPidorOfTheDay(chatId);
  }

  @Override
  public boolean test(LocalDate localDate) {
    return electionService.isElectionDay(localDate);
  }
}
