*I. Prerequisites:* (If you have already done set up JDK and Maven - skip this step)
- JDK(Mandatory) -  http://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html
    - JDK Installation:(Refer following Video and Article
    	- https://www.youtube.com/watch?v=r0jN33ZOmWM 
		- https://www3.ntu.edu.sg/home/ehchua/programming/howto/JDK_Howto.html (till step 4)
     - JAVA_HOME set up(Mandatory) :
     	- http://roufid.com/no-compiler-is-provided-in-this-environment/
- Apache Maven(Mandatory)  - https://maven.apache.org
     - Apache Maven: (Refer following Video and Article)
     	- https://www.youtube.com/watch?v=3ODSQ0EpoQI
		- https://www.mkyong.com/maven/how-to-install-maven-in-windows/
- GIT (optional)
     - GIT Setup(Optional): (Refer following Video and Article)
     	- https://www.youtube.com/watch?v=albr1o7Z1nw
		- https://www.codecademy.com/articles/git-setup


*II. Initial Set up your project for the Swagger Specification* :

- Why/How to store the mock data in the centralized place:
	It always provides a choice to choose your data base and configure(spring-jpa). Need to provide data base information in the Application.properties in the "src/main/resources" directory of your spring boot application: This would requires if you don't want to loose the existing mock data because as you redeploy the code the data would be lost.

- In memory DB: (Eveny restart you lose the old data)
	
	- virtualan.datasource.driver-class-name=org.hsqldb.jdbcDriver
	- virtualan.datasource.jdbcurl=jdbc:hsqldb:mem:dataSource
	- virtualan.datasource.username=sa
	- virtualan.datasource.password=
     
- Add the following entry for each Interface Spec/Yaml  to be set up as mock service:
	- to setup  "petstore.yaml" need to add following entry in the pom.xml. refer the pom.xml for reference
	
	```html
	<execution>
		<id>pet-service-vs-api</id>
		<goals>
			<goal>generate</goal>
		</goals>
		<configuration>
			<inputSpec>${project.basedir}/src/main/resources/external/petstore.yaml</inputSpec>
			<output>${project.basedir}/target/external/</output>
			<apiPackage>org.openapitools.virtualan.api</apiPackage>
			<modelPackage>org.openapitools.virtualan.to</modelPackage>
			<generatorName>spring</generatorName>
			<configOptions>
                                <virtualService>true</virtualService>
                        </configOptions>
		</configuration>
	</execution>
	```

*III. Navigate to root directory of the folder where pom.xml was present*:

- Build:

         - mvn clean install  
	 
	 - If you have any proxy issue use this command:  mvn -Dhttps.protocols=TLSv1,TLSv1.1,TLSv1.2 clean install 
                  
- Run using standalone JAR:
	
	- java -jar target/virtualan-pet.jar         

- To set up mock data:
      
      - Using Virtualan-UI:       
      	https://github.com/elan-venture/virtualan/wiki/Test-Data-Set-up-using-Virtualan
      
      - Using Virtualan-Rest service:
      	https://github.com/elan-venture/virtualan/blob/master/test/virtualan-test-data/src/main/resources/features/pet/pet.feature
	  	https://github.com/elan-venture/virtualan/tree/master/samples/virtualan-openapi-spring-mapping/src/test/java/io/virtualan/test

- Invoke Virtualan UI:  			
	- Navigate to http://localhost:8080/virtualan-ui.html 
	- More details about the user interface refer: https://github.com/elan-venture/virtualan/wiki 
