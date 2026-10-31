package de.martinspielmann.haveibeenpwned4j;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpHeaders;
import java.net.http.HttpRequest;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import de.martinspielmann.haveibeenpwned4j.internal.mapper.DefaultBreachListMapper;
import de.martinspielmann.haveibeenpwned4j.internal.mapper.DefaultBreachMapper;
import de.martinspielmann.haveibeenpwned4j.internal.mapper.DefaultBreachRangeMapper;
import de.martinspielmann.haveibeenpwned4j.internal.mapper.DefaultDataClassDescriptionsListMapper;
import de.martinspielmann.haveibeenpwned4j.internal.mapper.DefaultDataClassesListMapper;
import de.martinspielmann.haveibeenpwned4j.internal.mapper.DefaultDomainVerificationTokenMapper;
import de.martinspielmann.haveibeenpwned4j.internal.mapper.DefaultPasteListMapper;
import de.martinspielmann.haveibeenpwned4j.internal.mapper.DefaultPwnedPasswordsMapper;
import de.martinspielmann.haveibeenpwned4j.internal.mapper.DefaultStringListMapMapper;
import de.martinspielmann.haveibeenpwned4j.internal.mapper.DefaultSubscribedDomainsListMapper;
import de.martinspielmann.haveibeenpwned4j.internal.mapper.DefaultSubscriptionStatusMapper;
import de.martinspielmann.haveibeenpwned4j.mapper.BreachListMapper;
import de.martinspielmann.haveibeenpwned4j.mapper.BreachMapper;
import de.martinspielmann.haveibeenpwned4j.mapper.BreachRangeMapper;
import de.martinspielmann.haveibeenpwned4j.mapper.DataClassDescriptionsListMapper;
import de.martinspielmann.haveibeenpwned4j.mapper.DataClassesListMapper;
import de.martinspielmann.haveibeenpwned4j.mapper.DomainVerificationTokenMapper;
import de.martinspielmann.haveibeenpwned4j.mapper.PasteListMapper;
import de.martinspielmann.haveibeenpwned4j.mapper.PwnedPasswordsMapper;
import de.martinspielmann.haveibeenpwned4j.mapper.StringListMapMapper;
import de.martinspielmann.haveibeenpwned4j.mapper.SubscribedDomainsListMapper;
import de.martinspielmann.haveibeenpwned4j.mapper.SubscriptionStatusMapper;
import de.martinspielmann.haveibeenpwned4j.model.Breach;
import de.martinspielmann.haveibeenpwned4j.model.DataClassDescription;
import de.martinspielmann.haveibeenpwned4j.model.DomainVerificationToken;
import de.martinspielmann.haveibeenpwned4j.model.EmailAlias;
import de.martinspielmann.haveibeenpwned4j.model.HashMode;
import de.martinspielmann.haveibeenpwned4j.model.Paste;
import de.martinspielmann.haveibeenpwned4j.model.SubscribedDomain;
import de.martinspielmann.haveibeenpwned4j.model.SubscriptionStatus;

class HaveIBeenPwnedApiClientTest {

  private static final String BREACH_JSON = """
      {
        "Name": "Adobe",
        "Title": "Adobe",
        "Domain": "adobe.com",
        "BreachDate": "2013-10-04",
        "AddedDate": "2013-12-04T00:00:00Z",
        "ModifiedDate": "2022-05-15T23:52:49Z",
        "PwnCount": 152445165,
        "Description": "Adobe breach",
        "LogoPath": "Adobe.png",
        "DataClasses": ["Email addresses", "Passwords"],
        "IsVerified": true,
        "IsFabricated": false,
        "IsSensitive": false,
        "IsRetired": false,
        "IsSpamList": false,
        "IsMalware": false,
        "IsSubscriptionFree": false,
        "IsStealerLog": false,
        "Attribution": null
      }
      """;

  private static HttpServer server;
  private static String baseUrl;
  private static String pwnedPasswordsBaseUrl;

  private HaveIBeenPwnedApiClient clientWithApiKey;
  private HaveIBeenPwnedApiClient clientWithoutApiKey;

  @BeforeAll
  static void startServer() throws IOException {
    server = HttpServer.create(new InetSocketAddress("127.0.0.1", 0), 0);
    server.createContext("/api/v3/", HaveIBeenPwnedApiClientTest::handleHibpRequest);
    server.createContext("/range/", HaveIBeenPwnedApiClientTest::handleRangeRequest);
    server.start();
    int port = server.getAddress().getPort();
    baseUrl = "http://127.0.0.1:" + port + "/api/v3/";
    pwnedPasswordsBaseUrl = "http://127.0.0.1:" + port + "/range/";
  }

  @AfterAll
  static void stopServer() {
    if (server != null) {
      server.stop(0);
    }
  }

  private static void handleHibpRequest(HttpExchange exchange) throws IOException {
    String path = exchange.getRequestURI().getRawPath();
    String query = exchange.getRequestURI().getRawQuery();
    String apiKey = exchange.getRequestHeaders().getFirst("hibp-api-key");

    boolean requiresAuth = path.contains("/breachedaccount/")
        || path.contains("/breacheddomain/")
        || path.contains("/subscribeddomains")
        || path.contains("/domainverification/")
        || path.contains("/stealerlogs")
        || path.contains("/pasteaccount/")
        || path.contains("/subscription/");

    if (requiresAuth && (apiKey == null || apiKey.isBlank())) {
      sendResponse(exchange, 401, "Access denied due to missing hibp-api-key.");
      return;
    }

    if (path.endsWith("/rate-limited%40example.com")
        || path.endsWith("/range/" + StringHelper.getAccountHashPrefix("rate-limited@example.com"))) {
      exchange.getResponseHeaders().add("retry-after", "5");
      sendResponse(exchange, 429, "{\"statusCode\":429,\"message\":\"Rate limit exceeded\"}");
      return;
    }

    if (path.startsWith("/api/v3/breachedaccount/range/")) {
      sendResponse(exchange, 200, """
          [{"hashSuffix":"C6C0AADE0C085843D66E4944E108C4A4CD","websites":["Adobe","Gawker","Stratfor"]}]
          """);
      return;
    }

    if (path.startsWith("/api/v3/breachedaccount/")) {
      if (path.contains("notfound")) {
        sendResponse(exchange, 404, "");
      } else if (query != null && query.contains("truncateResponse=true")) {
        sendResponse(exchange, 200, "[{\"Name\":\"Adobe\"}]");
      } else {
        sendResponse(exchange, 200, "[" + BREACH_JSON + "]");
      }
      return;
    }

    if (path.equals("/api/v3/breaches")) {
      sendResponse(exchange, 200, "[" + BREACH_JSON + "]");
      return;
    }

    if (path.startsWith("/api/v3/breach/")) {
      if (path.endsWith("/Adobe")) {
        sendResponse(exchange, 200, BREACH_JSON);
      } else {
        sendResponse(exchange, 404, "");
      }
      return;
    }

    if (path.equals("/api/v3/latestbreach")) {
      sendResponse(exchange, 200, BREACH_JSON);
      return;
    }

    if (path.equals("/api/v3/dataclasses")) {
      sendResponse(exchange, 200, "[\"Email addresses\",\"Passwords\"]");
      return;
    }

    if (path.equals("/api/v3/dataclasses/descriptions")) {
      sendResponse(exchange, 200,
          "[{\"name\":\"Passwords\",\"description\":\"Exposed passwords\"},{\"Name\":\"Email addresses\",\"Description\":\"Exposed emails\"}]");
      return;
    }

    if (path.startsWith("/api/v3/pasteaccount/")) {
      if (path.contains("notfound")) {
        sendResponse(exchange, 404, "");
      } else {
        sendResponse(exchange, 200, """
            [{"Source":"Pastebin","Id":"8Q0BvKD8","Title":"syslog","Date":"2014-03-04T19:14:54Z","EmailCount":139}]
            """);
      }
      return;
    }

    if (path.startsWith("/api/v3/breacheddomain/")) {
      sendResponse(exchange, 200, "{\"jane\":[\"Adobe\",\"Gawker\"]}");
      return;
    }

    if (path.equals("/api/v3/subscribeddomains")) {
      sendResponse(exchange, 200, """
          [{"DomainName":"example.com","PwnCount":12,"PwnCountExcludingSpamLists":10,"PwnCountExcludingSpamListsAtLastSubscriptionRenewal":8,"NextSubscriptionRenewal":"2027-01-01T00:00:00Z"}]
          """);
      return;
    }

    if (path.equals("/api/v3/domainverification/generatednstoken")) {
      sendResponse(exchange, 200, "{\"txtRecordValue\":\"hibp-verify=dweb_abc123\"}");
      return;
    }

    if (path.equals("/api/v3/domainverification/verifydnstoken")) {
      String reqBody = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
      if (reqBody.contains("unverified.example.com")) {
        sendResponse(exchange, 404, "");
      } else {
        sendResponse(exchange, 200, "");
      }
      return;
    }

    if (path.equals("/api/v3/domainverification/sendemail")) {
      String reqBody = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
      if (reqBody.contains("notfound.example.com")) {
        sendResponse(exchange, 404, "");
      } else {
        sendResponse(exchange, 200, "");
      }
      return;
    }

    if (path.startsWith("/api/v3/stealerlogsbyemail/")) {
      sendResponse(exchange, 200, "[\"netflix.com\",\"spotify.com\"]");
      return;
    }

    if (path.startsWith("/api/v3/stealerlogsbywebsitedomain/")) {
      sendResponse(exchange, 200, "[\"jane@gmail.com\"]");
      return;
    }

    if (path.startsWith("/api/v3/stealerlogsbyemaildomain/")) {
      sendResponse(exchange, 200, "{\"jane\":[\"netflix.com\"]}");
      return;
    }

    if (path.equals("/api/v3/subscription/status")) {
      sendResponse(exchange, 200, """
          {
            "SubscriptionName": "Pro 2",
            "Description": "Pro plan",
            "SubscribedUntil": "2027-01-01T00:00:00Z",
            "Rpm": 100,
            "DomainSearchMaxBreachedAccounts": 1000,
            "MaxBreachedDomains": 10,
            "IncludesStealerLogs": true,
            "IncludesBulkDomainAdd": true,
            "IncludesAutoSubdomainVerification": true,
            "IncludesCustomerDomains": false,
            "IncludesKAnon": true
          }
          """);
      return;
    }

    sendResponse(exchange, 404, "");
  }

  private static void handleRangeRequest(HttpExchange exchange) throws IOException {
    if (exchange.getRequestHeaders().getFirst("hibp-api-key") != null) {
      sendResponse(exchange, 400, "hibp-api-key should not be sent to Pwned Passwords");
      return;
    }
    String path = exchange.getRequestURI().getRawPath();
    String query = exchange.getRequestURI().getRawQuery();
    if ("mode=ntlm".equals(query) && path.endsWith("/8846F")) {
      sendResponse(exchange, 200, "7EAEE8FB117AD06BDD830B7586C:99\r\n000000000000000000000000000:0");
    } else if (path.endsWith("/CBF18")) {
      // SHA-1("password123") = CBFDAC6008F9CAB4083784CBD1874F76618D2A97
      sendResponse(exchange, 200, "");
    } else if (path.endsWith("/CBFDA")) {
      sendResponse(exchange, 200,
          "C6008F9CAB4083784CBD1874F76618D2A97:12345\r\n00000000000000000000000000000000000:0");
    } else {
      sendResponse(exchange, 200, "00000000000000000000000000000000000:0");
    }
  }

  private static void sendResponse(HttpExchange exchange, int status, String body)
      throws IOException {
    byte[] bytes = body.getBytes(StandardCharsets.UTF_8);
    exchange.sendResponseHeaders(status, bytes.length);
    try (OutputStream os = exchange.getResponseBody()) {
      os.write(bytes);
    }
  }

  @BeforeEach
  void init() {
    clientWithApiKey = HaveIBeenPwnedApiClient.builder()
        .apiKey(HaveIBeenPwnedApiClient.TEST_API_KEY)
        .baseUrl(baseUrl)
        .pwnedPasswordsBaseUrl(pwnedPasswordsBaseUrl)
        .build();
    clientWithoutApiKey = HaveIBeenPwnedApiClient.builder()
        .baseUrl(baseUrl)
        .pwnedPasswordsBaseUrl(pwnedPasswordsBaseUrl)
        .build();
  }

  @Test
  void testConstructorsAndBuilder() {
    assertNotNull(new HaveIBeenPwnedApiClient());
    assertNotNull(new HaveIBeenPwnedApiClient("key"));
    InetSocketAddress proxy = InetSocketAddress.createUnresolved("127.0.0.1", 8080);
    HaveIBeenPwnedApiClient c1 = new HaveIBeenPwnedApiClient("key", proxy);
    assertFalse(c1.getHttpClient().proxy().isEmpty());
    HaveIBeenPwnedApiClient c2 = new HaveIBeenPwnedApiClient("key", proxy, "custom-agent");
    assertEquals("custom-agent", c2.buildRequest("http://example.com").headers().firstValue("user-agent").orElseThrow());
    HaveIBeenPwnedApiClient c3 = new HaveIBeenPwnedApiClient("key", proxy, "custom-agent", Duration.ofSeconds(5));
    assertEquals(Duration.ofSeconds(5), c3.getHttpClient().connectTimeout().orElseThrow());

    HttpClient customHttp = HttpClient.newHttpClient();
    HaveIBeenPwnedApiClient c4 = HaveIBeenPwnedApiClient.builder()
        .httpClient(customHttp)
        .baseUrl("https://example.com/api/v3")
        .pwnedPasswordsBaseUrl("https://example.com/range")
        .build();
    assertSame(customHttp, c4.getHttpClient());
  }

  @Test
  void testMapperGettersAndSetters() {
    assertEquals(DefaultBreachMapper.get(), clientWithoutApiKey.getBreachMapper());
    BreachMapper bm = _ -> null;
    clientWithoutApiKey.setBreachMapper(bm);
    assertSame(bm, clientWithoutApiKey.getBreachMapper());

    assertEquals(DefaultBreachListMapper.get(), clientWithoutApiKey.getBreachListMapper());
    BreachListMapper blm = _ -> null;
    clientWithoutApiKey.setBreachListMapper(blm);
    assertSame(blm, clientWithoutApiKey.getBreachListMapper());

    assertEquals(DefaultBreachRangeMapper.get(), clientWithoutApiKey.getBreachRangeMapper());
    BreachRangeMapper brm = (_, _, _) -> List.of();
    clientWithoutApiKey.setBreachRangeMapper(brm);
    assertSame(brm, clientWithoutApiKey.getBreachRangeMapper());

    assertEquals(DefaultDataClassesListMapper.get(), clientWithoutApiKey.getDataClassesListMapper());
    DataClassesListMapper dcm = _ -> null;
    clientWithoutApiKey.setDataClassesListMapper(dcm);
    assertSame(dcm, clientWithoutApiKey.getDataClassesListMapper());

    assertEquals(DefaultDataClassDescriptionsListMapper.get(),
        clientWithoutApiKey.getDataClassDescriptionsListMapper());
    DataClassDescriptionsListMapper dcdm = _ -> null;
    clientWithoutApiKey.setDataClassDescriptionsListMapper(dcdm);
    assertSame(dcdm, clientWithoutApiKey.getDataClassDescriptionsListMapper());

    assertEquals(DefaultPwnedPasswordsMapper.get(), clientWithoutApiKey.getPwnedPasswordsMapper());
    PwnedPasswordsMapper ppm = (_, _, _) -> false;
    clientWithoutApiKey.setPwnedPasswordsMapper(ppm);
    assertSame(ppm, clientWithoutApiKey.getPwnedPasswordsMapper());

    assertEquals(DefaultPasteListMapper.get(), clientWithoutApiKey.getPasteListMapper());
    PasteListMapper plm = _ -> null;
    clientWithoutApiKey.setPasteListMapper(plm);
    assertSame(plm, clientWithoutApiKey.getPasteListMapper());

    assertEquals(DefaultSubscribedDomainsListMapper.get(),
        clientWithoutApiKey.getSubscribedDomainsListMapper());
    SubscribedDomainsListMapper sdlm = _ -> null;
    clientWithoutApiKey.setSubscribedDomainsListMapper(sdlm);
    assertSame(sdlm, clientWithoutApiKey.getSubscribedDomainsListMapper());

    assertEquals(DefaultSubscriptionStatusMapper.get(),
        clientWithoutApiKey.getSubscriptionStatusMapper());
    SubscriptionStatusMapper ssm = _ -> null;
    clientWithoutApiKey.setSubscriptionStatusMapper(ssm);
    assertSame(ssm, clientWithoutApiKey.getSubscriptionStatusMapper());

    assertEquals(DefaultDomainVerificationTokenMapper.get(),
        clientWithoutApiKey.getDomainVerificationTokenMapper());
    DomainVerificationTokenMapper dvtm = _ -> null;
    clientWithoutApiKey.setDomainVerificationTokenMapper(dvtm);
    assertSame(dvtm, clientWithoutApiKey.getDomainVerificationTokenMapper());

    assertEquals(DefaultStringListMapMapper.get(), clientWithoutApiKey.getStringListMapMapper());
    StringListMapMapper slmm = _ -> null;
    clientWithoutApiKey.setStringListMapMapper(slmm);
    assertSame(slmm, clientWithoutApiKey.getStringListMapMapper());
  }

  @Test
  void testBuildRequest() {
    HttpRequest request = clientWithApiKey.buildRequest("http://example.com");
    assertEquals("GET", request.method());
    assertEquals(URI.create("http://example.com"), request.uri());
    HttpHeaders headers = request.headers();
    assertEquals(HaveIBeenPwnedApiClient.TEST_API_KEY, headers.firstValue("hibp-api-key").orElseThrow());
    assertEquals(HaveIBeenPwnedApiClient.class.getName(), headers.firstValue("user-agent").orElseThrow());
  }

  @Test
  void testGetBreachesForAccountString() {
    List<Breach> breachesForAccount = clientWithApiKey.getBreachesForAccount("foo.bar@example.com");
    assertFalse(breachesForAccount.isEmpty());
    breachesForAccount.forEach(b -> {
      assertNotNull(b.addedDate());
      assertNotNull(b.breachDate());
      assertFalse(b.dataClasses().isEmpty());
      assertNotNull(b.description());
      assertNotNull(b.domain());
      assertNotNull(b.logoPath());
      assertNotNull(b.modifiedDate());
      assertNotNull(b.name());
      assertNotNull(b.pwnCount());
      assertNotNull(b.title());
    });
  }

  @Test
  void testGetBreachesForAccountStringStringBooleanBoolean() {
    List<Breach> breachesForAccount =
        clientWithApiKey.getBreachesForAccount("foo.bar@example.com", "adobe.com", true, false);
    assertFalse(breachesForAccount.isEmpty());
    breachesForAccount.forEach(b -> {
      assertNull(b.addedDate());
      assertNull(b.breachDate());
      assertTrue(b.dataClasses() == null || b.dataClasses().isEmpty());
      assertNull(b.description());
      assertNull(b.domain());
      assertNull(b.logoPath());
      assertNull(b.modifiedDate());
      assertNotNull(b.name());
      assertNull(b.pwnCount());
      assertNull(b.title());
    });
  }

  @Test
  void testGetBreachesForAccountNonExisting() {
    List<Breach> breachesForAccount =
        clientWithApiKey.getBreachesForAccount("notfound@example.com");
    assertTrue(breachesForAccount.isEmpty());
  }

  @Test
  void testGetBreachesForAccountStringNoApiKey() {
    HaveIBeenPwnedException ex = assertThrows(HaveIBeenPwnedException.class,
        () -> clientWithoutApiKey.getBreachesForAccount("foo.bar@example.com"));
    assertEquals(Status.UNAUTHORIZED, ex.getStatus().orElseThrow());
  }

  @Test
  void testRateLimitExceptionIncludesRetryAfter() {
    HaveIBeenPwnedException ex = assertThrows(HaveIBeenPwnedException.class,
        () -> clientWithApiKey.getBreachesForAccount("rate-limited@example.com"));
    assertEquals(Status.TOO_MANY_REQUESTS, ex.getStatus().orElseThrow());
    assertEquals(5L, ex.getRetryAfterSeconds().orElseThrow());

    HaveIBeenPwnedException rangeEx = assertThrows(HaveIBeenPwnedException.class,
        () -> clientWithApiKey.getBreachNamesForAccountByRange("rate-limited@example.com"));
    assertEquals(Status.TOO_MANY_REQUESTS, rangeEx.getStatus().orElseThrow());
    assertEquals(5L, rangeEx.getRetryAfterSeconds().orElseThrow());
  }

  @Test
  void testGetBreachNamesForAccountByRange() {
    List<String> names = clientWithApiKey
        .getBreachNamesForAccountByRange("multiple-breaches@hibp-integration-tests.com");
    assertEquals(List.of("Adobe", "Gawker", "Stratfor"), names);
  }

  @Test
  void testGetBreaches() {
    assertFalse(clientWithoutApiKey.getBreaches().isEmpty());
    List<Breach> breaches = clientWithoutApiKey.getBreaches("adobe.com", false);
    assertEquals(1, breaches.size());
    Breach b = breaches.getFirst();
    assertEquals("Adobe", b.name());
    assertEquals("adobe.com", b.domain());
    assertFalse(b.malware());
    assertFalse(b.stealerLog());
    assertFalse(b.subscriptionFree());
  }

  @Test
  void testGetSingleBreachAndLatestBreach() {
    Breach b = clientWithoutApiKey.getSingleBreach("Adobe");
    assertNotNull(b);
    assertEquals("Adobe", b.name());
    assertNull(clientWithoutApiKey.getSingleBreach("NonExisting"));

    Breach latest = clientWithoutApiKey.getLatestBreach();
    assertNotNull(latest);
    assertEquals("Adobe", latest.name());
  }

  @Test
  void testGetDataClassesAndDescriptions() {
    List<String> dataClasses = clientWithoutApiKey.getDataClasses();
    assertEquals(List.of("Email addresses", "Passwords"), dataClasses);

    List<DataClassDescription> descriptions = clientWithoutApiKey.getDataClassDescriptions();
    assertEquals(2, descriptions.size());
    assertEquals("Passwords", descriptions.getFirst().name());
    assertEquals("Exposed passwords", descriptions.getFirst().description());
    assertEquals("Email addresses", descriptions.get(1).name());
    assertEquals("Exposed emails", descriptions.get(1).description());
  }

  @Test
  void testGetPastesForAccount() {
    List<Paste> pastes = clientWithApiKey.getPastesForAccount("account-exists@hibp-integration-tests.com");
    assertEquals(1, pastes.size());
    Paste p = pastes.getFirst();
    assertNotNull(p.date());
    assertEquals(139, p.emailCount());
    assertEquals("8Q0BvKD8", p.id());
    assertEquals("Pastebin", p.source());
    assertEquals("syslog", p.title());
  }

  @Test
  void testGetPastesForNonExistingAndUnauthorized() {
    assertTrue(clientWithApiKey.getPastesForAccount("notfound@gmail.com").isEmpty());
    assertThrows(HaveIBeenPwnedException.class,
        () -> clientWithoutApiKey.getPastesForAccount("foo.bar@example.com"));
  }

  @Test
  void testDomainAndStealerLogAndSubscriptionEndpoints() {
    Map<String, List<String>> breachedDomain = clientWithApiKey.getBreachedDomain("example.com");
    assertEquals(List.of("Adobe", "Gawker"), breachedDomain.get("jane"));

    List<SubscribedDomain> subscribedDomains = clientWithApiKey.getSubscribedDomains();
    assertEquals(1, subscribedDomains.size());
    assertEquals("example.com", subscribedDomains.getFirst().domainName());

    DomainVerificationToken token = clientWithApiKey.generateDnsVerificationToken("example.com");
    assertEquals("hibp-verify=dweb_abc123", token.txtRecordValue());

    assertTrue(clientWithApiKey.verifyDnsToken("example.com"));
    assertFalse(clientWithApiKey.verifyDnsToken("unverified.example.com"));
    clientWithApiKey.sendVerificationEmail("example.com", EmailAlias.SECURITY);
    assertThrows(HaveIBeenPwnedException.class,
        () -> clientWithApiKey.sendVerificationEmail("notfound.example.com", EmailAlias.SECURITY));

    assertEquals(List.of("netflix.com", "spotify.com"),
        clientWithApiKey.getStealerLogsByEmail("jane@example.com"));
    assertEquals(List.of("jane@gmail.com"),
        clientWithApiKey.getStealerLogsByWebsiteDomain("netflix.com"));
    assertEquals(Map.of("jane", List.of("netflix.com")),
        clientWithApiKey.getStealerLogsByEmailDomain("gmail.com"));

    SubscriptionStatus status = clientWithApiKey.getSubscriptionStatus();
    assertEquals("Pro 2", status.subscriptionName());
    assertEquals(100, status.rpm());
    assertTrue(status.includesStealerLogs());
  }

  @Test
  void testPwnedPasswords() {
    assertTrue(clientWithApiKey.isPasswordPwned("password123"));
    assertEquals(12345L, clientWithApiKey.getPasswordPwnCount("password123"));
    assertFalse(clientWithoutApiKey.isPasswordPwned("unbreached-random-password"));
    assertEquals(0L, clientWithoutApiKey.getPasswordPwnCount("unbreached-random-password"));

    assertTrue(clientWithoutApiKey.isPasswordPwned("password", HashMode.NTLM));
    assertEquals(99L, clientWithoutApiKey.getPasswordPwnCount("password", HashMode.NTLM));
    assertEquals("ntlm", HashMode.NTLM.queryValue());
    assertEquals("sha1", HashMode.SHA1.queryValue());
  }
}

