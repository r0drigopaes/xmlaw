/* ****************************************************************
 *   @filename:		Config.java
 *   @projectname:  Law
 *   @date:			Sep 21, 2004
 *   @author 		Rodrigo - LES (PUC-Rio)
 * 	 
 *   $Id: Config.java,v 1.1 2006/01/31 17:36:12 lfrodrigues Exp $
 * ***************************************************************/
package br.pucrio.inf.les.law.util;

import java.util.Properties;


/**
 * @author 	 Rodrigo  - LES (PUC-Rio)
 * @version  $Revision: 1.1 $
 */
public class Config {

	private Properties properties;
	
	private static Config singletonInstance = null;
    
    private String fileName = null; 

	private Config() 
	{
        this.fileName = ConfigUtils.getConfigDir()+"Config.properties";
		properties = ConfigUtils.loadProperties(this.fileName);
	}

	public static Config getInstance() {
		if (singletonInstance == null) {
			singletonInstance = new Config();
		}
		return singletonInstance;
	}
	
	public String get(String key)
	{
		return properties.getProperty(key);
	}
    
    public String getFileName(){
        return this.fileName;
    }
}


/* ****************************************************************
 * UPDATE LOG:
 * 
 * $Log: Config.java,v $
 * Revision 1.1  2006/01/31 17:36:12  lfrodrigues
 * classes Config e ConfigUtils movidas de pacote model  para pacote util
 *
 * Revision 1.2  2006/01/26 15:47:38  rbp
 * *** empty log message ***
 *
 * Revision 1.1  2005/12/05 18:29:48  rbp
 * Versão inicial
 *
 * Revision 1.2  2005/08/19 21:25:03  lfrodrigues
 * funcionalidade para escolha do diretorio de configuracao pelo cliente. O modulo de prolog ainda nao foi adaptado.
 *
 * Revision 1.1  2005/08/17 17:48:14  lfrodrigues
 * instalacao do maven como ferramenta de build automatico
 *
 * Revision 1.1  2005/05/10 16:23:04  guga
 * Inicio Refactoring
 *
 * Revision 1.3  2004/11/24 01:06:19  rbp
 * Main modifications:
 * - Allow multiple instances of the same scene
 * - Introducing a 3 layer mapping from XML: XML --> Descriptor Layer --> Model Layer
 * - Framework of actions introduced; now it is possible use java code as consequence of some law configuration.
 * - Implementation of a case study in the context of product trading using SWT and JFace ( it requires to configure the eclipse for running the application)
 * - Some bugs were fixed altough I don't remember of them.
 *
 * Revision 1.1  2004/09/22 01:04:38  rbp
 * This version contains severals modifications:
 * - Package renaming
 * - Specification of laws from XML language
 * - Added a Sax based Xml parser for reading from XML
 * - Some hard-coded code were removed and placed in the Config.properties file
 * - Added the simple application example
 *
 * ***************************************************************/