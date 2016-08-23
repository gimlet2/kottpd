#!/bin/bash
VERSION=$1
rm -rf upload/*
cp pom.xml upload/
gpg -ab -o upload/kottpd-${VERSION}.pom.asc upload/pom.xml

mvn package
cp target/kottpd-${VERSION}-jar-with-dependencies.jar upload/kottpd-${VERSION}.jar
gpg -ab -o upload/kottpd-${VERSION}.jar.asc upload/kottpd-${VERSION}.jar

mvn site:jar
cp target/kottpd-${VERSION}-site.jar upload/kottpd-${VERSION}-javadoc.jar
gpg -ab -o upload/kottpd-${VERSION}-javadoc.jar.asc upload/kottpd-${VERSION}-javadoc.jar

mvn source:jar
cp target/kottpd-${VERSION}-sources.jar upload/kottpd-${VERSION}-sources.jar
gpg -ab -o upload/kottpd-${VERSION}-sources.jar.asc upload/kottpd-${VERSION}-sources.jar
