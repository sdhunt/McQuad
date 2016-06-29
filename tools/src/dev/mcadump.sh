#!/bin/bash

MCQUAD_VERSION=1.2.2

MAIN=com.meowster.mcquad.McaDump

CP="/Users/simonh/dev/mcquad/McQuad/core/target/classes"

MCSAVES="/Users/simonh/dev/mcquad/tmp"

ls "$MCSAVES"

JAVA=/usr/bin/java


for FILE in $(ls "$MCSAVES");
do
    $JAVA -cp $CP $MAIN "$MCSAVES/$FILE"
done


