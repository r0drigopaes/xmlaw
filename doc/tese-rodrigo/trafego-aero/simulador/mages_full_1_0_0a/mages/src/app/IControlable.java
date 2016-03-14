

package mages.app;

import java.io.*;

public interface IControlable{  
  public void init();
  public Object getObject() throws IOException;
  public void setObject(Object obj);
  public void setEditor(IEditor e);
  public void update() throws IOException;
  public boolean validateObject() throws FormatException;
}

