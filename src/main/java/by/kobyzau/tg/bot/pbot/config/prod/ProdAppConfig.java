package by.kobyzau.tg.bot.pbot.config.prod;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource(value = {"/application-prod.yaml"})
@Profile("prod")
public class ProdAppConfig {}
