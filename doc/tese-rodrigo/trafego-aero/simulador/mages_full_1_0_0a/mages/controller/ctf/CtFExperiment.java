
package mages.controller.ctf;

import mages.simulation.*;
import mages.domain.ctf.*;
import mages.fuzion.*;
import mages.app.*;
import java.util.*;
import java.io.*;


public class CtFExperiment extends Experiment{

  long cntRuns = 0;
  
  public CtFExperiment(CtFRules rules, int mbp){
    super("CTF");
    World map = rules.getWorld();
    
    //Máximo de turnos
    addCondition(new Parameter("MAX_TURNS",Integer.toString(rules.getMaxTurns())));   
    
    //Count bots
    Bot bots[] = map.getBots();    
    int botsRed = 0;
    int botsBlue = 0;

    for(int b=0; b<bots.length; b++){
      if(bots[b].getId()==CtFSettings.RED){
        botsRed++;
      }
      else{
        botsBlue++;
      }

      addCondition(new Parameter("BOT_BP_"+b,Float.toString(bots[b].getBotPtos())));
      addCondition(new Parameter("BOT_CONTROL_"+b,bots[b].getControlName()));

      Weapon wpns[] = bots[b].getWeapons();
      int w1 = 0;
      int w2 = 0;
      if(wpns.length==1){
        w1 = wpns[0].getAmno();
      }   
      else{
        w1 = wpns[0].getAmno();      
        w2 = wpns[1].getAmno();
      }         
      addCondition(new Parameter("BOT_WPN1_"+b,Integer.toString(w1)));      
      addCondition(new Parameter("BOT_WPN2_"+b,Integer.toString(w2)));            
    }
    addCondition(new Parameter("BOTS_RED",Integer.toString(botsRed)));
    addCondition(new Parameter("BOTS_BLUE",Integer.toString(botsBlue)));        
    addCondition(new Parameter("MAX_BP",Integer.toString(mbp)));
    
    //Count flags
    Flag flags[] = map.getFlags();
    int flagsRed = 0;
    int flagsBlue = 0;
    for(int b=0; b<flags.length; b++){
      if(flags[b].getId()==CtFSettings.RED){
        flagsRed++;
      }
      else{
        flagsBlue++;
      }      
    }

    addCondition(new Parameter("FLAGS_RED",Integer.toString(flagsRed)));
    addCondition(new Parameter("FLAGS_BLUE",Integer.toString(flagsBlue)));  

    //Environment
    addCondition(new Parameter("ENV_WIDTH",Integer.toString(map.getWidth())));    
    addCondition(new Parameter("ENV_HEIGHT",Integer.toString(map.getHeight())));        
    addCondition(new Parameter("ENV_COST_AVR",Double.toString(map.avrCostTerrain())));            
    addCondition(new Parameter("ENV_FIX_OBS",Integer.toString(map.terrainsSolid())));                 
  }
  
  public void addNewReplication(CtFRules rules, long seed){
    World map = rules.getWorld();
    Run run = new Run(cntRuns++,seed);
    int flagsRedTeam = map.getNumFlagsInBase(CtFSettings.RED,CtFSettings.BLUE);
    int flagsBlueTeam = map.getNumFlagsInBase(CtFSettings.BLUE,CtFSettings.RED);    
    run.addMeasureOfPerformance(new Parameter("TURNS",Integer.toString(rules.getTurn()-1)));    
    run.addMeasureOfPerformance(new Parameter("FLAGS_RED_TEAM",Integer.toString(flagsRedTeam)));
    run.addMeasureOfPerformance(new Parameter("FLAGS_BLUE_TEAM",Integer.toString(flagsBlueTeam)));    

    Bot bots[] = map.getBots();    
    int cntRed = 0;
    int cntBlue = 0;
    for(int b=0; b<bots.length; b++){    
      if(!bots[b].isDead()){
        if(bots[b].getId()==CtFSettings.RED) cntRed++;
        else cntBlue++;
      }
      run.addMeasureOfPerformance(new Parameter("BOT_HITS_"+b,Integer.toString(bots[b].getHitsCur())));      
      run.addMeasureOfPerformance(new Parameter("BOT_FAT_"+b,Double.toString(bots[b].getFatCur())));            

      Weapon wpns[] = bots[b].getWeapons();
      int w1 = 0;
      int w2 = 0;
      if(wpns.length==1){
        w1 = wpns[0].getAmnoCur();
      }   
      else{
        w1 = wpns[0].getAmnoCur();      
        w2 = wpns[1].getAmnoCur();
      }         
      run.addMeasureOfPerformance(new Parameter("BOT_END_WPN1_"+b,Integer.toString(w1)));      
      run.addMeasureOfPerformance(new Parameter("BOT_END_WPN2_"+b,Integer.toString(w2)));
    }
    run.addMeasureOfPerformance(new Parameter("BOT_IN_RED",Integer.toString(cntRed)));    
    run.addMeasureOfPerformance(new Parameter("BOT_IN_BLUE",Integer.toString(cntBlue)));  
    addRun(run);      
  }
  
  public void save(String path) throws IOException{
    ExperimentController ex = new ExperimentController();
    ex.save(this,path);  
  }
}
  //MEDIDAS
  //time
  //número de bandeiras capturadas pelo time Red
  //número de bandeiras capturadas pelo time Blue  
  //número de bots sobreviventes no time Red
  //número de bots sobreviventes no time Blue  
  //para cada bot
  // energia final
  // fadiga final
  // munição final na arma #1
  // munição final na arma #2
  
  //FATORES
  //time
  //máximo de pontos de bots  
  //número de bots no time Red
  //número de bots no time Blue
  //número de flags no time Red
  //número de flags no time Blue
  
  //para cada bot
  //número de pontos
  //controladora
  // munição na arma #1
  // munição na arma #2  
  
  //ambiente
  //altura
  //largura
  //custo médio
  //número de obstáculos fixos    