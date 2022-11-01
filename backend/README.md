# **PlanIt - Spring Boot**

## REST APIs Documentation:
All APIs' requests, responses and attributes are documented using [Spring REST Docs MockMvc](https://docs.spring.io/spring-restdocs/docs/2.0.6.RELEASE/reference/html5/).

[`User APIs.adoc` file](src/main/java/planit/people/preparation/API_Documentation/User_APIs.adoc) contains the documentations for all APIs that belong to the [plan-it/user endpoint](src/main/java/planit/people/preparation/APIs/API_User.java):
- Create New PlanIt User
- Add A New Google Account Email to a PlanIt User
- Get All Emails For A PlanIt User

[`Calendar APIs.adoc` file](src/main/java/planit/people/preparation/API_Documentation/Calendar_APIs.adoc) contains the documentations for all APIs that belong to the [plan-it/calendar endpoint](src/main/java/planit/people/preparation/APIs/API_Calendar.java).

- Create New Event 
- Create New Event Preset Detail
- Get All Event Preset Details For A PlanIt User

[`OAuth APIs.adoc` file](src/main/java/planit/people/preparation/API_Documentation/OAuth_APIs.adoc) contains the documentations for all APIs that belong to the [plan-it/oauth endpoint](src/main/java/planit/people/preparation/APIs/API_OAuth.java):
- Get PlanIt User Id From Their Email

## Code Documentation:
Each Class, Method and Field is properly documented using [JavaDoc](https://www.oracle.com/technical-resources/articles/java/javadoc-tool.html). In order to generate Code Documentation using JavaDoc, specific commands, that can be found [here](https://docs.oracle.com/javase/9/javadoc/javadoc-command.htm#JSJAV-GUID-EAAAE17F-E540-42A0-B22B-4D2B2FD3E4D2), needs to run. 