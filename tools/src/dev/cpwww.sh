#!/bin/bash
#

BACK=$PWD
TARBALL=static.tar.gz
VERSION=1.2.1

cd ../../../web/target/mcquad-web-${VERSION}/
echo BUILD TAR
tar cvzf ../../$TARBALL --exclude META-INF --exclude WEB-INF .
cd ../../../out/www
echo EXTRACT TAR
tar xvzf ../../web/$TARBALL
ls -l .

cd $BACK
