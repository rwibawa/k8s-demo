FROM openjdk:8-jdk-alpine as build-stage

WORKDIR /srv

ADD ./src ./src
ADD pom.xml mvnw settings.xml ./
ADD ./.mvn ./.mvn

RUN ./mvnw -s ./settings.xml -Dmaven.repo.local=./.m2/repository package

FROM openjdk:8-jdk-alpine

COPY --from=build-stage /srv/dist/k8s-demo-0.0.1-SNAPSHOT.jar /srv/dist/k8s-demo-0.0.1-SNAPSHOT.jar
WORKDIR /srv/dist

EXPOSE 8097
EXPOSE 5005

ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-Xdebug","-Xrunjdwp:transport=dt_socket,server=y,suspend=n,address=5005","-jar","k8s-demo-0.0.1-SNAPSHOT.jar"]
