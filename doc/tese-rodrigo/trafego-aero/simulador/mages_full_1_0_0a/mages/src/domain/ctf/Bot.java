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

package mages.domain.ctf;

import mages.util.*;
import mages.fuzion.Character;
import mages.fuzion.Weapon;
import mages.fuzion.WeaponsCollection;
import mages.simulation.*;
import mages.agents.*;

import java.util.*;
import java.io.*;
import java.lang.reflect.*;

public class Bot implements Serializable, Stats, DynamicStats, Character, 
     Identificable, Directionable, Cloneable{
  public static final int MAX_WEAPONS = 2;
  public static final int WEAPON_1 = 1;
  public static final int WEAPON_2 = 2;  
     
  private final String[] attribsName = {"STR","DEX","BODY","MOV","COU"};
  private final String[] skillsName = {"FA","HVW","HTH","EV"};
  
  String name;
  String description;
  int hits;
  double rec;
  double fat;
  int pointsHits;
  int pointsFat;
  int addHits;
  int addFat;
  protected Hashtable attributes;
  protected Hashtable skills;
  protected Weapon weapon1, weapon2;
  Armor armor;  
  mages.util.User user;
  protected Date date;
  Coord pos = new Coord(0,0);
  String controlName;
  int useWeapon = WEAPON_1;
  protected Flag flag = null;
  boolean dodge = false;
  int direction = CtFSettings.NORTH;
  int curHits;
  double curFat;
  int idGroup;
  int modePos = SCREEN;
  int spaceWidth, spaceHeight;
  String ownerId;
  int numId = -1;
  
  public Bot(){
    attributes = new Hashtable(attribsName.length);
    skills = new Hashtable(skillsName.length);    
    for(int i=0; i<attribsName.length; i++){
      attributes.put(attribsName[i],new Feature(attribsName[i]));    
    }
    
    skills.put(skillsName[0],new Feature(skillsName[0],2));    
    skills.put(skillsName[1],new Feature(skillsName[1],4));       
    skills.put(skillsName[2],new Feature(skillsName[2],1));       
    skills.put(skillsName[3],new Feature(skillsName[3],1));       
    
    date = new Date();    
  }

  public Bot(int id){
    this();
    numId = id;
  }
    
  public void setName(String n){
    name = n;
  }

  public String getName(){
    return name;
  }

  public void setDescription(String d){
    description = d;
  }

  public String getDescription(){
    return description;
  }

  public int getHits(){
    return hits;
  }

  public double getRec(){
    return rec;
  }

  public void setFat(double f){
    if(f>fat){
      int d = (int)(f-fat);
      addFat = d;
      fat = curFat = f;
      pointsFat = d*3;
    }  
  }

  public double getFat(){
    return fat;
  }

  public int getPointsHits(){
    return pointsHits;
  }


  public int getPointsFat(){
    return pointsFat;
  }
  
  public void setArmor(Armor a){
    armor = a;
  }
  
  public void addWeapon(Weapon w, int n){
    switch(n){
      case WEAPON_1: weapon1 = w;
      break;
      case WEAPON_2: weapon2 = w;    
      break;
    }       
  }
  
  public float getBotPtos(){
    float sum = 0.0f;
    sum += getAttributesPtos();
    sum += getPlusPtos();
    sum += getSkillsPtos();
    sum += getWeaponsPtos();
    sum += getArmorPtos();            
    return sum;
  }

  public int getAttributesPtos(){
    int sum = 0;
    for(int i=0; i<attributes.size(); i++){
      Feature f = (Feature)attributes.get(attribsName[i]);    
      sum += f.getCost();
    }  
    return sum;
  }    
  
  public int getPlusPtos(){
    return getPointsHits()+getPointsFat();
  }  
  
  public int getSkillsPtos(){
    int sum = 0;
    for(int i=0; i<skills.size(); i++){
      Feature f = (Feature)skills.get(skillsName[i]);    
      sum += f.getCost();
    }  
    return sum;  
  }  
  
  public int getWeaponsPtos(){
    int sum = 0;
    sum += (weapon1==null)?0:weapon1.getCost();
    sum += (weapon2==null)?0:weapon2.getCost()+30;    
    return sum;
  }  
  
  public float getArmorPtos(){
    return armor.getCost();
  }  

  public int getAttribute(String a){
    Feature f = (Feature)attributes.get(a);  
    int tmp = 0;
    if(f!=null){
      tmp = f.getLevel();
    }           
    return tmp;
  }
  
  public int getSkill(String s){
    Feature f = (Feature)skills.get(s);  
    int tmp = -1;
    if(f!=null){
      tmp = f.getLevel();
    }           
    return tmp;
  }
      
  public void setAttribute(String a, int v){
    Feature f = (Feature)attributes.get(a);         
    f.setLevel((v<0)?0:v);
    if(f!=null){
      if(a.equals("FAT")){
        fat = getAttribute("BODY")*10;
        pointsFat = 0;        
      }
      else{
        if(a.equals("STR")||a.equals("BODY")){          
           rec = (getAttribute("STR")+getAttribute("BODY"))/100.0;
           if(a.equals("BODY")){
             hits = getAttribute("BODY")*10;
             curHits = hits;
             pointsHits = 0;     
             fat = getAttribute("BODY")*10;
             curFat = fat;
             pointsFat = 0;        
           }             
        }
      }
    }   
  }  
    
  public void setUser(mages.util.User u){
    user = u;
  }      
  
  public void setSkill(String s, int v){
    Feature f = (Feature)skills.get(s);         
    f.setLevel((v<0)?0:v);  
  }        
  
  public void setHits(int h){
    if(h>hits){
      int d = h-hits;
      addHits = d;
      hits = curHits = h;
      pointsHits = d*5;
    }  
  }        
    
  public static boolean validate(Bot b, Tournament t){
    boolean tmp = false;
    if(b.getBotPtos()<=t.getMaxBotsPtos()){
      tmp = true;
    }
    return tmp;
  }  
  
  public mages.util.User getUser(){
    return user;
  }  
  
  public Feature[] getAttributes(){
    Feature[] tmp = new Feature[attributes.size()];
    for(int i=0; i<attributes.size(); i++){
      tmp[i] = (Feature)attributes.get(attribsName[i]);
    }    
    return tmp;
  }
  
  public Feature[] getSkills(){
    Feature[] tmp = new Feature[skills.size()];
    for(int i=0; i<skills.size(); i++){
      tmp[i] = (Feature)skills.get(skillsName[i]);
    }    
    return tmp;  
  }  
  
  public Armor getArmor(){
    return armor;
  }
  
  public Weapon[] getWeapons(){
    Weapon[] tmp = null;
    if(weapon2==null){
      tmp = new Weapon[1];
      tmp[0] = weapon1;      
    }
    else{
      tmp = new Weapon[2];    
      tmp[0] = weapon1;
      tmp[1] = weapon2;              
    }
    return tmp;  
  }
  
  public int getAddHits(){
    return addHits;
  }
  
  public int getAddFat(){
    return addFat;
  }  
    
  public void setAttributes(Feature[] f){
     attributes = null;
     for(int i=0; i<f.length; i++){
       attributes.put(f[i].getName(),f[i]);
     }
  }  
  
  public void setSkills(Feature[] f){
     skills = null;
     for(int i=0; i<f.length; i++){
       skills.put(f[i].getName(),f[i]);
     }
  }  
  
  public Date getDate(){
    return date;
  }
  
  public void setWeapons(WeaponsCollection wc){
    Enumeration list = wc.getElements();
    int c = 1;     
    while(list.hasMoreElements()){
      Weapon w = (Weapon)list.nextElement();
      addWeapon(w,c);
      c++;
    }
  }
  
  public void print(){
    //User
    System.out.println("USER");
    user.print();

    System.out.println("BOT ("+getBotPtos()+") BP");    
    System.out.println("Name        : "+name);
    System.out.println("Description : "+description);    
    Calendar c = new GregorianCalendar();
    c.setTime(date);
    System.out.println("Created     : "+(c.get(Calendar.MONTH)+1)+"/"+  
                        c.get(Calendar.DATE)+"/"+c.get(Calendar.YEAR));
    //Attributes
    System.out.println("ATTRIBUTES ("+getAttributesPtos()+") BP");    
    for(int i=0; i<attribsName.length; i++){
      System.out.println(attribsName[i]+" : "+getAttribute(attribsName[i]));
    }
    
    //Skills
    System.out.println("SKILLS ("+getSkillsPtos()+") BP");    
    for(int i=0; i<skillsName.length; i++){
      System.out.println(skillsName[i]+" : "+getSkill(skillsName[i]));
    }    
    
    //Hits, Fat & Rec
    System.out.println("Hits : "+getHits()+" Fat: "+getFat()+" Rec: "+getRec()+
                       " ("+getPointsHits()+"/"+getPointsFat()+") BP");
    
    //Armor
    System.out.println("Armor : "+armor.getLevel()+" ("+armor.getCost()+") BP");
    
    //Weapons
    if(weapon2==null){
      System.out.println("Weapon 1: "+weapon1.getName()+" ("+weapon1.getCost()+
                         ") BP"+" Total: ("+getWeaponsPtos()+") BP");
    }
    else{
      System.out.println("Weapon 1: "+weapon1.getName()+" ("+weapon1.getCost()+") BP"+
                         " Weapon 2: "+weapon2.getName()+" ("+weapon2.getCost()+") BP"+
                         " Total: ("+getWeaponsPtos()+") BP");     
    } 
  }

  public Coord getPosition(){
    return pos;
  }
  
  public void setPosition(Coord c){
    pos = c;
    if(flag!=null){
      flag.setPosition(c);
    }
  }
  
  public String getControlName(){
    return controlName;
  }
  
  public void setControlName(String n){
    controlName = n;
  }  
  
  public String[] getStatsNames(){
    String[] tmp = {STR,DEX,BODY,MOV,COU,FA,HVW,HTH,EV,"ARMOR","REC"};
    return tmp;
  }
  
  public String[] getStatsValues(){
    String[] tmp = {Integer.toString(getAttribute(STR)),
                    Integer.toString(getAttribute(DEX)),
                    Integer.toString(getAttribute(BODY)),
                    Integer.toString(getAttribute(MOV)),
                    Integer.toString(getAttribute(COU)),
                    Integer.toString(getSkill(FA)),
                    Integer.toString(getSkill(HVW)),
                    Integer.toString(getSkill(HTH)),
                    Integer.toString(getSkill(EV)),
                    Integer.toString(getArmor().getLevel()),
                    Double.toString(getRec())};                                      
    return tmp;                    
  }
  
  public String[][] getStatsSetup(){
    String[][] tmp = null;
    if(weapon2!=null){
      tmp = new String[4][4];
    }
    else{
      if(weapon1!=null){
        tmp = new String[3][4];
      }
      else{
        tmp = new String[2][4];      
      }  
    }
    
    tmp[0][0] = "Hits";
    tmp[0][1] = tmp[1][1] = Integer.toString(0);
    tmp[0][2] = Integer.toString(curHits);
    tmp[0][3] = Integer.toString(getHits());
        
    tmp[1][0] = "Fat";
    tmp[1][2] = Double.toString(curFat);
    tmp[1][3] = Double.toString(getFat());
    
    int cnt = 2;
    if(weapon1!=null){
      tmp[cnt][0] = weapon1.getName();
      tmp[cnt][1] = Integer.toString(0);
      tmp[cnt][2] = Integer.toString(weapon1.getAmno());
      tmp[cnt][3] = Integer.toString(weapon1.getAmno());    
      cnt++;
    }

    if(weapon2!=null){
      tmp[cnt][0] = weapon2.getName();
      tmp[cnt][1] = Integer.toString(0);
      tmp[cnt][2] = Integer.toString(weapon2.getAmno());
      tmp[cnt][3] = Integer.toString(weapon2.getAmno());    
    }    
    return tmp;
  }

  public String[] getValues(){
    String[] tmp = new String[4];
    tmp[0] = Integer.toString(curHits); 
    tmp[1] = Double.toString(curFat);
    tmp[2] = Integer.toString(weapon1.getAmnoCur());    
    tmp[3] = (weapon2!=null)?Integer.toString(weapon2.getAmnoCur()):"-1";   
    return tmp;
  }     

  public void setValues(String[] v){
    curHits = Integer.parseInt(v[0]);
    curFat = Double.parseDouble(v[1]);
    if(weapon2!=null){
      weapon2.defineAmno(Integer.parseInt(v[4]));
    }
    else{
      if(weapon1!=null){
        weapon1.defineAmno(Integer.parseInt(v[3]));
      }
    }        
  }
    
  public void hits(int p, int t){
    int value = p-armor.getLevel();
    if(value>0){    
      if(t==USE_HAND){
        curHits -= (value);
      }
      else{
        if(t==USE_WEAPON){
          curHits -= (value*2);
        }
      }  
    }
    curHits = (curHits<0)?0:curHits;
    if(curHits==0){
      setPosition(new Coord(Double.POSITIVE_INFINITY,Double.POSITIVE_INFINITY));
    }
  }
  
  public int getInitiative(){
    return getAttribute("DEX");
  }
  
  public boolean isSolid(){
    if(isDead()){
      return false;
    }
    else{    
      return true;
    }  
  }

  public int getPositionState(){
    return 0;
  }  
  
  public int move(){
    int v = getAttribute(Character.MOV)+getModFatigue();
    v = (v<0)?0:v;
    return v;      
  }  
  
  public int run(){
    int v = getAttribute(Character.MOV)+getModFatigue();
    v = (v<0)?0:v;
    return v*2;
  }
  
  public int slowWalk(){
    int v = getAttribute(Character.MOV)+getModFatigue();
    v = (v<0)?0:v;
    return (int)(v*0.5);  
  }
  
  public Weapon getActiveWeapon(){
    if(useWeapon == WEAPON_1){
      return weapon1;
    }
    else{
      if(weapon2==null){
        return weapon1;
      }
      else{
        return weapon2;
      }
    }
  }
  
  public void activeWeapon(int id){
    switch(id){
      case WEAPON_1: useWeapon = WEAPON_1;
      break;
      case WEAPON_2: useWeapon = (weapon2==null)?WEAPON_1:WEAPON_2;
      break;
    }
  }
  
  public void fatigue(double p){
    curFat -= p;
    curFat =(curFat<0)?0:curFat;
  }
  
  public void getFlag(Flag f){
    f.setVisible(false);
    f.setPosition(new Coord(Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY));
    flag = f;
  }
  
  public Flag dropFlag(){
    if(flag!=null){
      flag.setVisible(true);
      return flag;
    }
    return null;  
  }
  
  public void resetFlag(){
    flag = null;
  }
  
  public Flag getFlag(){
    return flag;
  }
  
  public boolean hasFlag(){
    return (flag==null)?false:true;
  }
  
  public void recovery(){
    curFat += rec;
    curFat = (curFat>fat)?fat:curFat;
  }
  
  public void setDodge(boolean f){
    dodge = f;
  }
  
  public boolean isDodge(){
    return dodge;
  }
  
  public void rotate(int degree){
    int d = (degree>360)?360:degree;
    d = (degree<0)?0:d;
    direction = d;
  }
  
  public int getDirection(){
    return direction;
  }
  
  public int getModFatigue(){
    if(curFat==fat){
      return 0;
    }
    else{
      if(curFat>=(fat/2) && curFat==(fat-1)){
        return -1;
      }
      else{
        if(curFat>=(fat/4) && curFat<(fat/2)){
          return -2;
        }
        else{
          if(curFat<(fat/4)){
            return -4;
          }
        }
      }
    }  
    return 0;
  }
  
  public int rangeVision(){
    return 10;
  }  
  
  public int getHitsCur(){
    return curHits;
  }  
  
  public double getFatCur(){
    return curFat;
  }  
  
  public void setId(int i){
    idGroup = i;
  }
  
  public int getId(){
    return idGroup;
  }
  
  public void defineHits(int h){
    curHits = h;
  }
  
  public void defineFat(double f){
    curFat = f;
  }  
  
  public void defineAmno(int w, int v){
    if(w == WEAPON_1){
      weapon1.defineAmno(v);
    }
    else{
      if(w==WEAPON_2){
        weapon2.defineAmno(v);
      }
    }
  }
    
  public int getPositionMode(){
    return SCREEN;
  }
  
  public boolean isDead(){
    return (curHits<=0)?true:false;
  }
   
   public Object clone(){
     try{ 
       Bot b = (Bot)super.clone();
       b.setName(new String(b.getName()));
       b.setOwnerId(new String(b.getOwnerId()));       
       b.setDescription(new String(b.getDescription()));       
       b.setControlName(new String(b.getControlName()));       
       b.setPosition((Coord)getPosition().clone());
       if(armor!=null) b.setArmor((Armor)getArmor().clone());
       if(user!=null) b.setUser((User)getUser().clone());
       b.date = (Date)getDate().clone();
       if(flag!=null) b.flag = (Flag)flag.clone();
       b.attributes = (Hashtable)attributes.clone();
       b.skills = (Hashtable)skills.clone();       
       if(weapon1!=null) b.weapon1 = (Weapon)weapon1.clone();
       if(weapon2!=null) b.weapon2 = (Weapon)weapon2.clone();       
       return b;
     }
     catch(CloneNotSupportedException e){
       return null;
     } 
   }

   public Object copy(){
     return clone();
   }   
   
   public void setOwnerId(String id){
     ownerId = id;   
   }
   
   public String getOwnerId(){
     return ownerId;   
   }   

  public void setNumId(int i){
    numId = i;
  }
     
  public int getNumId(){
    return numId;
  }
}