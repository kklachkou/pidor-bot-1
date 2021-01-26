package by.kobyzau.tg.bot.pbot.handlers.stat.impl;

import by.kobyzau.tg.bot.pbot.collectors.BotActionCollector;
import by.kobyzau.tg.bot.pbot.handlers.stat.StatHandler;
import by.kobyzau.tg.bot.pbot.model.StatType;
import by.kobyzau.tg.bot.pbot.program.text.IntText;
import by.kobyzau.tg.bot.pbot.program.text.ParametizedText;
import by.kobyzau.tg.bot.pbot.repository.pidor.PidorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class PidorsCountStatHandler implements StatHandler {

  @Autowired private BotActionCollector botActionCollector;

  @Autowired private PidorRepository pidorRepository;

  @Override
  public void printStat(long chatId) {
    botActionCollector.text(
        chatId, new ParametizedText("{0} пидоров", new IntText(pidorRepository.getAll().size())));
  }

  @Override
  public StatType getType() {
    return StatType.PIDOR_COUNT;
  }
}
