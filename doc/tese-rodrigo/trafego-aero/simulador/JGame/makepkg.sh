cd ..
tar zcvf jgame-$1.tar.gz JGame/README JGame/MANUAL JGame/LICENSE \
	JGame/CHANGES JGame/makepkg.sh JGame/Makefile \
	JGame/make.bat JGame/make-docs.bat JGame/make-jars.bat \
	JGame/manifest JGame/manifest_gamegen \
	JGame/jgame-base.jar \
	JGame/jgame/*.java JGame/jgame/*.class JGame/jgame/package.html \
	JGame/examples/*.java JGame/examples/*.class JGame/examples/*.tbl \
	JGame/examples/package.html \
	JGame/gamegen/*.java JGame/gamegen/*.class JGame/gamegen/*.tbl \
	JGame/gamegen/README JGame/gamegen/examples/*.appconfig \
	JGame/gamegen/website/*.html JGame/gamegen/website/*.php \
	JGame/gfx/ \
	JGame/sound/ \
	JGame/html/*.html JGame/html/*-desc.txt JGame/html/gen-jgame-html.pl \
	JGame/html/gen-html-*.sh \
	JGame/html/*.gif JGame/html/*.jpg \
	JGame/javadoc/


#tar cvf jgame-$1-src.tar JGame/README JGame/MANUAL JGame/LICENSE JGame/makepkg.sh \
#	JGame/jgame/*.java JGame/jgame/package.html \
#	JGame/examples/*.java JGame/examples/*.tbl \
#	JGame/gfx/ \
#	JGame/html/*.html


