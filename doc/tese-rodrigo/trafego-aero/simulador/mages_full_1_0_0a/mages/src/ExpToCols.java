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

package mages;


import mages.app.*;
import java.io.*;

public class ExpToCols{
  
  //values[row][col]
  //values.length --> row
  //values[0].length --> col
  public ExpToCols(String exp, String cols, int mode){
    String values[][] = null;
    
    copyleft();
      
    try{
      values = ExperimentConversor.convertToCols(exp,mode);
      try{
        FileOutputStream file = new FileOutputStream(cols);
        PrintStream stream = new PrintStream(file);
        for(int r=0; r<values.length; r++){
          for(int c=0; c<values[r].length; c++){
            stream.print(values[r][c]+" ");
          }
          stream.println();
        }
      }
      catch(FileNotFoundException e1){
        System.out.println("ExToCols: Error! "+cols+" file");    
      }        
    }  
    catch(IOException e){
      System.out.println("ExToCols: Error! The file "+exp+" wasn't found\n");
    }    
  }
  
  private void copyleft(){
    System.out.println("GNU Mages version 1.0a, Copyright (C) 2001-2002 João Ricardo Bittencourt");
    System.out.println("GNU ExpToCols version 1.0a, Copyright (C) 2002 João Ricardo Bittencourt");    
    System.out.println("GNU ExpToCols comes with ABSOLUTELY NO WARRANTY; \nfor details read '/mages/gpl.txt'");
    System.out.println("This is free software, and you are welcome to redistribute it");
    System.out.println("under certain conditions; read `mages/gpl.txt' for details.\n\n");    
  }
    
  public static void main(String args[]){
    if(args.length==3){
      try{
        int mode = Integer.parseInt(args[2]);
        if(mode==ExperimentConversor.FACTORS_AND_MEASURES ||
           mode==ExperimentConversor.MEASURES){
          ExpToCols etc = new ExpToCols(args[0],args[1],mode);
        }
        else{
          System.out.println("ExToCols: Error! Invalid mode\n");                  
        }  
      }
      catch(NumberFormatException e){
        System.out.println("ExToCols: Error! Mode isn't number\n");          
      }  
    }
    else{
      System.out.println("ExToCols: Error! Wrong parameters\n");          
    }  
  }
}