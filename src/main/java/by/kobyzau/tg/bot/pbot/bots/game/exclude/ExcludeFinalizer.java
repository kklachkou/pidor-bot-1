package by.kobyzau.tg.bot.pbot.bots.game.exclude;

import by.kobyzau.tg.bot.pbot.artifacts.ArtifactType;
import by.kobyzau.tg.bot.pbot.artifacts.service.UserArtifactService;
import by.kobyzau.tg.bot.pbot.collectors.BotActionCollector;
import by.kobyzau.tg.bot.pbot.handlers.command.handler.pidor.PidorFunnyAction;
import by.kobyzau.tg.bot.pbot.model.DailyPidor;
import by.kobyzau.tg.bot.pbot.model.ExcludeGameUserValue;
import by.kobyzau.tg.bot.pbot.model.Pair;
import by.kobyzau.tg.bot.pbot.model.Pidor;
import by.kobyzau.tg.bot.pbot.program.selection.PrioritySelection;
import by.kobyzau.tg.bot.pbot.program.selection.Selection;
import by.kobyzau.tg.bot.pbot.program.text.IntText;
import by.kobyzau.tg.bot.pbot.program.text.ParametizedText;
import by.kobyzau.tg.bot.pbot.program.text.SimpleText;
import by.kobyzau.tg.bot.pbot.repository.dailypidor.DailyPidorRepository;
import by.kobyzau.tg.bot.pbot.service.BotService;
import by.kobyzau.tg.bot.pbot.service.ExcludeGameService;
import by.kobyzau.tg.bot.pbot.service.PidorService;
import by.kobyzau.tg.bot.pbot.tg.ChatAction;
import by.kobyzau.tg.bot.pbot.tg.action.SendMessageBotAction;
import by.kobyzau.tg.bot.pbot.util.CollectionUtil;
import by.kobyzau.tg.bot.pbot.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardRemove;

import javax.annotation.PostConstruct;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class ExcludeFinalizer {

  @Autowired private ExcludeGameService gameService;

  @Autowired private PidorService pidorService;
  @Autowired private DailyPidorRepository dailyPidorRepository;
  @Autowired private BotActionCollector botActionCollector;
  @Autowired private BotService botService;
  @Autowired private UserArtifactService userArtifactService;

  @Autowired private List<PidorFunnyAction> allPidorFunnyActions;

  private Selection<PidorFunnyAction> pidorFunnyActions;

  @PostConstruct
  private void init() {
    pidorFunnyActions =
        new PrioritySelection<>(
            allPidorFunnyActions.stream()
                .map(p -> new Pair<>(p, p.getPriority()))
                .collect(Collectors.toList()));
  }

  public void finalize(long chatId) {
    userArtifactService.clearUserArtifacts(chatId, ArtifactType.SILENCE);
    LocalDate now = DateUtil.now();
    if (dailyPidorRepository.getByChatAndDate(chatId, now).isPresent()) {
      return;
    }

    botActionCollector.add(
        new SendMessageBotAction(chatId, new SimpleText("Игра завершена!"))
            .withReplyMarkup(ReplyKeyboardRemove.builder().removeKeyboard(true).build()));
    botActionCollector.wait(chatId, ChatAction.TYPING);
    Set<Long> playedIds =
        gameService.getExcludeGameUserValues(chatId, now).stream()
            .map(ExcludeGameUserValue::getPlayerTgId)
            .collect(Collectors.toSet());
    List<Pidor> notPlayedPidors =
        pidorService.getByChat(chatId).stream()
            .filter(p -> !playedIds.contains(p.getTgId()))
            .filter(
                p ->
                    !userArtifactService
                        .getUserArtifact(chatId, p.getTgId(), ArtifactType.ANTI_PIDOR)
                        .isPresent())
            .collect(Collectors.toList());
    if (notPlayedPidors.isEmpty()) {
      botActionCollector.text(
          chatId, new SimpleText("Хмм, в этот раз придется выбрать пидора случайно..."));
      botActionCollector.wait(chatId, ChatAction.TYPING);
      processLastPidor(
          CollectionUtil.getRandomValue(
              pidorService.getByChat(chatId).stream()
                  .filter(
                      p ->
                          !userArtifactService
                              .getUserArtifact(chatId, p.getTgId(), ArtifactType.ANTI_PIDOR)
                              .isPresent())
                  .collect(Collectors.toList())));
    } else {
      botActionCollector.text(
          chatId,
          new ParametizedText(
              "{0} людей обладают крайне медлительной реакцией",
              new IntText(notPlayedPidors.size())));
      botActionCollector.wait(chatId, ChatAction.TYPING);
      botActionCollector.text(chatId, new SimpleText("Думаю кого-то из них нужно наказать"));
      processLastPidor(CollectionUtil.getRandomValue(notPlayedPidors));
    }
  }

  private void processLastPidor(Pidor pidor) {
    saveDailyPidor(pidor);
    botService.unpinLastBotMessage(pidor.getChatId());
    pidorFunnyActions.next().processFunnyAction(pidor.getChatId(), pidor);
  }

  private void saveDailyPidor(Pidor pidor) {
    DailyPidor dailyPidor = new DailyPidor();
    dailyPidor.setChatId(pidor.getChatId());
    dailyPidor.setPlayerTgId(pidor.getTgId());
    dailyPidor.setLocalDate(DateUtil.now());
    dailyPidorRepository.create(dailyPidor);
  }
}
