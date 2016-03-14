package jgame;
import java.awt.*;
import java.awt.event.*;
import javax.swing.ListCellRenderer;
import java.util.*;
import java.awt.image.*;
import java.net.*;

/** JGCanvas is internally used by JGEngine for updating and drawing objects
 * and tiles. You should not need to use this class directly when writing a game
 * application.
 */
class JGCanvas extends Canvas {

	/*====== mouse stuff ======*/

	AllMouse mouse_listener = new AllMouse();
	boolean has_focus=false;
	Point mousepos = new Point(0,0);
	boolean [] mousebutton = new boolean[] {false,false,false,false};
	boolean mouseinside=false;
	// part of the "official" method of handling keyboard focus
	public boolean isFocusTraversable() { return true; }
	public class AllMouse
	implements MouseListener, MouseMotionListener, FocusListener {
		void updateMouse(MouseEvent e,boolean pressed, boolean released,
		boolean inside) {
			mousepos = e.getPoint();
			mousepos.x = (int)(mousepos.x/x_scale_fac);
			mousepos.y = (int)(mousepos.y/y_scale_fac);
			mouseinside=inside;
			int button=0;
			if ((e.getModifiers()&InputEvent.BUTTON1_MASK)!=0) button=1;
			if ((e.getModifiers()&InputEvent.BUTTON2_MASK)!=0) button=2;
			if ((e.getModifiers()&InputEvent.BUTTON3_MASK)!=0) button=3;
			if (button==0) return;
			if (pressed)  {
				mousebutton[button]=true;
				keymap[255+button]=true;
				if (wakeup_key==-1 || wakeup_key==255+button) {
					if (!engine.running) {
						engine.start();
						// mouse button is cleared when it is used as wakeup key
						mousebutton[button]=false;
						keymap[255+button]=false;
					}
				}
			}
			if (released) {
				mousebutton[button]=false;
				keymap[255+button]=false;
			}
		}

		public void mouseClicked(MouseEvent e) {
			// part of the "official" method of handling keyboard focus
			// some people think it's a bug.
			// http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=4362074
			if (!has_focus) requestFocus();
			updateMouse(e,false,false,true); 
		}
		public void mouseEntered(MouseEvent e) {
			updateMouse(e,false,false,true); 
		}
		public void mouseExited(MouseEvent e) {
			updateMouse(e,false,false,false); 
		}
		public void mousePressed(MouseEvent e) {
			updateMouse(e,true,false,true); 
		}
		public void mouseReleased(MouseEvent e) {
			updateMouse(e,false,true,true); 
		}
		public void mouseDragged(MouseEvent e) {
			updateMouse(e,false,false,true); 
		}
		public void mouseMoved(MouseEvent e) {
			updateMouse(e,false,false,true); 
		}
		public void focusGained(FocusEvent e) {
			has_focus=true;
		}
		public void focusLost(FocusEvent e) {
			has_focus=false;
		}

	}

	/*====== init stuff ======*/

	JGEngine engine;
	/** Determines whether repaint will show the game graphics or do
	 * nothing. */
	boolean is_initialised=false;
	/** paint interface that is used when the canvas is not initialised (for
	 * displaying status info while starting up, loading files, etc. */
	private ListCellRenderer initpainter=null;
	/** for displaying progress info */
	String progress_message=null;
	/** for displaying progress bar, value between 0.0 - 1.0 */
	double progress_bar=0.0;

	public JGCanvas (JGEngine engine, int nrtilesx, int nrtilesy,
	int tilex,int tiley,
	int width, int height) {
		super();
		this.engine=engine;
		setSize(width,height);
		this.tilex=tilex;
		this.tiley=tiley;
		this.nrtilesx=nrtilesx;
		this.nrtilesy=nrtilesy;
		this.width_with_pad = width;
		this.height_with_pad = height;
		this.width = ((int)(width / nrtilesx))*nrtilesx;
		this.height = ((int)(height / nrtilesy))*nrtilesy;
		x_scale_fac = width  / (double)(tilex*nrtilesx);
		y_scale_fac = height / (double)(tiley*nrtilesy);
		min_scale_fac = Math.min( x_scale_fac, y_scale_fac );
		setBGDim(tilex,tiley, nrtilesx, nrtilesy, "");
		clearKeymap();
		addMouseListener(mouse_listener);
		addMouseMotionListener(mouse_listener);
		addFocusListener(mouse_listener);
	}

	void setInitialised() {
		is_initialised=true; 
		initpainter=null;
	}
	void setInitPainter(ListCellRenderer painter) {
		initpainter=painter;
	}
	void setProgressMessage(String msg) {
		progress_message=msg;
		// currently unused
		//if (!is_initialised && initpainter!=null) repaint();
	}
	void setProgressBar(double pos) {
		progress_bar=pos;
		if (!is_initialised && initpainter!=null) {
			repaint(100);
		}
	}

	/*====== variables ======*/

	class JGImageMap {//implements ImageObserver {
		public Image img;
		//public Image scaled_img=null;
		public int xofs,yofs;
		public int tilex,tiley;
		public int skipx,skipy;
		public JGImageMap (String imgfile, int xofs,int yofs,
		int tilex,int tiley, int skipx,int skipy) {
			//URL imageurl=null;
			//try {
			//	imageurl=new URL(baseurl,basepath+imgurl);
			//} catch (MalformedURLException e) {
			//	e.printStackTrace();
			//}
			//setProgressMessage(imageurl.toString());
			//img = imageutil.loadImage(imageurl);
			img = imageutil.loadImage(imgfile);
			this.xofs=xofs;
			this.yofs=yofs;
			this.tilex=tilex;
			this.tiley=tiley;
			this.skipx=skipx;
			this.skipy=skipy;
		}
		//public int getHeight() { return img.getHeight(this); }
		/** returns null when image was not loaded */
		public Point getImageCoord(int imgnr) {
			if (img==null) return null;
			Dimension size = imageutil.getSize(img);
			int imgs_per_line = (size.width - xofs + skipx) / (tilex+skipx);
			int ynr = imgnr / imgs_per_line;
			int xnr = imgnr % imgs_per_line;
			return new Point(
				xofs + xnr*(tilex+skipx),
				yofs + ynr*(tiley+skipy) );
		}
		//public Image getScaledImage() {
		//	if (scaled_img!=null) return scaled_img;
		//	Dimension size = imageutil.getSize(img);
		//	Point scaledpos = scalePos(size.width,size.height);
		//	scaled_img = imageutil.scale(img,scaledpos.x,scaledpos.y);
		//	return scaled_img;
		//}
		//public boolean
		//imageUpdate(Image img,int infoflags,int x,int y,int width,int height){
		//	return true;
		//}
	}

	/* images */

	public ImageUtil imageutil = new ImageUtil(this);
	int alpha_thresh=128;
	Color render_bg_color=null; // null means use engine.bg_color

	/** Strings -&gt; Images, original size,
	* nonexistence means there is no image */
	Hashtable images_orig = new Hashtable();
	/** Strings -&gt; Images, screen size, nonexistence indicates image
	* is not cached and needs to be generated from images_orig */
	Hashtable images = new Hashtable();
	/** indicates that image is defined even if it has no Image */
	Hashtable images_exists= new Hashtable(); 
	Hashtable images_transp = new Hashtable(); 
	/** Hashtable: name to filename. Indicates that image with given name
	* is loaded from given filename */
	Hashtable images_loaded= new Hashtable(); 
		/* Integers -> Objects, existence indicates transparency */
	Hashtable images_tile = new Hashtable(); /* Integers -> Strings */
	Hashtable images_bbox = new Hashtable(); /* Strings -> Rectangles */
	Hashtable images_tilecid = new Hashtable(); /* Integers -> Integers */

	Hashtable imagemaps = new Hashtable(); /* Strings->JGImageMaps*/

	/* objects */

	/** Note: objects lock is used to synchronise object updating between
	 * repaint thread and game thread.  The synchronize functions are found in
	 * Engine.doFrameAll and Canvas.paint */
	Hashtable objects=new Hashtable();      /* String->JGObject */
	Vector obj_to_remove = new Vector(20,40); /* String */
	Vector obj_spec_to_remove = new Vector(20,40); /* (String,Int) */
	Vector obj_to_add = new Vector(20,40); /* JGObject */

	/* playfield */

	int nrtilesx, nrtilesy;
	int xofs=0,yofs=0;
	int tilex=32,tiley=32;
	int virtualx,virtualy;
	int [] [] tilemap=null;
	int [] [] tilecidmap=null;

	/** Image name (not tile name) of image to use behind transparent tiles.
	* Null indicates: use solid fill */
	String bg_image=null;
	Dimension bg_image_tiles=null;
	String out_of_bounds_tile="";
	int out_of_bounds_cid=0;
	int preserve_cids=0;

	/* screen/drawing */

	/** originally specified size */
	int width_with_pad,height_with_pad;
	/** derived from size and tile dimensions */
	int width,height;
	/** min (scalex,scaley); is 1.0 until width, height is defined */
	double x_scale_fac=1.0, y_scale_fac=1.0, min_scale_fac=1.0;

	/* screen state */
	Image background=null,buffer=null;
	boolean do_repaint_all=false;

	/* keyboard */

	/** The codes 256-258 are the mouse buttons */
	boolean [] keymap = new boolean [256+3];
	int wakeup_key=0;

	private void clearKeymap() {
		for (int i=0; i<256+3; i++) keymap[i]=false;
	}


	boolean existsImage(String imgname) {
		return images_exists.containsKey(imgname);
	}

	boolean existsTileImage(int tileid) {
		Integer tileid_obj = new Integer(tileid);
		if (!images_tile.containsKey(tileid_obj)) return false;
		return images_orig.containsKey(images_tile.get(tileid_obj));
	}

	/** Returns null if image is a null image; throws error if image is not
	* defined.  */
	Image getImage(String imgname) {
		if (!existsImage(imgname)) throw new JGameError(
				"Image '"+imgname+"' not defined.",true );
		Image img = (Image)images.get(imgname);
		if (img==null) {
			img = (Image)images_orig.get(imgname);
			if (img==null) return null;
			// convert indexed to bitmask display-compatible image
			Color render_bg_col = render_bg_color;
			if (render_bg_col==null) render_bg_col = engine.bg_color;
			img = imageutil.toCompatibleBitmask(img,alpha_thresh,
					render_bg_col,true);
			Dimension size = imageutil.getSize(img);
			//BufferedImage img2 = imageutil.createCompatibleImage(
			//		size.width,size.height, Transparency.TRANSLUCENT );
			//img2.getGraphics().drawImage(img,0,0,null);
			//img=img2;
			if (width>0 && height>0) {
				Point scaledpos = scalePos(size.width,size.height);
				img = imageutil.scale(img, scaledpos.x,scaledpos.y);
				// convert translucent image to bitmask
				// not necessary?
				//img = imageutil.toCompatibleBitmask(img,alpha_thresh,
				//		render_bg_col,false);
				images.put(imgname,img);
			} else {
				throw new JGameError("Image width, height <= 0 !",true);
			}
		}
		return img;
	}

	Dimension getImageSize(String imgname) {
		Image img = (Image)images_orig.get(imgname);
		if (img==null) return null;
		return imageutil.getSize(img);
	}

	Image getSubImage(String mapname,int imgnr) {
		JGImageMap imgmap = (JGImageMap)imagemaps.get(mapname);
		if (imgmap == null) throw new JGameError(
				"Image map '"+mapname+"' not found.",true );
		Point subcoord = imgmap.getImageCoord(imgnr);
		if (subcoord!=null) {
			return imageutil.crop(imgmap.img,subcoord.x,subcoord.y,
					imgmap.tilex,imgmap.tiley);
		} else {
			return null;
		}
//		Image subimg = createImage(imgmap.tilex,imgmap.tiley);
//		Graphics g = subimg.getGraphics();
//		g.setColor(new Color(250,0,0,0));
//		g.fillRect(0,0,imgmap.tilex,imgmap.tiley);
//		g.drawImage(imgmap.img,
//			-subcoord.x,-subcoord.y,this);
//		//System.out.println("coord "+subcoord);
//		//try { Thread.sleep(2000); } catch (InterruptedException e) {}
//		return subimg;
	}

	void drawImage(Graphics g,int x,int y,String imgname) {
		if (imgname==null) return;
		Point scaled = scalePos(x,y);
		Image img = getImage(imgname);
		if (img!=null) g.drawImage(img,scaled.x,scaled.y,this);
	}

	/** If an image with name already exists, it is removed from memory.
	* @param imgfile  filespec, loaded as resource
	*/
	void defineImage(String name, String tilename, int collisionid,
	String imgfile, String img_op,
	int top,int left, int width,int height) {
		if (images_loaded.containsKey(name)) {
			imageutil.purgeImage((String)images_loaded.get(name));
		}
		Image img=null;
		if (!imgfile.equals("null")) {
			//System.out.println(baseurl+"#######"+basepath+"####"+imgfile);
			//URL imageurl=new URL(baseurl,basepath+imgfile);
			//setProgressMessage(imageurl.toString());
			//img = imageutil.loadImage(imageurl);
			img = imageutil.loadImage(imgfile);
			images_loaded.put(name,imgfile);
		}
		defineImage(name,tilename, collisionid, img,
			img_op, top,left, width,height);
	}

	/** passing -1 to top,left,width,height indicates these have to be taken
	* from the image dimensions.
	* @param imgfile  filespec, loaded as resource
	*/
	void defineImage(String name, String tilename, int collisionid,
	Image img, String img_op,
	int top,int left, int width,int height) {
		if (img!=null) {
			/* do image ops */
			img_op = img_op.toLowerCase();
			boolean flipx = img_op.indexOf("x") >= 0;
			boolean flipy = img_op.indexOf("y") >= 0;
			boolean rot90  = img_op.indexOf("r") >= 0;
			boolean rot180 = img_op.indexOf("u") >= 0;
			boolean rot270 = img_op.indexOf("l") >= 0;
			//System.out.println("img_op "+img_op+ " "+flipx+" "+flipy);
			if (flipx || flipy) img = imageutil.flip(img,flipx,flipy);
			if (rot90) { img = imageutil.rotate(img,90); }
			else if (rot180) { img = imageutil.rotate(img,180); }
			else if (rot270) { img = imageutil.rotate(img,270); }
			images_orig.put(name,img);
		}
		images_exists.put(name, "yes");
		Integer tileid = new Integer(tilestrToInt(tilename));
		if (img==null || !imageutil.isOpaque(img,alpha_thresh))
			images_transp.put(tileid, "yes");
		images_tile.put(tileid, name);
		images_tilecid.put(tileid, new Integer(collisionid));
		if (top >= 0) {
			images_bbox.put(name,new Rectangle(top,left,width,height));
		} else {
			Dimension size;
			if (img==null) size = new Dimension(0,0);
			else           size = imageutil.getSize(img);
			images_bbox.put(name,new Rectangle(0,0,size.width,size.height));
		}
		getImage(name); /* pre-load scaled image */
	}

	void defineImageMap(String mapname, String imgfile,
		int xofs,int yofs, int tilex,int tiley, int skipx,int skipy) {
		imagemaps.put(mapname, new JGImageMap (imgfile, xofs,yofs,
			tilex,tiley, skipx,skipy) );
	}

	Rectangle getImageBBox(String imgname) {
		return (Rectangle)images_bbox.get(imgname);
	}


	/** null=turn off bg image */
	void setBGImage(String bgimg) {
		bg_image=bgimg;
		if (bgimg!=null) {
			bg_image_tiles = imageutil.getSize((Image)images_orig.get(bgimg));
			bg_image_tiles.width /= tilex;
			bg_image_tiles.height /= tiley;
		}
	}


	void setBGDim(int tilex,int tiley, int virtualx,int virtualy,
	String filltile) {
		this.tilex=tilex;
		this.tiley=tiley;
		this.virtualx=virtualx;
		this.virtualy=virtualy;
		tilemap = new int [virtualx][virtualy];
		tilecidmap = new int [virtualx][virtualy];
		fillBG(filltile);
	}

	void setTileSettings(String out_of_bounds_tile,
	int out_of_bounds_cid,int preserve_cids) {
		this.out_of_bounds_tile=out_of_bounds_tile;
		this.out_of_bounds_cid=out_of_bounds_cid;
		this.preserve_cids=preserve_cids;
	}

	void fillBG(String filltile) {
		for (int y=0; y<virtualy; y++) {
			for (int x=0; x<virtualx; x++) {
				setTile(x,y,filltile);
			}
		}
	}


	/*====== objects ======*/

	/** Add new object, will become active next frame. */
	void markAddObject(JGObject obj) {
		obj_to_add.add(obj);
	}

	/** Add new object now.  Old object with the same name is replaced
	 * immediately, and its remove() method called.  */
	void addObject(JGObject obj) {
		JGObject old_obj = (JGObject)objects.get(obj.name);
		if (old_obj!=null) {
			// disable object so it doesn't call engine on removal
			old_obj.removeDone();
			// ensure any dispose stuff in the object is called
			old_obj.remove();
		}
		objects.put(obj.name,obj);
	}

	/** Mark object for removal. */
	void markRemoveObject(String index) {
		obj_to_remove.add(index);
	}

	/** Mark object for removal. */
	void markRemoveObject(JGObject obj) {
		obj_to_remove.add(obj.name);
	}

	/** Actually remove object now */
	void removeObject(JGObject obj) {
		obj.removeDone();
		objects.remove(obj.name);
	}

	/** Mark all objects with given spec for removal. */
	void markRemoveObjects(String prefix,int cidmask) {
		obj_spec_to_remove.add(prefix);
		obj_spec_to_remove.add(new Integer(cidmask));
	}
	/** Actually remove objects with given spec, including those in obj_to_add
	 * list. */
	void removeObjects(String prefix,int cidmask) {
		for (Enumeration e=objects.keys(); e.hasMoreElements();) {
			String name = (String) e.nextElement();
			if (prefix==null || name.startsWith(prefix)) {
				JGObject o = (JGObject) getObject(name);
				if (cidmask==0 || (o.colid&cidmask)!=0) {
					removeObject(o);
				}
			}
		}
		for (int i=obj_to_add.size()-1; i>=0; i--) {
			JGObject o = (JGObject) obj_to_add.get(i);
			if (prefix==null || o.name.startsWith(prefix)) {
				if (cidmask==0 || (o.colid&cidmask)!=0) {
					obj_to_add.remove(i);
				}
			}
		}
	}


	/** Remove objects marked for removal. */
	void flushRemoveList() {
		//if (clear_objects) objects.clear();
		//clear_objects=false;
		for (Enumeration e=obj_to_remove.elements(); e.hasMoreElements();) {
			String name = (String)e.nextElement();
			JGObject o = (JGObject)objects.get(name);
			if (o!=null) { // object might have been removed already
				removeObject(o);
			}
		}
		for (Enumeration e=obj_spec_to_remove.elements();e.hasMoreElements();){
			String prefix = (String) e.nextElement();
			int cid = ((Integer)e.nextElement()).intValue();
			removeObjects(prefix,cid);
		}
		obj_to_remove.clear();
		obj_spec_to_remove.clear();
	}

	/** Add objects marked for addition.
	*/
	void flushAddList() {
		for (Enumeration e=obj_to_add.elements(); e.hasMoreElements();) {
			addObject((JGObject)e.nextElement());
		}
		obj_to_add.clear();
	}

	/** Remove objects marked for addition before they can be added.
	*/
	void clearAddList() {
		for (Enumeration e=obj_to_add.elements(); e.hasMoreElements();) {
			JGObject o = (JGObject)e.nextElement();
			o.removeDone(); // be sure to mark the object as removed
		}
		obj_to_add.clear();
	}


	/* public */

	/** Get object if it exists, null if not.
	*/
	boolean existsObject(String index) {
		return objects.containsKey(index);
	}

	/** Get object if it exists.
	*/
	JGObject getObject(String index) {
		return (JGObject)objects.get(index);
	}

	/** Remove all objects.  All objects are marked for removal, the add
	* list is cleared.  */
	void clearObjects() {
		for (Enumeration e=objects.keys(); e.hasMoreElements(); ) {
			markRemoveObject((String)e.nextElement());
		}
		clearAddList();
		//clear_objects=true;
	}


	/** Move only those objects with given specifications.
	* @param cidmask collision id mask, 0 means ignore
	* @param prefix  ID prefix, null means ignore  */
	void moveObjects(String prefix, int cidmask) {
		for (Enumeration e=objects.keys(); e.hasMoreElements(); ) {
			String name = (String) e.nextElement();
			if (prefix!=null && !name.startsWith(prefix)) continue;
			JGObject o  = (JGObject)objects.get(name);
			if (cidmask!=0 && (o.colid&cidmask)==0) continue;
			try {
				o.move();
			} catch (JGameError ex) {
				engine.exitEngine(engine.dbgExceptionToString(ex));
			} catch (Exception ex) {
				engine.dbgShowException(o.name,ex);
			}
			o.updateAnimation();
			o.x += o.xdir*o.xspeed;
			o.y += o.ydir*o.yspeed;
			if (o.expiry==0) o.remove();
			if (o.expiry > 0) o.expiry--;
			if (o.expiry==-2 && !o.isOnScreen(16,16)) o.remove();
		}
		flushRemoveList();
	}

	Vector srcobj=new Vector(50,150);
	Vector dstobj=new Vector(50,150);
	/** Calls all colliders of objects that match dstid that collide with
	* objects that match srcid.
	*/
	void checkCollision(int srcid,int dstid) {
		srcobj.clear();
		dstobj.clear();
		/* get all matching objects */
		for (Enumeration e=objects.keys(); e.hasMoreElements(); ) {
			String okey = (String)  e.nextElement();
			JGObject o  = (JGObject)objects.get(okey);
			Rectangle r = o.getBBoxConst();
			if (r!=null) {
				if ((o.colid & srcid) != 0) {
					srcobj.add(o);
				}
				if ((o.colid & dstid) != 0) {
					dstobj.add(o);
				}
			}
		}
		/* check collision */
		for (Enumeration e=srcobj.elements(); e.hasMoreElements(); ) {
			JGObject srco = (JGObject) e.nextElement();
			Rectangle sr = srco.getBBoxConst();
			if (sr==null) continue;
			for (Enumeration f=dstobj.elements(); f.hasMoreElements(); ) {
				JGObject dsto = (JGObject) f.nextElement();
				if (dsto == srco) continue;
				Rectangle dr = dsto.getBBoxConst();
				if (dr==null) continue;
				if (sr.intersects(dr)) {
					try {
						dsto.hit(srco);
					} catch (JGameError ex) {
						engine.exitEngine(engine.dbgExceptionToString(ex));
					} catch (Exception ex) {
						engine.dbgShowException(dsto.name,ex);
					}
				}
			}
		}
		flushRemoveList();
	}

	/** Check collision of tiles within given rectangle, return the OR of all
	* cids found.
	*/
	int checkBGCollision(Rectangle r) {
		return getTileCid(getTiles(r));
	}

	Vector bgcobj=new Vector(50,150);
	/** Calls all bg colliders of objects that match objid that collide with
	* tiles that match tileid.
	*/
	void checkBGCollision(int tileid,int objid) {
		bgcobj.clear();
		/* get all matching objects */
		for (Enumeration e=objects.elements(); e.hasMoreElements(); ) {
			JGObject o = (JGObject) e.nextElement();
			Rectangle r = o.getTileBBox();
			if (r!=null) if ((o.colid & objid) != 0) {
				bgcobj.add(o);
			}
		}
		/* check collision */
		for (Enumeration e=bgcobj.elements(); e.hasMoreElements(); ) {
			JGObject o = (JGObject) e.nextElement();
			Rectangle r = o.getTileBBox();
			if (r==null) continue;
			int cid=checkBGCollision(r);
			if ((cid & tileid) != 0) {
				try {
					Rectangle tiler = getTiles(r);
					o.hit_bg(cid);
					o.hit_bg(cid,tiler.x,tiler.y,tiler.width,tiler.height);
					// XXX this might be slow, check its performance
					for (int y=0; y<tiler.height; y++) {
						for (int x=0; x<tiler.width; x++) {
							int thiscid = getTileCid(tiler.x+x, tiler.y+y);
							if ( (thiscid&tileid) != 0)
								o.hit_bg(thiscid, tiler.x+x, tiler.y+y);
						}
					}
				} catch (JGameError ex) {
					engine.exitEngine(engine.dbgExceptionToString(ex));
				} catch (Exception ex) {
					engine.dbgShowException(o.name,ex);
				}
			}
		}
		flushRemoveList();
	}


	/** Do final update actions on objects after all frame updates finished */
	void frameFinished() {
		for (Enumeration e=objects.elements(); e.hasMoreElements(); ) {
			((JGObject)e.nextElement()).frameFinished();
		}
	}

	void drawObject(Graphics g, JGObject o) {
		//o.prepareForFrame();
		drawImage(g,(int)o.x,(int)o.y,o.imgname);
		try {
			o.paint();
		} catch (JGameError ex) {
			engine.exitEngine(engine.dbgExceptionToString(ex));
		} catch (Exception e) {
			engine.dbgShowException(o.name,e);
		}
		if ((JGEngine.debugflags&JGEngine.BBOX_DEBUG)!=0) {
			g.setColor(engine.fg_color);
			Rectangle bbox = scalePos(o.getBBox());
			if (bbox!=null) { // bounding box defined
				g.drawRect(bbox.x,bbox.y,bbox.width,bbox.height);
			}
			bbox = scalePos(o.getTileBBox());
			if (bbox!=null) { // tile bounding box defined
				g.drawRect(bbox.x,bbox.y,bbox.width,bbox.height);
				g.setColor(engine.debug_auxcolor1);
				bbox = getTiles(o.getTileBBox());
				bbox.x *= tilex;
				bbox.y *= tiley;
				bbox.width *= tilex;
				bbox.height *= tiley;
				bbox = scalePos(bbox);
				g.drawRect(bbox.x,bbox.y,bbox.width,bbox.height);
				g.setColor(engine.debug_auxcolor2);
				bbox = o.getCenterTiles();
				bbox.x *= tilex;
				bbox.y *= tiley;
				bbox.width *= tilex;
				bbox.height *= tiley;
				bbox = scalePos(bbox);
				g.drawRect(bbox.x+2,bbox.y+2,bbox.width-2,bbox.height-2);
			}
		}
		//o.frameFinished();
	}


	/*====== tiles ======*/

	/** Set the cid of a single tile using and and or mask. */
	public void setTileCid(int x,int y,int and_mask,int or_mask) {
		if (x<0 || y<0 || x>=virtualx || y>=virtualy) return;
		tilecidmap[x][y] &= and_mask;
		tilecidmap[x][y] |= or_mask;
	}

	void setTile(int x,int y,String tilestr) {
		if (x<0 || y<0 || x>=virtualx || y>=virtualy) return;
		int tileid = tilestrToInt(tilestr);
		tilemap[x][y] = tileid;
		tilecidmap[x][y] &= preserve_cids;
		tilecidmap[x][y] |= tilestrToCid(tilestr);
		drawTile(x,y,tileid);
	}


	/** xi,yi must be within the tilemap */
	void drawTile(int xi,int yi,int tileid) {
		if (background != null) {
			int x = xi * tilex;
			int y = yi * tiley;
			Graphics bgg = background.getGraphics();
			// define background behind tile in case the tile is null or
			// transparent.
			boolean tile_defined = !( tileid==0 || !existsTileImage(tileid) );
			if (!tile_defined||images_transp.containsKey(new Integer(tileid))) {
				Point scp = scalePos(x,y);
				Point scd = scalePos(tilex,tiley);
				if (bg_image==null) {
					bgg.setColor(engine.bg_color);
					bgg.fillRect(scp.x,scp.y,scd.x,scd.y);
				} else {
					int xtile = xi % bg_image_tiles.width;
					int ytile = yi % bg_image_tiles.height;
					bgg.drawImage(getImage(bg_image),
						scp.x, scp.y, scp.x+scd.x, scp.y+scd.y,
						xtile*scd.x, ytile*scd.y, 
						(xtile+1)*scd.x, (ytile+1)*scd.y,
						engine.bg_color,
						null);
					//drawImage(bgg,x,y,bg_image);
				}
			}
			if (tile_defined) {
				drawImage(bgg,x-xofs,y-yofs,
					(String)images_tile.get(new Integer(tileid)) );
			}
		}
		//System.out.println("Drawn tile"+tileid);
	}

	void repaintBG() {
		for (int x=0; x<virtualx; x++)
			for (int y=0; y<virtualy; y++)
				drawTile(x,y,tilemap[x][y]);
		do_repaint_all=false;
	}

	int countTiles(int tilecidmask) {
		int count=0;
		for (int x=0; x<virtualx; x++) {
			for (int y=0; y<virtualy; y++) {
				if ( (tilecidmap[x][y]&tilecidmask) != 0) count++;
			}
		}
		return count;
	}

	int getTileCid(int xidx,int yidx) {
		if (xidx<0 || yidx<0 || xidx>=virtualx || yidx>=virtualy)
			return out_of_bounds_cid;
		return tilecidmap[xidx][yidx];
	}

	String getTileStr(int xidx,int yidx) {
		if (xidx<0 || yidx<0 || xidx>=virtualx || yidx>=virtualy)
			return out_of_bounds_tile;
		return tileidToString(tilemap[xidx][yidx]);
	}

	int getTileCid(Rectangle tiler) {
		int cid=0;
		for (int x=tiler.x; x<tiler.x+tiler.width; x++)
			for (int y=tiler.y; y<tiler.y+tiler.height; y++)
				cid |= getTileCid(x,y);
		return cid;
	}

	private int tilestrToCid(String tilestr) {
		if (tilestr==null || tilestr.length()==0) return 0;
		Integer tileid = (Integer)
			images_tilecid.get( new Integer(tilestrToInt(tilestr)) );
		if (tileid==null) {
			engine.dbgPrint("MAIN","Warning: unknown tile '"+tilestr+"'.");
			return 0;
		}
		return tileid.intValue();
	}

	/** null or empty string -&gt; 0 */
	private int tilestrToInt(String tilestr) {
		if (tilestr==null) return 0;
		switch (tilestr.length()) {
			case 0: return 0;
			case 1: return (int)tilestr.charAt(0);
			case 2: return (int)tilestr.charAt(0)
			             + (int)tilestr.charAt(1)*256;
			case 3: return (int)tilestr.charAt(0)
			             + (int)tilestr.charAt(1)*256
			             + (int)tilestr.charAt(2)*256*256;
			case 4: return (int)tilestr.charAt(0)
			             + (int)tilestr.charAt(1)*256
			             + (int)tilestr.charAt(2)*256*256
			             + (int)tilestr.charAt(3)*256*256*256;
			default:
				engine.dbgPrint("Warning: string '"+tilestr+" has wrong size.");
				return 0;
		}
	}

	/** tileid==0 -&gt; empty string */
	private String tileidToString(int tileid) {
		if (tileid==0) return "";
		StringBuffer tilestr = new StringBuffer(""+(char)(tileid&255));
		if (tileid >= 0x100) tilestr.append( (char)((tileid/0x100)&255));
		if (tileid >= 0x10000) tilestr.append( (char)((tileid/0x10000)&255));
		if (tileid>=0x1000000) tilestr.append( (char)((tileid/0x1000000)&255));
		return tilestr.toString();
	}

	/** Get tile position range of all tiles overlapping given rectangle.
	* Returns null is rectangle is null.
	* @param r   rectangle in pixel coordinates, null is none  */
	Rectangle getTiles(Rectangle r) {
		if (r==null) return null;
		Rectangle tiler = new Rectangle(r);
		if (tiler.x >= 0) {
			tiler.x /= tilex;
		} else {
			tiler.x = (tiler.x-tilex+1)/tilex;
		}
		if (tiler.y >= 0) {
			tiler.y /= tiley;
		} else {
			tiler.y = (tiler.y-tiley+1)/tiley;
		}
		tiler.width  = 1 - tiler.x + (r.x + r.width  - 1) / tilex;
		tiler.height = 1 - tiler.y + (r.y + r.height - 1) / tiley;
		return tiler;
	}


	/*====== math ======*/

	int scaleXPos(double x) {
		return(int)Math.floor(((double)width/(double)(tilex*nrtilesx)) * x);
	}

	int scaleYPos(double y) {
		return(int)Math.floor(((double)height/(double)(tiley*nrtilesy)) * y);
	}

	Point scalePos(double x, double y) {
		return new Point(
			(int)Math.floor(((double)width/(double)(tilex*nrtilesx)) * x),
			(int)Math.floor(((double)height/(double)(tiley*nrtilesy)) * y) );
	}

	/** returns null if r is null */
	Rectangle scalePos(Rectangle r) {
		// XXX that's a lot of object creation
		if (r==null) return null;
		Point topleft = scalePos(r.x, r.y);
		Point botright = scalePos(r.x+r.width, r.y+r.height);
		return new Rectangle(topleft.x, topleft.y,
			botright.x-topleft.x - 1, botright.y-topleft.y - 1);
	}


	/*====== paint ======*/

	/** Don't call directly. Use repaint().
	*/
	public void update(Graphics g) { paint(g); }

	/** Don't call directly. Use repaint().
	*/
	public void paint(Graphics g) { try {
		if (engine.is_exited) {
			engine.paintExitMessage(g);
			return;
		}
		if (!is_initialised) {
			if (initpainter!=null) {
				//if (buffer==null) {
				//	buffer=createImage(width,height);
				//}
				//if (incremental_repaint) {
					//initpainter.getListCellRendererComponent(null,
				//			buffer.getGraphics(),0,true,false);
				//	g.drawImage(buffer,0,0,this);
				//} else {
					initpainter.getListCellRendererComponent(null,
							getGraphics(),0,false,false);
				//}
			}
			return;
		}
		if (background==null) {
			background=createImage(width,height);
			do_repaint_all=true;
		}
		if (do_repaint_all) {
			repaintBG();
		}
		if (buffer==null) {
			buffer=createImage(width,height);
		}
		if (buffer!=null && background!=null) {
			/* clear buffer */
			Graphics bufg = buffer.getGraphics();
			engine.buf_gfx = bufg; // enable objects to draw on buffer gfx.
			//bufg.setColor(getBackground());
			bufg.drawImage(background,0,0,this);
			//Color defaultcolour=g.getColor();
			synchronized (objects) {
				/* sort objects */
				ArrayList sortedkeys = new ArrayList(objects.keySet());
				Collections.sort(sortedkeys);
				for (Iterator i=sortedkeys.iterator(); i.hasNext(); ) {
					JGObject o = (JGObject) objects.get(i.next());
					if (o==null) {
						System.out.println("Object being painted is null!\n"
							+"This should never happen.\n");
					} else {
						drawObject(bufg,o);
					}
				}
				engine.buf_gfx = null; // we're finished with the object drawing
				/* draw status */
				if (bufg!=null) engine.paintFrame(bufg);
			}
			/* draw buffer */
			g.drawImage(buffer,0,0,this);
			//g.setColor(defaultcolour);
			Toolkit.getDefaultToolkit().sync();
		}
	} catch (JGameError e) {
		engine.exitEngine("Error during paint:\n"
				+engine.dbgExceptionToString(e) );
	} }

}

