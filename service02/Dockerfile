FROM openjdk:17
VOLUME /tmp
ADD ./target/service02-0.0.1-SNAPSHOT.jar service02.jar
ENTRYPOINT ["java","-jar","/service02.jar"]