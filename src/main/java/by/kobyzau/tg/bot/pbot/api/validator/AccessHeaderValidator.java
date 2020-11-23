package by.kobyzau.tg.bot.pbot.api.validator;

import by.kobyzau.tg.bot.pbot.program.tokens.AccessTokenHolderFactory;
import by.kobyzau.tg.bot.pbot.program.tokens.TokenType;
import by.kobyzau.tg.bot.pbot.util.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class AccessHeaderValidator {

  @Autowired private AccessTokenHolderFactory tokenHolderFactory;

  public void assertAssess(String accessHeader) {
    if (StringUtil.isBlank(accessHeader)) {
      throw new RuntimeException("Missed access header");
    }
    if (!tokenHolderFactory.getTokenHolder(TokenType.API).getToken().equals(accessHeader)) {
      throw new RuntimeException("Invalid api token: " + accessHeader);
    }
  }
}
