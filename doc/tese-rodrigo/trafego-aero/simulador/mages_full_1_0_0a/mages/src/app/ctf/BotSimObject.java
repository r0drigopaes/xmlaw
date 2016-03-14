

package mages.app.ctf;

import mages.simulation.*;
import mages.domain.ctf.*;

public class BotSimObject extends SimObject{
  Bot bot = null;
  
  public BotSimObject(Bot b){
    bot = b;
  }
  
  public Bot getBot(){
    return bot;
  }
}