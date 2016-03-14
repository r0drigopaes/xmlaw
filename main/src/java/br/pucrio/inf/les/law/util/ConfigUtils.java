package br.pucrio.inf.les.law.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


public class ConfigUtils 
{
    /**
     * Logger for this class
     */
    private static final Log LOG = LogFactory.getLog(ConfigUtils.class);

	private static final String DEFAULT_CONFIG_DIR = System.getProperty("user.dir")+System.getProperty("file.separator")+"conf"+System.getProperty("file.separator");
	
	private static final String DEFAULT_TEMP_DIR = System.getProperty("user.dir")+System.getProperty("file.separator")+"temp"+System.getProperty("file.separator");
	
	/**
	 * Obtem o diretorio utilizado para arquivos de configuração
	 * @return Retorna o valor da propriedade law.conf.dir caso esteja configurada, senao retorna o valor 
	 * default da constante DEFAULT_CONFIG_DIR
	 */
	public static String getConfigDir()
	{
		String configDir = System.getProperty("law.conf.dir");
		if(configDir == null)
		{
			return DEFAULT_CONFIG_DIR;
		}
		
		//retorna o diretório com barra no final
		if(!configDir.endsWith("/") || !configDir.endsWith("\\"))
		{
			return configDir + System.getProperty("file.separator");
		}
		
		return configDir;
	}
	
	public static String getTempDir()
	{
		File tempDir = new File(DEFAULT_TEMP_DIR);
		if(! tempDir.exists())
		{
			if(! tempDir.mkdirs())
			{
				LOG.warn("Unable to create temp dir: " + DEFAULT_TEMP_DIR);				
			}
		}
		return DEFAULT_TEMP_DIR;
	}
	
	public static Properties loadProperties(String path)
	{
		Properties properties = new Properties();
		try
		{
			properties.load(new FileInputStream(path));
		}
		catch(FileNotFoundException fnfe)
		{
			LOG.error("Arquivo de configuração " + path + " nao encontrado",fnfe);
		}
		catch(IOException ioe)
		{
			LOG.error("Erro abrindo arquivo "+ path +" de configuração",ioe);
		}
		return properties;
	}

}
