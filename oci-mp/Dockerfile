
# 1st stage, build the app
FROM maven:3.8.4-openjdk-17-slim as build

WORKDIR /helidon

# Create a first layer to cache the "Maven World" in the local repository.
# Incremental docker builds will always resume after that, unless you update
# the pom
ADD pom.xml pom.xml
ADD client/pom.xml client/pom.xml
ADD server/pom.xml server/pom.xml
RUN mvn package -Dmaven.test.skip -Declipselink.weave.skip -DskipOpenApiGenerate

# Do the Maven build!
# Incremental docker builds will resume here when you change sources
ADD client/src client/src
ADD server/src server/src
ADD spec/api.yaml spec/api.yaml
RUN mvn package -DskipTests

RUN echo "done!"

# 2nd stage, build the runtime image
FROM openjdk:17-jdk-slim
WORKDIR /helidon

# Copy the binary built in the 1st stage
COPY --from=build /helidon/server/target/oci-mp-server.jar ./
COPY --from=build /helidon/server/target/libs ./libs

CMD ["java", "-jar", "oci-mp-server.jar"]

EXPOSE 8080
