package by.kobyzau.tg.bot.pbot.handlers.update.impl.validate;

import by.kobyzau.tg.bot.pbot.collectors.BotActionCollector;
import by.kobyzau.tg.bot.pbot.handlers.command.Command;
import by.kobyzau.tg.bot.pbot.handlers.command.ParsedCommand;
import by.kobyzau.tg.bot.pbot.handlers.command.handler.CommandHandlerFactory;
import by.kobyzau.tg.bot.pbot.handlers.command.parser.CommandParser;
import by.kobyzau.tg.bot.pbot.handlers.update.UpdateHandler;
import by.kobyzau.tg.bot.pbot.handlers.update.UpdateHandlerStage;
import by.kobyzau.tg.bot.pbot.program.logger.Logger;
import by.kobyzau.tg.bot.pbot.program.text.RandomText;
import by.kobyzau.tg.bot.pbot.tg.sticker.StickerType;
import by.kobyzau.tg.bot.pbot.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@Profile("prod")
public class SpamUpdateHandler implements UpdateHandler {

  private static final int LIMIT_PER_FEW_MINUTES = 4;
  private static final int LIMIT_PER_FEW_SECONDS = 1;
  @Autowired private Logger logger;

  @Autowired private CommandHandlerFactory commandHandlerFactory;

  @Autowired private CommandParser commandParser;

  @Autowired private BotActionCollector botActionCollector;

  @Value("${app.admin.userId}")
  private long adminUserId;

  private final Map<Key, List<LocalDateTime>> records = new HashMap<>();

  @Override
  public UpdateHandlerStage getStage() {
    return UpdateHandlerStage.VALIDATE;
  }

  @Override
  public boolean handleUpdate(Update update) {
    Command command = parseCommand(update);
    if (command == Command.NONE) {
      return false;
    }
    long chatId = update.getMessage().getChatId();
    if (chatId == adminUserId) {
      return false;
    }
    long userId = update.getMessage().getFrom().getId();
    Key key = new Key(chatId, userId, command);
    if (isSpam(key)) {
      botActionCollector.typing(chatId);
      logger.debug("\uD83D\uDCEC Spam is detected: " + key);
      botActionCollector.text(
          chatId, new RandomText("???????????? ??????????????", "???????????? ??????????????", "???? ?????? ??????????"));
      botActionCollector.sticker(chatId, StickerType.SPAM.getRandom());
      return true;
    } else {
      recordCommand(key);
      cleanOldRecords(key);
    }
    return false;
  }

  private void recordCommand(Key key) {
    List<LocalDateTime> list = records.getOrDefault(key, new ArrayList<>());
    list.add(DateUtil.currentTime());
    records.put(key, list);
  }

  private void cleanOldRecords(Key key) {
    LocalDateTime fromFewMinutes = DateUtil.currentTime().minusMinutes(10);
    List<LocalDateTime> list =
        records.getOrDefault(key, new ArrayList<>()).stream()
            .filter(fromFewMinutes::isBefore)
            .collect(Collectors.toList());
    list.add(DateUtil.currentTime());
    records.put(key, list);
  }

  private boolean isSpam(Key key) {
    List<LocalDateTime> timeList = records.getOrDefault(key, Collections.emptyList());

    LocalDateTime fromFewMinutes = DateUtil.currentTime().minusMinutes(1);
    LocalDateTime fromFewSeconds = DateUtil.currentTime().minusSeconds(10);

    boolean spamInSeconds =
        timeList.stream().filter(fromFewSeconds::isBefore).count() > LIMIT_PER_FEW_SECONDS;
    boolean spamInMinutes =
        timeList.stream().filter(fromFewMinutes::isBefore).count() > LIMIT_PER_FEW_MINUTES;
    return spamInMinutes || spamInSeconds;
  }

  private Command parseCommand(Update update) {
    return Optional.of(update)
        .map(Update::getMessage)
        .map(Message::getText)
        .map(commandParser::parseCommand)
        .map(ParsedCommand::getCommand)
        .orElse(Command.NONE);
  }

  private static class Key {
    private final long chatId;
    private final long userId;
    private final Command command;

    public Key(long chatId, long userId, Command command) {
      this.chatId = chatId;
      this.userId = userId;
      this.command = command;
    }

    public Command getCommand() {
      return command;
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;
      Key key = (Key) o;
      return chatId == key.chatId && userId == key.userId && command == key.command;
    }

    @Override
    public int hashCode() {
      return Objects.hash(chatId, userId, command);
    }

    @Override
    public String toString() {
      return "Key{" + "chatId=" + chatId + ", userId=" + userId + ", command=" + command + '}';
    }
  }
}
