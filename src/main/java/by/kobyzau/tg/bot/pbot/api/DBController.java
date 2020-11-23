package by.kobyzau.tg.bot.pbot.api;

import by.kobyzau.tg.bot.pbot.repository.dailypidor.IDailyPidorRepository;
import by.kobyzau.tg.bot.pbot.repository.pidor.IRepoPidorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

@RestController
@Profile("dev")
public class DBController {

  @Autowired private DataSource dataSource;

  @Autowired private IRepoPidorRepository pidorRepository;
  @Autowired private IDailyPidorRepository dailyPidorRepository;

  @GetMapping("/tables")
  public String tables() throws Exception {
    try (Connection c = dataSource.getConnection();
        Statement s = c.createStatement();
        ResultSet resultSet =
            s.executeQuery(
                "SELECT table_name\n"
                    + "  FROM information_schema.tables\n"
                    + " WHERE table_schema='public'")) {
      StringBuilder sb = new StringBuilder();

      while (resultSet.next()) {

        for (int col = 0; col < resultSet.getMetaData().getColumnCount(); col++) {
          Object object = resultSet.getObject(col + 1);
          sb.append(object).append(", ");
        }
        sb.append("\n<br>");
      }
      return sb.toString();
    }
  }

  @PostMapping("/execute")
  public String executeQuery(@RequestBody String query) throws Exception {
    System.out.println(query);
    try (Connection c = dataSource.getConnection();
        Statement s = c.createStatement()) {
      return "Done" + s.execute(query);
    }
  }
}
