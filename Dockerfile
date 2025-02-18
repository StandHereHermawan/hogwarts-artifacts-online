FROM eclipse-temurin:21-jre as builder
WORKDIR application
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} application.jar
RUN java -Djarmode=layertools -jar application.jar extract

FROM eclipse-temurin:21-jre
WORKDIR application

# App's Configurable Environment.
ENV SPRING_USER_NAME=''
ENV SPRING_USER_PASSWORD=''
ENV SPRING_APPLICATION_NAME=hogwarts_artifact_online
ENV SPRING_DATASOURCE_USERNAME=''
ENV SPRING_DATASOURCE_PASSWORD=''
ENV API_ENDPOINT_BASE_URL=/api/v1
ENV SERVER_PORT=''

COPY --from=builder application/dependencies/ ./
COPY --from=builder application/spring-boot-loader/ ./
COPY --from=builder application/snapshot-dependencies/ ./
COPY --from=builder application/application/ ./
# Main-Class: org.springframework.boot.loader.launch.JarLauncher as in MANIFEST.MF files.
# check MANIFEST.MF Files first then copy the Main-Class: value to
# "ENTRYPOINT ["java", "org.springframework.boot.loader.launch.JarLauncher"]".
ENTRYPOINT ["java", "org.springframework.boot.loader.launch.JarLauncher"]