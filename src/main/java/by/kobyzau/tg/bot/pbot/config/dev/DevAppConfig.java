package by.kobyzau.tg.bot.pbot.config.dev;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.PropertySource;

@Profile("dev")
@Configuration
@PropertySource(value = {"/application-dev.properties"})
public class DevAppConfig {}
