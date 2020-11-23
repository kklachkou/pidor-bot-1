package by.kobyzau.tg.bot.pbot.program.tokens;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class AccessTokenHolderFactory {

  @Autowired private List<AccessTokenHolder> tokenHolderList;

  private Map<TokenType, AccessTokenHolder> map;

  @PostConstruct
  private void init() {
    map = tokenHolderList.stream().collect(Collectors.toMap(AccessTokenHolder::getType, t -> t));
  }

  public AccessTokenHolder getTokenHolder(TokenType tokenType) {
    return Optional.ofNullable(map.get(tokenType))
        .orElseThrow(() -> new RuntimeException("Not supported token type: " + tokenType));
  }
}
