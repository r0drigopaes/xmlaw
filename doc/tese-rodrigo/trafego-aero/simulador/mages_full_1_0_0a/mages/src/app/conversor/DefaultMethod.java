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

import java.io.*;

public class DefaultMethod extends Method{

  public void save(String name)  throws IOException{
    FileOutputStream file = new FileOutputStream(name);
    PrintStream stream = new PrintStream(file);  
          
    for(int r=0; r<source.getRows(); r++){
      for(int c=0; c<source.getCols(r); c++){
        stream.print(source.getValue(r,c)+" ");
      }
      stream.println();
    }
    stream.close();    
  }
}