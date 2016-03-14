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

import java.io.*;
import java.awt.*;
import javax.swing.*;
import javax.swing.event.*;

public class InfoBox extends JDialog{

  public InfoBox(Frame own, String nFile, String title, Dimension dim){
    super(own);
    setTitle(title);
    setModal(true);
    setSize(dim.width,dim.height);    
    JTextArea pane = null;      
    try{
    System.out.println(nFile);
      pane = new JTextArea();
      pane.setEditable(false);
      pane.setLineWrap(true);

      FileReader file = new FileReader(nFile);  
      BufferedReader stream = new BufferedReader(file);       
      boolean more = true; 
      while(more){      
        String line = stream.readLine();        
        if(line != null){         
          pane.append(line+"\n");
        }
        else{
          more = false;
        }
      }  
      stream.close();
      setContentPane(new JScrollPane(pane));
    }    
    catch(IOException e){ System.err.println(e.getMessage());}
  }
}