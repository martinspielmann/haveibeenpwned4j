package de.martinspielmann.haveibeenpwned4j;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ProxySelector;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.Builder;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandler;
import java.net.http.HttpResponse.BodyHandlers;
import java.net.http.HttpResponse.BodySubscribers;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.logging.Logger;
import javax.script.ScriptEngine;
import de.martinspielmann.haveibeenpwned4j.internal.mapper.DefaultBreachListMapper;
import de.martinspielmann.haveibeenpwned4j.internal.mapper.DefaultBreachMapper;
import de.martinspielmann.haveibeenpwned4j.internal.mapper.DefaultDataClassesListMapper;
import de.martinspielmann.haveibeenpwned4j.internal.mapper.DefaultPasteListMapper;
import de.martinspielmann.haveibeenpwned4j.internal.mapper.DefaultPwnedPasswordsMapper;
import de.martinspielmann.haveibeenpwned4j.mapper.BreachListMapper;
import de.martinspielmann.haveibeenpwned4j.mapper.BreachMapper;
import de.martinspielmann.haveibeenpwned4j.mapper.DataClassesListMapper;
import de.martinspielmann.haveibeenpwned4j.mapper.Mapper;
import de.martinspielmann.haveibeenpwned4j.mapper.PasteListMapper;
import de.martinspielmann.haveibeenpwned4j.mapper.PwnedPasswordsMapper;
import de.martinspielmann.haveibeenpwned4j.model.Breach;
import de.martinspielmann.haveibeenpwned4j.model.Paste;

/**
 * This API client implements all APIs provided by Troy Hunt's ';-- Have I Been Pwned: API v3.
 * 
 * @see <a href="https://haveibeenpwned.com/API/v3">https://haveibeenpwned.com/API/v3</a>
 * @author Martin Spielmann
 */
public class HaveIBeenPwnedApiClient {

  /** The Logger used throughout this class */
  private static final Logger LOG = Logger.getLogger(HaveIBeenPwnedApiClient.class.getName());

  /** The URL to version 3 of the Have I Been Pwned API */
  private static final String HIBP_API_URL_V3 = "https://haveibeenpwned.com/api/v3/";

  /** The API key. */
  private final String hibpApiKey;

  /** The user agent. */
  private final String userAgent;

  /** The http client. */
  private final HttpClient httpClient;

  /** The breach mapper. */
  private BreachMapper breachMapper;

  /** The breach list mapper. */
  private BreachListMapper breachListMapper;

  /** The pwned passwords mapper. */
  private PwnedPasswordsMapper pwnedPasswordsMapper;

  /** The data classes list mapper. */
  private DataClassesListMapper dataClassesListMapper;

  /** The paste list mapper. */
  private PasteListMapper pasteListMapper;


  /**
   * Instantiates a new API client without an API key.
   * 
   * <strong>Watch out:</strong> Authorization is required for all APIs that enable searching HIBP
   * by email address, namely {@link #getBreachesForAccount(String)},
   * {@link #getBreachesForAccount(String, String, boolean, boolean)} and
   * {@link #getPastesForAccount(String)}. An API key is required to make an authorized call and can
   * be obtained on the API key page.
   * 
   * @see <a href=
   *      "https://haveibeenpwned.com/API/v3#Authorisation">https://haveibeenpwned.com/API/v3#Authorisation</a>
   * @see <a href="https://haveibeenpwned.com/API/Key">https://haveibeenpwned.com/API/Key</a>
   */
  public HaveIBeenPwnedApiClient() {
    this(null, null, null);
  }

  /**
   * Instantiates a new API client.
   * 
   * <strong>Watch out:</strong> Authorization is required for all APIs that enable searching HIBP
   * by email address, namely {@link #getBreachesForAccount(String)},
   * {@link #getBreachesForAccount(String, String, boolean, boolean)} and
   * {@link #getPastesForAccount(String)}. An API key is required to make an authorized call and can
   * be obtained on the API key page.
   * 
   * @param hibpApiKey the API key
   * 
   * @see <a href=
   *      "https://haveibeenpwned.com/API/v3#Authorisation">https://haveibeenpwned.com/API/v3#Authorisation</a>
   * @see <a href="https://haveibeenpwned.com/API/Key">https://haveibeenpwned.com/API/Key</a>
   */
  public HaveIBeenPwnedApiClient(String hibpApiKey) {
    this(hibpApiKey, null, null);
  }

  /**
   * Instantiates a new API client which uses a proxy server to reach the {@link #HIBP_API_URL_V3}.
   * 
   * <strong>Watch out:</strong> Authorization is required for all APIs that enable searching HIBP
   * by email address, namely {@link #getBreachesForAccount(String)},
   * {@link #getBreachesForAccount(String, String, boolean, boolean)} and
   * {@link #getPastesForAccount(String)}. An API key is required to make an authorized call and can
   * be obtained on the API key page.
   * 
   * @param hibpApiKey the API key
   * @param proxy the proxy
   * 
   * @see <a href=
   *      "https://haveibeenpwned.com/API/v3#Authorisation">https://haveibeenpwned.com/API/v3#Authorisation</a>
   * @see <a href="https://haveibeenpwned.com/API/Key">https://haveibeenpwned.com/API/Key</a>
   */
  public HaveIBeenPwnedApiClient(String hibpApiKey, InetSocketAddress proxy) {
    this(hibpApiKey, proxy, null);
  }

  /**
   * Instantiates a new API client which uses a proxy server to reach the {@link #HIBP_API_URL_V3}
   * as well as a custom user agent.
   * 
   * <strong>Watch out:</strong> Authorization is required for all APIs that enable searching HIBP
   * by email address, namely {@link #getBreachesForAccount(String)},
   * {@link #getBreachesForAccount(String, String, boolean, boolean)} and
   * {@link #getPastesForAccount(String)}. An API key is required to make an authorized call and can
   * be obtained on the API key page.
   * 
   * @param hibpApiKey the API key
   * @param proxy the proxy
   * @param userAgent the user agent
   * 
   * @see <a href=
   *      "https://haveibeenpwned.com/API/v3#Authorisation">https://haveibeenpwned.com/API/v3#Authorisation</a>
   * @see <a href="https://haveibeenpwned.com/API/Key">https://haveibeenpwned.com/API/Key</a>
   */
  public HaveIBeenPwnedApiClient(String hibpApiKey, InetSocketAddress proxy, String userAgent) {
    super();
    if (hibpApiKey != null) {
      this.hibpApiKey = hibpApiKey;
    } else {
      this.hibpApiKey = null;
      LOG.warning(
          "HaveIBeenPwnedApiClient was initialized without API key. Only APIs that work without an API key will be supported.");
    }
    java.net.http.HttpClient.Builder builder = HttpClient.newBuilder();
    // set proxy if configured
    if (proxy != null) {
      builder.proxy(ProxySelector.of(proxy));
    }
    this.httpClient = builder.build();
    if (userAgent != null) {
      this.userAgent = userAgent;
    } else {
      // set default user agent
      this.userAgent = HaveIBeenPwnedApiClient.class.getName();
    }
  }

  /*
   * Getters and setters
   */

  /**
   * Gets the http client.
   *
   * @return the http client
   */
  public HttpClient getHttpClient() {
    return httpClient;
  }

  /**
   * Gets the breach mapper that maps the JSON string provided by the API to a {@link Breach}.
   *
   * By default the {@link DefaultBreachMapper} based on {@link ScriptEngine} is used. If you
   * already have another JSON mapping library available within your project, you can provide your
   * own mapper implementation using {@link #setBreachMapper(BreachMapper)}.
   *
   * @return the breach mapper
   */
  public BreachMapper getBreachMapper() {
    if (this.breachMapper == null) {
      this.breachMapper = DefaultBreachMapper.get();
    }
    return this.breachMapper;
  }

  /**
   * Sets the breach mapper.
   *
   * @param breachMapper the new breach mapper
   */
  public void setBreachMapper(BreachMapper breachMapper) {
    this.breachMapper = breachMapper;
  }

  /**
   * Gets the breach list mapper that maps the JSON string provided by the API to a {@link List} of
   * type {@link Breach}.
   * 
   * By default the {@link DefaultBreachListMapper} based on {@link ScriptEngine} is used. If you
   * already have another JSON mapping library available within your project, you can provide your
   * own mapper implementation using {@link #setBreachListMapper(BreachListMapper)}.
   *
   * @return the breach list mapper
   */
  public BreachListMapper getBreachListMapper() {
    if (this.breachListMapper == null) {
      this.breachListMapper = DefaultBreachListMapper.get();
    }
    return this.breachListMapper;
  }

  /**
   * Sets the breach list mapper.
   * 
   * @param breachListMapper the new breach list mapper
   */
  public void setBreachListMapper(BreachListMapper breachListMapper) {
    this.breachListMapper = breachListMapper;
  }

  /**
   * Gets the data classes list mapper that maps the JSON string provided by the API to a
   * {@link List} of type {@link String}.
   *
   * By default the {@link DefaultDataClassesListMapper} based on {@link ScriptEngine} is used. If
   * you already have another JSON mapping library available within your project, you can provide
   * your own mapper implementation using {@link #setDataClassesListMapper(DataClassesListMapper)}.
   * 
   * @return the data classes list mapper
   */
  public DataClassesListMapper getDataClassesListMapper() {
    if (this.dataClassesListMapper == null) {
      this.dataClassesListMapper = DefaultDataClassesListMapper.get();
    }
    return dataClassesListMapper;
  }

  /**
   * Sets the data classes list mapper.
   *
   * @param dataClassesListMapper the new data classes list mapper
   */
  public void setDataClassesListMapper(DataClassesListMapper dataClassesListMapper) {
    this.dataClassesListMapper = dataClassesListMapper;
  }

  /**
   * Gets the pwned passwords mapper that maps the string provided by the API to a {@link Boolean}.
   * 
   * @return the pwned passwords mapper
   */
  public PwnedPasswordsMapper getPwnedPasswordsMapper() {
    if (this.pwnedPasswordsMapper == null) {
      this.pwnedPasswordsMapper = DefaultPwnedPasswordsMapper.get();
    }
    return this.pwnedPasswordsMapper;
  }

  /**
   * Sets the pwned passwords mapper.
   *
   * @param pwnedPasswordsMapper the new pwned passwords mapper
   */
  public void setPwnedPasswordsMapper(PwnedPasswordsMapper pwnedPasswordsMapper) {
    this.pwnedPasswordsMapper = pwnedPasswordsMapper;
  }

  /**
   * Gets the paste list mapper mapper that maps the JSON string provided by the API to a
   * {@link List} of type {@link Paste}.
   * 
   * By default the {@link DefaultPasteListMapper} based on {@link ScriptEngine} is used. If you
   * already have another JSON mapping library available within your project, you can provide your
   * own mapper implementation using {@link #setPasteListMapper(PasteListMapper)}.
   *
   * @return the paste list mapper
   */
  public PasteListMapper getPasteListMapper() {
    if (this.pasteListMapper == null) {
      this.pasteListMapper = DefaultPasteListMapper.get();
    }
    return pasteListMapper;
  }

  /**
   * Sets the paste list mapper.
   *
   * @param pasteListMapper the new paste list mapper
   */
  public void setPasteListMapper(PasteListMapper pasteListMapper) {
    this.pasteListMapper = pasteListMapper;
  }

  /*
   * Helper methods below
   */


  /**
   * Builds the request.
   *
   * @param url the url
   * @return the http request
   */
  protected HttpRequest buildRequest(String url) {
    Builder builder = HttpRequest.newBuilder().uri(URI.create(url)).header("user-agent", userAgent);
    // set api key if configured
    if (hibpApiKey != null) {
      builder.header("hibp-api-key", hibpApiKey);
    }
    return builder.build();
  }

  /**
   * Internal body handler.
   *
   * @param <T> the generic type
   * @param mapper the mapper
   * @return the body handler
   */
  private <T> BodyHandler<T> internalBodyHandler(Mapper<T> mapper) {
    return responseInfo -> {
      Status s = Status.of(responseInfo.statusCode());
      if (s.isError()) {
        throw new HaveIBeenPwnedException("Error handling body", s);
      } else {
        return BodySubscribers.mapping(BodySubscribers.ofString(StandardCharsets.UTF_8),
            mapper::map);
      }
    };
  }

  /**
   * Internal api request.
   *
   * @param <T> the generic type
   * @param url the URL
   * @param bodyHandler the body handler
   * @param errorMessage the error message
   * @return the request's response
   */
  private <T> T internalApiRequest(String url, BodyHandler<T> bodyHandler, String errorMessage) {
    try {
      return getHttpClient().send(buildRequest(url), bodyHandler).body();
    } catch (IOException e) {
      throw new HaveIBeenPwnedException(errorMessage, e);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
      throw new HaveIBeenPwnedException(errorMessage, e);
    }
  }


  /*
   * API methods below
   */

  /**
   * Gets the breaches for account.
   *
   * @param account the account
   * @return the breaches for account
   */
  public List<Breach> getBreachesForAccount(String account) {
    return getBreachesForAccount(account, null, false, true);
  }

  /**
   * Gets the breaches for account.
   *
   * @param account the account
   * @param domain the domain
   * @param truncateResponse the truncate response
   * @param includeUnverified the include unverified
   * @return the breaches for account
   */
  public List<Breach> getBreachesForAccount(String account, String domain, boolean truncateResponse,
      boolean includeUnverified) {
    String url = HIBP_API_URL_V3 + "breachedaccount/" + StringHelper.urlEncode(account)
        + "?truncateResponse=" + truncateResponse + "&includeUnverified=" + includeUnverified;
    if (domain != null) {
      url += "&domain=" + domain;
    }
    return internalApiRequest(url, internalBodyHandler(getBreachListMapper()),
        "Error checking for breaches");
  }

  /**
   * Gets the breaches.
   *
   * @return the breaches
   */
  public List<Breach> getBreaches() {
    return getBreaches(null);
  }


  /**
   * Gets the breaches.
   *
   * @param domain the domain
   * @return the breaches
   */
  public List<Breach> getBreaches(String domain) {
    String url = HIBP_API_URL_V3 + "breaches";
    if (domain != null) {
      url += "?domain=" + domain;
    }
    return internalApiRequest(url, internalBodyHandler(getBreachListMapper()),
        "Error checking for breaches");
  }

  /**
   * Gets a single breach based on the provided name.
   *
   * @param name the name of the breach
   * @return the breach
   * @throws HaveIBeenPwnedException if something unexpected happens during the request
   * @see <a href=
   *      "https://haveibeenpwned.com/API/v3#SingleBreach">https://haveibeenpwned.com/API/v3#SingleBreach</a>
   */
  public Breach getSingleBreach(String name) {
    return internalApiRequest(HIBP_API_URL_V3 + "breach/" + name,
        internalBodyHandler(getBreachMapper()), "Error checking for breach with name " + name);
  }

  /**
   * Gets the data classes.
   *
   * @return the data classes
   */
  public List<String> getDataClasses() {
    return internalApiRequest(HIBP_API_URL_V3 + "dataclasses",
        internalBodyHandler(getDataClassesListMapper()), "Error checking for data classes");
  }

  /**
   * Gets the pastes for account.
   *
   * @param account the account
   * @return the pastes for account
   */
  public List<Paste> getPastesForAccount(String account) {
    String url = HIBP_API_URL_V3 + "pasteaccount/" + StringHelper.urlEncode(account);
    return internalApiRequest(url, internalBodyHandler(getPasteListMapper()),
        "Error checking for pastes");
  }

  /**
   * Checks if the provided password pwned.
   * 
   * The provided password will not be sent over the network. Instead a
   *
   * @param password the password to be checked
   * @return true, if the password is pwned
   */
  public boolean isPasswordPwned(String password) {
    String url = "https://api.pwnedpasswords.com/range/" + StringHelper.getHashPrefix(password);
    try {
      HttpResponse<String> res = getHttpClient().send(buildRequest(url), BodyHandlers.ofString());
      return getPwnedPasswordsMapper().map(res.statusCode(), res.body(), password);
    } catch (IOException e) {
      throw new HaveIBeenPwnedException("Error checking for pwned password", e);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
      throw new HaveIBeenPwnedException("Error checking for pwned password", e);
    }
  }
}
