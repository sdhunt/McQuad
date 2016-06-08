#!/bin/bash
#

BACK=$PWD
TARBALL=static.tar.gz

cd ../../../web/target/mcquad-web-1.1.0-SNAPSHOT/
echo BUILD TAR
tar cvzf ../../$TARBALL --exclude META-INF --exclude WEB-INF .
cd ../../../out/www
echo EXTRACT TAR
tar xvzf ../../web/$TARBALL
ls -l .

cd $BACK
