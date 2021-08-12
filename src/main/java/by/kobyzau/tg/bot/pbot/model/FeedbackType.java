package by.kobyzau.tg.bot.pbot.model;

import java.util.Arrays;
import java.util.List;

import static by.kobyzau.tg.bot.pbot.model.FeedbackEmojiType.COCK;
import static by.kobyzau.tg.bot.pbot.model.FeedbackEmojiType.CRY;
import static by.kobyzau.tg.bot.pbot.model.FeedbackEmojiType.DISLIKE;
import static by.kobyzau.tg.bot.pbot.model.FeedbackEmojiType.EGGPLANT;
import static by.kobyzau.tg.bot.pbot.model.FeedbackEmojiType.LAUGH;
import static by.kobyzau.tg.bot.pbot.model.FeedbackEmojiType.LIKE;
import static by.kobyzau.tg.bot.pbot.model.FeedbackEmojiType.LOVE;

public enum FeedbackType {
  PIDOR(Arrays.asList(LAUGH, LOVE, CRY, EGGPLANT, COCK)),
  VERSION(Arrays.asList(LIKE, DISLIKE, LOVE, EGGPLANT));

  private final List<FeedbackEmojiType> emojiTypeList;

  FeedbackType(List<FeedbackEmojiType> emojiTypeList) {
    this.emojiTypeList = emojiTypeList;
  }

  public List<FeedbackEmojiType> getEmojiTypeList() {
    return emojiTypeList;
  }
}
