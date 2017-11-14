FROM java:8
MAINTAINER evgeniy.tashkinov@gmail.com
VOLUME /tmp
ADD ${project.build.finalName}.jar app.jar
EXPOSE 8080
RUN bash -c 'touch /app.jar'
ENTRYPOINT ["java", "-Djava.security.egd=file:/dev/./urandom","-jar","/app.jar"]