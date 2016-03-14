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


package mages.util;

import java.io.*;

public class MagesDimension implements Serializable{
  int width;
  int height;
  int depth;
  
  public MagesDimension(int w, int h, int d){
    width = w;
    height = h;
    depth = d;
  }
  
  public MagesDimension(int w, int h){
    width = w;
    height = h;
    depth = 0;
  }  
  
  public int getWidth(){
    return width;
  }
  
  public int getHeight(){
    return height;
  }  
  
  public int getDepth(){
    return depth;
  }  
}