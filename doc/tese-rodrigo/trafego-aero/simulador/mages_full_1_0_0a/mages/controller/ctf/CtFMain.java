

package mages.controller.ctf;

import mages.gui.*;
import mages.*;
import mages.controller.ctf.client.*;

public class CtFMain implements AgentWindowsFactory{

  public CtFMain(){}

  public void factory(MagesClient mc, Object[] objs){  
    CtFBot[] b = (CtFBot[])objs[1];
    boolean isHuman = ((Boolean)objs[2]).booleanValue();
    boolean useLog = ((Boolean)objs[3]).booleanValue();
    int mode = (isHuman)?AgentWindow.HUMAN:AgentWindow.COMPUTER;  
    for(int i=0; i<b.length; i++){
      //tem que especficar o agente
      AgentWindow aw = new AgentWindow(mc,b[i],mode, useLog);
      mc.getDesktop().addDesktop(aw);
    }    
  }
}               
                 