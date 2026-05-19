#!/bin/sh
# ----------------------------------------------------------------------------
# Maven Wrapper
# ----------------------------------------------------------------------------
# Minimal mvnw script to launch the maven wrapper jar
PRG="$0"
while [ -h "$PRG" ] ; do
  ls=`ls -ld "$PRG"`
  link=`expr "$ls" : '.*-> \(.*\)$'`
  if expr "$link" : '/.*' > /dev/null; then
    PRG="$link"
  else
    PRG=`dirname "$PRG"`/"$link"
  fi
done
PRGDIR=`dirname "$PRG"`
exec java -jar "$PRGDIR/.mvn/wrapper/maven-wrapper.jar" "$@"
