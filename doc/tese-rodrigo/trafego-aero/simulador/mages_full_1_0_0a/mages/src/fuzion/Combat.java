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

package mages.fuzion;

import mages.simulation.*;
import mages.util.*;
import java.util.*;

public class Combat{
  public final static int USE_AUTOFIRE = 0;
  public final static int USE_SIMPLE_SHOT = 1;  
    
  public static Character[] initiative(Character[] objs){
    Initiative values[] = new Initiative[objs.length];    
    int cnt = 0;
    for(int b=0; b<objs.length; b++){
      double v = objs[b].getInitiative()+Distribution.uniform(1,6);
      values[cnt++] = new Initiative(objs[b],v);
    }
    Arrays.sort(values);
    
    Character[] tmp = new Character[values.length];
    cnt = 0;
    for(int v=(values.length-1); v>=0; v--){
      tmp[cnt++] = values[v].getCharacter();
    }
    return tmp;
  }
  
  private static double distance(FuzionObject o1, FuzionObject o2){
    Coord p1 = o1.getPosition();
    Coord p2 = o2.getPosition();
    return Math.sqrt(Math.pow(p2.getX()-p1.getX(),2)+Math.pow(p2.getY()-p1.getY(),2));  
  }
  
  private static boolean validRange(Character o1, FuzionObject o2,Weapon wpn){
    double d = distance(o1,o2);
    if(d>wpn.getRange()){
      return false;
    }
    else{
      return true;
    }
  }
  
  private static double[][] viewField(Character ref){
    int degree1 = ref.getDirection();
    int ox = ref.getPosition().getX();
    int oy = ref.getPosition().getY();

    Coord c1 = getTarget(ref.getPosition(),ref.getDirection(),ref.rangeVision());    
    double x1 = c1.getDoubleX();
    double y1 = c1.getDoubleY();    
    
    int degree2 = ((degree1+45)>360)?(degree1+45)-360:degree1+45;
    Coord c2 = getTarget(ref.getPosition(),degree2,ref.rangeVision());        
    double x2 = c2.getDoubleX();
    double y2 = c2.getDoubleY();    
    
    int degree3 = ((degree1-45)<0)?360-degree1:degree1-45;    
    Coord c3 = getTarget(ref.getPosition(),degree3,ref.rangeVision());        
    double x3 = c3.getDoubleX();
    double y3 = c3.getDoubleY();    
    
    double mtx[][] = new double[3][5];    
    for(int i=0; i<3; i++){
      mtx[i][2] = 1;
      switch(i){
        case 0:
          mtx[i][0] = mtx[i][3] = -ox;
          mtx[i][1] = mtx[i][4] = -oy;          
        break;
        case 1:
          mtx[i][0] = mtx[i][3] = x2-ox;
          mtx[i][1] = mtx[i][4] = y2-oy;        
        break;
        case 2:
          mtx[i][0] = mtx[i][3] = x3-ox;
          mtx[i][1] = mtx[i][4] = y3-oy;        
        break;        
      }
    }
    return mtx;
  }
  
  private static boolean isInViewField(double[][] field, FuzionObject trt){
    int x = trt.getPosition().getX();
    int y = trt.getPosition().getY();
    double oldX = field[0][0];
    double oldY = field[0][1];    
    
    field[0][0] += x;
    field[0][1] += y;    
       
    double p1 = field[0][0]*field[1][1]*field[2][2];
    double p2 = field[0][1]*field[1][2]*field[2][3];    
    double p3 = field[0][2]*field[1][3]*field[2][4];    
    
    double n1 = field[0][2]*field[1][1]*field[2][0];
    double n2 = field[0][3]*field[1][2]*field[2][1];    
    double n3 = field[0][4]*field[1][3]*field[2][2];    
    
    double det = p1+p2+p3-n1-n2-n3;
    field[0][0] = oldX;
    field[0][1] = oldY;
    
    return (det==0)?true:false;
  }   
  
  public static void autofireAttack(Character attacker, Character targets[], Weapon wpn,
                             int mode, int shots, int modAt, int[] modDfs, FuzionMap map){
      System.out.println("af0 "+wpn.getWeaponClass());   
    if(wpn.getWeaponClass()==Weapon.RANGE){
      if(mode == USE_SIMPLE_SHOT){
      System.out.println("weapons is AF, using SS");
        rangeAttack(attacker,targets[0],wpn,shots,modAt,modDfs[0],map);
      }
      else{
        if(mode == USE_AUTOFIRE){
      System.out.println("weapons is AF, using AF");        
      System.out.println("af "+targets.length);                
          if(targets.length>0){
            int max = (int)(wpn.getAmnoCur()/targets.length);
            int first = wpn.getAmnoCur()-(targets.length*max)+max;                    
            for(int j=0; j<targets.length; j++){
              int max2 = (j==0)?first:max;
              wpn.amno(-max2);                                
              Character chr = (Character)targets[j]; 
                if(validRange(attacker,chr,wpn)){            
                  //Attack
                  double dist =  distance(attacker,chr);                        
                  int mrange = Settings.tableRange(wpn,dist);                                            
                  int av = attacker.getAttribute(Character.DEX)+
                           attacker.getSkill(wpn.getSkill())+ modAt + mrange;         
                                             
                  //Defense
                  int nmd = modDfs[j];
//                  if(lineSt==Settings.OBSCURED_VIEW){
  //                  nmd += (lineSt*2);
    //              }
                  nmd += (chr.isDodge())?3:0; //Defense action
         
                  int dv = chr.getAttribute(Character.DEX)+
                           chr.getSkill(Character.EV)+ nmd;                                    

                  double atck  = av + Settings.rollDouble();
                  double def = dv + Settings.rollDouble();                  
                  int nshots = Settings.tableAutofire((def-atck),max2);    
                  int damage = 0;
                  for(int k=0; k<nshots; k++){
                     damage += simpleShot(wpn.getDc());                                         
                  }                                         
                  chr.hits(damage,Character.USE_WEAPON);                                       
                }       
            }            
          }          
        }
      }
    }                             
  }
     
  public static void rangeAttack(Character attacker, Character defender,Weapon wpn,  
                                 int nshots, int modAt, int modDfs, FuzionMap map){
    if(wpn.getWeaponClass()==Weapon.RANGE){
       int lineSt = Settings.getStateLineSight(attacker,defender,map);
       int shots = (nshots>wpn.getAmnoCur())?wpn.getAmnoCur():nshots;       
       if(lineSt!=Settings.FULL_BLOCKED_VIEW){
         if(validRange(attacker,defender,wpn)){   
           double dist =  distance(attacker,defender);
           int mrange = Settings.tableRange(wpn,dist);
           int nmd = modDfs;           
           if(lineSt==Settings.OBSCURED_VIEW){
             nmd += (lineSt*2);
           }
           nmd += (defender.isDodge())?3:0; //Defense action
           int av = attacker.getAttribute(Character.DEX)+
                    attacker.getSkill(wpn.getSkill())+ modAt + lineSt + mrange;           
           int dv = defender.getAttribute(Character.DEX)+
                    defender.getSkill(Character.EV)+ nmd;                      
           if(wpn.getType()==Weapon.SIMPLE_SHOT || wpn.getType()==Weapon.SHOTGUN){
             for(int r = 0; r<shots; r++){
               double atck  = av + Settings.rollDouble();
               double def = Double.POSITIVE_INFINITY;
               if(defender.getHitsCur()>0){
                 def = dv + Settings.rollDouble();
               }                     
               if(atck>def){
                 int damage = 0;               
                 if(wpn.getType()==Weapon.SIMPLE_SHOT){
                   damage = simpleShot(wpn.getDc());
                 }                    
                 else{
                   if(wpn.getType()==Weapon.SHOTGUN){                  
                     damage = simpleShot(Settings.tableShotgun(wpn,dist));
                   }
                 }
                 defender.hits(damage,Character.USE_WEAPON);
               }             
             }                                                
           }                                        
         }
       }
       wpn.amno(-shots);       
    }                                 
  }
  
  public static void punch(Character attacker, Character defender,  int modAt, int modDfs){
    int av = attacker.getAttribute(Character.DEX)+attacker.getSkill(Character.HTH)+modAt;
    int dv = defender.getAttribute(Character.DEX)+defender.getSkill(Character.EV)+modDfs;
    double attack  = av+Settings.rollDouble(); //3D6
    double defense = dv+Settings.rollDouble(); //3D6    
    if(attack>defense){
      int damage = simpleShot(attacker.getAttribute(Character.STR));  
      defender.hits(damage,Character.USE_WEAPON);
    }
  }  
  
  private static int simpleShot(int ndc){
    int sum = 0;
    for(int i=0; i<ndc; i++){
      sum += (int)(Distribution.uniform(1,6));
    }
    return sum;
  } 
  
  public static Coord getTarget(Coord ref, int degree, int qnt){
    double angle = (degree*Math.PI)/180;
    double x1 = (Math.round(Math.cos(angle)*qnt)+ref.getX());
    double y1 = (Math.round(Math.sin(angle)*qnt)+ref.getY());        
    return new Coord(x1,y1);  
  }
}