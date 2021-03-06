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

package mages.app.conversor;

public class NormOp extends Operation{
  double min, max; 
  double ignore = Double.NaN;

  
  public NormOp(double mi, double mx){
    min = mi;
    max = mx;
  }
    
  public NormOp(double mi, double mx, double ig){
    min = mi;
    max = mx;
    ignore = ig;
  }
      
  public void exec(String source[][], int col){
    values = new String[source.length];
    for(int i=0; i<values.length; i++){          
      double v = Double.parseDouble(source[i][col]);
      if(v!=ignore){
        double nv = (v-min)/(max-min);
        values[i] = Double.toString(nv);
      }  
      else{
        values[i] = Double.toString(v);
      }
    }
  }  
  
  public double getMin(){
    return min;
  }
  
  public double getMax(){
    return max;
  }  
  
  public double getIgnore(){
    return ignore;
  }    
}