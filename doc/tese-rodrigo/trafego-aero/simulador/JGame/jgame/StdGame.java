package jgame;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
/** A basic framework for a game.  It supports an animation and game timer,
* object creation at fixed intervals, score, lives, levels, configurable keys.
* There are title, start-level, next-level, death, and game-over sequences.
* Todo: highscores, key configuration file and GUI.

* <p>To initialise this class, use the regular initEngine (from main), or
* initEngineApplet (from parameterless constructor).  You can supply the width
* and height of the window as command line arguments by calling
* parseSizeArgs(args) from your main().  Define initGame() as usual.  StdGame
* does all its logic in the doFrame method, so you should ensure that it is
* called (i.e. call super.doFrame() if you choose to override doFrame).  The
* game will automatically start in the "Title" gamestate when it finds that
* it isn't in this state in the first call to doFrame().  You can also set the
* "Title" state in initGame if you even want the first frame to be in
* "Title".

* <p>The class uses the following state machine, using JGEngine's state
* machine mechanism:

* <p><i>Title</i>: displays title screen.  Transition to
* {StartLevel,StartGame} when the key_startgame is pressed.  Before the
* transition, initNewGame(), defineLevel(), and initNewLife() are called.

* <p><i>InGame</i>: game is playing.  Transition to LifeLost when lifeLost()
* is called from within the game.  Transition to LevelDone when levelDone() is
* called from within the game.  Transition to GameOver when gameOver() is
* called (i.e. to quit game).  The gametime timer indicates how many ticks the
* game has been running since the beginning of the level.

* <p>StdGame supports a set of standard game sequences, which are represented
* as game states: StartLevel/StartGame, LevelDone, LifeLost, GameOver.  These
* can be configured so they add the InGame state to the sequence state (i.e.
* the game is in both states simultaneously).  This can be used to animate the
* game while playing the corresponding sequence.  This is off by default.  The
* seqtimer timer is set to 0 at the beginning of each sequence, and increments
* during the sequence to indicate how many ticks the sequence has been
* playing.  The number of ticks that the sequence should take can be
* configured, or the sequence can be skipped altogether by setting ticks to 0.

* <p><i>StartGame</i>: start game sequence is played.  Transition to InGame
* after a certain time has elapsed or the continuegame key is pressed. 

* <p><i>StartLevel</i>: start level sequence is played.  Transition to InGame
* after a certain time has elapsed or the continuegame key is pressed.  Is always
* active in combination with StartGame; it's just an indication that StartGame
* is also a start of a new level.

* <p><i>LevelDone</i>: next level sequence is played.  Transition to
* StartLevel/StartGame after a certain time has elapsed or the continuegame key
* is pressed.  Before the transition, resp. incrementLevel() and defineLevel()
* are called.

* <p><i>LifeLost</i>:  player has just died, a death sequence is played.
* Transition to either GameOver or StartGame after a certain time has elapsed
* or the continuegame key is pressed, dependent of whether there are lives left.
* Before the transition to StartGame, decrementLives() and initNewLife are
* called.

* <p><i>GameOver</i>: game over sequence is played.  Transition to Title after
* a certain time or the continuegame key is pressed.

* <p>Built in are also game exit (through the key_quitgame, which is Escape by
* default), and pause game (through the key_pausegame, which defaults to 'P').
*/
public abstract class StdGame extends JGEngine {

	/* settings */

	/** Flag indicating that audio is enabled */
	public boolean audioenabled=true;

	/** Key for starting the game, default = space. */
	public int key_startgame = ' ';
	/** Key for invoking the game settings window, default = enter. */
	public int key_gamesettings = KeyEnter;
	/** Key for continuing the game when in a sequence, default = space. */
	public int key_continuegame = ' ';
	/** Key for quitting the game, default = escape. */
	public int key_quitgame = 27;
	/** Key for pausing the game, default = P. */
	public int key_pausegame = 'P';
	/** Key for moving, default = cursors. */
	public int key_left=KeyLeft, key_right=KeyRight,
	           key_up  =KeyUp,    key_down=KeyDown;
	/** Key for moving diagonally, default = none. */
	//public int key_upleft=0, key_downleft=0,
	//           key_upright=0, key_downright=0;
	/** Key for firing (in case there are no separate directional fire keys),
	* default=Z. */
	public int key_fire      = 'Z';
	/** Key for directional firing, default = WSAD keys */
	public int key_fireleft = 'A', key_fireright= 'D',
	           key_fireup   = 'W', key_firedown = 'S';
	/** Key for diagonal firing, default = none */
	//public int key_fireupleft =0, key_firedownleft=0,
	//           key_fireupright=0, key_firedownright=0;
	/** Keys for special actions.  Default = action[0]=ctrl, action[1]=alt */
	//public int [] key_action = new int [] 
	//{ KeyCtrl,KeyAlt, 0,0,0, 0,0,0,0,0 };

	/** Game timer.  Is reset to 0 at the beginning of each level, increments
	 * during InGame. */
	public int gametime=0;
	/** Sequence timer.  Is reset to 0 at the start of the Title, StartLevel,
	* StartGame,
	* LevelDone, LifeLost, GameOver sequences.  Counts always.  Can be used to
	* time animations for these sequences. */
	public int seqtimer=0;
	/** Animation timer.  Always continues on counting. Can be used to time
	 * animations etc. */
	public int timer=0;
	/** Player score; starts at 0 at beginning of game. */
	public int score=0;
	/** Difficulty level; starts at 0 at beginning of game.  Can be
	 * incremented each time a level is complete. Can be used to determine game
	 * difficulty settings throughout the game.  */
	public int level=0;
	/** Game stage, which is usually the same as level, but typically goes on
	* counting, while level may stop increasing at a certain value.
	* Can be used to vary graphic sets, display stage number, etc. */
	public int stage=0;
	/** Lives count, 0 means game over.  */
	public int lives=0;
	/** Initial value for lives; default=4 */
	public int initial_lives=4;

	/** Number of ticks to stay in StartLevel/StartGame state, 0 = skip */
	public int startgame_ticks=80;
	/** Number of ticks to stay in LevelDone state, 0 = skip */
	public int leveldone_ticks=80;
	/** Number of ticks to stay in LifeLost state, 0 = skip */
	public int lifelost_ticks=80;
	/** Number of ticks to stay in GameOver state, 0 = skip */
	public int gameover_ticks=120;

	/** Indicates whether the InGame state should be retained when in the
	 * corresponding sequence state. */
	public boolean startgame_ingame=false, leveldone_ingame=false,
	               lifelost_ingame=false, gameover_ingame=false;

	/** Font to use to display score */
	public Font status_font = new Font("Courier",Font.BOLD,12);
	/** Color to use to display score */
	public Color status_color = Color.white;
	/** Image to use to display lives */
	public String lives_img = null;

	/** Font to use to display title and messages */
	public Font title_font = new Font("Courier",0,18);
	/** Color to use to display title and messages */
	public Color title_color = Color.white;
	/** Color to use to display background effects behind title and messages */
	public Color title_bg_color = Color.blue;

	/** indicates that engine has just started and has not produced a single
	 * frame. */
	boolean just_inited=true;

	/** The application configuration handler. Default is null, use
	* initAppConfig to initialise it. */
	public AppConfig appconfig=null;

	/** Set the status display variables in one go. */
	public void setStatusDisplay(Font status_font,Color status_color,
	String lives_img) {
		this.status_font=status_font;
		this.status_color=status_color;
		this.lives_img=lives_img;
	}
	/** Set all sequence variables in one go. */
	public void setSequences(boolean startgame_ingame,int startgame_ticks,
	boolean leveldone_ingame, int leveldone_ticks,
	boolean lifelost_ingame, int lifelost_ticks,
	boolean gameover_ingame, int gameover_ticks) {
		this.startgame_ingame=startgame_ingame;
		this.leveldone_ingame=leveldone_ingame;
		this.lifelost_ingame=lifelost_ingame;
		this.gameover_ingame=gameover_ingame;
		this.startgame_ticks=startgame_ticks;
		this.leveldone_ticks=leveldone_ticks;
		this.lifelost_ticks=lifelost_ticks;
		this.gameover_ticks=gameover_ticks;
	}

	/** Parse the first two arguments assuming they are width and height. */
	public static Dimension parseSizeArgs(String [] args) {
		Dimension size = new Dimension(0,0);
		if (args.length==2) {
			try {
				size.width = Integer.parseInt(args[0]);
				size.height = Integer.parseInt(args[1]);
			} catch (Exception e) {
				System.out.println("\nError parsing width/height arguments."
					+"They should be integers.\n" );
				System.exit(0);
			}
		} else if (args.length!=0) {
			System.out.println(
			"\nYou can supply either no arguments, or [width] [height].\n" );
			System.exit(0);
		}
		return size;
	}

	/** Returns path to writable location for a file with the given name.
	 * Basically it uses [user.home] / .jgame / [filename], with "/" being the
	 * system path separator.  In case [user.home] / .jgame does not exist, it
	 * is created.  In case .jgame is not a directory, null is returned.  In
	 * case the file does not exist yet, an empty file is created.
	 * @return path to writable file, or null if not possible
	 */
	public String getConfigPath(String filename) {
		if (isApplet()) return null;
		File jgamedir = new File(System.getProperty("user.home"), ".jgame");
		if (!jgamedir.exists()) {
			// try to create ".jgame"
			if (!jgamedir.mkdir()) {
				// fail
				return null;
			}
		}
		if (!jgamedir.isDirectory()) return null;
		File file = new File(jgamedir,filename);
		// try to create file if it didn't exist
		try {
			file.createNewFile();
		} catch (IOException e) {
			return null;
		}
		if (!file.canRead()) return null;
		if (!file.canWrite()) return null;
		try {
			return file.getCanonicalPath();
		} catch (IOException e) {
			return null;
		}
	}

	/* special state functions */

	/** Initialise the game when a new game is started.  Default sets level,
	 * stage, score to 0, and lives to initial_lives. */
	public void initNewGame() {
		level=0;
		stage=0;
		score=0;
		lives=initial_lives;
	}
	/** Initialise play specifically after a new life is introduced (that is,
	 * at game start and after the player has died.  This is typically used to
	 * reinitialise the player.  If you want a specific initialisation at
	 * both the beginning of the level or after the player death, use
	 * startInGame(). Default is do nothing. */
	public void initNewLife() {}

	/** Initialise a level.  Default is do nothing. */
	public void defineLevel() {}

	/** Code for losing a life before transition from LifeLost to InGame is
	 * made.  Default is decrement lives.
	 */
	public void decrementLives() {
		lives--;
	}
	/** Code for incrementing a level before transition from LevelDone to
	* InGame is made.  Default is increment level and stage. */
	public void incrementLevel() {
		level++;
		stage++;
	}

	/* state transition functions */

	/** Call to make state transition to LifeLost.  Is ignored when in
	 * another state than InGame or {InGame,StartLevel/StartGame}.
	 * After the LifeLost
	 * sequence, goes to InGame or GameOver, depending on lives left. */
	public final void lifeLost() {
		if (!inGameState("InGame") || inGameState("LevelDone")
		|| inGameState("LifeLost") || inGameState("GameOver") ) return;
		//	System.err.println(
		//	"Warning: lifeLost() called from other state than InGame." );
		//}
		clearKey(key_continuegame);
		removeGameState("StartLevel");
		removeGameState("StartGame");
		seqtimer=0;
		if (lifelost_ticks > 0) {
			if (lifelost_ingame) addGameState("LifeLost");
			else                 setGameState("LifeLost");
			new JGTimer(lifelost_ticks,true,"LifeLost") { public void alarm() {
				endLifeLost();
			} };
		} else {
			endLifeLost();
		}
	}
	private void endLifeLost() {
		clearKey(key_continuegame);
		decrementLives();
		if (lives <= 0) {
			gameOver();
		} else {
			initNewLife();
			seqtimer=0;
			if (startgame_ticks > 0) {
				// force call to startInGame()
				setGameState("StartGame");
				if (startgame_ingame) addGameState("InGame");
				new JGTimer(startgame_ticks,true,"StartGame") {
					public void alarm() { setGameState("InGame"); } };
			} else {
				// force call to startInGame()
				clearGameState();
				setGameState("InGame");
			}
		}
	}
	/** Call to make state transition to LevelDone.  Is ignored when state is
	 * not InGame or {Ingame,StartLevel/StartGame}. After the LevelDone
	 * sequence, it sets gametime to 0, calls
	 * incrementLevel and defineLevel, and goes to StartLevel/StartGame. */
	public final void levelDone() {
		if (!inGameState("InGame") || inGameState("LevelDone")
		|| inGameState("LifeLost") || inGameState("GameOver") ) return;
		//	System.err.println(
		//	"Warning: levelDone() called from other state than InGame." );
		//}
		clearKey(key_continuegame);
		removeGameState("StartLevel");
		removeGameState("StartGame");
		seqtimer=0;
		if (leveldone_ticks > 0) {
			if (leveldone_ingame) addGameState("LevelDone");
			else                  setGameState("LevelDone");
			new JGTimer(leveldone_ticks,true,"LevelDone") {public void alarm() {
				levelDoneToStartLevel();
			} };
		} else {
			levelDoneToStartLevel();
		}
	}
	private void levelDoneToStartLevel() {
		clearKey(key_continuegame);
		gametime=0;
		incrementLevel();
		defineLevel();
		seqtimer=0;
		if (startgame_ticks > 0) {
			// force call to startInGame
			setGameState("StartLevel");
			addGameState("StartGame");
			if (startgame_ingame) addGameState("InGame");
			new JGTimer(startgame_ticks,true,"StartLevel") {
				public void alarm() { setGameState("InGame"); } };
		} else {
			// force call to startInGame
			clearGameState();
			setGameState("InGame");
		}
	}
	/** Call to make straight transition to GameOver; is called automatically
	* by lifeLost when appropriate.  Is ignored when game state is not
	* {InGame}, {Ingame,Start*}, or LifeLost.  Will go to Title after GameOver
	* sequence.
	*/
	public final void gameOver() {
		// XXX hmm. we should check out these conditions
		if ( inGameState("GameOver")
		||  (!inGameState("InGame") && !inGameState("LifeLost")) ) return;
		//	System.err.println( "Warning: gameOver() called from other state"
		//		+" than InGame or LifeLost." );
		//}
		clearKey(key_continuegame);
		removeGameState("StartLevel");
		removeGameState("StartGame");
		removeGameState("LifeLost");
		seqtimer=0;
		if (gameover_ticks > 0) {
			if (gameover_ingame) addGameState("GameOver");
			else                 setGameState("GameOver");
			new JGTimer(gameover_ticks,true,"GameOver") {
				public void alarm() { gotoTitle(); } };
		} else {
			gotoTitle();
		}
	}
	private void gotoTitle() {
		seqtimer=0;
		clearKey(key_startgame);
		setGameState("Title");
	}

	/** Define appconfig for configuring keys.  Override to define your own
	 * appconfig; define an empty method to remove the ability of game
	 * configuration.  Default behaviour is: create appconfig, set the "key_"
	 * fields as configuration fields, then load them from a file named
	 * $JGAMEHOME/[classname].cfg, and save these in the StdGame object.
	 * Create your own AppConfig like this:
	 * <pre>
	 * appconfig = new AppConfig("[title]", this, getConfigPath("[filename]");
	 * </pre>
	 * getConfigPath returns a path to a writable file in [user.home]/.jgame/
	 */
	public void initAppConfig() {
		appconfig = new AppConfig(getClass().getName().substring(
			getClass().getName().indexOf('.')+1 )+" settings",  this,
			getConfigPath(getClass().getName()+".cfg") );
		appconfig.defineField("audioenabled","Enable Sound","boolean");
		appconfig.defineFields("key_","","","","key");
		appconfig.loadFromFile();
		appconfig.saveToObject();
	}

	/** The main doFrame takes care of all the standard game actions.  If you
	* override it, you should typically call super.doFrame().  doFrame
	* increments timer, increments gametime when in InGame, quits game when
	* key_quitgame is pressed in InGame. In Title, it waits for the user to
	* press the key_startgame, then sets gametime to 0, calls initNewGame,
	* defineLevel, and goes to StartLevel. It also handles the continue_game
	* key inside the sequences, and the gamesettings key in Title.  It also
	* ensures the audioenabled flag is passed to engine. */
	public void doFrame() {
		// pass audioenabled
		if (audioenabled) { enableAudio(); } else { disableAudio(); }
		// handle pause mode
		if (inGameState("Paused")) {
			clearKey(key_pausegame);
			// stop and remove game state on the next frame
			removeGameState("Paused");
			stop();
		}
		if (getKey(key_pausegame)) {
			addGameState("Paused");
			clearKey(key_pausegame);
			wakeUpOnKey(key_pausegame);
		}
		// handle general actions
		timer++;
		seqtimer++;
		if (just_inited) {
			setGameState("Title");
			just_inited=false;
			//handle appconfig
			initAppConfig();
 			if (appconfig!=null) {
				// continue when config window is closed
 				appconfig.setListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						start();
						requestGameFocus();
					}
 				} );
 			}
		} else if (inGameState("InGame")) {
			gametime++;
			if (getKey(key_quitgame)) gameOver();
		} else if (inGameState("Title")) {
			if (getKey(key_gamesettings) && appconfig!=null) {
				appconfig.openGui();
				clearKey(key_gamesettings);
 				//pause application until config window is closed
				//appconfig.waitCloseGui();
				stop();
			}
			if (getKey(key_startgame)) {
				gametime=0;
				initNewGame();
				defineLevel();
				initNewLife();
				// code duplicated in levelDone
				clearKey(key_continuegame);
				seqtimer=0;
				if (startgame_ticks > 0) {
					setGameState("StartLevel");
					addGameState("StartGame");
					if (startgame_ingame) addGameState("InGame");
					new JGTimer(startgame_ticks,true,"StartLevel") {
						public void alarm() { setGameState("InGame"); } };
				} else {
					setGameState("InGame");
				}
			}
		} else if (inGameState("StartGame")) {
			if (getKey(key_continuegame)) setGameState("InGame");
		} else if (inGameState("LevelDone")) {
			if (getKey(key_continuegame)) levelDoneToStartLevel();
		} else if (inGameState("LifeLost")) {
			if (getKey(key_continuegame)) endLifeLost();
		} else if (inGameState("GameOver")) {
			if (getKey(key_continuegame)) gotoTitle();
		}
	}

	/* default start... functions */

	/** Initialise the title screen.  This is a standard state transition
	* function. Default is do nothing. */
	public void startTitle() {}

	/** Initialisation at the start of the in-game action. This is a
	* standard state transition function.  Note that it is always called after
	* StartLevel and LifeLost, even if startgame_ingame and
	* lifelost_ingame are set.  Default is do nothing. */
	public void startInGame() {}

	/** Initialise start-level sequence. This is a
	* standard state transition function.  Default is do nothing. */
	public void startStartLevel() {}

	/** Initialise start-game sequence. This is a
	* standard state transition function.  Default is do nothing. */
	public void startStartGame() {}

	/** Initialise next-level sequence. This is a
	* standard state transition function.  Default is do nothing. */
	public void startLevelDone() {}

	/** Initialise death sequence. This is a
	* standard state transition function.  Default is do nothing. */
	public void startLifeLost() {}

	/** Initialise game over sequence. This is a
	* standard state transition function. Default is do nothing. */
	public void startGameOver() {}

	/* default paint functions */

	/** Default paintFrame displays score at top left, lives at top right.
	* When lives_img is set, it uses that image to display lives. */
	public void paintFrame() {
		setFont(status_font);
		setColor(status_color);
		drawString("Score "+score,0,0,-1);
		if (lives_img==null) {
			drawString("Lives "+lives,pfWidth(),0,1);
		} else {
			drawCount(lives-1, lives_img, pfWidth(),0,
				- getImageSize(lives_img).width-2 );
		}
	}
	/** Default displays class name as title, and "press [key_startgame] to
	 * start" below it. */
	public void paintFrameTitle() {
		drawString(getClass().getName().substring(getClass().getName().indexOf(
			'.' )+1 ), pfWidth()/2,pfHeight()/3,0,title_font,title_color);
		drawString("Press "+getKeyDesc(key_startgame)+" to start",
			pfWidth()/2,6*pfHeight()/10,0,title_font,title_color);
		drawString("Press "+getKeyDesc(key_gamesettings)+" for settings",
			pfWidth()/2,7*pfHeight()/10,0,title_font,title_color);
	}
	/** The game is halted in pause mode, but the paintFrame is still done to
	* refresh the screen.  Default behaviour of paintFramePaused() is display
	* "Paused", "Press [key_pausegame] to continue" using title_font,
	* title_color */
	public void paintFramePaused() {
		setColor(title_bg_color);
		drawRect(pfWidth()/20,15*pfHeight()/36,18*pfWidth()/20,
			5*pfHeight()/36 + (int)getFontHeight(title_font), true,false);
		drawString("Paused",pfWidth()/2,16*pfHeight()/36,0,
			title_font,title_color);
		drawString("Press "+getKeyDesc(key_pausegame)+" to continue",
			pfWidth()/2,19*pfHeight()/36,0, title_font,title_color);
	}
	/** Default displays "Level "+(stage+1). */
	public void paintFrameStartLevel() {
		drawString("Level "+(stage+1),
			pfWidth()/2,3*pfHeight()/5,0,title_font,title_color);
	}
	/** Default displays "Start !". */
	public void paintFrameStartGame() {
		drawString("Start !",
			pfWidth()/2,pfHeight()/3,0,title_font,title_color);
	}
	/** Default displays "Level Done !". */
	public void paintFrameLevelDone() {
		drawString("Level Done !",
			pfWidth()/2,pfHeight()/3,0,title_font,title_color);
	}
	/** Default displays "Life Lost !". */
	public void paintFrameLifeLost() {
		drawString("Life Lost !",
			pfWidth()/2,pfHeight()/3,0,title_font,title_color);
	}
	/** Default displays "Game Over!". */
	public void paintFrameGameOver() {
		drawString("Game Over !",
			pfWidth()/2,pfHeight()/3,0,title_font,title_color);
	}


	/* handy game functions */

	/** Returns true every increment ticks, but only when gametime is between
	* min_time and max_time. */
	public boolean checkTime(int min_time,int max_time,int increment) {
		return gametime>min_time && gametime<max_time &&(gametime%increment)==1;
	}
	/** Returns true every increment ticks. */
	public boolean checkTime(int increment) {
		return (gametime%increment)==1;
	}

	/* handy draw and effects functions */

	/** Draw a row of objects to indicate the value count.  This is typically
	 * used to indicate lives left. */
	public void drawCount(int count, String image,int x,int y,int increment_x) {
		if (increment_x < 0) x += increment_x;
		for (int i=0; i<count; i++) drawImage(x + i*increment_x, y, image);
	}
	/** Draw a string with letters that move up and down individually. */
	public void drawWavyString(String s, int x,int y,int align,int increment_x,
	int tmr,double amplitude, double pos_phaseshift, double timer_phaseshift,
	Font font, Color col) {
		setFont(font);
		setColor(col);
		if (align==0) {
			x -= increment_x*s.length()/2;
		} else if (align==1) {
			x -= increment_x*s.length();
		}
		for (int i=0; i<s.length(); i++)
			drawString(s.substring(i,i+1), x + i*increment_x,
				y + (int)(amplitude * 
				-Math.cos(Math.PI*(pos_phaseshift*i + tmr*timer_phaseshift))
				), 0);
	}

	/** Draw a String that zooms in and out. Alignment is always center. Note
	 * that tmr = 0 will start the font zooming in. */
	//public void drawZoomString(String s,int x,int y,
	//int tmr, double min_size_fac, double speed, Font font, Color col) {
	//	drawString(s,x,y,0,zoomed,col);
	//}

	/** Get font for zooming text in and out. Note that tmr = 0 will start
	* the font zooming in from the farthest position. */
	public Font getZoomingFont(Font basefont, int tmr,
	double min_size_fac, double speed) {
		double origsize = basefont.getSize2D();
		return basefont.deriveFont((float)( origsize*(min_size_fac + 
			0.5 - 0.5*Math.cos(Math.PI*speed*tmr) ) ));
	}

	/** Get a colour from a colour cycle. */
	public Color cycleColor(Color [] cycle, int tmr, double speed) {
		return cycle[ ( (int)(tmr*speed) ) % cycle.length ];
	}

	/** Walk across the screen, standing still momentarily at a specific
	* position. */
	public int posWalkForwards(int begin_pos, int end_pos, int tmr,
	int end_time,int standstill_pos,int standstill_time,int standstill_count){
		if (tmr < standstill_time) {
			double step = (standstill_pos - begin_pos)/(double)standstill_time;
			return begin_pos + (int)(tmr*step);
		} else if (tmr>=standstill_time&&tmr<standstill_time+standstill_count){
			return standstill_pos;
		} else if (tmr >= standstill_time+standstill_count && tmr < end_time) {
			int beg2_time = standstill_time + standstill_count;
			double step=(end_pos-standstill_pos)/(double)(end_time - beg2_time);
			return standstill_pos + (int)((tmr-beg2_time) * step);
		} else {
			return end_pos;
		}
	}
}
