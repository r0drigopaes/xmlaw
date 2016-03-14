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

package mages.domain.ctf;

import mages.simulation.*;
import mages.fuzion.*;
import mages.util.*;
import java.util.*;

public class CtFRules{
  public static final int SIMPLE = 0;
  public static final int AUTOFIRE = 1;  
  public static final int HAND_TO_HAND = 2;    
  
  int MAX_TURNS;
  World world = null;
    
  int turn = 0; 

  public CtFRules(World w, int mt){
    world = w;
    MAX_TURNS = mt;
  }
  
  public CtFRules(World w){
    world = w;
    MAX_TURNS = 3600;
  }
      
  public World getWorld(){
    return world;
  }
   
  //Turns 
  public boolean isFinished(){
    boolean f1 = (turn<MAX_TURNS)?false:true;
    boolean f2 = statusBots();
    boolean f3 = statusFlags();    
    return f1 || f2 || f3;
  }
  
  public int getTurn(){
    return turn;
  }
  
  public int getMaxTurns(){
    return MAX_TURNS;
  }
  
  public void passTurn(){
    turn++;
  }

  private boolean statusFlags(){
    return world.allFlagsInBase(CtFSettings.RED) || world.allFlagsInBase(CtFSettings.BLUE);
  }
    
  private boolean statusBots(){
    Bot tmp[] = world.getBots();
    int c = 0;
    for(int i=0; i<tmp.length; i++){
      if(tmp[i].isDead()) c++;    
    }
    return (c<tmp.length)?false:true;
  }
  
  private int invert(int d){
    int v = d;
    switch(d){
      case CtFSettings.NORTH: v = CtFSettings.SOUTH;
      break;
      case CtFSettings.SOUTH: v = CtFSettings.NORTH;
      break;
      case CtFSettings.NORTHWEST: v = CtFSettings.SOUTHWEST;
      break;           
      case CtFSettings.SOUTHWEST: v = CtFSettings.NORTHWEST;
      break;      
      case CtFSettings.SOUTHEAST: v = CtFSettings.NORTHEAST;
      break;      
      case CtFSettings.NORTHEAST: v = CtFSettings.SOUTHEAST;
      break;      
    }
    return v;
  }
  
  //Actions
  public void move(Bot b, int degree, int qnt, double act){
    int d = (degree>360)?360:degree;
    d = (d<0)?0:d;
    Coord tmpTrg = Combat.getTarget(b.getPosition(),invert(d),qnt);
    int x1 = (int)(tmpTrg.getDoubleX());
    int y1 = (int)(tmpTrg.getDoubleY());    
    Coord target = new Coord(x1,y1);    
    

    b.rotate(d);
    Coord line[] = BresenhamLine.line(b.getPosition(),target);
    double fmove = factorMove(line);
    int nqnt = (int)(line.length*(1-fmove));
    nqnt = (nqnt<=1)?2:nqnt; //anda no mínimo 1
    int i=1;
    boolean pathFree = true;
    while(i<=(nqnt-1) && pathFree){
      int x = line[i].getX();
      int y = line[i].getY();
      if(world.canMove(x,y)){
        world.remove(b.getPosition().getX(),b.getPosition().getY(),World.BOTS);
        world.set(b,x,y,World.BOTS);
        double ct = world.getCostTerrain(x,y);        
        double ffat = 1+(1-ct);        
        b.fatigue(ffat*act);
        i++;
      }
      else{
        pathFree = false;
      }
    }
  }
  
  private double factorMove(Coord line[]){
    double sum = 0;
    double valids = 0;
    boolean isFree = true;
    for(int i=1; i<line.length; i++){
      int x = line[i].getX();
      int y = line[i].getY();
      if(world.isValid(x,y) && world.canMove(x,y) && isFree){
        sum += world.getCostTerrain(line[i].getX(), line[i].getY());
        valids++;
      }
      else{
        isFree = false;
      }
    }

    double res = 0;
    if(sum!=0){
      res = sum/valids;    
    }
    return res;
  }
 
  private Coord frontOfBot(Bot b){
    int x = b.getPosition().getX();
    int y = b.getPosition().getY();  
    int xf = 0;
    int yf = 0;    
    switch(b.getDirection()){
      case CtFSettings.NORTH: xf=x; yf=y-1;
      break;
      case CtFSettings.NORTHWEST: xf=x-1; yf=y-1;
      break;      
      case CtFSettings.NORTHEAST: xf=x+1; yf=y-1;      
      break;      
      case CtFSettings.SOUTH: xf=x; yf=y+1; 
      break;      
      case CtFSettings.SOUTHWEST: xf=x-1; yf=y+1;      
      break;      
      case CtFSettings.SOUTHEAST: xf=x+1; yf=y+1;            
      break;      
      case CtFSettings.WEST: xf=x-1; yf=y;
      break;      
      case CtFSettings.EAST: xf=x+1; yf=y;           
      break;      
    }
    return new Coord(xf,yf);    
  }  
  
  public void getFlag(Bot b){  
    Coord fb = frontOfBot(b);
    int xf = fb.getX();
    int yf = fb.getY();   
    System.out.println("get_flag "+b.getDirection()+" "+xf+" "+yf);
    if(world.isOccupied(xf,yf,World.OBJECTS)){
      if(world.get(xf,yf,World.OBJECTS) instanceof Flag){
        Flag f = (Flag)world.get(xf,yf,World.OBJECTS);
        if(b.hasFlag()){
          dropFlag(b);
        }
        b.getFlag(f);
        world.remove(xf,yf,World.OBJECTS);        
      }
    }
  }

  public void dropFlag(Bot b){
    Coord fb = frontOfBot(b);    
    int xf = fb.getX();
    int yf = fb.getY();         
    if(world.isValid(xf,yf) && !world.isOccupied(xf,yf,World.OBJECTS) && 
      world.getCostTerrain(xf,yf)<1.0){
      Flag f = b.dropFlag();
      if(f!=null){        
        b.resetFlag();    
        world.set(f,xf,yf,World.OBJECTS);
      }
    }
  }  
  
  public void recovery(Bot b){
    b.recovery();
  }
  
  public void changeWeapon(Bot b, int i){
    b.activeWeapon(i);
  }
  
  public void defense(Bot b, boolean f){
    b.fatigue(CtFSettings.DEFENSE_CST); 
    b.setDodge(f);
  }
  
  public void rotate(Bot b, int degree){
    b.rotate(degree);
  }
  
  public void attack(Bot atk, Bot[] target, int mode, int nshots){
    atk.fatigue(CtFSettings.ATTACK_CST);
    int ma = atk.getModFatigue();
    
    if(mode!=HAND_TO_HAND && atk.getActiveWeapon().getWeaponClass()==Weapon.RANGE){    
      if(mode==AUTOFIRE){
        if(atk.getActiveWeapon().getType()==Weapon.AUTOFIRE){
          int mds[] = new int[target.length];
          for(int i=0; i<mds.length; i++){
            mds[i] = target[i].getModFatigue();
          }
          Combat.autofireAttack(atk, target,atk.getActiveWeapon(),Combat.USE_AUTOFIRE, 
                                nshots, ma, mds, world);                                       
          return;
        }
      }   
      int md = target[0].getModFatigue();    
      Combat.rangeAttack(atk, target[0],atk.getActiveWeapon(),nshots,ma,md, world);             
    }
    else{
      int md = target[0].getModFatigue();        
      Combat.punch(atk,target[0],ma,md);
    }
  }     
  
  public Bot[] initiative(Bot[] bots){
    mages.fuzion.Character tmp0[] = Combat.initiative(bots);  
    
    Bot tmp[] = new Bot[bots.length];
    for(int i=0; i<tmp.length; i++){
      tmp[i] = (Bot)tmp0[i];
    }
    return tmp;
  }
}