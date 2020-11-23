package by.kobyzau.tg.bot.pbot.program.tokens.impl;

import by.kobyzau.tg.bot.pbot.program.tokens.AccessTokenHolder;
import by.kobyzau.tg.bot.pbot.program.tokens.TokenType;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class ApiAccessTokenHolder implements AccessTokenHolder {

    private final String apiToken;


    public ApiAccessTokenHolder() {
        this.apiToken = UUID.randomUUID().toString();
    }

    @Override
    public String getToken() {
        return apiToken;
    }

    @Override
    public TokenType getType() {
        return TokenType.API;
    }
}
