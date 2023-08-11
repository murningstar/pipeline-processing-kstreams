FROM openjdk

#EXPOSE 8080

# Add the application's jar to the container
WORKDIR /usr/src/processing

COPY target/processing-0.0.1-SNAPSHOT.jar ./app.jar

CMD java -jar /usr/src/processing/app.jar