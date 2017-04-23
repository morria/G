FROM openjdk:7
RUN mkdir -p /usr/src/g
COPY . /usr/src/g
WORKDIR /usr/src/g
CMD ["java", "-jar", "target/G.jar"]
EXPOSE 7070
