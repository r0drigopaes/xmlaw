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


package mages.simulation;

import java.util.*;

public class Distribution{
  private static double xnormal = -1;
  
  private static Random rand = new Random();
    
  public static double uniform(){
    return rand.nextDouble();
  }
  
  public static double random(){
    return rand.nextDouble();
  }
    
  public static int uniformInt(int min, int max){
    int a = min -1;
    double v = (a+((max-a)*rand.nextDouble()));
    return ((int)v)+1;
  }
  
  public static double uniform(double min, double max){
    double a = min -1;
    double v = (a+((max-a)*rand.nextDouble()));
    return v+1;
  }  
    
  //Law. Simulation,  Modeling and analysis (Book)
  public static double normal(){
    if(xnormal==-1){
      double v1 = 0;
      double v2 = 0;            
      double w = 1.1;
      while(w>1){
        double u1 = rand.nextDouble();
        double u2 = rand.nextDouble();    
        v1 = (2*u1)-1;
        v2 = (2*u2)-1;          
        w = (Math.pow(v1,2))+(Math.pow(v2,2));      
      }
      double y = Math.sqrt((-2*Math.log(w))/w);
      double x1 = v1*y;
      double x2 = v2*y;    

      xnormal = x2;
      return x1;
    }
    else{
      double tmp = xnormal;
      xnormal = -1;
      return tmp;
    }  
  }
  
  public static double normal(double mean, double strd){
    return mean+(strd*normal());
  }
  
  public static int normal(int mean, int strd){
    return (int)(normal(mean,strd));
  }  
  
  public static void setSeed(long s){
    rand.setSeed(s);
  }
  
  public static void setRandom(Random r){
    rand = r;
  }  
}