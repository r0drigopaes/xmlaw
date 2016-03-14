all:
	javac examples/*.java jgame/*.java gamegen/*.java

docs:
	javadoc -d javadoc jgame examples/Std*.java

jars:
	jar cvfm jgame-all.jar manifest jgame/*.class \
	examples/*.class examples/*.tbl gfx/* sound/* \
	html/*-desc.txt html/*-thumb.*
	jar cvf jgame-base.jar jgame/*.class  examples/Std*.class
	jar cvfm jgame-gamegen.jar manifest_gamegen \
		jgame/*.class examples/Std*.class gamegen/*.class \
		gfx/blocks*.gif gamegen/simplegeneratedgame.tbl
