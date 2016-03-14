package jgame;
import java.awt.*;
import java.awt.geom.*;
import java.awt.font.*;
import java.awt.image.*;
import java.applet.*;
import java.awt.event.*;
import javax.swing.ListCellRenderer;
import javax.swing.JList;
import java.net.*;
import java.util.*;
import java.io.*;
import java.lang.reflect.*;
/** Contains the main functionality of the game engine.  Subclass it to write
 * your own game.  The engine can be run as both an applet and an application.
 * To run as an application, have your main() construct an instance of your
 * subclass. Then call initEngine(), which will open a window, and initialise.
 * To run as an applet, ensure that your subclass has a parameterless
 * constructor which enables the applet viewer to create it as an applet.
 * Within this constructor, you call initEngineApplet() to initialise.

 * <P> Upon initialisation, the engine will create a new thread in which all
 * game action will run.  First it calls initGame() in your subclass.  Here,
 * you can define any initialisation code, such as variable initialisation and
 * image loading.  Then, the engine will start producing frames.  Each frame
 * it will call doFrame() in your subclass, where you can call engine
 * functions to move the game objects, check collisions, and everything else
 * you want to do in a frame.  Then, it will draw the current frame on the
 * screen, after which it calls paintFrame(), where you can do any customised
 * drawing on the frame, such as status messages.  The engine enables you to
 * specify <i>states</i> that the game may be in, say, the title screen state
 * or the in-game state.  When a state is set, the engine will first try to
 * call start<i>&lt;state&gt;</i>() once at the beginning of the next frame.
 * Then it will try to call additional doFrame<i>&lt;state&gt;</i>() and
 * paintFrame<i>&lt;state&gt;</i>() methods every frame, where you can handle
 * the game action for that specific state. Multple states may be set
 * simultaneously, resulting in multiple method calls for each frame.

 * <P> The engine manages a list of sprite objects, and a matrix of
 * equal-sized tiles. Its primary way to draw graphics is images. Images may
 * be loaded from Gifs, JPegs, or PNGs (PNGs not in java 1.2), which may
 * contain either single images, or regularly spaced arrays or matrices of
 * images (i.e. sprite sheets, we will call them image maps).  An image is
 * identified by a unique name, and is defined by the image file it comes from,
 * the index within that file in case the file is an image map, and any flip
 * and rotate actions that are to be be done on the file image to arrive at
 * the final image.  Animations may be defined in terms of sequences of
 * images.  Image maps, images, and animations can be defined by calling resp.
 * defineImageMap, defineImage, or defineAnimation, or the definitions can be
 * loaded from a text-file table file using defineImages().

 * <P>
 * Objects are of the class JGObject; see this class for more information.
 * Objects are identified within the engine by unique String identifiers.
 * Creating a new object with the same identifier as an old object will
 * replace the old one.  Objects are drawn on the screen in lexical order.
 * The main methods for handling objects are moveObjects(), checkCollision(),
 * and checkBGCollision().

 * <P>
 * Tiles can be used to define a background that the sprite objects can
 * interact with, using a set of shorthands for reading, writing, and
 * colliding with tiles.  Tiles are uniquely identified by a short string
 * (1-4 characters).  Tiles may be transparent; a decorative background
 * image can be displayed behind the tiles. 

 * <P> Collision is done by assigning collision IDs (cids) to both objects and
 * tiles. A cid is basically a bit string (usually, one allocates 1 bit per
 * basic object or tile type).  Collision is done by specifying that a certain
 * set of object types should be notified of collision with a certain set of
 * object or tile types.  Such a set is specified by a bit mask, which matches
 * a cid when cid&amp;mask != 0.  JGObject defines collision methods (hit and
 * hit_bg) which can then be implemented to handle the collisions.  Objects
 * have two bounding boxes: one for object-object collision (the sprite
 * bounding box, or simply bounding box), and one for object-tile collision
 * (the tile bounding box).  The two bounding boxes may be dependent on the
 * image (typically, you want these bounding boxes to approximate the shape of
 * each individual image).  For this purpose, you can define a collision
 * bounding box with each image definition.  This image bounding box is the
 * default bounding box used by the object, but both sprite bbox and tile bbox
 * can be overridden separately. Often, you want to set the tile bbox to a
 * constant value, such as equal to the size of 1 tile.

 * <P> The engine supports arbitrary runtime scaling of the playfield.  This
 * means that you can code your game for a specific "virtual" screen size, and
 * have it scale to any other screen size by simply supplying the desired
 * "real" screen size at startup.  Applets will scale to the size as given by
 * the width and height fields in the HTML code.  The scale factor will in no
 * way affect the behaviour of the game (except performance-wise), but the
 * graphics may look a bit blocky or jaggy, as the engine uses a
 * translucency-free scaling algorithm to ensure good performance.

 * <p>The engine supports keyboard and mouse input.  The state of the keyboard
 * is maintained in a keymap, which can be read by getKey. The mouse can be
 * read using getMouseButton and getMouseX/Y.  The mouse buttons also have
 * special keycodes. 

 * <p>Sound clips can be loaded using defineAudio or by appropriate entries in
 * a table loaded by defineMedia.  playAudio and stopAudio can be used to
 * control clip playing.  enableAudio and disableAudio can be used to globally
 * turn audio on and off for the entire application.

 * <p>Upon initialisation, the engine shows an initialisation screen with a
 * progress bar that reflects progress on the current graphics table being
 * loaded.  A splash image can be displayed during the init screen, by
 * defining an image called splash_image.  As soon as this image is defined,
 * it is displayed above the progress message.  Typically, one defines
 * splash_image at the beginning of the graphics table, so that it displays as
 * soon as possible during the init screen.

 * <p>JGame applications can be quit by pressing Shift-Esc.

 * <p>JGame has a number of debug features. It is possible to display the game
 * state and the JGObjects' bounding boxes.  There is also the possibility to
 * output debug messages, using dbgPrint.  A debug message is associated with
 * a specific source (either a game object or the mainloop).  Generated
 * exceptions will generally be treated as debug messages.  The messages can
 * be printed on the playfield, next to the objects that output them.  See
 * dbgSetMessagesInPf for more information.

 */
public abstract class JGEngine extends Applet {

	/* debug stuff */

	static int debugflags = 8;
	static final int BBOX_DEBUG = 1;
	static final int GAMESTATE_DEBUG = 2;
	static final int FULLSTACKTRACE_DEBUG = 4;
	static final int MSGSINPF_DEBUG= 8;

	private static int dbgframelog_expiry=80;
	private Font debugmessage_font = new Font("Arial",0,12);
	Color debug_auxcolor1 = Color.green;
	Color debug_auxcolor2 = Color.magenta;

	//private Hashtable dbgwatchlist = new Hashtable();
	//private boolean dbgwatchall = false;
	/** Watch all dbgOut sources, or go back to watching dbgOut sources
	 * selectively. */
	//public void setDebugWatchAll(boolean enable) {
	//	dbgwatchall = enable;
	//}
	/** Start or stop watching the given dbgOut source. */
	//public void setDebugWatch(String source,boolean enable) {
	//	if (enable) {
	//		dbgwatchlist.put(source, new Object());
	//	} else {
	//		dbgwatchlist.remove(source);
	//	}
	//}

	private Hashtable dbgframelogs = new Hashtable(); // old error msgs
	private Hashtable dbgnewframelogs = new Hashtable(); // new error msgs
	/** flags indicating messages are new */
	private Hashtable dbgframelogs_new = new Hashtable();
	/** objects that dbgframes correspond to (JGObject) */
	private Hashtable dbgframelogs_obj = new Hashtable();
	/** time that removed objects are dead (Integer) */
	private Hashtable dbgframelogs_dead = new Hashtable();

	/** Refresh message logs for this frame. */
	private void refreshDbgFrameLogs() {
		dbgframelogs_new = new Hashtable(); // clear "new" flag
		for (Enumeration e=dbgnewframelogs.keys(); e.hasMoreElements();) {
			String source = (String) e.nextElement();
			Object log = dbgnewframelogs.get(source);
			dbgframelogs.put(source,log);
			dbgframelogs_new.put(source,"yes");
		}
		dbgnewframelogs = new Hashtable();
	}

	/** paint the messages */
	void paintDbgFrameLogs(Graphics g) {
		g.setFont(debugmessage_font);
		for (Enumeration e=dbgframelogs.keys(); e.hasMoreElements();) {
			String source = (String) e.nextElement();
			Vector log = (Vector) dbgframelogs.get(source);
			if (dbgframelogs_new.containsKey(source)) {
				// new message
				g.setColor(fg_color);
			} else {
				// message from previous frame
				g.setColor(debug_auxcolor1);
			}
			JGObject obj = getObject(source);
			if (obj==null) {
				// retrieve dead object
				obj = (JGObject) dbgframelogs_obj.get(source);
				// message from deleted object
				g.setColor(debug_auxcolor2);
				if (obj!=null) {
					// tick dead timer
					int deadtime=0;
					if (dbgframelogs_dead.containsKey(source)) 
						deadtime = ((Integer)dbgframelogs_dead.get(source))
							.intValue();
					if (deadtime < dbgframelog_expiry) {
						dbgframelogs_dead.put(source,new Integer(deadtime+1));
					} else {
						dbgframelogs_obj.remove(source);
						dbgframelogs_dead.remove(source);
					}
				}
			}
			int lineheight = debugmessage_font.getSize()+1;
			if (obj!=null) {
				Point scaled = canvas.scalePos(obj.x,obj.y+lineheight/3);
				scaled.y -= lineheight*log.size();
				for (Enumeration f=log.elements(); f.hasMoreElements(); ) {
					g.drawString((String)f.nextElement(),scaled.x,scaled.y);
					scaled.y += lineheight;
				}
			} else {
				if (!source.equals("MAIN")) {
					dbgframelogs.remove(source);
				} else {
					if (dbgframelogs_new.containsKey(source)) {
						// new message
						g.setColor(fg_color);
					} else {
						// message from previous frame
						g.setColor(debug_auxcolor1);
					}
					int ypos = canvas.scaleYPos(pfHeight());
					ypos -= lineheight*log.size();
					for (Enumeration f=log.elements(); f.hasMoreElements(); ) {
						g.drawString((String)f.nextElement(),0,ypos);
						ypos += lineheight;
					}
				}
			}
		}
	}

	/** Show bounding boxes around the objects: the image bounding box
	 * (getBBox) , the tile span (getTiles), and the center tiles
	 * (getCenterTiles).  */
	public static void dbgShowBoundingBox(boolean enabled) {
		if (enabled) debugflags |=  BBOX_DEBUG;
		else         debugflags &= ~BBOX_DEBUG;
	}

	/** Show the game state in the bottom right corner of the screen. The
	 * message font and foreground colour are used to draw the text. */
	public static void dbgShowGameState(boolean enabled) {
		if (enabled) debugflags |=  GAMESTATE_DEBUG;
		else         debugflags &= ~GAMESTATE_DEBUG;
	}

	/** Indicates whether to show full exception stack traces or just the
	 * first lines.  Default is false.  */
	public static void dbgShowFullStackTrace(boolean enabled) {
		if (enabled) debugflags |=  FULLSTACKTRACE_DEBUG;
		else         debugflags &= ~FULLSTACKTRACE_DEBUG;
	}

	/** Output messages on playfield instead of console. Default is true.
	 * Messages printed by an object are displayed close to that object.
	 * Messages printed by the main program are shown at the bottom of the
	 * screen.  The debug message font is used to display the messages.
	 * <p>A message that is generated in this frame is shown in the foreground
	 * colour at the appropriate source.  If the source did not generate a
	 * message, the last printed message remains visible, and is shown in
	 * debug colour 1.  If an object prints a message, and then dies, the
	 * message will remain for a period of time after the object is gone.
	 * These messages are shown in debug colour 2.
	 */
	public void dbgShowMessagesInPf(boolean enabled) {
		if (enabled) debugflags |=  MSGSINPF_DEBUG;
		else         debugflags &= ~MSGSINPF_DEBUG;
	}

	/** Set the number of frames a debug message of a removed object should
	 * remain on the playfield. */
	public void dbgSetMessageExpiry(int ticks) {dbgframelog_expiry = ticks;}

	/** Set the font for displaying debug messages. */
	public void dbgSetMessageFont(Font font) { debugmessage_font=font; }

	/** Set debug color 1, used for printing debug information. */
	public void dbgSetDebugColor1(Color col) { debug_auxcolor1=col; }

	/** Set debug color 2, used for printing debug information. */
	public void dbgSetDebugColor2(Color col) { debug_auxcolor2=col; }


	/** Print a debug message, with the main program being the source. */
	public void dbgPrint(String msg) { dbgPrint("MAIN",msg); }

	/** Print a debug message from a specific source, which is either the main
	 * program or a JGObject.
	* @param source  may be object ID or "MAIN" for the main program. */
	public void dbgPrint(String source,String msg) {
		if ((debugflags&MSGSINPF_DEBUG)!=0) {
			Vector log = (Vector)dbgnewframelogs.get(source);
			if (log==null) log = new Vector(5,15);
			if (log.size() < 19) {
				log.add(msg);
			} else if (log.size() == 19) {
				log.add("<messages truncated>");
			}
			dbgnewframelogs.put(source,log);
			JGObject obj = getObject(source);
			if (obj!=null) { // store source object
				dbgframelogs_obj.put(source,obj);
				dbgframelogs_dead.remove(source);
			}
		} else {
			System.out.println(source+": "+msg);
		}
	}

	/** Print the relevant information of an exception as a debug message.
	* @param source  may be object ID or "MAIN" for the main program. */
	public void dbgShowException(String source, Throwable e) {
		ByteArrayOutputStream st = new ByteArrayOutputStream();
		e.printStackTrace(new PrintStream(st));
		if ((debugflags&FULLSTACKTRACE_DEBUG)!=0) {
			dbgPrint(source,st.toString());
		} else {
			StringTokenizer toker = new StringTokenizer(st.toString(),"\n");
			dbgPrint(source,toker.nextToken());
			dbgPrint(source,toker.nextToken());
			if (toker.hasMoreTokens())
				dbgPrint(source,toker.nextToken());
		}
	}

	/**Convert the relevant information of an exception to a multiline String.*/
	public String dbgExceptionToString(Throwable e) {
		ByteArrayOutputStream st = new ByteArrayOutputStream();
		e.printStackTrace(new PrintStream(st));
		if ((debugflags&FULLSTACKTRACE_DEBUG)!=0) {
			return st.toString();
		} else {
			StringTokenizer toker = new StringTokenizer(st.toString(),"\n");
			String ret = toker.nextToken()+"\n";
			ret       += toker.nextToken()+"\n";
			if (toker.hasMoreTokens())
				ret   += toker.nextToken();
			return ret;
		}
	}

	/** Report an exit message and exit; use for fatal errors.  The error is
	 * printed on the console.  In case of an application, the program exits.
	 * In case of an applet, destroy is called, and the error is displayed on
	 * the playfield. */
	public void exitEngine(String msg) {
		System.err.println(msg);
		exit_message=msg;
		System.err.println("Exiting JGEngine.");
		if (!i_am_applet) System.exit(0);
		destroy();
	}

	/** Path from where files can be loaded; null means not initialised yet */
	private Thread thread=null;
	JGCanvas canvas=null;
	private JGListener listener = new JGListener();
	private double fps = 35;
	private double maxframeskip = 4.0; /* max # of frames to skip  */
	private long target_time=0; /* time at which next frame should start */
	/** Should engine thread run or halt? Set by start() / stop()*/
	boolean running=true;
	/** Engine game state */
	Vector gamestate=new Vector(10,20);
	/** New engine game state to be assigned at the next frame */
	Vector gamestate_nextframe=new Vector(10,20);
	/** New game states which the game has to transition to, and for which
	 * start[state] have to be called. */
	Vector gamestate_new=new Vector(10,20);
	/** indicates when engine is inside a parallel object update (moveObjects,
	 * check*Collision) */
	boolean in_parallel_upd=false;
	private Vector timers = new Vector(20,40);


	/** signals that JGame globals are set, and exit code should null globals in
	* JGObject */
	boolean is_inited=false;
	/** signals that thread should die and canvas should paint exit message */
	boolean is_exited=false;
	String exit_message="JGEngine exited";

	/** True means the game is running as an applet, false means
	 * application. */
	public boolean i_am_applet=false;

	/** Keycode of cursor key. */
	public static final int KeyUp=38,KeyDown=40,KeyLeft=37,KeyRight=39;
	public static final int KeyShift=16;
	public static final int KeyCtrl=17;
	public static final int KeyAlt=18;
	public static final int KeyEsc=27;
	public static final int KeyEnter=10;
	/** Keymap equivalent of mouse button. */
	public static final int KeyMouse1=256, KeyMouse2=257, KeyMouse3=258;
	
	/* playfield dimensions */

	/** width/height after scaling to screen; 0 is not initialised yet */
	int width=0,height=0; 
	int nrtilesx,nrtilesy;
	int tilex,tiley;
	Window my_win=null;
	Frame my_frame=null;

	Color fg_color = Color.white;
	Color bg_color = Color.black;
	Font msg_font=null;

	Graphics buf_gfx=null;

	Hashtable animations = new Hashtable();

	/** Construct engine, but do not initialise it yet. 
	* Call initEngine or initEngineApplet to initialise the engine. */
	public JGEngine() {}

	/** Init engine as applet; call this in your engine constructor.  Applet
	 * init() will start the game.
	 * @param nrtilesx  nr of tiles horizontally
	 * @param nrtilesy  nr of tiles vertically
	 * @param tilex  width of one tile
	 * @param tiley  height of one tile
	 * @param fgcolor pen/text colour, null for default white
	 * @param bgcolor background colour, null for default black
	 * @param msgfont font for messages and text drawing, null for default
	 */
	public void initEngineApplet(int nrtilesx,int nrtilesy, int tilex,int tiley,
	Color fgcolor, Color bgcolor, Font msgfont){
		i_am_applet=true;
		this.nrtilesx=nrtilesx;
		this.nrtilesy=nrtilesy;
		this.tilex=tilex;
		this.tiley=tiley;
		setColorsFont(fgcolor,bgcolor,msgfont);
		// we get the width/height only after init is called
	}

	/** Init engine as application.  Passing (0,0) for width, height will
	 * result in a full-screen window without decoration.  Passing another
	 * value results in a regular window with decoration.
	 * @param nrtilesx  nr of tiles horizontally
	 * @param nrtilesy  nr of tiles vertically
	 * @param tilex  width of one tile
	 * @param tiley  height of one tile
	 * @param fgcolor pen/text colour, null for default white
	 * @param bgcolor background colour, null for default black
	 * @param msgfont font for messages and text drawing, null for default
	 * @param width  real screen width, 0 = use screen size
	 * @param height real screen height, 0 = use screen size */
	public void initEngine(int nrtilesx,int nrtilesy, int tilex,int tiley,
	Color fgcolor, Color bgcolor, Font msgfont,
	int width,int height) {
		i_am_applet=false;
		this.nrtilesx=nrtilesx;
		this.nrtilesy=nrtilesy;
		this.tilex=tilex;
		this.tiley=tiley;
		if (width==0) {
			Dimension scrsize = Toolkit.getDefaultToolkit().getScreenSize();
			this.width = scrsize.width;
			this.height = scrsize.height;
		} else {
			this.width=width;
			this.height=height;
		}
		setColorsFont(fgcolor,bgcolor,msgfont);
		createWindow(width!=0);
		init();
	}

	/** Call this to get focus. */
	public void requestGameFocus() {
		canvas.requestFocus();
	}

	/** Are we running as an applet or as an application? */
	public boolean isApplet() { return i_am_applet; }

	/** Get the virtual width in pixels (not the scaled screen width) */
	public int pfWidth() { return nrtilesx*tilex; }
	/** Get the virtual height in pixels (not the scaled screen height) */
	public int pfHeight() { return nrtilesy*tiley; }

	/** Get the number of tiles in X direction */
	public int pfTilesX() { return nrtilesx; }
	/** Get the number of tiles in Y direction */
	public int pfTilesY() { return nrtilesy; }

	/** Get the tile width in (virtual) pixels. */
	public int pfTileWidth()  { return tilex; }
	/** Get the tile height in (virtual) pixels. */
	public int pfTileHeight() { return tiley; }

	/** Initialise engine; don't call directly.  This is supposed to be called
	 * by the applet viewer or the initer.
	 */
	public void init() {
		//setAudioLatency(getAudioLatencyPlatformEstimate());
		if (width==0) {
			// get width/height from applet dimensions
			width=getWidth();
			height=getHeight();
		}
		canvas = new JGCanvas(this, nrtilesx,nrtilesy,tilex,tiley,width,height);
		//getAudioClip(getFullPath("sounds/mysound")).play();
		// set bg color so that the canvas's padding is in the proper color
		canvas.setBackground(bg_color);
		if (my_win!=null) my_win.setBackground(bg_color);
		// determine default font size (unscaled)
		msg_font = new Font("Helvetica",0,(int)(16.0/(640.0/(tilex*nrtilesx))));
		setLayout(new FlowLayout(FlowLayout.LEADING,0,0));
		add(canvas);
		if (!JGObject.setEngine(this,canvas)) {
			/** yes, you see that right.  I've used a random interface with a
			 * method that allows me to pass a Graphics.  We shall move this
			 * stuff to JGCanvas later, i suppose */
			canvas.setInitPainter(new ListCellRenderer () {
				public Component getListCellRendererComponent(JList d1,
				Object value, int d2, boolean initialise, boolean d4) {
					Graphics g = (Graphics) value;
					//if (initialise) {
					//	g.setColor(bg_color);
					//	g.fillRect(0,0,width,height);
					//}
					setFont(g,msg_font);
					g.setColor(fg_color);
					drawString(g,"JGame is already running in this VM",
						pfWidth()/2,pfHeight()/3,0);
					return null;
				} } );
			return;
		}
		is_inited=true;
		canvas.setInitPainter(new ListCellRenderer () {
			public Component getListCellRendererComponent(JList d1,
			Object value, int d2, boolean initialise, boolean d4) {
				Graphics g = (Graphics) value;
				//if (initialise) {
				//	g.setColor(bg_color);
				//	g.fillRect(0,0,width,height);
				//}
				setFont(g,msg_font);
				g.setColor(fg_color);
				Image splash = canvas.existsImage("splash_image") ?
						getImage("splash_image")  :  null;
				if (splash!=null) {
					Dimension splash_size=getImageSize("splash_image");
					canvas.drawImage(g,pfWidth()/2-splash_size.width/2,
						pfHeight()/4-splash_size.height/2,"splash_image");
				}
				drawString(g,"Please wait, loading files .....",
					pfWidth()/2,pfHeight()/2,0);
				//if (canvas.progress_message!=null) {
					//drawString(g,canvas.progress_message,
					//		pfWidth()/2,2*pfHeight()/3,0);
				//}
				// paint the right hand side black in case the bar decreases
				g.setColor(bg_color);
				drawRect(g,(int)(pfWidth()*(0.1+0.8*canvas.progress_bar)),
						(int)(pfHeight()*0.6),
						(int)(pfWidth()*0.8*(1.0-canvas.progress_bar)),
						(int)(pfHeight()*0.05), true,false);
				// left hand side of bar
				g.setColor(fg_color);
				drawRect(g,(int)(pfWidth()*0.1), (int)(pfHeight()*0.6),
						(int)(pfWidth()*0.8*canvas.progress_bar),
						(int)(pfHeight()*0.05), true,false);
				// length stripes
				drawRect(g,(int)(pfWidth()*0.1), (int)(pfHeight()*0.6),
						(int)(pfWidth()*0.8),
						(int)(pfHeight()*0.008), true,false);
				drawRect(g,(int)(pfWidth()*0.1), (int)(pfHeight()*(0.6+0.046)),
						(int)(pfWidth()*0.8),
						(int)(pfHeight()*0.008), true,false);
				return null;
			} } );
		//addKeyListener(listener); //this was here until 200608
		if (my_win!=null) {
			my_win.setVisible(true);
			my_win.validate();
		}
		// initialise keyboard handling
		canvas.addKeyListener(listener);
		canvas.requestFocus();
		thread = new Thread(new JGEngineThread());
		thread.start();
	}

	/** Override to define your own initialisations.  This method
	* is called by the game thread after initEngine() or initEngineApplet()
	* was called. */
	abstract public void initGame();

	/** Signal that the engine should start running. May be called by the web
	 * browser. */
	public void start() { running=true; }
	/** signal that the engine should stop running and wait. May be called by
	* the web browser.*/
	public void stop() { running=false; }

	/** Make engine call start() when a key is pressed.  This can be used to
	* determine a start criterion when halting the engine from within using
	* stop().
	* @param key  keycode to wake up on, -1=any key or mouse, 0=none */
	public void wakeUpOnKey(int key) { canvas.wakeup_key=key; }

	/** Destroy function for deinitialising the engine properly.  In
	* particular, this is called by the applet viewer to dispose the applet. */
	public void destroy() {
		// kill game thread
		is_exited=true;
		// applets cannot interrupt threads; their threads will 
		// be destroyed for them (not always, though ...).
		if (thread!=null) {
			if (!i_am_applet) thread.interrupt();
			try {
				thread.join(2000); // give up after 2 sec
			} catch (InterruptedException e) {
				e.printStackTrace();
				// give up
			}
		}
		// remove frame??
		// close files?? that appears to be unnessecary
		// reset global variables
		if (is_inited) {
			JGObject.default_engine=null;
			JGObject.default_canvas=null;
		}
		System.out.println("JGame engine disposed.");
	}


	/** Set frame rate in frames per second, and maximum number of frames that
	 * may be skipped before displaying a frame again. Default is 35 frames
	 * per second, with a maxframeskip of 4.
	 * @param fps  frames per second, useful range 2...80
	 * @param maxframeskip  max successive frames to skip, useful range 0..10*/
	public void setFrameRate(double fps, double maxframeskip) {
		this.fps = fps;
		this.maxframeskip = maxframeskip;
	}

	/** Configure image rendering.  alpha_thresh is used to determine how a
	 * translucent image is converted to a bitmask image.  Alpha values below
	 * the threshold are set to 0, the others to 255. render_bg_col is used to
	 * render transparency for scaled images; it is the background colour that
	 * interpolations between transparent and non-transparent pixels are
	 * rendered to.  Currently, this has an effect in Jdk1.2 only. The default
	 * render_bg_col is null, meaning the global background colour is used.
	 * @param alpha_thresh  bitmask threshold, 0...255, default=128
	 * @param render_bg_col bg colour for render, null=use background colour
	 */
	public void setRenderSettings(int alpha_thresh,Color render_bg_col) {
		canvas.alpha_thresh=alpha_thresh;
		canvas.render_bg_color=render_bg_col;
	}

	/** Set global background colour, which is displayed in borders, and behind
	* transparent tiles if no BGImage is defined. */
	public void setBGColor(Color bgcolor) {
		if (canvas!=null) canvas.setBackground(bgcolor);
		if (my_win!=null) my_win.setBackground(bgcolor);
		bg_color=bgcolor; 
	}

	/** Set image to display behind transparent tiles.  Image size must be a
	 * multiple of the tile size. Passing null turns off background image; the
	 * background colour will be used instead.
	 * @param bgimg  image name, null=turn off background image */
	public void setBGImage(String bgimg) { canvas.setBGImage(bgimg); }

	/** Set global foreground colour, used for printing text and status
	 * messages.  It is also the default colour for painting */
	public void setFGColor(Color fgcolor) { fg_color=fgcolor;  }

	/** Set the (unscaled) message font, used for displaying status messages.
	* It is also the default font for painting.  */
	public void setMsgFont(Font msgfont) { msg_font = msgfont; }

	/** Set foreground and background colour, and message font in one go;
	* passing a null means ignore that argument. */
	public void setColorsFont(Color fgcolor,Color bgcolor,Font msgfont) {
		if (msgfont!=null) msg_font = msgfont;
		if (fgcolor!=null) fg_color = fgcolor;
		if (bgcolor!=null) bg_color = bgcolor;
	}

	/** 1x1 pixel image with transparent colour */
	private BufferedImage null_image = 
			new BufferedImage(1,1,BufferedImage.TYPE_INT_ARGB);
	/** Set mouse cursor, null means hide cursor */
	public void setCursor(Cursor cursor) {
		if (cursor==null) {
			canvas.setCursor(Toolkit.getDefaultToolkit().createCustomCursor(
					null_image, new Point(0,0), "hidden" ) );
		} else {
			canvas.setCursor(cursor);
		}
	}

	/** Remove all JGTimers still ticking in the system. */
	public void removeAllTimers() { timers.clear(); }

	void registerTimer(JGTimer timer) { timers.add(timer); }

	private void tickTimers() {
		for (int i=timers.size()-1; i>=0; i--) {
			JGTimer timer = (JGTimer)timers.get(i);
			if (timer.tick()) {
				timers.remove(timer);
			}
		}
	}


	/** Set the game's main state on the next frame.  Methods with the names
	 * doFrame&lt;state&gt; and paintFrame&lt;state&gt; will be called in
	 * addition to doFrame() and paintFrame(). Before the next frame,
	 * start&lt;state&gt; is called once.  Note that setGameState may actually
	 * set a state that's already set, in which case start&lt;state&gt; is not
	 * called. Also, if the setGameState is superseded by another setGameState
	 * within the same frame, the first setGameState is ignored. */
	public void setGameState(String state) {
		boolean already_in_state = inGameStateNextFrame(state);
		gamestate_nextframe.clear();
		gamestate_nextframe.add(state);
		gamestate_new.clear();
		if (!already_in_state) gamestate_new.add(state);
	}

	/** Add the given state to the game's existing state on the next frame.
	* The methods
	* doFrame&lt;state&gt; and paintFrame&lt;state&gt; will be called <i>in
	* addition to</i> the methods of any states already set. 
	* Before the next frame,
	* start&lt;state&gt; is called once.  Note that addGameState may actually
	* set a state that's already set, in which case start&lt;state&gt; is not
	* called.  
	*/
	public void addGameState(String state) {
		if (!inGameStateNextFrame(state)) {
			gamestate_nextframe.add(state);
			gamestate_new.add(state);
		}
	}

	/** Remove the given state from the game's existing state on the next
	* frame. */
	public void removeGameState(String state) {
		gamestate_nextframe.remove(state);
		gamestate_new.remove(state);
	}

	/** Set the game's main state to none, on the next frame.
	* Only doFrame() and paintFrame() will be called each frame. */
	public void clearGameState() { gamestate_nextframe.clear(); }


	/** Check if game is in given state. */
	public boolean inGameState(String state) {
		for (Enumeration e=gamestate.elements(); e.hasMoreElements(); ) {
			if ( ((String)e.nextElement()).equals(state) ) return true;
		}
		return false;
	}

	/** Check if game will be in given state the next frame. */
	public boolean inGameStateNextFrame(String state) {
		for (Enumeration e=gamestate_nextframe.elements();e.hasMoreElements();){
			if ( ((String)e.nextElement()).equals(state) ) return true;
		}
		return false;
	}

	/** Do some administration, call doFrame. */
	private void doFrameAll() {
		synchronized (canvas.objects) {
			// the first flush is needed to remove any objects that were created
			// in the main routine after the last moveObjects or checkCollision
			canvas.flushRemoveList();
			canvas.flushAddList();
			// tick timers before doing state transitions, because timers may
			// initiate new transitions.
			tickTimers();
			canvas.flushRemoveList();
			canvas.flushAddList();
			// the game state transition starts here
			gamestate = gamestate_nextframe;
			gamestate_nextframe = new Vector(10,20);
			gamestate_nextframe.addAll(gamestate);
			// we assume that state transitions will not initiate new state
			// transitions!
			invokeGameStateMethods("start",gamestate_new);
			gamestate_new.clear();
			canvas.flushRemoveList();
			canvas.flushAddList();
			try {
				doFrame();
			} catch (JGameError ex) {
				exitEngine(dbgExceptionToString(ex));
			} catch (Exception ex) {
				dbgShowException("MAIN",ex);
			}
			invokeGameStateMethods("doFrame",gamestate);
			canvas.frameFinished();
		}
	}

	private void invokeGameStateMethods(String prefix,Vector states) {
		for (Enumeration e=states.elements(); e.hasMoreElements(); ) {
			String state = (String) e.nextElement();
			tryMethod(this,prefix+state,new Object[]{});
			//try {
			//	Method met=getClass().getMethod(prefix+state, null);
			//	met.invoke(this,new Object[0]);
			//} catch (NoSuchMethodException ex) {
			//	// ignore when method is not defined
			//} catch (InvocationTargetException ex) {
			//	Throwable ex_t = ex.getTargetException();
			//	if (ex_t instanceof JGameError) {
			//		exitEngine(dbgExceptionToString(ex_t));
			//	} else {
			//		dbgShowException("MAIN",ex_t);
			//	}
			//} catch (IllegalAccessException ex) {
			//	System.err.println("Unexpected exception:");
			//	ex.printStackTrace();
			//}
		}
	}

	/** Is called every frame.  Override to define frame action.  Default is
	 * do nothing. */
	public void doFrame() {}

	void paintFrame(Graphics g) {
		buf_gfx=g;
		g.setColor(fg_color);
		setFont(msg_font);
		try {
			paintFrame();
		} catch (JGameError ex) {
			exitEngine(dbgExceptionToString(ex));
		} catch (Exception ex) {
			dbgShowException("MAIN",ex);
		}
		invokeGameStateMethods("paintFrame",gamestate);
		if ((debugflags&GAMESTATE_DEBUG)!=0) {
			String state="{";
			for (Enumeration e=gamestate.elements(); e.hasMoreElements(); ) {
				state += (String)e.nextElement();
				if (e.hasMoreElements()) state +=",";
			}
			state += "}";
			setFont(msg_font);
			g.setColor(fg_color);
			drawString(state,pfWidth(),
					pfHeight()-(int)getFontHeight(g,msg_font), 1 );
		}
		if ((debugflags&MSGSINPF_DEBUG)!=0) paintDbgFrameLogs(buf_gfx);
		buf_gfx=null;
	}

	/** Is called when the engine's default screen painting is finished,
	* and custom painting actions may be carried out. Can be used to display
	* status information or special graphics.  Default is do nothing. */
	public void paintFrame() {}

	/** Get the graphics context for drawing on the buffer during a
	 * paintFrame().  Returns null when not inside paintFrame. */
	public Graphics getBufferGraphics() { return buf_gfx; }

	/** Get scale factor of real screen width wrt virtual screen width */
	public double getXScaleFactor() { return canvas.x_scale_fac; }
	/** Get scale factor of real screen height wrt virtual screen height */
	public double getYScaleFactor() { return canvas.y_scale_fac; }
	/** Get minimum of the x and y scale factors */
	public double getMinScaleFactor() { return canvas.min_scale_fac; }

	/* some convenience functions for drawing during repaint and paintFrame()*/

	/** Set current drawing colour. */
	public void setColor(Color col) {
		if (buf_gfx!=null) buf_gfx.setColor(col);
	}

	/** Set current font, scale the font to screen size. */
	public void setFont(Font font) { setFont(buf_gfx,font); }

	/** Set current font, scale the font to screen size. */
	public void setFont(Graphics g,Font font) {
		if (canvas!=null && g!=null) {
			double origsize = font.getSize2D();
			font=font.deriveFont((float)(origsize*canvas.min_scale_fac));
			g.setFont(font);
		}
	}

	/** Set the line thickness */
	public void setStroke(double thickness) {
		Graphics2D g = (Graphics2D) buf_gfx;
		g.setStroke(new BasicStroke((float)(thickness*canvas.min_scale_fac)));
	}

	/* convert all coordinates to double?? */

	/** Double version of drawLine combined with thickness/colour setting;
	* doubles are rounded to int. */
	public void drawLine(double x1,double y1,double x2,double y2,
	double thickness, Color color) {
		setColor(color);
		setStroke(thickness);
		drawLine((int)x1,(int)y1,(int)x2,(int)y2);
	}
	/** Double version of drawLine; doubles are rounded to int. */
	public void drawLine(double x1,double y1,double x2,double y2) {
		if (buf_gfx==null) return;
		buf_gfx.drawLine(canvas.scaleXPos((int)x1),canvas.scaleYPos((int)y1),
				   canvas.scaleXPos((int)x2),canvas.scaleYPos((int)y2) );
	}
	/** Integer version of drawLine. */
	public void drawLine(int x1,int y1,int x2,int y2) {
		if (buf_gfx==null) return;
		buf_gfx.drawLine(canvas.scaleXPos(x1),canvas.scaleYPos(y1),
				   canvas.scaleXPos(x2),canvas.scaleYPos(y2) );
	}


	/**
	* @param centered indicates (x,y) is center instead of topleft.
	*/
	public void drawRect(int x,int y,int width,int height, boolean filled,
	boolean centered, double thickness, Color color) {
		setColor(color);
		setStroke(thickness);
		drawRect(x,y,width,height,filled,centered);
	}

	/**
	* @param centered indicates (x,y) is center instead of topleft.
	*/
	public void drawRect(int x,int y,int width,int height, boolean filled,
	boolean centered) {
		if (buf_gfx==null) return;
		drawRect(buf_gfx,x,y,width,height,filled,centered);
	}
	void drawRect(Graphics g,int x,int y,int width,int height,
	boolean filled, boolean centered) {
		if (centered) {
			x -= (width/2);
			y -= (height/2);
		}
		Rectangle r = canvas.scalePos(new Rectangle(x,y,width,height));
		if (filled) {
			g.fillRect(r.x,r.y,r.width,r.height);
		} else {
			g.drawRect(r.x,r.y,r.width,r.height);
		}
	}

	/**
	* @param centered indicates (x,y) is center instead of topleft.
	*/
	public void drawOval(int x,int y,int width,int height, boolean filled,
	boolean centered, double thickness, Color color) {
		setColor(color);
		setStroke(thickness);
		drawOval(x,y,width,height,filled,centered);
	}

	/**
	* @param centered indicates (x,y) is center instead of topleft.
	*/
	public void drawOval(int x,int y, int width,int height,boolean filled,
	boolean centered) {
		if (buf_gfx==null) return;
		x = canvas.scaleXPos(x);
		y = canvas.scaleYPos(y);
		width = canvas.scaleXPos(width);
		height = canvas.scaleYPos(height);
		if (centered) {
			x -= (width/2);
			y -= (height/2);
		}
		if (filled) {
			buf_gfx.fillOval(x,y,width,height);
		} else {
			buf_gfx.drawOval(x,y,width,height);
		}
	}

	/** Draw image with given ID. */
	public void drawImage(int x,int y,String imgname) {
		if (buf_gfx==null) return;
		canvas.drawImage(buf_gfx,x,y,imgname);
	}

	/** Draws string so that (x,y) is the topleft coordinate (align=-1), the
	 * top middle coordinate (align=0), or the top right coordinate (align=1).
	 * Use current font and colour.
	 * @param align  text alignment, -1=left, 0=center, 1=right */
	public void drawString(String str, int x, int y, int align) {
		if (buf_gfx==null) return;
		drawString(buf_gfx, str, x,y, align);
	}

	/** Draws string so that (x,y) is the topleft coordinate (align=-1), the
	 * top middle coordinate (align=0), or the top right coordinate (align=1).
	 * Use given font and colour; filling in null for either means ignore.
	 * @param align  text alignment, -1=left, 0=center, 1=right */
	public void drawString(String str, int x, int y, int align,
	Font font, Color color) {
		if (font!=null) setFont(font);
		if (color!=null) setColor(color);
		drawString(buf_gfx, str, x,y, align);
	}

	void drawString(Graphics g, String str, int x, int y, int align){
		if (g==null) return;
		Point p = canvas.scalePos(x,y);
		Font font = g.getFont();
		FontRenderContext fontrc = ((Graphics2D)g).getFontRenderContext();
		//Rectangle2D fontbounds = font.getMaxCharBounds(fontrc);
		//Rectangle2D stringbounds = getStringBounds(str, fontrc);
		TextLayout layout = new TextLayout(str, font, fontrc);
		Rectangle2D strbounds = layout.getBounds();
		if (align==-1) {
			g.drawString(str,
					(int)(p.x-strbounds.getMinX()),
					(int)(p.y-strbounds.getMinY()));
		} else if (align==0) {
			g.drawString(str,
					(int)(p.x-strbounds.getCenterX()),
					(int)(p.y-strbounds.getMinY()));
		} else {
			g.drawString(str,
					(int)(p.x-strbounds.getMaxX()),
					(int)(p.y-strbounds.getMinY()));
		}
	}

	/** Get height of given font in pixels. */
	public double getFontHeight(Font font) {
		if (buf_gfx!=null) return getFontHeight(buf_gfx,font);
		return 0.0;
	}
	/** Get height of given font or current font in pixels. */
	double getFontHeight(Graphics g,Font font) {
		if (font==null) font=g.getFont();
		FontRenderContext fontrc = ((Graphics2D)g).getFontRenderContext();
		Rectangle2D fontbounds = font.getMaxCharBounds(fontrc);
		return fontbounds.getHeight();
		//return fontbounds.getHeight() / canvas.y_scale_fac;
	}

	/** Draws a single line of text using an image map as font;
	* text alignment is same as drawString.  Typically, an image font only
	* defines the ASCII character range 32-96.  In this case, set char_offset
	* to 32, and use only the uppercase letters.
	* @param align  text alignment, -1=left, 0=center, 1=right
	* @param imgmap  name of image map
	* @param char_offset  ASCII code of first image of image map
	* @param spacing  number of pixels to skip between letters
	*/
	public void drawImageString(String string, int x, int y, int align,
	String imgmap, int char_offset, int spacing) {
		JGCanvas.JGImageMap map = (JGCanvas.JGImageMap)
			canvas.imagemaps.get(imgmap);
		if (map==null) throw new JGameError(
				"Font image map '"+imgmap+"' not found.",true );
		if (align==0) {
			x -= (map.tilex+spacing) * string.length()/2;
		} else if (align==1) {
			x -= (map.tilex+spacing) * string.length();
		}
		//Image img = map.getScaledImage();
		for (int i=0; i<string.length(); i++) {
			int imgnr = -char_offset+string.charAt(i);
			//Point coord = map.getImageCoord(imgnr);
			String lettername = imgmap+"#"+string.charAt(i);
			//System.out.println(lettername);
			//System.out.println("N"+(letter==null));
			if (!canvas.existsImage(lettername)) {
				canvas.defineImage(lettername, "FONT", 0,
					canvas.getSubImage(imgmap,imgnr),
					"-", 0,0,0,0);
			}
			Image letter = canvas.getImage(lettername);
			canvas.drawImage(buf_gfx, x,y,lettername);
			//Point scaledtl = canvas.scalePos(x, y);
			//Point scaledbr = canvas.scalePos(x+map.tilex, y+map.tiley);
			//buf_gfx.drawImage(map.img,
			//	scaledtl.x, scaledtl.y, scaledbr.x, scaledbr.y,
			//	coord.x, coord.y, coord.x+map.tilex, coord.y+map.tiley, null);
			//buf_gfx.drawImage(map.img,
			//	x, y, x+map.tilex, y+map.tiley,
			//	coord.x, coord.y, coord.x+map.tilex, coord.y+map.tiley, null);
			x += map.tilex + spacing;
		}
	}

	/** Get current mouse position */
	public Point getMousePos() { return new Point(canvas.mousepos); }
	/** Get current mouse X position */
	public int getMouseX() { return canvas.mousepos.x; }
	/** Get current mouse Y position */
	public int getMouseY() { return canvas.mousepos.y; }

	/** Get state of button.
	* @param nr  1=button 1 ... 3 = button 3
	* @return true=pressed, false=released */
	public boolean getMouseButton(int nr) { return canvas.mousebutton[nr]; }
	/** Set state of button to released.
	* @param nr  1=button 1 ... 3 = button 3 */
	public void clearMouseButton(int nr) { canvas.mousebutton[nr]=false; }
	/** Set state of button to pressed.
	* @param nr  1=button 1 ... 3 = button 3 */
	public void setMouseButton(int nr) { canvas.mousebutton[nr]=true; }
	/** Check if mouse is inside game window */
	public boolean getMouseInside() { return canvas.mouseinside; }

	/** Get the key status of the given key. */
	public boolean getKey(int key) { return canvas.keymap[key]; }
	/** Set the key status of a key to released. */
	public void clearKey(int key) { canvas.keymap[key]=false; }
	/** Set the key status of a key to pressed. */
	public void setKey(int key) { canvas.keymap[key]=true; }

	/** Get a printable string describing the key. */
	public static String getKeyDesc(int key) {
		if (key==32) return "space";
		if (key==0) return "(none)";
		if (key==KeyEnter) return "enter";
		if (key==KeyEsc) return "escape";
		if (key==KeyUp) return "cursor up";
		if (key==KeyDown) return "cursor down";
		if (key==KeyLeft) return "cursor left";
		if (key==KeyRight) return "cursor right";
		if (key==KeyShift) return "shift";
		if (key==KeyAlt) return "alt";
		if (key==KeyCtrl) return "control";
		if (key==KeyMouse1) return "left mouse button";
		if (key==KeyMouse2) return "middle mouse button";
		if (key==KeyMouse3) return "right mouse button";
		if (key==27) return "escape";
		if (key >= 33 && key <= 95)
			return new String(new char[] {(char)key});
		return "keycode "+key;
	}

	/** Obtain key code from printable string describing the key, the inverse
	 * of getKeyDesc. The string is trimmed and lowercased. */
	public static int getKeyCode(String keydesc) {
		// tab, enter, backspace, insert, delete, home, end, pageup, pagedown
		// escape
		keydesc = keydesc.toLowerCase().trim();
		if (keydesc.equals("space")) {
			return 32;
		} else if (keydesc.equals("escape")) {
			return KeyEsc;
		} else if (keydesc.equals("(none)")) {
			return 0;
		} else if (keydesc.equals("enter")) {
			return KeyEnter;
		} else if (keydesc.equals("cursor up")) {
			return KeyUp;
		} else if (keydesc.equals("cursor down")) {
			return KeyDown;
		} else if (keydesc.equals("cursor left")) {
			return KeyLeft;
		} else if (keydesc.equals("cursor right")) {
			return KeyRight;
		} else if (keydesc.equals("shift")) {
			return KeyShift;
		} else if (keydesc.equals("alt")) {
			return KeyAlt;
		} else if (keydesc.equals("control")) {
			return KeyCtrl;
		} else if (keydesc.equals("left mouse button")) {
			return KeyMouse1;
		} else if (keydesc.equals("middle mouse button")) {
			return KeyMouse2;
		} else if (keydesc.equals("right mouse button")) {
			return KeyMouse3;
		} else if (keydesc.startsWith("keycode")) {
			return Integer.parseInt(keydesc.substring(7));
		} else if (keydesc.length() == 1) {
			return keydesc.charAt(0);
		}
		return 0;
	}

	/*====== animation ======*/

	class Animation implements Cloneable {
		/* settings;  the public ones may be manipulated freely */
		String [] frames;
		public double speed;
		public int increment=1;
		public boolean pingpong=false;
		/* state */
		int framenr=0;
		double phase=0.0;
		boolean running=true;
		public Animation (String [] frames, double speed) {
			this.frames=frames;
			this.speed=speed;
		}
		public Animation (String [] frames, double speed, boolean pingpong) {
			this.frames=frames;
			this.speed=speed;
			this.pingpong=pingpong;
		}
		public Animation (String [] frames, double speed, boolean pingpong,
		int increment) {
			this.frames=frames;
			this.speed=speed;
			this.pingpong=pingpong;
			this.increment=increment;
		}
		public void stop() { running=false; }
		public void start() { running=true; }
		public void reset() {
			framenr=0;
			phase=0.0;
		}
		public Object clone() {
			try {
				return super.clone();
			} catch (CloneNotSupportedException e) { return null; }
		}
		public Animation copy() {
			return (Animation) clone();
		}
		public String getCurrentFrame() {
			if (framenr < 0 || framenr >= frames.length) {
				return frames[0];
			} else {
				return frames[framenr];
			}
		}
		/** Does one animation step and returns current image.  Note that the
		 * function returns the frame before the state is being updated. */
		public String animate() {
			String ret = getCurrentFrame();
			if (running) {
				phase += speed;
				while (phase >= 1.0) {
					phase -= 1.0;
					framenr += increment;
					if (!pingpong) {
						if (framenr >= frames.length) framenr -= frames.length;
						if (framenr < 0)              framenr += frames.length;
					} else {
						if (framenr >= frames.length) {
							framenr -= 2*increment;
							increment = -increment;
						}
						if (framenr < 0) {
							framenr -= 2*increment;
							increment = -increment;
						}
					}
				}
			}
			return ret;
		}
	}

	/** Define new animation sequence. Speed must be &gt;= 0.
	* @param id  the name by which the animation is known
	* @param frames  an array of image names that should be played in sequence
	* @param speed the sequence speed: the number of animation steps per frame
	*/
	public void defineAnimation (String id,
	String [] frames, double speed) {
		animations.put(id, new Animation(frames,speed));
	}

	/** Define new animation sequence. Speed must be &gt;= 0.
	* @param id  the name by which the animation is known
	* @param frames  an array of image names that should be played in sequence
	* @param speed the sequence speed: the number of animation steps per frame
	* @param pingpong  true=play the images in forward order, then in reverse
	*/
	public void defineAnimation (String id,
	String [] frames, double speed, boolean pingpong) {
		animations.put(id, new Animation(frames,speed,pingpong));
	}

	Animation getAnimation(String id) {
		return (Animation) animations.get(id);
	}

	/*====== image ======*/

	/** Define image map, a large image containing a number of smaller images
	* to use for sprites or fonts.  The images must be in a regularly spaced
	* matrix.  One may define multiple image maps with the same image but
	* different matrix specs. 
	* @param mapname  id of image map
	* @param imgfile  filespec in resource path
	* @param xofs  x offset of first image 
	* @param yofs  y offset of first image
	* @param tilex  width of an image
	* @param tiley  height of an image
	* @param skipx  nr of pixels to skip between successive images
	* @param skipy  nr of pixels to skip between successive images vertically.
	*/
	public void defineImageMap(String mapname, String imgfile,
		int xofs,int yofs, int tilex,int tiley, int skipx,int skipy) {
		canvas.defineImageMap(mapname,imgfile, xofs,yofs, tilex,tiley,
			skipx,skipy);
	}

	/** Define new sprite/tile image from a file.  If an image with this 
	* id is already defined, it is removed from any caches, so that the old
	* image is really unloaded.  This can be used to load large (background)
	* images on demand, rather than have them all in memory.  Note that the
	* unloading does not work for images defined from image maps.
	* @param imgname  image id
	* @param tilename  tile id (1-4 characters)
	* @param collisionid  cid to use for tile collision matching
	* @param imgfile  filespec in resource path; "null" means no file
	* @param top  collision bounding box dimensions
	* @param left  collision bounding box dimensions
	* @param width  collision bounding box dimensions
	* @param height  collision bounding box dimensions
	*/
	public void defineImage(String imgname, String tilename, int collisionid,
	String imgfile, String img_op,
	int top,int left, int width,int height) {
		canvas.defineImage(imgname,tilename,collisionid,imgfile,
			img_op, top,left,width,height);
	}

	/** Define new sprite/tile image from a file, with collision bounding box
	* equal to the image's dimensions. If an image with this 
	* id is already defined, it is removed from any caches, so that the old
	* image is really unloaded.  This can be used to load large (background)
	* images on demand, rather than have them all in memory.  Note that the
	* unloading does not work for images defined from image maps.
	*
	* @param imgname  image id
	* @param tilename  tile id (1-4 characters)
	* @param collisionid  cid to use for tile collision matching
	* @param imgfile  filespec in resource path; "null" means no file
	*/
	public void defineImage(String imgname, String tilename, int collisionid,
	String imgfile, String img_op) {
		canvas.defineImage(imgname,tilename,collisionid,imgfile, img_op,
		-1,-1,-1,-1);
	}

	/** Define new sprite/tile image from map.
	* @param imgname  image id
	* @param tilename  tile id (1-4 characters)
	* @param collisionid  cid to use for tile collision matching
	* @param imgmap  id of image map
	* @param mapidx  index of image in map, 0=first
	* @param top  collision bounding box dimensions
	* @param left  collision bounding box dimensions
	* @param width  collision bounding box dimensions
	* @param height  collision bounding box dimensions
	*/
	public void defineImage(String imgname, String tilename, int collisionid,
	String imgmap, int mapidx, String img_op,
	int top,int left, int width,int height) {
		canvas.defineImage(imgname,tilename,collisionid,
			canvas.getSubImage(imgmap, mapidx),
			img_op, top,left,width,height);
	}

	/** Define new sprite/tile image from map, with collision bounding box
	* equal to the image's dimensions.
	* @param imgname  image id
	* @param tilename  tile id (1-4 characters)
	* @param collisionid  cid to use for tile collision matching
	* @param imgmap  id of image map
	* @param mapidx  index of image in map, 0=first
	*/
	public void defineImage(String imgname, String tilename, int collisionid,
	String imgmap, int mapidx, String img_op) {
		canvas.defineImage(imgname,tilename,collisionid,
			canvas.getSubImage(imgmap, mapidx), img_op, -1,-1,-1,-1);
	}

	/** This method does exactly the same as defineMedia.
	* @see #defineMedia(String)
	* @deprecated */
	public void defineGraphics(String filename) { defineMedia(filename); }

	/** Load a set of imagemap, image, animation, and audio clip definitions
	* from a file.
	* The file contains one image / imagemap / animation definition / audio
	* clip
	* on each line, with the fields separated by one or more tabs.  Lines not
	* matching the required number of fields are ignored.
	* The fields have the same order as in defineImage, defineImageMap, 
	* defineAnimation, and defineAudioClip. For example:
	* <p>
	* <code>defineImage("mytile", "#", 1,"gfx/myimage.gif", "-");</code>
	* <p>
	* is equivalent to the following line in the table:
	* <p>
	* <code>mytile &nbsp;&nbsp;&nbsp; # &nbsp;&nbsp;&nbsp;  1
	* &nbsp;&nbsp;&nbsp; gfx/myimage.gif &nbsp;&nbsp;&nbsp; -</code>
	* <p>
	* with the whitespace between the fields consisting of one or more tabs.
	* The defineAnimation methods take an array of names as the second
	* argument.  This is represented in table format as the names separated by
	* semicolon ';' characters.  So:
	* <P>
	* <code>defineAnimation("anim",new String[]{"frame0","frame1",...},0.5);
	* </code><p>
	* is equivalent to:
	* <p>
	* <code>anim &nbsp;&nbsp;&nbsp; frame0;frame1;... &nbsp;&nbsp;&nbsp; 0.5
	* </code>
	**/
	public void defineMedia(String filename) {
		int lnr=1;
		int nr_lines=0;
		try {
			// count nr of lines in file first
			BufferedReader in = new BufferedReader( new InputStreamReader(
				getClass().getClassLoader().getResourceAsStream( filename ) ));
			while (in.readLine() != null) nr_lines++;
			// now, read the file
			in = new BufferedReader( new InputStreamReader(
				getClass().getClassLoader().getResourceAsStream( filename ) ));
			String line;
			String [] fields = new String [14];
			while ( (line = in.readLine()) != null) {
				canvas.setProgressBar((double)lnr / (double)nr_lines);
				int i=0;
				StringTokenizer toker = new StringTokenizer(line,"\t");
				while (toker.hasMoreTokens()) {
					fields[i++] = toker.nextToken();
				}
				if (i==8) {
					canvas.defineImageMap(
						fields[0], fields[1],
						Integer.parseInt(fields[2]),
						Integer.parseInt(fields[3]),
						Integer.parseInt(fields[4]),
						Integer.parseInt(fields[5]),
						Integer.parseInt(fields[6]),
						Integer.parseInt(fields[7]) );
				} else if (i==9) {
					canvas.defineImage(fields[0],fields[1],
						Integer.parseInt(fields[2]),
						fields[3],
						fields[4],
						Integer.parseInt(fields[5]),
						Integer.parseInt(fields[6]),
						Integer.parseInt(fields[7]),
						Integer.parseInt(fields[8])  );
				} else if (i==5) {
					canvas.defineImage(fields[0],fields[1],
						Integer.parseInt(fields[2]),
						fields[3],
						fields[4], -1,-1,-1,-1  );
				} else if (i==10) {
					canvas.defineImage(fields[0],fields[1],
						Integer.parseInt(fields[2]),
						canvas.getSubImage(fields[3],
							Integer.parseInt(fields[4]) ),
						fields[5],
						Integer.parseInt(fields[6]),
						Integer.parseInt(fields[7]),
						Integer.parseInt(fields[8]),
						Integer.parseInt(fields[9])  );
				} else if (i==6) {
					canvas.defineImage(fields[0],fields[1],
						Integer.parseInt(fields[2]),
						canvas.getSubImage(fields[3],
							Integer.parseInt(fields[4]) ),
						fields[5], -1,-1,-1,-1  );
				} else if (i==3) {
					defineAnimation(fields[0], splitList(fields[1]), 
						Double.parseDouble(fields[2])  );
				} else if (i==4) {
					defineAnimation(fields[0], splitList(fields[1]),
						Double.parseDouble(fields[2]),
						fields[3].equals("true"));
				} else if (i==2) {
					defineAudioClip(fields[0], fields[1]);
				}
				lnr++;
			}
		} catch (JGameError e) {
			exitEngine("Error in "+filename+" line "+lnr+": "+e);
		} catch (Exception e) {
			exitEngine("Error in "+filename+" line "+lnr+":\n"
				+ dbgExceptionToString(e));
		}
		
	}

	void paintExitMessage(Graphics g) { try {
		g.setFont(debugmessage_font);
		int height = (int) (getFontHeight(g,null) / canvas.y_scale_fac);
		// clear background
		g.setColor(bg_color);
		drawRect(g, pfWidth()/2, pfHeight()/2,
			9*pfWidth()/10, height*5, true,true);
		g.setColor(debug_auxcolor2);
		// draw colour bars
		drawRect(g, pfWidth()/2, pfHeight()/2 - 5*height/2,
			9*pfWidth()/10, 5, true,true);
		drawRect(g, pfWidth()/2, pfHeight()/2 + 5*height/2,
			9*pfWidth()/10, 5, true,true);
		g.setColor(fg_color);
		int ypos = pfHeight()/2 - 3*height/2;
		StringTokenizer toker = new StringTokenizer(exit_message,"\n");
		while (toker.hasMoreTokens()) {
			drawString(g,toker.nextToken(),pfWidth()/2,ypos,0);
			ypos += height+1;
		}
	} catch(java.lang.NullPointerException e) {
		// this sometimes happens during drawString when the applet is exiting
		// but calls repaint while the graphics surface is already disposed.
		// See also bug 4791314:
		// http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=4791314
	} }

	private String [] splitList(String liststr) {
		Vector list = new Vector(10,20);
		for (StringTokenizer toker=new StringTokenizer(liststr,";");
		toker.hasMoreTokens(); ) {
			list.add(toker.nextToken());
		}
		String [] list_arr = new String [list.size()];
		int i=0;
		for (Enumeration e=list.elements(); e.hasMoreElements(); ) {
			list_arr[i] = (String) e.nextElement();
			i++;
		}
		return list_arr;
	}

	/** Gets (scaled) image directly. Is usually not necessary. */
	public Image getImage(String imgname) { return canvas.getImage(imgname); }
	/** Gets (non-scaled) image's physical size directly. */
	public Dimension getImageSize(String imgname) {
		return canvas.getImageSize(imgname);
	}

	/** Gets the collision bounding box of an image. */
	public Rectangle getImageBBox(String imgname) {
		return canvas.getImageBBox(imgname);
	}

	/* background methods */

	/** Set dimensions of background. This function has been disabled as
	 * public function.  It is for defining scrolling virtual screens.
	* @param tilex x size of tile
	* @param tiley y size of tile
	* @param virtualx  number of tiles in x direction
	* @param virtualy  number of tiles in y direction
	* @param filltile  tilename of tile to use as fill pattern
	*/
	//public void setBGDim(int tilex,int tiley, int virtualx,int virtualy,
	//String filltile) {
	//	canvas.setBGDim(tilex,tiley,virtualx,virtualy,filltile);
	//}

	/** Define background tile settings.  Default is setBGCidSettings("",0,0).
	* @param out_of_bounds_tile  tile string to use outside of screen bounds
	* @param out_of_bounds_cid  cid to use outside of screen boundaries
	* @param preserve_cids  cid mask to preserve when setting tiles */
	public void setTileSettings(String out_of_bounds_tile,
	int out_of_bounds_cid,int preserve_cids) {
		canvas.setTileSettings(out_of_bounds_tile,out_of_bounds_cid,
			preserve_cids);
	}

	/** Fill the background with the given tile.
	* @param filltilename null means use background colour */
	public void fillBG(String filltilename) {
		canvas.fillBG(filltilename);
	}

	/** Set the cid of a single tile to the given value, leaving the actual
	 * tile. */
	public void setTileCid(int x,int y,int value) {
		canvas.setTileCid(x,y,0,value);
	}

	/** Modify the cid of a single tile by ORing a bit mask, leaving the actual
	 * tile. */
	public void orTileCid(int x,int y,int or_mask) {
		canvas.setTileCid(x,y,-1,or_mask);
	}

	/** Modify the cid of a single tile by ORing a bit mask, leaving the actual
	 * tile. */
	public void andTileCid(int x,int y,int and_mask) {
		canvas.setTileCid(x,y,and_mask,0);
	}

	/** Set a single tile.
	*/
	public void setTile(int x,int y,String tilename) {
		canvas.setTile(x,y,tilename);
	}

	/** Set a single tile.
	*/
	public void setTile(Point tileidx,String tilename) {
		canvas.setTile(tileidx.x,tileidx.y,tilename);
	}

	/** Set a block of tiles according to the single-letter tile names in the
	* nxm character array tilemap.  
	*/
	public void setTiles(int xofs,int yofs,String [] tilemap) {
		for (int y=0; y<tilemap.length; y++) {
			for (int x=0; x<tilemap[y].length(); x++) {
				canvas.setTile(x+xofs,y+yofs,
					new String(tilemap[y].substring(x,x+1)) );
			}
		}
	}

	/** Set a block of tiles according to the tile names in the nxm element
	* array tilemap.  The tile names may be multiple characters.  Each String
	* in the tilemap consists of a list of tile names separated by spaces. So:
	* <code> "x aa ab abc"</code> stands for a sequence of four tiles, "x",
	* "aa", "ab", and "abc".
	*/
	public void setTilesMulti(int xofs,int yofs,String [] tilemap) {
		for (int y=0; y<tilemap.length; y++) {
			StringTokenizer toker = new StringTokenizer(tilemap[y]," ");
			int x=0;
			while (toker.hasMoreTokens()) {
				canvas.setTile(x+xofs,y+yofs, toker.nextToken());
				x++;
			}
		}
	}

	/** Get collision id of the tile at given pixel coordinates.
	*/
	public int getTileCidAtCoord(double x,double y) {
		int xidx = (int)x / canvas.tilex;
		int yidx = (int)y / canvas.tiley;
		return canvas.getTileCid(xidx,yidx);
	}
	/** Get collision id of tile at given tile index position. */
	public int getTileCid(int xidx,int yidx) {
		return canvas.getTileCid(xidx,yidx);
	}
	/** Get the tile cid of the point that is (xofs,yofs) from the tile index
	 * coordinate center. */
	public int getTileCid(Point center, int xofs, int yofs) {
		return canvas.getTileCid(center.x+xofs, center.y+yofs);
	}

	/** Get collision Id of the tile at given pixel coordinates.
	*/
	public String getTileStrAtCoord(double x,double y) {
		int xidx = (int)x / canvas.tilex;
		int yidx = (int)y / canvas.tiley;
		return canvas.getTileStr(xidx,yidx);
	}
	/** get string id of tile at given index position */
	public String getTileStr(int xidx,int yidx) {
		return canvas.getTileStr(xidx,yidx);
	}
	/** Get the tile string of the point that is (xofs,yofs) from the tile
	* index coordinate center. */
	public String getTileStr(Point center, int xofs, int yofs) {
		return canvas.getTileStr(center.x+xofs, center.y+yofs);
	}


	/** Get the OR of the cids at the tile indexes given by tiler */
	public int getTileCid(Rectangle tiler) {
		return canvas.getTileCid(tiler);
	}

	/** Count number of tiles with given mask. Actually searches all tiles, so
	 * it's inefficient and should be used sparingly (such as, determine
	 * the number of something at the beginning of a game). */
	public int countTiles(int tilecidmask) {
		return canvas.countTiles(tilecidmask);
	}

	/** Check if object exists.
	*/
	public boolean existsObject(String index) {
		return canvas.existsObject(index);
	}

	/** Get object if it exists, null if not.
	*/
	public JGObject getObject(String index) {
		return canvas.getObject(index);
	}


	/** Remove object or mark object for removal. */
	void removeObject(JGObject obj) {
		if (in_parallel_upd) { // queue remove
			canvas.markRemoveObject(obj);
		} else { // do remove immediately
			canvas.removeObject(obj);
		}
	}

	/** Remove all objects which have the given name prefix and/or match the
	* given cidmask.  The actual matching and removal is done after the
	* current moveObjects or check*Collision ends, or immediately if done
	* from within the main doFrame loop.  It also removes any matching
	* objects which are pending to be added the next frame.
	* @param cidmask collision id mask, 0 means ignore
	* @param prefix  ID prefix, null means ignore  */
	public void removeObjects(String prefix,int cidmask) {
		if (in_parallel_upd) {
			canvas.markRemoveObjects(prefix,cidmask);
		} else {
			canvas.removeObjects(prefix,cidmask);
		}
	}


	/** Count how many objects there are with both the given name prefix and
	* have colid&amp;cidmask != 0.  Either criterion can be left out. Actually
	* searches the object array, so it may be inefficient.
	* @param cidmask collision id mask, 0 means ignore
	* @param prefix  ID prefix, null means ignore  */
	public int countObjects(String prefix,int cidmask) {
		int nr_obj=0;
		for (Enumeration e=canvas.objects.keys(); e.hasMoreElements();) {
			String name = (String) e.nextElement();
			if (prefix==null || name.startsWith(prefix)) {
				if (cidmask==0 || (getObject(name).colid&cidmask)!=0) {
					nr_obj++;
				}
			}
		}
		return nr_obj;
	}


	/** Call the move() methods of those objects matching the given name
	 * prefix and collision id mask.
	* @param cidmask collision id mask, 0 means ignore
	* @param prefix  ID prefix, null means ignore  */
	public void moveObjects(String prefix, int cidmask) {
		if (in_parallel_upd) throw new JGameError("Recursive call",true);
		in_parallel_upd=true;
		canvas.moveObjects(prefix,cidmask);
		in_parallel_upd=false;
	}

	/** Call the move() methods of all registered objects. */
	public void moveObjects() {
		if (in_parallel_upd) throw new JGameError("Recursive call",true);
		in_parallel_upd=true;
		canvas.moveObjects(null,0); 
		in_parallel_upd=false;
	}

	/** Start automatic animation in case it was stopped. */
	//public void startAllAnim() { canvas.anim_running=true; }

	/** Stop automatic animation of all objects. */
	//public void stopAllAnim() { canvas.anim_running=false; }

	/** Calls all colliders of objects that match dstid that collide with
	* objects that match srcid.
	*/
	public void checkCollision(int srccid,int dstcid) {
		if (in_parallel_upd) throw new JGameError("Recursive call",true);
		in_parallel_upd=true;
		canvas.checkCollision(srccid,dstcid);
		in_parallel_upd=false;
	}


	/** Check collision of tiles within given rectangle, return the OR of all
	* cids found.
	* @param r  bounding box in pixel coordinates
	*/
	public int checkBGCollision(Rectangle r) {
		return canvas.checkBGCollision(r);
	}

	/** Calls all bg colliders of objects that match objid that collide with
	* tiles that match tileid.
	*/
	public void checkBGCollision(int tilecid,int objcid) {
		if (in_parallel_upd) throw new JGameError("Recursive call",true);
		in_parallel_upd=true;
		canvas.checkBGCollision(tilecid,objcid);
		in_parallel_upd=false;
	}


	/* computation */

	/** A Boolean AND shorthand to use for collision;
	* returns (value&amp;mask) != 0. */
	public boolean and(int value, int mask) {
		return (value&mask) != 0;
	}

	/** A floating-point random number between min and max */
	public double random(double min, double max) {
		return min + Math.random()*(max-min);
	}

	/** Generates discrete random number between min and max inclusive, with
	 * steps of interval.  Epsilon is added to max to ensure there are no
	 * rounding error problems with the interval.  So, random(0.0, 4.2, 2.1)
	 * generates either 0.0, 2.1, or 4.2 with uniform probabilities.  If max
	 * is halfway between interval steps, max is treated as exclusive.  So,
	 * random(0.0,5.0,2.1) generates 0.0, 2.1, 4.2 with uniform probabilities.
	 * If you need integer ranges, be sure to use the integer version to avoid
	 * rounding problems. */
	public double random(double min, double max, double interval) {
		int steps = (int)Math.floor(0.00001 + (max-min)/interval);
		return min + ( (int)(Math.random()*(steps+0.99)) )*interval;
	}

	/** Generates discrete random number between min and max inclusive, with
	 * steps of interval, integer version.  If max is halfway between two
	 * interval steps, it is treated as exclusive. */
	public int random(int min, int max, int interval) {
		int steps = (max-min)/interval;
		return min + ( (int)(Math.random()*(steps+0.99)) )*interval;
	}

	/** Get tile index range of all tiles overlapping given rectangle of pixel
	 * coordinates. 
	 * @return tile indices */
	public Rectangle getTiles(Rectangle r) {
		return canvas.getTiles(r);
	}

	/** Get tile index of the tile the coordinate is on.*/
	public Point getTileIndex(double x, double y) {
		return new Point(
			(int)Math.floor( x / (double)canvas.tilex ),
			(int)Math.floor( y / (double)canvas.tiley )  );
	}

	/** Get pixel coordinate corresponding to the top left of the tile at the
	* given index */
	public Point getTileCoord(int tilex, int tiley) {
		return new Point( tilex*pfTileWidth(), tiley*pfTileHeight() );
	}

	/** Get pixel coordinate corresponding to the top left of the tile at the
	* given index */
	public Point getTileCoord(Point tileidx) {
		return new Point( tileidx.x*pfTileWidth(), tileidx.y*pfTileHeight() );
	}

	/** Snap to grid, double version. Epsilon is added to the gridsnap value,
	* so that isXAligned(x,margin) always implies that snapToGridX(x,margin)
	* will snap.
	* @param x  position to snap
	* @param gridsnapx  snap margin, 0.0 means no snap   */
	public double snapToGridX(double x, double gridsnapx) {
		if (gridsnapx == 0.0) return x;
		int xaligned = canvas.tilex*(int)
			Math.floor(((x + canvas.tilex/2.0) / (double)canvas.tilex));
		double gridofsx = Math.abs(x - xaligned);
		if (gridofsx <= gridsnapx+0.0002) return xaligned;
		return x;
	}

	/** Snap to grid, double version. Epsilon is added to the gridsnap value,
	* so that isYAligned(y,margin) always implies that snapToGridY(y,margin)
	* will snap.
	* @param y  position to snap
	* @param gridsnapy  snap margin, 0.0 means no snap   */
	/** Snap to grid, double version. Epsilon is added to the gridsnap value.
	* 0.0 means no snap */
	public double snapToGridY(double y, double gridsnapy) {
		if (gridsnapy == 0.0) return y;
		int yaligned = canvas.tiley*(int)
			Math.floor(((y + canvas.tiley/2.0) / (double)canvas.tiley));
		double gridofsy = Math.abs(y - yaligned);
		if (gridofsy <= gridsnapy+0.0002) return yaligned;
		return y;
	}

	/** Snap p to grid in case p is close enough to the grid lines. Note: this
	 * function only handles integers so it should not be used to snap an
	 * object position.  */
	public void snapToGrid(Point p,int gridsnapx,int gridsnapy) {
		if (gridsnapx==0 && gridsnapy==0) return;
		int xaligned = canvas.tilex*(int)
			Math.floor(((p.x + canvas.tilex/2.0) / (double)canvas.tilex));
		int yaligned = canvas.tiley*(int)
			Math.floor(((p.y + canvas.tiley/2.0) / (double)canvas.tiley));
		int gridofsx = Math.abs(p.x - xaligned);
		int gridofsy = Math.abs(p.y - yaligned);
		if (gridofsx <= gridsnapx) p.x = xaligned;
		if (gridofsy <= gridsnapy) p.y = yaligned;
	}

	/** Returns true if x falls within margin of the tile snap grid. Epsilon
	 * is added to the margin, so that isXAligned(1.0000, 1.0000)
	 * always returns true. */
	public boolean isXAligned(double x,double margin) {
		int xaligned = canvas.tilex*(int)(((int)x + canvas.tilex/2)
			/ canvas.tilex);
		return Math.abs(x - xaligned) <= margin+0.00005;
	}

	/** Returns true if y falls within margin of the tile snap grid. Epsilon
	 * is added to the margin, so that isYAligned(1.0000, 1.0000)
	 * always returns true. */
	public boolean isYAligned(double y,double margin) {
		int yaligned = canvas.tiley*(int)(((int)y + canvas.tiley/2)
			/ canvas.tiley);
		return Math.abs(y - yaligned) <= margin+0.00005;
	}

	/** Returns the difference between position and the closest tile-aligned
	 * position. */
	public double getXAlignOfs(double x) {
		int xaligned = canvas.tilex*(int)(((int)x + canvas.tilex/2)
			/ canvas.tilex);
		return x - xaligned;
	}

	/** Returns the difference between position and the closest tile-aligned
	 * position. */
	public double getYAlignOfs(double y) {
		int yaligned = canvas.tiley*(int)(((int)y + canvas.tiley/2)
			/ canvas.tiley);
		return y - yaligned;
	}


	/* listening */

	class JGListener implements KeyListener, WindowListener {
		public JGListener () {}
		/* Standard Wimp event handlers */
		public void keyPressed(KeyEvent e) {
			int keychar = e.getKeyChar();
			int keycode = e.getKeyCode();
			if (keycode>=0 && keycode < 256) {
				canvas.keymap[keycode]=true;
				if (canvas.wakeup_key==-1 || canvas.wakeup_key==keycode) {
					if (!running) {
						start();
						// key is cleared when it is used as wakeup key
						canvas.keymap[keycode]=false;
					}
				}
			}
			//System.out.println(e+" keychar"+e.getKeyChar());
		}

		/* handle keys, shift-escape patch by Jeff Friesen */
		public void keyReleased (KeyEvent e) {
			int keychar = e.getKeyChar ();
			int keycode = e.getKeyCode ();
			if (keycode >= 0 && keycode < 256) {
				canvas.keymap [keycode] = false;
			}
			/* shift escape = exit */
			if (e.isShiftDown () 
			&& e.getKeyCode () == KeyEvent.VK_ESCAPE 
			&& !i_am_applet) {
				System.exit(0);
			}
		}
		public void keyTyped (KeyEvent e) { }

		/* old keyhandler code, does not work on some platforms
		public void keyReleased(KeyEvent e) {
			int keychar = e.getKeyChar();
			int keycode = e.getKeyCode();
			if (keycode>=0 && keycode < 256) {
				canvas.keymap[keycode]=false;
			}
		}
		public void keyTyped(KeyEvent e) {
			if (e.isShiftDown() && e.getKeyChar() == 27 && !i_am_applet) {
				System.exit(0);
			}
		}*/

		/* WindowListener handlers */

		public void windowActivated(WindowEvent e) {}
		public void windowClosed(WindowEvent e) {System.out.println("Closed");}
		public void windowClosing(WindowEvent e) {
			System.out.println("Window closed; exiting.");
			closeWindow();
		}
		public void windowDeactivated(WindowEvent e) {}
		public void windowDeiconified(WindowEvent e) {}
		public void windowIconified(WindowEvent e) {}
		public void windowOpened(WindowEvent e) {}

	}


	/** Engine thread, executing game action. */
	class JGEngineThread implements Runnable {
		public JGEngineThread () {}
		public void run() { try {
			try {
				initGame();
			} catch (Exception e) {
				throw new JGameError("Exception during initGame(): "+e);
			}
			canvas.setInitialised();
			target_time = System.currentTimeMillis()+(long)(1000.0/fps);
			while (!is_exited) {
				if ((debugflags&MSGSINPF_DEBUG)!=0) refreshDbgFrameLogs();
				long cur_time = System.currentTimeMillis();
				if (!running) {
					// wait in portions of 1/2 sec until running is set;
					// reset target time
					Thread.sleep(500);
					target_time = cur_time+(long)(1000.0/fps);
				} else if (cur_time < target_time+(long)(330.0/fps)) {
					// we lag behind less than 1/3 frame -> do full frame.
					// This empirically produces the smoothest animation
					doFrameAll();
					canvas.repaint();
					if (cur_time+3 < target_time) {
						//we even have some time left -> sleep it away
						Thread.sleep(target_time-cur_time);
					} else {
						// we don't, just yield to give input handler and
						// painter some time
						Thread.yield();
					}
					target_time += (1000.0/fps);
				} else if (cur_time >
				target_time + (long)(1000.0*maxframeskip/fps)) {
					// we lag behind more than the max # frames ->
					// draw full frame and reset target time
					doFrameAll();
					canvas.repaint();
					// yield to give input handler + painter some time
					Thread.yield();
					target_time=cur_time + (long)(1000.0/fps);
				} else {
					// we lag behind a little -> frame skip
					doFrameAll();
					// yield to give input handler some time
					Thread.yield();
					target_time += (long)(1000.0/fps);
				}
			}
		} catch (InterruptedException e) {
			/* exit thread when interrupted */
			System.out.println("JGame thread exited.");
		} catch (Exception e) {
			dbgShowException("MAIN",e);
		} catch (JGameError e) {
			exitEngine("Error in main:\n"+dbgExceptionToString(e));
		} }
	}

	/** Note: this assumes that primitive type wrappers:
	* Integer, Char, Boolean, Double, Float are always primitive types */
	Method getMethod(Class cls,String name,Object [] args) {
		Class [] args_cls = new Class[args.length];
		for (int i=0; i<args.length; i++) {
			if (args[i] instanceof Boolean) {
				args_cls[i] = Boolean.TYPE;
			} else if (args[i] instanceof Character) {
				args_cls[i] = Character.TYPE;
			} else if (args[i] instanceof Integer) {
				args_cls[i] = Integer.TYPE;
			} else if (args[i] instanceof Double) {
				args_cls[i] = Double.TYPE;
			} else if (args[i] instanceof Float) {
				args_cls[i] = Float.TYPE;
			} else {
				args_cls[i] = args[i].getClass();
			}
		}
		try {
			return cls.getMethod(name, args_cls);
		} catch (NoSuchMethodException ex) {
			return null;
		}
	}

	/** Try to execute given method on given object.  Handles invocation
	 * target exceptions.
	 * @return true = method exists and has been invoked */
	boolean tryMethod(Object o,String name,Object [] args) {
		try {
			Method met=getMethod(o.getClass(),name,args);
			if (met==null) return false;
			met.invoke(o,args);
		} catch (InvocationTargetException ex) {
			Throwable ex_t = ex.getTargetException();
			if (ex_t instanceof JGameError) {
				exitEngine(dbgExceptionToString(ex_t));
			} else {
				dbgShowException("MAIN",ex_t);
			}
			return false;
		} catch (IllegalAccessException ex) {
			System.err.println("Unexpected exception:");
			ex.printStackTrace();
			return false;
		}
		return true;
	}

	boolean existsMethod(Class cls,String name,Object [] args) {
		return getMethod(cls,name,args)!=null;
	}

	boolean existsMethod(Class cls,String name,Class [] args) {
		try {
			cls.getMethod(name, args);
			return true;
		} catch (NoSuchMethodException ex) {
			return false;
		}
	}

	/** create window to simulate applet window.
	*/
	private void createWindow(boolean add_decoration) {
		/* create window to `emulate' an applet's frame */
		if (existsMethod(Frame.class,"setUndecorated",new Class[] {
		Boolean.TYPE } )) {
			/* this is the jdk1.4+ way to do it */
			my_win = new Frame();
			tryMethod(my_win,"setUndecorated",new Object[] {
				new Boolean(!add_decoration) } );
		} else {
			/* jdk1.2: adding a window to a frame like this results in a
			* window without decoration. */
			if (!add_decoration) {
				my_frame = new Frame();
				my_win = new Window(my_frame);
				/* in jdk1.4, we need to call the following two methods to
				* ensure we can get the focus. However, jdk1.2 doesn't have the
				* setFocusableWindowState method however. */
				//my_win.setFocusableWindowState(true);
				tryMethod(my_win,"setFocusableWindowState",new Object[] {
					new Boolean(true) } );
				my_frame.setVisible(true);
				//tryMethod(my_frame,"setVisible",new Object[] {
				//	new Boolean(true) };
			} else {
				my_win = new Frame();
			}
		}
		//my_win.setResizable(false);
		//System.out.println(my_win.isFocusableWindow());
		// ensure no margins around canvas
		my_win.setLayout(new FlowLayout(FlowLayout.CENTER,0,0));
		if (!add_decoration) {
			my_win.setSize(width, height);
		} else {
			// setting the size of the canvas or applet has no effect
			// we need to add the height of the title bar to the height
			// XXX can't figure out how to get the title bar height
			// 24 is the empirically determined height in WinXP
			// 48 enables us to have the whole window with title bar on-screen
			my_win.setSize(width, height+24);
		}
		my_win.add(this);
		my_win.addWindowListener(listener);
		//my_win.addKeyListener(listener); this one worked in both 1.2 and 1.4

	}
	private void closeWindow() {
		my_win.setVisible(false);
		System.exit(0);
	}

	/* audio */

	/** Expected latency of sound on this platform, in milliseconds. */
//	static int audiolatency=0;
//
//	private int getAudioLatencyPlatformEstimate() {
//		int estimate=0;
//		String javaversion = System.getProperty("java.version");
//		String processor = System.getProperty("os.arch");//only tested on i386
//		String osname = System.getProperty("os.name");
//		System.out.println(osname);
//		if (javaversion.startsWith("1.2")
//		||  javaversion.startsWith("1.3")
//		||  javaversion.startsWith("1.4")) {
//			estimate=120; // found on windows 3ghz jdk121
//		}
//		if (osname.equals("NetBSD")) {
//			// XXX netbsd audio is broken
//			estimate=330; //found on netbsd 600mhz sunjdk142
//		} else if (osname.equals("FreeBSD")) {
//			estimate=700; // found on freebsd 1.2ghz jdk142
//		} else if (osname.equals("Linux")) {
//			estimate=120; // to be determined
//		}
//		System.out.println("Audio latency estimate: "+estimate);
//		return estimate;
//	}
//
//	/** Get estimated audio latency in milliseconds.  Don't expect this method
//	 * to return an accurate value!  It varies very much across platforms and
//	 * versions.  It can be anything between 0 and 0.7 seconds.  You will
//	 * probably have to set this value yourself using setAudioLatency. The
//	 * method is made to return a "safe" lower bound. */
//	public int getAudioLatency() { return audiolatency; }
//	/** Set the expected audio latency for this platform in milliseconds. */
//	public void setAudioLatency(int milliseconds){audiolatency = milliseconds;}
//	/** Get estimated audio latency in frames for the current fps setting. */
//	public int getAudioLatencyFps() {
//		return (int)Math.round(audiolatency / (1000/fps));
//	}

	/** clipid -} filename */
	Hashtable audioclips = new Hashtable();
	/** channelname -} clipid -} AudioClip.  Clipid and AudioClip are not
	* defined until played at least once.  */
	Hashtable channels = new Hashtable();

	/** channelname -} clipid.  Sample has been played last as non-loop. */
	Hashtable lastplayed = new Hashtable();
	/** channelname -} clipid.  Sample is playing as loop. */
	Hashtable islooping = new Hashtable();

	int unnamedchnr = 0;
	int nr_unnamedch = 6;

	boolean audioenabled=true;

	/** Enable audio, restart any audio loops. */
	public void enableAudio() {
		if (audioenabled==true) return;
		audioenabled=true;
		for (Enumeration e=channels.keys(); e.hasMoreElements(); ) {
			String channel = (String)e.nextElement();
			String lastclipid=(String)islooping.get(channel);
			if (lastclipid==null) continue;
			Hashtable chan = (Hashtable)channels.get(channel);
			AudioClip clip = (AudioClip)chan.get(lastclipid);
			if (clip!=null) clip.loop(); 
		}
	}

	/** Disable audio, stop all currently playing audio.  Audio commands will
	* be ignored, except that audio loops (music, ambient sounds) are
	* remembered and will be restarted once audio is enabled again. */
	public void disableAudio() {
		if (audioenabled==false) return;
		audioenabled=false;
		for (Enumeration e=channels.keys(); e.hasMoreElements(); ) {
			String channel = (String)e.nextElement();
			String lastclipid=(String)lastplayed.get(channel);
			if (lastclipid==null) continue;
			Hashtable chan = (Hashtable)channels.get(channel);
			AudioClip clip = (AudioClip)chan.get(lastclipid);
			if (clip!=null) clip.stop(); 
		}
	}

	/** Associate given clipid with a filename.  Files are loaded from the
	* resource path.  Java 1.2+ supports at least: midi and wav files. */
	public void defineAudioClip(String clipid,String filename) {
		audioclips.put(clipid,filename);
		// XXX we should replace the old clip.
		//replace requires all old audioclip instances to be deleted.
	}
	/** Get clip as AudioClip object */
	private AudioClip loadAudioClip(String clipid) {
		URL clipres = getClass().getClassLoader().getResource(
				(String) audioclips.get(clipid) );
		AudioClip clip;
		if (i_am_applet) {
			clip=getAudioClip(clipres);
		} else {
			clip=Applet.newAudioClip(clipres);
		}
		return clip;
	}

	/** Returns the audioclip that was last played, null if audio was stopped
	* with stopAudio.	Note the clip does not actually have to be playing; it
	* might have finished playing already. */
	public String lastPlayedAudio(String channel) {
		return (String)lastplayed.get(channel);
	}

	/** Returns the audioclip that was last played, null if audio was stopped
	* with stopAudio.	Note the clip does not actually have to be playing; it
	* might have finished playing already. */
	//public void clearLastPlayedAudio(String channel) {
	//	lastplayed.remove(channel);
	//}

	/** Play audio clip on unnamed channel, which means it will not replace
	* another clip, and cannot be stopped.  The clip is not looped. */
	public void playAudio(String clipid) {
		playAudio("_unnamed"+unnamedchnr,clipid,false);
		unnamedchnr = (unnamedchnr+1)%nr_unnamedch;
	}

	/** Play clip on channel, naming the channel by a number. 
	* @see #playAudio(String,String,boolean) */
	//public void playAudio(int channel,String clipid,boolean loop) {
	//	playAudio(""+channel,clipid,loop);
	//}

	/** Play clip on channel with given name.  Will replace any other clip
	 * already playing on the channel.  Will restart if the clip is already
	 * playing <i>and</i> either this call or the already playing one are
	 * <i>not</i> specified as looping.  If both are looping, the looped sound
	 * will continue without restarting.  If you want the looping sound to be
	 * restarted, call stopAudio first.  Note the channel "music" is reserved
	 * for enabling/disabling music separately in future versions.  */
	public void playAudio(String channel,String clipid,boolean loop) {
		AudioClip clip = null;
		Hashtable chan = (Hashtable) channels.get(channel);
		String clipplaying = (String)lastplayed.get(channel);
		if (chan!=null) {
			clip = (AudioClip) chan.get(clipid);
		} else {
			chan = new Hashtable();
			channels.put(channel,chan);
		}
		if (clip==null) {
			clip = loadAudioClip(clipid);
			chan.put(clipid,clip);
		}
		boolean restart=true;
		if (clipplaying!=null && !clipplaying.equals(clipid)) {
			AudioClip prevclip = (AudioClip) chan.get(clipplaying);
			if (audioenabled) prevclip.stop();
		} else {
			// previous clip is same as this one
			String looping = (String)islooping.get(channel);
			if (loop && looping!=null && looping.equals(clipid)) {
				// both are looping, don't do anything
				restart=false;
			} else {
				// other is not looping, restart
				restart=true;
			}
		}
		if (loop) {
			if (restart) {
				if (audioenabled) clip.loop();
				islooping.put(channel,clipid);
			}
		} else {
			if (audioenabled) clip.play();
			islooping.remove(channel);
		}
		lastplayed.put(channel,clipid);
	}

	/** Play clip on channel with given name.  Will replace any other clip
	 * already playing on the channel.  Will loop if loop is set, and restart
	 * if already playing and restart is set. */
	//public void playAudio(String channel,String clipid,boolean loop,
	//boolean restart) {
	//	// XXX todo
	//}

	/** Stop one audio channel. */
	public void stopAudio(String channel) {
		String lastclipid = (String) lastplayed.get(channel);
		if (lastclipid==null) return;
		Hashtable chan = (Hashtable)channels.get(channel);
		AudioClip clip = (AudioClip)chan.get(lastclipid);
		if (clip!=null) if (audioenabled) clip.stop(); 
		lastplayed.remove(channel);
		islooping.remove(channel);
	}
	/** Stop all audio channels. */
	public void stopAudio() {
		for (Enumeration e=channels.keys(); e.hasMoreElements(); ) {
			stopAudio((String)e.nextElement());
		}
	}
}

