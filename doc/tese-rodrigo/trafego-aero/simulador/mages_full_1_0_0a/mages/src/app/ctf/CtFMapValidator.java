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

package mages.app.ctf;

import mages.domain.ctf.*;
import mages.app.*;
import mages.*;
import mages.simulation.*;

import java.io.*;

public class CtFMapValidator implements IValidator{

  public boolean validate(Object src, String[] args) throws IOException{
    if(args[0]!=null){
      String path = MagesEnv.getFilesDir("ctfTourn")+args[0];
      Map map = (Map)src;
      TournamentController tc = new TournamentController();
      Tournament tourn = (Tournament)tc.load(path);
      Layer objects = map.getLayer(1);
      int flagsBlue = 0;
      int flagsRed = 0;      
      for(int r=0; r<objects.getHeight(); r++){
        for(int c=0; c<objects.getWidth(); c++){
          SimObject so = objects.getCell(c,r);
          if(so!=null){
            if(so.getName().equals("Flag Blue")){
              flagsBlue++;
            }
            else{
              if(so.getName().equals("Flag Red")){
                flagsRed++;
              }
            }
          }
        }
      }//end for
      return flagsBlue<=tourn.getMaxFlag() && flagsRed<=tourn.getMaxFlag();
    }
    return false;   
  }  
  
  public String getErrorMessage(){
    return "Pretty attention in number of flags";
  }  
}