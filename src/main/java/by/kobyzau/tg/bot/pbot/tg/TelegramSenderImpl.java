package by.kobyzau.tg.bot.pbot.tg;

import by.kobyzau.tg.bot.pbot.program.logger.Logger;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.HttpEntity;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.ByteArrayBody;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.ChatMember;
import org.telegram.telegrambots.meta.api.objects.User;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

@Component
public class TelegramSenderImpl implements TelegramSender {

  @Autowired private Logger logger;

  @Override
  public Optional<ChatMember> getChatMember(String botId, long chatId, long userId) {
    try (CloseableHttpClient httpClient = createHttpClient()) {
      HttpGet request = new HttpGet(buildGetChatMemberURI(botId, chatId, userId));
      request.setHeader("Content-Type", "application/json");
      try (CloseableHttpResponse response = httpClient.execute(request)) {
        int statusCode = response.getStatusLine().getStatusCode();
        if (statusCode >= 500 && statusCode <= 599 || statusCode == 400) {
          return Optional.empty();
        }
        HttpEntity responseEntity = response.getEntity();
        String content = EntityUtils.toString(responseEntity, StandardCharsets.UTF_8);
        EntityUtils.consume(responseEntity);
        JSONObject chatMember = new JSONObject(content);
        if(chatMember.has("result")) {
          String userString = chatMember.getJSONObject("result").toString();
          return Optional.ofNullable(new ObjectMapper().readValue(userString, ChatMember.class));
        }
        return Optional.empty();
      }
    } catch (Exception ex) {
      logger.error("Cannot get ChatMember " + userId + " for chat " + chatId, ex);
      return Optional.empty();
    }
  }

  @Override
  public void deleteMessage(String botId, String chatId, int messageId) {
    try (CloseableHttpClient httpClient = createHttpClient()) {
      HttpGet request = new HttpGet(buildDeleteMessageURI(botId, chatId, messageId));
      request.setHeader("Content-Type", "application/json");
      try (CloseableHttpResponse response = httpClient.execute(request)) {
        int statusCode = response.getStatusLine().getStatusCode();
        if (statusCode >= 500 && statusCode <= 599 || statusCode == 400) {
          throw new RuntimeException("Bad request: " + statusCode);
        }
      }
    } catch (Exception ex) {
      logger.error("Cannot delete message " + messageId + " for chat " + chatId, ex);
    }
  }

  @Override
  public void sendMessage(
      String botId, String chatId, String message, boolean disableNotification) {
    try (CloseableHttpClient httpClient = createHttpClient()) {
      HttpPost request = new HttpPost(buildSendMessageURI(botId));
      request.setHeader("Content-Type", "application/json");
      request.setEntity(
          new StringEntity(
              buildSendMessage(message, chatId, disableNotification), StandardCharsets.UTF_8));
      try (CloseableHttpResponse response = httpClient.execute(request)) {
        int statusCode = response.getStatusLine().getStatusCode();
        if (statusCode >= 500 && statusCode <= 599 || statusCode == 400) {
          throw new RuntimeException("Bad request: " + statusCode);
        }
      }
    } catch (IOException ex) {
      throw new RuntimeException("Bad Request", ex);
    }
  }

  @Override
  public void sendMessage(String botId, String chatId, String message) {
    sendMessage(botId, chatId, message, false);
  }

  @Override
  public String sendStringAsFile(String botId, String chatId, String fileName, String string) {
    try (CloseableHttpClient httpClient = createHttpClient()) {
      HttpPost request = new HttpPost(buildSendDocURI(botId, chatId));
      // request.setHeader("Content-Type", "application/json");

      MultipartEntity reqEntity = new MultipartEntity();
      reqEntity.addPart("document", new ByteArrayBody(string.getBytes(), fileName));
      request.setEntity(reqEntity);

      try (CloseableHttpResponse response = httpClient.execute(request)) {
        int statusCode = response.getStatusLine().getStatusCode();
        if ((statusCode >= 500 && statusCode <= 599) || (statusCode >= 400 && statusCode <= 499)) {
          throw new RuntimeException(
              "Bad request: " + statusCode + "; " + fetchContentAsString(response.getEntity()));
        }
        HttpEntity responseEntity = response.getEntity();
        String content = fetchContentAsString(responseEntity);
        EntityUtils.consume(responseEntity);
        return content;
      }
    } catch (IOException ex) {
      throw new RuntimeException("Bad Request", ex);
    }
  }

  @Override
  public String sendFile(String botId, String chatId, File file) {
    try (CloseableHttpClient httpClient = createHttpClient()) {
      HttpPost request = new HttpPost(buildSendDocURI(botId, chatId));
      // request.setHeader("Content-Type", "application/json");
      FileBody uploadFilePart = new FileBody(file);
      MultipartEntity reqEntity = new MultipartEntity();
      reqEntity.addPart("document", uploadFilePart);
      request.setEntity(reqEntity);

      try (CloseableHttpResponse response = httpClient.execute(request)) {
        int statusCode = response.getStatusLine().getStatusCode();
        if ((statusCode >= 500 && statusCode <= 599) || (statusCode >= 400 && statusCode <= 499)) {
          throw new RuntimeException(
              "Bad request: " + statusCode + "; " + fetchContentAsString(response.getEntity()));
        }
        HttpEntity responseEntity = response.getEntity();
        String content = fetchContentAsString(responseEntity);
        EntityUtils.consume(responseEntity);
        return content;
      }
    } catch (IOException ex) {
      throw new RuntimeException("Bad Request", ex);
    }
  }

  @Override
  public Optional<Chat> getChat(String botId, long chatId) {
    try (CloseableHttpClient httpClient = createHttpClient()) {
      HttpGet request = new HttpGet(buildGetChatURI(botId, chatId));
      request.setHeader("Content-Type", "application/json");
      try (CloseableHttpResponse response = httpClient.execute(request)) {
        int statusCode = response.getStatusLine().getStatusCode();
        if (statusCode >= 500 && statusCode <= 599 || statusCode == 400) {
          return Optional.empty();
        }
        HttpEntity responseEntity = response.getEntity();
        String content = EntityUtils.toString(responseEntity, StandardCharsets.UTF_8);
        EntityUtils.consume(responseEntity);
        JSONObject chatMember = new JSONObject(content);
        String userString = chatMember.getJSONObject("result").toString();
        return Optional.ofNullable(new ObjectMapper().readValue(userString, Chat.class));
      }
    } catch (Exception ex) {
      logger.error("Cannot get ChatInfo for chat " + chatId, ex);
      return Optional.empty();
    }
  }

  @Override
  public User getMe(String botToken) {
    try (CloseableHttpClient httpClient = createHttpClient()) {
      HttpGet request = new HttpGet("https://api.telegram.org/bot" + botToken + "/getMe");
      request.setHeader("Content-Type", "application/json");
      try (CloseableHttpResponse response = httpClient.execute(request)) {
        int statusCode = response.getStatusLine().getStatusCode();
        if (statusCode >= 500 && statusCode <= 599 || statusCode == 400) {
          throw new RuntimeException("Cannot getMe: " + statusCode);
        }
        HttpEntity responseEntity = response.getEntity();
        String content = EntityUtils.toString(responseEntity, StandardCharsets.UTF_8);
        EntityUtils.consume(responseEntity);
        JSONObject chatMember = new JSONObject(content);
        String userString = chatMember.getJSONObject("result").toString();
        return new ObjectMapper().readValue(userString, User.class);
      }
    } catch (Exception ex) {
      throw new RuntimeException("Cannot getMe", ex);
    }
  }

  @Override
  public int getChatMemberCount(String botId, long chatId) {
    try (CloseableHttpClient httpClient = createHttpClient()) {
      HttpGet request = new HttpGet(buildGetChatMemberCountURI(botId, chatId));
      request.setHeader("Content-Type", "application/json");
      try (CloseableHttpResponse response = httpClient.execute(request)) {
        int statusCode = response.getStatusLine().getStatusCode();
        if (statusCode >= 500 && statusCode <= 599 || statusCode == 400) {
          throw new RuntimeException("Cannot get chat member count: " + statusCode);
        }
        HttpEntity responseEntity = response.getEntity();
        String content = EntityUtils.toString(responseEntity, StandardCharsets.UTF_8);
        EntityUtils.consume(responseEntity);
        JSONObject chatMember = new JSONObject(content);
        if (!chatMember.has("result")) {
          return 0;
        }
        return chatMember.getInt("result");
      }
    } catch (Exception ex) {
      throw new RuntimeException(ex);
    }
  }

  private String buildSendDocURI(String botId, String chatId) {
    return "https://api.telegram.org/bot" + botId + "/sendDocument?chat_id=" + chatId;
  }

  private String buildSendMessageURI(String botId) {
    return "https://api.telegram.org/bot" + botId + "/sendMessage";
  }

  private String buildDeleteMessageURI(String botId, String chatId, int messageId) {
    return "https://api.telegram.org/bot"
        + botId
        + "/deleteMessage?chat_id="
        + chatId
        + "&message_id="
        + messageId;
  }

  private String buildGetChatMemberURI(String botId, long chatId, long userId) {
    return "https://api.telegram.org/bot"
        + botId
        + "/getChatMember?chat_id="
        + chatId
        + "&user_id="
        + userId;
  }

  private String buildGetChatURI(String botId, long chatId) {
    return "https://api.telegram.org/bot" + botId + "/getChat?chat_id=" + chatId;
  }

  private String buildGetChatMemberCountURI(String botId, long chatId) {
    return "https://api.telegram.org/bot" + botId + "/getChatMemberCount?chat_id=" + chatId;
  }

  private String buildSendMessage(String message, String chatId, boolean disableNotification) {
    JSONObject jsonObject = new JSONObject();
    jsonObject.put("text", message);
    jsonObject.put("chat_id", chatId);
    jsonObject.put("disable_notification", disableNotification);
    jsonObject.put("parse_mode", "HTML");
    return jsonObject.toString();
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

  private String fetchContentAsString(HttpEntity response) throws IOException {
    return EntityUtils.toString(response, StandardCharsets.UTF_8);
  }
}
