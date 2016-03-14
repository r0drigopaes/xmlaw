
package mages.app;

import java.io.*;

public interface IValidator{
  public boolean validate(Object src, String[] args) throws IOException;  
  public String getErrorMessage();
}