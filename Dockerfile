FROM eclipse-temurin:17-jdk

COPY target/task-management.jar task-management.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","/task-management.jar"]
