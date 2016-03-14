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

import mages.app.*;
import java.io.*;
import org.w3c.dom.*;
import org.apache.xerces.dom.*;
import org.apache.xml.serialize.*;

public class TransformationXML extends XMLObject{
  
  String[] elems = {"simple","norm","cls","col"};
                 
  public Document createDoc(Object obj) throws IOException{
    Transformation t = (Transformation)obj;

    DocumentImpl document = new DocumentImpl();  
    Element root = (Element) document.createElement("transf"); 
    
    Element methodEl = (Element) document.createElement("method");     
    Method m[] = t.getMethods();
    for(int i=0; i<m.length; i++){
      if(m[i] instanceof DefaultMethod){
        Element dme = (Element) document.createElement("default");
        methodEl.appendChild(dme);
      }           
      else{
        if(m[i] instanceof RandomMethod){
          Element rme = (Element) document.createElement("random");      
          RandomMethod rm = (RandomMethod)m[i];
          Attr atNSamp = (Attr) document.createAttribute("num.samples");            
          atNSamp.setValue(Integer.toString(rm.getNumSamples()));        
          rme.setAttributeNode(atNSamp);        
          methodEl.appendChild(rme);        
        }
        else{
          if(m[i] instanceof ClustersMethod){
            Element cme = (Element) document.createElement("clusters");      
            ClustersMethod cm = (ClustersMethod)m[i];    
              
            Attr atNSamp = (Attr) document.createAttribute("num.samples");            
            atNSamp.setValue(Integer.toString(cm.getNumSamples()));                  
            cme.setAttributeNode(atNSamp);                  
          
            Attr atCol = (Attr) document.createAttribute("id.Col");            
            atCol.setValue(Integer.toString(cm.getCol()));                  
            cme.setAttributeNode(atCol);        
            methodEl.appendChild(cme);               
          }
        }
      }
    }  
    root.appendChild(methodEl);    
    
    Operation ops[] = t.getOperations();
    if(ops!=null){
      for(int o=0; o<ops.length; o++){
        int cols[] = ops[o].getCols();      
        Element el = null;
        if(ops[o] instanceof SimpleOp){
          el = (Element) document.createElement(elems[0]);       
          root.appendChild(el);          
        }
        else{
          if(ops[o] instanceof NormOp){
            el = (Element) document.createElement(elems[1]);                    
            Attr atMin = (Attr) document.createAttribute("min");            
            atMin.setValue(Double.toString(((NormOp)ops[o]).getMin()));        
            el.setAttributeNode(atMin);                     

            Attr atMax = (Attr) document.createAttribute("max");            
            atMax.setValue(Double.toString(((NormOp)ops[o]).getMax()));        
            el.setAttributeNode(atMax);              
            
            double ig = ((NormOp)ops[o]).getIgnore();
            if(ig!=Double.NaN){
              Attr atIg = (Attr) document.createAttribute("ignore");            
              atIg.setValue(Double.toString(ig));        
              el.setAttributeNode(atIg);                            
            }       
            root.appendChild(el);                 
          }
          else{
            if(ops[o] instanceof ClassOp){
              el = (Element) document.createElement(elems[2]);                      
              String ptn[][] = ((ClassOp)ops[o]).getPatterns();
              for(int p=0; p<ptn.length; p++){
                Element pEl = (Element) document.createElement(elems[2]);                                  
                Attr atValue = (Attr) document.createAttribute("value");            
                atValue.setValue(ptn[p][0]);        
                pEl.setAttributeNode(atValue);                                     
                
                Attr atNValue = (Attr) document.createAttribute("newvalue");            
                atNValue.setValue(ptn[p][1]);        
                pEl.setAttributeNode(atNValue);                                                     
                el.appendChild(pEl);
              }
              root.appendChild(el);              
            }
          }
        }        
                
        if(cols!=null){
          for(int c=0; c<cols.length; c++){
            Element cEl = (Element) document.createElement(elems[3]);   
            Attr attribute = (Attr) document.createAttribute("id.col");            
            attribute.setValue(Integer.toString(cols[c]));        
            cEl.setAttributeNode(attribute);              
            el.appendChild(cEl);            
          }
        }
        root.appendChild(el);
      }
    }    
             

    Element[] elements = new Element[elems.length];
    for(int i = 0; i<elements.length; i++){
      elements[i] = (Element) document.createElement(elems[i]);            
      root.appendChild(elements[i]);
    }

    document.appendChild(root);         
    return document;                                   
  }

  public Object newInstance(Element root) throws IOException{       
    Transformation t = new Transformation();
    Operation op = null;

    NodeList ml = root.getElementsByTagName("method");     
    Element methodEl = (Element)ml.item(0);
    NodeList ml2 = methodEl.getElementsByTagName("*");         
    for(int j=0; j<ml2.getLength(); j++){    
      Element elem = (Element)ml2.item(j);
      String name = elem.getTagName();    
      if(name.equals("default")){
        DefaultMethod dm = new DefaultMethod();
        t.addMethod(dm);
      }      
      else{
        if(name.equals("random")){
          RandomMethod rm = new RandomMethod();          
          Attr numSamAt = elem.getAttributeNode("num.samples");          
          rm.setNumSamples(Integer.parseInt(numSamAt.getValue()));
          t.addMethod(rm);          
        }
        else{
          if(name.equals("clusters")){     
            ClustersMethod cm = new ClustersMethod();
            Attr numSamAt = elem.getAttributeNode("num.samples");          
            cm.setNumSamples(Integer.parseInt(numSamAt.getValue()));               
            Attr colAt = elem.getAttributeNode("id.col");          
            cm.setCol(Integer.parseInt(colAt.getValue()));                           
            t.addMethod(cm);            
          }
        }
      }
    }
    
    NodeList ops = root.getElementsByTagName("*");     
    for(int i=0; i<ops.getLength(); i++){
      Element elem = (Element)ops.item(i);
      String name = elem.getTagName();
      if(name.equals(elems[0])){
        //simple
        op = new SimpleOp();        
        t.addOperation(op);        
      }
      else{
        if(name.equals(elems[1])){
          //norm
          Attr minAt = elem.getAttributeNode("min");          
          Attr maxAt = elem.getAttributeNode("max");  
          Attr igAt = elem.getAttributeNode("ignore");
          if(igAt==null){  
            op = new NormOp(Double.parseDouble(minAt.getValue()),
                            Double.parseDouble(maxAt.getValue()));                                       
          }
          else{
            op = new NormOp(Double.parseDouble(minAt.getValue()),
                            Double.parseDouble(maxAt.getValue()),
                            Double.parseDouble(igAt.getValue()));                                                 
          }                  
          t.addOperation(op);                           
        }
        else{
          if(name.equals(elems[2])){
            //class
            op = new ClassOp();
            NodeList ptns = elem.getElementsByTagName("ptn");
            for(int j=0; j<ptns.getLength(); j++){
              Element ptnEl = (Element)ptns.item(j);
              Attr valueAt = ptnEl.getAttributeNode("value");
              Attr newValueAt = ptnEl.getAttributeNode("newvalue");
              ((ClassOp)op).addPattern(valueAt.getValue(),newValueAt.getValue());
            }
          	t.addOperation(op);            
          }
        }
      }      
      //cols
      NodeList cols = elem.getElementsByTagName("col");
      for(int k=0; k<cols.getLength(); k++){
        Element cEl = (Element)cols.item(k);
        Attr idAt = cEl.getAttributeNode("id.col");
        op.addCol(Integer.parseInt(idAt.getValue()));
      }
    }
    return t;
  }         
}
