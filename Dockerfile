FROM maven:3.9.6-eclipse-temurin-21

WORKDIR /app

COPY pom.xml .

COPY . /app

RUN mvn package

CMD ["java", "-jar", "target/OTP1_inclass1_assignment_Hassan-1.0-SNAPSHOT.jar"]