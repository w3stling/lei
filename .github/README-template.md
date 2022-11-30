Legal Entity Identifier (LEI)
=============================

[![Build](https://github.com/w3stling/lei/actions/workflows/build.yml/badge.svg)](https://github.com/w3stling/lei/actions/workflows/build.yml)
[![Download](https://img.shields.io/badge/download-%%version%%-brightgreen.svg)](https://search.maven.org/artifact/com.apptasticsoftware/lei/%%version%%/jar)
[![Javadoc](https://img.shields.io/badge/javadoc-%%version%%-blue.svg)](https://w3stling.github.io/lei/javadoc/%%version%%)
[![License](http://img.shields.io/:license-MIT-blue.svg?style=flat-round)](http://apptastic-software.mit-license.org)   
[![CodeQL](https://github.com/w3stling/lei/actions/workflows/codeql-analysis.yml/badge.svg)](https://github.com/w3stling/lei/actions/workflows/codeql-analysis.yml)
[![Quality Gate](https://sonarcloud.io/api/project_badges/measure?project=w3stling_lei&metric=alert_status)](https://sonarcloud.io/summary/new_code?id=w3stling_lei)
[![Coverage](https://sonarcloud.io/api/project_badges/measure?project=w3stling_lei&metric=coverage)](https://sonarcloud.io/summary/new_code?id=w3stling_lei)
[![Bugs](https://sonarcloud.io/api/project_badges/measure?project=w3stling_lei&metric=bugs)](https://sonarcloud.io/summary/new_code?id=w3stling_lei)
[![Vulnerabilities](https://sonarcloud.io/api/project_badges/measure?project=w3stling_lei&metric=vulnerabilities)](https://sonarcloud.io/summary/new_code?id=w3stling_lei)
[![Code Smells](https://sonarcloud.io/api/project_badges/measure?project=w3stling_lei&metric=code_smells)](https://sonarcloud.io/summary/new_code?id=w3stling_lei)

> **Note** - from version 2.0.0:
> * New Java package name
> * New group ID in Maven / Gradle dependency declaration
> * Moved repository from `JCenter` to `Maven Central Repository`

The Legal Entity Identifier (LEI) is unique global identifier of legal entities participating in financial transactions.
These can be individuals, companies or government entities that participate in financial transaction.
The identifier is used in reporting to financial regulators and all financial companies and funds are required to have an LEI.

The identifier is formatted as a 20-character, alpha-numeric code based on the ISO 17442 standard developed by the International Organization for Standardization (ISO).
It connects to key reference information that enables clear and unique identification of legal entities participating in financial transactions.
Each LEI contains information about an entity’s ownership structure and thus answers the questions of 'who is who’ and ‘who owns whom’.
Simply put, the publicly available LEI data pool can be regarded as a global directory of participants in the financial market.

This Java library makes it easy to lookup LEI information.

Examples
--------
Search LEI by legal name
```java
LeiLookup lookup = LeiLookup.getInstance();
List<Lei> lei = lookup.getLeiByLegalName("Apple");
```

Search LEI by LEI code
```java
LeiLookup lookup = LeiLookup.getInstance();
Optional<Lei> lei = lookup.getLeiByLeiCode("W22LROWP2IHZNBB6K528");
```

Search LEI by ISIN code
```java
LeiLookup lookup = LeiLookup.getInstance();
Optional<Lei> lei = lookup.getLeiByIsinCode("US0378331005");
```

Search LEI by BIC code
```java
LeiLookup lookup = LeiLookup.getInstance();
Optional<Lei> lei = lookup.getLeiByBicCode("BUKBGB22XXX");
```

Check if LEI code format valid
```java
boolean valid = LeiCodeValidator.isValid("W22LROWP2IHZNBB6K528");
```

Check if ISIN code format valid
```java
boolean valid = IsinCodeValidator.isValid("US0378331005");
```

Check if BIC code format valid
```java
boolean valid = BicCodeValidator.isValid("DEUTDEFFXXX");
```


Download
--------

Download [the latest JAR][1] or grab via [Maven][2] or [Gradle][3].

### Maven setup
Add dependency declaration:
```xml
<project>
    ...
    <dependencies>
        <dependency>
            <groupId>com.apptasticsoftware</groupId>
            <artifactId>lei</artifactId>
            <version>%%version%%</version>
        </dependency>
    </dependencies>
    ...
</project>
```

### Gradle setup
Add dependency declaration:
```groovy
dependencies {
    implementation 'com.apptasticsoftware:lei:%%version%%'
}
```

Lei library requires at minimum Java 11.

License
-------

    MIT License
    
    Copyright (c) %%year%%, Apptastic Software
    
    Permission is hereby granted, free of charge, to any person obtaining a copy
    of this software and associated documentation files (the "Software"), to deal
    in the Software without restriction, including without limitation the rights
    to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
    copies of the Software, and to permit persons to whom the Software is
    furnished to do so, subject to the following conditions:
    
    The above copyright notice and this permission notice shall be included in all
    copies or substantial portions of the Software.
    
    THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
    IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
    FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
    AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
    LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
    OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
    SOFTWARE.


[1]: https://search.maven.org/artifact/com.apptasticsoftware/lei/%%version%%/jar
[2]: https://maven.apache.org
[3]: https://gradle.org