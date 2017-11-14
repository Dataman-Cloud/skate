#!/usr/bin/env bash
set -e

p1=$1

if [ ! -n "$p1" ] ;then
    echo "you have not input a parameter. eg. test local or web!"
    exit
fi

if [ $p1 = "test" ] ;then
	echo "stop test demo!"
	DOCKER_IP=192.168.31.46
	IMAGE_PREFIX="192.168.31.34/skate"
	SKATE_VERSION="latest"
	WEB_IP='192.168.31.46'
elif [ $p1 = "local" ] ;then
	echo "stop local develop demo!"
	DOCKER_IP=192.168.31.46
	IMAGE_PREFIX="skate"
	SKATE_VERSION="latest"
	WEB_IP='192.168.31.46'
elif [ $p1 = "web" ] ;then
	echo "stop public web demo!"
	DOCKER_IP=10.3.8.23
	IMAGE_PREFIX="demoregistry.dataman-inc.com/skate"
	SKATE_VERSION=":latest"
	WEB_IP='106.75.90.26'
else
    echo "you have not input a parameter. eg. test local or web!"
    exit
fi

export SKATE_VERSION DOCKER_IP WEB_IP IMAGE_PREFIX

#env IMAGE_PREFIX='192.168.31.34/skate' SKATE_VERSION='latest' WEB_IP='192.168.31.46' docker-compose -f docker-compose_test.yml down -v
docker-compose -f docker-compose.yml down -v
