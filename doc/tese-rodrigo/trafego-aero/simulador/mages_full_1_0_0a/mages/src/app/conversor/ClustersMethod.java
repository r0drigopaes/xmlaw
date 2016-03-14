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
import java.io.*;

public class ClustersMethod extends Method{
  int numSamples;
  int col;
  Hashtable clusters = new Hashtable();
  
  public int getNumSamples(){
    return numSamples;
  }

  public void setNumSamples(int n){
    numSamples = n;
  }  
    
  public int getCol(){
    return col;
  }  
  
  public void setCol(int c){
    col = c;
  }  
  
  private void classify(){
    for(int r=0; r<source.getRows(); r++){
      String value = source.getValue(r,col);
      Cluster cluster = null;      
      if(clusters.containsKey(value)){
        cluster = (Cluster)clusters.get(value);
        cluster.increment();
      }
      else{
        cluster = new Cluster(value);
        cluster.increment();        
        clusters.put(value,cluster);
      }
    }  
  }

  private void sizeSampling(){
    Enumeration enum = clusters.elements();
    int sumSize = 0;
    String minValue = null;
    int minFreq = (int)Double.POSITIVE_INFINITY;
    
    while(enum.hasMoreElements()){
      Cluster cluster = (Cluster)enum.nextElement();
      int f = cluster.getFrequency();
      if(f<minFreq){
        minFreq = f;
        minValue = cluster.getValue();
      }
      double slice = ((double)f)/((double)source.getRows());
      int size = (int)(numSamples*slice);
      sumSize += size;
      cluster.setMaxSamples(size);
    }
    
    if(sumSize<numSamples){
      int dif = numSamples-sumSize;
      Cluster clusterTmp = (Cluster)clusters.get(minValue);
      clusterTmp.setMaxSamples(clusterTmp.getMaxSamples()+dif);
    }
  }
    
  public void save(String name)  throws IOException{
    FileOutputStream file = new FileOutputStream(name);
    PrintStream stream = new PrintStream(file);  
    classify();
    sizeSampling();

    for(int r=0; r<source.getRows(); r++){
      if(!source.isMarked(r)){
        String value = source.getValue(r,col);    
        Cluster cluster = (Cluster)clusters.get(value);      
        if(!cluster.isFull()){
          cluster.incrementSamples();
          source.markValue(r,true);
          for(int c=0; c<source.getCols(r); c++){
            stream.print(source.getValue(r,c)+" ");
          }        
          stream.println();            
        }
      }
    }  
    stream.close();    
  }    
  
  private class Cluster{
    int frequency;
    int maxSamples;
    int samples;
    String value;
    
    public Cluster(String v){
      frequency = maxSamples = samples = 0;
      value = v;
    }
    
    public void increment(){
      frequency++;
    }

    public void incrementSamples(){
      samples++;
    }
        
    public int getFrequency(){
      return frequency;
    }
    
    public void setMaxSamples(int s){
      maxSamples = s;
    }
    
    public int getMaxSamples(){
      return maxSamples;
    }    
    
    public String getValue(){
      return value;
    }    
    
    public boolean isFull(){
      return (samples==maxSamples);
    }
  }
}