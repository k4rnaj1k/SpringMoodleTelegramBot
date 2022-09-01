FROM maven:3.8.6-openjdk-18-slim
WORKDIR ./mentor-buddy
COPY ./pom.xml ./
COPY ./src ./src
RUN mvn -B clean verify
ENTRYPOINT ["mvn","-B", "spring-boot:run"]