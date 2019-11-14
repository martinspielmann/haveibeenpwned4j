package de.martinspielmann.haveibeenpwned4j.model;

import java.io.Serializable;
import java.time.ZonedDateTime;

public class Paste implements Serializable {

  private static final long serialVersionUID = 1L;

  private String source;
  private String id;
  private String title;
  private ZonedDateTime date;
  private Integer emailCount;

  public String getSource() {
    return source;
  }

  public void setSource(String source) {
    this.source = source;
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public ZonedDateTime getDate() {
    return date;
  }

  public void setDate(ZonedDateTime date) {
    this.date = date;
  }

  public Integer getEmailCount() {
    return emailCount;
  }

  public void setEmailCount(Integer emailCount) {
    this.emailCount = emailCount;
  }

  @Override
  public String toString() {
    return "Paste [source=" + source + ", id=" + id + ", title=" + title + ", date=" + date
        + ", emailCount=" + emailCount + "]";
  }

}
