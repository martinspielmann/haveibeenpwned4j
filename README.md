# haveibeenpwned4j

The ultimate Java library for [Troy Hunt's](https://www.troyhunt.com/) `';-- Have I Been Pwned: API v3`.

[![Build Status](https://github.com/pingunaut/haveibeenpwned4j/workflows/Java%20CI/badge.svg)](https://github.com/pingunaut/haveibeenpwned4j/actions)
![Contributors](https://img.shields.io/github/contributors/pingunaut/haveibeenpwned4j.svg)
[![Apache-2.0](https://img.shields.io/github/license/pingunaut/haveibeenpwned4j.svg)](https://raw.githubusercontent.com/pingunaut/haveibeenpwned4j/master/LICENSE)
[![Vulnerabilities](https://sonarcloud.io/api/project_badges/measure?project=de.martinspielmann.haveibeenpwned4j%3Ahaveibeenpwned4j&metric=vulnerabilities)](https://sonarcloud.io/dashboard?id=de.martinspielmann.haveibeenpwned4j%3Ahaveibeenpwned4j)
[![Coverage](https://img.shields.io/sonar/https/sonarcloud.io/de.martinspielmann.haveibeenpwned4j%3Ahaveibeenpwned4j/coverage.svg)](https://sonarcloud.io/component_measures?id=de.martinspielmann.haveibeenpwned4j%3Ahaveibeenpwned4j&metric=coverage)


## About The Project

[Troy Hunt's](https://www.troyhunt.com/) `';-- Have I Been Pwned` is an awesome project that lets you check if you have an account that has been compromised in a data breach.

As you can see on the [Consumers](https://haveibeenpwned.com/API/Consumers) page 
of [https://haveibeenpwned.com](https://haveibeenpwned.com), there are already Java clients available for the API.
Unfortunately some do not fully implement the API or have weird dependencies.

Here comes **haveibeenpwned4j** awesomeness:

* All features of `';-- Have I Been Pwned: API v3` implemented (incl. support for providing your API key) ✔ 
  * Check if a password has been breached	✔
  * Check if an account has been breached	✔
  * Check if an account has been in a paste	✔
  * Get all breaches (or based on a domain)	✔
  * Get a single breach	✔
* Apache License Version 2.0 ✔
* Available from Maven Central ✔


## Getting Started

### Prerequisites

Install Java on your system. Java version 11 is the minimum required version

* Java: Ubuntu
```sh
sudo apt install default-jdk
```
* Java: Fedora
```sh
sudo dnf install java-11-openjdk
```
* Java: Windows
Install Java 11: E.g. [OpenJDK](https://adoptopenjdk.net/?variant=openjdk11&jvmVariant=hotspot) or [Oracle JDK](https://www.oracle.com/technetwork/java/javase/downloads/jdk11-downloads-5066655.html)


## Usage

1. Include the dependency in your pom.xml

```xml
<dependency>
  <groupId>de.martinspielmann.haveibeenpwned4j</groupId>
  <artifactId>haveibeenpwned4j</artifactId>
  <version>1.0.0</version>
</dependency>
```

2. Create a new `HaveIBeenPwnedApiClient` and start using it

```java
/**
 * <strong>Watch out:</strong> Authorization is required for all APIs that enable searching HIBP
 * by email address, namely getting breaches for an account, and getting pastes for and account. 
 * An API key is required to make an authorized call and can
 * be obtained on the API key page.
 * 
 * @see https://haveibeenpwned.com/API/v3#Authorisation
 * @see https://haveibeenpwned.com/API/Key
 */
 
HaveIBeenPwnedApiClient client = new HaveIBeenPwnedApiClient("my-super-secret-api-key");

// find out if given password has been breached
boolean isPasswordPwned = client.isPasswordPwned("password123");

// get breaches for an account (needs API key)
List<Breach> breachesByAccount = client.getBreachesForAccount("foo.bar@example.com");

// get all breaches
List<Breach> allBreaches = client.getBreaches();

// find breach by name
Breach singleBreach = client.getSingleBreach("Example Breach");

// get pastes for an account (needs API key)
List<Paste> pastes = client.getPastesForAccount("foo.bar@example.com");
```

## Contributing

If you have any problem or idea, dont hesitate to
<a href="https://github.com/pingunaut/haveibeenpwned4j/issues">report a bug</a>
or
<a href="https://github.com/pingunaut/haveibeenpwned4j/issues">request a feature</a>.

If you want to help out with some code, tests or documentation, just follow these steps:

1. Fork the Project
2. Create your Feature Branch (`git checkout -b feature/AmazingFeature`)
3. Commit your Changes (`git commit -m 'Add some AmazingFeature`)
4. Push to the Branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

Any contributions you make are **highly appreciated**.

### Prepare

Install Apache Maven on your developer system

* Java and Apache Maven: Ubuntu
```sh
sudo apt install maven
```
* Java and Apache Maven: Fedora
```sh
sudo dnf install maven
```
* Java and Apache Maven: Windows
  * Install Java 11: E.g. [OpenJDK](https://adoptopenjdk.net/?variant=openjdk11&jvmVariant=hotspot) or [Oracle JDK](https://www.oracle.com/technetwork/java/javase/downloads/jdk11-downloads-5066655.html)
  * Download [Apache Maven](http://maven.apache.org/download.cgi) and install like described [here](http://maven.apache.org/install.html)


### Develop

```sh
git clone https://github.com/pingunaut/haveibeenpwned4j.git
cd haveibeenpwned4j

# to make all the tests run, you need to provide your HIBP API key as an environment variable
HIPB_API_KEY=your-super-secret-key && mvn test
```


## Versioning

This project uses [Semantic Versioning](https://semver.org/)


## License

Distributed under the Apache License Version 2.0. See `LICENSE` for more information.


## Contact

Martin Spielmann - [@pingunaut](https://twitter.com/pingunaut)

Project Link: [https://github.com/pingunaut/haveibeenpwned4j](https://github.com/pingunaut/haveibeenpwned4j)


## Acknowledgements
* [';-- Have I Been Pwned](https://haveibeenpwned.com/)
