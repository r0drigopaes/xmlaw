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

package mages.app.ctf;

import mages.app.*;
import java.io.*;

public class WeaponsController implements IController{
        
  public void init(){}
    
  public void save(Object obj, String name) throws IOException{    
    XMLController.save(obj, new WeaponsCollectionXML(),name,"../../dtd/weapons.dtd");  
  }
  
  public Object load(String name) throws IOException{
    return XMLController.load(new WeaponsCollectionXML(),name);
  }   
}