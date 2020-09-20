
<h1 align="center">Virtualan</h1>

<div align="center">

[![Maven Central](https://img.shields.io/maven-central/v/io.virtualan/virtualan-plugin.svg?label=Maven%20Central)](https://search.maven.org/search?q=g:%22io.virtualan%22%20AND%20a:%22virtualan-plugin%22)
[![Build Status](https://travis-ci.com/virtualansoftware/virtualan.svg?branch=master)](https://travis-ci.com/virtualansoftware/virtualan)

[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=virtualansoftware_virtualan&metric=alert_status)](https://sonarcloud.io/dashboard?id=virtualansoftware_virtualan)
</div>

# What is Virtualan : 
Virtualan would be built with spring boot  framework that would convert API service as Virtualized service in matter of adding couple of annotations. **_Simply as Virtualized service_** which currently supports spring-boot based Rest service(API) with **Spring-RestController** or **CXF-Jaxrs** as Virtualized service with **@VirtualService** and **@ApiVirtual** annotations.

**How it could be useful:**
In the Agile world, We need to develop (Micro)services & Test the services in parallel. How can tester or development team can develop or test parallel to all the APIs before the real Microservices would be developed? Here Virtualized service comes into the picture.

## **What would be the benefits for?**

### Development team :
* If Services needs to be connected to 1 or more microservices in the development/Testing Environment if the other dependent services would not be available at the time.
* If the dependent services contract had changed then the new changes(implementation) may not be available in day-1 of the sprint.
* If Need to create several use-case scenarios during the development phase even though the dependent service(s) would not be available or not developed yet..
* end etc..

###  Testing team(Shift left approach) :
* **Start developing automation scripts in day-1** of the sprint in parallel with service implementation* .
* Develop and test right away.
* Prepare all the test scenarios and test cases and **test/validate all the test scripts before the actual service** is ready.
* Tester can create all scenario and including error scenarios and create automated test cases before the service is ready.
* Once the real service is ready, just switch the endpoint to real service and validate all the test-cases in a minutes. 
* And etc..

**Overview:**
How developer to make and deploy the (newly developing) spring boot application interface with stub as Virtualized service. How this would helps team to proceed with "Shift Left"  Strategy. 

**What is Shift left(Wikipedia):**
Shift left testing is an approach to software testing and system testing in which testing is performed earlier in the lifecycle (i.e., moved left on the project timeline). It is the first half of the maxim "Test early and often."

# **How to make my Service As Virtualized :**
Developing  a sprint boot(supports from 2.0.1.RELEASE) REST services using Rest Controller or CXF(JAX-RX) can be easily deployed as Virtualized service with three simple steps .

### Step (1) : Add the "virtualan-plugin" dependency
	• Add "virtualan-plugin" dependency in the  pom.xml  
		<dependency>
			<groupId>io.virtualan</groupId>
			<artifactId>virtualan-plugin</artifactId>
			<version>${virtualan.version}</version>
		</dependency>
		

### Step (2) :  Add @VirtualService and @ApiVirtual annotations
	• @VirtualService - Annotation should be added in the class level
	• @ApiVirtual - Annotation should be added in the method level that the API would you like to Virtualize.
	

### Step (3) :  Service Data base setup:
	• Add the entries in the **application.properties** of the database to be used to store the test data (Example: hsql with in memory DB).
		virtualan.datasource.driver-class-name=<org.hsqldb.jdbcDriver>
		virtualan.datasource.jdbc-url=<jdbc:hsqldb:mem:dataSource>
		virtualan.datasource.username=<sa>
		virtualan.datasource.password=<>



## [License](#table-of-contents)
-------

Copyright 2020 Virtualan Contributors (https://virtualan.io)  

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at [apache.org/licenses/LICENSE-2.0](http://www.apache.org/licenses/LICENSE-2.0)

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.

---



