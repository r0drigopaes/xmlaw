/* ****************************************************************
 *   @filename:		AgentIdentification.java
 *   @projectname:  Law
 *   @date:			Jun 14, 2004
 *   @author 		rbp
 * 	 
 *   $Id: AgentIdentification.java,v 1.4 2006/07/04 21:10:33 mgatti Exp $
 * ***************************************************************/

package br.pucrio.inf.les.law.communication;

import java.util.HashSet;
import java.util.Set;

/**
 * Classe que fornece uma identificação para um agente.
 * @alias AgentIdentification
 * @author rbp & guga
 * @version $Revision: 1.4 $
 */
public class AgentIdentification {
	
	/**
	 * Agent's name
	 */
	private String name;
	
	/**
	 * The Set containing the agent's addresses
	 */
	private Set<String> address;
	
	private int port;
	
	/**
	 * Instancia um objeto que serve para identificar os agentes.
	 */
	public AgentIdentification(String name)
	{
		this.name = name;
		this.address 			= new HashSet<String>();
	} 

	/** 
	 * Adiciona um endereço ao agente. Um agente pode ter vários endereços.
	 * Por exemplo: ele pode se comunicar via email ou por http.  
	 *  
	 */
	public void addAddresses(String address) {
		this.address.add(address);
	}
	
	/**
	 * Remove todos os endereços previamente cadastrados.
	 */
	public void clearAllAddresses() {
		this.address.clear();
	}

	/** 
	 * Retorna um iterator para todos os endereços (String) do agente.
	 */
	public Set<String> getAllAddresses(){
		return this.address;
	}	

	/** 
	 * Retorna o nome do agente.
	 */
	public String getName() {		
		return this.name;
	}
	
	/** 
	 * Define o nome do agente.
	 */
	public void setName(String name) {	
		this.name = name;		
	}

	/** 
	 * Remove um endereço específico do agente.
	 */
	public boolean removeAddresses(String address) {		
		return this.address.remove(address);
	}
	
	@Override
	public int hashCode() 
	{	
		return getName().hashCode();
	}
	
	@Override
	public boolean equals(Object obj) 
	{
		if(!(obj instanceof AgentIdentification))
		{
			return false;
		}
		
		AgentIdentification agentIdentification = (AgentIdentification)obj;
		
		if(!agentIdentification.getName().equals(getName()))
		{
			return false;
		}
		
		if(agentIdentification.getAllAddresses().size() != address.size())
		{
			return false;
		}
			
		for(String agentAddress : agentIdentification.getAllAddresses())
		{
			if(! address.contains(agentAddress))
			{
				return false;
			}
		}
		
		return true;
	}	
    
    public String toString(){
        return name;
    }

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}
}


/* ****************************************************************
 * LOG DE ALTERAÇÕES:
 * 
 * $Log: AgentIdentification.java,v $
 * Revision 1.4  2006/07/04 21:10:33  mgatti
 * 04/07/2006: XMLaw - versão utilizada no projeto final de programação da Maíra.
 *
 * Revision 1.3  2006/04/25 16:36:22  mgatti
 * 26/04/2006: Versão com alterações da implementação do broadcast.
 *
 * Revision 1.2  2006/02/22 20:49:30  mgatti
 * Maíra: 1ª Versão da Integração DimaX & XMLaw
 *
 * Revision 1.1  2006/02/01 20:18:45  lfrodrigues
 * mudificações no pacote model para melhor modularização do projeto em api cliente e servidor
 *
 * Revision 1.8  2006/01/18 18:08:53  lfrodrigues
 * adaptacao da remocao de RoleDescriptor de dentro de AgentId para OrganizationExecution
 *
 * Revision 1.7  2006/01/17 17:28:08  lfrodrigues
 * hashcode sobrescrito
 *
 * Revision 1.6  2006/01/16 18:35:49  lfrodrigues
 * metodo para retornar todos os papeis exercidos pelo agente
 *
 * Revision 1.5  2006/01/16 18:10:57  rbp
 * Implementado o shouldCreate no IDescriptor.
 * Removido os casos de Teste do Jade para que possamos executar a partir do eclipse.
 *
 * Revision 1.4  2006/01/16 15:58:33  lfrodrigues
 * inclusao de papel exercido pelo agente
 *
 * Revision 1.3  2006/01/13 15:51:28  lfrodrigues
 * correcao no metodo equals, tamanho da lista de enderecos e comparada
 *
 * Revision 1.2  2006/01/13 15:15:12  lfrodrigues
 * agent Identification implementa equals
 *
 * Revision 1.1  2005/12/05 18:29:48  rbp
 * Versão inicial
 *
 * Revision 1.1  2005/08/17 17:48:14  lfrodrigues
 * instalacao do maven como ferramenta de build automatico
 *
 * Revision 1.1  2005/05/10 16:22:57  guga
 * Inicio Refactoring
 *
 * Revision 1.3  2004/11/24 01:06:04  rbp
 * Main modifications:
 * - Allow multiple instances of the same scene
 * - Introducing a 3 layer mapping from XML: XML --> Descriptor Layer --> Model Layer
 * - Framework of actions introduced; now it is possible use java code as consequence of some law configuration.
 * - Implementation of a case study in the context of product trading using SWT and JFace ( it requires to configure the eclipse for running the application)
 * - Some bugs were fixed altough I don't remember of them.
 *
 * Revision 1.1  2004/09/22 01:04:37  rbp
 * This version contains severals modifications:
 * - Package renaming
 * - Specification of laws from XML language
 * - Added a Sax based Xml parser for reading from XML
 * - Some hard-coded code were removed and placed in the Config.properties file
 * - Added the simple application example
 *
 * Revision 1.4  2004/07/14 21:08:16  rbp
 * Inserindo alterações para permitir multiplas organizações, cenários, não-determinismo etc.
 *
 * Revision 1.2  2004/06/28 01:23:09  rbp
 * Primeira versão da interface gráfica de monitoramento do protocolo.
 *
 * Revision 1.1  2004/06/24 22:42:52  rbp
 * Após primeira reengenharia de pacotes e antes de gerar modelos
 *
 * Revision 1.1  2004/06/16 01:58:25  rbp
 * Primeiros arquivos a entrar no CVS
 *
 * ***************************************************************/