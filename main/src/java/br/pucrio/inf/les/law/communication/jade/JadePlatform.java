/*******************************************************************************
 * @filename: JadePlataform.java @projectname: Law @date: Jun 14, 2004 @author
 * rbp $Id: JadePlatform.java,v 1.8 2006/05/30 21:46:53 lfrodrigues Exp $
 ******************************************************************************/
package br.pucrio.inf.les.law.communication.jade;

import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.tools.rma.rma;
import jade.wrapper.AgentContainer;
import jade.wrapper.AgentController;

import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.pucrio.inf.les.law.util.ConfigUtils;

/**
 * <p>
 * O ambiente de execução do Jade é baseado em 2 conceitos principais:
 * Plataforma e containers. Em uma plataforma só pode existir um único
 * main-container, porém podem existir outros containers 'normais'. Esta classe
 * cuida para que exista um único main-container.
 * </p>
 * <p>
 * As configurações do JADE, bem como a sua documentação se encontram no arquivo
 * JadePlataform.properties localizado no diretório retornado por ConfigUtils
 * </p>
 * 
 * @author Rodrigo Paes
 * @version $Revision: 1.8 $
 */
public class JadePlatform
{
	/**
	 * Logger for this class
	 */
	private static final Log	LOG	= LogFactory.getLog(JadePlatform.class);

	public static AgentContainer createContainer()
	{
		LOG.info("Creating Jade Container");

		Properties properties = ConfigUtils.loadProperties(ConfigUtils.getConfigDir() + "JadePlatform.properties");
		Profile profile = new ProfileImpl();

		profile.setParameter(Profile.FILE_DIR, ConfigUtils.getTempDir());

		// retrieves the main container option
		boolean isMainContainer = Boolean.valueOf(properties.getProperty(Profile.MAIN)).booleanValue();
		

		// HOST where the container will run. If its the main container this
		// name must indicate the local host. Otherwise, if its a secondary
		// container, it must indicate the host of the main container
		String host = properties.getProperty(Profile.MAIN_HOST);
		profile.setParameter(Profile.MAIN_HOST, host);

		// connection port for the container
		int port = Integer.parseInt(properties.getProperty(Profile.MAIN_PORT));
		profile.setParameter(Profile.MAIN_PORT, "" + port);

		// platform identifier
		String plataformId = properties.getProperty(Profile.PLATFORM_ID);
		profile.setParameter(Profile.PLATFORM_ID, plataformId);
	
		profile.setParameter(Profile.LOCAL_PORT, ""+port);

		if (!isMainContainer)
		{
			return Runtime.instance().createAgentContainer(profile);
		}
		
		AgentContainer mainContainer = Runtime.instance().createMainContainer(profile);
		rma rmaAgent = new rma();

		try
		{
			AgentController rmaAgentControler = mainContainer.acceptNewAgent("rma", rmaAgent);
			rmaAgentControler.start();
		}
		catch (Exception spe)
		{
			LOG.error("Error starting RMA Agent", spe);
		}

		return mainContainer;
	}
}

/*******************************************************************************
 * LOG DE ALTERAÇÕES: $Log: JadePlatform.java,v $
 * LOG DE ALTERAÇÕES: Revision 1.8  2006/05/30 21:46:53  lfrodrigues
 * LOG DE ALTERAÇÕES: *** empty log message ***
 * LOG DE ALTERAÇÕES: LOG DE ALTERAÇÕES: Revision
 * 1.7 2006/03/08 18:06:56 lfrodrigues LOG DE ALTERAÇÕES: configuração jade para
 * direcionar arquivos temporarios do cliente para diretorio temp LOG DE
 * ALTERAÇÕES: LOG DE ALTERAÇÕES: Revision 1.6 2006/02/17 12:51:36 lfrodrigues
 * LOG DE ALTERAÇÕES: classe transformada em criador de Communications, nao
 * existe mais o armazenamento de containers LOG DE ALTERAÇÕES: LOG DE
 * ALTERAÇÕES: Revision 1.5 2006/02/07 10:19:02 lfrodrigues LOG DE ALTERAÇÕES:
 * configuração da plataforma para utilizacao de diretorio de arquivos
 * temporarios LOG DE ALTERAÇÕES: LOG DE ALTERAÇÕES: Revision 1.4 2006/02/06
 * 13:18:08 lfrodrigues LOG DE ALTERAÇÕES: organize imports LOG DE ALTERAÇÕES:
 * LOG DE ALTERAÇÕES: Revision 1.3 2006/01/31 20:39:44 lfrodrigues LOG DE
 * ALTERAÇÕES: remocao de log do metodo getInstance LOG DE ALTERAÇÕES: LOG DE
 * ALTERAÇÕES: Revision 1.2 2006/01/31 17:36:12 lfrodrigues LOG DE ALTERAÇÕES:
 * classes Config e ConfigUtils movidas de pacote model para pacote util LOG DE
 * ALTERAÇÕES: LOG DE ALTERAÇÕES: Revision 1.1 2006/01/09 20:06:34 mgatti LOG DE
 * ALTERAÇÕES: Maíra: Inclusão dos novos elementos gerados pelo Rose, e
 * modificação de pacotes. LOG DE ALTERAÇÕES: obs: os que foram alterados no
 * Rose (ITrigger, por exemplo), não estão sendo incluídos agora. LOG DE
 * ALTERAÇÕES: LOG DE ALTERAÇÕES: Revision 1.2 2005/12/29 16:35:30 rbp LOG DE
 * ALTERAÇÕES: Versão da primeira pausa do pair-programming. Paramos depois da
 * conversa com o guga. LOG DE ALTERAÇÕES: - Incluido o arquivo Brainstorm de
 * planejamento, que contem os principios e passos de planejamento. LOG DE
 * ALTERAÇÕES: - Incluido uma primeira versao da proposta do xml. LOG DE
 * ALTERAÇÕES: LOG DE ALTERAÇÕES: Revision 1.1 2005/12/05 18:29:48 rbp LOG DE
 * ALTERAÇÕES: Versão inicial LOG DE ALTERAÇÕES: Revision 1.3 2005/11/10
 * 12:04:44 lfrodrigues functionality of temp dir for jade files added Revision
 * 1.2 2005/08/19 21:25:03 lfrodrigues funcionalidade para escolha do diretorio
 * de configuracao pelo cliente. O modulo de prolog ainda nao foi adaptado.
 * Revision 1.1 2005/08/17 17:48:14 lfrodrigues instalacao do maven como
 * ferramenta de build automatico Revision 1.1 2005/05/10 16:22:56 guga Inicio
 * Refactoring Revision 1.3 2004/11/24 01:06:04 rbp Main modifications: - Allow
 * multiple instances of the same scene - Introducing a 3 layer mapping from
 * XML: XML --> Descriptor Layer --> Model Layer - Framework of actions
 * introduced; now it is possible use java code as consequence of some law
 * configuration. - Implementation of a case study in the context of product
 * trading using SWT and JFace ( it requires to configure the eclipse for
 * running the application) - Some bugs were fixed altough I don't remember of
 * them. Revision 1.1 2004/09/22 01:04:38 rbp This version contains severals
 * modifications: - Package renaming - Specification of laws from XML language -
 * Added a Sax based Xml parser for reading from XML - Some hard-coded code were
 * removed and placed in the Config.properties file - Added the simple
 * application example Revision 1.2 2004/06/28 01:23:08 rbp Primeira versão da
 * interface gráfica de monitoramento do protocolo. Revision 1.1 2004/06/24
 * 22:42:51 rbp Após primeira reengenharia de pacotes e antes de gerar modelos
 * Revision 1.3 2004/06/22 21:32:52 rbp Adicionado o framework de testes de
 * unidade que funciona baseado no Junit e também uma primeira versão do pattern
 * matching utilizando Prolog Revision 1.2 2004/06/21 23:07:32 rbp Modificando a
 * JadePlataform para funcionar em computadores diferentes. Um arquivo de
 * properties foi criado. Revision 1.1 2004/06/16 01:58:25 rbp Primeiros
 * arquivos a entrar no CVS
 ******************************************************************************/
