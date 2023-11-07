#!/bin/sh
java -cp target/classes:target/dependency/* webserver/WebServer ${1:-8080}
