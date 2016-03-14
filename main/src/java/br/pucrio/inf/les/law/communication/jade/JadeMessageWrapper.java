/* ****************************************************************
 *   @filename:		JadeMessageWrapper.java
 *   @projectname:  Law
 *   @date:			Jun 14, 2004
 *   @author 		rbp
 * 	 
 *   $Id: JadeMessageWrapper.java,v 1.16 2006/05/06 19:47:11 lfrodrigues Exp $
 * ***************************************************************/
package br.pucrio.inf.les.law.communication.jade;

import jade.core.AID;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.UnreadableException;
import jade.util.leap.Properties;

import java.io.IOException;
import java.io.Serializable;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

import br.pucrio.inf.les.law.communication.AgentIdentification;
import br.pucrio.inf.les.law.communication.Message;

/**
 * Essa classe é responsável por fazer as conversões entre as mensagens e
 * identificadores JADE para mensagens e identificadores utilizados no framework
 * de LAW.
 * 
 * @author Rodrigo Paes
 * @version $Revision: 1.16 $
 */
public class JadeMessageWrapper {
	
    private static JadeMessageWrapper singletonInstance = null;
    
    protected static final String ENTRY_SEPARATOR = "" + (char)156 + (char)156 + (char)156;
    protected static final String KEY_VALUE_SEPARATOR = "" + (char)157 + (char)157 + (char)157;
    protected static final String ENTRY_ROLE_SEPARATOR = "" + (char)158 + (char)158 + (char)158;

    public static JadeMessageWrapper getInstance() {
        if (singletonInstance == null) {
            singletonInstance = new JadeMessageWrapper();
        }
        return singletonInstance;
    }

    /**
     * Transforma uma mensagem do tipo Message em outra mensagem do tipo
     * ACLMessage.
     * 
     * @param msg
     *            A mensagem a ser transformada
     * @return Uma mensagem do tipo ACLMessage
     * @pre (msg!=null) && (msg.getPerformative() !=null) && (msg.getContent()) &&
     *      (msg.getConversationId()!=null) && msg.getSender()
     * @author rbp
     */
    public ACLMessage transform(Message msg) {
        // Cria a mensagem e define a performativa
        ACLMessage aclMessage = new ACLMessage(ACLMessage.getInteger(msg.getPerformative()));

        for(Map.Entry entry : msg.getAllUserDefined().entrySet())
        {
        	aclMessage.addUserDefinedParameter((String)entry.getKey(),(String)entry.getValue());
        }
        // Define o(s) destinatário(s) da mensagem
        for(AgentIdentification receiverAid : msg.getAllReceivers().keySet())
        {
        	aclMessage.addReceiver(this.transform(receiverAid));
        }
        
        String senderRole = msg.getSenderRole();
        if(msg.getSenderRole() == null)
        {
        	senderRole = "";        	
        }
        aclMessage.addUserDefinedParameter(JadeCommunication.SENDER_ROLE, senderRole);

        // The XMLmessage content is a Map<String,String> which will be transformed to a serialized String, 
        // the entries will be identified with the ENTRY_SEPARATOR String while the keys and values be the 
        // KEY_VALEU_SEPARATOR String
        StringBuffer aclMessageContent = new StringBuffer();
        for(Map.Entry entry : msg.getContent().entrySet())
        {
        	aclMessageContent.append(ENTRY_SEPARATOR);
        	aclMessageContent.append(entry.getKey());
        	aclMessageContent.append(KEY_VALUE_SEPARATOR);
        	aclMessageContent.append(entry.getValue());
        }
        for(Map.Entry entry : msg.getAllReceivers().entrySet())
        {
        	aclMessageContent.append(ENTRY_SEPARATOR);
        	aclMessageContent.append(ENTRY_ROLE_SEPARATOR);
        	aclMessageContent.append(entry.getKey().toString());
        	aclMessageContent.append(KEY_VALUE_SEPARATOR);
        	aclMessageContent.append(entry.getValue());
        }
        
        aclMessage.setContent(aclMessageContent.toString());
        aclMessage.setConversationId(msg.getConversationId());
        aclMessage.setSender(this.transform(msg.getSender()));
        aclMessage.setProtocol(msg.getProtocol());

        return aclMessage;
    }

    /**
     * Transforma uma mensagem ACLMessage de Jade para uma mensagem Message do
     * framework de LAWs.
     * 
     * @param aclMessage
     *            A mensagem a ser transformada
     * @return Uma mensagem do tipo Message
     * @pre (aclMessage.getContent()!=null) && (aclMessage.getConversationId()
     *      !=null) && (aclMessage.getSender() !=null)
     * @author rbp
     */
	public Message transform(ACLMessage aclMessage) {
        // Cria uma mensagem e defina a performativa
        Message msg = new Message(ACLMessage.getPerformative(aclMessage
                .getPerformative()));
        //Recuperando o objeto que relaciona os destinatários com os seus papéis
        Map<String, String> receiversObj = new HashMap<String, String>();

        // Transforms the aclMessageContent String into a Message content Map
        StringTokenizer messageContentStr = new StringTokenizer(aclMessage.getContent(),ENTRY_SEPARATOR);
        String keyToken;
        String valueToken;
        while(messageContentStr.hasMoreTokens())
        {
        	StringTokenizer entryStr = new StringTokenizer(messageContentStr.nextToken(),KEY_VALUE_SEPARATOR);
        	keyToken = entryStr.nextToken();
        	if (keyToken.indexOf(ENTRY_ROLE_SEPARATOR)>=0){
        		//é uma chave para o papel
        		if (entryStr.hasMoreTokens()){
        			valueToken = entryStr.nextToken();
        			receiversObj.put(keyToken.replaceFirst(ENTRY_ROLE_SEPARATOR,""),valueToken);
        		}else{
        			receiversObj.put(keyToken.replaceFirst(ENTRY_ROLE_SEPARATOR,""),"");
        		}
        	}else{
        		if (entryStr.hasMoreTokens()){
        			valueToken = entryStr.nextToken();
        		}else{
        			valueToken = "";
        		}
        		msg.setContentValue(keyToken,valueToken);
        		
        	}
        }
        
        for(Map.Entry entry : receiversObj.entrySet())
        {
        	msg.addReceiver(new AgentIdentification(entry.getKey().toString()),entry.getValue().toString());
        }
        
        msg.setProtocol(aclMessage.getProtocol());
        
        // Define o conversationId
        msg.setConversationId(aclMessage.getConversationId());
        // Define o remetente da mensagem
        msg.setSender(this.transform(aclMessage.getSender()), aclMessage
                .getUserDefinedParameter(JadeCommunication.SENDER_ROLE));

        Properties properties = aclMessage.getAllUserDefinedParameters();
        Enumeration keysEnum = properties.keys();

        while (keysEnum.hasMoreElements()) {
            String key = (String) keysEnum.nextElement();
            if(key.equals(JadeCommunication.SENDER_ROLE) || key.equals(JadeCommunication.RECEIVER_ROLE))
            {
            	continue;
            }
            String value = (String) properties.get(key);
            msg.addUserDefined(key, value);
        }

        return msg;
    }

    /**
     * Converte um objeto do tipo AgentIdentification em outro do tipo AID.
     * 
     * @param aid
     * @return
     */
    public AID transform(AgentIdentification aid) {

        // Define o Nome
        AID jadeAid = new AID(aid.getName(), AID.ISLOCALNAME);

        // Define os endereços
        Iterator iter = aid.getAllAddresses().iterator();
        while (iter.hasNext()) {
            String address = (String) iter.next();
            jadeAid.addAddresses(address);
        }
        return jadeAid;
    }

    /**
     * Converte um objeto do tipo AID em outro do tipo AgentIdentification.
     * 
     * @param aid
     * @return
     */
    public AgentIdentification transform(AID jadeAid) {
        // removes the text after @
        AgentIdentification aid = new AgentIdentification(jadeAid.getLocalName());
        // Define os endereços
        Iterator iter = jadeAid.getAllAddresses();
        while (iter.hasNext()) {
            String address = (String) iter.next();
            aid.addAddresses(address);
        }
        
        return aid;
        
    }

}

/*******************************************************************************
 * LOG DE ALTERAÇÕES: $Log: JadeMessageWrapper.java,v $
 * LOG DE ALTERAÇÕES: Revision 1.16  2006/05/06 19:47:11  lfrodrigues
 * LOG DE ALTERAÇÕES: adaptacao do impacto da transformacao de mensagem de um para varios receptores
 * LOG DE ALTERAÇÕES:
 * LOG DE ALTERAÇÕES: Revision 1.15  2006/05/03 22:39:02  mgatti
 * LOG DE ALTERAÇÕES: 03/05/2006: Implementação de alguns TODOs:
 * LOG DE ALTERAÇÕES: Enforcement na mensagem, entre outros.
 * LOG DE ALTERAÇÕES:
 * LOG DE ALTERAÇÕES: Revision 1.14  2006/05/02 23:33:55  mgatti
 * LOG DE ALTERAÇÕES: 02/05/2006:  XMLaw - Actions e Constraints
 * LOG DE ALTERAÇÕES: Está dando quando as duas cenas de uma mesma organização estão especificadas (o "saco" dos states e transitions está pegando tudo)
 * LOG DE ALTERAÇÕES:
 * LOG DE ALTERAÇÕES: Revision 1.13  2006/04/25 16:36:23  mgatti
 * LOG DE ALTERAÇÕES: 26/04/2006: Versão com alterações da implementação do broadcast.
 * LOG DE ALTERAÇÕES:
 * LOG DE ALTERAÇÕES: Revision 1.12  2006/02/20 17:34:06  lfrodrigues
 * LOG DE ALTERAÇÕES: uso de nome local ao transformar mensagem jade para mensagem xmlaw
 * LOG DE ALTERAÇÕES:
 * LOG DE ALTERAÇÕES: Revision 1.11  2006/02/16 18:48:32  lfrodrigues
 * LOG DE ALTERAÇÕES: inclusao de verificacao de papel de rementente e destinatorio para que nao sejam incluidos no Map de userDefined
 * LOG DE ALTERAÇÕES:
 * LOG DE ALTERAÇÕES: Revision 1.10  2006/02/01 20:18:45  lfrodrigues
 * LOG DE ALTERAÇÕES: mudificações no pacote model para melhor modularização do projeto em api cliente e servidor
 * LOG DE ALTERAÇÕES:
 * LOG DE ALTERAÇÕES: Revision 1.9  2006/01/31 20:39:13  lfrodrigues
 * LOG DE ALTERAÇÕES: implementacao de protecao contra valores nulos no valor do papel do sender para o metodo transform(Message)
 * LOG DE ALTERAÇÕES:
 * LOG DE ALTERAÇÕES: Revision 1.8  2006/01/31 17:33:56  lfrodrigues
 * LOG DE ALTERAÇÕES: classe Message movida de pacote model para pacote communication
 * LOG DE ALTERAÇÕES:
 * LOG DE ALTERAÇÕES: Revision 1.7  2006/01/24 19:46:32  lfrodrigues
 * LOG DE ALTERAÇÕES: correcao em transformacao de aclmessage para message
 * LOG DE ALTERAÇÕES: remocao de map que guardava identificacao de agentes
 * LOG DE ALTERAÇÕES:
 * LOG DE ALTERAÇÕES: Revision 1.6  2006/01/18 16:36:14  mgatti
 * LOG DE ALTERAÇÕES: Maíra: clienteapi
 * LOG DE ALTERAÇÕES:
 * LOG DE ALTERAÇÕES: Revision 1.5  2006/01/18 15:41:21  lfrodrigues
 * LOG DE ALTERAÇÕES: adaptacao da remocao dos atributos de execucao de cena e organizacao em mensagem
 * LOG DE ALTERAÇÕES:
 * LOG DE ALTERAÇÕES: Revision 1.4  2006/01/17 17:21:36  lfrodrigues
 * LOG DE ALTERAÇÕES: implementacao de recuperacao de identificacao de agente
 * LOG DE ALTERAÇÕES:
 * LOG DE ALTERAÇÕES: Revision 1.3  2006/01/13 15:15:12  lfrodrigues
 * LOG DE ALTERAÇÕES: agent Identification implementa equals
 * LOG DE ALTERAÇÕES:
 * LOG DE ALTERAÇÕES: Revision 1.2  2006/01/11 18:23:07  lfrodrigues
 * LOG DE ALTERAÇÕES: adaptacao para transformar Map de conteudo da messagem XMLaw em String de conteudo para aclMessage e vice-versar
 * LOG DE ALTERAÇÕES:
 * LOG DE ALTERAÇÕES: Revision 1.1  2006/01/09 20:06:34  mgatti
 * LOG DE ALTERAÇÕES: Maíra: Inclusão dos novos elementos gerados pelo Rose, e modificação de pacotes.
 * LOG DE ALTERAÇÕES: obs: os que foram alterados no Rose (ITrigger, por exemplo), não estão sendo incluídos agora.
 * LOG DE ALTERAÇÕES:
 * LOG DE ALTERAÇÕES: Revision 1.1  2005/12/05 18:29:48  rbp
 * LOG DE ALTERAÇÕES: Versão inicial
 * LOG DE ALTERAÇÕES: Revision 1.3 2005/11/09
 * 17:04:53 lfrodrigues removed nullPointerException from waitForMessage with
 * timeout method in Agent class Revision 1.2 2005/09/06 17:46:30 lfrodrigues
 * remocao de variaveis chamadas de enum, essa palavra eh reservada para java
 * 1.5 Revision 1.1 2005/08/17 17:48:14 lfrodrigues instalacao do maven como
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
 * application example Revision 1.4 2004/09/10 17:49:44 rbp Main modifications:
 * Message is not an event anymore. The information can be carried through the
 * InfoCarrier object. TriggerManager complete modification. Now it allows
 * multiple norm and clock instances. Revision 1.3 2004/07/14 21:08:16 rbp
 * Inserindo alterações para permitir multiplas organizações, cenários,
 * não-determinismo etc. Revision 1.2 2004/07/06 16:53:29 guga atualizacao
 * subjects e observer por identificador Revision 1.1 2004/06/24 22:42:51 rbp
 * Após primeira reengenharia de pacotes e antes de gerar modelos Revision 1.1
 * 2004/06/16 01:58:25 rbp Primeiros arquivos a entrar no CVS
 ******************************************************************************/
