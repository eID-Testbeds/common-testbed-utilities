# Common Testbed Utilities
This repository contains utility classes that are frequently used as part of the eID Testbeds.

## Requirements

### Building

+ JDK 17.0.12+ (July 16, 2024)
+ Maven 3.6.3+ (November 25, 2019)

## Build and Deployment

The Common Testbed Utilities can be build by executing `mvn clean install`.

A complete guideline for an eID-Testbed deployment is given in the handbook (`server/install/handbook.md`, available in German only).

## Dependencies

| Description              | Library                                        | Version | Date         |
| :----------------------- | :--------------------------------------------- | :------ | :----------- |
| Testing                  | org.testng                                     | 7.6.0   | May 18, 2022 |
| Logging                  | org.apache.logging.log4j.log4j-core            | 2.17.2  | Feb 27, 2022 |
| Key and Cert. Generation | org.bouncycastle.bcpkix-jdk18on                | 1.78.1  | Apr 18, 2024 |
| Key and Cert. Generation | org.bouncycastle.bcprov-jdk18on                | 1.78.1  | Apr 18, 2024 |
| Key and Cert. Generation | org.bouncycastle.bctls-jdk18on                 | 1.78.1  | Apr 18, 2024 |
| Key and Cert. Generation | org.bouncycastle.bcutil-jdk18on                | 1.78.1  | Apr 18, 2024 |
| JakartaEE Platform       | jakarta.platform.jakarta.jakartaee-api         | 8.0.0   | Sep 10, 2019 |
| XML Mapper               | jakarta.platform.jakarta.jakarta.xml.bind-api  | 2.3.2   | Jan 08, 2019 |
| XML Web Services         | jakarta.platform.jakarta.jakarta.xml.ws-api    | 2.3.2   | Jan 09, 2019 |
| Web Services             | jakarta.platform.jakarta.jakarta.jws-api       | 1.1.1   | Jan 08, 2019 |
|                          | **BouncyCertGenerator**                        |         |              |
| Surefire Plugin          | org.apache.maven.plugins.maven-surefire-plugin | 2.18.1  | Dec 22, 2014 |
|                          | **CommonUtilities**                            |         |              |
| Commons Codec            | commons-codec.commons-codec                    | 1.15    | Sep 01, 2020 |
