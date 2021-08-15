package by.kobyzau.tg.bot.pbot.service;

import by.kobyzau.tg.bot.pbot.model.Feedback;
import by.kobyzau.tg.bot.pbot.model.QuestionnaireAnswer;
import by.kobyzau.tg.bot.pbot.model.QuestionnaireType;
import java.util.List;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

public interface QuestionnaireService {

  void addUniqueAnswer(QuestionnaireAnswer answer);

  List<QuestionnaireAnswer> getAnswers(long chatId);
}
