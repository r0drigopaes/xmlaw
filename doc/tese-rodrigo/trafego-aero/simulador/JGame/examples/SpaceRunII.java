package examples;
import jgame.*;
import java.awt.*;
/** A more customised version of Space Run */
public class SpaceRunII extends StdGame {
	public static void main(String [] args) {
		new SpaceRunII(parseSizeArgs(args));
	}
	public SpaceRunII() { initEngineApplet(20,15,16,16,null,null,null); }
	public SpaceRunII(Dimension size) {
		initEngine(20,15,16,16,null,null,null,size.width,size.height);
	}
	public void initGame() {
		defineMedia("examples/space_run.tbl");
		setFrameRate(45,1);
		lives_img="ship";
		startgame_ingame=true;
		leveldone_ingame=true;
		title_color = Color.yellow;
		title_bg_color = new Color(140,0,0);
		title_font = new Font("Arial",0,20);
	}
	public void initNewLife() {
		removeObjects(null,0);
		new Player(16,pfHeight()/2,5);
	}
	public void startGameOver() { removeObjects(null,0); }
	public void doFrameInGame() {
		moveObjects();
		checkCollision(2+4,1); // enemies, pods hit player
		if (checkTime(0,800,8-(level/2)))
			new JGObject("enemy",true,pfWidth(),random(0,pfHeight()-16),
				2, "enemy", 0,0,16,16, -3.0-level, random(-1,1), -2);
		if (checkTime(0,800,20-level))
			new JGObject("pod",true,pfWidth(),random(0,pfHeight()-16),
				4, "pod", 0,0,14,14, -3.0-level, 0.0, -2);
		if (gametime >= 800 && countObjects("enemy",0) == 0) levelDone();
	}
	public void incrementLevel() {
		if (level<7) level++;
		stage++;
	}
	Font scoring_font = new Font("Arial",0,8);
	public void paintFrameLifeLost() {
		drawRect(160,50,seqtimer*7,seqtimer*5,true,true,1,title_bg_color);
		int ypos = posWalkForwards(-24,pfHeight(), seqtimer, 80, 50, 20, 10);
		drawString("You're hit !",160,ypos,0,
			getZoomingFont(title_font,seqtimer,0.2,1/40.0),  title_color);
	}
	public void paintFrameGameOver() {
		drawRect(160,51,seqtimer*2,seqtimer/2,true,true,1,title_bg_color);
		drawString("Game Over !",160,50,0,
			getZoomingFont(title_font,seqtimer,0.2,1/120.0), title_color);
	}
	public void paintFrameStartGame() {
		drawString("Get Ready!",160,50,0,
			getZoomingFont(title_font,seqtimer,0.2,1/80.0), title_color);
	}
	public void paintFrameStartLevel() {
		drawString("Stage "+(stage+1),160,50+seqtimer,0,
			getZoomingFont(title_font,seqtimer,0.2,1/80.0), title_color);
	}
	public void paintFrameLevelDone() {
		drawString("Stage "+(stage+1)+" Clear !",160,50,0,
			getZoomingFont(title_font,seqtimer+80,0.2,1/80.0), title_color);
	}
	public void paintFrameTitle() {
		drawString("Space Run",160,50,0,
			getZoomingFont(title_font,seqtimer+20,0.3,0.03), title_color);
		drawString("Press "+getKeyDesc(key_startgame)+" to start",160,120,0,
			getZoomingFont(title_font,seqtimer+5,0.3,0.03), title_color);
		drawString("Press "+getKeyDesc(key_gamesettings)+" for settings",
			160,160,0,getZoomingFont(title_font,seqtimer,0.3,0.03),title_color);
	}
	public class Player extends JGObject {
		public Player(double x,double y,double speed) {
			super("player",true,x,y,1,"ship", 0,0,32,16,0,0,speed,speed,-1);
		}
		public void move() {
			setDir(0,0);
			if (getKey(key_up)    && y > yspeed)               ydir=-1;
			if (getKey(key_down)  && y < pfHeight()-16-yspeed) ydir=1;
			if (getKey(key_left)  && x > xspeed)               xdir=-1;
			if (getKey(key_right) && x < pfWidth()-32-yspeed)  xdir=1;
		}
		public void hit(JGObject obj) {
			if (and(obj.colid,2)) lifeLost();
			else {
				score += 5;
				obj.remove();
				new StdScoring("pts",obj.x,obj.y,0,-1.0,40,"5 pts",scoring_font,
					new Color [] { Color.red,Color.yellow },2);
			}
		}
	}
}
