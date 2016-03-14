#!/bin/sh
############################################################
# @file     jmake.sh
# @author   John Miller
# @title    Shell script to produce makefile for JSIM models
# @usage    jmake.sh <model>
# @example  jmake.sh Bank
#

echo MODEL = $1 > makefile
cat $JSIM_HOME/examples/Makefile >> makefile
make

