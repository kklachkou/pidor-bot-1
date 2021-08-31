package by.kobyzau.tg.bot.pbot.client;

import by.kobyzau.tg.bot.pbot.model.api.github.CommitResponseDto;
import feign.RequestInterceptor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Optional;

@FeignClient(
    value = "github",
    url = "https://api.github.com",
    configuration = GithubClient.Configuration.class)
public interface GithubClient {

  @GetMapping("/repos/{user}/{repo}/commits/{branch}")
  Optional<CommitResponseDto> getCommit(
      @RequestParam("user") String user,
      @RequestParam("repo") String repo,
      @RequestParam("branch") String branch);

  class Configuration {
    @Bean
    public RequestInterceptor tokenInterceptor(@Value("${api.github.token}") String token) {
      return template -> template.header("Authorization", "token " + token);
    }
  }
}
