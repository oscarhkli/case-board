FROM amazoncorretto:23.0.1-alpine

# Create a non-root user
RUN addgroup -S caseboarduser && adduser -S caseboarduser -G caseboarduser
USER caseboarduser:caseboarduser

# ARG variables (build-time arguments)
ARG SPRING_PROFILES_ACTIVE=cloud
ARG DEPENDENCY=target/dependency
COPY ${DEPENDENCY}/BOOT-INF/lib /app/lib
COPY ${DEPENDENCY}/META-INF /app/META-INF
COPY ${DEPENDENCY}/BOOT-INF/classes /app

# Set the active profile for Spring Boot application dynamically
ENV SPRING_PROFILES_ACTIVE=${SPRING_PROFILES_ACTIVE}

# ENTRYPOINT to run the Spring Boot application
ENTRYPOINT ["java","-Dspring.profiles.active=${SPRING_PROFILES_ACTIVE}","-cp","app:app/lib/*","com.oscarhkli.caseboard.CaseBoardApplication"]
