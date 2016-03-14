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

package mages.gui.ctf;

import mages.util.*;
import mages.*;
import mages.agents.*;
import mages.controller.ctf.*;
import mages.controller.ctf.server.*;
import mages.domain.ctf.*;
import java.awt.*;
import nu.bugbase.gamelib.sprite.*; 
import nu.bugbase.gamelib.utils.*; 

public class Bot2D{  

  public static final int DIE = 0;
  public static final int SHOT = 1;  
  public static final int DODGE = 2;  
  public static final int GO = 5;    
  public static final int REC = 8;    
  public static final int RUN = 11;    
  public static final int STOP = 14;    
  
  Sprite sprite;    
  CtFBotServer cbs;
  int xfactor, yfactor;
  String sufix[] = {"_die_0","_shot_0","_dodge_","_go_","_rec_","_run_","_stop_"};
          
  public Bot2D(CtFBotServer bs, int xf, int yf){    
    String prefix = null;
    cbs = bs;
    xfactor = xf;
    yfactor = yf;
    if(cbs.getBot().getId()==CtFSettings.RED){
      prefix = "ctf/red";
    }
    else{
      if(cbs.getBot().getId()==CtFSettings.BLUE){
        prefix = "ctf/blue";      
      }
    }  
       
    SpriteImage imgs[] = new SpriteImage[17];
    
    int cnt = 0;
    for(int k=0; k<2; k++){
      String path = MagesEnv.getFilesDir("simObjects")+MagesEnv.validate(prefix+sufix[k]+".gif");
      imgs[cnt++] = new SpriteImage(MagesEnv.loadImage(path));          
    }
    
    for(int i=2 ;i<sufix.length; i++){
      for(int j=0; j<3; j++){
        String path = MagesEnv.getFilesDir("simObjects")+MagesEnv.validate(prefix+sufix[i]+j+".gif");
        imgs[cnt++] = new SpriteImage(MagesEnv.loadImage(path));      
      }
    }
    
    sprite = new Sprite(imgs);
  }   
  
  private int definePos(){
    if(cbs.getBot().isDead()){
      return DIE;
    }
    else{
      Action act = cbs.getAction();
      if(act instanceof AttackAct){
        return SHOT;
      }
      else{
        if(act instanceof DodgeAct){
          return DODGE;
        }      
        else{
          if(act instanceof MoveAct){
            String tmp[] = act.getParameters();
            if(Integer.parseInt(tmp[1])==MoveAct.RUN){
              return RUN;
            }
            else{
              return GO;
            }
          }        
          else{
            if(act instanceof RecoveryAct){
              return REC;
            }          
          }
        }
      }
    }
    return STOP;
  }
          
  public void draw(Graphics2D g2){
    int pos = definePos();
    int index = 0;
    if(pos==DIE || pos==SHOT){
      index = pos;
    }
    else{
       if(cbs.getBot().hasFlag()){
         if(cbs.getBot().getFlag().getColor()==cbs.getBot().getId()){
           index = pos + 1;
         }
         else{
           index = pos + 2;
         }
       }
       else{
         index = pos;
       }
    }
    Coord c = cbs.getBot().getPosition();
    sprite.setPosition(c.getX()*xfactor,c.getY()*yfactor);       
    sprite.draw(g2,index);  
  }     
  
  public void setBotServer(CtFBotServer c){
    cbs = c;
  }
}