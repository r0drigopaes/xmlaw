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

package mages.app.mapeditor;

import mages.simulation.*;
import mages.app.*;

import java.io.*;
import java.util.Vector;
import org.w3c.dom.*;
import org.apache.xerces.dom.*;

public class MapXML extends XMLObject{
  
  String[] attribs = {"width","height"};  
                 
  public Document createDoc(Object obj) throws IOException{
      Map map = (Map)obj;

      DocumentImpl document = new DocumentImpl();  
      Element root = (Element) document.createElement("map");
            
      for(int i = 0; i<attribs.length; i++){
        Attr a = document.createAttribute(attribs[i]);            
        switch(i){
          case 0: a.setValue(Integer.toString(map.getWidth()));
          break;
          case 1: a.setValue(Integer.toString(map.getHeight()));
          break;          
        }        
        root.setAttributeNode(a);     
      }
      
      
      //Map Schema
      MapSchemaXML msx = new MapSchemaXML();
      Document doc = msx.createDoc(map.getMapSchema());
      Element e0 = (Element)document.importNode(doc.getDocumentElement(),true);
      root.appendChild(e0);  
                                      
      //Layer
      SimObjectXML sox = new SimObjectXML();          
      for(int k=0; k<map.getNumLayers(); k++){
        Layer layer = map.getLayer(k);
        Element e = document.createElement("layer");
        //Layer's attributes
        Attr idLayer = document.createAttribute("ly.id");        
        idLayer.setValue(Integer.toString(layer.getId()));
        e.setAttributeNode(idLayer);

        //Icons table
        if(map.getMapSchema().getMapSchemaEntry(k).isUsedIndex()){
           IconsTable it = layer.indexTable();
           //Create table of icons
           Element ei = document.createElement("icons");
           Icon[] icons = it.getIcons();
           IconXML ix = new IconXML();            
           for(int p=0; p<icons.length; p++){
             Document d = ix.createDoc(icons[p]);        
             Element eic = (Element)document.importNode(d.getDocumentElement(),true);
             ei.appendChild(eic);          
           }     
           e.appendChild(ei);               
        }

        //Create simulation objects
        for(int r=0; r<layer.getHeight(); r++){
          for(int c=0; c<layer.getWidth(); c++){
            SimObject so = layer.getCell(c,r);
            if(so!=null){
               Document docSo = sox.createDoc(so);
               Element eso = (Element)document.importNode(docSo.getDocumentElement(),true);
               e.appendChild(eso);               
            }
          }
        }        
        root.appendChild(e);              
      }
    document.appendChild(root);                
    return document;                                   
  }

  public Object newInstance(Element root) throws IOException{    
    Map map = null;
    int width = 0;
    int height = 0;
    Vector tmpLayers = new Vector();

    Attr w = root.getAttributeNode("width");
    width = Integer.parseInt(w.getValue());
    Attr h = root.getAttributeNode("height");

    height = Integer.parseInt(h.getValue());        
    //Map Schema
    NodeList msl = root.getElementsByTagName("mapschema");            
    Element mse = (Element)msl.item(0);     
    MapSchemaXML msx = new MapSchemaXML();
    MapSchema ms = (MapSchema)msx.newInstance(mse);
    
    //All tags, any kind of tag there is in document
    NodeList elemList = root.getElementsByTagName("layer");                
    
    //Layer    
    SimObjectXML sox = new SimObjectXML();
    for(int i=0; i<elemList.getLength(); i++){  
      Element layerElem = (Element) elemList.item(i);
      
      //Table of icons
      NodeList tmp = layerElem.getElementsByTagName("icons");
      IconsTable it = null;            
      if(tmp.getLength()!=0){
         Element eIcons = (Element)tmp.item(0);      
         NodeList icons = eIcons.getElementsByTagName("icon");         
         it = new IconsTable();
         IconXML ix = new IconXML();    
         
         for(int n=0; n<icons.getLength(); n++){
           Element e = (Element)icons.item(n);      
           Icon icon = (Icon)ix.newInstance(e);
           it.addIcon(icon);
         }           
      }            

      NodeList soList = layerElem.getElementsByTagName("simobj");      
      Attr lyId = layerElem.getAttributeNode("ly.id");      
      int id = Integer.parseInt(lyId.getValue());
      Layer layer = new Layer(id,width,height);
      for(int j=0; j<soList.getLength(); j++){
        Element soElem = (Element) soList.item(j);        
        SimObject so = (SimObject)sox.newInstance(soElem);
        if(ms.getMapSchemaEntry(i).isUsedIndex()){
          so.setIconsTable(it);           
          so.setUseTable(true);
        }        
        layer.setCell(so.getX(),so.getY(),so);            
      }
      tmpLayers.addElement(layer);      
    }        

    map = new Map(width,height,tmpLayers.size());
    map.setMapSchema(ms);    
    for(int k=0; k<tmpLayers.size(); k++){
      map.setLayer((Layer)tmpLayers.elementAt(k),k);
    }
    return map;
  }         
}