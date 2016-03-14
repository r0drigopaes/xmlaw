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

package mages.app.conversor;

import java.util.*;

public class ClassOp extends Operation{
  Hashtable patterns = null;  
      
  public ClassOp(){
    patterns = new Hashtable();
  }
      
  public void exec(String source[][], int col){
    values = new String[source.length];
    for(int i=0; i<values.length; i++){    
      String nvalue = (String)patterns.get(source[i][col]);
      if(nvalue==null){
        nvalue = (String)patterns.get("*");
        if(nvalue!=null){
           if(nvalue.equals("=")){
             nvalue = source[i][col];
           }           
        }
      }

      if(nvalue!=null) values[i] = nvalue;      
    }
  }  
         
  public void addPattern(String value, String nvalue){
    patterns.put(value,nvalue);
  }
  
  public String[][] getPatterns(){
    String tmp[][] = new String[patterns.size()][2];
    Enumeration keys = patterns.keys();
    int cnt =0;
    while(keys.hasMoreElements()){
      String key = (String)keys.nextElement();
      String value = (String)patterns.get(key);
      tmp[cnt][0] = key; 
      tmp[cnt][1] = value;
    }
    return tmp;
  }
}