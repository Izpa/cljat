FROM openjdk:8-alpine

COPY target/uberjar/cljat.jar /cljat/app.jar

EXPOSE 3000

CMD ["java", "-jar", "/cljat/app.jar"]
