package de.martinspielmann.haveibeenpwned4j.internal.mapper.json;

import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import de.martinspielmann.haveibeenpwned4j.model.Breach;
import de.martinspielmann.haveibeenpwned4j.model.Paste;

public class SimpleJsonMapper {

  private SimpleJsonMapper() {

  }

  public static Breach parseBreach(Map<String, Object> jsonMap) {
    Breach b = new Breach();
    b.setAddedDate(parseDateTime(jsonMap.get("AddedDate")));
    b.setBreachDate(parseDate(jsonMap.get("BreachDate")));
    b.setDataClasses(parseStringList(jsonMap.get("DataClasses")));
    b.setDescription(parseString(jsonMap.get("Description")));
    b.setDomain(parseString(jsonMap.get("Domain")));
    b.setFabricated(parseBoolean(jsonMap.get("IsFabricated")));
    b.setLogoPath(parseString(jsonMap.get("LogoPath")));
    b.setModifiedDate(parseDateTime(jsonMap.get("ModifiedDate")));
    b.setName(parseString(jsonMap.get("Name")));
    b.setPwnCount(parseInt(jsonMap.get("PwnCount")));
    b.setRetired(parseBoolean(jsonMap.get("Retired")));
    b.setSensitive(parseBoolean(jsonMap.get("Sensitive")));
    b.setSpamList(parseBoolean(jsonMap.get("SpamList")));
    b.setTitle(parseString(jsonMap.get("Title")));
    b.setVerified(parseBoolean(jsonMap.get("Verified")));
    return b;
  }

  public static Paste parsePaste(Map<String, Object> jsonMap) {
    Paste p = new Paste();
    p.setDate(parseDateTime(jsonMap.get("Date")));
    p.setEmailCount(parseInt(jsonMap.get("EmailCount")));
    p.setId(parseString(jsonMap.get("Id")));
    p.setSource(parseString(jsonMap.get("Source")));
    p.setTitle(parseString(jsonMap.get("Title")));
    return p;
  }

  static LocalDate parseDate(Object dateString) {
    if (dateString == null) {
      return null;
    }
    return LocalDate.parse((String) dateString);
  }

  static ZonedDateTime parseDateTime(Object dateString) {
    if (dateString == null) {
      return null;
    }
    return ZonedDateTime.parse((String) dateString);
  }

  static String parseString(Object string) {
    return (String) string;
  }

  static Boolean parseBoolean(Object bool) {
    return (Boolean) bool;
  }

  static Integer parseInt(Object integer) {
    return (Integer) integer;
  }

  @SuppressWarnings("unchecked")
  static List<String> parseStringList(Object stringList) {
    if (stringList == null) {
      return new ArrayList<>();
    }
    return (List<String>) stringList;
  }

}
