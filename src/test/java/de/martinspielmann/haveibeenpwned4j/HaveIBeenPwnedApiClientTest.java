package de.martinspielmann.haveibeenpwned4j;

import static org.junit.jupiter.api.Assertions.*;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.http.HttpHeaders;
import java.net.http.HttpRequest;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import de.martinspielmann.haveibeenpwned4j.internal.mapper.DefaultBreachListMapper;
import de.martinspielmann.haveibeenpwned4j.internal.mapper.DefaultBreachMapper;
import de.martinspielmann.haveibeenpwned4j.internal.mapper.DefaultDataClassesListMapper;
import de.martinspielmann.haveibeenpwned4j.internal.mapper.DefaultPasteListMapper;
import de.martinspielmann.haveibeenpwned4j.internal.mapper.DefaultPwnedPasswordsMapper;
import de.martinspielmann.haveibeenpwned4j.mapper.BreachListMapper;
import de.martinspielmann.haveibeenpwned4j.mapper.BreachMapper;
import de.martinspielmann.haveibeenpwned4j.mapper.DataClassesListMapper;
import de.martinspielmann.haveibeenpwned4j.mapper.PasteListMapper;
import de.martinspielmann.haveibeenpwned4j.mapper.PwnedPasswordsMapper;
import de.martinspielmann.haveibeenpwned4j.model.Breach;
import de.martinspielmann.haveibeenpwned4j.model.Paste;

class HaveIBeenPwnedApiClientTest {

  private static final String API_KEY = System.getenv("HIBP_API_KEY");

  private HaveIBeenPwnedApiClient clientWithApiKey;
  private HaveIBeenPwnedApiClient clientWithoutApiKey;

  @BeforeEach
  void init() {
    clientWithApiKey = new HaveIBeenPwnedApiClient(API_KEY);
    clientWithoutApiKey = new HaveIBeenPwnedApiClient();
    try {
      // bad idea to add a sleep here, but it's to avoid TOO_MANY_REQUESTS errors.
      Thread.sleep(2000);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }
  }


  @Test
  void testHaveIBeenPwnedApiClient() {
    assertNotNull(clientWithoutApiKey);
  }

  @Test
  void testGetBreachMapper() {
    assertEquals(DefaultBreachMapper.get(), clientWithoutApiKey.getBreachMapper());
  }

  @Test
  void testSetBreachMapper() {
    BreachMapper m = new BreachMapper() {
      @Override
      public Breach map(String json) {
        return null;
      }
    };
    clientWithoutApiKey.setBreachMapper(m);
    assertSame(m, clientWithoutApiKey.getBreachMapper());
  }

  @Test
  void testGetBreachListMapper() {
    assertEquals(DefaultBreachListMapper.get(), clientWithoutApiKey.getBreachListMapper());
  }

  @Test
  void testSetBreachListMapper() {
    BreachListMapper m = new BreachListMapper() {
      @Override
      public List<Breach> map(String json) {
        return null;
      }
    };
    clientWithoutApiKey.setBreachListMapper(m);
    assertSame(m, clientWithoutApiKey.getBreachListMapper());
  }

  @Test
  void testGetDataClassesListMapper() {
    assertEquals(DefaultDataClassesListMapper.get(),
        clientWithoutApiKey.getDataClassesListMapper());
  }

  @Test
  void testSetDataClassesListMapper() {
    DataClassesListMapper m = new DataClassesListMapper() {
      @Override
      public List<String> map(String json) {
        return null;
      }
    };
    clientWithoutApiKey.setDataClassesListMapper(m);
    assertSame(m, clientWithoutApiKey.getDataClassesListMapper());
  }

  @Test
  void testGetPwnedPasswordsMapper() {
    assertEquals(DefaultPwnedPasswordsMapper.get(), clientWithoutApiKey.getPwnedPasswordsMapper());
  }

  @Test
  void testSetPwnedPasswordsMapper() {
    PwnedPasswordsMapper m = new PwnedPasswordsMapper() {
      @Override
      public boolean map(int responseStatus, String responseBody, String password) {
        return false;
      }
    };
    clientWithoutApiKey.setPwnedPasswordsMapper(m);
    assertSame(m, clientWithoutApiKey.getPwnedPasswordsMapper());
  }

  @Test
  void testGetPasteListMapper() {
    assertEquals(DefaultPasteListMapper.get(), clientWithoutApiKey.getPasteListMapper());
  }

  @Test
  void testSetPasteListMapper() {
    PasteListMapper m = new PasteListMapper() {
      @Override
      public List<Paste> map(String json) {
        return null;
      }
    };
    clientWithoutApiKey.setPasteListMapper(m);
    assertSame(m, clientWithoutApiKey.getPasteListMapper());
  }

  @Test
  void testGetHttpClient() {
    assertTrue(clientWithoutApiKey.getHttpClient().proxy().isEmpty());
    InetSocketAddress proxy = InetSocketAddress.createUnresolved("127.0.0.1", 8080);
    HaveIBeenPwnedApiClient c = new HaveIBeenPwnedApiClient(null, proxy);
    assertFalse(c.getHttpClient().proxy().isEmpty());
  }

  @Test
  void testBuildRequest() {
    HttpRequest request = clientWithApiKey.buildRequest("http://example.com");
    assertEquals("GET", request.method());
    assertEquals(URI.create("http://example.com"), request.uri());
    HttpHeaders headers = request.headers();
    assertEquals(API_KEY, headers.firstValue("hibp-api-key").get());
    assertEquals(HaveIBeenPwnedApiClient.class.getName(), headers.firstValue("user-agent").get());
  }

  @Test
  void testGetBreachesForAccountString() {
    List<Breach> breachesForAccount = clientWithApiKey.getBreachesForAccount("foo.bar@example.com");
    assertTrue(breachesForAccount.size() > 0);
    breachesForAccount.forEach(b -> {
      assertNotNull(b.getAddedDate());
      assertNotNull(b.getBreachDate());
      assertFalse(b.getDataClasses().isEmpty());
      assertNotNull(b.getDescription());
      assertNotNull(b.getDomain());
      assertNotNull(b.getLogoPath());
      assertNotNull(b.getModifiedDate());
      assertNotNull(b.getName());
      assertNotNull(b.getPwnCount());
      assertNotNull(b.getTitle());
    });
  }

  @Test
  void testGetBreachesForAccountStringStringBooleanBoolean() {
    List<Breach> breachesForAccount =
        clientWithApiKey.getBreachesForAccount("foo.bar@example.com", null, true, false);
    assertTrue(breachesForAccount.size() > 0);
    // verify result is truncated. only name field is mapped
    breachesForAccount.forEach(b -> {
      assertNull(b.getAddedDate());
      assertNull(b.getBreachDate());
      assertTrue(b.getDataClasses() == null || b.getDataClasses().isEmpty());
      assertNull(b.getDescription());
      assertNull(b.getDomain());
      assertNull(b.getLogoPath());
      assertNull(b.getModifiedDate());
      assertNotNull(b.getName());
      assertNull(b.getPwnCount());
      assertNull(b.getTitle());
    });
  }

  @Test
  void testGetBreachesForAccountNonExisting() {
    List<Breach> breachesForAccount =
        clientWithApiKey.getBreachesForAccount(UUID.randomUUID().toString() + "@example.com");
    assertTrue(breachesForAccount.isEmpty());
  }

  @Test
  void testGetBreachesForAccountStringNoApiKey() {
    assertThrows(HaveIBeenPwnedException.class,
        () -> clientWithoutApiKey.getBreachesForAccount("foo.bar@example.com"));
  }

  @Test
  void testGetBreaches() {
    List<Breach> breachesForAccount = clientWithoutApiKey.getBreaches("adobe.com");
    assertTrue(breachesForAccount.size() >= 1);
    // verify result is truncated. only name field is mapped
    breachesForAccount.forEach(b -> {
      assertNotNull(b.getAddedDate());
      assertNotNull(b.getBreachDate());
      assertFalse(b.getDataClasses().isEmpty());
      assertNotNull(b.getDescription());
      assertNotNull(b.getDomain());
      assertNotNull(b.getLogoPath());
      assertNotNull(b.getModifiedDate());
      assertNotNull(b.getName());
      assertNotNull(b.getPwnCount());
      assertNotNull(b.getTitle());
    });
  }

  @Test
  void testGetSingleBreach() {
    Breach b = clientWithoutApiKey.getSingleBreach("Adobe");
    assertNotNull(b.getAddedDate());
    assertNotNull(b.getBreachDate());
    assertFalse(b.getDataClasses().isEmpty());
    assertNotNull(b.getDescription());
    assertNotNull(b.getDomain());
    assertNotNull(b.getLogoPath());
    assertNotNull(b.getModifiedDate());
    assertEquals("Adobe", b.getName());
    assertNotNull(b.getPwnCount());
    assertNotNull(b.getTitle());
  }

  @Test
  void testGetSingleBreachNonExisting() {
    Breach b = clientWithoutApiKey.getSingleBreach(UUID.randomUUID().toString());
    assertNull(b);
  }

  @Test
  void testGetDataClasses() {
    List<String> dataClasses = clientWithoutApiKey.getDataClasses();
    assertTrue(dataClasses.size() > 0);
  }

  @Test
  void testGetPastesForAccount() {
    List<Paste> pastes = clientWithApiKey.getPastesForAccount("dilipsinghrana4@gmail.com");
    assertTrue(pastes.size() > 0);
    pastes.forEach(b -> {
      assertNotNull(b.getDate());
      assertNotNull(b.getEmailCount());
      assertNotNull(b.getId());
      assertNotNull(b.getSource());
    });
  }

  @Test
  void testGetPastesForNonExisting() {
    List<Paste> pastes =
        clientWithApiKey.getPastesForAccount(UUID.randomUUID().toString() + "@gmail.com");
    assertTrue(pastes.isEmpty());
  }

  @Test
  void testGetPastesForAccountWithoutApiKey() {
    assertThrows(HaveIBeenPwnedException.class,
        () -> clientWithoutApiKey.getPastesForAccount("foo.bar@example.com"));
  }

  @Test
  void testIsPasswordPwnedWithCommonPassword() {
    assertTrue(clientWithoutApiKey.isPasswordPwned("password123"));
  }

  @Test
  void testIsPasswordPwnedWithRandomPassword() {
    assertFalse(clientWithoutApiKey.isPasswordPwned(UUID.randomUUID().toString()));
  }



}
