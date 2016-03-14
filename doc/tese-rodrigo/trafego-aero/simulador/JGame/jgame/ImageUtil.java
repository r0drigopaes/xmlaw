package jgame;
import java.awt.*;
import java.util.*;
import java.awt.image.*;
import java.net.*;

/** Some handy utilities for loading an manipulating images; used internally
 * by jgame. */
public class ImageUtil {

	Component output_comp;
	public MediaTracker mediatracker;
	public DummyObserver observer = new DummyObserver();

	Hashtable loadedimages = new Hashtable(); /* filenames => Images */

	class DummyObserver implements ImageObserver {
		public DummyObserver() {}
		public boolean imageUpdate(Image img, int infoflags,
		int x,int y, int width,int height) { return true; }
	}

	public ImageUtil(Component comp) {
		output_comp=comp;
		mediatracker = new MediaTracker(output_comp);
	}

	/** Returns null if there was an error.
	public Image loadImageFromURL(String imgurl) {
		Image img = (Image)loadedimages.get(imgurl);
		if (img==null) {
			img = output_comp.getToolkit().createImage(imgurl);
			loadedimages.put(imgurl,img);
		}
		try {
			ensureLoaded(img);
		} catch (Exception e) {
			System.err.println("Error loading image "+imgurl);
			return null;
		}
		return img;
	} */

	/** Load image from resource path (using getResource).  Note that GIFs are
	 * loaded as _translucent_ indexed images.   Images are cached: loading
	 * an image with the same name twice will get the cached image the second
	 * time.  If you want to remove an image from the cache, use purgeImage.
	* Throws JGError when there was an error. */
	public Image loadImage(String imgfile) {
		Image img = (Image)loadedimages.get(imgfile);
		if (img==null) {
			URL imgurl = getClass().getClassLoader().getResource(imgfile);
			if (imgurl==null) throw new JGameError(
					"File "+imgfile+" not found.",true);
			img = output_comp.getToolkit().createImage(imgurl);
			loadedimages.put(imgfile,img);
		}
		try {
			ensureLoaded(img);
		} catch (Exception e) {
			throw new JGameError("Error loading image "+imgfile );
		}
		return img;
	}

	/** Behaves like loadImage(String).  Returns null if there was an error. */
	public Image loadImage(URL imgurl) {
		Image img = (Image)loadedimages.get(imgurl);
		if (img==null) {
			img = output_comp.getToolkit().createImage(imgurl);
			loadedimages.put(imgurl,img);
		}
		try {
			ensureLoaded(img);
		} catch (Exception e) {
			System.err.println("Error loading image "+imgurl);
			return null;
		}
		return img;
	}

	/** Purge image with the given resourcename from the cache. */
	public void purgeImage(String imgfile) {
		if (loadedimages.containsKey(imgfile)) loadedimages.remove(imgfile);
	}


	public Dimension getSize(Image img) {
		return new Dimension(
			img.getWidth(observer),
			img.getHeight(observer) );
	}

	/** true means the image has some transparent pixels below the given
	 * alpha threshold, false means image is completely opaque. It actually
	 * checks the alpha channel pixel for pixel.  */
	public boolean isOpaque(Image img,int alpha_thresh) {
		//System.out.print("isOpaque start"+System.currentTimeMillis());
		//BufferedImage bimg = toBuffered(img,alpha_thresh,Color.black);
		//WritableRaster ras = bimg.getAlphaRaster();
		//int [] alpha = ras.getPixels(ras.getMinX(), ras.getMinY(),
		//	ras.getWidth(), ras.getHeight(), (int []) null);
		int [] alpha = getPixels(img);
		//for (int i=0; i<alpha.length; i++)
		//	System.out.print(alpha[i]);
		for (int i=0; i<alpha.length; i++) {
			//if (alpha[i]!=1) {
			if ( ((alpha[i]>>24)&0xff) < alpha_thresh)
				//System.out.println("False");
				return false;
		}
		//System.out.println("True");
		return true;
	}

	public int [] getPixels(Image img) {
		Dimension size = getSize(img);
		int [] buffer = new int [size.width * size.height];
		PixelGrabber grabber = new PixelGrabber(img, 0, 0,
				size.width,size.height, buffer, 0, size.width);
		grabPixels(grabber);
		return buffer;
	}

	public int [] getPixels(Image img, int x,int y,int width,int height) {
		int [] buffer = new int [width * height];
		PixelGrabber grabber = new PixelGrabber(img, x, y,
				width,height, buffer, 0, width);
		grabPixels(grabber);
		return buffer;
	}

	private void grabPixels(PixelGrabber grabber) {
		try {
			grabber.grabPixels();
		} catch(InterruptedException e) {
			e.printStackTrace();
		}
		//if (grabber.getColorModel() != ColorModel.getRGBdefault()) {
		//	System.err.println("Warning: found other colormodel than default.");
		//}
	}

	/** for angle, only increments of 90 are allowed */
	public Image rotate(Image img,int angle) {
		Image rot = null;
		Dimension size = getSize(img);
		int [] buffer = getPixels(img);
		int [] rotate = new int [size.width * size.height];
		int angletype = (angle/90) & 3;
		if (angletype==0) return img;
		if (angletype==1) {
			/* 1 2 3 4    9 5 1
			 * 5 6 7 8 => a 6 2
			 * 9 a b c    b 7 3 
			 *            c 8 4 */
			for(int y = 0; y < size.height; y++) {
				for(int x = 0; x < size.width; x++) {
					rotate[x*size.height + (size.height-1-y) ] =
							buffer[(y*size.width)+x];
				}
			}
			return output_comp.createImage(
				new MemoryImageSource(size.height,size.width,
					rotate, 0, size.height) );
		}
		if (angletype==3) {
			/* 1 2 3 4    4 8 c
			 * 5 6 7 8 => 3 7 b
			 * 9 a b c    2 6 a 
			 *            1 5 9 */
			for(int y = 0; y < size.height; y++) {
				for(int x = 0; x < size.width; x++) {
					rotate[(size.width-x-1)*size.height + y] =
							buffer[(y*size.width)+x];
				}
			}
			return output_comp.createImage(
				new MemoryImageSource(size.height,size.width,
					rotate, 0, size.height) );
		}
		if (angletype==2) {
			/* 1 2 3 4    c b a 9
			 * 5 6 7 8 => 8 7 6 5
			 * 9 a b c    4 3 2 1 */
			for(int y = 0; y < size.height; y++) {
				for(int x = 0; x < size.width; x++) {
					rotate[((size.height-y-1)*size.width)+(size.width-x-1)] =
							buffer[(y*size.width)+x];
				}
			}
		}
		return output_comp.createImage(
			new MemoryImageSource(size.width,size.height,
				rotate, 0, size.width) );
	}

	public Image flip(Image img,boolean horiz,boolean vert) {
		Image flipped = null;
		Dimension size = getSize(img);
		int [] buffer = getPixels(img);
		int [] flipbuf = new int [size.width * size.height];
		if (vert && !horiz) {
			for(int y = 0; y < size.height; y++) {
				for(int x = 0; x < size.width; x++) {
					flipbuf[(size.height-y-1)*size.width + x] =
							buffer[(y*size.width)+x];
				}
			}
		} else if (horiz && !vert) {
			for(int y = 0; y < size.height; y++) {
				for(int x = 0; x < size.width; x++) {
					flipbuf[y*size.width + (size.width-x-1)] =
							buffer[(y*size.width)+x];
				}
			}
		} else if (horiz && vert) {
			for(int y = 0; y < size.height; y++) {
				for(int x = 0; x < size.width; x++) {
					flipbuf[(size.height-y-1)*size.width + (size.width-x-1)] =
							buffer[(y*size.width)+x];
				}
			}
		}
		return output_comp.createImage(
			new MemoryImageSource(size.width,size.height,
				flipbuf, 0, size.width) );
	}
	/** Returns a smoothly scaled image using getScaledInstance.  This method
	 * has interesting behaviour.  The scaled image retains its type
	 * (indexed/rgb and bitmask/translucent), and the algorithm tries to scale
	 * smoothly within these constraints.  For indexed, interpolated pixels
	 * are rounded to the existing indexed colours.  For bitmask, the
	 * behaviour depends on the platform.  On WinXP/J1.2 I found that the
	 * colour _behind_ each transparent pixel is used to interpolate between
	 * nontransparent and transparent pixels.  On BSD/J1.4 I found that the
	 * colours of transparent pixels are never used, and only the
	 * nontransparent pixels are used when interpolating a region with mixed
	 * transparent/nontransparent pixels.
	 */
	public Image scale(Image img, int width, int height) {
		//BufferedImage dstimg = createCompatibleImage(width,height);
		//BufferedImage srcimg = toBuffered(img);
		Image scaledimg=img.getScaledInstance(width,height,Image.SCALE_SMOOTH);
		try {
			/* this is necessary for scaled images too */
			ensureLoaded(scaledimg);
		} catch (Exception e) {
			System.err.println("Error scaling image.");
		}
		return scaledimg;
	}
	public void ensureLoaded(Image img) throws Exception {
		//System.err.println("In ensureloaded");
		mediatracker.addImage(img,0);
		try {
			mediatracker.waitForAll();
			if (mediatracker.getErrorsAny()!=null) {
				mediatracker.removeImage(img);
				throw new Exception("Error loading image");
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		mediatracker.removeImage(img);
		//System.err.println("Out ensureloaded");
	}

	public Image crop(Image img, int x,int y, int width,int height) {
		Dimension size = getSize(img);
		int [] buffer = getPixels(img,x,y, width,height);
		return output_comp.createImage(
			new MemoryImageSource(width,height,
				buffer, 0, width) );
	}

//	/** Create display-compatible bitmask BufferedImage version of image for
//	* faster drawing; returns the identical object if it was
//	* already a bitmask BufferedImage.  For X11, you have to do this so that
//	* you get pixmaps instead of ximages.  The alpha channel is thresholded
//	* using the given value between 0 and 255.  The borders around the image
//	* are rendered to bg_col. 
//	*/
//	public BufferedImage toBuffered(Image img,int alpha_thresh,Color bg_col) {
//		if (img instanceof BufferedImage) {
//			// XXX in theory, we should also check if display compatible
//			if ( ((BufferedImage)img).getColorModel().getTransparency()
//			== Transparency.BITMASK ) {
//				return (BufferedImage)img;
//			}
//		}
//		img = toCompatibleBitmask(img,alpha_thresh,bg_col,false);
//		/* see the developer's almanac,
//		* http://javaalmanac.com/egs/java.awt.image/Image2Buf.html,
//		* for docs */
//		Dimension size = getSize(img);
//		BufferedImage bimage = createCompatibleImage(size.width,size.height,
//			Transparency.BITMASK);
//		Graphics g = bimage.createGraphics();
//		g.drawImage(img, 0, 0, null);
//		g.dispose();
//		return bimage;
//	}


	/** Turn a (possibly) translucent or indexed image into a
	* display-compatible bitmask image using the given alpha threshold and
	* render-to-background colour. The alpha values in the
	* image are set to either 0 (below threshold) or 255 (above threshold).
	* The render-to-background colour bg_col is used to determine how the
	* pixels overlapping transparent pixels should be rendered.  The fast
	* algorithm just sets the colour behind the transparent pixels in the
	* image (for bitmask source images); the slow algorithm actually
	* renders the image to a background of bg_col (for translucent sources).
	*
	* @param thresh alpha threshold between 0 and 255
	* @param fast use fast algorithm (only set bg_col behind transp. pixels) */
	public Image toCompatibleBitmask(Image img, int thresh,Color bg_col,
	boolean fast) {
		int bgcol_rgb = (bg_col.getRed()<<16) | (bg_col.getGreen()<<8)
						| bg_col.getBlue();
		Dimension size = getSize(img);
		int [] buffer = getPixels(img);
		// render image to bg depending on bg_col
		BufferedImage img_bg = createCompatibleImage(size.width,size.height,
			Transparency.BITMASK);
		int [] bg_buf;
		if (!fast) {
			Graphics g=img_bg.getGraphics();
			g.setColor(bg_col);
			// the docs say I could use bg_col in the drawImage as an
			// equivalent to the following two lines, but this
			// doesn't handle translucency properly and is _slower_
			g.fillRect(0,0,size.width,size.height);
			g.drawImage(img,0,0,null);
			bg_buf = getPixels(img_bg);
		} else {
			bg_buf=buffer;
		}
		//g.dispose();
		//ColorModel rgb_bitmask = ColorModel.getRGBdefault();
		//rgb_bitmask = new PackedColorModel(
		//		rgb_bitmask.getColorSpace(),25,0xff0000,0x00ff00,0x0000ff,
		//		0x1000000, false, Transparency.BITMASK, DataBuffer.TYPE_INT);
//		ColorSpace space, int bits, int rmask, int gmask, int bmask, int amask, boolean isAlphaPremultiplied, int trans, int transferType) 
		int [] thrsbuf = new int [size.width * size.height];
		for(int y = 0; y < size.height; y++) {
			for(int x = 0; x < size.width; x++) {
				if ( ( (buffer[y*size.width+x] >> 24) & 0xff ) >= thresh) {
					thrsbuf[y*size.width+x]=bg_buf[y*size.width+x]|(0xff<<24);
				} else {
					// explicitly set the colour of the transparent pixel.
					// This makes a difference when scaling!
					//thrsbuf[y*size.width+x]=bg_buf[y*size.width+x]&~(0xff<<24);
					thrsbuf[y*size.width+x]=bgcol_rgb;
				}
			}
		}
		return output_comp.createImage(
			new MemoryImageSource(size.width,size.height,
				//rgb_bitmask,
				img_bg.getColorModel(), // display compatible bitmask
				thrsbuf, 0, size.width) );
	}

	/** Create empty image with given alpha mode that should be efficient on
	* this display */
	public BufferedImage createCompatibleImage(int width,int height,
	int transparency) {
		//try {
		GraphicsEnvironment ge =
				GraphicsEnvironment.getLocalGraphicsEnvironment();
		GraphicsDevice gs = ge.getDefaultScreenDevice();
		GraphicsConfiguration gc = gs.getDefaultConfiguration();
		// always use bitmask transparency
		BufferedImage bimage = gc.createCompatibleImage(
				width, height, transparency);
		return bimage;
		//} catch (HeadlessException e) { // this exception is not in 1.2
			// The system does not have a screen
		//	e.printStackTrace();
		//	return null;
		//}
	}

}

