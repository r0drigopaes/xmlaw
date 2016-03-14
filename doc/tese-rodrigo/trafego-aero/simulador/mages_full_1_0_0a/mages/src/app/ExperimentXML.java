
package mages.app;

import mages.app.*;
import mages.app.mapeditor.*;
//import mages.gui.*;
import mages.simulation.*;

import java.io.*;
import org.w3c.dom.*;
import org.apache.xerces.dom.*;
import org.apache.xml.serialize.*;

public class ExperimentXML extends XMLObject{
  
  String[] tags = {"domain.name"};
  String[] elems = {"conditions","replications"};
                 
  public Document createDoc(Object obj) throws IOException{
    Experiment exp = (Experiment)obj;

    DocumentImpl document = new DocumentImpl();  
    Element root = (Element) document.createElement("experiment");            
    
    Attr attribute = (Attr) document.createAttribute(tags[0]);
    attribute.setValue(exp.getDomainName());        
    root.setAttributeNode(attribute);                                    

    Element[] elements = new Element[elems.length];
    for(int i = 0; i<elements.length; i++){
      elements[i] = (Element) document.createElement(elems[i]);            
      root.appendChild(elements[i]);
    }

    //Conditions
    Parameter prms[] = exp.getConditions();
    ParameterXML px = new ParameterXML();        
    for(int i=0; i<prms.length; i++){
      Document docPrm = px.createDoc(prms[i]);
      Element prmEl = (Element)document.importNode(docPrm.getDocumentElement(),true);
      elements[0].appendChild(prmEl); 
    }

    //Replications
    Run runs[] = exp.getReplications();
    for(int j=0; j<runs.length; j++){
      Element runEl = (Element) document.createElement("run");
      Attr idRunAt = (Attr) document.createAttribute("run.id");      
      idRunAt.setValue(Long.toString(runs[j].getId()));        
      runEl.setAttributeNode(idRunAt);      
      
      if(runs[j].getSeed()!=-1){
        Attr seedRunAt = (Attr) document.createAttribute("seed");      
        seedRunAt.setValue(Long.toString(runs[j].getSeed()));        
        runEl.setAttributeNode(seedRunAt);              
      }         
          
      Parameter mop[] = runs[j].getMeasuresOfPerformance();
      for(int i=0; i<mop.length; i++){
        Document docPrm = px.createDoc(mop[i]);
        Element prmEl = (Element)document.importNode(docPrm.getDocumentElement(),true);
        runEl.appendChild(prmEl); 
      }      
      elements[1].appendChild(runEl);
    }              

    document.appendChild(root);         
    return document;                                   
  }

  public Object newInstance(Element root) throws IOException{           
    //Domain name
    Attr domainName = root.getAttributeNode(tags[0]);      
    Experiment exp = new Experiment(domainName.getValue());
    ParameterXML px = new ParameterXML();          
    
    //Conditions
    NodeList condLst = root.getElementsByTagName(elems[0]);           
    Element cond = (Element)condLst.item(0);
    NodeList paramLst = cond.getElementsByTagName("param");
    for(int i=0; i<paramLst.getLength(); i++){
      Element pEl = (Element)paramLst.item(i);
      Parameter prm  = (Parameter)px.newInstance(pEl);
      exp.addCondition(prm); 
    }
    
    //Replications
    NodeList repLst = root.getElementsByTagName(elems[1]);               
    Element rep = (Element)repLst.item(0);    
    NodeList runsLst = rep.getElementsByTagName("run");    
    for(int j=0; j<runsLst.getLength(); j++){
      Element runEl = (Element)runsLst.item(j);    
      Attr idAt = runEl.getAttributeNode("run.id");
      long id = Long.parseLong(idAt.getValue());
      Attr seedAt = runEl.getAttributeNode("seed");
      long seed = -1;      
      if(seedAt!=null){
        seed = Long.parseLong(seedAt.getValue());        
      }

      Run run = new Run(id,seed);
      NodeList prmLst = runEl.getElementsByTagName("param"); 
      for(int i=0; i<prmLst.getLength(); i++){
        Element pEl = (Element)prmLst.item(i);
        Parameter prm  = (Parameter)px.newInstance(pEl);
        run.addMeasureOfPerformance(prm);   
      }
      exp.addRun(run);
    }
    return exp;
  }         
}