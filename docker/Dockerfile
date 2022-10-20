FROM adoptopenjdk:11-jre-hotspot
ARG JAR_FILE=*.jar
COPY ${JAR_FILE} wup.jar
ENTRYPOINT ["java", "-jar", "wup.jar"]