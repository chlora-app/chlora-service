# ── Stage 1: Dependency cache ─────────────────────────────────────────────────
FROM maven:3.9-eclipse-temurin-21-alpine AS deps

WORKDIR /build
COPY pom.xml .
RUN --mount=type=cache,target=/root/.m2 mvn dependency:go-offline -q

# ── Stage 2: Build & extract layers ──────────────────────────────────────────
FROM deps AS builder

COPY src ./src
RUN --mount=type=cache,target=/root/.m2 mvn package -DskipTests -q

RUN java -Djarmode=layertools \
         -jar target/*.jar \
         extract --destination target/extracted

# ── Stage 3: Runtime ──────────────────────────────────────────────────────────
FROM eclipse-temurin:21-jre-alpine AS runtime

RUN addgroup -S chlora && adduser -S -G chlora chlora

WORKDIR /app

# ── Spring Boot layered JAR (least-to-most volatile) ─────────────────────────
COPY --from=builder --chown=chlora:chlora /build/target/extracted/dependencies/          ./
COPY --from=builder --chown=chlora:chlora /build/target/extracted/spring-boot-loader/    ./
COPY --from=builder --chown=chlora:chlora /build/target/extracted/snapshot-dependencies/ ./
COPY --from=builder --chown=chlora:chlora /build/target/extracted/application/           ./

USER chlora

EXPOSE 8080

# ── Health check ──────────────────────────────────────────────────────────────
HEALTHCHECK --interval=30s --timeout=5s --start-period=60s --retries=3 \
    CMD wget -qO- http://localhost:8080/actuator/health | grep -q '"status":"UP"' || exit 1

ENV TZ=UTC JAVA_TOOL_OPTIONS=""

ENTRYPOINT ["java", \
    "-XX:+UseContainerSupport", \
    "-XX:MaxRAMPercentage=75.0", \
    "-XX:+ExitOnOutOfMemoryError", \
    "-Djava.security.egd=file:/dev/./urandom", \
    "-Dspring.profiles.active=${SPRING_PROFILES_ACTIVE:-prod}", \
    "org.springframework.boot.loader.launch.JarLauncher"]