package examples;
import jgame.*;
import java.awt.*;

/** A "lunar lander" on steroids, inspired by a little known Commodore 64
 * shooter, the name of which I forgot.  I spent a lot of time on getting
 * nicely generated levels in this game.  Almost half the code is level
 * generation. */
public class CavernsOfFire extends StdGame {

	/* init */

	public static void main(String[]args){
		new CavernsOfFire(parseSizeArgs(args));
	}
	public CavernsOfFire() { initEngineApplet(40,30,16,16,null,null,null); }
	public CavernsOfFire(Dimension size) {
		initEngine(40,30,16,16,null,null,null,size.width,size.height);
	}
	public void initGame() {
		defineMedia("examples/caverns_of_fire.tbl");
		setFrameRate(40,1);
		startgame_ticks=150;
		lifelost_ingame=true;
		setTileSettings("",1,0);
	}

	/* Globals */

	/* Cids:  1=player 2=bullet 4=enemy 8=enemy bullet 16=fireball.
	** (we should make constants for these) */

	Player player=null;
	int lavalevel=0; /*in-game, it contains tile position at which lava begins*/
	int nr_pads=0;
	// used for ensuring that one monster type isn't dominant in two
	// successive levels
	int non_preferred_monster=0;
	// indicates whether level is a "lava level" or not
	int lavaarea=0;

	/** A single solid block of code that does all the level generation. */
	public void defineLevel() {
		setBGImage("cave_bg"+(stage%4));
		/* Cave parameters. Some considerations:
		**
		** For amplitudes 9-14,lavalevel=3..4 = moderate, lavalevel=7 = hard
		** For amplitudes <9, lavalevel 3..4 = hard
		** for amplitudes >14, lavalevel 7 = moderate...hard, more is
		**     probably too tight.
		** 
		**/
		// first, determine amplitude and lavalevel
		// amplitude = 7 ... 18
		double amplitude = random(7, level < 8 ? 8+(5*level)/4 : 18, 1);
		// lavalevel = 1 ... +/- 8
		lavalevel = random(0,
			(int)Math.min(amplitude-2, level < 7 ? 1+level : 8), 1);
		/* Draw the cavern */
		fillBG(".");
		for (int i=0; i<500; i++)
			setTile(random(0,pfTilesX(),1),random(0,pfTilesY(),1),",");
		removeObjects(null,0);
		player=new Player(16,pfHeight()/2,5);
		// draw cave
		// minimum distance between floor and ceiling
		int cave_tightness;
		// overlap between most overhanging ceiling and most up reaching floor
		// positive means overlap, negative means no overlap
		int cave_overlap=0;
		trycave: do {
			cave_tightness=30;
			int cave_highfloor=pfTilesY();
			int cave_lowceil = 0;
			double topph = random(0,6);
			double top1 = random(1.5,4.0);
			double top1a = (amplitude/10)*random(2.5,3.5);
			double top2 = random(0.5,1.5);
			double top2a = (amplitude/10)*random(0.5,2.0);
			double botph = random(0,6);
			double bot1 = random(1.5,4.0);
			double bot1a = (amplitude/10)*random(2.5,3.5);
			double bot2 = random(0.5,1.5);
			double bot2a = (amplitude/10)*random(0.5,2.0);
			// try if these parameters yield a legal cave
			for (int x=0; x<pfTilesX(); x++) {
				int upper =(int)( amplitude/2 + top1a*Math.sin(topph+x/top1)
							+ top2a*Math.sin(topph+x/top2) );
				int lowerl2=(int)(amplitude/2 + bot1a*Math.sin(botph+(x-2)/bot1)
							+ bot2a*Math.sin(botph+(x-2)/bot2) );
				int lowerl =(int)(amplitude/2 + bot1a*Math.sin(botph+(x-1)/bot1)
							+ bot2a*Math.sin(botph+(x-1)/bot2) );
				int lower  =(int)( amplitude/2 + bot1a*Math.sin(botph+x/bot1)
							+ bot2a*Math.sin(botph+x/bot2) );
				int lowerr =(int)(amplitude/2 + bot1a*Math.sin(botph+(x+1)/bot1)
							+ bot2a*Math.sin(botph+(x+1)/bot2) );
				int lowerr2=(int)(amplitude/2 + bot1a*Math.sin(botph+(x+2)/bot1)
							+ bot2a*Math.sin(botph+(x+2)/bot2) );
				double segment_tightness =
					Math.min( (pfTilesY()-upper) - lowerl2,
					Math.min( (pfTilesY()-upper) - lowerl,
					Math.min( (pfTilesY()-upper) - lower,
					Math.min( (pfTilesY()-upper) - lowerr,
							  (pfTilesY()-upper) - lowerr2 ) ) ) );
				if (segment_tightness < 4) continue trycave;
				if (x > 4)
					cave_tightness = (int)Math.min(
							cave_tightness,segment_tightness );
				cave_highfloor = (int)Math.min(cave_highfloor,pfTilesY()-lower);
				cave_lowceil   = (int)Math.max(cave_lowceil,upper);
				cave_overlap   = cave_lowceil - cave_highfloor;
			}
			for (int x=0; x<pfTilesX(); x++) {
				for (int y=0; y<amplitude/2 + top1a*Math.sin(topph+x/top1)
								  + top2a*Math.sin(topph+x/top2); y++) {
					setTile(x,y,"#");
				}
				for (int y=0; y<amplitude/2 + bot1a*Math.sin(botph+x/bot1)
								  + bot2a*Math.sin(botph+x/bot2); y++) {
					setTile(x,pfTilesY()-y,"#");
				}
			}
			break;
		} while (true);
		// dig out the player's start position
		setTiles(0,pfTilesY()/2-1,new String[] {"...",".,.","..,",",.."});
		lavalevel = pfTilesY() - lavalevel;
		// draw lava
		for (int y=lavalevel; y<pfTilesY(); y++) {
			for (int x=0; x<pfTilesX(); x++) {
				if (!and(getTileCid(x,y),1))
					setTile(x,y, random(0,1) > 0.5 ? "<" : ">");
			}
		}
		Point loc;
		/* do landing pads.  If we fail to draw enough landing pads,
		** we reduce lava level and try to add the rest of the pads */
		nr_pads=0;
		while (true) {
			int goodyofs=1; // y offset where i can put landing pad, 1=none
			// draw landing pads
			int triesleft=102;
			for (int i=0; i<4; i++) {
				try_location:while (true) {
					// try placing pads on the right side first
					// and try lower locations first
					int check_topmost = -11 + (triesleft/18);
					if (triesleft > 100) {
						loc = new Point(random(pfTilesX()-9,pfTilesX()-2,1),
							pfTilesY()-1);
						check_topmost = -12;
					} else {
						loc = new Point(random(4,pfTilesX()-2,1),pfTilesY()-1);
					}
					if ( triesleft-- <= 0) break;
					goodyofs=1;
					// search the column given by loc, try to find lower lying
					// locations first.
					// We must also check the column above topmost
					// in case we placed a platform there
					for (int y=0; y>-12; y--) {
						// fail when we find lava or a pad
						if (and(getTileCid(loc,0,y),6)
						||  and(getTileCid(loc,1,y),6)) continue try_location;
						// search for the first location with a regular wall
						if(!and(getTileCid(loc,0,y)|getTileCid(loc,1,y),1)){
							if (goodyofs==1 && y >= check_topmost) goodyofs=y;
						}
					}
					if (goodyofs == 1) continue try_location;
					break;
				}
				if (goodyofs < 1) {
					// ensure we're 3 tiles above lava
					if (loc.y+goodyofs == lavalevel-1) goodyofs--;
					if (loc.y+goodyofs == lavalevel-2) goodyofs--;
					setTiles(loc.x,loc.y+goodyofs,new String[]{"[]","##","##"});
					nr_pads++;
				}
			}
			if (nr_pads >= 2) break;
			// reduce lava level
			for (int x=0; x<pfTilesX(); x++)
				if (and(getTileCid(x,lavalevel),2))
					setTile(x,lavalevel,".");
			lavalevel++;
		}
		// finish tile seams
		for (int y=0; y<pfTilesY(); y++) {
			for (int x=0; x<pfTilesX(); x++) {
				if ( (getTileCid(x,y)&7) == 1) {
					int context =
						(getTileCid(x,y-1)&1)  |  ((getTileCid(x,y+1)&1)*2)|
						((getTileCid(x-1,y)&1)*4)|((getTileCid(x+1,y)&1)*8);
					setTile(x,y,"#"+(15-context));
				} else if (and(getTileCid(x,y),2)) {
					int context =
						 (getTileCid(x,y-1)==1 ? 1 : 0 )
						|(getTileCid(x,y+1)==1 ? 2 : 0 )
						|(getTileCid(x-1,y)==1 ? 4 : 0 )
						|(getTileCid(x+1,y)==1 ? 8 : 0 );
					if (context != 0)
						setTile(x,y,"<"+context);
				}
			}
		}
		/* place objects. Some considerations:
		**
		** Lava and tanks are worse in "open" levels.  Coils, bats, jellies are
		** worse in "closed" levels.
		*/
		double [] [] monster_difficulty = {
		/*       coils jellies bats tanks */
		/*open*/  {0.4,  0.5,  0.7,  1.2},
			      {0.5,  0.6,  0.8,  0.9},
			      {0.5,  0.6,  0.8,  0.8},
		/*closed*/{0.6,  0.7,  1.0,  0.7}  };
		// determine lava area
		lavaarea=0;
		for (int x=0; x<pfTilesX(); x++) {
			if (and(getTileCid(x,lavalevel),2)) lavaarea++;
		}
		int cave_tight_level=0;
		if (cave_tightness <=13) cave_tight_level=1;
		if (cave_tightness <=10) cave_tight_level=2;
		if (cave_tightness <= 8) cave_tight_level=3;
		if (cave_tightness <= 6) cave_tight_level=4;
		if (cave_tightness <= 5) cave_tight_level=5;
		if (cave_overlap > -6) cave_tight_level++;
		if (cave_overlap > -1) cave_tight_level++;
		int difficulty = lavaarea/4 + cave_tight_level;
		// count out some rare exaggerated values (up to 12 has been found)
		if (difficulty > 10) difficulty=10;
		//dbgPrint("cave_tightness="+cave_tightness+"  lavaarea="+lavaarea
		//	+"  cave_overlap="+cave_overlap+" --> difficulty = "+difficulty);
		// determine # of monsters
		int nr_coils=0,nr_jellies=0,nr_bats=0,nr_tanks=0;
		// keep adding monsters until difficulty points meet difficulty level
		int difficulty_left = 5 + level - difficulty;
		boolean has_placed_monsters=false; // indicates first time addition
		while (true) {
			// special case for level 0...3
			int monstertype = level;
			if (level >= 0) {
				// choose random monster, disprefer dominant monster of the
				// previous level
				do monstertype = random(0,3,1);
				while (monstertype == non_preferred_monster);
			}
			double dif = monster_difficulty[cave_tight_level/2][monstertype];
			int nrmonsters = (int)(difficulty_left/dif);
			if (nrmonsters <= 0) break;
			if (nrmonsters > 10) {
				nrmonsters=10;
				non_preferred_monster=monstertype;
			}
			if (!has_placed_monsters) non_preferred_monster=monstertype;
			difficulty_left -= dif*nrmonsters;
			if (monstertype==0) nr_coils += nrmonsters;
			if (monstertype==1) nr_jellies += nrmonsters;
			if (monstertype==2) nr_bats += nrmonsters;
			if (monstertype==3) nr_tanks += nrmonsters;
			has_placed_monsters=true;
		}
		// place monsters
		for (int i=0; i<nr_coils; i++) {
			do { loc=new Point(random(5,pfTilesX()-1,1),random(0,pfTilesY(),1));
			} while (and(getTileCid(loc,0,0),1));
			new Coil(loc.x*pfTileWidth(),loc.y*pfTileHeight(),
				0,random(-1,1,2),4);
		}
		// ensure a little room around the jellies
		for (int i=0; i<nr_jellies; i++) {
			do { loc=new Point(random(9,pfTilesX()-1,1),random(0,pfTilesY(),1));
			} while ( and(getTileCid(loc, 0, 0),1)
			||        and(getTileCid(loc, 1, 0),1)
			||        and(getTileCid(loc,-1, 0),1)
			||        and(getTileCid(loc, 0, 1),1)
			||        and(getTileCid(loc, 0,-1),1) );
			new Jelly(loc.x*pfTileWidth(),loc.y*pfTileHeight(),
				random(-1,1,2),2.5);
		}
		for (int i=0; i<nr_bats; i++) {
			loc = new Point(random(9,pfTilesX()-1,1),0);
			while (and(getTileCid(loc,0,0),1)) loc.y++;
			new Bat(loc.x*pfTileWidth(),loc.y*pfTileHeight(),1, 4);
		}
		for (int i=0; i<nr_tanks/2; i++) {
			loc = new Point(random(5,pfTilesX()-1,1),pfTilesY()-1);
			while (and(getTileCid(loc,0,0),1)) loc.y--;
			new Tank(loc.x*pfTileWidth(),loc.y*pfTileHeight(),1, 0.6);
		}
		for (int i=0; i<nr_tanks/2; i++) {
			loc = new Point(random(5,pfTilesX()-1,1),0);
			while (and(getTileCid(loc,0,0),1)) loc.y++;
			new Tank(loc.x*pfTileWidth(),loc.y*pfTileHeight(),-1, 0.6);
		}
	}

	/* Game state handling */

	public void initNewGame() {
		level = 0;
		stage = 0;
		score = 0;
		lives = 5; 
	}
	public void incrementLevel() {
		if (level<15) level++;
		stage++;
	}
	public void initNewLife() {
		player=new Player(16,pfHeight()/2,5);
	}
	//public void startStartLevel() { lives++; }
	public void doFrameStartLevel() {
		// call move objects once to define sprites
		if (seqtimer==1) moveObjects();
	}
	public void startGameOver() { removeObjects(null,0); }
	public void doFrameInGame() {
		moveObjects();
		checkCollision(4|8|16,1); // enemies hit player
		checkCollision(4|16,2); // enemies, fireballs hit bullet
		checkBGCollision(1,1|2|4|8|16); // walls hit every object type
		// find lava (2 adjacent lava tiles)
		Point loc = new Point(random(0,pfTilesX()-1,1),lavalevel);
		if (and(getTileCid(loc,0,0),2)&&and(getTileCid(loc,1,0),2)) {
			double ballx = loc.x*pfTileWidth() + random(0,15);
			double bally = loc.y*pfTileHeight();
			if (checkTime(15)) {
				new Fireball(ballx, bally,
					random(-1,1), random(-3.5,-1.5), false, -2);
			} else {
				new Fireball(ballx,bally, 0, -0.5,
					random(0,1) > 0.5 ? false : true, random(3,11,1));
			}
		}
		if ( nr_pads==0
		||   (countObjects(null,4)==0 && lavaarea<=12)
		/*||   getKey('L')*/) levelDone();
	}
	public void doFrameLevelDone() { moveObjects("zexplo",0); }
	public void doFrameStartGame() { moveObjects("zexplo",0); }
	public void startLifeLost() { player.remove(); }
	public void doFrameLifeLost() {
		if ( (seqtimer%5) == 1)
			new Explo(player.x+random(-10,10),player.y+random(-10,10));
	}
	Font scoring_font = new Font("Arial",0,8);

	/* game objects */

	public class Player extends JGObject {
		int invulnerability=80;
		int landed=0;
		int gassing=0;
		public Player(double x,double y,double speed) {
			super("player",false,x,y,1,"ship");
			//setTileBBox(0,0,16,16);
		}
		public void move() {
			// determine if we're landed
			Point tl = getTopLeftTile();
			if (xspeed==0&&yspeed<1 && tl!=null&&and(getTileCid(tl,0,1),4)) {
				if ( (landed++) > 50) {
					if (getTileStr(tl.x,tl.y+1).equals("[")) {
						setTiles(tl.x,tl.y+1,new String[] {"()"});
						nr_pads--;
						score += 25;
					}
				}
			} else {
				landed=0;
				yspeed += 0.01;
				if (getKey(key_down)) yspeed += 0.3;
				if (getKey(key_left))  xspeed -= 0.3;
				if (getKey(key_right)) xspeed += 0.3;
				if (getKey(key_fire) && countObjects("bullet",0) < 7) {
					clearKey(key_fire);
					new Bullet(x,y,(int)(xspeed*2.5),(int)(yspeed*2.5-1));
				}
				if (x+xspeed < 0) xspeed = -xspeed;
				if (x+xspeed >= pfWidth()-16) xspeed = -xspeed;
			}
			if (getKey(key_up)) {
				yspeed -= 0.3;
				if (gassing<4) gassing++;
			} else {
				if (gassing>0) gassing--;
			}
			if (invulnerability>=0) {
				setGraphic((invulnerability%4)<2 ? "ship" : null);
				invulnerability--;
			}
		}
		public void hit(JGObject obj) { if (invulnerability<0) lifeLost(); }
		public void hit_bg(int tilecid, int tx,int ty,int twidth,int theight) {
			if (!and(tilecid,4)) {
				lifeLost(); 
			} else {
				// center nicely
				Point padpos=new Point(tx,ty);
				for (int dy=0; dy<theight; dy++) {
					for (int dx=0; dx<twidth; dx++) {
						String tile = getTileStr(tx+dx,ty+dy);
						if (tile.equals("[") || tile.equals("(")) {
							padpos.x=tx+dx;
							padpos.y=ty+dy;
						}
						if (tile.equals("]") || tile.equals(")")) {
							padpos.x=tx+dx-1;
							padpos.y=ty+dy;
						}
					}
				}
				double newx = padpos.x*pfTileWidth() + 8;
				double newy = padpos.y*pfTileHeight() - 16;
				if (Math.abs(newx - x) + Math.abs(newy - y) > 16) {
					// bad landing -> die anyway
					lifeLost();
				} else {
					setPos(newx,newy);
					setSpeed(0,0);
				}
			}
		}
		public void paint() {
			setColor(cycleColor(new Color[]{Color.red,Color.yellow},timer,0.5));
			if (gassing>1)
				drawOval((int)x+8, (int)y+16+gassing,
					3*gassing/2, gassing*3, true, true);
		}
	}
	public class Bullet extends JGObject {
		public Bullet(double x,double y,double xspeed,double yspeed) {
			super("bullet",true,x,y,2,"bullet",-2);
			setSpeed(xspeed,yspeed);
		}
		public void move() {
			yspeed += 0.1;
		}
		public void hit(JGObject obj) {
			remove();
			if (!and(obj.colid,16)) {
				obj.remove();
				if (obj instanceof Bat) {
					score += 10;
				} else if (obj instanceof Tank) {
					score += 20;
				} else {
					score += 5;
				}
				new Explo(x+8,y+8);
			}
		}
		public void hit_bg(int tilecid) {
			remove();
		}
	}
	public class Coil extends JGObject {
		double speed;
		public Coil(double x,double y,int xdir,int ydir,double speed) {
			super("coil",true,x,y,4,"coil");
			setDirSpeed(xdir,ydir,0.0,speed/2.0);
			this.speed=speed;
		}
		public void move() {
			Point cen = getCenterTile();
			if (and(getTileCid(cen,0,ydir*5),1)) {
				ydir = -ydir;
				yspeed = -yspeed;
			}
			if (yspeed < speed) yspeed += 0.2;
			setAnimSpeed(ydir*yspeed/5.0);
		}
	}
	public class Jelly extends JGObject {
		double speed;
		public Jelly(double x,double y,int xdir,double speed) {
			super("jelly",true,x,y,4,null);
			setDirSpeed(xdir,0,speed,speed/10.0);
			this.speed=speed;
		}
		public void move() {
			if ( Math.abs(x - player.x) < 64) {
				ydir = (y > player.y) ? -1 : 1;
			} else {
				ydir=0;
			}
			setGraphic("jelly"+ (xdir < 0 ? "l" : "r"));
		}
		public void hit_bg(int tilecid) { 
			xdir = -xdir;
			ydir = -ydir;
		}
	}
	public class Bat extends JGObject {
		double speed;
		int orient;
		int delay=0;
		public Bat(double x,double y,int orient,double speed) {
			super("nat",true,x,y,4,"bat_d");
			this.speed=speed;
			this.orient=orient;
			stopAnim();
		}
		public void move() {
			if (player!=null) {
				if (Math.abs(player.x-x) < 100 && xspeed==0 && yspeed==0
				&& delay==0) { // get ready to jump
					delay = random(5,25,1);
				}
				if (delay>0) {
					if ((--delay)==0) { // jump
						// check if our path is free
						Point cen = getCenterTile();
						if ( (  (y > player.y && orient==-1)
						      ||(y < player.y && orient==1) )
						&&   (  (x < player.x && !and(getTileCid(cen,1,0),1))
						      ||(x >= player.x&&!and(getTileCid(cen,-1,0),1)))
						) {
							double angle = Math.atan2(player.x-x,player.y-y);
							if (y > player.y) {
								xspeed = 2.0*Math.sin(angle);
								yspeed = 7.0*Math.cos(angle);
							} else {
								xspeed = 2.0*Math.sin(angle);
								yspeed = 0.5*Math.cos(angle);
							}
							startAnim();
						} else if (and(getTileCid(cen,1,0),1)
						&&         and(getTileCid(cen,-1,0),1)) {
							// we're closed in on both sides -> jump straight
							if (y > player.y) {
								yspeed = -7.0;
							} else {
								yspeed = 0.5;
							}
							startAnim();
						}
					}
				}
			}
			if (xspeed!=0 || yspeed!=0) yspeed+=0.07;
		}
		public void hit_bg(int tilecid) {
			xspeed=0;
			yspeed=0;
			snapToGrid(16,16);
			Point cen = getCenterTile();
			// find whether ceiling or floor is closer
			orient = -1;
			for (int yofs=0; yofs<20; yofs++) {
				if (and(getTileCid(cen,0,yofs),1)) {
					break;
				}
				if (and(getTileCid(cen,0,-yofs),1)) {
					orient=1;
					break;
				}
			}
			setGraphic("bat_"+ (orient>0 ? "d" : "u") );
			stopAnim();
			// search the floor/ceiling for the closest surface
			int foundx=0,foundy=0;
			for (int yofs=20; yofs>=0; yofs--) {
				for (int xofs=-1; xofs<=1; xofs+=2) {
					if ( and(getTileCid(cen,xofs,-orient*(yofs-1)),1)
					&&  !and(getTileCid(cen,xofs,-orient*(yofs-2)),1)) {
						foundx=xofs;
						foundy=-orient*(yofs-2);
					}
					if ( and(getTileCid(cen,0,-orient*yofs),1)
					&&  !and(getTileCid(cen,0,-orient*(yofs-1)),1)) {
						foundx=0;
						foundy=-orient*(yofs-1);
					}
				}
				
			}
			x=(cen.x+foundx)*pfTileWidth();
			y=(cen.y+foundy)*pfTileHeight();
		}
	}
	public class Tank extends JGObject {
		double speed;
		int move_timer=random(100,500,1),shoot_timer=0;
		public Tank(double x,double y,int yorient,double speed) {
			super("tank",true,x,y,4,null);
			this.speed=speed;
			ydir=yorient;
			xdir = random(-1,1,2);
			setTileBBox(2,yorient*8,10,14);
		}
		public void move() {
			String gfx="tank_";
			gfx += (ydir > 0) ? "u" : "d";
			gfx +=  (xdir > 0) ? "r" : "l";
			if (xdir*yspeed > 0.5) gfx += "d";
			if (xdir*yspeed < -0.5) gfx += "u";
			setGraphic(gfx);
			if (move_timer>0) {
				startAnim();
				if ((--move_timer)<=0) shoot_timer=100;
				Point cen = getCenterTile();
				if (and(getTileCid(cen,0,0),1)
				||  and(getTileCid(cen,xdir,0),1)) {
					if (yspeed > 0)
						yspeed = 0;
					else
						yspeed += -0.05*speed;
				} else {
					if (yspeed < 0)
						yspeed=0;
					else
						yspeed += 0.1*speed;
				}
				xspeed=(speed*1.2-Math.abs(yspeed));
				if (xspeed<0.1) xspeed=0.1;
				if (x <= 8 && xdir < 0)        xdir = 1;
				if (x > pfWidth()-24 && xdir > 0) xdir = -1;
			} else {
				stopAnim();
				if ((--shoot_timer)<=0) {
					move_timer = random(300,600,1);
					xdir = -xdir;
				}
				xspeed=0;
				yspeed=0;
				if ( (shoot_timer%20)==10) {
					double angle = Math.atan2(player.x-x,player.y-y);
					if (y < player.y) {
						new EnemyBullet(x, y,
							2.0*Math.sin(angle), 0.5*Math.cos(angle));
					} else {
						new EnemyBullet(x, y,
							2.0*Math.sin(angle), 5.5*Math.cos(angle));
					}
				}
			}
		}
	}
	public class EnemyBullet extends JGObject {
		public EnemyBullet(double x,double y,double xspeed,double yspeed) {
			super("Enemybullet",true,x,y,8,"bomb",-2);
			setSpeed(xspeed,yspeed);
		}
		public void move() {
			yspeed += 0.05;
		}
		public void hit_bg(int tilecid) {
			remove();
		}
	}
	public class Fireball extends JGObject {
		boolean is_small;
		int invulnerability=10;
		public Fireball(double x,double y,double xspeed,double yspeed,
		boolean is_small,int expiry) {
			super("fireball",true,x,y,16,is_small ? "fireball_sm":"fireball",
				expiry);
			setSpeed(xspeed,yspeed);
			this.is_small=is_small;
		}
		public void move() {
			yspeed += 0.03 + (is_small ? 0.03 : 0);
			invulnerability--;
		}
		public void hit_bg(int tilecid,int tilex,int tiley) {
			if (!is_small && invulnerability<0 && !and(tilecid,2)) {
				remove();
				for (int i=0; i<7; i++)
					new Fireball(x,y,xspeed+random(-2,2),random(-2,1),true,
						random(20,50,1) );
			}
		}
	}
	public class Explo extends JGObject {
		public Explo(double x,double y) {
			super("zexplo",true,x,y,0,null,17);
		}
		public void paint() {
			setColor(cycleColor(new Color[] {
				Color.yellow,Color.green,Color.cyan,Color.blue,
				Color.magenta,Color.red }, gametime, 0.25));
			setStroke(1);
			for (int i=0; i<2; i++) {
				double size = random(7,10+expiry);
				double radius = 20-expiry;
				drawOval((int)(x+random(-radius,radius)),
					(int)(y+random(-radius,radius)),
					(int)size,(int)size,true,true);
			}
		}
	}
}
