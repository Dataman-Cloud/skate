FROM anapsix/alpine-java:8

ENV SQUID_ZK_ADDRESS="zookeeper:2181"
ENV SQUID_SERVICE_NAMESPACE="squid.dataman.com"
ENV SQUID_KAFKA_CLUSTER_NAME="kafka"
ENV LOG_HOME="/squid/demo/"
ENV SQUID_DEMO_CLIENT_SERVICE_NAME="payment-service"
ENV SQUID_DEMO_CLIENT_DEPENDENCY_SERVICE_NAME="account-service"
ENV SQUID_DEMO_CLIENT_DEPENDENCY_SERVICE_NAMESPACE="squid.dataman.com"
ENV SQUID_DEMO_CLIENT_SLEEPTIME_MS="1000"
ENV SQUID_DEMO_CLIENT_SERVER_SLEEPTIME_MS="1"

VOLUME /tmp
ADD payment-service-master-SNAPSHOT.jar app.jar
RUN bash -c 'touch /app.jar'
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/app.jar"]