package by.kobyzau.tg.bot.pbot.tg.action;

import org.telegram.telegrambots.meta.api.methods.polls.SendPoll;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

public class PollBotActionBuilder {

  private final long chatId;
  private String question;
  private List<String> options;
  private boolean isAnonymous = true;
  private Type type = Type.REGULAR;
  private Integer correctAnswer;
  private Consumer<Message> postAction;

  public PollBotActionBuilder(long chatId) {
    this.chatId = chatId;
  }

  public static PollBotActionBuilder forChat(long chatId) {
    return new PollBotActionBuilder(chatId);
  }

  public PollBotActionBuilder withQuestion(String question) {
    this.question = question;
    return this;
  }

  public PollBotActionBuilder withOptions(String... options) {
    this.options = Arrays.asList(options);
    return this;
  }

  public PollBotActionBuilder withOptions(List<String> options) {
    this.options = options;
    return this;
  }

  public PollBotActionBuilder isAnonymous(boolean isAnonymous) {
    this.isAnonymous = isAnonymous;
    return this;
  }

  public PollBotActionBuilder withType(Type type) {
    this.type = type;
    return this;
  }

  public PollBotActionBuilder withCorrectAnswer(int correctAnswer) {
    this.correctAnswer = correctAnswer;
    return this;
  }

  public PollBotActionBuilder withPostAction(Consumer<Message> postAction) {
    this.postAction = postAction;
    return this;
  }

  public PollBotAction build() {
    SendPoll.SendPollBuilder builder =
        SendPoll.builder()
            .chatId(String.valueOf(chatId))
            .question(question)
            .options(options)
            .type(this.type.type)
            .isAnonymous(isAnonymous);

    if (Type.QUIZ.equals(this.type)) {
      builder.correctOptionId(correctAnswer);
    }

    return new PollBotAction(builder.build(), postAction);
  }

  public enum Type {
    QUIZ("quiz"),
    REGULAR("regular");
    private final String type;

    Type(String type) {
      this.type = type;
    }
  }
}
