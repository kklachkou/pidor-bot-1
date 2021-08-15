package by.kobyzau.tg.bot.pbot.handlers.stat.impl;

import by.kobyzau.tg.bot.pbot.collectors.BotActionCollector;
import by.kobyzau.tg.bot.pbot.handlers.stat.StatHandler;
import by.kobyzau.tg.bot.pbot.model.QuestionnaireAnswer;
import by.kobyzau.tg.bot.pbot.model.QuestionnaireType;
import by.kobyzau.tg.bot.pbot.model.StatType;
import by.kobyzau.tg.bot.pbot.program.text.IntText;
import by.kobyzau.tg.bot.pbot.program.text.LongText;
import by.kobyzau.tg.bot.pbot.program.text.NewLineText;
import by.kobyzau.tg.bot.pbot.program.text.ParametizedText;
import by.kobyzau.tg.bot.pbot.program.text.SimpleText;
import by.kobyzau.tg.bot.pbot.program.text.SpaceText;
import by.kobyzau.tg.bot.pbot.program.text.TextBuilder;
import by.kobyzau.tg.bot.pbot.repository.questionnaire.QuestionnaireAnswerRepository;
import by.kobyzau.tg.bot.pbot.tg.ChatAction;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class QuestionnaireStatHandler implements StatHandler {
  @Autowired private BotActionCollector botActionCollector;
  @Autowired private QuestionnaireAnswerRepository repository;

  @Override
  public void printStat(long chatId) {
    for (QuestionnaireType type : QuestionnaireType.values()) {
      printStat(chatId, type);
    }
  }

  private void printStat(long chatId, QuestionnaireType type) {
    botActionCollector.wait(chatId, 1, ChatAction.TYPING);
    List<QuestionnaireAnswer> answers =
        repository.getAll().stream().filter(a -> a.getType() == type).collect(Collectors.toList());
    TextBuilder tx = new TextBuilder();
    tx.append(new SimpleText(type.getName())).append(new NewLineText());
    List<String> options = type.getOptions();
    for (int i = 0; i < options.size(); i++) {
      final int optionId = i;
      long count = answers.stream().filter(a -> a.getOption() == optionId).count();
      String option = options.get(i);
      tx.append(new NewLineText());
      tx.append(new SimpleText(option))
          .append(new SpaceText())
          .append(new LongText(count))
          .append(new ParametizedText(" ({0}%)", new IntText((int) (100 * count / answers.size()))));
    }

    botActionCollector.text(chatId, tx);
  }

  @Override
  public StatType getType() {
    return StatType.QUESTIONNAIRE;
  }
}
