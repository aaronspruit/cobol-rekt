#
# Build stage
#
FROM maven:3.9-eclipse-temurin-21-jammy as build
ENV HOME=/usr/app
RUN mkdir -p $HOME
WORKDIR $HOME
ADD . $HOME
RUN mvn clean verify

#
# Package stage
#
FROM eclipse-temurin:21-jre-jammy 
COPY --from=build /usr/app/smojol-cli/target/*.jar /app/cli.jar
COPY --from=build /usr/app/che-che4z-lsp-for-cobol-integration/server/dialect-daco/target/*.jar /app/dialect-daco.jar
COPY --from=build /usr/app/che-che4z-lsp-for-cobol-integration/server/dialect-idms/target/*.jar /app/dialect-idms.jar
CMD ["bash"]