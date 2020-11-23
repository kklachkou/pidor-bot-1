package by.kobyzau.tg.bot.pbot.handlers.command.handler;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.WeakHashMap;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.User;

import by.kobyzau.tg.bot.pbot.collectors.BotActionCollector;
import by.kobyzau.tg.bot.pbot.handlers.command.Command;
import by.kobyzau.tg.bot.pbot.model.DailyPidor;
import by.kobyzau.tg.bot.pbot.model.Pidor;
import by.kobyzau.tg.bot.pbot.program.text.IntText;
import by.kobyzau.tg.bot.pbot.program.text.NewLineText;
import by.kobyzau.tg.bot.pbot.program.text.SimpleText;
import by.kobyzau.tg.bot.pbot.program.text.Text;
import by.kobyzau.tg.bot.pbot.program.text.TextBuilder;
import by.kobyzau.tg.bot.pbot.program.text.collector.ToTextCollector;
import by.kobyzau.tg.bot.pbot.program.text.pidor.FullNamePidorText;
import by.kobyzau.tg.bot.pbot.repository.dailypidor.DailyPidorRepository;
import by.kobyzau.tg.bot.pbot.service.PidorService;
import by.kobyzau.tg.bot.pbot.tg.ChatAction;
import by.kobyzau.tg.bot.pbot.util.CollectionUtil;

@Component
public class CallerCommandHandler implements CommandHandler {

  @Autowired private BotActionCollector botActionCollector;
  @Autowired private DailyPidorRepository dailyPidorRepository;
  @Autowired private PidorService pidorService;

  @Override
  public void processCommand(Message message, String text) {
    long chatId = message.getChatId();
    botActionCollector.text(chatId, new SimpleText("Определяю кто чаще всего призывал меня..."));
    botActionCollector.wait(chatId,  ChatAction.TYPING);

    PidorService cachedPidorService = new CachedPidorService(pidorService);
    List<Pidor> pidors =
        dailyPidorRepository.getByChat(chatId).stream()
            .map(DailyPidor::getCaller)
            .filter(Objects::nonNull)
            .map(id -> cachedPidorService.getPidor(chatId, id))
            .filter(Optional::isPresent)
            .map(Optional::get)
            .collect(Collectors.toList());
    if (CollectionUtil.isEmpty(pidors)) {
      botActionCollector.text(chatId, new SimpleText("Никто еще не вызывал меня"));
      return;
    }
    Map<Pidor, Integer> callers = new HashMap<>();
    for (Pidor pidor : pidors) {
      callers.put(pidor, callers.getOrDefault(pidor, 0) + 1);
    }
    Text info =
        callers.entrySet().stream()
            .sorted(Collections.reverseOrder(Map.Entry.comparingByValue()))
            .map(this::toInfo)
            .collect(new ToTextCollector(new NewLineText()));
    botActionCollector.text(chatId, new SimpleText("Вот список:"));
    botActionCollector.wait(chatId,  ChatAction.TYPING);
    botActionCollector.text(chatId, info);
  }

  private Text toInfo(Map.Entry<Pidor, Integer> info) {
    TextBuilder tb = new TextBuilder();
    tb.append(new SimpleText("\u26A1 "));
    tb.append(new FullNamePidorText(info.getKey()));
    tb.append(new SimpleText(" - "));
    tb.append(new IntText(info.getValue()));
    return tb;
  }

  @Override
  public Command getCommand() {
    return Command.CALLER;
  }

  private static class CachedPidorService implements PidorService {

    private final PidorService pidorService;
    private final Map<Integer, Optional<Pidor>> cache;

    public CachedPidorService(PidorService pidorService) {
      this.pidorService = pidorService;
      this.cache = new WeakHashMap<>();
    }

    @Override
    public Optional<Pidor> getPidor(long chatId, int userId) {
      Optional<Pidor> cached = cache.getOrDefault(userId, Optional.empty());
      if (cached.isPresent()) {
        return cached;
      }
      Optional<Pidor> pidor = pidorService.getPidor(chatId, userId);
      cache.put(userId, pidor);
      return pidor;
    }

    @Override
    public Pidor createPidor(long chatId, User user) {
      return pidorService.createPidor(chatId, user);
    }

    @Override
    public void updatePidor(Pidor pidor) {
      pidorService.updatePidor(pidor);
    }

    @Override
    public List<Pidor> getByChat(long chatId) {
      return pidorService.getByChat(chatId);
    }
  }
}
