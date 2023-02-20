# oci-mp-server

# Helidon OCI Archetype Application

This example demonstrates Helidon's integration with Oracle Cloud Infrastructure (OCI) services using the OCI SDK. It shows the following:

1. Server code generation
2. Client code generation
3. Integration with the OCI SDK
4. OCI IAM Authentication: User Principal and Instance Principal
5. MicroProfile Metrics published to OCI Monitoring Service
6. Logs published to OCI Logging Service
7. OCI Custom Logs Monitoring using Unified Monitoring Agent
8. Health and Liveness checks
9. Configuration profiles for switching between `config_file` and `instance_principal` configurations

This project demonstrates OpenApi-driven development approach where the practice of designing and building APIs is done first, 
then creating the rest of an application around them is implemented next. Below are the modules that are part of this project:

1. [Spec](src/spec/README.md) - Contains OpenApi v3 specification documentation that will be used as input for
   both the [server](src/server/README.md) and [client](src/client/README.md) modules in generating server and client side code
2. [Server](src/server/README.md) - Generates server-side JAX-RS service and model source code which are used to implement
   business logic for the [server](src/server/README.md) application
3. [Client](src/client/README.md) - Generates client-side microprofile rest client and model source code which can be
   injected/initialized into the client application for the purpose of accessing the [server](src/server/README.md) application


Here's a high level diagram of how this project will work:

                      +-------------------------------------+
                      | OpenApi Specification Documentation |
                      +-------------------------------------+
                                         ^   
                                         |
                            +------------+------------+
                            |                         |
                            |                         |
                   +--------+--------+       +--------+--------+
                   | Generate Client |       | Generate Server |
                   |   Source Code   |       |   Source Code   |
                   +--------+--------+       +--------+--------+
                            |                         | 
                            |                         |
                            v                         v
                  +--------------------+     +-------------------+
                  |                    |     | Sever Application |
                  | Client Application +---->|    port:8080      |
                  |                    |     |    path: /greet   |
                  +--------------------+     +-------------------+


## Build and run



### Build the application

1. Build without running a test
   ```bash
   mvn clean package -DskipTests
   ```
2. Build with run default test profile using mocked unit tests
   ```bash
   mvn clean package
   ```
3. Build and run `test` test profile that performs authentication for OCI services via .oci config file
   ```bash
   mvn clean package -Ptest
   ```
4. Build and execute `prod` test profile that performs OCI services authentication via instance
principal when running it in an oci compute instance.
   ```bash
   mvn clean package -Pprod

### Run the application

1. Default with no profile will use `config_file,instance_principals,resource_principal` authentication strategy
     ```bash
     java -jar server/target/oci-mp-server.jar
     ```
2. `test` profile will use user principal via oci config file authentication
     ```bash
     java -Dconfig.profile=test -jar server/target/oci-mp-server.jar
     ```
3. `prod` profile will use instance principal authentication
     ```bash
     java -Dconfig.profile=prod -jar server/target/oci-mp-server.jar
     ```
            

## Exercise the application

1. Use curl to access the client application
   ```
   curl -X GET http://localhost:8080/greet
   {"message":"Hello World!","date":[2022,4,1]}

   curl -X GET http://localhost:8080/greet/Joe
   {"message":"Hello Joe!","date":[2022,4,1]}

   curl -X PUT -H "Content-Type: application/json" -d '{"greeting" : "Ola"}' http://localhost:8080/greet/greeting
   curl -X GET http://localhost:8080/greet
   {"message":"Ola World!","date":[2022,4,8]}
   ```
2. Use curl to access the health checks:
   ```
   $ curl -X GET  http://localhost:8080/health/live
   {"outcome":"UP","status":"UP","checks":[{"name":"CustomLivenessCheck","state":"UP","status":"UP","data":{"time":1646361485815}}]}
   $ curl -X GET  http://localhost:8080/health/ready
   {"outcome":"UP","status":"UP","checks":[{"name":"CustomReadinessCheck","state":"UP","status":"UP","data":{"time":1646361474774}}]}
   ```
                


## Building the Docker Image

```
docker build -t oci-mp-server .
```

## Running the Docker Image


```
docker run --rm -p 8080:8080 -v ~/.oci:/root/.oci:ro oci-mp-server:latest
```
This will mount `~/.oci` as a volume in the running docker container.


Exercise the application as described above.
                                

## Run the application in Kubernetes

If you don’t have access to a Kubernetes cluster, you can [install one](https://helidon.io/docs/latest/#/about/kubernetes) on your desktop.

### Verify connectivity to cluster

```
kubectl cluster-info                        # Verify which cluster
kubectl get pods                            # Verify connectivity to cluster
```


### Configure the application

Make sure, your application has access to your OCI setup. One way, you can do so, if your kubernetes cluster is running locally, is by volume.

Create a volume pointing to your OCI configuration file
```yaml
      volumes:
        - name: oci-config
          hostPath:
            # directory location on host
            path: <Directory with oci config file>
```

Mount this volume as part of your application containers specification
```yaml
        volumeMounts:
        - mountPath: /root/.oci
          name: oci-config
```
                                

### Deploy the application to Kubernetes

```
kubectl create -f app.yaml                  # Deploy application
kubectl get pods                            # Wait for quickstart pod to be RUNNING
kubectl get service  oci-mp-server         # Get service info
```

Note the PORTs. You can now exercise the application as you did before but use the second
port number (the NodePort) instead of 8080.

After you’re done, cleanup.

```
kubectl delete -f app.yaml
```
                                
