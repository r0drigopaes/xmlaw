

package mages.app;

import java.io.*;

public interface IController{  
  public void init();
  public void save(Object obj, String name) throws IOException;
  public Object load(String name) throws IOException;
}

