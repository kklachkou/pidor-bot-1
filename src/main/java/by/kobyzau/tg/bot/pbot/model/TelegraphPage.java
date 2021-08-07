package by.kobyzau.tg.bot.pbot.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "telegraph_page")
public class TelegraphPage {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private long id;

  private String path;
  private String url;
  private String linkedId;

  public TelegraphPage() {}

  public TelegraphPage(TelegraphPage page) {
    this.id = page.getId();
    this.url = page.getUrl();
    this.path = page.getPath();
    this.linkedId = page.getLinkedId();
  }

  public long getId() {
    return id;
  }

  public void setId(long id) {
    this.id = id;
  }

  public String getPath() {
    return path;
  }

  public void setPath(String path) {
    this.path = path;
  }

  public String getLinkedId() {
    return linkedId;
  }

  public void setLinkedId(String linkedId) {
    this.linkedId = linkedId;
  }

  public String getUrl() {
    return url;
  }

  public void setUrl(String url) {
    this.url = url;
  }
}
