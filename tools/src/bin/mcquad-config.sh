#!/bin/bash
# ========================================
# === McQuad script configuration file ===
# ========================================


# MCSAVES should be the directory path to the Minecraft game saves directory
export MCSAVES=/Users/simonh/Library/Application\ Support/minecraft/saves

# MCMAPS should be the directory path to the map data document root
export MCMAPS=/Users/simonh/dev/mcquad/McQuad/out/www

# Note: it is assumed that webserver will symbolic link to here, e.g.:
#    export TOMCAT=/Users/simonh/Applications/apache-tomcat-8.0.15
#    export TC_ROOT=$TOMCAT/webapps/mcmaps
#    ln -s $TC_ROOT $MCMAPS

# MCMAPS_CONTEXT should be the configured webserver context
export MCMAPS_CONTEXT=/mcmaps

##
## It is assumed that Tomcat (or whatever webserver you use) has been
## configured with the given context for the given document root
##
## The above example would expect the URL
##
##     "http://localhost:8080/mcmaps"
##
## to load docs from the document root
##
##     "/Users/simonh/Applications/apache-tomcat-8.0.15/webapps/mcmaps
##
## which is symbolically linked to
##
##     "/Users/simonh/dev/mcquad/McQuad/out/www"
##

# MCQUAD_VERSION should be set to correct version to pull out of maven repo
export MCQUAD_VERSION=1.2.2

