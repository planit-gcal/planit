# **PlanIt - Spring Boot - Deployment test**

## Application Properties file

To run the program, create an [`application.properties` file](src/main/resources/application.properties) and fill it with important variables. The repository. The current file contains the configuration to a local database. database information can be found in the file. 

## How To Run:

When the server is ready to be deployed, the following steps needs to be taken:

1. Modify the `application.properties` in order to connect to the real DB and change the value from Update to validate of the settings spring.jpa.hibernate.ddl-auto
2. Run the command `mvn clean install` this command Clears the target directory and builds the project described by your
   Maven POM file and installs the resulting artifact (JAR) into your local Maven repository
3. Run the command  `java -jar target\preparation-0.0.1-SNAPSHOT.jar` this command will run the server

## Project Description:

The goal of this backend project is to test the deployment on AWS.
the server is connected to a postgres DB that contains one table, `person`. the structure of the table is defined and controlled, thanks to the tage `@Entity`, using the class [`Entity_Person` file](src/main/java/planit/people/preparation/Entities/Entity_Person.java). 

## REST APIs Documentation:
All APIs' requests, responses and attributes are documented using [Spring REST Docs MockMvc](https://docs.spring.io/spring-restdocs/docs/2.0.6.RELEASE/reference/html5/). [`People APIs.adoc` file](src/main/java/planit/people/preparation/API_Documentation/People_APIs.adoc) contains the documentations for all APIs used in this project.

## Code Documentation:
Each Class, Method and Field is properly documented using [JavaDoc](https://www.oracle.com/technical-resources/articles/java/javadoc-tool.html). In order to generate Code Documentation using JavaDoc, specific commands, that can be found [here](https://docs.oracle.com/javase/9/javadoc/javadoc-command.htm#JSJAV-GUID-EAAAE17F-E540-42A0-B22B-4D2B2FD3E4D2), needs to run. 