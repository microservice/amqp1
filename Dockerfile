FROM        openjdk:12.0.1-jdk-oracle

RUN         yum -y install procps-ng

RUN         mkdir /app
WORKDIR     /app
ADD         .mvn .mvn
ADD         mvnw .

RUN         ./mvnw -v

ADD         microservice.yaml .
ADD         pom.xml .
ADD         src src

RUN         ls  /app/
RUN         ./mvnw package

ENTRYPOINT  java -Xms50m -Xmx50m -Xss256k -jar /app/target/amqp1-1.0-SNAPSHOT.jar