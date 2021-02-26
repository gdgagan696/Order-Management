FROM openjdk:8-jdk-alpine
LABEL maintainer="Gagan Dhand"
EXPOSE 8030
ADD ./target/order-management-0.0.1-SNAPSHOT.jar /
ENTRYPOINT ["java","-jar","order-management-0.0.1-SNAPSHOT.jar"]
