Legal Entity Identifier (LEI)
=============================

[![Build Status](https://travis-ci.org/w3stling/lei.svg?branch=master)](https://travis-ci.org/w3stling/lei)
[![Download](https://api.bintray.com/packages/apptastic/maven-repo/lei/images/download.svg)](https://bintray.com/apptastic/maven-repo/lei/_latestVersion)
[![Javadoc](https://img.shields.io/badge/javadoc-1.0.4-blue.svg)](https://w3stling.github.io/lei/javadoc/1.0.4)
[![License](http://img.shields.io/:license-MIT-blue.svg?style=flat-round)](http://apptastic-software.mit-license.org)   
[![Quality Gate](https://sonarcloud.io/api/project_badges/measure?project=com.apptastic%3Alei&metric=alert_status)](https://sonarcloud.io/dashboard?id=com.apptastic%3Alei)
[![Coverage](https://sonarcloud.io/api/project_badges/measure?project=com.apptastic%3Alei&metric=coverage)](https://sonarcloud.io/component_measures?id=com.apptastic%3Alei&metric=Coverage)
[![Bugs](https://sonarcloud.io/api/project_badges/measure?project=com.apptastic%3Alei&metric=bugs)](https://sonarcloud.io/component_measures?id=com.apptastic%3Alei&metric=bugs)
[![Vulnerabilities](https://sonarcloud.io/api/project_badges/measure?project=com.apptastic%3Alei&metric=vulnerabilities)](https://sonarcloud.io/component_measures?id=com.apptastic%3Alei&metric=vulnerabilities)
[![Code Smells](https://sonarcloud.io/api/project_badges/measure?project=com.apptastic%3Alei&metric=code_smells)](https://sonarcloud.io/component_measures?id=com.apptastic%3Alei&metric=code_smells)

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
Get LEI
```java
LeiLookup lookup = LeiLookup.getInstance();
Optional<Lei> lei = lookup.getLei("W22LROWP2IHZNBB6K528");
```

Get a list of LEIs
```java
LeiLookup lookup = LeiLookup.getInstance();
List<Lei> lei = lookup.getLei("W22LROWP2IHZNBB6K528", "4PQUHN3JPFGFNF3BB653")
                      .collect(Collectors.toList());
```


Download
--------

Download [the latest JAR][1] or grab via [Maven][2] or [Gradle][3].

### Maven setup
Add JCenter repository for resolving artifact:
```xml
<project>
    ...
    <repositories>
        <repository>
            <id>jcenter</id>
            <url>https://jcenter.bintray.com</url>
        </repository>
    </repositories>
    ...
</project>
```

Add dependency declaration:
```xml
<project>
    ...
    <dependencies>
        <dependency>
            <groupId>com.apptastic</groupId>
            <artifactId>lei</artifactId>
            <version>1.0.4</version>
        </dependency>
    </dependencies>
    ...
</project>
```

### Gradle setup
Add JCenter repository for resolving artifact:
```groovy
repositories {
    jcenter()
}
```

Add dependency declaration:
```groovy
dependencies {
    implementation 'com.apptastic:lei:1.0.4'
}
```

Lei library requires at minimum Java 11.

License
-------

    MIT License
    
    Copyright (c) 2019, Apptastic Software
    
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


[1]: https://bintray.com/apptastic/maven-repo/lei/_latestVersion
[2]: https://maven.apache.org
[3]: https://gradle.org