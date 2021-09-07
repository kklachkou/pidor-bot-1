package by.kobyzau.tg.bot.pbot.handlers.update.impl.callback;

import by.kobyzau.tg.bot.pbot.collectors.BotActionCollector;
import by.kobyzau.tg.bot.pbot.handlers.update.CallbackUpdateHandler;
import by.kobyzau.tg.bot.pbot.model.Pidor;
import by.kobyzau.tg.bot.pbot.model.dto.KillChatDto;
import by.kobyzau.tg.bot.pbot.model.dto.SerializableInlineType;
import by.kobyzau.tg.bot.pbot.program.text.LongText;
import by.kobyzau.tg.bot.pbot.program.text.ParametizedText;
import by.kobyzau.tg.bot.pbot.repository.pidor.PidorRepository;
import by.kobyzau.tg.bot.pbot.tg.action.EditMessageBotAction;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Update;

@Component
@RequiredArgsConstructor
public class KillChatUpdateHandler extends CallbackUpdateHandler<KillChatDto> {

  private final PidorRepository pidorRepository;
  private final BotActionCollector botActionCollector;

  @Override
  protected Class<KillChatDto> getDtoType() {
    return KillChatDto.class;
  }

  @Override
  protected SerializableInlineType getSerializableType() {
    return SerializableInlineType.KILL_CHAT;
  }

  @Override
  protected void handleCallback(Update update, KillChatDto dto) {
    pidorRepository.getByChat(dto.getChatId()).stream()
        .map(Pidor::getId)
        .forEach(pidorRepository::delete);
    CallbackQuery callbackQuery = update.getCallbackQuery();
    botActionCollector.add(
        new EditMessageBotAction(
            callbackQuery.getMessage(),
            new ParametizedText("Чат {0} удален", new LongText(dto.getChatId()))));
  }
}
