package de.martinspielmann.haveibeenpwned4j.model;

import java.io.Serializable;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

/**
 * Represents a data class and its human-readable description.
 *
 * @param name Name of the data class (e.g., "Email addresses")
 * @param description Human-readable description of the data class
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonNaming(PropertyNamingStrategies.UpperCamelCaseStrategy.class)
public record DataClassDescription(
    String name,
    String description) implements Serializable {
}
