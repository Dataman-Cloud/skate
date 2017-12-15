FROM anapsix/alpine-java:8

ENV SQUID_ZK_CONNECTION="zookeeper:2181"
ENV SQUID_METRICS_PORT="12345"
ENV SQUID_DISABLE_METRICS="false"
ENV AUTH_SECRET="123457"

VOLUME /tmp
ADD account-service-master-SNAPSHOT.jar app.jar
RUN bash -c 'touch /app.jar'
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/app.jar"]