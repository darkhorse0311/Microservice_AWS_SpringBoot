FROM openjdk:17
VOLUME /tmp
ADD ./target/service01-0.0.1-SNAPSHOT.jar service01.jar
ENTRYPOINT ["java","-jar","/service01.jar"]