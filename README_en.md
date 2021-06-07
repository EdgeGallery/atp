[ch](README.md) | [en](README_en.md)

# ATP-BE

[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)
![Jenkins](https://img.shields.io/jenkins/build?jobUrl=http%3A%2F%2Fjenkins.edgegallery.org%2Fview%2FMEC-PLATFORM-BUILD%2Fjob%2Fappstore-backend-docker-image -build-update-daily-master%2F)

ATP (Application test platform) is a platform for application test certification. It provides a consistent APP test experience for the developer platform and APPStore by building a unified test standard and a test framework.


## Feature introduction

Upload and manage App

## Compile and run

  atp-be provides restful interfaces to the outside world, develops based on the open source ServiceComb microservice framework, and integrates the Spring Boot framework. Local operation needs to rely on ServiceCenter for service registration discovery, and interface testing through postman.

-### Environment preparation (local operation)
  
    | Name | Version | Link |
    | ---- | ---- | ---- |
    | JDK1.8 |1.8xxx or above | [download](https://www.oracle.com/java/technologies/javase-jdk8-downloads.html)
    | MavApache Maven |3.6.3 | [download](https://maven.apache.org/download.cgi)
    | IntelliJ IDEA |Community |[download](https://www.jetbrains.com/idea/download/)
    | Servicecomb Service-Center | 1.3.0 | [download](https://servicecomb.apache.org/cn/release/service-center-downloads/)
    | Postgres | 9.6.17 or above | [download](https://www.enterprisedb.com/downloads/postgres-postgresql-downloads) |

-### Modify the configuration file /src/main/resources/application.properties

    -1 Modify the postgres configuration, the default IP for local installation is 127.0.0.1, the default port is 5432, and the default user name and password postgres/root are as follows:
    ```
    postgres.ip=127.0.0.1
    postgres.database=postgres
    postgres.port=5432
    postgres.username=postgres
    postgres.password=root
    ```
    -2 Configure Service Center, the local installation IP is 127.0.0.1, the default port is 30100, servicecomb.name is the service name registered to the servicecenter, which can be modified, the default is mec-developer, the configuration is as follows:
    ```
    #### Service Center config ####
    # ip or service name in k8s
    servicecenter.ip=127.0.0.1
    servicecenter.port=30100
    servicecomb.name=application-test-platform
    ```

-### Compile and package
    Pull code from the code repository, the default master branch
    
    ```
    git clone https://github.com/EdgeGallery/atp.git
    ```

    Compile and build, need to rely on JDK1.8, the first compilation will be more time-consuming, because maven needs to download all dependent libraries.

    ```
    mvn clean install
    ```

-### Run
    cd to the packaging path and start via java:
    ```
    java -jar atp.jar
    ```
    After startup, you can visit http://127.0.0.1/30103 through the browser to check whether the service is successfully registered.