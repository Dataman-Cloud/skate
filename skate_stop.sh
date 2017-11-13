#!/usr/bin/env bash
set -e

p1=$1

if [ ! -n "$p1" ] ;then
    echo "you have not input a parameter eg. test local or web!"
    exit
fi

if [ p1="test" ] ;then
	IMAGE_PREFIX="192.168.31.34/skate"
	SKATE_VERSION="latest"
	WEB_IP='192.168.31.46'
elif [ p1="local" ] ;then
	IMAGE_PREFIX="skate"
	SKATE_VERSION="latest"
	WEB_IP='192.168.31.46'
elif [ p1="web" ] ;then
	IMAGE_PREFIX="demoregistry.dataman-inc.com/skate"
	SKATE_VERSION="latest"
	WEB_IP='192.168.31.46'
else
    echo "you have not input a parameter in  'test'\'local'\'web'"
    exit
fi

export IMAGE_PREFIX SKATE_VERSION WEB_IP

#env IMAGE_PREFIX='192.168.31.34/skate' SKATE_VERSION='latest' WEB_IP='192.168.31.46' docker-compose -f docker-compose_test.yml down -v
docker-compose -f docker-compose_test.yml down -v
