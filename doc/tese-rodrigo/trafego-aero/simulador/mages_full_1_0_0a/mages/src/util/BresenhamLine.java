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

import java.util.Vector;

public class BresenhamLine{
  
  static Vector points = null;
  static boolean inverse = false;
  
  public static Coord[] line(Coord start, Coord end, int w, int h){
    Coord tmp[] = simpleLine(screenToCartesian(start,w,h),screenToCartesian(end,w,h));
    
    for(int i=0; i<tmp.length; i++){
      tmp[i] = cartesianToScreen(tmp[i],w,h);
    }
    return tmp;
  }

  public static Coord[] line(Coord start, Coord end){
    return simpleLine(start,end);
  }
    
  private static Coord[] simpleLine(Coord start, Coord end){
    int x1 = start.getX();
    int y1 = start.getY();
    int x2 = end.getX();
    int y2 = end.getY();
    
	 double deltax = x2 - x1;
	 double deltay = y2 - y1;
    points = new Vector();
    inverse = false;
    
	 if(deltax==0){
      posSimpleLine(start,end);
	 }  
	 else{
	   double slope = deltay / deltax;

	   if(slope>0.0){	   
		  if(slope>1.0){
          posSimpleLine(start,end);		    
		  }  
		  else{
          posCase(start,end);		    		    
		  } 
	   } 
	   else{
		  if(slope>-1.0){
          negCase(start,end);		    		    
		  } 
		  else{
          negSimpleLine(start,end);		    		   
		  }
		}   
	 }
	 
    //Create array
    Coord[] tmp = new Coord[points.size()]; 
    for(int i=0; i<tmp.length; i++){
      tmp[i] = (Coord)points.elementAt(i);
    }

    if(inverse){
      Coord[] tmp2 = new Coord[points.size()];
      int cnt = 0;
      for(int i=tmp.length-1; i>=0; i--){
        tmp2[cnt++] = tmp[i];
      }      
      return tmp2;
    }
    else{
      return tmp;    
    }	 
  }
  
  private static void posSimpleLine(Coord start, Coord end) {
    int dx, dy, p, twodx, twodxdy, x, y, yend;
    int x1 = start.getX();
    int y1 = start.getY();
    int x2 = end.getX();
    int y2 = end.getY();    

    dx = Math.abs(x1 - x2);
	 dy = Math.abs(y1 - y2);
	 twodx = 2 * dx;
	 p = twodx - dy;
	 twodxdy = 2 * (dx - dy);
	 y = y1;
	 yend = y2;
	 x = x1;

    // Vertical line
	 if(x1==x2){
	   if (y2 > y1){
		  yend = y2;
		  y = y1;
	   } 
	   else{
		  yend = y1;
		  y = y2;
		  inverse = true;
	   }

	   for (; y <= yend; y++){
      points.addElement(new Coord(x1,y));   	   
      }
	}
	
	if(x1>x2){
	  x = x2;
	  y = y2;
     yend = y1;
     inverse = true;     
	}
	
	if (x1<x2){
	  x = x1;
	  y = y1;
	  yend = y2;
	}
	
	if(x1!=x2){
      points.addElement(new Coord(x1,y1));   	   	
	   while(y<yend){
		  y++;
		  if(p<0){
		    p+=twodx;
		  }  
		  else{
		    x++;
		    p+=twodxdy;
		  }
        points.addElement(new Coord(x,y));   	   			  
	   }
	}
  }  
  
  private static void posCase(Coord start, Coord end){
	  int dx, dy, p, twody, twodydx, x, y, xend;
     int x1 = start.getX();
     int y1 = start.getY();
     int x2 = end.getX();
     int y2 = end.getY();    
     
	  dx = Math.abs(x1 - x2);
	  dy = Math.abs(y1 - y2);
	  twody = 2 * dy;
	  p = twody - dx;
	  twodydx = 2 * (dy - dx);

	  if(x1>x2){
	   x = x2;
	   y = y2;
	   xend = x1;
      inverse = true;	   
	  }
	  else{
	   x = x1;
	   y = y1;
	   xend = x2;
	  }
     points.addElement(new Coord(x,y));   	  
      
	  while(x<xend){
	     x++;
	     if(p < 0){
  		    p+= twody;
		  }    
	     else{
		    y++;
		    p+= twodydx;
	     }
        points.addElement(new Coord(x,y));   	   	   
  	  }
  }  
  
  private static void negCase(Coord start, Coord end){
	 int dx, dy, p, twody, twodydx, x, y, xend;
    int x1 = start.getX();
    int y1 = start.getY();
    int x2 = end.getX();
    int y2 = end.getY();    

	 dx = Math.abs(x2 - x1);
	 dy = Math.abs(y2 - y1);
	 p = 2 * dy - dx;
	 twody = 2 * dy;
	 twodydx = 2 * (dy - dx);

	 if(x1>x2){
	   x = x2;
	   y = y2;
	   xend = x1;
   	inverse = true;	   
	 }
	 else{
	   x = x1;
	   y = y1;
	   xend = x2;
	 }
    points.addElement(new Coord(x,y));   	   	   	  

	 while(x<xend){
	   x++;
	   if(p<0){
		  p+=twody;
		}  
	   else{
		  y--;
		  p+=twodydx;
	   }
      points.addElement(new Coord(x,y));   	   	   	   
    }
  }  
  
  private static void negSimpleLine(Coord start, Coord end){
	 int dx, dy, p, twodx, twodxdy, x, y, yend;
    int x1 = start.getX();
    int y1 = start.getY();
    int x2 = end.getX();
    int y2 = end.getY();    
    
	 dx = Math.abs (x2 - x1);
	 dy = Math.abs (y2 - y1);
	 p = 2 * dx - dy;
	 twodx = 2 * dx;
	 twodxdy = 2 * (dx - dy);

	 if(x1>x2){
	   x = x2;
	   y = y2;
	   yend = y1;
  	   inverse = true;	   
	 }
	 else{
	   x = x1;
	   y = y1;
	   yend = y2;
	  }
     points.addElement(new Coord(x,y));   	   	   	   	  

	  while(y>yend){
	   y--;
	   if(p < 0){
  		  p += twodx;
		}    
	   else{
		  x++;
		  p += twodxdy;
	   }
      points.addElement(new Coord(x,y));   	   	   	   
  	  }
  }
   
  public static Coord screenToCartesian(Coord o, int w, int h){
     int mx = (int)(w/2);
     int my = (int)(h/2);               
     int x2 = o.getX()-mx;
     int y2 = -(o.getY()-my);     
     return new Coord(x2,y2);
  }
  
  public static Coord cartesianToScreen(Coord o, int w, int h){
     int mx = (int)(w/2);
     int my = (int)(h/2);               
 
     int x2 = o.getX()+mx;
     int y2 = -(o.getY()-my);     
     return new Coord(x2,y2);
  }   
}