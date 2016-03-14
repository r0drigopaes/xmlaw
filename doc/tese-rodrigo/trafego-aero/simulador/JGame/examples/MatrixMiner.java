package examples;
import jgame.*;
import java.awt.*;
public class MatrixMiner extends StdGame {
	Color intro_color = new Color(60,100,255);
	public static void main(String [] args) {
		new MatrixMiner(parseSizeArgs(args));
	}
	public MatrixMiner() { initEngineApplet(19,15,16,16,intro_color,null,null);}
	public MatrixMiner(Dimension size){
		initEngine(19,15,16,16,intro_color,null,null,size.width,size.height);
	}
	public void initGame() {
		defineMedia("examples/matrix_miner.tbl");
		setFrameRate(40,4);
		lifelost_ingame=true;
		startgame_ticks=0;
		gameover_ticks=0;
		leveldone_ticks=120;
		defineLevel();
	}
	String bonus_msg;
	Player player=null;
	public void doFrameInGame() {
		moveObjects();
		checkCollision(1,8); // player hits goodies
		checkCollision(2+16,1); // enemies and enemy bullets hit player
		checkCollision(2,32); // enemies hit goodies
		checkCollision(4,2); // bullets hit robots
		checkBGCollision(1,4+16); // walls hit bullets
		if (gametime%(int)(gametime < 30+4*level ? 5 : 55-level*3 ) == 0) {
			String enemy_gfx = "enemy"+(1+stage%3)+"_";
			switch ((int)random(0,4)) {
				case 0: new Enemy(enemy_gfx,(int)random(1,19,2), -0.8, 0, 1);
				break;
				case 1: new Enemy(enemy_gfx,(int)random(1,19,2), 14.8, 0, -1);
				break;
				case 2: new Enemy(enemy_gfx,-0.8,(int)random(1,15,2), 1,0);
				break;
				case 3: new Enemy(enemy_gfx,18.8,(int)random(1,15,2),-1,0);
				break;
			}
		}
		if (countObjects("@goodie",8)==0) {
			int bonus = countObjects("@goodie",0);
			if (bonus < 20) {
				bonus_msg = "25 X "+bonus+"..."+(25*bonus)+" PTS";
				score += 25*bonus;
			} else {
				bonus_msg = "PERFECT! 1000 PTS";
				score += 1000;
			}
			removeObjects(null,0);
			levelDone();
		}
	}
	public void incrementLevel() {
		if (level<9) level++;
		stage++;
	}
	public void paintFrame() {
		drawImageString(""+score,0,0,-1,"font_map",32,2);
		for (int i=1; i<lives; i++) drawImage(pfWidth()-16*i,0,"player_r0");
	}
	public void paintFrameTitle() {
		drawImageString("MATRIX MINER",40,75,-1,"font_map",32,2);
		drawImageString("PRESS " + getKeyDesc(key_startgame).toUpperCase(),
			50,140,-1,"font_map",32,2);
	}
	public void paintFrameLifeLost() {
		drawImageString("GOT YOU !",75,75,-1,"font_map",32,2);
	}
	public void paintFrameLevelDone() {
		int ypos1 = posWalkForwards(-16,pfHeight(),seqtimer, 120, 75, 10,100);
		drawImageString("LEVEL "+(stage+1)+" CLEAR!", pfWidth()/2, ypos1, 0,
			"font_map",32,2 );
		int ypos2 = posWalkForwards(-16,pfHeight(),seqtimer, 120, 140, 15,95);
		drawImageString(bonus_msg,pfWidth()/2,ypos2,0,"font_map",32,2);
	}
	public void startInGame() {
		removeObjects("Enemy",0);
		removeObjects("bullet",0);
		player=new Player(160-16,80,key_up,key_down,key_left,key_right);
	}
	public void startTitle() { removeObjects(null,0); }
	public void defineLevel() {
		removeObjects(null,0);
		//player=new Player(160,80);
		fillBG(".");
		// generate base matrix
		for (int y=0; y<pfTilesY(); y++) {
			for (int x=0; x<pfTilesX(); x++) {
				if(!and(x,1) && !and(y,1)) {
					setTile(x,y,"#"+(stage%7));
				} else
				if (x==0 || x==pfTilesX()-1
				||  y==0 || y==pfTilesY()-1 ) {
					setTile(x,y,"*"); // invisible wall for player
				}
			}
		}
		// some extra wall segments
		for (int i=0; i<10+10*(stage%2); i++) {
			int wx,wy;
			// only place when there's no wall or invisible wall
			// and when the exits in all compass directions are still open
			do {
				wx = (int)random(1,pfTilesX()-1,1);
				wy = (int)random(1,pfTilesY()-1,2) + ((wx&1)!=0 ? 1 : 0);
			} while ( and(getTileCid(wx,wy),1+2)
			||        and(getTileCid(wx-2,wy),1+2)
			||        and(getTileCid(wx,wy-2),1+2)
			||        and(getTileCid(wx+2,wy),1+2)
			||        and(getTileCid(wx,wy+2),1+2) );
			setTile(wx,wy,"#"+(stage%7));
		}
		for (int i=0; i<20; i++) {
			new Goodie(16*random(4,15,2), 16*random(4,11,2));
		}
	}
	public class Goodie extends JGObject {
		Point [] prevpos = new Point [4];
		JGObject to_follow=null;
		boolean is_in_tile=true;
		public Goodie(double x,double y) {
			super("@goodie",true,x,y,8,"goodie",0,0,16,16);
			// stick out on all sides so we detect when player passes along
			setBBox(-4,-4,24,24);
		}
		public void hit(JGObject obj) {
			if (colid==32) { // hit with enemy
				remove();
			} else if (is_in_tile) { // player passes along goodie
				int xdir=0,ydir=0;
				if (player.xdir!=0 && player.y > y+8) ydir = -1;
				if (player.xdir!=0 && player.y < y-8) ydir = 1;
				if (player.ydir!=0 && player.x > x+8) xdir = -1;
				if (player.ydir!=0 && player.x < x-8) xdir = 1;
				if (!and(getTileCid(getCenterTile(),xdir,ydir),1)) {
					clearBBox();
					x += xdir*16;
					y += ydir*16;
					is_in_tile=false;
					score += 5;
				}
			} else { // player collects goodie
				colid=32;
				if (((Player)obj).following!=null)
					((Player)obj).following.to_follow=this;
				((Player)obj).following=this;
				to_follow=obj;
				for (int i=0; i<4; i++) shiftPos(obj.x,obj.y);
				score += 10;
			}
		}
		Point shiftPos(double x,double y) {
			for (int i=0; i<3; i++) prevpos[i] = prevpos[i+1]; 
			prevpos[3] = new Point((int)x,(int)y);
			return prevpos[0];
		}
		public void move() {
			if (to_follow!=null) { // follow the player
				if (!to_follow.isAlive()) remove();
				Point newpos = shiftPos(to_follow.getLastX(),
					to_follow.getLastY() );
				x = newpos.x;
				y = newpos.y;
			}
		}
	}
	public class Player extends StdMazePlayer {
		Goodie following=null;
		public Player(double x, double y, int k_up,int k_down,int k_left,
		int k_right) {
			super("player", x,y, 1, "player_", true, false, 3, 3.0,
			k_up,k_down,k_left,k_right);
		}
		public void move() {
			super.move();
			if (countObjects("bullet",4) < 3) {
				if (getKey(key_fireleft)) {
					new Bullet(x,y,-1,0, 4); clearKey(key_fireleft);
				} else if (getKey(key_fireright)) {
					new Bullet(x,y, 1,0, 4); clearKey(key_fireright);
				} else if (getKey(key_fireup)) {
					new Bullet(x,y,0,-1, 4); clearKey(key_fireup);
				} else if (getKey(key_firedown)) {
					new Bullet(x,y,0,1,  4); clearKey(key_firedown);
				}
			}
		}
		public void hit(JGObject obj) {
			for (int i=0; i<16; i++) new JGObject("explo",true,x,y,0,"explo",
					random(-2,2),random(-2,2), (int)random(16,64) );
			player.remove();
			lifeLost();
		}
	}
	public class Bullet extends JGObject {
		public Bullet(double x,double y, int xdir,int ydir, int cid) {
			super("bullet",true,x,y,cid,"bullet", 0,0,-2);
			double speed = and(cid,16) ?  4.0/(3.0 - 0.15*level)  :  4.0;
			setSpeedAbs(xdir*speed, ydir*speed);
		}
		public void hit_bg(int cid) { remove(); }
	}
	public class Enemy extends StdMazeMonster {
		int bullettimer=200 - (int)random(0,100);
		public Enemy(String graphic,double x, double y, int xdir, int ydir) {
			super("Enemy",true,16*x,16*y,2,graphic,true,1,
				xdir,ydir, 0.0, null,false, 0.0);
			home_in=player;
			setSpeed(0.35+0.05*level);
		}
		public void move() {
			super.move();
			if ((xdir!=0 || ydir!=0) && bullettimer<=0) { // shoot
				new Bullet(x,y,xdir,ydir,16);
				bullettimer=440 - 20*level;
			}
			if (bullettimer > 0) bullettimer--;
			if (!isOnScreen(0,0)) remove();
		}
		public void hit(JGObject o) {
			for (int i=0; i<8; i++) new JGObject("explo",true,x,y,0,"explo",
					random(-2,2),random(-2,2), (int)random(8,32) );
			o.remove();
			remove();
			score += 5;
		}
	}
}
