# haveibeenpwned4j

The ultimate Java library for [Troy Hunt's](https://www.troyhunt.com/) `';-- Have I Been Pwned: API v3`.

[![Java CI](https://github.com/martinspielmann/haveibeenpwned4j/actions/workflows/maven.yml/badge.svg)](https://github.com/martinspielmann/haveibeenpwned4j/actions/workflows/maven.yml)
[![Maven Central](https://img.shields.io/maven-central/v/de.martinspielmann.haveibeenpwned4j/haveibeenpwned4j.svg)](https://central.sonatype.com/artifact/de.martinspielmann.haveibeenpwned4j/haveibeenpwned4j)
![Java 25](https://img.shields.io/badge/Java-25%2B-blue.svg)
![Contributors](https://img.shields.io/github/contributors/martinspielmann/haveibeenpwned4j.svg)
[![Apache-2.0](https://img.shields.io/github/license/martinspielmann/haveibeenpwned4j.svg)](https://raw.githubusercontent.com/martinspielmann/haveibeenpwned4j/master/LICENSE)


## About The Project

[Troy Hunt's](https://www.troyhunt.com/) `';-- Have I Been Pwned` is an awesome project that lets you check if an email address, domain, or password has been compromised in a data breach.

As you can see on the [Consumers](https://haveibeenpwned.com/API/Consumers) page of [https://haveibeenpwned.com](https://haveibeenpwned.com), there are other Java clients available for the API, but many are outdated, incomplete, or carry heavy dependency trees.

Here comes **haveibeenpwned4j** awesomeness:

* Complete coverage of `';-- Have I Been Pwned: API v3` ✔
  * **Pwned Passwords**: Check if a password has been breached or get its breach count via k-anonymity (SHA-1 and NTLM, with response padding enabled) ✔
  * **Email Address Search**: Get all breaches for an account (direct or privacy-preserving 6-char SHA-1 k-anonymity range search) ✔
  * **Breaches & Data Classes**: Get all breaches (with domain / spam-list filters), single breach by name, latest breach, data classes, and data class descriptions ✔
  * **Domain Search & Verification**: Get breached aliases for a domain, list subscribed domains, and verify domains via DNS or email ✔
  * **Stealer Logs**: Search stealer logs by email address, website domain, or email domain ✔
  * **Pastes**: Get all pastes for an account ✔
  * **Subscription**: Query current HIBP subscription status and rate limits ✔
* Built on modern **Java 25 LTS** with immutable Java `record` models and `java.net.http.HttpClient` ✔
* Apache License Version 2.0 ✔
* Available from Maven Central ✔


## Getting Started

### Prerequisites

Java **25** (latest LTS) is the minimum required version.

* macOS (Homebrew)
```sh
brew install openjdk@25
```
* Ubuntu / Debian
```sh
sudo apt install openjdk-25-jdk
```
* Fedora
```sh
sudo dnf install java-25-openjdk
```
* Cross-platform: [Eclipse Temurin 25](https://adoptium.net/)


## Usage

1. Include the dependency in your `pom.xml`:

```xml
<dependency>
  <groupId>de.martinspielmann.haveibeenpwned4j</groupId>
  <artifactId>haveibeenpwned4j</artifactId>
  <version>3.0.0</version>
</dependency>
```

2. Create a `HaveIBeenPwnedApiClient` (using the fluent builder or constructor) and start querying:

```java
// Build a client with your HIBP API key (or HaveIBeenPwnedApiClient.TEST_API_KEY for test accounts)
HaveIBeenPwnedApiClient client = HaveIBeenPwnedApiClient.builder()
    .apiKey("my-super-secret-api-key")
    .userAgent("my-awesome-app")
    .build();

// Check if a password has been breached (no API key required, k-anonymity + padding)
boolean isPasswordPwned = client.isPasswordPwned("password123");
long pwnCount = client.getPasswordPwnCount("password123");
long ntlmCount = client.getPasswordPwnCount("password123", HashMode.NTLM);

// Get breaches for an account (requires API key)
List<Breach> breachesByAccount = client.getBreachesForAccount("foo.bar@example.com");

// Privacy-preserving k-anonymity email breach lookup (requires Pro / High RPM API key)
List<String> breachNames = client.getBreachNamesForAccountByRange("foo.bar@example.com");

// Get all breaches, single breach, or the most recently added breach
List<Breach> allBreaches = client.getBreaches();
Breach adobeBreach = client.getSingleBreach("Adobe");
Breach latestBreach = client.getLatestBreach();

// Get data classes and descriptions
List<String> dataClasses = client.getDataClasses();
List<DataClassDescription> descriptions = client.getDataClassDescriptions();

// Get pastes for an account (requires API key)
List<Paste> pastes = client.getPastesForAccount("foo.bar@example.com");

// Domain search & stealer logs (requires API key and verified domain)
Map<String, List<String>> domainBreaches = client.getBreachedDomain("example.com");
List<SubscribedDomain> subscribedDomains = client.getSubscribedDomains();
List<String> stealerDomains = client.getStealerLogsByEmail("jane@example.com");

// Check subscription status
SubscriptionStatus status = client.getSubscriptionStatus();
```

## Contributing

If you have any problem or idea, don't hesitate to
<a href="https://github.com/martinspielmann/haveibeenpwned4j/issues">report a bug</a>
or
<a href="https://github.com/martinspielmann/haveibeenpwned4j/issues">request a feature</a>.

1. Fork the Project
2. Create your Feature Branch (`git checkout -b feature/AmazingFeature`)
3. Commit your Changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the Branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

Any contributions you make are **highly appreciated**.

### Develop

```sh
git clone https://github.com/martinspielmann/haveibeenpwned4j.git
cd haveibeenpwned4j
mvn verify
```


## Versioning

This project uses [Semantic Versioning](https://semver.org/).


## License

Distributed under the Apache License Version 2.0. See `LICENSE` for more information.


## Contact

Martin Spielmann - [mail@martinspielmann.de](mailto:mail@martinspielmann.de)

Project Link: [https://github.com/martinspielmann/haveibeenpwned4j](https://github.com/martinspielmann/haveibeenpwned4j)


## Acknowledgements
* [';-- Have I Been Pwned](https://haveibeenpwned.com/)

