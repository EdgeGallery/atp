#
#    Copyright 2020-2021 Huawei Technologies Co., Ltd.
#
#    Licensed under the Apache License, Version 2.0 (the "License");
#    you may not use this file except in compliance with the License.
#    You may obtain a copy of the License at
#
#        http://www.apache.org/licenses/LICENSE-2.0
#
#    Unless required by applicable law or agreed to in writing, software
#    distributed under the License is distributed on an "AS IS" BASIS,
#    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
#    See the License for the specific language governing permissions and
#    limitations under the License.
#
FROM swr.cn-north-4.myhuaweicloud.com/eg-common/openjdk:8u201-jdk-alpine

# Define all environment variable here
#ENV JAVA_HOME /usr/lib/jvm/java-1.8-openjdk
ENV TZ='Asia/Shanghai'
ENV APP_FILE application-test-platform.jar
ENV UID=166
ENV GID=166
ENV USER_NAME=eguser
ENV GROUP_NAME=eggroup
ENV APP_HOME /usr/app
ENV ENV="/etc/profile"

# # CREATE APP USER ##
# Set umask
RUN sed -i "s|umask 022|umask 027|g" /etc/profile

# Create the home directory for the new app user.
RUN mkdir -p /usr/app
RUN mkdir -p /usr/app/bin

RUN apk update && \
    apk add nmap nmap-scripts nmap-nping bash shadow

# Create an app user so our program doesn't run as root.
RUN groupadd -r -g $GID $GROUP_NAME &&\
    useradd -r -u $UID -g $GID -d $APP_HOME -s /sbin/nologin -c "Docker image user" $USER_NAME

WORKDIR $APP_HOME

RUN chmod 750 $APP_HOME &&\
    chmod -R 550 $APP_HOME/bin &&\
    mkdir -p -m 750 $APP_HOME/log &&\
    mkdir -p -m 700 $APP_HOME/ssl &&\
    mkdir -p -m 750 $APP_HOME/testCases &&\
    mkdir -p -m 750 $APP_HOME/icon &&\
    chown -R $USER_NAME:$GROUP_NAME $APP_HOME

RUN mkdir -p -m 750 /usr/atp &&\
    mkdir -p -m 750 /usr/atp/file &&\
    chown -R $USER_NAME:$GROUP_NAME /usr/atp

COPY --chown=$USER_NAME:$GROUP_NAME target/*.jar $APP_HOME/bin
COPY --chown=$USER_NAME:$GROUP_NAME target/classes/testCase/ $APP_HOME/testCases/
COPY --chown=$USER_NAME:$GROUP_NAME target/classes/icon/ $APP_HOME/icon/

EXPOSE 8073

# Change to the app user.
USER $USER_NAME

# Execute script & application
ENTRYPOINT ["sh", "-c"]
CMD ["exec java -jar -Dlog4j2.formatMsgNoLookups=true ./bin/$APP_FILE"]
