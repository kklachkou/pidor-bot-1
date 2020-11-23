package by.kobyzau.tg.bot.pbot.tasks;

import by.kobyzau.tg.bot.pbot.program.logger.Logger;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component("pingHeroku")
@Profile("prod")
public class PingHerokuTask implements Task {

  @Value("${heroku.url}")
  private String herokuURL;

  @Autowired private Logger logger;

  @Override
  public void processTask() {
    logger.debug("\uD83D\uDCC6 Task " + this.getClass().getSimpleName() + " is started");
    try (CloseableHttpClient httpClient = createHttpClient()) {
      HttpGet request = new HttpGet(herokuURL);
      try (CloseableHttpResponse response = httpClient.execute(request)) {
        int statusCode = response.getStatusLine().getStatusCode();
        if (statusCode >= 400 && statusCode <= 599) {
          logger.warn("Heroku has bad state: " + statusCode);
        }
      }
    } catch (Exception ex) {
      logger.error("Cannot ping heroku", ex);
    }
  }

  private CloseableHttpClient createHttpClient() {
    RequestConfig requestConfig =
        RequestConfig.custom()
            .setConnectTimeout(30 * 1000)
            .setConnectionRequestTimeout(30 * 1000)
            .setSocketTimeout(30 * 1000)
            .build();

    return HttpClientBuilder.create()
        .useSystemProperties()
        .setDefaultRequestConfig(requestConfig)
        .build();
  }
}
