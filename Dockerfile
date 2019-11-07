# Alpine Linux with OpenJDK JRE
FROM openjdk:8-jre-alpine

ENV APP_PATH /app

# copy dependencies into image
COPY target/dependency-jars/* $APP_PATH/dependency-jars/

# copy jar into image
COPY target/tutorial-kafka-java-1.0.jar $APP_PATH/tutorial-kafka-java-1.0.jar

WORKDIR $APP_PATH

# run application with this command line 
CMD ["java", "-cp", "tutorial-kafka-java-1.0.jar:dependency-jars/*", "com.test.Consumer"]
