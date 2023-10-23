#!/bin/sh
mvn clean package
java -cp target/classes:target/dependency/* webserver/WebServer 8080 &