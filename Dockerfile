FROM eclipse-temurin:11 as builder

VOLUME C:/logs/tmp

ARG DEPENDENCY=target/dependency

COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .
COPY src src

RUN ./mvnw install -DskipTests
RUN ./mvnw package -DskipTests
RUN mkdir -p ${dependency} && cd ${dependency} && jar -xf ../*.jar

FROM eclipse-temurin:11

ARG DEPENDENCY=target/dependency

COPY --from=builder ${DEPENDENCY}/BOOT-INF/lib /app/lib
COPY --from=builder ${DEPENDENCY}/META-INF /app/META-INF
COPY --from=builder ${DEPENDENCY}/BOOT-INF/classes /app

ENV DB_USERNAME="postgres"
ENV DB_PASSWORD="mysecretpassword"
ENV DB_URL="jdbc:postgresql://localhost:5433/software"
ENV API_KEY=""
ENV PORT=8080

ENTRYPOINT ["java","-cp","app:app/lib/*","ucb.arqsoft.currencyconverter.CurrencyConverterApplicationKt"]