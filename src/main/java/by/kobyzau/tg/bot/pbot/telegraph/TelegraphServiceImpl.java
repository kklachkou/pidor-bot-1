package by.kobyzau.tg.bot.pbot.telegraph;

import by.kobyzau.tg.bot.pbot.bots.PidorBot;
import by.kobyzau.tg.bot.pbot.model.TelegraphPage;
import by.kobyzau.tg.bot.pbot.program.logger.Logger;
import by.kobyzau.tg.bot.pbot.repository.telegraph.TelegraphPageRepository;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.telegram.telegraph.api.methods.CreatePage;
import org.telegram.telegraph.api.methods.EditPage;
import org.telegram.telegraph.api.objects.Node;
import org.telegram.telegraph.api.objects.NodeText;
import org.telegram.telegraph.api.objects.Page;
import org.telegram.telegraph.exceptions.TelegraphException;

@Service
public class TelegraphServiceImpl implements TelegraphService {

  @Value("${telegraph.token}")
  private String accountToken;

  @Autowired private Logger logger;

  @Autowired private PidorBot pidorBot;

  @Autowired private TelegraphPageRepository repository;

  @Override
  public TelegraphPage getPage(String linkedId) {
    return repository
        .getPageByLinkedId(linkedId)
        .orElseThrow(() -> new IllegalStateException("No page for " + linkedId));
  }

  @Override
  public void createPageIfNotExist(String linkedId) {
    if (repository.getPageByLinkedId(linkedId).isPresent()) {
      return;
    }
    logger.debug("Create Telegraph Page for " + linkedId);
    try {
      Page page =
          new CreatePage(accountToken, linkedId, getDefaultContent())
              .setReturnContent(false)
              .execute();
      TelegraphPage telegraphPage = new TelegraphPage();
      telegraphPage.setLinkedId(linkedId);
      telegraphPage.setUrl(page.getUrl());
      telegraphPage.setPath(page.getPath());
      repository.create(telegraphPage);
    } catch (TelegraphException e) {
      logger.error("Cannot create telegraph page", e);
      throw new IllegalStateException("Cannot create telegraph page", e);
    }
  }

  private List<Node> getDefaultContent() {
    Node contentNode = new NodeText("In progress...");
    List<Node> content = new ArrayList<>();
    content.add(contentNode);
    return content;
  }

  @Override
  public void updatePage(String linkedId, String title, List<Node> content) {
    logger.debug("Update Telegraph Page for " + linkedId);
    TelegraphPage telegraphPage = getPage(linkedId);
    try {
      Page page =
          new EditPage(accountToken, telegraphPage.getPath(), title, content)
              .setAuthorName("Pidor Bot (@" + pidorBot.getBotUsername() + ")")
              .execute();

      telegraphPage.setUrl(page.getUrl());
      telegraphPage.setPath(page.getPath());
      repository.update(telegraphPage);
    } catch (TelegraphException e) {
      logger.error("Cannot update telegraph page " + linkedId, e);
      throw new IllegalStateException("Cannot update telegraph page " + linkedId, e);
    }
  }
}
