

package mages.app;

import java.io.*;

public abstract class Formatter implements IEditor{
  //abrir um arquivo de propriedades A e colocar o arquivo no formato B
  //exibir os dados contidos no formato B
  String operation = null;
  String source, target;
  String defaultDir;  
  IController control = null;
  IControlable controled = null;
  int state;
  
  public Formatter(String op, String s, String t){
    operation = op;
    source = s;
    target = t;
  }
  
  public void setController(IController c){
    control = c;
  }
  
  public void setControled(IControlable c){
    controled = c;
  }
   
  public void setDefaultDir(String dir){
    defaultDir = dir;
  }
      
  public abstract void init();
  public abstract void header();  
  public abstract void help();
  public abstract void show(Object o);
    
  protected String getSource(){
    return source;
  }
  
  private boolean existFile(String name){
    File tmp = new File(name);
    return tmp.exists();
  }
    
  public void create() throws IOException{  
    Object o = controled.getObject();
    if(o!=null){    
      boolean canSave = true;
      if(existFile(defaultDir+target)){   
        System.out.println("Warning !! Do you want to rewrite "+target+" ? (Type Y)");      
        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
        String tmp = in.readLine();        
        char ch = tmp.charAt(tmp.length()-1);
        if(ch=='Y' || ch=='y'){
          canSave = true;
        }
      }
      if(canSave){
        control.save(o,defaultDir+target);      
      }
    }
  }
  
  public void view() throws IOException{
    show(control.load(defaultDir+source));
  }
    
  public void start() throws IOException{
    init();
    header();
    if(operation.equals("-create")){
      create();
    }
    else{
      if(operation.equals("-view")){
        view();
      }
      else{
        help();
      }
    }
  }

  public boolean save(){return true;}
  public void saveAs(){}
  public void open(){}  
  public void exit(){}    
  public void setStatus(int s){
    state = s;
  }  

  public int getStatus(){
    return state;
  }  
  
  public String[] getValues(){
    return null;
  }    
}