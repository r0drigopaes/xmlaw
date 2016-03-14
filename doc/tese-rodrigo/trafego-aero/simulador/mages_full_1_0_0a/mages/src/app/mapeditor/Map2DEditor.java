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

import mages.gui.*;
import mages.app.mapeditor.GraphicManager;
import mages.app.mapeditor.MapController;


import mages.*;

import java.io.*;

public class Map2DEditor extends GraphicEditor{

  public Map2DEditor() {
    super(GraphicEditor.NO_USE_FILEDIALOG);
    setTitle("Map 2D Editor v.1.0a");
  }
  
  public void init(){
    GraphicManager gm = new GraphicManager();  
    gm.setEditor(this);    
    gm.setEnabled(false);
    add(gm);    

    setController(new MapController());
    setControled(gm);
    pack();     
    
    setDefaultDir(MagesEnv.getFilesDir("maps"));
  }

  public String[] getValues(){
    return null;
  }
  
  public static void main(String args[]){ 
 try{
    FileOutputStream file = new FileOutputStream("debug.txt");
    PrintStream ps = new PrintStream(file);
    System.setOut(ps);
   }    
  catch(Exception e){  }
    Map2DEditor me = new Map2DEditor();
    me.show();
  }
}