package jgame;
import java.awt.*;
import java.awt.image.ImageObserver;
import java.io.Serializable;
import java.net.*;

/** Superclass for game objects, override to define animated game objects.
 * When an object is created, it is automatically registered with the
 * currently running engine.  The object will become active only after the
 * frame ends.  The object is managed by the engine, which will display it and
 * call the move and hit methods when appropriate.  Call remove() to remove
 * the object.  It will be removed after the frame ends.  Use isAlive()
 * to see if the object has been removed or not.

 * <p> Each object corresponds to one image.  The object's appearance can be
 * changed using setImage or any of the animation functions.  If you want
 * multi-image objects, use multiple objects and co-reference them using
 * regular references or using JGEngine's getObject().  You can also define
 * your own paint() method to generate any appearance you want.

 * <p>
 * Objects have a pointer to the engine by which they are managed (eng).  This
 * can be used to call the various useful methods in the engine.
 * Alternatively, the objects can be made inner classes of your JGEngine
 * class, so that they have direct access to all JGEngine methods. 

 * <p> The object remembers some of the state of the previous frame (in
 * particular the previous position and bounding boxes), so that corrective
 * action can be taken after something special happened (such as bumping into
 * a wall).  

 * <P> Objects have a direction and speed built in.  After their move() method
 * finishes, their x and y position are incremented with the given
 * speed/direction.  Speed may be used as absolute value (leave the direction
 * at 1), or as a value relative to the direction (the movement is speed*dir).

 */
public class JGObject {
	static int next_id = 0; 
	/* global which might be accessed concurrently */
	static JGCanvas default_canvas=null;
	static JGEngine default_engine=null;
	/** @return true if success; false if other engine already running */
	static boolean setEngine(JGEngine engine,JGCanvas canvas) {
		if (default_engine!=null) return false;
		default_engine=engine;
		default_canvas=canvas;
		return true;
	}

	/** Print a message to the debug channel, using the object ID as source */
	public void dbgPrint(String msg) { eng.dbgPrint(name,msg); }


	/** Object position */
	public double x=0, y=0;
	/** Object speed; default=0 */
	public double xspeed=0, yspeed=0;
	/** Object direction, is multiplied with speed; default=1 */
	public int xdir=1,ydir=1;
	/** Collision ID */
	public int colid;
	/** Object's global identifier; may not change during the lifetime of the
	 * object. */
	String name;
	/** Number of move() steps before object removes itself, -1 (default)
	 * is never; -2 means expire when off-screen. */
	public int expiry=-1;
	/** Get object's ID */
	public String getName() { return name; }

	boolean is_alive=true;

	String imgname=null;
	JGEngine.Animation anim=null; /* will update imgname if set */
	String animid=null;
	/** cached value: has to be recomputed when image changes; simply set to
	 * null to do this. */
	Rectangle imgbbox=null;
	/** tile bbox is the bbox with offset 0; we have to add the current
	 * coordinate to obtain the actual tile bbox.  Set to null to use regular
	 * bbox instead. */
	Rectangle tilebbox=null;
	/** The bbox that should override the image bbox if not null. */
	Rectangle bbox=null;
	private JGCanvas canvas;

	/** You can use this to call methods in the object's engine.  Even handier
	 * is to have the objects as inner class of the engine. */
	public JGEngine eng;

	/* dimensions of last time drawn  */
	double lastx=0, lasty=0;
	Rectangle lastbbox=null;
	Rectangle lasttilebbox=null; /* actual coordinates */

	private void initObject(JGEngine engine,JGCanvas canvas,
	String name,int collisionid) {
		this.eng=engine;
		this.canvas=canvas;
		this.name=name;
		colid=collisionid;
		canvas.markAddObject(this);
	}

	/** Clear tile bbox definition so that we use the regular bbox again. */
	public void clearTileBBox() { tilebbox=null; }

	public void setTileBBox(int x,int y, int width,int height) {
		tilebbox = new Rectangle(x,y,width,height);
	}

	/** Set bbox definition to override the image bbox.  */
	public void setBBox(int x,int y, int width,int height) {
		bbox = new Rectangle(x,y,width,height);
	}
	/** Clear bbox definition so that we use the image bbox again. */
	public void clearBBox() { bbox=null; }


	///** Set object's tile span by defining the number of tiles and the margin
	// * by which the object's position is snapped for determining the object's
	// * top left position.  */
	//public void setTiles(int xtiles,int ytiles,int gridsnapx,int gridsnapy){
	//	this.xtiles=xtiles;
	//	this.ytiles=ytiles;
	//	this.gridsnapx=gridsnapx;
	//	this.gridsnapy=gridsnapy;
	//}

	public void setPos(double x,double y) {
		this.x=x;
		this.y=y;
	}

	/** Set absolute speed.  Set xdir, ydir to the sign of the supplied speed,
	 * and xspeed and yspeed to the absolute value of the supplied speed.
	 * Passing a value of exactly 0.0 sets the dir to 0. */
	public void setSpeedAbs(double xspeed, double yspeed) {
		if (xspeed < 0.0) {
			xdir = -1;
			this.xspeed = -xspeed;
		} else if (xspeed == 0.0) {
			xdir = 0;
			this.xspeed = 0;
		} else {
			xdir = 1;
			this.xspeed = xspeed;
		}
		if (yspeed < 0.0) {
			ydir = -1;
			this.yspeed = -yspeed;
		} else if (yspeed == 0.0) {
			ydir = 0;
			this.yspeed = 0;
		} else {
			ydir = 1;
			this.yspeed = yspeed;
		}
	}

	/** Set speed and direction in one go. */
	public void setDirSpeed(int xdir,int ydir, double xspeed, double yspeed) {
		this.xdir=xdir;
		this.ydir=ydir;
		this.xspeed=xspeed;
		this.yspeed=yspeed;
	}

	/** Set speed and direction in one go. */
	public void setDirSpeed(int xdir,int ydir, double speed) {
		this.xdir=xdir;
		this.ydir=ydir;
		this.xspeed=speed;
		this.yspeed=speed;
	}

	/** Set relative speed; the values are copied into xspeed,yspeed. */
	public void setSpeed(double xspeed, double yspeed) {
		this.xspeed=xspeed;
		this.yspeed=yspeed;
	}

	/** Set relative speed; the value is copied into xspeed,yspeed. */
	public void setSpeed(double speed) {
		this.xspeed=speed;
		this.yspeed=speed;
	}

	/** Set direction. */
	public void setDir(int xdir, int ydir) {
		this.xdir=xdir;
		this.ydir=ydir;
	}

	/** Set ID of animation or image to display.  First, look for an animation
	 * with the given ID, and setAnim if found.  Otherwise, look for an image
	 * with the given ID, and setImage if found.  Passing null clears the
	 * image and stops the animation.  */
	public void setGraphic(String gfxname) {
		if (gfxname==null) {
			setImage(gfxname);
		} else {
			JGEngine.Animation newanim = eng.getAnimation(gfxname);
			if (newanim!=null) {
				setAnim(gfxname);
			} else {
				setImage(gfxname);
			}
		}
	}

	/** Set ID of image to display; clear animation. Passing null clears
	* the image. */
	public void setImage(String imgname) {
		this.imgname=imgname;
		imgbbox=null;
		anim=null;
		animid=null;
		//stopAnim();
	}
	/** Get object's current image ID */
	public String getImage(String imgname) { return imgname; }

	/* animation */

	/** Set the animation to the given default animation definition, or leave
	 * it as it was if the anim_id is unchanged.  Subsequent changes made in
	 * the animation's parameters do not change the default animation
	 * definition. The changes will be preserved if another call to
	 * setAnimation is made with the same anim_id.  If you want to reset the
	 * animation to the original settings, use resetAnimation().
	 */
	public void setAnim(String anim_id) {
		if (animid==null || !animid.equals(anim_id)) {
			anim = eng.getAnimation(anim_id);
			if (anim==null) {
				eng.dbgPrint(name,"Warning: animation "+anim_id+" not found.");
				return;
			}
			anim = anim.copy();
			animid = anim_id;
			imgname = anim.getCurrentFrame();
		}
	}

	/** Always set the animation to the given default animation definition,
	 * resetting any changes or updates made to the animation. Subsequent
	 * changes made in the animation's parameters do not change the default
	 * animation definition.
	 */
	public void resetAnim(String anim_id) {
		anim = eng.getAnimation(anim_id).copy();
		animid = anim_id;
	}

	/** Clear the animation, the object's current image will remain. */
	public void clearAnim() { anim=null; animid=null; }

	/** Get the ID of the currently running animation. */
	public String getAnimId() { return animid; }

	//public void setAnimIncrement(int increment) {
	//	if (anim!=null) anim.increment=increment;
	//}
	/** Set animation speed; speed may be less than 0, indicating that
	 * animation should go backwards. */
	public void setAnimSpeed(double speed) {
		if (anim!=null) {
			if (speed >= 0) {
				anim.speed=speed;
				anim.increment=1;
			} else {
				anim.speed=-speed;
				anim.increment=-1;
			}
		}
	}
	public void setAnimPingpong(boolean pingpong) {
		if (anim!=null) anim.pingpong=pingpong;
	}
	public void startAnim() { if (anim!=null) anim.start(); }
	public void stopAnim() { if (anim!=null) anim.stop(); }

	/** Reset the animation's state to the start state. */
	public void resetAnim() { if (anim!=null) anim.reset(); }


	/** Create object.
	* @param unique_id  append name with unique ID if unique_id set
	* @param gfxname  id of animation or image, null = no image */
	public JGObject (String name, boolean unique_id,
	double x,double y,int collisionid,String gfxname) {
		initObject(default_engine,default_canvas,
				name + (unique_id ? ""+(next_id++) : "" ), collisionid );
		setPos(x,y);
		setGraphic(gfxname);
	}

	/** Create object with given expiry.
	* @param unique_id  append name with unique ID if unique_id set
	* @param gfxname  id of animation or image, null = no image */
	public JGObject (String name, boolean unique_id,
	double x,double y,int collisionid,String gfxname,int expiry) {
		initObject(default_engine,default_canvas,
				name + (unique_id ? ""+(next_id++) : "" ), collisionid );
		setPos(x,y);
		setGraphic(gfxname);
		this.expiry=expiry;
	}

	/** Create object with given tile bbox.
	* @param unique_id  append name with unique ID if unique_id set
	* @param gfxname  id of animation or image, null = no image */
	public JGObject (String name, boolean unique_id,
	double x,double y,int collisionid,String gfxname,
	int tilebbox_x,int tilebbox_y, int tilebbox_width,int tilebbox_height) {
		initObject(default_engine,default_canvas,
				name + (unique_id ? ""+(next_id++) : "" ), collisionid );
		setPos(x,y);
		setGraphic(gfxname);
		setTileBBox(tilebbox_x,tilebbox_y,tilebbox_width,tilebbox_height);
	}

	/** Create object with given tile bbox and expiry.
	* @param unique_id  append name with unique ID if unique_id set
	* @param gfxname  id of animation or image, null = no image */
	public JGObject (String name, boolean unique_id,
	double x,double y,int collisionid,String gfxname,
	int tilebbox_x,int tilebbox_y, int tilebbox_width,int tilebbox_height,
	int expiry) {
		initObject(default_engine,default_canvas,
				name + (unique_id ? ""+(next_id++) : "" ), collisionid );
		setPos(x,y);
		setGraphic(gfxname);
		setTileBBox(tilebbox_x,tilebbox_y,tilebbox_width,tilebbox_height);
		this.expiry=expiry;
	}

	/** Create object with given absolute speed.
	* @param unique_id  append name with unique ID if unique_id set
	* @param gfxname  id of animation or image, null = no image */
	public JGObject (String name, boolean unique_id,
	double x,double y,int collisionid,String gfxname,
	double xspeed, double yspeed) {
		initObject(default_engine,default_canvas,
				name + (unique_id ? ""+(next_id++) : "" ), collisionid );
		setPos(x,y);
		setGraphic(gfxname);
		setSpeedAbs(xspeed,yspeed);
	}

	/** Create object with given absolute speed and expiry.
	* @param unique_id  append name with unique ID if unique_id set
	* @param gfxname  id of animation or image, null = no image */
	public JGObject (String name, boolean unique_id,
	double x,double y,int collisionid,String gfxname,
	double xspeed, double yspeed, int expiry) {
		initObject(default_engine,default_canvas,
				name + (unique_id ? ""+(next_id++) : "" ), collisionid );
		setPos(x,y);
		setGraphic(gfxname);
		setSpeedAbs(xspeed,yspeed);
		this.expiry = expiry;
	}

	/** Create object with given tile bbox and absolute speed.
	* @param unique_id  append name with unique ID if unique_id set
	* @param gfxname  id of animation or image, null = no image */
	public JGObject (String name, boolean unique_id,
	double x,double y,int collisionid,String gfxname,
	int tilebbox_x,int tilebbox_y, int tilebbox_width,int tilebbox_height,
	double xspeed, double yspeed) {
		initObject(default_engine,default_canvas,
				name + (unique_id ? ""+(next_id++) : "" ), collisionid );
		setPos(x,y);
		setGraphic(gfxname);
		setTileBBox(tilebbox_x,tilebbox_y,tilebbox_width,tilebbox_height);
		setSpeedAbs(xspeed,yspeed);
	}

	/** Create object with given tile bbox, absolute speed, expiry.
	* @param unique_id  append name with unique ID if unique_id set
	* @param gfxname  id of animation or image, null = no image */
	public JGObject (String name, boolean unique_id,
	double x,double y,int collisionid,String gfxname,
	int tilebbox_x,int tilebbox_y, int tilebbox_width,int tilebbox_height,
	double xspeed, double yspeed, int expiry) {
		initObject(default_engine,default_canvas,
				name + (unique_id ? ""+(next_id++) : "" ), collisionid );
		setPos(x,y);
		setGraphic(gfxname);
		setTileBBox(tilebbox_x,tilebbox_y,tilebbox_width,tilebbox_height);
		setSpeedAbs(xspeed,yspeed);
		this.expiry = expiry;
	}

	/** Create object with given direction/speed, expiry.
	* @param unique_id  append name with unique ID if unique_id set
	* @param gfxname  id of animation or image, null = no image */
	public JGObject (String name, boolean unique_id,
	double x,double y,int collisionid,String gfxname,
	int xdir, int ydir, double xspeed, double yspeed, int expiry) {
		initObject(default_engine,default_canvas,
				name + (unique_id ? ""+(next_id++) : "" ), collisionid );
		setPos(x,y);
		setGraphic(gfxname);
		setDirSpeed(xdir,ydir,xspeed,yspeed);
		this.expiry = expiry;
	}

	/** Create object with given tile bbox, direction/speed, expiry.
	* @param unique_id  append name with unique ID if unique_id set
	* @param gfxname  id of animation or image, null = no image */
	public JGObject (String name, boolean unique_id,
	double x,double y,int collisionid,String gfxname,
	int tilebbox_x,int tilebbox_y, int tilebbox_width,int tilebbox_height,
	int xdir, int ydir, double xspeed, double yspeed, int expiry) {
		initObject(default_engine,default_canvas,
				name + (unique_id ? ""+(next_id++) : "" ), collisionid );
		setPos(x,y);
		setGraphic(gfxname);
		setTileBBox(tilebbox_x,tilebbox_y,tilebbox_width,tilebbox_height);
		setDirSpeed(xdir,ydir,xspeed,yspeed);
		this.expiry = expiry;
	}


	/* Bounding box functions.  Return copies. May return null if
	 * image is not defined. */


	/** Get collision bounding box in pixels. Has actual coordinate offset.
	* @return copy of bbox in pixel coordinates, null if no bbox
	*/
	public Rectangle getBBox() {
		if (bbox!=null) return new Rectangle(bbox.x+(int)x, bbox.y+(int)y,
				bbox.width, bbox.height);
		if (imgbbox==null) {
			imgbbox = getImageBBox();
		}
		if (imgbbox!=null) {
			return new Rectangle(imgbbox.x+(int)x, imgbbox.y+(int)y,
				imgbbox.width, imgbbox.height );
		}
		return null;
	}

	Rectangle temp_bbox = new Rectangle(0,0,0,0);

	/**an attempt at reducing the number of objects created by using a temp
	* variable. We consider this unsafe, however, as the returned object may
	* not be modified. So, we use a special, private, variant of the method.
	*/
	Rectangle getBBoxConst() {
		if (bbox!=null) {
			temp_bbox.x = bbox.x+(int)x;
			temp_bbox.y = bbox.y+(int)y;
			temp_bbox.width = bbox.width;
			temp_bbox.height= bbox.height;
			return temp_bbox;
		}
		if (imgbbox==null) {
			imgbbox = getImageBBox();
		}
		if (imgbbox!=null) {
			temp_bbox.x = imgbbox.x+(int)x;
			temp_bbox.y = imgbbox.y+(int)y;
			temp_bbox.width=imgbbox.width;
			temp_bbox.height=imgbbox.height;
			return temp_bbox;
		}
		return null;
	}

	/** Get collision bounding box in pixels of previous frame.
	* @return pixel coordinates, null if no bbox */
	public Rectangle getLastBBox() {
		if (lastbbox==null) return null;
		return new Rectangle(lastbbox);
	}

	/** Get collision bounding box in pixels for the purpose of colliding with
	 * tiles.  Bounding box has actual coordinate offset.
	* @return  pixel coordinates, null if no bbox */
	public Rectangle getTileBBox() {
		if (tilebbox==null) return getBBox();
		return new Rectangle((int)x+tilebbox.x,(int)y+tilebbox.y,
			tilebbox.width,tilebbox.height);
	}
	/*
	//an attempt at optimisation of new Rectangle. It doesn't work, however,
	// as it freezes the program.
	Rectangle temp_tilebbox = new Rectangle(0,0,0,0);
	public Rectangle getTileBBox() {
		if (tilebbox==null) {
			Rectangle bbox = getBBox();
			temp_tilebbox.x = bbox.x;
			temp_tilebbox.y = bbox.y;
			temp_tilebbox.width = bbox.width;
			temp_tilebbox.height = bbox.height;
			return temp_tilebbox;
		}
		temp_tilebbox.x = (int)x+tilebbox.x;
		temp_tilebbox.y = (int)y+tilebbox.y;
		temp_tilebbox.width = tilebbox.width;
		temp_tilebbox.height = tilebbox.height;
		return temp_tilebbox;
	} */


	/** Get tile collision bounding box of previous frame.
	* @return  pixel coordinates, null if no bbox */
	public Rectangle getLastTileBBox() {
		if (lasttilebbox==null) return null;
		return new Rectangle(lasttilebbox);
	}

	/** Get collision bounding box of object's image (same as object's default
	* bbox, note that the offset is (0,0) here).
	* @return  pixel coordinates, null if no bbox */
	public Rectangle getImageBBox() {
		if (imgbbox==null && imgname!=null) {
			imgbbox = canvas.getImageBBox(imgname);
		}
		if (imgbbox==null) return null;
		return new Rectangle(imgbbox);
	}

	/** Get x position of previous frame. */
	public double getLastX() { return lastx; }
	/** Get y position of previous frame. */
	public double getLastY() { return lasty; }


	/* snap functions */

	/** Snap object to grid using the default gridsnap margin of
	 * (xspeed-epsilon, yspeed-epsilon), corresponding to the default
	 * is...Aligned margin. */
	public void snapToGrid() {
		x = eng.snapToGridX(x,Math.abs(xspeed-0.001));
		y = eng.snapToGridY(y,Math.abs(yspeed-0.001));
	}

	/** Snap object to grid.
	* @param gridsnapx margin below which to snap, 0.0 is no snap
	* @param gridsnapy margin below which to snap, 0.0 is no snap */
	public void snapToGrid(double gridsnapx,double gridsnapy) {
		x = eng.snapToGridX(x,gridsnapx);
		y = eng.snapToGridY(y,gridsnapy);
	}
	///** Snap object to grid. */
	//public void snapToGrid(int gridsnapx, int gridsnapy) {
	//	Point p = new Point((int)x,(int)y);
	//	eng.snapToGrid(p,gridsnapx,gridsnapy);
	//	x = p.x;
	//	y = p.y;
	//}
	/** Snap an object's tile bbox corner to grid; floats are rounded down.
	* Snaps to bottom or right of object instead of top and left if the resp.
	* flags are set. Note that bottom and right alignment means that the
	* object's bounding box is one pixel away from crossing the tile border.
	* @param snap_right snap the right hand side of the tile bbox
	* @param snap_bottom snap the bottom of the tile bbox */
	public void snapBBoxToGrid(double gridsnapx,double gridsnapy,
	boolean snap_right, boolean snap_bottom) {
		Rectangle bbox = getTileBBox();
		double bx = x + bbox.x;
		double by = y + bbox.y;
		if (snap_right)  bx += bbox.width;
		if (snap_bottom) by += bbox.height;
		double bxs = eng.snapToGridX(bx,gridsnapx);
		double bys = eng.snapToGridY(by,gridsnapy);
		bxs -= bbox.x;
		bys -= bbox.y;
		if (snap_right)  bxs -= bbox.width;
		if (snap_bottom) bys -= bbox.height;
		x = bxs;
		y = bys;
	}

	/* bg interaction */

	/** Get the tile index coordinates of all the tiles that the object's
	* tile bbox overlaps with.
	* @return tile index coordinates, null if no bbox */
	public Rectangle getTiles() {
		return canvas.getTiles(getTileBBox());
	}

	/** Get the tile index coordinates of the object's previous tile bbox.
	* @return tile index coordinates, null if no tile bbox */
	public Rectangle getLastTiles() {
		return canvas.getTiles(getLastTileBBox());
	}

	/** Get the tile indices spanning the tiles that the object has the
	* most overlap with.  The size of the span is
	* always the same as size of the tile bbox in tiles. For example, if the
	* tile bbox is 48x32 pixels and the tile size is 32x32 pixels, the size
	* in tiles is always 1.5x1 tiles, which is rounded up to 2x1 tiles.
	*
	* @return  tile index coordinates, null if no tile bbox is defined */
	public Rectangle getCenterTiles() {
		Rectangle bbox = getTileBBox();
		if (bbox==null) return null;
		/* find the tile on which the center of the bounding box is located,
		 * that will be the center of our tile span. */
		Point p = new Point(bbox.x + (bbox.width/2), bbox.y + (bbox.height/2));
		p = eng.getTileIndex(p.x,p.y);
		p.x = p.x*canvas.tilex + canvas.tilex/2;
		p.y = p.y*canvas.tiley + canvas.tiley/2;
		bbox.x = canvas.tilex * ( (p.x-(bbox.width /2)) / canvas.tilex);
		bbox.y = canvas.tiley * ( (p.y-(bbox.height/2)) / canvas.tiley);
		//if (bbox.width == 16) System.out.println("span"+canvas.getTiles(bbox));
		//bbox.width  = (int)((bbox.width  + canvas.tilex - 1) / canvas.tilex);
		//bbox.height = (int)((bbox.height + canvas.tiley - 1) / canvas.tiley);
		return canvas.getTiles(bbox);
	}

	/** @return tile index coordinates, null if no tile bbox */
	public Rectangle getLastCenterTiles() {
		Rectangle bbox = getLastTileBBox();
		Point p = new Point(bbox.x,bbox.y);
		eng.snapToGrid(p, canvas.tilex, canvas.tiley);
		bbox.x = p.x;
		bbox.y = p.y;
		return canvas.getTiles(bbox);
	}

	/** Get the top left center tile of the object (that is, the x and y of
	* getCenterTiles()).  If the object is 1x1 tile in size, you get the
	* center tile.  If the object is larger, you get the top left tile of the
	* center tiles.
	* @return tile index coordinate, null if no tile bbox */
	public Point getCenterTile() {
		Rectangle bbox = getTileBBox();
		if (bbox==null) return null;
		/* find the tile on which the center of the bounding box is located,
		 * that will be the center of our tile span. */
		Point p = new Point(bbox.x + (bbox.width/2), bbox.y + (bbox.height/2));
		p = eng.getTileIndex(p.x,p.y);
		p.x = p.x*canvas.tilex + canvas.tilex/2;
		p.y = p.y*canvas.tiley + canvas.tiley/2;
		return new Point(
				( (p.x-(bbox.width /2)) / canvas.tilex),
				( (p.y-(bbox.height/2)) / canvas.tiley)  );
	}

	/** Get the topleftmost tile of the object.
	* @return tile index coordinate, null if no bbox */
	public Point getTopLeftTile() {
		Rectangle r = getTiles();
		if (r==null) return null;
		return new Point(r.x,r.y);
	}

	/** Returns true if x is distance xspeed-epsilon away from being grid
	* aligned. If an object moves at its current xspeed, this method will
	* always return true when the object crosses the tile alignment line, and
	* return false when the object is snapped to grid, and then
	* moves xspeed away from its aligned position. */
	public boolean isXAligned() {
		return eng.isXAligned(x,Math.abs(xspeed-0.001));
	}

	/** Returns true if y is distance yspeed-epsilon away from being grid
	* aligned. If an object moves at its current yspeed, this method will
	* always return true when the object crosses the tile alignment line, and
	* return false when the object is snapped to grid, and then
	* moves xspeed away from its aligned position. */
	public boolean isYAligned() {
		return eng.isYAligned(y,Math.abs(yspeed-0.001));
	}

	/** Returns true if x is within margin of being tile grid aligned. Epsilon
	 * is added to the margin, so that isXAligned(1.0000, 1.0000)
	 * always returns true. */
	public boolean isXAligned(double margin) {
		return eng.isXAligned(x,margin);
	}

	/** Returns true if y is within margin of being tile grid aligned. Epsilon
	 * is added to the margin, so that isXAligned(1.0000, 1.0000)
	 * always returns true. */
	public boolean isYAligned(double margin) {
		return eng.isYAligned(y,margin);
	}

	/** Returns true if the left of the object's tile bbox is within margin of
	* being tile grid aligned. */
	public boolean isLeftAligned(double margin) {
		Rectangle bbox = getTileBBox();
		if (bbox!=null) {
			return eng.isXAligned(bbox.x,margin);
		} else {
			return eng.isXAligned(x,margin);
		}
	}

	/** Returns true if the top of the object's tile bbox is within margin of
	* being tile grid aligned. */
	public boolean isTopAligned(double margin) {
		Rectangle bbox = getTileBBox();
		if (bbox!=null) {
			return eng.isYAligned(bbox.y,margin);
		} else {
			return eng.isYAligned(y,margin);
		}
	}

	/** Returns true if the right of the object's tile bbox is within margin of
	* being tile grid aligned. Note that right aligned means that the bbox is
	* one pixel away from crossing the tile border. */
	public boolean isRightAligned(double margin) {
		Rectangle bbox = getTileBBox();
		if (bbox!=null) {
			return eng.isXAligned(bbox.x+bbox.width,margin);
		} else {
			return eng.isXAligned(x,margin);
		}
	}

	/** Returns true if the bottom of the object's tile bbox is within margin of
	* being tile grid aligned. Note that right aligned means that the bbox is
	* one pixel away from crossing the tile border. */
	public boolean isBottomAligned(double margin) {
		Rectangle bbox = getTileBBox();
		if (bbox!=null) {
			return eng.isYAligned(bbox.y+bbox.height,margin);
		} else {
			return eng.isYAligned(y,margin);
		}
	}

	/* something odd going on here: it fails to find the checkBGCollision in
	 * Engine when I define this one in Object
	public int checkBGCollision() {
		return eng.checkBGCollision(getTileBBox());
	}*/
	
	/** Check collision of tiles within given rectangle, return the OR of all
	* cids found.  this method just calls engine.checkBGCollision.
	*/
	public int checkBGCollision(Rectangle r) {
		return eng.checkBGCollision(r);
	}


	/** Get OR of Tile Cids of the object's current tile bbox at the current
	* position, when offset by the given offset. */
	public int checkBGCollision(double xofs,double yofs) {
		Rectangle bbox = getTileBBox();
		if (tilebbox==null) return 0;
		bbox.x += xofs;
		bbox.y += yofs;
		return checkBGCollision(bbox);
	}

	/** Margin is the margin beyond which the object is considered offscreen.
	* The tile bounding box is used as the object's size, if there is none, we
	* use a zero-size bounding box.
	* @param marginx pixel margin, may be negative
	* @param marginy pixel margin, may be negative
	*/
	public boolean isOnScreen(int marginx,int marginy) {
		Rectangle bbox = getTileBBox();
		if (bbox==null) bbox = new Rectangle(0,0,0,0);
		if (bbox.x + bbox.width  < -marginx) return false;
		if (bbox.y + bbox.height < -marginy) return false;
		if (bbox.x > eng.pfWidth() + marginx) return false;
		if (bbox.y > eng.pfHeight() + marginy) return false;
		return true;
	}

	/** A Boolean AND shorthand to use for collision;
	* returns (value&amp;mask) != 0. */
	public static boolean and(int value, int mask) {
		return (value&mask) != 0;
	}

	/** Do automatic animate */
	final void updateAnimation() {
		if (anim!=null) {// && eng.canvas.anim_running) {
			imgname = anim.animate();
			imgbbox=null;
		}
	}
	/** a new frame has just been updated; animate and make snapshot of state */
	final void frameFinished() {
		lastx=x;
		lasty=y;
		lastbbox = getBBox();
		lasttilebbox = getTileBBox();
	}

	/** Check if object is still active, or has already been removed. */
	public boolean isAlive() { return is_alive; }

	/** Signal to object that remove is done. */
	void removeDone() { is_alive=false; }

	/** Mark object for removal, ignore if already removed. 
	* The object will be removed at the end of this
	* moveObjects, checkCollision, or checkBGCollision action, or immediately
	* if not inside either of these actions. You can override this to
	* implement object disposal code, as the engine guarantees that remove is
	* called when the object is removed.  If you do so, you must call
	* super.remove() to ensure proper removal from the system.
	*/
	public void remove() { if (isAlive()) eng.removeObject(this); }

	/** Override to implement automatic move; default is do nothing.
	*/
	public void move() {}

	/** Override to handle collision; default is do nothing.
	*/
	public void hit(JGObject obj) {}

	/** Override to handle tile collision; default is do nothing.
	*/
	public void hit_bg(int tilecid) {}
	
	/** Override to handle tile collision; default is do nothing.
	* This method is always called when hit_bg(int) is also called, only, extra
	* information is passed that may be useful.
	*/
	public void hit_bg(int tilecid,int tx,int ty, int txsize,int tysize) {}

	/** Override to handle tile collision; default is do nothing.
	* This method is always called when hit_bg(int) is also called, only it may
	* be called multiple times, namely once for each tile the object collides
	* with.  The arguments pass the tile cid and position of that tile.  It is
	* always called after hit_bg(int) and hit_bg(int,int,int,int,int) have
	* been called. */
	public void hit_bg(int tilecid,int tx,int ty) {}

	/** Override to define custom paint actions. */
	public void paint() {}
}

