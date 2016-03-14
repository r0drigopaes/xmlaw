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
import mages.util.*;
import mages.fuzion.*;

import java.io.*;
import java.util.Vector;

public class World implements Serializable, FuzionMap{
  public static final int OBJECTS = 0;
  public static final int BOTS = 1;
    
  protected Map map = null;
  protected Coord posBlueBase = null;
  protected Coord posRedBase = null;
  protected double costTerrain[][] = null;
  protected FuzionObject objects[][][] = null;

  int rRed = 1;    
  int rBlue = 1;
  int cntRed = 0;
  int cntBlue = 0; 
  int startRedFlags = 0;
  int startBlueFlags = 0;  
  Vector baseRed = new Vector();
  Vector baseBlue = new Vector();
      
  public World(Map m){ 
    map = m;
    init();    
  }
    
  private void init(){
    costTerrain = new double[map.getWidth()][map.getHeight()];
    objects = new FuzionObject[map.getWidth()][map.getHeight()][2];
    
    for(int i=0; i<map.getHeight(); i++){
      for(int j=0; j<map.getWidth(); j++){
        SimObject so = map.getSimObject(j,i,1);        
        if(so!=null){
          //Find red and blue bases                
          if(so.getName().equals("Base Blue")){
            posBlueBase = new Coord(so.getX(),so.getY());      
          }
          else{
            if(so.getName().equals("Base Red")){     
              posRedBase = new Coord(so.getX(),so.getY());                                        
            }
            else{
              if(so.getName().equals("Flag Red")){     
                Flag f = new Flag(CtFSettings.RED,new Coord(j,i));
                objects[j][i][OBJECTS] = f;        
                startRedFlags++;
              }
              else{
                if(so.getName().equals("Flag Blue")){                       
                  Flag f = new Flag(CtFSettings.BLUE,new Coord(j,i));
                  objects[j][i][OBJECTS] = f;                           
                  startBlueFlags++;                  
                }              
              }
            }
          }
        }        
        //Terrain
        SimObject so2 = map.getSimObject(j,i,0);      
        if(so2!=null){    
          costTerrain[j][i] = Double.parseDouble(so2.getParameter("cost").getValue());
        }  
      }
    }
  }
    
  public boolean isOccupied(int x, int y, int ly){
    try{
      return (objects[x][y][ly]==null)?false:true;
    }
    catch(ArrayIndexOutOfBoundsException e){
      return false;
    }
  }      
  
  public boolean isValid(int x, int y){
    return ((x>=0) && (x<(map.getWidth()-1)) && (y>=0) && (y<(map.getHeight()-1)))?true:false;
  }
  
  public boolean canMove(int x, int y){
    if(isValid(x,y)){
      boolean terr = (costTerrain[x][y]>=1)?false:true;
      boolean obj = true;
      boolean b = true;
      if(isOccupied(x,y,OBJECTS)) obj = !objects[x][y][OBJECTS].isSolid();      
      if(isOccupied(x,y,BOTS)) b = false;            
      return terr && obj && b;    
    }
    else{
      return false;
    }
  }
      
  private int[] matrixA(int r){
    int m[] = new int[9];
    m[0]=-r; 
    m[1]=0; 
    m[2]=r;    
    m[3]=-r; 
    m[4]=0; 
    m[5]=r;
    m[6]=-r; 
    m[7]=0; 
    m[8]=r;        
    return m;
  }
   
  private int[] matrixB(int r){
    int m[] = new int[9];  
    m[0]=-r; 
    m[1]=-r; 
    m[2]=-r;    
    m[3]=0; 
    m[4]=0; 
    m[5]=0;
    m[6]=r; 
    m[7]=r; 
    m[8]=r;        
    return m;    
  }
      
  private int[] matrix(int v){      
    int[] mx = new int[9];
    for(int i=0; i<mx.length; i++){
      mx[i] = v;
    }
    return mx;
  }
  
  private void posRec(Bot b, Coord c){
    int Mx[] = matrix(c.getX());
    int My[] = matrix(c.getY());
    
    int r = (b.getId()==CtFSettings.RED)?rRed:rBlue;
    int cnt = (b.getId()==CtFSettings.RED)?cntRed:cntBlue;
    int A[] = matrixA(r);    
    int B[] = matrixB(r);  
        
    boolean find = false;
    while(!find){
      int px = Mx[cnt] + A[cnt];
      int py = My[cnt] + B[cnt];    
     
      if(isValid(px,py) && canMove(px,py)){      
        find = true;
        objects[px][py][BOTS] = b;
	     b.setPosition(new Coord(px,py));			             
      }
      cnt++;      
      cntRed += (b.getId()==CtFSettings.RED)?1:0;
      cntBlue += (b.getId()==CtFSettings.BLUE)?1:0;
            
      if(cnt==9 && !find){
        find = true;
        cnt = 0;
        cntRed = (b.getId()==CtFSettings.RED)?0:cntRed;
        cntBlue = (b.getId()==CtFSettings.BLUE)?0:cntBlue;        
        r++;
        rRed += (b.getId()==CtFSettings.RED)?1:0;
        rBlue += (b.getId()==CtFSettings.BLUE)?1:0;                
        posRec(b,c);
      }
    }    
  }
      
  public void position(Bot b){            
    if(b.getId()==CtFSettings.RED){            
      posRec(b,posRedBase);                         
    }
    else{
      if(b.getId()==CtFSettings.BLUE){
        posRec(b,posBlueBase);                               
      }
    }
  }
    
  public Map getMap(){
    return map;
  }
  
  public synchronized FuzionObject get(int x, int y, int ly){
    FuzionObject fo = null;  
    if(isBase(x,y,CtFSettings.RED)){ }
    else{
      if(isBase(x,y,CtFSettings.BLUE)){ }
      else{
        fo = objects[x][y][ly];        
      }
    }     
    return fo;
  }  

  private boolean isBase(int x, int y, int id){
    if(id==CtFSettings.RED){
      return (x==posRedBase.getX() && y==posRedBase.getY())?true:false;
    }
    else{
      return (x==posBlueBase.getX() && y==posBlueBase.getY())?true:false;      
    }
  }
    
  private boolean isBase(FuzionObject fo, int id){
    Coord c = fo.getPosition();
    if(id==CtFSettings.RED){
      return (c.getX()==posRedBase.getX() && c.getY()==posRedBase.getY())?true:false;
    }
    else{
      return (c.getX()==posBlueBase.getX() && c.getY()==posBlueBase.getY())?true:false;      
    }
  }
  
  public synchronized void set(FuzionObject fo, int x, int y, int ly){    
    fo.setPosition(new Coord(x,y));  
    if(fo instanceof Flag){
      if(isBase(fo,CtFSettings.RED)){
        baseRed.addElement(fo);
        Flag f = (Flag)fo;
        f.setPosition(new Coord(Double.POSITIVE_INFINITY,Double.POSITIVE_INFINITY));
      }
      else{
        if(isBase(fo,CtFSettings.BLUE)){        
          baseBlue.addElement(fo);
          Flag f = (Flag)fo;
          f.setPosition(new Coord(Double.POSITIVE_INFINITY,Double.POSITIVE_INFINITY));          
        }
        else{
          objects[x][y][ly] = fo;        
        }
      }
    }
    else{
      objects[x][y][ly] = fo;    
    }
  }
  
  public void remove(int x, int y, int ly){     
    FuzionObject fo = objects[x][y][ly]; 
    if(fo instanceof Flag){
      if(isBase(fo,CtFSettings.RED)){
        baseRed.remove(fo);
      }
      else{
        if(isBase(fo,CtFSettings.BLUE)){        
          baseBlue.remove(fo);
        }
      }
    }    
    objects[x][y][ly] = null;    
  }
  
  public double getCostTerrain(int x, int y){
    try{
      return costTerrain[x][y];
    }
    catch(ArrayIndexOutOfBoundsException e){
      return -1.0;
    }  
  }    

  public Coord getPosBlueBase(){
   return posBlueBase;  
  }
  
  public Coord getPosRedBase(){
   return posRedBase;  
  }  


  public Bot[] getBots(){
    Vector tmp = new Vector();
    for(int i=0; i<map.getHeight(); i++){
      for(int j=0; j<map.getWidth(); j++){
        if(objects[j][i][BOTS] instanceof Bot){
          tmp.addElement(objects[j][i][BOTS]);
        }
      }
    }  
    
    Bot tmp2[] = new Bot[tmp.size()];
    for(int k=0; k<tmp2.length; k++){
      tmp2[k] = (Bot)tmp.elementAt(k);
    }
    return tmp2;
  }
    
  public Flag[] getFlags(){
    Vector tmp = new Vector();
    for(int i=0; i<map.getHeight(); i++){
      for(int j=0; j<map.getWidth(); j++){
        if(objects[j][i][OBJECTS] instanceof Flag){
          tmp.addElement(objects[j][i][OBJECTS]);
        }
      }
    }  
    
    Flag tmp2[] = new Flag[tmp.size()];
    for(int k=0; k<tmp2.length; k++){
      tmp2[k] = (Flag)tmp.elementAt(k);
    }
    return tmp2;
  }
  
  public Coord[] getBases(){
    Coord tmp[] = new Coord[2];
    tmp[0] = posRedBase;
    tmp[1] = posBlueBase;
    return tmp;
  } 

  public int getLayers(){
    return 2;
  }

  public int getWidth(){
    return map.getWidth();
  }
  
  public int getHeight(){
    return map.getHeight();
  } 
       
  public Object clone(){
      World tmp = new World(map);
      tmp.map = (Map)map.clone();    
      tmp.posBlueBase = (Coord)posBlueBase.clone();
      tmp.posRedBase = (Coord)posRedBase.clone();    

      double tmpCostTerrain[][] = new double[map.getWidth()][map.getHeight()];
      FuzionObject tmpObjects[][][] = new FuzionObject[map.getWidth()][map.getHeight()][2];    
    
      for(int i=0; i<map.getWidth(); i++){
        for(int j=0; j<map.getHeight();j++){
          tmpCostTerrain[i][j] = costTerrain[i][j];
        }
      }
      tmp.costTerrain = tmpCostTerrain;
    

      for(int i=0; i<map.getWidth(); i++){
        for(int j=0; j<map.getHeight();j++){
          for(int k=0; k<2; k++){
            if(objects[i][j][k]==null){
              tmpObjects[i][j][k] = null;            
            }
            else{
              tmpObjects[i][j][k] = (FuzionObject)objects[i][j][k].copy();
            }
          }  
        }
      }    
      tmp.objects = tmpObjects;

      return tmp;      
  }
  
  public double avrCostTerrain(){  
    double sum =0;
    for(int i=0; i<map.getHeight(); i++){
      for(int j=0; j<map.getWidth(); j++){
        if(costTerrain[j][i]<1.0) sum+=costTerrain[j][i];
      }
    }  
    return sum/(map.getHeight()*map.getWidth());
  }
  
  public int terrainsSolid(){  
    int cnt = 0;
    for(int i=0; i<map.getHeight(); i++){
      for(int j=0; j<map.getWidth(); j++){
        if(costTerrain[j][i]==1.0) cnt++;
      }
    }  
    return cnt;
  }  
  
  public int getNumFlagsInBase(int idB, int idF){
    int cnt = 0;
    if(idB==CtFSettings.RED){
      if(baseRed.size()!=0){
        for(int i=0; i<baseRed.size(); i++){
          if(baseRed.elementAt(i) instanceof Flag){
            Flag f = (Flag)baseRed.elementAt(i);
            if(f.getColor()==idF) cnt++;
          }
        }
      }
    }
    else{
      if(baseBlue.size()!=0){
        for(int i=0; i<baseBlue.size(); i++){
          if(baseBlue.elementAt(i) instanceof Flag){
            Flag f = (Flag)baseBlue.elementAt(i);
            if(f.getColor()==idF) cnt++;
          }
        }
      }    
    }
    return cnt;
  }
  
  public boolean allFlagsInBase(int b){
    if(b==CtFSettings.RED){    
       return (getNumFlagsInBase(CtFSettings.RED,CtFSettings.BLUE)==startBlueFlags)?true:false;    
    }
    else{
      if(b==CtFSettings.BLUE){
        return (getNumFlagsInBase(CtFSettings.BLUE,CtFSettings.RED)==startRedFlags)?true:false;          
      }   
      else{
        return false;
      }
    }
  }
  
  public double[][] getCostTerrain(){
    return costTerrain;
  }
  
  public synchronized double[] getRectCosts(Coord p, int size, int id, double myFlag, double partner, double otherFlag, double enemy){
    int width,height;
    width = height = (2*size)+1;  
    int startX = p.getX()-size;
	 int startY = p.getY()-size;	   
	 int cnt = 0;
	   
    double ret[] = new double[width*height];    
    for(int k=0; k<ret.length;k++){
      ret[k] = Double.POSITIVE_INFINITY;
    }  	 
	     
	 for(int i=0; i<height; i++){
	   for(int j=0; j<width; j++){
	     try{
  	       ret[cnt] = (costTerrain[j+startX][i+startY]==0.0)?0.0:-costTerrain[j+startX][i+startY];
	     }
	     catch(ArrayIndexOutOfBoundsException e){
	       ret[cnt] = Double.POSITIVE_INFINITY;
	     }    
	     for(int l=0; l<2; l++){
	       if(objects[j+startX][i+startY][l]!=null){	       
	         if(objects[j+startX][i+startY][l].isSolid()){
	           if(objects[j+startX][i+startY][l] instanceof Flag){
	             Flag f = (Flag)objects[j+startX][i+startY][l];
	             if(f.getColor()==id){
        	         ret[cnt] = myFlag;	         
	             }
	             else{
        	         ret[cnt] = otherFlag;	         	             
	             }    
	           }
	           else{
   	          if(objects[j+startX][i+startY][l] instanceof Bot){	           
	               Bot b = (Bot)objects[j+startX][i+startY][l];
	               if(b.getId()==id){
	                 ret[cnt] = partner;
	               }
	               else{
  	                 ret[cnt] = enemy;
	               }    
	             }
	           }  
	         }
	       }
	     }
	     cnt++;
	  }
	 }    
	 return ret;
  }
}