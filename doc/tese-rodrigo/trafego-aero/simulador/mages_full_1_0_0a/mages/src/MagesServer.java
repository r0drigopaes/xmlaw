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

import clisp.*;
import mages.controller.ctf.*;
import mages.controller.ctf.server.*;
import java.util.*;
import java.io.*;

public class MagesServer extends ServerApp{
  final String version = "1.0a";
  
  int maxBotsSession;
  long maxComputerTime;
  long maxHumanTime;  
  boolean useScreen;
  int ttt;
  long rec;
  long tolerance;
  int maxRuns;
  String email;
  long timeComm;
  
  public MagesServer(){
    super(10);
    copyleft();        
  }
      
  public void init(){
    //Properties
    try{    
      Properties prop = new Properties();    
      FileInputStream file = new FileInputStream("config");
      prop.load(file);    
      maxBotsSession = Integer.parseInt(prop.getProperty("MAX_BOTS_SESSION","8"));
      maxComputerTime = Long.parseLong(prop.getProperty("TIME_COMPUTER_AGENT","100"));      
      maxHumanTime = Long.parseLong(prop.getProperty("TIME_HUMAN_AGENT","300000"));            
      int tmpScr =  Integer.parseInt(prop.getProperty("USE_SCREEN","1"));
      useScreen = (tmpScr==1)?true:false;
      ttt = Integer.parseInt(prop.getProperty("TIMES_TO_TRY","3"));      
      rec = Long.parseLong(prop.getProperty("TIME_RECOVERY","600000"));                  
      tolerance = Long.parseLong(prop.getProperty("TOLERANCE","5"));                        
      maxRuns = Integer.parseInt(prop.getProperty("MAX_RUNS","1000"));      
      email = prop.getProperty("ADMIN_EMAIL","default@localdomain");        
      timeComm = Long.parseLong(prop.getProperty("TIME_COMM","50"));                                  
      file.close();
    }  
    catch(IOException e){
      System.out.println("ERROR : File 'config' wasn't found");
    }
        
    try{
      //CtFDaemon
      CtFDaemon d = new CtFDaemon();
      d.setMaxComputerTime(maxComputerTime);
      d.setMaxHumanTime(maxHumanTime);  
      d.setUseScreen(useScreen);        
      d.setTimesToTry(ttt);                    
      d.setRecoveryTime(rec);   
      d.setTolerance(tolerance);        
      d.setAdminEmail(email);                     
      addDaemon(d);                
      System.out.println("INIT : "+d.getName());
    
      //CtFUserCleanDaemon
      CtFUserCleanDaemon cd = new CtFUserCleanDaemon(d);
      addDaemon(cd);                
      System.out.println("INIT : "+cd.getName());
      
      //CtFBotDaemon
      CtFBotDaemon bd = new CtFBotDaemon(d,maxBotsSession);
      bd.setTimesToTry(ttt);    
      bd.setTimeComm(timeComm);          
      addDaemon(bd);                
      System.out.println("INIT : "+bd.getName());      
    }
    catch(ConfigurationException e){
      System.out.println("ERROR : Problems with daemon process");      
    }    
  }
  
  private void copyleft(){
    System.out.println("GNU MagesServer version "+version+", Copyright (C) 2001 João Ricardo Bittencourt");
    System.out.println("GNU MagesServer comes with ABSOLUTELY NO WARRANTY; \nfor details read '/mages/gpl.txt'");
    System.out.println("This is free software, and you are welcome to redistribute it");
    System.out.println("under certain conditions; read `mages/gpl.txt' for details.\n\n");    
  }
  
  public static void main(String args[]){   
    MagesServer server = new MagesServer(); 
    server.run(); 
  }
}  