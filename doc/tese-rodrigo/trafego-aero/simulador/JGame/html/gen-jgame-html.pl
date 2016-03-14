#!/usr/bin/perl -w

if ($#ARGV < 2) {
	print "gen-jgame-html - generate HTML for JGame applet\n";
	print "\n";
	print "usage:\n";
	print "\n";
	print "gen-jgame-html.pl [java class name] [codebase or archive field string]\n";
	print "                  [ list of resolutions ]\n";
	print "\n";
	print "Resolutions are specified as [xsize]x[ysize].\n";
	print "\n";
	print "The generator looks for a desc file named [gamename]-desc.txt.\n";
	print "\n";
	print "It creates files named applet-[classname]-[resolution].html\n";
	exit(0);
}
$classname   = shift @ARGV;
$customfield = shift @ARGV;
@resolutions = @ARGV;
push @resolutions, "fullscreen";

$classname =~ /([^.]*)$/;
$gamename = $1;
$gamename_lc = lc $gamename;

$descfile = "$gamename_lc-desc.txt";
open INPUT,"<$descfile";
read(INPUT, $text, 1024000);


foreach $res (@resolutions) {
	if ($res =~ /([0-9]*)x([0-9]*)/) {
		$resx = $1;
		$resy = $2;
	} else { #fullscreen
		$resx = "100%";
		$resy = "100%";
	}
	open OUT, ">applet-$gamename_lc-$res.html";
	print OUT "<html><head><title>$gamename applet</title></head>\n";
	print OUT "<body bgcolor=#444488 text=#9999dd link=#eeccff vlink=#cc88ff alink=#ffaaff>\n";
	print OUT "<center>\n";
	print OUT "<applet code=\"$classname\" width=\"$resx\" height=\"$resy\"";
	print OUT " $customfield >\n";
	print OUT "$gamename applet\n";
	print OUT "</applet>\n";
	print OUT "<p>\n";

	print OUT  $text;

	print OUT "</center>\n";
	print OUT "</body></html>\n";
	close OUT;
}

