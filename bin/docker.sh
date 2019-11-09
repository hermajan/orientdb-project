#! /bin/bash

COMMAND=$1

function help() {
    echo -e "commands:
    \t bash - runs bash in container
    \t clean - removes unused containers
    \t console - runs OrientDB console in container
    \t down - downs container (stops and removes it)
    \t list - lists containers
    \t logs - lists logs
    \t restart - downs and starts container
    \t start - builds and startups container
    \t stop - stops container"
}

function bash() {
    docker exec -it orientdb bash
}

function clean() {
    docker system prune -a
}

function console() {
    docker exec -it orientdb "bin/console.sh"
}

function down() {
    docker-compose down
}

function list() {
    docker-compose ps
}

function logs() {
    docker-compose logs
}

function restart() {
    stop
    start
}

function start() {
    docker-compose up -d --build
}

function stop() {
    docker-compose stop
}

if [ "${COMMAND}" == "" ]; then
    help
fi

# run command
$COMMAND
