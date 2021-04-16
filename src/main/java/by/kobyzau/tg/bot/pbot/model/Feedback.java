package by.kobyzau.tg.bot.pbot.model;


import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "feedback")
public class Feedback {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private long chatId;
    private long playerTgId;
    private int messageId;
    private FeedbackEmojiType emojiType;
    private FeedbackType feedbackType;

    public Feedback() {
    }

    public Feedback(long chatId, long playerTgId, int messageId, FeedbackEmojiType emojiType, FeedbackType feedbackType) {
        this.chatId = chatId;
        this.playerTgId = playerTgId;
        this.messageId = messageId;
        this.emojiType = emojiType;
        this.feedbackType = feedbackType;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getChatId() {
        return chatId;
    }

    public void setChatId(long chatId) {
        this.chatId = chatId;
    }

    public long getPlayerTgId() {
        return playerTgId;
    }

    public void setPlayerTgId(long playerTgId) {
        this.playerTgId = playerTgId;
    }

    public int getMessageId() {
        return messageId;
    }

    public void setMessageId(int messageId) {
        this.messageId = messageId;
    }

    public FeedbackEmojiType getEmojiType() {
        return emojiType;
    }

    public void setEmojiType(FeedbackEmojiType emojiType) {
        this.emojiType = emojiType;
    }

    public FeedbackType getFeedbackType() {
        return feedbackType;
    }

    public void setFeedbackType(FeedbackType feedbackType) {
        this.feedbackType = feedbackType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Feedback feedback = (Feedback) o;
        return id == feedback.id &&
                chatId == feedback.chatId &&
                playerTgId == feedback.playerTgId &&
                messageId == feedback.messageId &&
                emojiType == feedback.emojiType &&
                feedbackType == feedback.feedbackType;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, chatId, playerTgId, messageId, emojiType, feedbackType);
    }
}
