#!/usr/bin/env bash

set -e

#请修改对应images的版本号
SKATE_VERSION=${PUBLISH_VERSION:-latest}

#填入相应的影像前缀
IMAGE_PREFIX_ID=skate

HOST_IP=`ifconfig | grep 'inet'| grep -v '127.0.0.1'|grep -v '172.' | cut -d: -f2 | awk '{ print $2}'`

#缺省WEB界面的访问IP地址，有需要则修改
WEB_IP=$HOST_IP

# docker-machine doesn't exist in Linux, assign default ip if it's not set
DOCKER_IP=${HOST_IP:-192.168.31.46}
WEB_IP=${WEB_IP:-192.168.31.46}
IMAGE_PREFIX=${IMAGE_PREFIX_ID:-192.168.31.34}

export SKATE_VERSION DOCKER_IP WEB_IP IMAGE_PREFIX

# Remove existing containers
docker-compose -f docker-compose_local.yml stop
docker-compose -f docker-compose_local.yml rm -f

# Start the config service first and wait for it to become available
docker-compose -f docker-compose_local.yml up -d config-service

while [ -z ${CONFIG_SERVICE_READY} ]; do
  echo "Waiting for config service..."
  if [ "$(curl --silent $DOCKER_IP:8888/health 2>&1 | grep -q '\"status\":\"UP\"'; echo $?)" = 0 ]; then
      CONFIG_SERVICE_READY=true;
  fi
  sleep 2
done

# Start the discovery service next and wait
docker-compose -f docker-compose_local.yml up -d discovery-service

while [ -z ${DISCOVERY_SERVICE_READY} ]; do
  echo "Waiting for discovery service..."
  if [ "$(curl --silent $DOCKER_IP:8761/health 2>&1 | grep -q '\"status\":\"UP\"'; echo $?)" = 0 ]; then
      DISCOVERY_SERVICE_READY=true;
  fi
  sleep 2
done

# Start the other containers
docker-compose -f docker-compose_local.yml up -d

# Attach to the log output of the cluster
#docker-compose logs
