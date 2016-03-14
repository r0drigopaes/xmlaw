
#############################################################################
# @file   Makefile
# @author John Miller
# @date   Mon Nov 29 11:27:03 EST 1999
# @title  Makefile to run JSIM Bean Box
#############################################################################

.IGNORE:

#############################################################################
# General Definitions

COMP      = javac
COMPFLAGS = -g

EXE      = java
EXEFLAGS = -Djava.compiler=NONE

DOC       = javadoc
#DOCFLAGS  = -private -d
DOCFLAGS  = -d

JAR       = jar

CD        = cd
COPY      = cp
ECHO      = echo
COUNT     = wc
REMOVE    = /bin/rm

E1        = *.java
E2        = *.class


#############################################################################
# Targets

### Run the JSIM Bean Box (line1 => fast mode, line2 => debug mode)

run:
	$(EXE) jsim.beanbox.BeanBoxFrame
#	$(EXE) $(EXEFLAGS) jsim.beanbox.BeanBoxFrame


