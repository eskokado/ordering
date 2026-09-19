FROM eclipse-temurin:25-jre
RUN apt-get update && apt-get install -y curl && rm -rf /var/lib/apt/lists/*
ENV JAR_NAME=ordering.jar
ADD build/libs/$JAR_NAME $JAR_NAME
CMD java $JAVA_OPTS -jar $JAR_NAME