[ch](README.md) | [en](README_en.md)

# ATP-BE

[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)
![Jenkins](https://img.shields.io/jenkins/build?jobUrl=http%3A%2F%2Fjenkins.edgegallery.org%2Fview%2FMEC-PLATFORM-BUILD%2Fjob%2Fappstore-backend-docker-image-build-update-daily-master%2F)

ATP (Application test platform) is currently divided into management side functions and user plane functions. 

On the management side, administrators can manage test scenarios, test suites, test cases, test tasks, contributed test cases and config item. The menu for contribution management is visible only to the administrator. The platform has built-in community scenarios and 3 sample operator scenarios. For community scenarios, the platform has built-in general security test test suites, general compliance test test suites, and general sandbox test test suites. There are corresponding test cases under each test suite. The platform supports one-click batch import of test models to facilitate the addition of batch test models. For test tasks with manual use cases, the administrator can manually change the test case status in the background to complete the update of the manual test case status.  

On the user plane side, after the developer platform develops app, the generated csars package will not be published to the app store until after the application test is passed. For suppliers or manufacturers who already have an application package, the app can be released to the app store after the app has been uploaded. The user can select the scene to be tested, wherein the community scene is selected and then the system dynamically displays the execution process of the test case. When the test is complete, a test report is generated and an analysis of the test results is given and the user can download the test report in pdf format. User can finish the contribution to the test case by filling the description of the test case or the way of uploading the script."


## Features Introduction

#### Choose a test scenario
The user can select the test scene to be carried out according to the need and the ATP service executes the test case of the corresponding scene.

#### Display test task process
The dynamic display of the implementation of a test task can dynamically see the implementation of each use case under each scene.

#### Test report analysis
Show test report results.The report gives the analysis of the community scene and the test case result of the selected scene, showing the pass rate of each test sleeve, the number of the use routine and the failure reason of the specific test case.

#### Test report download
Provide the test report download function to download the test report as pdf format.

#### Test cases contribution
The user side supports the entrance of the user contribution test case, supports the user contribution test case script, or contributes the text description of the test case.

#### Self-test report upload
After the test cases are automatically executed, users can upload self-test reports. If users have application specific tests such as functional tests, they can upload self-test reports. The application test platform will integrate the platform test case execution report and the user uploaded self-test report into one test report display

#### Test scenario management 
Contains the demo, add, delete, modify operation of the test scene.

#### Test Suite Management
Contains the demo, add, delete, modify operation of the test suite.

#### Test Case Management
Contains display of test cases, add, delete, modify operations.

#### One-click import of test models
One can define batches of test scenarios, test suites, and test cases in excel, put test case scripts and test scenario icons in a folder, and finally compress these files into a zip package, and import batch test models in one-click on the management side data.

#### Test task analysis
The ATP Management Face home page shows the distribution of the number of test tasks in nearly 6 months and the total number.

#### Test task management
Contains the display of the test task and the batch delete operation of the test task.

#### Contribution management
Contains script-type contribution use case download and contribution batch deletion operations.

#### Config item management
You can config parameters for a test case to make the test case more flexible

## Compile and run
Atp-be provides external restful interface, develops based on the open source servicecomb microservice framework, and integrates the spring boot framework. Local operation needs to rely on servicecenter for service registration and discovery, and interface testing through postman.

- ### Environmental preparation（Run locally）
  
    |  Name     | Version   | Link |
    |  ----     | ----  |  ---- |
    | JDK1.8 |1.8xxx or above | [download](https://www.oracle.com/java/technologies/javase-jdk8-downloads.html)
    | MavApache Maven |3.6.3 | [download](https://maven.apache.org/download.cgi)
    | IntelliJ IDEA |Community |[download](https://www.jetbrains.com/idea/download/)
    | Servicecomb Service-Center    | 1.3.0 | [download](https://servicecomb.apache.org/cn/release/service-center-downloads/)
    | Postgres  | 9.6.17 or above |   [download](https://www.enterprisedb.com/downloads/postgres-postgresql-downloads) |

- Please refer to the following documents for local environment construction and code operation:
http://docs.edgegallery.org/zh_CN/latest/Projects/ATP/ATP_Contribution.html

- ### Compile and package
    Pull code from the code repository，defaultmasterBranch
    
    ```
    git clone https://github.com/EdgeGallery/atp.git
    ```

    Compile and build，Need to rely onJDK1.8，Compiling for the first time will be time-consuming，becausemavenNeed to download all dependent libraries。

    ```
    mvn clean install
    ```
