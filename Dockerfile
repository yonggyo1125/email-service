FROM openjdk:17-jdk
ARG JAR_FILE=build/libs/emailservice-0.0.1-SNAPSHOT.jar
COPY ${JAR_FILE} app.jar

ENV REDIS_PORT=6379

ENTRYPOINT ["java", "-jar", "-Ddb.host=${DB_HOST}", "-Ddb.password=${DB_PASSWORD}", "-Ddb.username=${DB_USERNAME}", "-Dddl.auto=${DDL_AUTO}", "-Dconfig.server=${CONFIG_SERVER}", "-Deureka.server=${EUREKA_SERVER}", "-Dhostname=${HOSTNAME}","-Dredis.host=${REDIS_HOST}", "-Dredis.port=${REDIS_PORT}", "-Dmail.username=${MAIL_USERNAME}", "-Dmail.password=${MAIL_PASSWORD}", "app.jar"]

EXPOSE 3004