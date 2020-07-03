package de.martinspielmann.haveibeenpwned4j.model;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

@JsonNaming(PropertyNamingStrategy.UpperCamelCaseStrategy.class)
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
  @JsonProperty("IsVerified")
  private Boolean verified;
  @JsonProperty("IsFabricated")
  private Boolean fabricated;
  @JsonProperty("IsSensitive")
  private Boolean sensitive;
  @JsonProperty("IsRetired")
  private Boolean retired;
  @JsonProperty("IsSpamList")
  private Boolean spamList;
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
        + ", verified=" + verified + ", fabricated=" + fabricated + ", isSensitive="
        + sensitive + ", isRetired=" + retired + ", isSpamList=" + spamList + ", logoPath="
        + logoPath + "]";
  }

  public Boolean getVerified() {
    return verified;
  }

  public void setVerified(Boolean verified) {
    this.verified = verified;
  }

  public Boolean getFabricated() {
    return fabricated;
  }

  public void setFabricated(Boolean fabricated) {
    this.fabricated = fabricated;
  }

  public Boolean getSensitive() {
    return sensitive;
  }

  public void setSensitive(Boolean sensitive) {
    this.sensitive = sensitive;
  }

  public Boolean getRetired() {
    return retired;
  }

  public void setRetired(Boolean retired) {
    this.retired = retired;
  }

  public Boolean getSpamList() {
    return spamList;
  }

  public void setSpamList(Boolean spamList) {
    this.spamList = spamList;
  }
}
