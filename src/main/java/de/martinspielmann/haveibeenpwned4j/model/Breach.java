package de.martinspielmann.haveibeenpwned4j.model;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.List;

public class Breach implements Serializable {

  private static final long serialVersionUID = 1L;

  private String name;
  private String title;
  private String domain;
  private LocalDate breachDate;
  private ZonedDateTime addedDate;
  private ZonedDateTime modifiedDate;
  private Integer pwnCount;
  private String description;
  private List<String> dataClasses;
  private Boolean isVerified;
  private Boolean isFabricated;
  private Boolean isSensitive;
  private Boolean isRetired;
  private Boolean isSpamList;
  private String logoPath;

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public String getDomain() {
    return domain;
  }

  public void setDomain(String domain) {
    this.domain = domain;
  }

  public LocalDate getBreachDate() {
    return breachDate;
  }

  public void setBreachDate(LocalDate breachDate) {
    this.breachDate = breachDate;
  }

  public ZonedDateTime getAddedDate() {
    return addedDate;
  }

  public void setAddedDate(ZonedDateTime addedDate) {
    this.addedDate = addedDate;
  }

  public ZonedDateTime getModifiedDate() {
    return modifiedDate;
  }

  public void setModifiedDate(ZonedDateTime modifiedDate) {
    this.modifiedDate = modifiedDate;
  }

  public Integer getPwnCount() {
    return pwnCount;
  }

  public void setPwnCount(Integer pwnCount) {
    this.pwnCount = pwnCount;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public List<String> getDataClasses() {
    return dataClasses;
  }

  public void setDataClasses(List<String> dataClasses) {
    this.dataClasses = dataClasses;
  }

  public Boolean isVerified() {
    return isVerified;
  }

  public void setVerified(Boolean isVerified) {
    this.isVerified = isVerified;
  }

  public Boolean isFabricated() {
    return isFabricated;
  }

  public void setFabricated(Boolean isFabricated) {
    this.isFabricated = isFabricated;
  }

  public Boolean isSensitive() {
    return isSensitive;
  }

  public void setSensitive(Boolean isSensitive) {
    this.isSensitive = isSensitive;
  }

  public Boolean isRetired() {
    return isRetired;
  }

  public void setRetired(Boolean isRetired) {
    this.isRetired = isRetired;
  }

  public Boolean isSpamList() {
    return isSpamList;
  }

  public void setSpamList(Boolean isSpamList) {
    this.isSpamList = isSpamList;
  }

  public String getLogoPath() {
    return logoPath;
  }

  public void setLogoPath(String logoPath) {
    this.logoPath = logoPath;
  }

  @Override
  public String toString() {
    return "Breach [name=" + name + ", title=" + title + ", domain=" + domain + ", breachDate="
        + breachDate + ", addedDate=" + addedDate + ", modifiedDate=" + modifiedDate + ", pwnCount="
        + pwnCount + ", description=" + description + ", dataClasses=" + dataClasses
        + ", isVerified=" + isVerified + ", isFabricated=" + isFabricated + ", isSensitive="
        + isSensitive + ", isRetired=" + isRetired + ", isSpamList=" + isSpamList + ", logoPath="
        + logoPath + "]";
  }
}
