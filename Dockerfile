FROM maven:3.9.6-eclipse-temurin-21 AS build

WORKDIR /app
COPY pom.xml .
RUN mvn -q dependency:go-offline
COPY src ./src
RUN mvn -q test package

FROM maven:3.9.6-eclipse-temurin-21

RUN apt-get update \
    && apt-get install -y --no-install-recommends \
        libgtk-3-0 libgl1-mesa-glx libxtst6 libxrender1 libx11-xcb1 \
        xvfb x11-utils dbus \
    && rm -rf /var/lib/apt/lists/*

WORKDIR /app
COPY --from=build /root/.m2 /root/.m2
COPY --from=build /app/pom.xml /app/pom.xml
COPY --from=build /app/src /app/src
COPY --from=build /app/target /app/target

ENV DISPLAY=:99
ENTRYPOINT ["bash", "-c", "Xvfb :99 -screen 0 1024x768x24 > /dev/null 2>&1 & sleep 2 && mvn javafx:run"]
