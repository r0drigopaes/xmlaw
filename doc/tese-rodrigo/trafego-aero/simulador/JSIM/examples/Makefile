
#############################################################################
# @file   Makefile
# @author John Miller, Junxiu Tao
# @date   Fri Dec  8 14:36:48 EST 2000
# @title  Makefile for jsim example simulation models
#############################################################################

.IGNORE:

#############################################################################
# Definitions
# MODEL may need to be updated from default (e.g., to Bank)
#       set automatically if run by jmake.sh

#MODEL     = JModel

COMP      = javac
COMPFLAGS = -g

DOC       = javadoc
DOCFLAGS  = -d

CD        = cd
COUNT     = wc
COPY      = cp
ECHO      = echo
JAR       = jar
REMOVE    = /bin/rm

#############################################################################
# Targets

#all: applet
all: compile archive install


### Compile source (.java) files into bytecode (.class) files

compile:
	$(COMP) $(COMPFLAGS) *.java


### Archive bytecode (.class) files into archive (.jar) files

archive:
	$(ECHO) Class-Path: $(JSIM_HOME)/jsim/jars/sys/jsim.jar $(JSIM_HOME)/jsim/jars/jrun.jar > manifest.tmp
	$(ECHO) Main-Class: $(MODEL) >> manifest.tmp
	$(JAR) cmf manifest.tmp $(MODEL).jar *.class
	$(REMOVE) manifest.tmp

### Install the archived model federate (<model>Fed.jar) in the jars directory
#
install:
	$(COPY) $(MODEL).jar $(JSIM_HOME)/jsim/jars

### Run the model as a standalone applet

applet:
	appletviewer -J-Djava.security.policy=$(JSIM_HOME)/examples/jsim.policy $(MODEL).html

### Run the model federate as an application. Automatic code generation of federates is a
### pending feature. At this point, federates can be created with some hand coding.

#federate:
#	java -Djsim.use_xml=true -Djsim.generate_xml_files=true -jar $(MODEL)Fed.jar

### Run the model federate as an applet (a pending feature)

#fedApplet:
#	appletviewer -J-Djava.security.policy=$(JSIM_HOME)/examples/jsim.policy $(MODEL)Fed.html

### Count the lines of code

count:
	$(COUNT) *.java 

### Clean away the bytecode (.class) files

clean:
	$(REMOVE) *.class *.jar
