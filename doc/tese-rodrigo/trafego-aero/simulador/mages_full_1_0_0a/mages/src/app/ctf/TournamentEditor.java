/*
GNU Mages, version 1.00 alpha
Multi-Agents Environment Simulator
Copyright (C) 2001-2002 Jo�o Ricardo Bittencourt <jrbitt@uol.com.br>
 
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

import mages.*;
import mages.gui.*;
import mages.app.ctf.TournamentController;
import mages.app.ctf.TournamentControlable;

public class TournamentEditor extends GraphicEditor{

  public TournamentEditor() {
    super(GraphicEditor.NO_USE_FILEDIALOG);
    setTitle("Tournament Editor v.1.0a");
  }
  
  public void init(){
    TournamentControlable tc = new TournamentControlable();  
    tc.setEditor(this);    
    tc.init();
    tc.setEnabled(false);
    add(tc);    

    setController(new TournamentController());
    setControled(tc);
    pack();     
    
    setDefaultDir(MagesEnv.getFilesDir("ctfTourn"));
  }

  public String[] getValues(){
    return null;
  }
  
  public static void main(String args[]){ 
    TournamentEditor te = new TournamentEditor();
    te.show();
  }   
}