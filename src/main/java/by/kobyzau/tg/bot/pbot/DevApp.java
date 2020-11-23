package by.kobyzau.tg.bot.pbot;

import by.kobyzau.tg.bot.pbot.repository.TestObjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Profile;
import org.springframework.core.env.AbstractEnvironment;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.sql.DataSource;

@SpringBootApplication
@RestController
@Profile("dev")
public class DevApp {

  @Autowired private TestObjectRepository testObjectRepository;

  @Autowired private DataSource dataSource;

  @GetMapping("/")
  public String hello() {
    return "hello dev";
  }

  public static void main(String[] args) {
    System.setProperty(AbstractEnvironment.ACTIVE_PROFILES_PROPERTY_NAME, "dev");
    SpringApplication.run(DevApp.class, args);
  }
}
