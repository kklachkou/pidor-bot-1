package by.kobyzau.tg.bot.pbot.repository.questionnaire;

import by.kobyzau.tg.bot.pbot.model.QuestionnaireAnswer;
import by.kobyzau.tg.bot.pbot.model.QuestionnaireAnswer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
//@Profile({"dev", "integration-test"})
public class HashMapQuestionnaireAnswerRepository implements QuestionnaireAnswerRepository {

    private final Map<Long, QuestionnaireAnswer> map = new HashMap<>();


    @Override
    public List<QuestionnaireAnswer> getByChat(long chatId) {
        return getAll().stream().filter(p -> p.getChatId() == chatId).collect(Collectors.toList());
    }

    @Override
    public long create(QuestionnaireAnswer obj) {
        long newId = getNewId();
        obj.setId(newId);
        map.put(newId, obj);
        return newId;
    }

    @Override
    public void update(QuestionnaireAnswer obj) {
        map.put(obj.getId(), obj);
    }

    @Override
    public QuestionnaireAnswer get(long id) {
        return map.get(id);
    }

    @Override
    public List<QuestionnaireAnswer> getAll() {
        return map.values().stream().filter(Objects::nonNull).collect(Collectors.toList());
    }


    @Override
    public void delete(long id) {
        map.remove(id);
    }

    private synchronized long getNewId() {
        long newId = map.keySet().stream().max(Long::compareTo).orElse(1L) + 1;
        map.put(newId, null);
        return newId;
    }
}
