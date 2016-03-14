/*
GNU Mages, version 1.00 alpha
Multi-Agents Environment Simulator
Copyright (C) 2001-2002 João Ricardo Bittencourt <jrbitt@uol.com.br>
 
This program is free software; you can redistribute it and/or modify 
it under the terms of the GNU General Public License as published by 
the Free Software Foundation; either version 2 of the License, or 
(at your option) any later version. 

This program is distributed in the hope that it will be useful, 
but WITHOUT ANY WARRANTY; without even the implied warranty of 
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the 
GNU General Public License for more details. 

You should have received a copy of the GNU General Public License 
along with this program; if not, write to the Free Software 
Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.
*/

package mages.gui;

import java.awt.*; 
import java.applet.*; 
import java.awt.image.*; 
import java.awt.event.*; 

// Import the complete GameLib package 
import nu.bugbase.gamelib.sprite.*; 
import nu.bugbase.gamelib.utils.*;  
import nu.bugbase.gamelib.background.*; 

public abstract class Game extends Panel implements Runnable{ 
	public static final int GAME_STATE_INIT = 0;
	public static final int GAME_STATE_MENU = 1;
	public static final int GAME_STATE_START = 2;
	public static final int GAME_STATE_RUN = 3;
	public static final int GAME_STATE_SHUTDOWN = 4;
	public static final int GAME_STATE_EXIT = 5;					
 
	private int gameState;
	private boolean waitingForRepaint = false; 
	private BufferedImage bimg = null; 
	private BufferedImage background = null; 	
	private Graphics2D bg2 = null; 
   protected GameController control = null; 
   private int speed = 30;
   protected Frame f = null; 	   
    				
	public abstract int mainMenu();	
	public abstract void shutdown();	
	public abstract String getName();
   public abstract void initEvents();   	      
   public abstract void configController();

	public void init(){ 
	 setBackground(Color.black); 
	 setSize(new Dimension(300,300));
	}       
      
   public void setSpeed(int s){
     speed = s;
   }
   
	public void initGame(){	  
	  init();
	  configController();
	  gameState = GAME_STATE_INIT;	  
	}
	   
	public void start(){ 
		Thread t = new Thread(this); 
		t.start(); 
	} 

	public void startGame(){
     gameState = mainMenu();
     switch(gameState){
       case GAME_STATE_START:
         start();       
       break;

       case GAME_STATE_SHUTDOWN:
         shutdown();       
         gameState = GAME_STATE_EXIT;
       break;       
       
       default: 
       break;
     }
	}
	
	private void initGfx(){ 
		// Creates a doublebuffer image. 
		Dimension gm = getSize();				
		bimg = (BufferedImage) createImage(gm.width, gm.height); 
		bg2 = bimg.createGraphics(); 
		bg2.setBackground(Color.black); 
		bg2.setColor(Color.white); 
		bg2.setRenderingHint(RenderingHints.KEY_RENDERING, 
							 RenderingHints.VALUE_RENDER_SPEED); 	
		control.init();
		initEvents();
	} 

	public synchronized void updateScreen(Graphics g){ 
		if (bimg == null) { 
			initGfx(); 
		} 

		if(g != null) { 
			waitingForRepaint = false; 
			Graphics2D g2 = (Graphics2D)g; 
			g2.setRenderingHint(RenderingHints.KEY_RENDERING, 
								RenderingHints.VALUE_RENDER_SPEED); 
			g2.drawImage(bimg, 0, 0, this); 
		} 
		else { 
         control.draw(bg2);
		} 
	} 

	public void paint(Graphics g){ 
		updateScreen(g); 
	} 


	public void update(Graphics g){ 
		updateScreen(g); 
	} 


	public void run(){ 
	   gameState = GAME_STATE_RUN;
		Ticker ticker = SystemToolkit.getTicker(); 
		Toolkit tk = this.getToolkit(); 
		long delay = ticker.getTickFrequence()/speed; 
		long tm = 0; 
 
		while(gameState!=GAME_STATE_EXIT) { 
			tm = ticker.getTickCounter() + delay; 
			updateScreen(null); 
			if(!waitingForRepaint) { 
				waitingForRepaint = true; 
				repaint(0); 
			} 
			tk.sync(); 
			ticker.sleepTo(tm); 
		} 
	} 

	public Dimension getPreferredSize(){ 
		Dimension gm = getSize();			  
		return(new Dimension(gm.width,gm.height)); 
	} 
			
   public void createFrame(Game g){         
		f = new Frame(g.getName()); 	

		Dimension game = getSize();	
		Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();        
      f.setResizable(true); 		
		f.addWindowListener(new CloseFrame(f));       	
  	   f.setLayout(new GridLayout(1,1));		
		if(game.width>=screen.width || game.height>=screen.height){
  		  f.setSize(screen.width,screen.height);							  		
		  ScrollPane scroll = new ScrollPane();
		  scroll.setSize(screen.width,screen.height);							  		
		  scroll.add(g);
		  f.add(scroll);
		}
		else{		
  		  f.setSize(game.width+6,game.height+25);							    		    
		  f.add(g); 		    
		}				
		f.pack(); 
		Rectangle bounds = new Rectangle(screen.width,screen.height); 
		Rectangle abounds = f.getBounds(); 
		f.setLocation(bounds.x + (bounds.width - abounds.width) / 2, 
  					     bounds.y + (bounds.height - abounds.height) / 2); 
		f.show();   
   }     

   
	protected GameController getControl(){
	  return control;
	}   
	
   protected class CloseFrame extends WindowAdapter{
     Frame fr;
     
     public CloseFrame(Frame f){
       fr = f;
     } 

   	public void windowClosing(WindowEvent e) { 
	      shutdown();
         gameState = GAME_STATE_EXIT;			   
     	   fr.dispose(); 	             
   	} 
   }	   
}