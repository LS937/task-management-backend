FROM eclipse-temurin:21.0.10_7-jre-ubi9-minimal

COPY target/task-management-app.jar task-management-app.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","/task-management-app.jar"]
