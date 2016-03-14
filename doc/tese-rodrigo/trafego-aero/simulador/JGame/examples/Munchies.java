package examples;
import jgame.*;
import java.awt.*;
public class Munchies extends StdGame {
	public static void main(String [] args) {new Munchies(parseSizeArgs(args));}
	public Munchies() { initEngineApplet(20,15,16,16,null,null,null); }
	public Munchies(Dimension size) {
		initEngine(20,15,16,16,null,null,null,size.width,size.height);
	}
	public void initGame() {
		defineMedia("examples/munchies.tbl");
		setFrameRate(40,4);
		key_startgame = KeyMouse1;
		lives_img="munchie_g_l3";
		startgame_ingame=true;
	}
	public void startTitle() {
		removeObjects(null,0);
		setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
		playAudio("music","titlemusic",true);
	}
	public void initNewGame() {
		super.initNewGame();
		stopAudio("music");
		lives=4;
	}
	public void startInGame() {
		setCursor(null);
		removeObjects(null,0);
		new Player();
		for (int i=2; i<4+(level%3); i++) {
			new Munchie(random(0.0,pfWidth()-16),pfHeight()+8-i*pfTileHeight(),
				(2.5 - (0.3*(level%3)) )*random(-1,1,2));
		}
		fillBG("");
	}
	//public void initNewGame() { super.initNewGame();level=8;}
	public void startGameOver() { removeObjects(null,0); }
	public void doFrameInGame() {
		moveObjects();
		checkCollision(1,4); // player hits spiders
		checkCollision(4,2); // spiders hit munchies
		if (checkTime(0, 1500, (int)(63-level*3))) {
			int tilepos;
			do {
				tilepos = random(1,pfTilesX()-2,1);
			} while (and(getTileCid(tilepos,0),1));
			orTileCid(tilepos,0,1);
			new Spider(tilepos);
		}
		if (gametime >= 1500 && countObjects("spider",0) == 0) levelDone();
	}
	public void incrementLevel() {
		stage++;
		level++;
		if (level > 10) level=8;
	}
	public void paintFrameLifeLost() {
		drawWavyString("Munchie Killed !",160,50,0,12,seqtimer,
			5.0,0.5,0.1, title_font,Color.red);
	}
	public void paintFrameGameOver() {
		drawWavyString("Game Over !",160,50,0,12,seqtimer,
			5.0,0.5,0.1, title_font,Color.red);
	}
	public void paintFrameStartLevel() {
		drawWavyString("Level "+(stage+1)+"!",160,50,0,12,seqtimer,
			5.0,0.5,0.1, title_font,Color.yellow);
	}
	public void paintFrameStartGame() {}
	public void paintFrameLevelDone() {
		drawWavyString("Level Done !",160,50,0,12,seqtimer,
			5.0,0.5,0.1, title_font,Color.yellow);
	}
	public void paintFrameTitle() {
		drawWavyString("Feed the Munchies",160,50,0,12,seqtimer,
			5.0,0.5,0.05, title_font,Color.yellow);
		drawWavyString("Press "+getKeyDesc(key_startgame)+" to start",160,120,0,8,seqtimer,
			5.0,0.5,0.05, status_font,Color.red);
	}
	public void drawWavyString(String s, int x,int y,Color col) {
		setColor(col);
		for (int i=0; i<s.length(); i++)
			drawString(s.substring(i,i+1) ,8 + x + i*13,
				y+(int)(4.9*Math.sin(i+timer/4.0)),0);
	}
	public class Spider extends JGObject {
		boolean falling=false;
		double divider = random(8,20);
		public Spider(int tilex) {
			super("spider",true,tilex*pfTileWidth(),-16,4,"spider");
		}
		public void move() {
			if (!falling) {
				setSpeedAbs(0, 0.25 + 0.2*Math.sin(timer/divider));
				// set bounding box around thread
				setBBox(4,(int)-y,8,(int)y+16);
			} else {
				// set bounding box around spider
				clearBBox();
			}
			if (y >= pfHeight()-24) {
				setSpeedAbs(0,0);
				Point cen=getCenterTile();
				for (int i=1; i<pfTilesY(); i++)andTileCid(cen.x,i,~1);
			}
		}
		public void paint() {
			if (!falling) {
				drawLine((int)x+8,0,(int)x+8,(int)y,1.0,new Color(150,0,120));
			}
		}
		public void hit(JGObject obj) {
			if (!falling && getMouseButton(1)) {
				falling=true;
				yspeed = 3.0;
				setAnimSpeed(0.3);
				Point cen=getCenterTile();
				for (int i=1; i<pfTilesY(); i++) orTileCid(cen.x,i, 1);
			}
		}
	}
	public class Munchie extends JGObject {
		int timeleft=600+random(0,200,1);
		public Munchie(double x,double y,double speed) {
			super("munchie",true,x,y,2,null);
			setSpeedAbs(speed,0);
		}
		public void move() {
			if (xdir!=0) { // moving
				setGraphic(getGraphicName());
				if ((timeleft--) <= 0) {
					setGraphic("munchie_die");
					lifeLost();
				}
			}
			if (x < -pfTileWidth() && xdir < 0) x = pfWidth();
			if (x > pfWidth() && xdir > 0) x = -pfTileWidth();
			if (isXAligned()) {
				if (and(getTileCid(getCenterTile(),0,0),1)) {
					// open mouth for spider
					setGraphic(getGraphicName()+"_eat");
					xdir=0;
					Point cen=getCenterTile();
					for (int i=1; i<pfTilesY(); i++)andTileCid(cen.x,i,~1);
				}
			}
		}
		public void hit(JGObject obj) {
			if (xdir==0) { // eat spider when we opened mouth
				playAudio("munch");
				// remove occupied space
				Point spiderocc = getTileIndex(obj.x,0);
				andTileCid(spiderocc.x,spiderocc.y,~1);
				obj.remove();
				xdir = random(-1,1,2);
				timeleft=600;
				score += 5;
			} else if (obj.yspeed==0) { // die if spider stands still
				setGraphic("munchie_die");
				lifeLost();
			}
		}
		private String getGraphicName() {
			String gfxname="munchie_";
			if      (timeleft < 200) gfxname += "r_";
			else if (timeleft < 400) gfxname += "y_";
			else                     gfxname += "g_";
			return gfxname + ( (xdir<0) ?  "l" : "r" );
		}
	}
	public class Player extends JGObject {
		public Player() {
			super("player",false,0,0,1,null);
		}
		boolean scissors_c=false;
		public void move() {
			x = getMouseX();
			y = getMouseY();
			if (getMouseButton(1)) {
				setGraphic("scissors_c");
				if (!scissors_c) playAudio("scissors");
				scissors_c=true;
			} else {
				setGraphic("scissors_o");
				scissors_c=false;
			}
		}
	}
}
