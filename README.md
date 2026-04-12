# Hibernate monitoring

This repository is a sample application for my
[Hibernate logging and monitoring guide](https://beaufume.fr/articles/hibernate-monitoring/) article.

This simple Spring Boot web application shows how to use several Hibernate observability features such as logs
and metrics.

The project was started with Spring Boot 2 then migrated to Spring Boot 3 then 4.
The main branch targets Spring Boot 4. To use the Spring Boot 2 ou 3 variant, use the dedicated branch.

## Usage

Configure the Hibernate logging and monitoring as needed by editing `application.properties`.

Run the application with `mvn spring-boot:run` or using your IDE.

Then open http://localhost:8080/ and use the links to execute some persistence operations.
