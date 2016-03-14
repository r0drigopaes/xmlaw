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
import java.io.*;

public class Parameters implements Serializable{
  private Vector parameters;
  
  public Parameters(){
    parameters = new Vector();
  }
  
  public void add(byte b){
    Byte tmp = new Byte(b);
    parameters.addElement(tmp);
  }

  public void add(char c){
    Character tmp = new Character(c);
    parameters.addElement(tmp);  
  }  
  
  public void add(double d){   
    Double tmp = new Double(d);
    parameters.addElement(tmp);  
  }  

  public void add(float f){
    Float tmp = new Float(f);
    parameters.addElement(tmp);  
  }  
  
  public void add(int i){
    Integer tmp = new Integer(i);
    parameters.addElement(tmp);  
  }  
  
  public void add(long l){
    Long tmp = new Long(l);
    parameters.addElement(tmp);  
  }  
  
  public void add(short s){
    Short tmp = new Short(s);
    parameters.addElement(tmp);  
  }    

  public void add(boolean b){
    Boolean tmp = new Boolean(b);
    parameters.addElement(tmp);  
  }    
    
  public void add(String s){
    parameters.addElement(s);  
  }    
      
  public void add(Object o){
    parameters.addElement(o);  
  }    
  
  public void add(int[] ia){
    Vector tmp = new Vector();
    for(int i=0; i<ia.length; i++){
      tmp.addElement(new Integer(ia[i]));
    }
    parameters.addElement(tmp);
  }
  
  public byte getByteParameter(int i){
    Byte tmp = (Byte)parameters.elementAt(i);
    return tmp.byteValue();
  }
  
  public char getCharParameter(int i){
    Character tmp = (Character)parameters.elementAt(i);
    return tmp.charValue();  
  }  
  
  public double getDoubleParameter(int i){
    Double tmp = (Double)parameters.elementAt(i);
    return tmp.doubleValue();  
  }    
  
  public float getFloatParameter(int i){
    Float tmp = (Float)parameters.elementAt(i);
    return tmp.floatValue();  
  }    
  
  public int getIntParameter(int i){
    Integer tmp = (Integer)parameters.elementAt(i);
    return tmp.intValue();  
  }    
  
  public long getLongParameter(int i){
    Long tmp = (Long)parameters.elementAt(i);
    return tmp.longValue();  
  }    

  public short getShortParameter(int i){
    Short tmp = (Short)parameters.elementAt(i);
    return tmp.shortValue();  
  }    
  
  public boolean getBooleanParameter(int i){
    Boolean tmp = (Boolean)parameters.elementAt(i);
    return tmp.booleanValue();  
  }    
    
  public String getStringParameter(int i){
    String tmp = (String)parameters.elementAt(i);
    return tmp;  
  }    
      
  public Object getObjectParameter(int i){
    return parameters.elementAt(i);
  }    
  
  public int[] getIntArrayParameter(int i){
    Vector tmp = (Vector) parameters.elementAt(i);
    int[] ia = new int[tmp.size()];
    for(int j=0; j< ia.length; j++){
      Integer io = (Integer)tmp.elementAt(j);
      ia[j] = io.intValue();
    }
    return ia;
  }
  
  public Vector getParameters(){
    return parameters;
  }
  
  public int getNumParameters(){
    return parameters.size();
  }
  
  public void removeParameters(){
    parameters = null;
  }  
}