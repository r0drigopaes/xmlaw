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

package mages.controller.ctf.client;

import mages.controller.sensors.*;
import mages.agents.*;
import mages.domain.ctf.*;
import mages.util.*;
import java.util.*;


public class CtFSensoryInfo extends SensoryInformation{
  
  final int MAX = 12;
  
  GPS gps = null;
  Sonar sonarRedFlag = null;
  Sonar sonarRedBots = null;
  Sonar sonarBlueFlag = null;
  Sonar sonarBlueBots = null;  
  DistanceAndAngle daaRedFlag = null;
  DistanceAndAngle daaRedBots = null;  
  DistanceAndAngle daaBlueFlag = null;
  DistanceAndAngle daaBlueBots = null;    
  Direction dir = null;
  GPS base = null;
  Grid terrain = null;  
    
  public CtFSensoryInfo(){
    gps = new GPS();
    sonarRedFlag = new Sonar("Red Flags");
    sonarRedBots = new Sonar("Red Bots");
    sonarBlueFlag = new Sonar("Blue Flags");
    sonarBlueBots = new Sonar("Blue Bots");  
    daaRedFlag = new DistanceAndAngle("Red Flags");
    daaRedBots = new DistanceAndAngle("Red Bots");  
    daaBlueFlag = new DistanceAndAngle("Blue Flags");
    daaBlueBots = new DistanceAndAngle("Blue Bots");          
    dir = new Direction();
    base = new GPS();
    terrain = new Grid(1);
  }
  
  public Object[] value(long timestamp){
    Object[] tmp = new Object[MAX];
    tmp[0] = gps.value(timestamp);
    tmp[1] = sonarRedFlag.value(timestamp);
    tmp[2] = sonarRedBots.value(timestamp);    
    tmp[3] = sonarBlueFlag.value(timestamp);
    tmp[4] = sonarBlueBots.value(timestamp);
    tmp[5] = daaRedFlag.doubleValue(timestamp);
    tmp[6] = daaRedBots.doubleValue(timestamp);    
    tmp[7] = daaBlueFlag.doubleValue(timestamp);    
    tmp[8] = daaBlueBots.doubleValue(timestamp);                    
    tmp[9] = new Integer(dir.value(timestamp));
    tmp[10] = base.value(timestamp);    
    tmp[11] = terrain.doubleValue(timestamp);
    return tmp;
  }

  public void setValue(Object[] v){ 
    gps.setValue(v[0]);
    sonarRedFlag.setValue(v[1]);
    sonarRedBots.setValue(v[2]);    
    sonarBlueFlag.setValue(v[3]);
    sonarBlueBots.setValue(v[4]);            
    daaRedFlag.setValue(v[5]);
    daaRedBots.setValue(v[6]);    
    daaBlueFlag.setValue(v[7]);    
    daaBlueBots.setValue(v[8]);                
    dir.setValue(v[9]);
    base.setValue(v[10]);
    terrain.setValue(v[11]);    
  }
  
  //0 Bot source  
  //1 Map
  //2 array of flags (all)
  //3 array of bases
  //4 array of bots (include myself)
  public void config(Object[] prm){       
    gps = new GPS((Posicionable)prm[0]);
    dir = new Direction((Directionable)prm[0]);    
    int id = ((Identificable)prm[0]).getId();
        
    Flag flags[] = (Flag[])prm[2];
    Vector redFlagsVec = new Vector();
    Vector blueFlagsVec = new Vector();   
    for(int i=0; i<flags.length; i++){
      if(flags[i].getColor()==CtFSettings.RED){
        redFlagsVec.addElement(flags[i]);
      }
      else{
        if(flags[i].getColor()==CtFSettings.BLUE){
          blueFlagsVec.addElement(flags[i]);       
        }
      }
    }

    Bot bots[] = (Bot[])prm[4];
    Vector redBotsVec = new Vector();
    Vector blueBotsVec = new Vector();   
    for(int i=0; i<bots.length; i++){
      if(bots[i]!=(Posicionable)prm[0]){
        if(bots[i].getId()==CtFSettings.RED){
          redBotsVec.addElement(bots[i]);
        }
        else{
          if(bots[i].getId()==CtFSettings.BLUE){
            blueBotsVec.addElement(bots[i]);       
          }
       }     
      }     
    }

    Flag redFlags[] = new Flag[redFlagsVec.size()];
    for(int j=0; j<redFlags.length; j++){
      redFlags[j] = (Flag)redFlagsVec.elementAt(j);
    }

    Flag blueFlags[] = new Flag[blueFlagsVec.size()];
    for(int j=0; j<blueFlags.length; j++){
      blueFlags[j] = (Flag)blueFlagsVec.elementAt(j);
    }

    Bot redBots[] = new Bot[redBotsVec.size()];
    for(int j=0; j<redBots.length; j++){
      redBots[j] = (Bot)redBotsVec.elementAt(j);
    }
      
    Bot blueBots[] = new Bot[blueBotsVec.size()];
    for(int j=0; j<blueBots.length; j++){
      blueBots[j] = (Bot)blueBotsVec.elementAt(j);
    }
  
    World map = (World)prm[1];  
    MagesDimension dim = new MagesDimension(map.getMap().getWidth(),map.getMap().getHeight());            
    Coord bases[] = map.getBases();
    base = new GPS(bases[id]);
         
    sonarRedFlag = new Sonar((Posicionable)prm[0],redFlags,10,CtFSettings.RED,"Red Flag",dim);
    sonarRedBots = new Sonar((Posicionable)prm[0],redBots,10,CtFSettings.RED,"Red Bots",dim);   
    sonarBlueFlag = new Sonar((Posicionable)prm[0],blueFlags,10,CtFSettings.BLUE,"Blue Flag",dim);
    sonarBlueBots = new Sonar((Posicionable)prm[0],blueBots,10,CtFSettings.BLUE,"Blue Bots",dim);          
    
    daaRedFlag = new DistanceAndAngle("Red Flags",(Directionable)prm[0],sonarRedFlag.getSixSectors(),redFlags.length,dim);    
    daaRedBots = new DistanceAndAngle("Red Bots",(Directionable)prm[0],sonarRedBots.getSixSectors(),redBots.length,dim);    
    daaBlueFlag = new DistanceAndAngle("Blue Flags",(Directionable)prm[0],sonarBlueFlag.getSixSectors(),blueFlags.length,dim);    
    daaBlueBots = new DistanceAndAngle("Blue Bots",(Directionable)prm[0],sonarBlueBots.getSixSectors(),blueBots.length,dim);        

    terrain = new Grid((Posicionable)prm[0],map.getCostTerrain(),1);
    addNode(gps);
    addNode(daaRedFlag);
    addNode(daaRedBots);
    addNode(daaBlueFlag);
    addNode(daaBlueBots);            
  }
  
  public Sensor[] getSensors(){
    Sensor tmp[] = new Sensor[MAX];   
    tmp[0] = gps;    
    tmp[1] = sonarRedFlag;
    tmp[2] = sonarRedBots;
    tmp[3] = sonarBlueFlag;
    tmp[4] = sonarBlueBots;        
    tmp[5] = daaRedFlag;
    tmp[6] = daaRedBots;    
    tmp[7] = daaBlueFlag;    
    tmp[8] = daaBlueBots;                    
    tmp[9] = dir;
    tmp[10] = base;
    tmp[11] = terrain;
    return tmp;    
  }  
}

