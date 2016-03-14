
/**************************************************************************
 * An interface for producing an input stream on demand
 * A null means that the resource is not (or no longer) available.
 * So, a one-shot producer may provide a value the first time but not
 * after that.
 */

package jsim.beanbox;

import java.io.*;


public interface InputStreamProducer
{
    InputStream getInputStream ();

}; // interface

