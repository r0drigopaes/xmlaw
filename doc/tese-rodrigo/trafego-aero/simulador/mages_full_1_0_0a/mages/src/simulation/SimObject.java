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
import java.awt.Image;
import java.io.*;

public class SimObject implements ISimObject,Serializable{
  public static final int PARAM = 0;
  public static final int ICON = 1;

  final boolean USE_ICONS_TABLE = true;
  final boolean NO_USE_ICONS_TABLE = false;  
    
  String name;
  int x;
  int y;
  Hashtable params;
  Vector icons;
  Vector links;
  int maxWidth = -1;
  int maxHeight = -1;
  boolean flagParam = false;
  boolean flagIcon = false;  
  boolean useTable = false;
  public IconsTable table = null;
  
  public SimObject(){
    name = null;
    x = y = -1;
    params = new Hashtable(3);
    icons = new Vector();    
    links = new Vector();
    setUseTable(false);
  }  
  
  public void setName(String n){
    name = n;
  }

  public String getName(){
    return name;
  }

  public void setPosition(int xx, int yy){
    x = xx;
    y = yy;
  }

  public int getX(){
    return x;
  }

  public int getY(){
    return y;
  }  
  
  public void addParameter(Parameter p){
    params.put(p.getName(),p);
    if(!flagParam){
      flagParam = p.isModified();    
    }  
  }
  
  public Parameter getParameter(String n){
    return (Parameter)params.get(n);
  }
  
  public Parameter[] getParameters(){
    Enumeration en = params.elements();
    Parameter[] tmp = new Parameter[params.size()];

    for(int i=0; i<params.size(); i++){
      Parameter p = (Parameter)en.nextElement();      
      tmp[i] = p;
    }
    return tmp;
  }
  
  public void addIcon(Icon i){
    if(!useTable){
      icons.addElement(i);
      if(i.getWidth()>maxWidth){
        maxWidth = i.getWidth();
      }
      if(i.getHeight()>maxHeight){
        maxHeight = i.getHeight();
      }    
    }
  }
  
  public Icon getIcon(int index){
    if(!useTable){    
      return (Icon)icons.elementAt(index);
    }  
    else{
      if(table!=null){
        int lnk = getLink(index);
        return table.getIcon(lnk);
      }  
    }
    return null;
  }
  
  public Icon[] getIcons(){
    Icon[] tmp = null;
    if(!useTable){  
      tmp = new Icon[icons.size()];

      for(int i=0; i<icons.size(); i++){
        Icon in = getIcon(i);      
        tmp[i] = in;
      }
    }
    else{
      if(table!=null){
        int[] lks = getLinks();
        for(int i=0; i<lks.length; i++){
          tmp[i] = getIcon(lks[i]);
        }
      }
    }  
    return tmp;        
  }  
  
  public int getMaxWidth(){
    if(useTable){     
      if(table!=null&&maxWidth==-1){
        int[] lks = getLinks();
        for(int i=0; i<lks.length; i++){
          int tmp = getIcon(lks[i]).getWidth();
          if(tmp>maxWidth){
             maxWidth = tmp;
          }
        }
      }    
    }  
    return maxWidth;     
  }

  public int getMaxHeight(){
    if(useTable){     
      if(table!=null&&maxHeight==-1){
        int[] lks = getLinks();
        for(int i=0; i<lks.length; i++){
          int tmp = getIcon(lks[i]).getHeight();
          if(tmp>maxHeight){
             maxHeight = tmp;
          }
        }
      }    
    }  
    return maxHeight;   
  }  
  
  public SimObject[] split(){
    SimObject[] sos = null;  
    if(icons.size()==1){
      sos = new SimObject[1];
      sos[0] = this;
    }
    else{
      if(icons.size()>1){
        sos = new SimObject[icons.size()];
        for(int i=0; i<icons.size(); i++){
		    sos[i] = new SimObject();
		    sos[i].setName(getName());
 			 sos[i].setPosition(getX(),getY());
   	    sos[i].addIcon(getIcon(i));      
	       Parameter[] tmp = getParameters();   
	 	    for(int j=0; j<tmp.length; j++){
		      sos[i].addParameter(tmp[j]);
		    }
        }
     }
    }
    return sos; 
  }
  
  public int getNumIcons(){
    if(!useTable){    
      return icons.size();
    }
    else{
      return getNumLinks();
    }  
  }
  
  public int getNumParameters(){
    return params.size();
  }  
  
  public void setParameterValue(String n, String v){
    Parameter p = getParameter(n);
    p.setValue(v);
    flagParam = p.isModified();
  }
  
  public Object clone(){
    SimObject s = new SimObject();
    s.setName(getName());
    s.setPosition(getX(),getY());

    Parameter[] tmp = getParameters();
    for(int i=0; i<tmp.length; i++){
      s.addParameter((Parameter)tmp[i].clone());
    }
    
    if(!useTable){      
      Icon[] tmp2 = getIcons();
      for(int j=0; j<tmp2.length; j++){
        s.addIcon((Icon)tmp2[j].clone());
      }    
    }  
    else{
      if(table!=null){
        int[] tmp3 = getLinks();
        for(int k=0; k<tmp3.length; k++){
          if(!s.hasLink(tmp3[k])){
            s.addLink(tmp3[k]);
          }  
        }      
      }
    }
    return s;
  }
  
  public void modified(int t, boolean m){
    switch(t){
      case PARAM: flagParam = m;    
      break;
      case ICON: flagIcon = m;    
      break;      
    }
  }
    
  public boolean isModified(){
    return flagParam || flagIcon;
  }
    
  public boolean paramIsModified(){
    return flagParam;
  }  
  
  public boolean iconIsModified(){
    return flagIcon;
  }  
  
  public boolean hasLink(int ref){
    boolean flag = false;
    int[] tmp = getLinks();    
    for(int i=0; i<tmp.length; i++){
      if(tmp[i]==ref){
        flag = true;
      }
    }     
    return flag;
  }
    
  public void addLink(int ref){
    if(!hasLink(ref)){
      links.addElement(new Integer(ref));    
    }
  }
  
  public int getLink(int index){
    return ((Integer)links.elementAt(index)).intValue();
  }
  
  public int[] getLinks(){
    int[] tmp = new int[links.size()];

    for(int i=0; i<links.size(); i++){
      tmp[i] = getLink(i);;
    }
    return tmp;
  }  
  
  public int getNumLinks(){
    return links.size();
  }  
  
  public void setIconsTable(IconsTable it){
    table = it;
  }
  
  public void setUseTable(boolean f){
    useTable = f;
    if(useTable){ }
    else{
      if(table!=null){
        int[] lks = getLinks();
        for(int i=0; i<lks.length; i++){
          addIcon(getIcon(lks[i]));
        }
        links = new Vector();        
      }    
    }
  }
 
  public Image getImage(String r, int p) throws InterruptedException{
    Icon ic = null;  
    if(table==null){
      ic = getIcon(p);
    }
    else{
      if(table!=null){
        int[] lks = getLinks();
        int ref = lks[p];
        ic = table.getIcon(ref);
      }
    }
    
    Image tmp = null;        
    if(ic!=null){
      if(flagParam){
         tmp = ic.getImage(r,true);      
      }
      else{
         tmp = ic.getImage(r,false);      
      }
    }    
    return tmp;    
  }  
   
  public Image getImage(String r, int p, int mode) throws InterruptedException{
    if(table!=null){
      int ref = getLink(0);
      return table.getImage(r, ref, mode);
    }
    else{
      return getImage(r,p);    
    }
  }  
}