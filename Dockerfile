FROM maven:3.9.6-eclipse-temurin-21 AS build

WORKDIR /app
COPY pom.xml .
RUN mvn -q dependency:go-offline
COPY src ./src
RUN mvn -q test package

FROM maven:3.9.6-eclipse-temurin-21

RUN apt-get update \
    && DEBIAN_FRONTEND=noninteractive apt-get install -y --no-install-recommends \
        libgtk-3-0 libgl1-mesa-glx libxtst6 libxrender1 libx11-xcb1 \
        xvfb x11-utils dbus fontconfig fonts-noto-cjk locales \
    && sed -i 's/^# *ja_JP.UTF-8 UTF-8/ja_JP.UTF-8 UTF-8/' /etc/locale.gen \
    && locale-gen ja_JP.UTF-8 \
    && fc-cache -f \
    && rm -rf /var/lib/apt/lists/*

ENV LANG=ja_JP.UTF-8 \
    LANGUAGE=ja_JP:en_US:en \
    LC_ALL=ja_JP.UTF-8

WORKDIR /app
COPY --from=build /root/.m2 /root/.m2
COPY --from=build /app/pom.xml /app/pom.xml
COPY --from=build /app/src /app/src
COPY --from=build /app/target /app/target

ENV DISPLAY=:99
ENTRYPOINT ["bash", "-lc", "if [ \"$DISPLAY\" = \":99\" ]; then Xvfb :99 -screen 0 1024x768x24 > /dev/null 2>&1 & xvfb_pid=$!; trap 'kill $xvfb_pid 2>/dev/null || true' EXIT; sleep 2; fi; exec mvn javafx:run"]
