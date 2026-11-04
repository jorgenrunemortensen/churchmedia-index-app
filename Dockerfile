# ── Build stage ─────────────────────────────────────────────────────────────
# The common library (included via includeBuild) requires a Java 21 toolchain,
# while indexing-app itself requires Java 25. Both JDKs are made available to Gradle.
FROM eclipse-temurin:21-jdk AS jdk21

FROM eclipse-temurin:25-jdk AS build
COPY --from=jdk21 /opt/java/openjdk /opt/jdk21

# Mirror the host folder layout, so includeBuild('../../common') resolves
COPY --from=common-src . /workspace/common/
WORKDIR /workspace/church-media/indexing-app

# Copy Gradle wrapper and project descriptor files first (better layer caching)
COPY gradlew .
COPY gradle/ gradle/
COPY build.gradle settings.gradle ./

# Copy source and build the fat JAR
COPY src/ src/
RUN chmod +x gradlew && ./gradlew bootJar --no-daemon -x test \
    -Porg.gradle.java.installations.paths=/opt/jdk21,/opt/java/openjdk \
    -Porg.gradle.java.installations.auto-download=false

# ── Runtime stage ────────────────────────────────────────────────────────────
FROM eclipse-temurin:25-jre
WORKDIR /app

COPY --from=build /workspace/church-media/indexing-app/build/libs/*.jar app.jar

EXPOSE 8086

ENTRYPOINT ["java", "-jar", "app.jar"]
