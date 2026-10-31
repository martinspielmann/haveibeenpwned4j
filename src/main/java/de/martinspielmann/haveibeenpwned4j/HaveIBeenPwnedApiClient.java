package de.martinspielmann.haveibeenpwned4j;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ProxySelector;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpRequest.Builder;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandler;
import java.net.http.HttpResponse.BodyHandlers;
import java.net.http.HttpResponse.BodySubscribers;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.logging.Logger;
import com.fasterxml.jackson.core.JsonProcessingException;
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
import de.martinspielmann.haveibeenpwned4j.mapper.Mapper;
import de.martinspielmann.haveibeenpwned4j.mapper.PasteListMapper;
import de.martinspielmann.haveibeenpwned4j.mapper.PwnedPasswordsMapper;
import de.martinspielmann.haveibeenpwned4j.mapper.ResponseMapper;
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

/**
 * This API client implements all APIs provided by Troy Hunt's ';-- Have I Been Pwned: API v3.
 *
 * @see <a href="https://haveibeenpwned.com/API/v3">https://haveibeenpwned.com/API/v3</a>
 * @author Martin Spielmann
 */
public class HaveIBeenPwnedApiClient {

  /** Official HIBP test API key for querying {@code @hibp-integration-tests.com} test accounts. */
  public static final String TEST_API_KEY = "00000000000000000000000000000000";

  /** The Logger used throughout this class. */
  private static final Logger LOG = Logger.getLogger(HaveIBeenPwnedApiClient.class.getName());

  /** The default URL to version 3 of the Have I Been Pwned API. */
  private static final String HIBP_API_URL_V3 = "https://haveibeenpwned.com/api/v3/";

  /** The default URL to the Pwned Passwords range API. */
  private static final String PWNED_PASSWORDS_URL = "https://api.pwnedpasswords.com/range/";

  private final String hibpApiKey;
  private final String userAgent;
  private final HttpClient httpClient;
  private final String baseUrl;
  private final String pwnedPasswordsBaseUrl;

  private BreachMapper breachMapper;
  private BreachListMapper breachListMapper;
  private BreachRangeMapper breachRangeMapper;
  private PwnedPasswordsMapper pwnedPasswordsMapper;
  private DataClassesListMapper dataClassesListMapper;
  private DataClassDescriptionsListMapper dataClassDescriptionsListMapper;
  private PasteListMapper pasteListMapper;
  private SubscribedDomainsListMapper subscribedDomainsListMapper;
  private SubscriptionStatusMapper subscriptionStatusMapper;
  private DomainVerificationTokenMapper domainVerificationTokenMapper;
  private StringListMapMapper stringListMapMapper;

  /**
   * Instantiates a new API client without an API key.
   *
   * <p><strong>Watch out:</strong> Authorization is required for all APIs that enable searching
   * HIBP by email address or domain.
   *
   * @see <a href="https://haveibeenpwned.com/API/v3#Authorisation">HIBP API v3 Authorisation</a>
   * @see <a href="https://haveibeenpwned.com/API/Key">HIBP API Key</a>
   */
  public HaveIBeenPwnedApiClient() {
    this(null, null, null, null);
  }

  /**
   * Instantiates a new API client with an API key.
   *
   * @param hibpApiKey the API key
   * @see <a href="https://haveibeenpwned.com/API/v3#Authorisation">HIBP API v3 Authorisation</a>
   * @see <a href="https://haveibeenpwned.com/API/Key">HIBP API Key</a>
   */
  public HaveIBeenPwnedApiClient(String hibpApiKey) {
    this(hibpApiKey, null, null, null);
  }

  /**
   * Instantiates a new API client which uses a proxy server.
   *
   * @param hibpApiKey the API key
   * @param proxy the proxy
   */
  public HaveIBeenPwnedApiClient(String hibpApiKey, InetSocketAddress proxy) {
    this(hibpApiKey, proxy, null, null);
  }

  /**
   * Instantiates a new API client with a proxy and custom user agent.
   *
   * @param hibpApiKey the API key
   * @param proxy the proxy
   * @param userAgent the user agent
   */
  public HaveIBeenPwnedApiClient(String hibpApiKey, InetSocketAddress proxy, String userAgent) {
    this(hibpApiKey, proxy, userAgent, null);
  }

  /**
   * Instantiates a new API client with a proxy, custom user agent, and connection timeout.
   *
   * @param hibpApiKey the API key
   * @param proxy the proxy
   * @param userAgent the user agent
   * @param connectionTimeout the connection timeout duration for the HttpClient
   */
  public HaveIBeenPwnedApiClient(String hibpApiKey, InetSocketAddress proxy, String userAgent,
      Duration connectionTimeout) {
    this(builder()
        .apiKey(hibpApiKey)
        .proxy(proxy)
        .userAgent(userAgent)
        .connectionTimeout(connectionTimeout));
  }

  private HaveIBeenPwnedApiClient(ClientBuilder builder) {
    if (builder.hibpApiKey != null) {
      this.hibpApiKey = builder.hibpApiKey;
    } else {
      this.hibpApiKey = null;
      LOG.warning(
          "HaveIBeenPwnedApiClient was initialized without API key. Only APIs that work without an API key will be supported.");
    }
    if (builder.httpClient != null) {
      this.httpClient = builder.httpClient;
    } else {
      HttpClient.Builder httpBuilder = HttpClient.newBuilder();
      if (builder.proxy != null) {
        httpBuilder.proxy(ProxySelector.of(builder.proxy));
      }
      if (builder.connectionTimeout != null) {
        httpBuilder.connectTimeout(builder.connectionTimeout);
      }
      this.httpClient = httpBuilder.build();
    }
    this.userAgent = builder.userAgent != null
        ? builder.userAgent
        : HaveIBeenPwnedApiClient.class.getName();
    this.baseUrl = normalizeTrailingSlash(
        builder.baseUrl != null ? builder.baseUrl : HIBP_API_URL_V3);
    this.pwnedPasswordsBaseUrl = normalizeTrailingSlash(
        builder.pwnedPasswordsBaseUrl != null ? builder.pwnedPasswordsBaseUrl : PWNED_PASSWORDS_URL);
  }

  /**
   * Creates a new fluent builder for {@link HaveIBeenPwnedApiClient}.
   *
   * @return a new client builder
   */
  public static ClientBuilder builder() {
    return new ClientBuilder();
  }

  private static String normalizeTrailingSlash(String url) {
    return url.endsWith("/") ? url : url + "/";
  }

  /**
   * Fluent builder for configuring {@link HaveIBeenPwnedApiClient}.
   */
  public static final class ClientBuilder {

    private String hibpApiKey;
    private InetSocketAddress proxy;
    private String userAgent;
    private Duration connectionTimeout;
    private HttpClient httpClient;
    private String baseUrl;
    private String pwnedPasswordsBaseUrl;

    private ClientBuilder() {
    }

    public ClientBuilder apiKey(String hibpApiKey) {
      this.hibpApiKey = hibpApiKey;
      return this;
    }

    public ClientBuilder proxy(InetSocketAddress proxy) {
      this.proxy = proxy;
      return this;
    }

    public ClientBuilder userAgent(String userAgent) {
      this.userAgent = userAgent;
      return this;
    }

    public ClientBuilder connectionTimeout(Duration connectionTimeout) {
      this.connectionTimeout = connectionTimeout;
      return this;
    }

    public ClientBuilder httpClient(HttpClient httpClient) {
      this.httpClient = httpClient;
      return this;
    }

    public ClientBuilder baseUrl(String baseUrl) {
      this.baseUrl = baseUrl;
      return this;
    }

    public ClientBuilder pwnedPasswordsBaseUrl(String pwnedPasswordsBaseUrl) {
      this.pwnedPasswordsBaseUrl = pwnedPasswordsBaseUrl;
      return this;
    }

    public HaveIBeenPwnedApiClient build() {
      return new HaveIBeenPwnedApiClient(this);
    }
  }

  /*
   * Getters and setters
   */

  public HttpClient getHttpClient() {
    return httpClient;
  }

  public BreachMapper getBreachMapper() {
    if (this.breachMapper == null) {
      this.breachMapper = DefaultBreachMapper.get();
    }
    return this.breachMapper;
  }

  public void setBreachMapper(BreachMapper breachMapper) {
    this.breachMapper = breachMapper;
  }

  public BreachListMapper getBreachListMapper() {
    if (this.breachListMapper == null) {
      this.breachListMapper = DefaultBreachListMapper.get();
    }
    return this.breachListMapper;
  }

  public void setBreachListMapper(BreachListMapper breachListMapper) {
    this.breachListMapper = breachListMapper;
  }

  public BreachRangeMapper getBreachRangeMapper() {
    if (this.breachRangeMapper == null) {
      this.breachRangeMapper = DefaultBreachRangeMapper.get();
    }
    return this.breachRangeMapper;
  }

  public void setBreachRangeMapper(BreachRangeMapper breachRangeMapper) {
    this.breachRangeMapper = breachRangeMapper;
  }

  public DataClassesListMapper getDataClassesListMapper() {
    if (this.dataClassesListMapper == null) {
      this.dataClassesListMapper = DefaultDataClassesListMapper.get();
    }
    return this.dataClassesListMapper;
  }

  public void setDataClassesListMapper(DataClassesListMapper dataClassesListMapper) {
    this.dataClassesListMapper = dataClassesListMapper;
  }

  public DataClassDescriptionsListMapper getDataClassDescriptionsListMapper() {
    if (this.dataClassDescriptionsListMapper == null) {
      this.dataClassDescriptionsListMapper = DefaultDataClassDescriptionsListMapper.get();
    }
    return this.dataClassDescriptionsListMapper;
  }

  public void setDataClassDescriptionsListMapper(
      DataClassDescriptionsListMapper dataClassDescriptionsListMapper) {
    this.dataClassDescriptionsListMapper = dataClassDescriptionsListMapper;
  }

  public PwnedPasswordsMapper getPwnedPasswordsMapper() {
    if (this.pwnedPasswordsMapper == null) {
      this.pwnedPasswordsMapper = DefaultPwnedPasswordsMapper.get();
    }
    return this.pwnedPasswordsMapper;
  }

  public void setPwnedPasswordsMapper(PwnedPasswordsMapper pwnedPasswordsMapper) {
    this.pwnedPasswordsMapper = pwnedPasswordsMapper;
  }

  public PasteListMapper getPasteListMapper() {
    if (this.pasteListMapper == null) {
      this.pasteListMapper = DefaultPasteListMapper.get();
    }
    return this.pasteListMapper;
  }

  public void setPasteListMapper(PasteListMapper pasteListMapper) {
    this.pasteListMapper = pasteListMapper;
  }

  public SubscribedDomainsListMapper getSubscribedDomainsListMapper() {
    if (this.subscribedDomainsListMapper == null) {
      this.subscribedDomainsListMapper = DefaultSubscribedDomainsListMapper.get();
    }
    return this.subscribedDomainsListMapper;
  }

  public void setSubscribedDomainsListMapper(
      SubscribedDomainsListMapper subscribedDomainsListMapper) {
    this.subscribedDomainsListMapper = subscribedDomainsListMapper;
  }

  public SubscriptionStatusMapper getSubscriptionStatusMapper() {
    if (this.subscriptionStatusMapper == null) {
      this.subscriptionStatusMapper = DefaultSubscriptionStatusMapper.get();
    }
    return this.subscriptionStatusMapper;
  }

  public void setSubscriptionStatusMapper(SubscriptionStatusMapper subscriptionStatusMapper) {
    this.subscriptionStatusMapper = subscriptionStatusMapper;
  }

  public DomainVerificationTokenMapper getDomainVerificationTokenMapper() {
    if (this.domainVerificationTokenMapper == null) {
      this.domainVerificationTokenMapper = DefaultDomainVerificationTokenMapper.get();
    }
    return this.domainVerificationTokenMapper;
  }

  public void setDomainVerificationTokenMapper(
      DomainVerificationTokenMapper domainVerificationTokenMapper) {
    this.domainVerificationTokenMapper = domainVerificationTokenMapper;
  }

  public StringListMapMapper getStringListMapMapper() {
    if (this.stringListMapMapper == null) {
      this.stringListMapMapper = DefaultStringListMapMapper.get();
    }
    return this.stringListMapMapper;
  }

  public void setStringListMapMapper(StringListMapMapper stringListMapMapper) {
    this.stringListMapMapper = stringListMapMapper;
  }

  /*
   * Helper methods below
   */

  /**
   * Builds a GET request for the HIBP API.
   *
   * @param url the URL
   * @return the HTTP request
   */
  protected HttpRequest buildRequest(String url) {
    return buildRequest(url, true);
  }

  /**
   * Builds a GET request, optionally including the HIBP API key header.
   *
   * @param url the URL
   * @param includeApiKey whether to attach the {@code hibp-api-key} header when configured
   * @return the HTTP request
   */
  protected HttpRequest buildRequest(String url, boolean includeApiKey) {
    Builder builder = HttpRequest.newBuilder()
        .uri(URI.create(url))
        .header("user-agent", userAgent)
        .header("Add-Padding", "true");
    if (includeApiKey && hibpApiKey != null) {
      builder.header("hibp-api-key", hibpApiKey);
    }
    return builder.build();
  }

  /**
   * Builds a POST request with a JSON payload for the HIBP API.
   *
   * @param url the URL
   * @param payload the object to serialize as JSON
   * @return the HTTP request
   */
  protected HttpRequest buildPostRequest(String url, Object payload) {
    String jsonBody;
    try {
      jsonBody = ResponseMapper.toJson(payload);
    } catch (JsonProcessingException e) {
      throw new HaveIBeenPwnedException("Error serializing request payload", e);
    }
    Builder builder = HttpRequest.newBuilder()
        .uri(URI.create(url))
        .header("user-agent", userAgent)
        .header("content-type", "application/json")
        .POST(BodyPublishers.ofString(jsonBody, StandardCharsets.UTF_8));
    if (hibpApiKey != null) {
      builder.header("hibp-api-key", hibpApiKey);
    }
    return builder.build();
  }

  private <T> BodyHandler<T> internalBodyHandler(Mapper<T> mapper) {
    return responseInfo -> {
      Status s = Status.of(responseInfo.statusCode());
      if (s.isError()) {
        Long retryAfter = parseRetryAfter(responseInfo.headers());
        throw new HaveIBeenPwnedException("Error handling body", s, retryAfter);
      }
      if (s == Status.NOT_FOUND) {
        return BodySubscribers.replacing(mapper.map(null));
      }
      return BodySubscribers.mapping(
          BodySubscribers.ofString(StandardCharsets.UTF_8),
          mapper::map);
    };
  }

  private static Long parseRetryAfter(java.net.http.HttpHeaders headers) {
    return headers
        .firstValue("retry-after")
        .map(val -> {
          try {
            return Long.parseLong(val.trim());
          } catch (NumberFormatException _) {
            return null;
          }
        })
        .orElse(null);
  }

  private <T> T internalApiRequest(String url, BodyHandler<T> bodyHandler, String errorMessage) {
    return executeRequest(buildRequest(url), bodyHandler, errorMessage);
  }

  private <T> T executeRequest(HttpRequest request, BodyHandler<T> bodyHandler,
      String errorMessage) {
    try {
      return getHttpClient().send(request, bodyHandler).body();
    } catch (IOException e) {
      throw new HaveIBeenPwnedException(errorMessage, e);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
      throw new HaveIBeenPwnedException(errorMessage, e);
    }
  }

  /*
   * Email Address Search APIs
   */

  /**
   * Gets all breaches for an email address (truncated=false, includeUnverified=true).
   *
   * @param account the email address
   * @return the breaches for the account
   */
  public List<Breach> getBreachesForAccount(String account) {
    return getBreachesForAccount(account, null, false, true);
  }

  /**
   * Gets all breaches for an email address with optional filters.
   *
   * @param account the email address
   * @param domain optional domain filter
   * @param truncateResponse whether to return only breach names
   * @param includeUnverified whether to include unverified breaches
   * @return the breaches for the account
   */
  public List<Breach> getBreachesForAccount(String account, String domain, boolean truncateResponse,
      boolean includeUnverified) {
    Objects.requireNonNull(account, "account must not be null");
    String url = baseUrl + "breachedaccount/" + StringHelper.urlEncode(account)
        + "?truncateResponse=" + truncateResponse
        + "&includeUnverified=" + includeUnverified;
    if (domain != null) {
      url += "&domain=" + StringHelper.urlEncode(domain);
    }
    return internalApiRequest(url, internalBodyHandler(getBreachListMapper()),
        "Error checking for breaches");
  }

  /**
   * Gets all breach names for an email address using k-anonymity (6-character SHA-1 hash prefix).
   * Any non-matching hash suffixes returned by the API are immediately discarded.
   *
   * @param account the email address
   * @return list of breach names the email address has appeared in
   * @see <a href="https://haveibeenpwned.com/API/v3#BreachesForHashRange">Breaches for hash range</a>
   */
  public List<String> getBreachNamesForAccountByRange(String account) {
    Objects.requireNonNull(account, "account must not be null");
    String prefix = StringHelper.getAccountHashPrefix(account);
    String url = baseUrl + "breachedaccount/range/" + prefix;
    try {
      HttpResponse<String> res = getHttpClient().send(buildRequest(url),
          BodyHandlers.ofString(StandardCharsets.UTF_8));
      Status status = Status.of(res.statusCode());
      if (status.isError()) {
        throw new HaveIBeenPwnedException("Error checking for breaches by hash range",
            status, parseRetryAfter(res.headers()));
      }
      return getBreachRangeMapper().map(res.statusCode(), res.body(), account);
    } catch (IOException e) {
      throw new HaveIBeenPwnedException("Error checking for breaches by hash range", e);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
      throw new HaveIBeenPwnedException("Error checking for breaches by hash range", e);
    }
  }

  /*
   * Domain Search & Verification APIs
   */

  /**
   * Gets all breached email aliases and their associated breach names for a verified domain.
   *
   * @param domain the verified domain name
   * @return map of email alias to list of breach names
   * @see <a href="https://haveibeenpwned.com/API/v3#BreachesForDomain">Breaches for domain</a>
   */
  public Map<String, List<String>> getBreachedDomain(String domain) {
    Objects.requireNonNull(domain, "domain must not be null");
    String url = baseUrl + "breacheddomain/" + StringHelper.urlEncode(domain);
    return internalApiRequest(url, internalBodyHandler(getStringListMapMapper()),
        "Error checking for breached domain " + domain);
  }

  /**
   * Gets all verified domains associated with the current HIBP API key.
   *
   * @return list of subscribed domains
   * @see <a href="https://haveibeenpwned.com/API/v3#SubscribedDomains">Subscribed domains</a>
   */
  public List<SubscribedDomain> getSubscribedDomains() {
    String url = baseUrl + "subscribeddomains";
    return internalApiRequest(url, internalBodyHandler(getSubscribedDomainsListMapper()),
        "Error checking for subscribed domains");
  }

  /**
   * Generates a DNS TXT record verification token for a domain.
   *
   * @param domainName the domain name to verify
   * @return the DNS verification token containing the TXT record value
   * @see <a href="https://haveibeenpwned.com/API/v3#VerifyDomainViaDns">Verifying domains via DNS</a>
   */
  public DomainVerificationToken generateDnsVerificationToken(String domainName) {
    Objects.requireNonNull(domainName, "domainName must not be null");
    String url = baseUrl + "domainverification/generatednstoken";
    HttpRequest request = buildPostRequest(url, Map.of("DomainName", domainName));
    return executeRequest(request, internalBodyHandler(getDomainVerificationTokenMapper()),
        "Error generating DNS verification token for domain " + domainName);
  }

  /**
   * Verifies that the DNS TXT verification record has been set on the given domain.
   *
   * @param domainName the domain name to verify
   * @return true if verification succeeded (HTTP 200)
   * @see <a href="https://haveibeenpwned.com/API/v3#VerifyDomainViaDns">Verifying domains via DNS</a>
   */
  public boolean verifyDnsToken(String domainName) {
    Objects.requireNonNull(domainName, "domainName must not be null");
    String url = baseUrl + "domainverification/verifydnstoken";
    HttpRequest request = buildPostRequest(url, Map.of("DomainName", domainName));
    return executeRequest(request, internalBodyHandler(Objects::nonNull),
        "Error verifying DNS token for domain " + domainName);
  }

  /**
   * Sends a domain verification email to one of the allowable aliases on the domain.
   *
   * @param domainName the domain name to verify
   * @param emailAlias the allowable recipient alias on the domain
   * @see <a href="https://haveibeenpwned.com/API/v3#VerifyDomainViaEmail">Verifying domains via email</a>
   */
  public void sendVerificationEmail(String domainName, EmailAlias emailAlias) {
    Objects.requireNonNull(domainName, "domainName must not be null");
    Objects.requireNonNull(emailAlias, "emailAlias must not be null");
    String url = baseUrl + "domainverification/sendemail";
    HttpRequest request = buildPostRequest(url,
        Map.of("DomainName", domainName, "EmailAlias", emailAlias.value()));
    executeRequest(request, internalBodyHandler(input -> {
      if (input == null) {
        throw new HaveIBeenPwnedException(
            "Domain verification endpoint not found for domain " + domainName, Status.NOT_FOUND);
      }
      return null;
    }), "Error sending verification email for domain " + domainName);
  }

  /*
   * Breaches & Data Classes APIs
   */

  /**
   * Gets all breached sites in the system.
   *
   * @return list of all breaches
   */
  public List<Breach> getBreaches() {
    return getBreaches(null, null);
  }

  /**
   * Gets all breached sites in the system, optionally filtered by domain.
   *
   * @param domain optional domain filter
   * @return list of breaches
   */
  public List<Breach> getBreaches(String domain) {
    return getBreaches(domain, null);
  }

  /**
   * Gets all breached sites in the system, optionally filtered by domain and/or spam list flag.
   *
   * @param domain optional domain filter
   * @param isSpamList optional filter for breaches flagged (or not flagged) as a spam list
   * @return list of breaches
   * @see <a href="https://haveibeenpwned.com/API/v3#AllBreaches">Getting all breached sites</a>
   */
  public List<Breach> getBreaches(String domain, Boolean isSpamList) {
    String url = baseUrl + "breaches";
    List<String> queryParams = new ArrayList<>(2);
    if (domain != null) {
      queryParams.add("domain=" + StringHelper.urlEncode(domain));
    }
    if (isSpamList != null) {
      queryParams.add("isSpamList=" + isSpamList);
    }
    if (!queryParams.isEmpty()) {
      url += "?" + String.join("&", queryParams);
    }
    return internalApiRequest(url, internalBodyHandler(getBreachListMapper()),
        "Error checking for breaches");
  }

  /**
   * Gets a single breach based on the provided breach name.
   *
   * @param name the name of the breach
   * @return the breach, or null if not found
   * @throws HaveIBeenPwnedException if something unexpected happens during the request
   * @see <a href="https://haveibeenpwned.com/API/v3#SingleBreach">Getting a single breached site</a>
   */
  public Breach getSingleBreach(String name) {
    Objects.requireNonNull(name, "name must not be null");
    return internalApiRequest(baseUrl + "breach/" + StringHelper.urlEncode(name),
        internalBodyHandler(getBreachMapper()), "Error checking for breach with name " + name);
  }

  /**
   * Gets the most recently added breach in the system.
   *
   * @return the latest breach
   * @see <a href="https://haveibeenpwned.com/API/v3#MostRecentBreach">Getting the most recently added breach</a>
   */
  public Breach getLatestBreach() {
    return internalApiRequest(baseUrl + "latestbreach",
        internalBodyHandler(getBreachMapper()), "Error checking for latest breach");
  }

  /**
   * Gets all data classes in the system.
   *
   * @return alphabetically ordered list of data class names
   */
  public List<String> getDataClasses() {
    return internalApiRequest(baseUrl + "dataclasses",
        internalBodyHandler(getDataClassesListMapper()), "Error checking for data classes");
  }

  /**
   * Gets all data classes in the system along with their human-readable descriptions.
   *
   * @return alphabetically ordered list of data class descriptions
   * @see <a href="https://haveibeenpwned.com/API/v3#DataClassDescriptions">Getting data class descriptions</a>
   */
  public List<DataClassDescription> getDataClassDescriptions() {
    return internalApiRequest(baseUrl + "dataclasses/descriptions",
        internalBodyHandler(getDataClassDescriptionsListMapper()),
        "Error checking for data class descriptions");
  }

  /*
   * Stealer Logs APIs
   */

  /**
   * Gets all stealer log website domains for an email address.
   *
   * @param email the email address
   * @return alphabetically sorted list of website domains
   * @see <a href="https://haveibeenpwned.com/API/v3#StealerLogsForEmail">Stealer logs for email</a>
   */
  public List<String> getStealerLogsByEmail(String email) {
    Objects.requireNonNull(email, "email must not be null");
    String url = baseUrl + "stealerlogsbyemail/" + StringHelper.urlEncode(email);
    return internalApiRequest(url, internalBodyHandler(getDataClassesListMapper()),
        "Error checking for stealer logs for email");
  }

  /**
   * Gets all stealer log email addresses for a website domain.
   *
   * @param websiteDomain the website domain
   * @return alphabetically sorted list of email addresses
   * @see <a href="https://haveibeenpwned.com/API/v3#StealerLogsForWebsiteDomain">Stealer logs for website domain</a>
   */
  public List<String> getStealerLogsByWebsiteDomain(String websiteDomain) {
    Objects.requireNonNull(websiteDomain, "websiteDomain must not be null");
    String url = baseUrl + "stealerlogsbywebsitedomain/" + StringHelper.urlEncode(websiteDomain);
    return internalApiRequest(url, internalBodyHandler(getDataClassesListMapper()),
        "Error checking for stealer logs for website domain " + websiteDomain);
  }

  /**
   * Gets all stealer log email aliases and associated website domains for an email domain.
   *
   * @param emailDomain the email domain
   * @return map of email alias to list of website domains
   * @see <a href="https://haveibeenpwned.com/API/v3#StealerLogsForEmailDomain">Stealer logs for email domain</a>
   */
  public Map<String, List<String>> getStealerLogsByEmailDomain(String emailDomain) {
    Objects.requireNonNull(emailDomain, "emailDomain must not be null");
    String url = baseUrl + "stealerlogsbyemaildomain/" + StringHelper.urlEncode(emailDomain);
    return internalApiRequest(url, internalBodyHandler(getStringListMapMapper()),
        "Error checking for stealer logs for email domain " + emailDomain);
  }

  /*
   * Pastes API
   */

  /**
   * Gets all pastes for an email address.
   *
   * @param account the email address
   * @return list of pastes for the account
   */
  public List<Paste> getPastesForAccount(String account) {
    Objects.requireNonNull(account, "account must not be null");
    String url = baseUrl + "pasteaccount/" + StringHelper.urlEncode(account);
    return internalApiRequest(url, internalBodyHandler(getPasteListMapper()),
        "Error checking for pastes");
  }

  /*
   * Subscription API
   */

  /**
   * Gets the status and capabilities of the current HIBP subscription.
   *
   * @return the subscription status
   * @see <a href="https://haveibeenpwned.com/API/v3#SubscriptionStatus">Subscription status</a>
   */
  public SubscriptionStatus getSubscriptionStatus() {
    String url = baseUrl + "subscription/status";
    return internalApiRequest(url, internalBodyHandler(getSubscriptionStatusMapper()),
        "Error checking for subscription status");
  }

  /*
   * Pwned Passwords APIs
   */

  /**
   * Checks if the provided password has been pwned using the SHA-1 k-anonymity range API.
   * The full password or hash is never sent over the network.
   *
   * @param password the password to be checked
   * @return true if the password has been exposed in a data breach
   */
  public boolean isPasswordPwned(String password) {
    return isPasswordPwned(password, HashMode.SHA1);
  }

  /**
   * Checks if the provided password has been pwned using the k-anonymity range API in the specified
   * hash mode (SHA-1 or NTLM).
   *
   * @param password the password to be checked
   * @param hashMode the hash mode ({@link HashMode#SHA1} or {@link HashMode#NTLM})
   * @return true if the password has been exposed in a data breach
   */
  public boolean isPasswordPwned(String password, HashMode hashMode) {
    Objects.requireNonNull(password, "password must not be null");
    Objects.requireNonNull(hashMode, "hashMode must not be null");
    if (hashMode == HashMode.SHA1) {
      HttpResponse<String> res = fetchPwnedPasswordsRange(password, HashMode.SHA1);
      return getPwnedPasswordsMapper().map(res.statusCode(), res.body(), password);
    }
    return getPasswordPwnCount(password, hashMode) > 0;
  }

  /**
   * Gets the number of times the provided password has appeared in data breaches (SHA-1 mode).
   *
   * @param password the password to be checked
   * @return the breach count (0 if not pwned)
   */
  public long getPasswordPwnCount(String password) {
    return getPasswordPwnCount(password, HashMode.SHA1);
  }

  /**
   * Gets the number of times the provided password has appeared in data breaches using the
   * specified hash mode (SHA-1 or NTLM).
   *
   * @param password the password to be checked
   * @param hashMode the hash mode ({@link HashMode#SHA1} or {@link HashMode#NTLM})
   * @return the breach count (0 if not pwned)
   */
  public long getPasswordPwnCount(String password, HashMode hashMode) {
    Objects.requireNonNull(password, "password must not be null");
    Objects.requireNonNull(hashMode, "hashMode must not be null");
    HttpResponse<String> res = fetchPwnedPasswordsRange(password, hashMode);
    return getPwnedPasswordsMapper().mapCount(res.statusCode(), res.body(), password, hashMode);
  }

  private HttpResponse<String> fetchPwnedPasswordsRange(String password, HashMode hashMode) {
    String prefix = hashMode == HashMode.NTLM
        ? StringHelper.getNtlmHashPrefix(password)
        : StringHelper.getHashPrefix(password);
    String url = pwnedPasswordsBaseUrl + prefix;
    if (hashMode == HashMode.NTLM) {
      url += "?mode=ntlm";
    }
    try {
      return getHttpClient().send(buildRequest(url, false),
          BodyHandlers.ofString(StandardCharsets.UTF_8));
    } catch (IOException e) {
      throw new HaveIBeenPwnedException("Error checking for pwned password", e);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
      throw new HaveIBeenPwnedException("Error checking for pwned password", e);
    }
  }
}

