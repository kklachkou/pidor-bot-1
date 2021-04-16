package by.kobyzau.tg.bot.pbot.program.backup.v2;

import by.kobyzau.tg.bot.pbot.model.Feedback;
import by.kobyzau.tg.bot.pbot.model.FeedbackEmojiType;
import by.kobyzau.tg.bot.pbot.model.FeedbackType;
import by.kobyzau.tg.bot.pbot.repository.feedback.FeedbackRepository;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class FeedbackBackupProcessorV2 implements BackupProcessorV2 {

  private static final String ID_KEY = "ID";
  private static final String CHAT_KEY = "C";
  private static final String MESSAGE_KEY = "M";
  private static final String USER_KEY = "U";
  private static final String EMOJI_KEY = "E";
  private static final String TYPE_KEY = "T";

  @Autowired private FeedbackRepository feedbackRepository;

  @Override
  public String getType() {
    return "FEEDBACK";
  }

  @Override
  public JSONArray getData() {
    JSONArray allData = new JSONArray();
    feedbackRepository.getAll().stream().map(this::toJson).forEach(allData::put);
    return allData;
  }

  private JSONObject toJson(Feedback feedback) {
    JSONObject json = new JSONObject();
    json.put(ID_KEY, feedback.getId());
    json.put(CHAT_KEY, feedback.getChatId());
    json.put(MESSAGE_KEY, feedback.getMessageId());
    json.put(USER_KEY, feedback.getPlayerTgId());
    json.put(EMOJI_KEY, feedback.getEmojiType());
    json.put(TYPE_KEY, feedback.getFeedbackType());
    return json;
  }

  @Override
  public void restoreFromBackup(JSONArray jsonArray) {
    int length = jsonArray.length();

    for (int i = 0; i < length; i++) {
      JSONObject pidorData = jsonArray.getJSONObject(i);
      if (!pidorData.has(CHAT_KEY)
          || !pidorData.has(MESSAGE_KEY)
          || !pidorData.has(ID_KEY)
          || !pidorData.has(USER_KEY)
          || !pidorData.has(EMOJI_KEY)
          || !pidorData.has(TYPE_KEY)) {
        continue;
      }
      long id = pidorData.getLong(ID_KEY);
      long chatId = pidorData.getLong(CHAT_KEY);
      int messageId = pidorData.getInt(MESSAGE_KEY);
      long userId = pidorData.getLong(USER_KEY);
      FeedbackEmojiType feedbackEmojiType =
          FeedbackEmojiType.valueOf(pidorData.getString(EMOJI_KEY));
      FeedbackType feedbackType = FeedbackType.valueOf(pidorData.getString(TYPE_KEY));

      if (feedbackRepository.get(id) == null) {
        Feedback feedback = new Feedback();
        feedback.setChatId(chatId);
        feedback.setMessageId(messageId);
        feedback.setPlayerTgId(userId);
        feedback.setEmojiType(feedbackEmojiType);
        feedback.setFeedbackType(feedbackType);
        feedbackRepository.create(feedback);
      }
    }
  }
}
