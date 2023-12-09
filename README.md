# Hibernate monitoring

This repository is a sample application for my [Hibernate monitoring guide](https://www.adeliosys.fr/articles/hibernate-monitoring/) article.

This simple Spring Boot web application shows how to use several Hibernate monitoring features such as logs
and metrics/statistics.

The project was started with Spring Boot 2.7.4 then migrated to Spring Boot 3.0.1.
To stick with Spring Boot 2.7.4, use the `spring-boot-2` tag.

## Usage

Configure the Hibernate monitoring features as needed by editing `application.yml`.

Run the application with `mvn spring-boot:run` or using your IDE.

Then open http://localhost:8080/ and use the links to execute some persistence operations.
