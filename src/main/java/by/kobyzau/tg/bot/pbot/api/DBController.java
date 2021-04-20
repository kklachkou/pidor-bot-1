package by.kobyzau.tg.bot.pbot.api;

import by.kobyzau.tg.bot.pbot.api.validator.AccessHeaderValidator;
import by.kobyzau.tg.bot.pbot.program.logger.Logger;
import by.kobyzau.tg.bot.pbot.repository.dailypidor.IDailyPidorRepository;
import by.kobyzau.tg.bot.pbot.repository.pidor.IRepoPidorRepository;
import by.kobyzau.tg.bot.pbot.util.TGUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.*;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;

@RestController
@Profile("with-db")
public class DBController {

  @Autowired private DataSource dataSource;
  @Autowired private Logger logger;
  @Autowired private IRepoPidorRepository pidorRepository;
  @Autowired private IDailyPidorRepository dailyPidorRepository;
  @Autowired private AccessHeaderValidator accessHeaderValidator;

  @GetMapping("/tables")
  public String tables(@RequestHeader("api-token") String token) throws Exception {
    accessHeaderValidator.assertAssess(token);
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
  public String executeQuery(@RequestHeader("api-token") String token, @RequestBody String query)
      throws Exception {
    accessHeaderValidator.assertAssess(token);
    logger.info("Executing query: " + TGUtil.escapeHTML(query));
    try (Connection c = dataSource.getConnection();
        Statement s = c.createStatement()) {
      return "Done" + s.execute(query);
    }
  }

  @PostMapping("/select")
  public String selectQuery(@RequestHeader("api-token") String token, @RequestBody String query)
      throws Exception {
    accessHeaderValidator.assertAssess(token);
    logger.info("Select query: " + TGUtil.escapeHTML(query));
    try (Connection c = dataSource.getConnection();
        Statement s = c.createStatement()) {
      ResultSet rs = s.executeQuery(query);
      StringBuilder sb = new StringBuilder();
      while (rs.next()) {
        sb.append("\n\n");
        ResultSetMetaData metaData = rs.getMetaData();
        for (int columnIndex = 0; columnIndex < metaData.getColumnCount(); columnIndex++) {
          sb.append("\n")
              .append(metaData.getColumnName(columnIndex))
              .append(": ")
              .append(rs.getObject(columnIndex));
        }
      }
      return sb.toString();
    }
  }
}
