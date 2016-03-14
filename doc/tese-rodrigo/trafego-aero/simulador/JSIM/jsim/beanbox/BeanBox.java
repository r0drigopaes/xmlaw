
/**************************************************************************
 * The BeanBox is a reference container for JavaBeans.  It has two purposes.
 * First, it allows bean developers to check that their bean correctly
 * implements the various bean APIs.  Second, it provides a reference example
 * container that can be copied and modified freely.
 * <p>
 * The BeanBox allows beans to be manipulated visually.  You can use the
 * associated property sheet to edit the exposed properties, you can use the
 * edit/events menu to connect events from am event source to an event sink,
 * or you can use the edit/customize menu to test a customizer.
 * <p>
 * The BeanBox is itself a bean, and you can recursively embed a BeanBox
 * inside a BeanBox.  At the top-level, a BeanBox is embedded inside a
 * BeanBoxFrame, which handles things like the MenuBar.
 */

package jsim.beanbox;

import java.awt.*;
import java.awt.datatransfer.*;
import java.awt.event.*;
import java.io.*;
import java.beans.*;
import java.beans.beancontext.*;
import java.lang.reflect.*;
import java.util.*;


/**************************************************************************
 *
 */

public class BeanBox extends Panel
                     implements Serializable,
                                Runnable, 
                                MouseListener,
                                MouseMotionListener,
                                BeanContextProxy
{
    static final long serialVersionUID = -1930952830824256715L; 


    /**********************************************************************
     * Initialize a new BeanBox.
     */
    public BeanBox ()
    {
        setLayout (null);
        addMouseListener (this);
        addMouseMotionListener (this);
        bcss.setDesignTime (true);

    }; // BeanBox


    /**********************************************************************
     * Get the top wrapper (i.e. this BeanBox's wrapper)         
     */
    public Wrapper getTopWrapper ()
    {
        return getFrame ().getTopWrapper ();

    }; // getTopWrapper


    /**********************************************************************
     * Update the MenuBar for the current beanBox and focus bean.
     */
    public synchronized void updateMenuBar (MenuBar bar)
    {
        BeanBoxFrame frame = getFrame();
    
        if (bar.getMenuCount() == 0) {
            // Create the basic menus.  These are bean/beanbox independent.

            Menu m = new Menu ("File");         // menu 0
            addMenuItem (frame, m, new MenuItem ("Save..."));
            addMenuItem (frame, m, new MenuItem ("SerializeComponent..."));
            addMenuItem (frame, m, new MenuItem ("Load..."));
            addMenuItem (frame, m, new MenuItem ("LoadJar..."));
            addMenuItem (frame, m, new MenuItem ("Print"));
            addMenuItem (frame, m, new MenuItem ("Clear"));
            addMenuItem (frame, m, new MenuItem ("Exit"));
            bar.add (m);

            m = new Menu ("Edit");              // menu 1
            addMenuItem (frame, m, new MenuItem ("Cut"));
            addMenuItem (frame, m, new MenuItem ("Copy"));
            pasteMenuItem = new MenuItem ("Paste");
            pasteMenuItem.setEnabled (false);
            addMenuItem (frame, m, pasteMenuItem);
            bar.add (m);

            m = new Menu ("Help");              // menu 2
            addMenuItem (frame, m, new MenuItem ("Documentation..."));
            bar.setHelpMenu (m);
        }; // if

        Menu editMenu = bar.getMenu (EDIT_MENUID);

        // Remove any bean-specific menu items from the edit menu.
        for (int i = 0; i < editMenu.getItemCount(); i++) {
            String label = editMenu.getItem(i).getLabel();
            if (label.equals("Customize...") || label.equals("Events")) {
                editMenu.remove(i);
                i--;
            }; // if
        }; // for

        customizerClass = null;
        eventMap = null;
        methodMap = null;

        // Take a look at the current focus bean, and create appropriate
        // menu items for its events, customizer, etc.

        try {
            Object bean = BeanBoxFrame.getCurrentBean();

            BeanInfo bi = Introspector.getBeanInfo(bean.getClass());

            try {
                BeanDescriptor bd = bi.getBeanDescriptor();
                customizerClass = bd.getCustomizerClass();
                if (customizerClass != null) {
                    addMenuItem(frame, editMenu, new MenuItem("Customize..."));
                }; // if
            } catch (Exception ex) {
                error("Couldn't initialize customizer", ex);
            }; // try

            java.beans.EventSetDescriptor esd[] = bi.getEventSetDescriptors();
            boolean bindable = false;

            if (esd.length > 0) {

                Menu eventMenu = new Menu("Events");
                eventMap = new Hashtable();
                methodMap = new Hashtable();
                // We create a separate menu for each event listener interface,
                // with a menu item for each event handler method.

                for (int i = 0; i < esd.length; i++) {
                    String dname = esd[i].getDisplayName();
                    Menu methodMenu = new Menu(dname);
                    eventMap.put(methodMenu, esd[i]);
                    Method methods[] = esd[i].getListenerMethods();
                    for (int j = 0; j < methods.length; j++) {
                        String mname = methods[j].getName();
                        MenuItem mi = new MenuItem(mname);
                        addMenuItem(frame, methodMenu, mi);
                        methodMap.put(mi, methods[j]);
                    }; // for
                    eventMenu.add(methodMenu);
                }; // for

                editMenu.add (eventMenu);

            }; // if

        } catch (IntrospectionException ex) {
            error("Caught unexpected exception while building event menu", ex);
        }; // try

    }; // updateMenuBar


    /**********************************************************************
     * Register the BeanBoxFrame as the event handler and
     * add the menuitem to the menu.
     */
    private void addMenuItem (BeanBoxFrame frame, Menu m, MenuItem mi)
    {
        mi.addActionListener (frame);
        m.add (mi);

    }; // addMenuItem


    /**********************************************************************
     * Provide an identifier for serializing this object.
     */
    private synchronized String serFileName (Object o)
    {
        if (o == null) {
            // distinguished root-component
            return BeanBoxFrame.getTmpDir()+File.separator+"___comp_ROOT.ser";
        } else {
            String s = (String) serNames.get(o);
            if (s == null) {
                String name = "___comp_"+HookupManager.getId()+".ser";
                s = BeanBoxFrame.getTmpDir()+File.separator+name;
                serNames.put(o, s);
            }; // if
            return s;
        } // if

    }; // serFileName


    /**********************************************************************
     * This method is currently unused
     */
    private boolean generateManifestTemplate (File file)
    {
        File dir = new File(file.getParent());

        if (dir != null) {
            dir.mkdirs();
        }; // if

        OutputStream os;

        try {
            os = new FileOutputStream(file);
            PrintWriter pos = new PrintWriter(os);

            int count = getComponentCount();

            for (int i = 0; i < count; i++) {
                Wrapper w = Wrapper.findWrapper(getComponent(i));
                pos.println("Name: "+serFileName(w).replace(File.separatorChar, '/'));
                pos.println("Java-Bean: True");
                pos.println();
            }; // for

            pos.flush();
            pos.close();

        } catch (Exception ex) {
            error("Problems creating manifest template file", ex);
            return false;
        }; // try

        return true;

    }; // generateManifestTemplate


    /**********************************************************************
     * This implements the "save" menu item.  This stores away the
     * current state of the BeanBox to a named file.
     * Note: The format is builder-dependent.
     */
    public void save ()
    {
        // Write a JAR file that contains all the hookups and a
        // single serialized stream.
        // Then, on load, read the components and mock-up the AppletStub.

        FileDialog fd = new FileDialog(getFrame(), "Save BeanBox File", FileDialog.SAVE);
        // the setDirectory() is not needed, except for a bug under Solaris...
        fd.setDirectory(System.getProperty("user.dir"));
        fd.setFile(defaultStoreFile);
        fd.show();
        String fname = fd.getFile();
        if (fname == null) {
            return;
        }; // if
        String dname = fd.getDirectory();
        File file = new File(dname, fname);            

        try {
            // create the single ObjectOutputStream
            File serFile = new File(serFileName(null));

            // we could use a JarEntrySource here to avoid writing the ser file
            FileOutputStream f = new FileOutputStream(serFile);
            ObjectOutputStream oos = new ObjectOutputStream(f);
            writeContents(oos);
            oos.close();

            String[] hookups;
            hookups = HookupManager.getHookupFiles();
            String[] files = new String[hookups.length+1];
            System.arraycopy(hookups, 0, files, 1, hookups.length);
            files[0] = serFileName(null);

            JarAccess.create(new FileOutputStream(file),
                             files);
        } catch (Exception ex) {
            error("Save failed", ex);
        }; // try

    }; // save


    /**********************************************************************
     * This implements the "serializeComponent" menu item.
     * The selected component is serialized to the chosen file
     */
    private void serializeComponent ()
    {
        FileDialog fd = new FileDialog (getFrame (), "Serialize Component into File",
                                        FileDialog.SAVE);
        // needed for a bug under Solaris...
        fd.setDirectory (System.getProperty ("user.dir"));
        fd.setFile (defaultSerializeFile);
        fd.show ();
        String fname = fd.getFile ();

        if (fname == null) {
            return;
        }; // if

        String dname = fd.getDirectory ();
        File file = new File (dname, fname);

        Wrapper w = BeanBoxFrame.getCurrentWrapper ();

        try {
            FileOutputStream f = new FileOutputStream (file);
            ObjectOutputStream oos = new ObjectOutputStream (f);
            // Ask the Wrapper to serialize the "naked" bean.
            // as in copy ()
            w.writeNakedBean (oos);
            oos.close ();
        } catch (Exception ex) {
            error ("Serialization of Component failed", ex);
        }; // try

    }; // serializeComponent


    /**********************************************************************
     * This implements the "load" menu item.  This loads the BeanBox state
     * from a named file.
     */
    private void load ()
    {
        FileDialog fd = new FileDialog (getFrame (), "Load saved BeanBox",
                                                      FileDialog.LOAD);
        // needed for a bug under Solaris...
        fd.setDirectory (System.getProperty ("user.dir"));
        fd.setFile (defaultStoreFile);
        fd.show ();
        String fname = fd.getFile ();

        if (fname == null) {
            return;
        }; // if

        String dname = fd.getDirectory ();
        File file = new File (dname, fname);

        JarLoader jl = null;
        try {
            jl = new JarLoader (file.getPath ());
            JarInfo ji = jl.loadJar ();
        } catch (Throwable th) {
            error ("BeanBox load failed", th);
            return;
        }; // try

        removeAll ();

        // OK, loaded all the classes -- now, instantiate them...

        try {
            // get the one object as a bean...
            ClassLoader cl = jl.getLoader ();
            InputStream is = cl.getResourceAsStream (serFileName (null).replace
                                                    (File.separatorChar, '/'));
            ObjectInputStream ois = new ObjectInputStreamLoader (is, cl);

            // Read all the contained beans into this BeanBox.
            readContents (ois);

        } catch (Throwable th) {
            error ("Couldn't read serialized BeanBox data from Jar", th);
            return;
        }; // try

    }; // load


    /**********************************************************************
     * BeanContext support ...
     */
    public void removeAll ()
    {
        synchronized(getTreeLock()) {
            Component[] comps = getComponents();
            
            for (int i = 0; i < comps.length; i++) {
                try {
                    Wrapper w = (Wrapper)comps[i];

                    bcss.remove(w.getBean());
                } catch (ClassCastException cce) {
                    continue;
                }
            }

            super.removeAll();
        }

    }; // removeAll


    /**********************************************************************
     * Support for serialization
     */
    private void writeContents (java.io.ObjectOutputStream s)
                         throws java.io.IOException
    {
        int count = getComponentCount();

        // We unhook all the event wiring from the beans while
        // they are being written out.
        for (int i=0; i<count; i++) {
            Wrapper w = (Wrapper) getComponent(i);
            w.removeListeners();

            bcss.remove(w.getBean());
        }

        // Now actually write out all the wrappers.
        s.writeInt(count);
        for (int i = 0; i < count; i++) {
            Wrapper w = (Wrapper) getComponent(i);
            s.writeObject(w);
        }

        // Now reattach all the event wiring.
        for (int i=0; i<count; i++) {
            Wrapper w = (Wrapper) getComponent(i);
            w.attachListeners();        

            bcss.add(w.getBean());
        }

    }; // writeContents


    /**********************************************************************
     *
     */
    private void readContents (java.io.ObjectInputStream ois)
                        throws java.lang.ClassNotFoundException,
                               java.io.IOException
    {
        removeAll();

        ClassLoader cl = SimpleClassLoader.ourLoader;

        int count = ois.readInt();

        for (int i=0; i<count; i++) {
            Component c = (Component) ois.readObject();
            Wrapper w = (Wrapper) c;
            w.setFromPrototype(true);   // treat saved beans as if they had
                                        // hidden state.

            Object bean = w.getBean();

            // Add the new component to our AWT container.
            add(c);
            c.invalidate();

            bcss.add(bean);

        }

        // We need to wait until the entire graph has been read back in
        // before we can redo the wiring.
        for (int i=0; i<count; i++) {
            Wrapper w = (Wrapper) getComponent(i);
            w.attachListeners();        
        }

    }; // readContents


    /**********************************************************************
     * Support for serialization
     */
    private void writeObject (java.io.ObjectOutputStream oos)
                       throws java.io.IOException
    {
        writeContents(oos);

    }; // writeObject


    /**********************************************************************
     *
     */
    private void readObject (java.io.ObjectInputStream ois)
                      throws java.lang.ClassNotFoundException,
                             java.io.IOException
    {
        readContents(ois);

    }; // readObject


    /**********************************************************************
     * Support for printing
     * Print the current content of this Beanbox
     */
    private void print ()
    {

        PrintJob pj = Toolkit.getDefaultToolkit().getPrintJob(ourFrame, 
                     "Printing Test", (Properties)null);

        if (pj != null) {

            Graphics g;
            Dimension pageDim = pj.getPageDimension();
            int pageRes = pj.getPageResolution();
            boolean lastFirst = pj.lastPageFirst();

            Graphics gr  = pj.getGraphics();

            if (gr!=null) {
                // We print all components one after the other in the
                // same page.                   
                int count = getComponentCount();

                for (int i=0; i<count; i++) {
                    Wrapper wr = (Wrapper) getComponent(i);
                    Object bean = wr.getBean();

                    if (bean instanceof Component) {
                        Component c;
                        c = (Component) Beans.getInstanceOf(bean, Component.class);
                        Dimension d = c.getSize();
                        Point o = wr.getLocation();
                        Image offScreen = c.createImage(d.width, d.height);
                        c.paint(offScreen.getGraphics());
                        gr.drawImage(offScreen, o.x, o.y, Color.white, null);
                    }; // if

                }; // for

            } else {
                System.err.println("Could not get Graphics handle.");
            }; // if
                         
            gr.dispose();
            pj.end();

        } else {

            System.err.println("PrintJob cancelled.");

        }; // if

    }; // print


    /**********************************************************************
     * This implements the "loadJar" menu item.
     * Load the contents of a JAR file
     */
    private void loadJar ()
    {
        FileDialog fd = new FileDialog(getFrame(), "Load beans from JAR File", FileDialog.LOAD);
        // the setDirectory() is not needed, except for a bug under Solaris...
        fd.setDirectory(System.getProperty("user.dir"));
        fd.setFile(defaultStoreFile);
        fd.show();
        String fname = fd.getFile();
        if (fname == null) {
            return;
        }; // if

        String dname = fd.getDirectory();
        File file = new File(dname, fname);

        try {
            BeanBoxFrame.getToolBox().addBeansInJar(file.getPath());
        } catch (Throwable th) {
            error("Couldn't load Jar", th);
        }; // try

    }; // loadJar


    /**********************************************************************
     * Serialize the current focus bean to our clipboard file, and
     * then remove it from the BeanBox.
     */
    private void cut ()
    {
        Wrapper wrapper = BeanBoxFrame.getCurrentWrapper();
        Object bean = wrapper.getBean();
        if (bean != BeanBoxFrame.getTopBox()) {

            if (copy()) {
                // succeeded in serializing the component
                Container parent = wrapper.getParent();
                BeanBoxFrame.setCurrentComponent(null);
                if (parent != null) {
                    parent.remove(wrapper);
                }; // if
                wrapper.cleanup();
            }; // if

            bcss.remove(bean);
        }; // if

    }; // cut


    /**********************************************************************
     * Print a report on the current focus bean.
     */
    private void report ()
    {
        Object bean = BeanBoxFrame.getCurrentBean();
        if (bean == null) {
            System.out.println("No current focus.");
            return;
        }; // if

    }; // report


    /**********************************************************************
     * Serialize the current focus bean to our clipboard file.
     */
    private boolean copy ()
    {
        Wrapper wrapper = BeanBoxFrame.getCurrentWrapper();

        BeanBoxFrame.setClipLabel(null);
        BeanBoxFrame.setClipName(null);
        try {
            File f = new File(BeanBoxFrame.getClipFileName());
            File dir = new File(f.getParent());
            dir.mkdirs();
            FileOutputStream fos = new FileOutputStream(f);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            // Ask the Wrapper to serialize the "naked" bean.
            // This causes the Wrapper to remove all listeners
            // before serializing, and add them back after.
            wrapper.writeNakedBean(oos);
            oos.close();
            fos.close();
            BeanBoxFrame.setClipLabel(wrapper.getBeanLabel());
            BeanBoxFrame.setClipName(wrapper.getBeanName());            
            // We need to preserve information about whether or not a bean
            // originated from a .ser file across copies and pastes.
            // See paste(), below for more explanation.
            BeanBoxFrame.setClipFromPrototypeInfo(wrapper.isFromPrototype());

            pasteMenuItem.setEnabled(true);
            return true;
        } catch (Exception ex) {
            error("Copy failed", ex);
            pasteMenuItem.setEnabled(false);
            return false;
        } // try

    }; // copy


    /**********************************************************************
     * Read in a bean from our clipboard file and insert it into
     * the BeanBox.
     */
    private void paste ()
    {
        synchronized (this) {
            mouseClickEvent = null;
        }; // synchronized

        try {
            // Set the insert cursor before reading the clipboard.
            setCursor(crosshairCursor);

            SimpleClassLoader loader = SimpleClassLoader.ourLoader;
            MyProducer p = new MyProducer(BeanBoxFrame.getClipFileName());
            String clipName = BeanBoxFrame.getClipName();
            String beanLabel = BeanBoxFrame.getClipLabel();

            // We need to preserve information about whether or not a bean
            // originated from a .ser file across copies and pastes.
            
            // Beans that originate from a .ser file must be treated
            // effectively as having hidden-state. As such, they must always
            // be serialized. We need to propagate this information to 
            // a newly instantiated beans' wrapper, when we do an insert
            // into the beanbox, so that this information will be available
            // later at code generation time.
            boolean fromPrototypeInfo =BeanBoxFrame.getClipFromPrototypeInfo();
                
            Object bean = loader.instantiate(clipName, p);
            doInsert(bean, beanLabel, clipName, true, fromPrototypeInfo);
        } catch (Exception ex) {
            error("Paste failed", ex);
            pasteMenuItem.setEnabled(false);
            setCursor(defaultCursor);
        }; // try

    }; // paste


    /**********************************************************************
     * Implementation method to make a connection between two beans.
     * We draw a rubber banded line from a source bean until the mouse
     * is clicked on a target bean.
     * @return  the selected target bean.
     */
    private synchronized Wrapper getConnection (Wrapper sourceWrapper)
    {
        connectionSource = sourceWrapper.getChild ();

        // Wait for a mouse click.
        mouseClickEvent = null;

        while (mouseClickEvent == null) {
            try {
                wait ();
            } catch (InterruptedException ex) {
                connectionSource = null;
                return null;
            }; // try
        }; // while

        connectionSource = null;
        Component target = mouseClickEvent.getComponent ();
        Wrapper targetWrapper = Wrapper.findWrapper (target);
        hookupWrappers.add(sourceWrapper);
        hookupWrappers.add(targetWrapper);
        paintConnectedComp( sourceWrapper, targetWrapper);
        return Wrapper.findWrapper (target);

    }; // getConnection


    /**********************************************************************
     * Create an event connection from source to target
     * @param  evt  action event
     */
    void doEventHookup (ActionEvent evt)
    {
        Wrapper sourceWrapper = BeanBoxFrame.getCurrentWrapper ();

        MenuItem mi = (MenuItem) evt.getSource ();
        Menu parent = (Menu) mi.getParent ();

        EventSetDescriptor esd = (EventSetDescriptor) eventMap.get (parent);
        Method meth = (Method) methodMap.get (mi);

        if (esd == null || meth == null) {
            return;
        }; // if

        Wrapper targetWrapper = getConnection (sourceWrapper);

        if (targetWrapper == null) {
            return;
        }; // if

        new EventTargetDialog (getFrame (), sourceWrapper, targetWrapper, esd, meth);

    }; // doEventHookup


    /**********************************************************************
     * Get the address of a target component relative to the 
     * current BeanBox.
     */
    private Rectangle getLocalCoordinates (Component c)
    {
        int width = c.getSize().width;
        int height = c.getSize().height;
        int x = 0;
        int y = 0;
        for (;;) {
            if (c == null) {
                return new Rectangle(0,0,0,0);
            }
            if (c == this) {
                return new Rectangle(x, y, width, height);
            }
            x += c.getLocation().x;
            y += c.getLocation().y;
            c = c.getParent();
        }

    }; // getLocalCoordinates


    /**********************************************************************
     * Paint a rubber-banded line from Component s to Component t
     */  
    private void unpaintConnectedComp (Wrapper s, Wrapper t)
    {
        Component c1 = s.getChild();
        Component c2 = t.getChild();
 
        Graphics g = getGraphics ();
        if (g == null) {
            System.err.println("<test1>");
            return;
        }; // if 
 
        g.setColor ( getBackground());

        if (s == null || t == null) {
            System.err.println("<test2>");
            return;
        }; // if 
 
        Rectangle b1 = getLocalCoordinates (s);
        int x1 = b1.x + (b1.width/2);
        int y1 = b1.y + (b1.height/2);
        Rectangle b2 = getLocalCoordinates (t);
        int x2 = b2.x + (b2.width/2);
        int y2 = b2.y + (b2.height/2);

        g.drawLine (x1, y1, x2, y2);
        g.drawLine (x1+1, y1+1, x2+1, y2+1);
         
    }; // unpaintConnectedComp
 

    /**********************************************************************
     * Paint a rubber-banded line from Component s to Component t
     */  
    private void paintConnectedComp (Wrapper s, Wrapper t)
    {
        Component c1 = s.getChild();
        Component c2 = t.getChild();

        Graphics g = getGraphics ();
        if (g == null) {
            System.err.println("<test1>");
            return;
        }; // if

        g.setColor (Color.yellow);

        if (s == null || t == null) {
            System.err.println("<test2>");
            return;
        }; // if

        Rectangle b1 = getLocalCoordinates (s);
        int x1 = b1.x + (b1.width/2);
        int y1 = b1.y + (b1.height/2);
        Rectangle b2 = getLocalCoordinates (t);
        int x2 = b2.x + (b2.width/2);
        int y2 = b2.y + (b2.height/2);
        
        g.drawLine (x1, y1, x2, y2);
        g.drawLine (x1+1, y1+1, x2+1, y2+1);

    }; // paintConnectedComp



    /**********************************************************************
     * Paint a rubber-banded line from our current connection
     * source "connectionSource" to the specified newX, newY.
     */
    private synchronized void paintConnection (int newX, int newY)
    {
        unpaintConnection ();

        Graphics g = getGraphics ();
        if (g == null) {
            return;
        }; // if

        g.setColor (Color.red);

        Component comp = connectionSource;

        if (comp == null) {
            return;
        }; // if

        Rectangle b = getLocalCoordinates (comp);
        int x2 = b.x + (b.width/2);
        int y2 = b.y + (b.height/2);
        g.drawLine (newX, newY, x2, y2);
        g.drawLine (newX+1, newY+1, x2+1, y2+1);

        oldX1 = newX;
        oldX2 = x2;
        oldY1 = newY;
        oldY2 = y2;

    }; // paintConnection


    /**********************************************************************
     * Erase any outstanding connection rubber-band line.
     */
    private void unpaintConnection ()
    {
        Graphics g = getGraphics ();
        if (g == null || oldX1 < 0) {
            return;
        }; // if
        g.setColor (getBackground ());
        g.drawLine (oldX1, oldY1, oldX2, oldY2);
        g.drawLine (oldX1+1, oldY1+1, oldX2+1, oldY2+1);
        oldX1 = -1;

    }; // unpaintConnection


    /**********************************************************************
     * Repaint the current beanbox. Actually all we need to do
     * is take note that any rubber-band lines or boxes have 
     * been removed.
     */
    public void paint (Graphics g)
    {
        if (oldX1 >= 0) {
            paintConnection (oldX1, oldY1);
        }; // if
        for(int i=0; i< hookupWrappers.size(); i=i+2) {
           Wrapper s = (Wrapper)hookupWrappers.elementAt(i);
           Wrapper t = (Wrapper)hookupWrappers.elementAt(i+1);
           paintConnectedComp( s, t);
        }
        oldRubberBox = null;
        super.paint (g);

    }; // paint


    /**********************************************************************
     * @return my BeanContextServices Delegate ...
     */
    public BeanContextChild getBeanContextProxy ()
    {
        return bcss;

    }; // getBeanContextProxy

    //----------------------------------------------------------------------

    // Mouse listener methods.
    //
    // Note that in general these methods must NOT be "synchronized" or we
    // risk serious deadlock problems.
    //
    // At this level we mostly just do rubber-banding and simple
    // move/resize handling.

    /**********************************************************************
     *
     */
    public void mouseClicked (MouseEvent evt)
    {
        // To workaround #4039858 we do mouseClicked processing in mouseRelease
        // and do nothing here.

    }; // mouseClicked


    /**********************************************************************
     *
     */
    public void mousePressed (MouseEvent evt)
    {
        mousePressedWhen = evt.getWhen();
        // If we're in the middle of making a connection then clear the line.
        if (connectionSource != null) {
            //unpaintConnection();
            repaint();
        }; // if

    }; // mousePressed


    /**********************************************************************
     *
     */
    public void mouseReleased (MouseEvent evt)
    {
        long deltaWhen = (evt.getWhen() - mousePressedWhen);
        mousePressedWhen = 0;

        int x = evt.getX();
        int y = evt.getY();

        // If we're in the middle of a move, complete the move.
        if (moveChild != null) {
            // Make sure the event coordinates are relative to the child.
            if (evt.getComponent() == this) {
                x -= moveChild.getLocation().x;
                y -= moveChild.getLocation().y;
            }; // if
            // OK, insert the current object at the mouse.
            deleteRubberBox();
            finishMove(x, y);
            return;
        }; // if

        // If we're in the middle of a resize, complete the resize.
        if (resizeChild != null) {
            // Make sure the event coordinates are relative to the child.
            if (evt.getComponent() == this) {
                x -= resizeChild.getLocation().x;
                y -= resizeChild.getLocation().y;
            }; // if
            deleteRubberBox();
            finishResize(x, y);
            return;
        }; // if

        // Because of bug #4039858 we get a bogus mouseClicked and mouseReleased
        // event when a menuItem is selected.  Therefore we manually check
        // if there has been a mousePressed within the preceeding 5 seconds before
        // the mouseRelease, and only then do we treat it as a genuine mouse
        // click.                                                KGH 4/3/97
        if (deltaWhen < 5000) {
            // OK, treat this as a genuine mouse click.        
            // Wakeup anyway who is waiting for a mouse down.
            synchronized (this) {
        
                mouseClickEvent = new MouseEvent(evt.getComponent(), evt.getID(),
                        evt.getWhen(), evt.getModifiers(), evt.getX(), evt.getY(),
                         evt.getClickCount(), evt.isPopupTrigger());
                notifyAll();

                // set the new focus, unless we're in the middle of an insert.
                if (getCursor() != crosshairCursor) {
                    Component target = evt.getComponent();
                    BeanBoxFrame.setCurrentComponent(target);
                }; // if

            }; // synchronized
        }; // if

    }; // mouseReleased


    public void mouseEntered (MouseEvent evt) { }

    public void mouseExited (MouseEvent evt) { }


    /**********************************************************************
     *
     */
    public synchronized void mouseDragged (MouseEvent evt)
    {
        int x = evt.getX();
        int y = evt.getY();

        // If we're in the middle of a move, draw a rubber banded box.
        if (moveChild != null) {
            // Make sure the event coordinates are relative to the child.
            if (evt.getComponent() == this) {
                x -= moveChild.getLocation().x;
                y -= moveChild.getLocation().y;
            }
            drawRubberBox(getMoveBox(x, y));
        }

        // If we're in the middle of a resize, draw a rubber-banded
        // resize box.
        if (resizeChild != null) {
            // Make sure the event coordinates are relative to the child.
            if (evt.getComponent() == this) {
                x -= resizeChild.getLocation().x;
                y -= resizeChild.getLocation().y;
            }
            drawRubberBox(getResizeBox(x, y));
        }

    }; // mouseDragged


    /**********************************************************************
     *
     */
    public synchronized void mouseMoved (MouseEvent evt)
    {
        // If we're in the middle of making a connection then do 
        // the rubber-banding of the connection line.
        if (connectionSource != null) {
            Component c = evt.getComponent();
            int x = evt.getX();
            int y = evt.getY();
            while (c != this && c != null) {
                x += c.getLocation().x;
                y += c.getLocation().y;
                c = c.getParent();
            }
            if (c != null) {
                paintConnection(x, y);
            }
        }
        
    }; // mouseMoved

    //----------------------------------------------------------------------

    /**********************************************************************
     * This method is called from a menuWorkerThread to execute
     * the work associated with a given menu event.  Note that a
     * new MenuWorkerThread is created for each menu event, so they
     * can afford to block while they work their way through the
     * processing associated with the menu action.
     */
    void doMenuItem (ActionEvent evt)
    {
        MenuItem mi = (MenuItem) evt.getSource ();
        Menu parent = (Menu) mi.getParent ();
        String label = mi.getLabel ();

        if (parent == null) {
            System.err.println ("doMenuItem: disconnected MenuItem " + mi);
            return;
        }; // if

        if (parent.getLabel ().equals ("File")) {

            if (label.equals ("Exit")) {
               System.exit (0);
            } else if (label.equals ("Clear")) {
                removeAll ();
                for(int i=0; i< hookupWrappers.size(); i=i+2) {
                     Wrapper s = (Wrapper)hookupWrappers.elementAt(i);
                     Wrapper t = (Wrapper)hookupWrappers.elementAt(i+1);
                     unpaintConnectedComp( s, t);
                }
                hookupWrappers.removeAllElements ();
                repaint();
                BeanBoxFrame.setCurrentComponent (null);
            } else if (label.equals ("Save...")) {
                save ();
            } else if (label.equals ("SerializeComponent...")) {
                        serializeComponent();
            } else if (label.equals ("Load...")) {
                load ();
            } else if (label.equals ("LoadJar...")) {
                loadJar ();
            } else if (label.equals ("Print")) {
                print ();
            }; // if

        } else if (parent.getLabel ().equals ("Edit")) {

            if (label.equals ("Cut")) {
                cut ();
            } else if (label.equals ("Copy")) {
                copy ();
            } else if (label.equals ("Customize...")) {
                if (customizerClass != null) {
                    try {
                        Customizer customizer = (Customizer) customizerClass.newInstance ();
                        BeanBoxFrame frame = getFrame ();
                        Object bean = frame.getCurrentBean ();
                        customizer.setObject (bean);
                        new CustomizerDialog (frame, customizer, bean);
                    } catch (Exception ex) {
                        System.err.println ("Couldn't instantiate customizer: " + ex);
                    }; // try
                }; // if
            } else if (label.equals ("Paste")) {
                paste ();
            }; // if

        } else if (parent.getLabel ().equals ("Help")) {

            if (label.equals ("Documentation...")) {
                // This pops up a modal dialog.
                File cwd = new File (System.getProperty("user.dir"));
                File pwd = new File (cwd.getParent());
                File readme = new File (pwd, "README.html");
                String mess = "Use a web browser to view the online documentation starting at\n"
                        + "file://" + readme;
                new MessageDialog (getFrame (), "Documentation", mess);
            }; // if

        } else {

            // The only place left is the "events" submenu.
            // Create an event hookup from a source to a target.
            doEventHookup (evt);
        }; // if

    }; // doMenuItem


    /**********************************************************************
     * Insert a given Component instance into the current BeanBox.
     */

    public void doInsert (Object  bean, 
                          String  beanLabel, 
                          String  beanName, 
                          boolean useOldClick, 
                          boolean fromPrototype)
    {
        // Change the cursor to indicate an "insert".  (We may already
        // have done this, but that's OK.)

        setCursor(crosshairCursor);

        // Wait for a mouse down event.
        MouseEvent evt;
        synchronized (this) {
            if (!useOldClick) {
                mouseClickEvent = null;
            }
            while (mouseClickEvent == null) {
                try {
                    wait();
                } catch (InterruptedException ex) {
                    break;
                }
            }
            evt = mouseClickEvent;
        }

        // Revert to the default cursor.
        setCursor(defaultCursor);

        if (evt == null) {
            // We were interrupted.
            return;
        }

        // If its a BeanBox, color it a darker shade than the current.
        // Nested BeanBoxes are currently disabled - epll.
        if (bean instanceof BeanBox) {
            BeanBox bb = (BeanBox)bean;
            bb.setSize(400,200);
            int r = getBackground().getRed();
            int g = getBackground().getGreen();
            int b = getBackground().getBlue();
            Color newColor = new Color((r*9)/10, (g*9)/10, (b*9)/10);
            bb.setBackground(newColor);
        }

        // Create a Wrapper for the child
        Wrapper child = new Wrapper(bean, beanLabel, beanName);

        // Annotate this wrapper as to whether or not this bean originated
        // from a .ser file.
        child.setFromPrototype(fromPrototype);
        
        // Insert and reshape the child component.
        add(child);
        int childWidth = child.getPreferredSize().width;
        int childHeight = child.getPreferredSize().height;
        int x = evt.getX() - childWidth/2;
        int y = evt.getY() - childHeight/2;
        child.setBounds(x, y, childWidth, childHeight);

        bcss.add(bean); // add the bean to the BeanContext ...

        // If bean has notion of design/runtime mode, initialize it to
        // the mode of the nesting BeanContext.
        if (bean instanceof DesignMode) {
            DesignMode bdm = (DesignMode)bean;
            boolean bcdmode = (boolean)((DesignMode)getBeanContextProxy()).isDesignTime();
            bdm.setDesignTime(bcdmode);
        }

        // Make the child the focus.
        BeanBoxFrame.setCurrentComponent(child);

        // Notice that we do not attach mouse event listeners to the child
        // or to the wrapped bean.  This allows them to use the old
        // AWT event model if the bean is a transitional bean.
        // We rely on the wrapper calling our MouseListener and
        // MouseMotionListener methods when mouse events happen.

    }; // doInsert


    //----------------------------------------------------------------------

    /**********************************************************************
     * Schedule a menu item to be executed asynchronously in the BeanBox's
     * menu handling thread.
     */
    public synchronized void queueMenuItem (ActionEvent evt)
    {
        //doMenuItem (evt);       // FIX - try to handle eveent directly

        if (events == null) {
            events = new Vector ();
        }; // if
        events.addElement (evt);
        notifyAll ();
        if (eventThread == null) {
            eventThread = new Thread (this);
            eventThread.start ();
        }; // if

    }; // queueMenuItem


    /**********************************************************************
     * Main method for our internal MenuItem handling thread.
     * This guy lets us minimize the amount of work we do in the AWT event 
     * thread, which reduces the risk of deadlock.
     */
    public void run ()
    {
        for (;;) {

            // Wait for an event.
            ActionEvent evt;
            synchronized (this) {
                while (events.size () == 0) {
                    try {
                        wait();
                    } catch (InterruptedException ix) {
                    }; // try
                }; // while
                evt = (ActionEvent) events.elementAt (0);
                events.removeElementAt (0);
            }; // synchronized

            // Now process the event.
            try {
                doMenuItem (evt);
            } catch (Throwable ex) {
                System.err.println ("BeanBox caught exception " + ex +
                     " while processing: " + evt.getActionCommand ());
                System.err.println ("  msg: " + ex.getMessage ());
                if (ex instanceof ExceptionInInitializerError) {
                    ExceptionInInitializerError ex2 =
                        (ExceptionInInitializerError) ex;
                    Throwable e = ex2.getException ();
                    e.printStackTrace ();
                }; // if
            }; // try

        } // for

    }; // run


    //----------------------------------------------------------------------

    /**********************************************************************
     * Someone has done a mouse button down on the "wrapper" around the bean.
     * This starts a move that will be terminated when they release the
     * mouse button.  We will draw a rubbed-banded box to indicate the
     * current move position.
     */
    void startMove (Wrapper child, int x, int y)
    {
        moveChild = child;
        moveStartX = x;
        moveStartY = y;

    }; // startMove


    /**********************************************************************
     * Figure out the on-screen box to be drawn to represent the proposed
     * new move location of a bean we're moving.
     */
    Rectangle getMoveBox (int mx, int my)
    {
        int x = moveChild.getLocation().x;
        int y = moveChild.getLocation().y;
        int w = moveChild.getSize().width;
        int h = moveChild.getSize().height;
        x = x + mx - moveStartX;        
        y = y + my - moveStartY;
        return new Rectangle(x, y, w, h);

    }; // getMoveBox


    /**********************************************************************
     * Complete a move started with "startMove".
     */
    void finishMove (int mx, int my)
    {
        Rectangle box = getMoveBox(mx, my);
        moveChild.setBounds(box.x, box.y, box.width, box.height);
        deleteRubberBox();
        moveChild = null;

    }; // finishMove


    //----------------------------------------------------------------------

    /**********************************************************************
     * Someone has done a mouse button down on a corner of the "wrapper"
     * around the bean.  This starts a resize sequence that will be
     * terminated when they release the mouse button.  We will draw a 
     * rubbed-banded box to indicate the proposed resize shape..
     */
    void startResize (Wrapper child, int x, int y, Cursor cursor)
    {
        resizeStartX = child.getLocation().x + x;
        resizeStartY = child.getLocation().y + y;
        resizeChild = child;
        resizeCursor = cursor;

    }; // startResize


    /**********************************************************************
     * Figure out the box to draw to imndicate the result of an
     * in-progress resize.  This is slightly complicated because
     * it depends on which corner they picked-up to begin the
     * resize.
     */
    Rectangle getResizeBox (int mx, int my)
    {
        int x = resizeChild.getLocation().x;
        int y = resizeChild.getLocation().y;
        int w = resizeChild.getSize().width;
        int h = resizeChild.getSize().height;
        if (resizeCursor == nwResizeCursor) {
            w = w - mx;
            h = h - my;
            x = x + mx;
            y = y + my;
        } else if (resizeCursor == swResizeCursor) {
            w = w - mx;
            h = my;
            x = x + mx;
        } else if (resizeCursor == neResizeCursor) {
            w = mx;
            h = h - my;
            y = y + my;
        } else if (resizeCursor == seResizeCursor) {
            w = mx;
            h = my;
        }
        if (w < 10) {
            w = 10;
        }
        if (h < 10) {
            h = 10;
        }
        return new Rectangle(x, y, w, h);

    }; // getResizeBox


    /**********************************************************************
     * Complete a resize begin with startResize.
     */
    void finishResize (int mx, int my)
    {
        Rectangle box = getResizeBox(mx, my);
        resizeChild.setBounds(box.x, box.y, box.width, box.height);
        resizeChild.doLayout();
        deleteRubberBox();
        resizeChild = null;

    }; // finishResize

    //----------------------------------------------------------------------

    /**********************************************************************
     *
     */
    private void drawRubberBox (Rectangle box)
    {
        Graphics g = getGraphics();
        if (g == null) {
            return;
        }
        // First remove any existing rubber box.
        deleteRubberBox();
        // Now draw the new rubber box.
        g.setColor(Color.red);
        g.drawRect(box.x, box.y, box.width, box.height);        
        oldRubberBox = box;

    }; // drawRubberBox


    /**********************************************************************
     *
     */
    private void deleteRubberBox ()
    {
        if (oldRubberBox != null) {
            Graphics g = getGraphics();
            if (g == null) {
                return;
            }
            g.setColor(getBackground());
            g.drawRect(oldRubberBox.x, oldRubberBox.y, 
                        oldRubberBox.width, oldRubberBox.height);        
            oldRubberBox = null;
        }

    }; // deleteRubberBox


    //----------------------------------------------------------------------

    /**********************************************************************
     * Figure out the top-level BeanBoxFrame that contains the current
     * BeanBox.
     */
    private synchronized BeanBoxFrame getFrame ()
    {
        if (ourFrame != null) {
            return ourFrame;
        }
        Component c = this;
        while (c != null) {
            if (c instanceof BeanBoxFrame) {
                ourFrame = (BeanBoxFrame) c;
                return ourFrame;
            }
            c = c.getParent();
        }
        // This should never happen
        throw new Error("Couldn't find frame ?!?");

    }; // getFrame


    /**********************************************************************
     * Log an error.
     */
    void error (String message, Throwable th)
    {
        String mess = message + ":\n" + th;
        System.err.println (message);
        th.printStackTrace ();

        // Popup an ErrorDialog with the given error message.
        new ErrorDialog (getFrame (), mess);

    }; // error


    /**********************************************************************
     *   
     */
    void error (String message)
    {
        System.err.println(message);
        // Popup an ErrorDialog with the given error message.
        new ErrorDialog(getFrame(), message);

    }; // error


    private static boolean debug = false;

    /**********************************************************************
     *   
     */
    static void debug (String msg)
    {
        if (debug) {
            System.err.println("BeanBox:: "+msg);
        }; // debug

    }; // debug


    //----------------------------------------------------------------------
 
    private static int FILE_MENUID = 0;
    private static int EDIT_MENUID = 1;

    transient BeanContextServicesSupport bcss = new BeanContextServicesSupport();

    transient BeanBoxFrame ourFrame;
    transient MenuItem pasteMenuItem;

    transient Component connectionSource;
    transient int oldX1 = -1;
    transient int oldX2;
    transient int oldY1;
    transient int oldY2;
    transient Rectangle oldRubberBox;
  
    transient Hashtable methodMap;   // maps MenuItems to Methods
    transient Hashtable eventMap;    // maps Menus to EventSetDescriptors
    transient Class customizerClass;

    private static Hashtable serNames = new Hashtable();

    // If moveChild is non-null, we're moving that child
    transient Wrapper moveChild;
    transient int moveStartX;
    transient int moveStartY;

    // If resizeChild is non-null, we're resizing that child
    transient Wrapper resizeChild;
    transient Cursor resizeCursor;
    transient int resizeStartX;
    transient int resizeStartY;

    private static transient int unique = 0;

    private final String  ibn = "beanbox_default_" + unique++;
    private transient boolean servicesVisible = true;

    private final static int padX = 10;
    private final static int padY = 10;

    private final static String defaultStoreFile = "beanbox.tmp";
    private final static String defaultSerializeFile = "example.ser";

    // We keep a private event queue and a thread to process them.
    transient private Thread eventThread;
    transient private Vector events;

    private static Vector hookupWrappers = new Vector();
    // MouseClickEvent describes the moset recent mouse click event.
    private transient MouseEvent mouseClickEvent;
    private transient long mousePressedWhen = 0;

    // Shorthands for the cursors.
    private static Cursor nwResizeCursor = Cursor.getPredefinedCursor(Cursor.NW_RESIZE_CURSOR);
    private static Cursor neResizeCursor = Cursor.getPredefinedCursor(Cursor.NE_RESIZE_CURSOR);
    private static Cursor swResizeCursor = Cursor.getPredefinedCursor(Cursor.SW_RESIZE_CURSOR);
    private static Cursor seResizeCursor = Cursor.getPredefinedCursor(Cursor.SE_RESIZE_CURSOR);
    private static Cursor crosshairCursor = Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR);
    private static Cursor defaultCursor = Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR);

}; // class


/**************************************************************************
 * Auxiliary class to deliver the InputStream
 */

class MyProducer implements InputStreamProducer
{
    private String name;


    /**********************************************************************
     *
     */
    public MyProducer (String fileName)
    {

        this.name = fileName;

    }; // MyProducer


    /**********************************************************************
     *
     */
    public synchronized InputStream getInputStream ()
    {
        try {
            return new FileInputStream(name);
        } catch (Exception ex) {
            return null;
        } // try

    }; // getInputStream


}; // class

