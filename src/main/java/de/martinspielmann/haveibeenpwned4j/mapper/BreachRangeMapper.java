package de.martinspielmann.haveibeenpwned4j.mapper;

import java.util.List;

/**
 * Maps a k-anonymity email breach range response to the breach names matching the target account.
 */
public interface BreachRangeMapper {

  List<String> map(int responseStatus, String responseBody, String account);

}
