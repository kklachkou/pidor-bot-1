package by.kobyzau.tg.bot.pbot;

import by.kobyzau.tg.bot.pbot.util.DateUtil;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Profile;
import org.springframework.core.env.AbstractEnvironment;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;


@SpringBootApplication
@RestController
@Profile("dev")
public class DevApp {

  @GetMapping("/")
  public String hello() {
    return "hello dev";
  }

  public static void main(String[] args) {
    List<String> profiles = new ArrayList<>();
    profiles.add("dev");
    if (DateUtil.isNewYearTime()) {
      profiles.add("new-year");
    }
    System.setProperty(AbstractEnvironment.ACTIVE_PROFILES_PROPERTY_NAME, String.join(",", profiles));
    SpringApplication.run(DevApp.class, args);
  }
}
