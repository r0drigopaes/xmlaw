#!/bin/sh
############################################################
# @file     jbuild.sh
# @author   Greg Silver
# @title    Shell script to build JSIM models
# @usage    jbuild.sh <model>
# @example  jmake.sh test
#

cp $JSIM_HOME/ModelBuild.xml .
ant -buildfile ModelBuild.xml compile
