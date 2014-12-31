#!/bin/bash
#
## source in the configuration...
. ./mcquad-config.sh

MSG_NAD="Not a Directory"
MSG_NMC="Does not appear to be a Minecraft saves directory"
MSG_CSL="Context does not begin with a slash (/)"

## Sanity checking on the source directory (Minecraft Saves)

if [ ! -d "$MCSAVES" ]
then
    echo "MCSAVES: [$MCSAVES] -- $MSG_NAD"
    exit 1
fi

MCOPTS="$MCSAVES/../options.txt"
if [ ! -r "$MCOPTS" ]
then
    echo "MCSAVES: [$MCSAVES] -- $MSG_NMC"
    exit 1
fi

grep -q "^renderClouds:" "$MCOPTS"
if [ "$?" -eq "1" ]
then
    echo "MCSAVES: [$MCSAVES] -- $MSG_NMC"
    exit 1
fi


## Sanity checking on the target directory (web server document root)

if [ ! -d "$MCMAPS" ]
then
    echo "MCMAPS: [$MCMAPS] -- $MSG_NAD"
    exit 1
fi

if [ -z "$MCMAPS_CONTEXT" ]
then
    echo "MCMAPS_CONTEXT: not set"
    exit 1
fi

echo "$MCMAPS_CONTEXT" | grep -q "^\/"
if [ "$?" -eq "1" ]
then
    echo "MCMAPS_CONTEXT: [$MCMAPS_CONTEXT] -- $MSG_CSL"
    exit 1
fi


### ...................................................................

WORLD="$1"
if [ -z "$WORLD" ]
then
    echo "Usage: $0 <world-dir-name>"
    exit 1
fi

WORLD_SAVE="$MCSAVES/$WORLD"
if [ ! -d "$WORLD_SAVE" ]
then
    echo "Cannot find world save: $WORLD_SAVE"
    exit 1
fi

echo "Found save dir: $WORLD_SAVE"

REGION="$WORLD_SAVE/region"
echo "Region directory contains..."
ls "$REGION"

TARGET="$MCMAPS/$WORLD"
if [ ! -d "$TARGET" ]
then
    echo "Creating target directory: $TARGET ..."
    mkdir "$TARGET"
fi

TILES_TARGET="$MCMAPS/$WORLD/tiles"

### ...................................................................
## locate the mcquad jar file

REPO="$HOME/.m2/repository/com/meowster"
MQ_JAR="$REPO/mcquad-core/$MCQUAD_VERSION/mcquad-core-$MCQUAD_VERSION.jar"

#ls -l "$MQ_JAR"

if [ ! -r $MQ_JAR ]
then
    echo "Unable to locate jar file: $MQ_JAR"
    exit 1
fi

if [ -z "$JAVA_HOME" ]
then
    echo "JAVA_HOME is not set"
    exit 1
fi

JAVA="$JAVA_HOME/bin/java"

$JAVA -jar "$MQ_JAR" "$REGION" -o "$TILES_TARGET"

echo DONE
