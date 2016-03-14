
/**********************************************************************
 * A BeanBoxFrame acts as the top-level "frame" that contains a BeanBox.
 * <p>
 * The BeanBoxFrame manages the frame's menubar and keeps track of which
 * bean currently has the focus.
 * <p>
 * Note that there is an asumption that there is only one BeanBoxFrame
 * per application, so various method/fields are static.
 */

package jsim.beanbox;

import java.awt.*;
import java.awt.event.*;
import java.beans.*;
import java.util.*;

import jsim.util.*;


/**********************************************************************
 *
 */

public class BeanBoxFrame extends Frame
                          implements LayoutManager,
                                     Runnable,
                                     ActionListener
{
    static final Color FG_COLOR = Color.black;
    static final Color BG_COLOR = DeColores.lightskyblue;

    private static String       tmpDir = "tmp";
    private static boolean      doShowTimes = false;
    private static Thread       focusThread;
    private static Component    nextFocus;
    private static BeanBoxFrame instance;

    private static String  clipDir = "tmp";
    private static String  clipFile = "beanBoxClip.ser";
    private static String  clipResource = "beanBoxClip";
    private static String  versionID = "BDK1.0 - July 1998";
    private static String  clipLabel;
    private static String  clipName;
    private static boolean clipFromPrototypeInfo;
    private static boolean quickStart = false;
    private static boolean defineOnDemand = true;

    private static BeanBox topBox;

    private static Wrapper topWrapper;

    private static Wrapper currentFocus;

    private static ToolBox toolBox;

    private static boolean hideInvisibleBeans;    


    /******************************************************************
     * Main method for beanbox.
     * @param  argv  command-line arguments
     */
    public static void main (String [] argv)
    {
        try {
            Class.forName ("java.beans.beancontext.BeanContextSupport");
        } catch (Exception e) {
            System.err.println ("This version of beanbox requires Java 2 SDK or later.");
            System.err.println ("Exiting...");
            System.exit (-1);
        }; // try

        if (argv.length > 1) {
            throw new Error ("Bad args");
        }; // if

        for (int i = 0; i < argv.length; i++) {

            if (argv [i].equals ("-version")) {
                System.out.println ("BeanBox version: " + versionID);
                System.exit (0);
            } else if (argv [i].equals ("-quick")) {
                System.err.println ("quick starting...");
                quickStart = true;
            } else { // undocumented? 
                tmpDir = argv [0];
            }; // if

        }; // for

        Beans.setDesignTime (true);

        // Make sure the temporary directory exists and is writeable.
        String absTmpDir = (new java.io.File (tmpDir)).getAbsolutePath ();
        java.io.File tmp = new java.io.File (absTmpDir);

        try {
            tmp.mkdirs ();
        } catch (Exception ex) {
        }; // try

        if (!tmp.isDirectory ()) {
            System.err.println ("main" +
                "Couldn't create temporary directory\n\"" + tmp + "\"");
            System.exit (3);
        }; // if

        if (!tmp.canWrite ()) {
            System.err.println ("main" +
                "No write access to temporary directory\n\"" + tmp + "\""); 
            System.exit (3);
        }; // if

        // Create the main BeanBox frames.
        new BeanBoxFrame ();

    }; // main


    /******************************************************************
     * Create a new BeanBoxFrame at a default screen location.
     */
    public BeanBoxFrame ()
    {
        super ("JSIM BeanBox");

        // there should only be one instance of this class.    
        if (instance != null) {
            throw new Error ("Attempt to create multiple instances of BeanBoxFrame");
        }; // if

        instance = this;

        setLayout (null);
        setForeground (FG_COLOR);
        setBackground (BG_COLOR);
        setMenuBar (new MenuBar ());

        // Timing note: the setFont causes AWT to initialize itself,
        // so a large chunk of time happens here.
        setFont (new Font ("Dialog", Font.PLAIN, 14));

        toolBox = new ToolBox (20, 20);
        new WindowCloser (toolBox, true);

        topBox = new BeanBox ();
        topBox.setBackground (BG_COLOR);

        topWrapper = new Wrapper (topBox, "BeanBox", "jsim.beanbox.BeanBox");
        topWrapper.setBackground (BG_COLOR);
        add (topWrapper);

        doSetCurrentFocus (topWrapper);
        topBox.setSize (100, 100);
        setLayout (this);

        setBounds (170, 20, 600, 550);
        new WindowCloser (this, true);
        show ();

        toolBox.show ();

        // Create a thread to handle focus changes.
        focusThread = new Thread (this);
        focusThread.start ();

    }; // BeanBoxFrame


    /******************************************************************
     * 
     */
    private boolean inBeanBox (Component c)
    {
        while (c != null) {
            if (c instanceof BeanBox) {
                return true;
            }; // if
            c = c.getParent ();
        }; // while

        return false;

    }; // inBeanBox


    /******************************************************************
     * Menu actions come here: we forward them to the current BeanBox.
     */
    public void actionPerformed (ActionEvent evt)
    {
        getCurrentBeanBox ().queueMenuItem (evt);

    }; // actionPerformed


    /******************************************************************
     * 
     */
    static Object getCurrentBean ()
    {
        return currentFocus.getBean ();

    }; // getCurrentBean


    /******************************************************************
     * 
     */
    static Component getCurrentComponent ()
    {
        return currentFocus.getChild ();

    }; // getCurrentComponent


    /******************************************************************
     * 
     */
    static Wrapper getCurrentWrapper ()
    {
        return currentFocus;

    }; // getCurrentWrapper


    /******************************************************************
     * 
     */
    static BeanBox getCurrentBeanBox ()
    {
        Component c = getCurrentComponent ();

        for (;;) {

            if (c == null) {
                System.err.println ("No BeanBox in focus???");
                return null;
            }; // if

            if (c instanceof BeanBox) {
                return (BeanBox) c;
            }; // if

            c = c.getParent ();

        } // for

    }; // getCurrentBeanBox


    /******************************************************************
     * 
     */
    static void setCurrentComponent (Component focus)
    {
        // Null means focus on the top-level beanbox.
        if (focus == null) {
            focus = topWrapper;
        }; // if

        // Wakeup our internal thread to do the focus change.
        synchronized (instance) {
            nextFocus = focus;
            instance.notifyAll ();
        }; // synchronized

    }; // setCurrentComponent


    /******************************************************************
     * Internal thread to handle focus changes.  We use a separate
     * thread for this to offload work from the event thread in order
     * to avoid deadlocks.
     */
    public void run ()
    {
        for (;;) {

            Component bean;

            synchronized (this) {
    
                while (nextFocus == null) {
                    try {
                        wait ();
                    } catch (Exception ex) {
                        System.err.println ("Unexpected interrupt in focus thread");
                    }; // try
                }; // while
        
                bean = nextFocus;
                nextFocus = null;

            }; // synchronized
    
            doSetCurrentFocus (bean);
    
        } // for

    }; // run


    /******************************************************************
     *
     */
    private void doSetCurrentFocus (Component focus)
    {
        Wrapper target = Wrapper.findWrapper (focus);
    
        if (target == currentFocus) {
            return;
        }; // if
        
        // Deactivate the old Wrapper.
        if (currentFocus != null) {
            currentFocus.deactivate ();
        }; // if
    
        currentFocus = target;
    
        // Activate the new Wrapper.
        currentFocus.activate ();
    
        // Find the nearest surrounding BeanBox and use its menubar.
        for (Component c = target.getChild (); c != null; c = c.getParent ()) {
    
            if (c instanceof BeanBox) {
                BeanBox hdr = (BeanBox) c;
                hdr.updateMenuBar (getMenuBar());
                break;
            }; // if
    
        }; // for

    }; // doSetCurrentFocus


    /******************************************************************
     *
     */
    public void dispose ()
    {
        System.exit (0);

    }; // dispose


    /******************************************************************
     *
     */
    public static String getClipDir ()
    {
        return clipDir;

    }; // getClipDir


    /******************************************************************
     *
     */
    public static String getClipFileName ()
    {
        if (clipDir != null) {
            return clipDir+java.io.File.separator+clipFile;
        } else {
            return clipFile;
        } // if

    }; // getClipFileName


    /******************************************************************
     * This method is currently unused
     */
    public static String getClipResource ()
    {
        return clipResource;

    }; // getClipResource


    /******************************************************************
     * 
     */
    public static String getTmpDir ()
    {
        return tmpDir;

    }; // getTmpDir


    /******************************************************************
     * 
     */
    public static void setClipName (String name)
    {
        clipName = name;

    }; // setClipName


    /******************************************************************
     * 
     */
    public static String getClipName ()
    {
        return clipName;

    }; // getClipName
    

    /******************************************************************
     * Added the following getter/setters.
     * We need to propogate the fromPrototype info across copies and pastes
     * since beans originating from .ser files ("prototypes") must be
     * effectivley treated as having hidden-state at code generation time.
     * If we didn't propogate this info, we would have to generate
     * initialization statements for all properties, not just those modified
     * by the user at code generation time.
     */
    public static void setClipFromPrototypeInfo (boolean prototypeInfo)
    {
        clipFromPrototypeInfo = prototypeInfo;

    }; // setClipFromPrototypeInfo

    
    /******************************************************************
     * 
     */
    public static boolean getClipFromPrototypeInfo ()
    {
        return clipFromPrototypeInfo;

    }; // getClipFromPrototypeInfo


    /******************************************************************
     * 
     */
    public static void setClipLabel (String label)
    {
        clipLabel = label;

    }; // setClipLabel


    /******************************************************************
     * 
     */
    public static String getClipLabel ()
    {

        return clipLabel;

    }; // getClipLabel


    /******************************************************************
     * 
     */
    public static boolean getQuickStart ()
    {
        return quickStart;

    }; // getQuickStart


    /******************************************************************
     * 
     */
    public static boolean getDefineOnDemand ()
    {
        return defineOnDemand;

    }; // getDefineOnDemand


    //----------------------------------------------------------------------
    // Layout related stuff.
    //----------------------------------------------------------------------

    public void addLayoutComponent (String name, Component comp) { }

    public void removeLayoutComponent (Component comp) { }


    /******************************************************************
     * 
     */
    public Dimension preferredLayoutSize (Container parent)
    {
        return new Dimension (400, 300);

    }; // preferredLayoutSize


    /******************************************************************
     * 
     */
    public Dimension minimumLayoutSize (Container parent)
    {
        return new Dimension (100, 100);

    }; // minimumLayoutSize


    /******************************************************************
     * 
     */
    public void layoutContainer (Container parent)
    {
        Dimension d = parent.getSize();
        Insets ins = parent.getInsets();
        int width = d.width - (ins.left + ins.right);
        int height = d.height - (ins.top + ins.bottom);
        topWrapper.setBounds(ins.left, ins.top, width, height);
        topWrapper.invalidate();
        topWrapper.doLayout();

    }; // layoutContainer


    /******************************************************************
     * 
     */
    public static BeanBox getTopBox ()
    {
        return topBox;

    }; // getTopBox   


    /******************************************************************
     * 
     */
    public static Wrapper getTopWrapper ()
    {
        return topWrapper;

    }; // getTopWrapper  


    /******************************************************************
     * 
     */
    public static ToolBox getToolBox ()
    {
        return toolBox;

    }; // getToolBox


    /******************************************************************
     * 
     */
    public static boolean showTimes ()
    {
        return doShowTimes;

    }; // showTimes


    /******************************************************************
     * 
     */
    public static boolean getHideInvisibleBeans ()
    {
        return hideInvisibleBeans;

    }; // getHideInvisibleBeans


    /******************************************************************
     * 
     */
    public static void setHideInvisibleBeans (boolean hide)
    {
        hideInvisibleBeans = hide;
        Wrapper.showInvisibleBeans(!hide);

    }; // setHideInvisibleBeans


    /******************************************************************
     * 
     */
    public static void setDesignMode (boolean designMode)
    {
        Beans.setDesignTime (designMode);
        if (designMode) {
            toolBox.setVisible (true);
        } else {
            toolBox.setVisible (false);
        }; // if

    }; // setDesignMode


    /******************************************************************
     * 
     */
    public static String getVersionID ()
    {
        return versionID;

    }; // getVersionID


}; // class

