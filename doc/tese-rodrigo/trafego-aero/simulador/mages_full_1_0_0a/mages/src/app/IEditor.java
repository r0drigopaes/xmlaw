

package mages.app;


public interface IEditor{
  public static final int READY_STATE = -1;
  public static final int NEW_STATE = 0;
  public static final int FILE_SAVED_STATE = 1;
  public static final int FILE_EDITED_STATE = 2;
   
  public void init();
  public boolean save();
  public void saveAs();
  public void open();  
  public void exit();    
  public void setController(IController ctrl);  
  public void setControled(IControlable ctrled);
  public void setStatus(int s);
  public int getStatus();  
  public String[] getValues();      
}

