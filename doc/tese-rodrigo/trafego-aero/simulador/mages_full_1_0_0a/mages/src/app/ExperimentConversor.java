

package mages.app;

import mages.*;
import mages.simulation.*;
import java.io.*;

public class ExperimentConversor{
  
  public static final int FACTORS_AND_MEASURES = 0;  
  public static final int MEASURES = 1;  
  
  public static String[][] convertToCols(String nameExp, int mode) throws IOException{
    String pathExp = MagesEnv.getFilesDir("exp")+MagesEnv.validate(nameExp);
    ExperimentController ex = new ExperimentController();
    
    Experiment exp = (Experiment)ex.load(pathExp);
        
    Parameter factors[] = exp.getConditions();
    Run rep[] = exp.getReplications();
    String values[][] = null;
    
    if(mode==FACTORS_AND_MEASURES){
      values = new String[rep.length][1+factors.length+rep[0].getNumMeasuresOfPerformance()];
      int cnt = 0;
      for(int r=0; r<rep.length; r++){
        values[r][0] = Long.toString(rep[r].getId());
        for(int f=0; f<factors.length; f++){
          values[r][f+1] = factors[f].getValue();
        }
        Parameter mop[] = rep[r].getMeasuresOfPerformance();
        int start =1+factors.length;
        for(int m=0;m<mop.length;m++){
          values[r][m+start] = mop[m].getValue();
        }
      }      
    }
    else{
      values = new String[rep.length][1+rep[0].getNumMeasuresOfPerformance()];    
      for(int r=0; r<rep.length; r++){
        values[r][0] = Long.toString(rep[r].getId());

        Parameter mop[] = rep[r].getMeasuresOfPerformance();
        for(int m=0;m<mop.length;m++){
          values[r][m+1] = mop[m].getValue();
        }
      }      
    }    
    return values;
  }
}