
/**************************************************************************
 * Filter class.
 */

package jsim.beanbox;

import java.io.*;


public class FileExtension implements FilenameFilter
{
    private String extension;


    public FileExtension (String ext)
    {
	this.extension = ext;

    }; // FileExtension
    

    public boolean accept (File dir, String name)
    {
	return name.endsWith(extension);

    }; // accept


}; // class

