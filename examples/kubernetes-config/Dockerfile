FROM amazoncorretto:17

COPY examples/kubernetes-config/build/libs/*.jar /app.jar

ENTRYPOINT ["java","-jar","/app.jar"]