/******************************************************************
 * @(#) FrameMenuBar.java     1.3     98/4/16
 * 
 * Copyright (c) 2005, John Miller
 * All rights reserved.
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *   
 * 1. Redistributions of source code must retain the above
 *    copyright notice, this list of conditions and the following
 *    disclaimer. 
 * 2. Redistributions in binary form must reproduce the above
 *    copyright notice, this list of conditions and the following
 *    disclaimer in the documentation and/or other materials
 *    provided with the distribution. 
 * 3. Neither the name of the University of Georgia nor the names
 *    of its contributors may be used to endorse or promote
 *    products derived from this software without specific prior
 *    written permission. 
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND 
 * CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, 
 * INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF 
 * MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE 
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR 
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, 
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, 
 * BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR 
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
 * HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR 
 * OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, 
 * EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 * @version     1.3, 28 Jan 2005
 * @author      John Miller
 */

package jsim.jmodel;

import java.awt.*;
import javax.swing.*;


/******************************************************************
 * The menu bar for the JMODEL designer.
 */

public class FrameMenuBar extends JMenuBar
{
    /////////////////// Immutable Variables \\\\\\\\\\\\\\\\\\\\\\\
    /**
     * Drawing canvas
     */
    private final NetCanvas  localCanvas;

    /**
     * Containing frame
     */
    private final NetFrame   localFrame;

    /**
     * First menu in bar
     */
    private final FileMenu   file;

    /**
     * Second menu in bar
     */
    private final JMenu      setup;

    /**
     * Third menu in bar
     */
    private final HelpMenu   help;

	/**
	 * Unique identifier of serialized object
	 */
	private static final long serialVersionUID = 6587219786742100592L;

    /************************************************************
     * Construct a menu bar for the JMODEL frame (NetFrame).
     * @param  locCan    local canvas
     * @param  locFrame  local frame
     */
    public FrameMenuBar (NetCanvas locCan, NetFrame locFrame)
    {
        super ();

        localCanvas = locCan;
        localFrame  = locFrame;

        file  = new FileMenu ("FILE", localFrame, localCanvas);
        setup = new JMenu    ("SETUP");
        help  = new HelpMenu ("HELP");

        add (file);
        add (setup);
        add (help);

        //setHelpMenu (help);    // not yet implemented in JDK

    }; // FrameMenuBar


}; // class

