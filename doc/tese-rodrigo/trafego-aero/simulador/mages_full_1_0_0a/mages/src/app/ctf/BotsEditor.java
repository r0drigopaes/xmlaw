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

import mages.app.*;
import mages.app.ctf.BotsController;
import mages.app.ctf.BotsControlable;
import mages.*;
import mages.domain.ctf.*;

import java.io.*;

public class BotsEditor extends Formatter{
  String tournName;
  
  public BotsEditor(){
    super(null,null,null);
  }
    
  public BotsEditor(String op, String s, String t, String rule){
    super(op,s,t);
    tournName = rule;    
  }
  
  public void init(){
    setController(new BotsController());
    BotsControlable ctr = new BotsControlable();
    ctr.init();
    ctr.setEditor(this);
    setControled(ctr);
    setDefaultDir(MagesEnv.getFilesDir("ctfBots"));    
  }
  
  public void header(){
    System.out.println("BotsEditor version 1.0a");
    System.out.println();
  }
    
  public void help(){
    System.out.println("java BotsEditor [-view|-create] [tournament] [source file] [target file]");
    System.out.println("  -view\tuse just source file");
    System.out.println("  -create\tuse source file, target file, and tournament rules\n");    
  }
    
  public void show(Object o){
    Bot bot = (Bot)o;
    bot.print();
  }      
 
  public String[] getValues(){
    String[] tmp = new String[2];
    tmp[0] = getSource();
    tmp[1] = tournName;
    return tmp;
  }
   
  public static void main(String args[]){    
    BotsEditor be = new BotsEditor();
    if(args.length==2 || args.length ==4){
      try{
        if(args.length==2){
          be = new BotsEditor(args[0],args[1],null,null);
        }
        else{
          //command texto target rules   
          //command0 rules1 text2 target3                  
          be = new BotsEditor(args[0],args[2],args[3],args[1]);        
        }  
        be.start();
      }
      catch(IOException e){
        System.out.println(e.getMessage()+"\n");
      }  
    }
    else{
      be.help();
    }
  }
}