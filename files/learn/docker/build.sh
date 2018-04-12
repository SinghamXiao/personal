#!/usr/bin/env bash

ask_yes_or_no() {
    read -p "$1 ([Y / y] yes or [N / n] no): "
    case $(echo $REPLY | tr '[A-Z]' '[a-z]') in
        Y|y|yes) echo "yes" ;;
        *)  echo "no" ;;
    esac
}

clean() {
    docker rm `docker ps -a | grep Exited | awk '{print $1}'`

    docker rmi docker/test:latest
}

go clean & go build -o dockerTest DockerTest.go

docker build -t docker/test:latest .

docker images | grep docker/test

if [ "yes" != $(ask_yes_or_no "Do you want to continue? ") ]; then
    exit
fi

docker run --name docker_test docker/test:latest

if [ "yes" != $(ask_yes_or_no "Do you want to continue? ") ]; then
    exit
fi

clean