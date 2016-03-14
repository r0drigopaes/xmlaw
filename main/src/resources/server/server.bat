@ECHO OFF
IF (%1)==() GOTO NOARG

:ARG
java -Djava.ext.dirs=./lib -Dlaw.conf.dir=%1 %1 br.pucrio.inf.les.law.RunXMLawServer
GOTO END

:NOARG
java -Djava.ext.dirs=./lib -cp ./conf br.pucrio.inf.les.law.RunXMLawServer
GOTO END

:END
ECHO server stoped