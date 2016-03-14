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

package mages.comm;

import java.io.*;

public class CommElement implements Serializable{
  public int idMsg;
  public int idSender;
  public int idGroup;    
  public Object arg; 
  
  public CommElement(int im, int is, int ig, Object a){
    idMsg = im;
    idSender = is;
    idGroup = ig;
    arg = a; 
  }
  
  public int hashCode(){
    int a = idMsg*1000;
    int b = idGroup*100;
    int c = idSender*10;
    int d = (arg==null)?0:arg.hashCode();
    return a+b+c+d;
  }
  
  public boolean equals(Object o){
    try{
      CommElement target = (CommElement)o;
      boolean a = (idMsg==target.idMsg);
      boolean b = (idGroup==target.idGroup);        
      boolean c = (idSender==target.idSender);        
      boolean d = (arg!=null)?arg.equals(target.arg):true;        
      return a && b && c && d;
    }
    catch(ClassCastException e){
      return false;
    }  
  }
}