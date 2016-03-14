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
import java.util.*;

public class Conversor{
  String source[][] = null;
  Source target = null;
  
  public Conversor(){
    copyleft();
  }
  
  public void readSource(String path) throws FileNotFoundException, IOException{
    FileReader file = new FileReader(path);      
    BufferedReader stream = new BufferedReader(file);         
    boolean more = true;
    int sum = 0;
    int maxCol = 0;

    while(more){      
      String line = stream.readLine();    
      if(line!=null){ 
        StringTokenizer token = new StringTokenizer(line," \t;");
        maxCol = (token.countTokens()>maxCol)?token.countTokens():maxCol;      
        sum++;
      }
      else{
        more = false;
      }              
    }
    
    stream.close();
    file.close();
    file = new FileReader(path);      
    stream = new BufferedReader(file);             
    more = true;
    
    source = new String[sum][maxCol];
    int r = 0;
    while(more){
      String line = stream.readLine();    
      if(line!=null){ 
        StringTokenizer token = new StringTokenizer(line," \t;");
        int c = 0;
        while(token.hasMoreElements()){
          source[r][c++] = token.nextToken();
        }        
        r++;
      }
      else{
        more = false;
      }      
    }  
  }
  
  public void process(Transformation t){
    target = t.conversion(source);
  }
  
  
  public Source getSource(){
    return target;
  }
             
  private static void help(){
    copyleft();
    System.out.println("Conversor <transformation_file.xml> <file source> <target1.txt,target2.txt>\n");  
  }
  
  private static void copyleft(){
    System.out.println("GNU Mages version 1.0a, Copyright (C) 2001-2002 Jo\u00E3o Ricardo Bittencourt");
    System.out.println("GNU Conversor version 1.0a, Copyright (C) 2002 João Ricardo Bittencourt");    
    System.out.println("GNU Conversor comes with ABSOLUTELY NO WARRANTY; \nfor details read '/mages/gpl.txt'");
    System.out.println("This is free software, and you are welcome to redistribute it");
    System.out.println("under certain conditions; read `mages/gpl.txt' for details.\n\n");    
  }
  
  private static String[] listNames(String str){
    StringTokenizer token = new StringTokenizer(str,":");
    Vector tmp = new Vector();
    while(token.hasMoreElements()){
      tmp.addElement(token.nextToken());
    }
    String tmp2[] = new String[tmp.size()];
    for(int i=0; i<tmp.size(); i++){
      tmp2[i] = (String)tmp.elementAt(i);
    }
    return tmp2;
  }
         
  public static void main(String args[]){
    if(args.length==3){
      Conversor c = new Conversor();
      String names[] = listNames(args[2]);
    
      try{
        TransformationController tx = new TransformationController();  
        Transformation t = (Transformation)tx.load(args[0]);
        if(t.getNumMethods()==names.length){
          c.readSource(args[1]);           
          c.process(t);     
          t.setSource(c.getSource());
          try{
            t.save(names);             
          }
          catch(IOException e0){
            System.out.println("Conversor: Error! Problems with "+args[2]+" file");
          }
        }
        else{
          System.out.println("Conversor: Error! Incorret number of names for methods");          
        }  
      }
      catch(IOException e1){
        System.out.println("Conversor: Error! Problems with "+args[0]+" transformation file");
      }  
    }
    else{
      if(args.length==0){
        help();
      }
      else{
        System.out.println("Conversor: Error! Wrong parameters");          
      }  
    }  
  }
}
