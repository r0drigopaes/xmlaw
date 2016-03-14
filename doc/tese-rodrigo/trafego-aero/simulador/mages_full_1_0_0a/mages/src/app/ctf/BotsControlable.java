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

package mages.app.ctf;

import mages.*;
import mages.app.*;
import mages.util.*;
import mages.domain.ctf.*;
import mages.fuzion.*;

import java.awt.*;
import java.io.*;
import java.util.*;

public class BotsControlable implements IControlable{
  Bot bot = null;
  IEditor owner = null;
  
  public void init(){
    bot = new Bot();
  } 
  
  public Object getObject() throws IOException{
    bot = null;
    String[] tmp = owner.getValues();    
    try{
      bot = new Bot();
      Properties prop = new Properties();
      FileInputStream file = new FileInputStream(tmp[0]);
      prop.load(file); 

      //Create User
      User user = new User();
      user.setName(prop.getProperty("USER_NAME",""));
      user.setEmail(prop.getProperty("USER_EMAIL",""));
      user.setWebsite(prop.getProperty("USER_SITE",""));
      user.setInstitution(prop.getProperty("USER_INST",""));
      user.setCountry(prop.getProperty("USER_COUNTRY",""));       
      bot.setUser(user); 

      //Header
      bot.setName(prop.getProperty("BOT_NAME","Default"));                
      bot.setDescription(prop.getProperty("BOT_DESCRIPTION",""));                      
      bot.setControlName(prop.getProperty("BOT_CONTROL_NAME"));

      //Atributes
      bot.setAttribute("STR",Integer.parseInt(prop.getProperty("BOT_STR","1")));
      bot.setAttribute("DEX",Integer.parseInt(prop.getProperty("BOT_DEX","1")));
      bot.setAttribute("BODY",Integer.parseInt(prop.getProperty("BOT_BODY","1")));
      bot.setAttribute("MOV",Integer.parseInt(prop.getProperty("BOT_MOV","1")));
      bot.setAttribute("COU",Integer.parseInt(prop.getProperty("BOT_COU","1")));

      //Skills
      bot.setSkill("FA",Integer.parseInt(prop.getProperty("BOT_FA","0")));                              
      bot.setSkill("HVW",Integer.parseInt(prop.getProperty("BOT_HVW","0")));                              
      bot.setSkill("HTH",Integer.parseInt(prop.getProperty("BOT_HTH","0")));                              
      bot.setSkill("EV",Integer.parseInt(prop.getProperty("BOT_EV","0")));    

      //Other attributes
      bot.setHits(Integer.parseInt(prop.getProperty("BOT_HITS","0")));    
      bot.setFat(Integer.parseInt(prop.getProperty("BOT_FAT","0")));    

      //Armor
      Armor armor = new Armor(Integer.parseInt(prop.getProperty("ARMOR_LEVEL","0")));                                                  
      bot.setArmor(armor);

      //Weapons collection
      WeaponsController wcontrol = new WeaponsController();
      WeaponsCollection wc = (WeaponsCollection)wcontrol.load("weapons.xml"); 

      //Weapons
      int maxW = Integer.parseInt(prop.getProperty("MAX_WEAPONS","1"));
      for(int i=1; i<=maxW; i++){
        String name = prop.getProperty("WEAPON"+i+"_NAME","");
        Weapon weapon = wc.getWeapon(name.trim());
        bot.addWeapon(weapon,i);
      }
    }
    catch(IOException e1){
      System.out.println("Error: "+e1.getMessage()); 
    }      
    
    TournamentController tc = new TournamentController();
    Tournament tourn = (Tournament)tc.load(MagesEnv.getFilesDir("ctfTourn")+tmp[1]);
    if(!Bot.validate(bot,tourn)){
      throw new IOException("Bot imcompatible with Tournament rules. ("+bot.getBotPtos()+">"+tourn.getMaxBotsPtos()+") BP");      
    }
    return bot;  
  }
  
  public void setObject(Object obj){}
 
  public void setEditor(IEditor e){
    owner = e;
  }
 
  public void update() throws IOException{}
  
  //This method can have problem.
  public boolean validateObject() throws FormatException{return true;}  
}