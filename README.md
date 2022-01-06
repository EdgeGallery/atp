[ch](README.md) | [en](README_en.md)

# ATP-BE

[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)
![Jenkins](https://img.shields.io/jenkins/build?jobUrl=http%3A%2F%2Fjenkins.edgegallery.org%2Fview%2FMEC-PLATFORM-BUILD%2Fjob%2Fappstore-backend-docker-image-build-update-daily-master%2F)

ATP (Application test platform)是应用测试认证的平台，目前分为管理面功能和用户面功能。
- 在管理面，管理员可以管理测试场景、测试套、测试用例、测试任务、贡献的测试用例以及配置项，其中贡献管理的菜单仅管理员可见。平台内置了社区场景以及3个样例运营商场景。对于社区场景，平台内置了通用安全性测试测试套、通用遵从性测试测试套和通用沙箱测试测试套，每个测试套下面都有对应的测试用例。平台支持一键式批量导入测试模型，方便批量测试模型的新增。对于有手工用例的测试任务，管理员可以在后台手动更改测试用例状态，完成手工测试用例状态的更新。
- 在用户面（即集成在开发者平台和应用商店中的功能），对于开发者，在开发者平台开发app以后，对生成的csar包，要经过应用测试认证服务测试通过后才可以发布到应用商店；对于已经有应用包的供应商或者厂商，在应用商店中上传应用包以后，也要经过应用测试认证服务测试通过后才可以发布到应用商店。用户可以选择要进行测试的场景，其中社区场景是必选的，然后系统会动态展示出测试用例的执行过程。当测试完成后，会生成测试报告，给出测试结果的分析，用户也可以下载测试报告，格式为pdf格式。用户面也为用户提供了贡献测试用例的入口，用户可以通过填写对测试用例的描述或者上传脚本的方式，完成对测试用例的贡献。



## 特性介绍

#### 选择测试场景
用户可以根据需要，选择要进行的测试场景，ATP服务会执行对应场景的测试用例。

#### 测试任务进展展示
动态展示一条测试任务的执行进展，可以动态看到各个场景下面各个用例的执行情况。

#### 测试报告分析
展示测试报告结果。报告会给出社区场景及所选场景的测试用例结果分析，显示出各个测试套的通过率，用例数量以及具体测试用例的失败原因。

#### 测试报告下载
提供测试报告下载功能，将测试报告作为pdf格式下载。

#### 贡献测试用例
用户面支持用户贡献测试用例的入口，支持用户贡献测试用例脚本，或者贡献测试用例的文本描述。

#### 自测报告导入
在自动执行完测试用例后，支持用户上传自测报告。如果用户有功能性测试等应用特定的测试，可以上传自测报告，应用测试平台会将平台用例执行报告和用户上传自测报告整合成一份测试报告展示。

#### 测试场景管理
包含测试场景的展示、新增、删除、修改操作。

#### 测试套管理
包含测试套的展示、新增、删除、修改操作。

#### 测试用例管理
包含测试用例的展示、新增、删除、修改操作。

#### 测试模型一键导入
可以将批量的测试场景、测试套、测试用例定义到excel中，测试用例脚本和测试场景图标放到文件夹中，最终将这些文件压缩成zip包，在管理面一键式导入批量的测试模型数据。

#### 测试任务分析
ATP管理面首页展示近6个月测试任务数量的分布情况以及总数。

#### 测试任务管理
包含测试任务的展示、以及测试任务的批量删除操作。

#### 贡献管理
包含脚本类型的贡献用例下载、贡献的批量删除操作。

#### 配置项管理
可以针对某个测试用例配置参数，使测试用例更灵活。

## 编译运行

  atp-be对外提供restful接口，基于开源的ServiceComb微服务框架进行开发，并且集成了Spring Boot框架。本地运行需要依赖ServiceCenter进行服务注册发现，通过postman进行接口测试。

- ### 环境准备（本地运行）
  
    |  Name     | Version   | Link |
    |  ----     | ----  |  ---- |
    | JDK1.8 |1.8xxx or above | [download](https://www.oracle.com/java/technologies/javase-jdk8-downloads.html)
    | MavApache Maven |3.6.3 | [download](https://maven.apache.org/download.cgi)
    | IntelliJ IDEA |Community |[download](https://www.jetbrains.com/idea/download/)
    | Servicecomb Service-Center    | 1.3.0 | [download](https://servicecomb.apache.org/cn/release/service-center-downloads/)
    | Postgres  | 9.6.17 or above |   [download](https://www.enterprisedb.com/downloads/postgres-postgresql-downloads) |

- 本地环境搭建及代码运行请参考文档：
http://docs.edgegallery.org/zh_CN/latest/Projects/ATP/ATP_Contribution.html


- ### 编译打包
    从代码仓库拉取代码，默认master分支
    
    ```
    git clone https://github.com/EdgeGallery/atp.git
    ```

    编译构建，需要依赖JDK1.8，首次编译会比较耗时，因为maven需要下载所有的依赖库。

    ```
    mvn clean install
    ```
