FROM liquibase/liquibase:latest

# Add MySQL JDBC driver
USER root
RUN lpm add mysql --global
USER liquibase

WORKDIR /liquibase
