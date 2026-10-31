package de.martinspielmann.haveibeenpwned4j.model;

import java.io.Serializable;
import java.time.ZonedDateTime;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

/**
 * Represents a paste entry returned by the Have I Been Pwned API v3.
 *
 * @param source Paste service the record was retrieved from
 * @param id ID of the paste at the source service
 * @param title Title of the paste as observed on the source site (may be null)
 * @param date Date and time the paste was posted (may be null)
 * @param emailCount Number of email addresses found in the paste
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonNaming(PropertyNamingStrategies.UpperCamelCaseStrategy.class)
public record Paste(
    String source,
    String id,
    String title,
    ZonedDateTime date,
    Integer emailCount) implements Serializable {
}

