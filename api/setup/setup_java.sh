#!/bin/sh

set -e

export JAVA_HOME=/usr/lib/jvm/java-21-openjdk
export PATH=$JAVA_HOME/bin:$PATH

which java
java --version

echo "Java installed sucessfully"

sleep infinity
