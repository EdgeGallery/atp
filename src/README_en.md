# ATP-BE

[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)
![Jenkins](https://img.shields.io/jenkins/build?jobUrl=http%3A%2F%2Fjenkins.edgegallery.org%2Fview%2FMEC-PLATFORM-BUILD%2Fjob%2Fappstore-backend-docker-image-build-update-daily-master%2F)

ATP (Application test platform)Is a platform for application testing and certification，By building a unified test standard and building a test framework，For the developer platform andAPPStoreProvide consistentAPPTest experience


## Features Introduction

Upload、managementApp

## Compile and run

  atp-beExternally availablerestfulinterface，Open source basedServiceCombMicroservice framework for development，And integratedSpring Bootframe。Local operation needs to depend onServiceCenterPerform service registration discovery，bypostman进行interface测试。

- ### Environmental preparation（Run locally）
  
    |  Name     | Version   | Link |
    |  ----     | ----  |  ---- |
    | JDK1.8 |1.8xxx or above | [download](https://www.oracle.com/java/technologies/javase-jdk8-downloads.html)
    | MavApache Maven |3.6.3 | [download](https://maven.apache.org/download.cgi)
    | IntelliJ IDEA |Community |[download](https://www.jetbrains.com/idea/download/)
    | Servicecomb Service-Center    | 1.3.0 | [download](https://servicecomb.apache.org/cn/release/service-center-downloads/)
    | Postgres  | 9.6.17 or above |   [download](https://www.enterprisedb.com/downloads/postgres-postgresql-downloads) |

- ### Modify the configuration file/src/main/resources/application.properties

    - 1 modifypostgresConfiguration，Local installation defaultIPYes127.0.0.1，默认端口Yes5432，Default username and passwordpostgres/root，as follows：
    ```
    postgres.ip=127.0.0.1
    postgres.database=postgres
    postgres.port=5432
    postgres.username=postgres
    postgres.password=root
    ```
    - 2 ConfigurationService Center，Local installationIPYes127.0.0.1，Default
      port30100，servicecomb.nameYes注册到servicecenterService name on，Can be modified，默认Yesmec-atp，Configuration如下：
    ```
    #### Service Center config ####
    # ip or service name in k8s
    servicecenter.ip=127.0.0.1
    servicecenter.port=30100
    servicecomb.name=application-test-platform
    ```

- ### Compile and package
    Pull code from the code repository，defaultmasterBranch
    
    ```
    git clone https://github.com/EdgeGallery/atp.git
    ```

    Compile and build，Need to rely onJDK1.8，Compiling for the first time will be time-consuming，becausemavenNeed to download all dependent libraries。

    ```
    mvn clean install
    ```

- ### run
    cdTo package path，byjavastart up：
    ```
    java -jar atp.jar
    ```
    Access via browser after launch http://127.0.0.1/30103 You can check whether the service is successfully registered。
