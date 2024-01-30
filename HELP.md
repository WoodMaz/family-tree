# Getting Started

# Documentation

### Features
As of this moment this project is a simple CRUD app. It allows to create a new member,
who can be related to other members.

### Database
This project uses MongoDB. Currently, there are 2 collections:

'User' which contains:
* username
* encrypted password
* role (not used yet)

'Person' which contains:
* name
* surname
* dates of birth and death
* id of mother, father and spouse
* note
* userId

### TODO list

Database:
'User' and 'Person' can be related with 'familyTreeId' so that one user can have many trees

Application:
* test and develop security
* consider advanced features:
* * sharing data between users
* * joining individual trees
* * storing multimedia
* * downloading a tree as e.g. PDF
* * import/export as .ged

# Reference Documentation
For further reference, please consider the following sections:

* [Official Apache Maven documentation](https://maven.apache.org/guides/index.html)
* [Spring Boot Maven Plugin Reference Guide](https://docs.spring.io/spring-boot/docs/3.0.1/maven-plugin/reference/html/)
* [Create an OCI image](https://docs.spring.io/spring-boot/docs/3.0.1/maven-plugin/reference/html/#build-image)
* [Spring Web](https://docs.spring.io/spring-boot/docs/3.0.1/reference/htmlsingle/#web)
* [Spring Data JPA](https://docs.spring.io/spring-boot/docs/3.0.1/reference/htmlsingle/#data.sql.jpa-and-spring-data)

### Guides
The following guides illustrate how to use some features concretely:

* [Building a RESTful Web Service](https://spring.io/guides/gs/rest-service/)
* [Serving Web Content with Spring MVC](https://spring.io/guides/gs/serving-web-content/)
* [Building REST services with Spring](https://spring.io/guides/tutorials/rest/)
* [Accessing Data with JPA](https://spring.io/guides/gs/accessing-data-jpa/)






