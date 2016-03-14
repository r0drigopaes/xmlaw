/* ****************************************************************
 *   @filename:		CommunicationAccess.java
 *   @projectname:  Law
 *   @date:			Sep 14, 2004
 *   @author 		Rodrigo - LES (PUC-Rio)
 * 	 
 *   $Id: CommunicationProvider.java,v 1.9 2006/05/30 21:46:53 lfrodrigues Exp $
 * ***************************************************************/
package br.pucrio.inf.les.law.communication;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.pucrio.inf.les.law.model.LawException;
import br.pucrio.inf.les.law.util.Constants;

/**
 * @author Rodrigo - LES (PUC-Rio)
 * @version $Revision: 1.9 $
 */
public class CommunicationProvider
{

	/**
	 * Logger for this class
	 */
	private static final Log					LOG					= LogFactory.getLog(CommunicationProvider.class);

	private static ICommunication				mediatorCommunication;

	private static Map<String, ICommunication>	agentCommunications	= new HashMap<String, ICommunication>();
	
	public static Map<String, ICommunication> getAllAgentComunications()
	{
		return agentCommunications;
	}

	public static ICommunication agentCommunication(String agent) throws LawException
	{
		ICommunication communication = agentCommunications.get(agent);
		if (communication == null)
		{
			String className = Constants.AGENT_COMMUNICATION_CLASS.getValue();
			communication = createCommunication(className, agent);

			agentCommunications.put(agent, communication);
		}
		return communication;
	}
	
	public static void destroyAgentCommunication(String agent)
	{
		ICommunication agentCommunication = agentCommunications.get(agent);
		if(agentCommunication != null)
		{
			agentCommunication.close();
			agentCommunications.remove(agent);
		}
		else
		{
			LOG.warn("No agent comunication [" + agent + "] to be destroyed");
		}
	}

	public static ICommunication mediatorCommunication() throws LawException
	{
		if (mediatorCommunication == null)
		{
			String name = Constants.MEDIATOR_NAME.getValue();
			String className = Constants.MEDIATOR_COMMUNICATION_CLASS.getValue();

			mediatorCommunication = createCommunication(className, name);
		}

		return mediatorCommunication;
	}
	
	public static void destroyMediatorCommunication()
	{
		mediatorCommunication.close();
		mediatorCommunication = null;
	}

	private static ICommunication createCommunication(String clazz, String name) throws LawException
	{
		ICommunication communication = null;
		try
		{

			/* instancia camada de comunicação */
			Class communicationClass = Class.forName(clazz);

			try
			{
				// tenta primeiro o construtor default
				communication = (ICommunication) communicationClass.newInstance();
			}
			catch (Exception e)
			{
				// tenta o construtor com um parametro.
				communication = (ICommunication) communicationClass.getConstructor(new Class[] { String.class })
						.newInstance(new Object[] { name });
			}
		}
		catch (ClassNotFoundException e)
		{
			LOG
					.error("Communication class: [" + clazz
							+ "] was not found. Check your classpath variable or verify spelling mistakes in the Config.properties file.");
			throw new LawException(
					"Communication class: [" + clazz
							+ "] was not found. Check your classpath variable or verify spelling mistakes in the Config.properties file.",
					LawException.CLASS_NOT_FOUND);
		}
		catch (NoSuchMethodException e)
		{
			LOG.error("Communication class: [" + clazz
					+ "] does not have neither a default constructor nor a constructor with a String parameter.", e);
			throw new LawException("Communication class: [" + clazz
					+ "] does not have neither a default constructor nor a constructor with a String parameter.",
					LawException.CONSTRUCTOR_INVOCATION);
		}
		catch (InvocationTargetException e)
		{
			LOG.error("Communication class: [" + clazz
					+ "] does not have neither a default constructor nor a constructor with a String parameter.", e);
			throw new LawException("Communication class: [" + clazz
					+ "] does not have neither a default constructor nor a constructor with a String parameter.",
					LawException.CONSTRUCTOR_INVOCATION);
		}
		catch (IllegalAccessException e)
		{
			LOG.error("Communication class: [" + clazz
					+ "] does not have neither a default constructor nor a constructor with a String parameter.");
			throw new LawException("Communication class: [" + clazz
					+ "] does not have neither a default constructor nor a constructor with a String parameter.",
					LawException.CONSTRUCTOR_INVOCATION);
		}
		catch (InstantiationException e)
		{
			LOG.error("Communication class: [" + clazz
					+ "] does not have neither a default constructor nor a constructor with a String parameter.");
			throw new LawException("Communication class: [" + clazz
					+ "] does not have neither a default constructor nor a constructor with a String parameter.",
					LawException.CONSTRUCTOR_INVOCATION);
		}

		return communication;

	}
}

/*******************************************************************************
 * UPDATE LOG:
 * 
 * $Log: CommunicationProvider.java,v $
 * Revision 1.9  2006/05/30 21:46:53  lfrodrigues
 * *** empty log message ***
 *
 * Revision 1.8  2006/02/20 17:34:59  lfrodrigues
 * inclusao de metodo para destruir conexao de agente
 *
 * Revision 1.7  2006/02/17 12:49:58  lfrodrigues
 * inclusao de metodo para finalizar communicacao de mediador
 *
 * Revision 1.6  2006/02/17 11:32:01  lfrodrigues
 * instanciacao de communication para agente e mediador
 * Revision 1.5 2006/02/02 15:36:35 rbp ***
 * empty log message ***
 * 
 * Revision 1.4 2006/02/01 20:18:45 lfrodrigues mudificações no pacote model
 * para melhor modularização do projeto em api cliente e servidor
 * 
 * Revision 1.3 2006/01/31 17:36:12 lfrodrigues classes Config e ConfigUtils
 * movidas de pacote model para pacote util
 * 
 * Revision 1.2 2006/01/26 15:47:38 rbp *** empty log message ***
 * 
 * Revision 1.1 2006/01/11 18:21:03 lfrodrigues implementacao do provedor da
 * camada de comunicacao
 * 
 * Revision 1.1 2005/08/17 17:48:14 lfrodrigues instalacao do maven como
 * ferramenta de build automatico
 * 
 * Revision 1.1 2005/05/10 16:23:01 guga Inicio Refactoring
 * 
 * Revision 1.3 2004/11/24 01:06:19 rbp Main modifications: - Allow multiple
 * instances of the same scene - Introducing a 3 layer mapping from XML: XML -->
 * Descriptor Layer --> Model Layer - Framework of actions introduced; now it is
 * possible use java code as consequence of some law configuration. -
 * Implementation of a case study in the context of product trading using SWT
 * and JFace ( it requires to configure the eclipse for running the application) -
 * Some bugs were fixed altough I don't remember of them.
 * 
 * Revision 1.1 2004/09/22 01:04:38 rbp This version contains severals
 * modifications: - Package renaming - Specification of laws from XML language -
 * Added a Sax based Xml parser for reading from XML - Some hard-coded code were
 * removed and placed in the Config.properties file - Added the simple
 * application example
 * 
 ******************************************************************************/
