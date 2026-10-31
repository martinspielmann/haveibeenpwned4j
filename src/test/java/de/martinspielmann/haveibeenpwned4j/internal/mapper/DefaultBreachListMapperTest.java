package de.martinspielmann.haveibeenpwned4j.internal.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import de.martinspielmann.haveibeenpwned4j.HaveIBeenPwnedException;
import de.martinspielmann.haveibeenpwned4j.model.Breach;
import de.martinspielmann.haveibeenpwned4j.model.DataClassDescription;
import de.martinspielmann.haveibeenpwned4j.model.DomainVerificationToken;
import de.martinspielmann.haveibeenpwned4j.model.Paste;
import de.martinspielmann.haveibeenpwned4j.model.SubscribedDomain;
import de.martinspielmann.haveibeenpwned4j.model.SubscriptionStatus;

class DefaultBreachListMapperTest {

  private static final String SAMPLE_BREACH_JSON = """
      {
        "Name": "Adobe",
        "Title": "Adobe",
        "Domain": "adobe.com",
        "BreachDate": "2013-10-04",
        "AddedDate": "2013-12-04T00:00:00Z",
        "ModifiedDate": "2022-05-15T23:52:49Z",
        "PwnCount": 152445165,
        "Description": "Adobe breach description",
        "LogoPath": "Adobe.png",
        "DataClasses": ["Email addresses", "Passwords"],
        "IsVerified": true,
        "IsFabricated": false,
        "IsSensitive": false,
        "IsRetired": false,
        "IsSpamList": false,
        "IsMalware": false,
        "IsSubscriptionFree": true,
        "IsStealerLog": false,
        "Attribution": "Security Researcher",
        "FutureUnknownField": "ignored"
      }
      """;

  @Test
  void testGet() {
    assertNotNull(DefaultBreachListMapper.get());
    assertNotNull(DefaultBreachMapper.get());
    assertNotNull(DefaultBreachRangeMapper.get());
    assertNotNull(DefaultDataClassesListMapper.get());
    assertNotNull(DefaultDataClassDescriptionsListMapper.get());
    assertNotNull(DefaultPasteListMapper.get());
    assertNotNull(DefaultSubscribedDomainsListMapper.get());
    assertNotNull(DefaultSubscriptionStatusMapper.get());
    assertNotNull(DefaultDomainVerificationTokenMapper.get());
    assertNotNull(DefaultStringListMapMapper.get());
  }

  @Test
  void testMapNullAndBlank() {
    assertTrue(DefaultBreachListMapper.get().map(null).isEmpty());
    assertTrue(DefaultBreachListMapper.get().map("  ").isEmpty());
    assertNull(DefaultBreachMapper.get().map(null));
    assertNull(DefaultBreachMapper.get().map(""));
    assertTrue(DefaultDataClassesListMapper.get().map(null).isEmpty());
    assertTrue(DefaultDataClassDescriptionsListMapper.get().map(null).isEmpty());
    assertTrue(DefaultPasteListMapper.get().map(null).isEmpty());
    assertTrue(DefaultSubscribedDomainsListMapper.get().map(null).isEmpty());
    assertNull(DefaultSubscriptionStatusMapper.get().map(null));
    assertNull(DefaultDomainVerificationTokenMapper.get().map(null));
    assertTrue(DefaultStringListMapMapper.get().map(null).isEmpty());
  }

  @Test
  void testMapBreachWithAllV3FieldsAndUnknownField() {
    Breach breach = DefaultBreachMapper.get().map(SAMPLE_BREACH_JSON);
    assertNotNull(breach);
    assertEquals("Adobe", breach.name());
    assertEquals("Adobe", breach.title());
    assertEquals("adobe.com", breach.domain());
    assertEquals(LocalDate.of(2013, 10, 4), breach.breachDate());
    assertEquals(152445165, breach.pwnCount());
    assertEquals(List.of("Email addresses", "Passwords"), breach.dataClasses());
    assertTrue(breach.verified());
    assertFalse(breach.fabricated());
    assertFalse(breach.sensitive());
    assertFalse(breach.retired());
    assertFalse(breach.spamList());
    assertFalse(breach.malware());
    assertTrue(breach.subscriptionFree());
    assertFalse(breach.stealerLog());
    assertEquals("Adobe.png", breach.logoPath());
    assertEquals("Security Researcher", breach.attribution());

    List<Breach> list = DefaultBreachListMapper.get().map("[" + SAMPLE_BREACH_JSON + "]");
    assertEquals(1, list.size());
    assertEquals(breach, list.getFirst());
  }

  @Test
  void testMapBreachRange() {
    String json = """
        [
          {
            "hashSuffix": "C6C0AADE0C085843D66E4944E108C4A4CD",
            "websites": ["Adobe", "Gawker", "Stratfor"]
          },
          {
            "hashSuffix": "0000000000000000000000000000000000",
            "websites": ["Other"]
          }
        ]
        """;
    List<String> websites = DefaultBreachRangeMapper.get()
        .map(200, json, "multiple-breaches@hibp-integration-tests.com");
    assertEquals(List.of("Adobe", "Gawker", "Stratfor"), websites);
    assertTrue(DefaultBreachRangeMapper.get().map(200, json, "other@example.com").isEmpty());
    assertTrue(DefaultBreachRangeMapper.get().map(404, null, "other@example.com").isEmpty());
    assertTrue(DefaultBreachRangeMapper.get().map(200, "", "other@example.com").isEmpty());
    assertThrows(HaveIBeenPwnedException.class,
        () -> DefaultBreachRangeMapper.get().map(401, null, "other@example.com"));
  }

  @Test
  void testMapOtherModels() {
    List<DataClassDescription> descriptions = DefaultDataClassDescriptionsListMapper.get().map("""
        [{"Name": "Email addresses", "Description": "Email desc"}]
        """);
    assertEquals(1, descriptions.size());
    assertEquals("Email addresses", descriptions.getFirst().name());
    assertEquals("Email desc", descriptions.getFirst().description());

    List<Paste> pastes = DefaultPasteListMapper.get().map("""
        [{"Source": "Pastebin", "Id": "8Q0BvKD8", "Title": "syslog", "Date": "2014-03-04T19:14:54Z", "EmailCount": 139}]
        """);
    assertEquals(1, pastes.size());
    assertEquals("Pastebin", pastes.getFirst().source());
    assertEquals(139, pastes.getFirst().emailCount());

    List<SubscribedDomain> domains = DefaultSubscribedDomainsListMapper.get().map("""
        [{"DomainName": "example.com", "PwnCount": 10, "PwnCountExcludingSpamLists": 8, "PwnCountExcludingSpamListsAtLastSubscriptionRenewal": 5, "NextSubscriptionRenewal": "2027-01-01T00:00:00Z"}]
        """);
    assertEquals(1, domains.size());
    assertEquals("example.com", domains.getFirst().domainName());
    assertEquals(10, domains.getFirst().pwnCount());

    SubscriptionStatus status = DefaultSubscriptionStatusMapper.get().map("""
        {
          "SubscriptionName": "Pro 2",
          "Description": "Pro plan",
          "SubscribedUntil": "2027-01-01T00:00:00Z",
          "Rpm": 100,
          "DomainSearchMaxBreachedAccounts": 1000,
          "MaxBreachedDomains": null,
          "IncludesStealerLogs": true,
          "IncludesBulkDomainAdd": true,
          "IncludesAutoSubdomainVerification": true,
          "IncludesCustomerDomains": false,
          "IncludesKAnon": true
        }
        """);
    assertEquals("Pro 2", status.subscriptionName());
    assertEquals(100, status.rpm());
    assertTrue(status.includesStealerLogs());
    assertTrue(status.includesKAnon());

    DomainVerificationToken token = DefaultDomainVerificationTokenMapper.get().map("""
        {"txtRecordValue": "hibp-verify=dweb_123"}
        """);
    assertEquals("hibp-verify=dweb_123", token.txtRecordValue());

    Map<String, List<String>> map = DefaultStringListMapMapper.get().map("""
        {"alias1": ["Adobe"], "alias2": ["Gawker", "Stratfor"]}
        """);
    assertEquals(List.of("Adobe"), map.get("alias1"));
    assertEquals(List.of("Gawker", "Stratfor"), map.get("alias2"));
  }

  @Test
  void testInvalidJsonThrowsIllegalStateException() {
    assertThrows(IllegalStateException.class, () -> DefaultBreachMapper.get().map("{invalid"));
    assertThrows(IllegalStateException.class, () -> DefaultBreachListMapper.get().map("[invalid"));
    assertThrows(IllegalStateException.class,
        () -> DefaultBreachRangeMapper.get().map(200, "[invalid", "a@b.com"));
    assertThrows(IllegalStateException.class,
        () -> DefaultDataClassesListMapper.get().map("[invalid"));
    assertThrows(IllegalStateException.class,
        () -> DefaultDataClassDescriptionsListMapper.get().map("[invalid"));
    assertThrows(IllegalStateException.class, () -> DefaultPasteListMapper.get().map("[invalid"));
    assertThrows(IllegalStateException.class,
        () -> DefaultSubscribedDomainsListMapper.get().map("[invalid"));
    assertThrows(IllegalStateException.class,
        () -> DefaultSubscriptionStatusMapper.get().map("{invalid"));
    assertThrows(IllegalStateException.class,
        () -> DefaultDomainVerificationTokenMapper.get().map("{invalid"));
    assertThrows(IllegalStateException.class,
        () -> DefaultStringListMapMapper.get().map("{invalid"));
  }
}

