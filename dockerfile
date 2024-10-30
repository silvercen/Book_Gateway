FROM amazoncorretto:21.0.5-alpine3.20
COPY ./target/Book_Gateway-0.0.1-SNAPSHOT.jar Book_Gateway.jar
CMD ["java","-jar","Book_Gateway.jar"]