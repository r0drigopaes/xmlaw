/* ****************************************************************
 *   @filename:                Message.java
 *   @projectname:  Law
 *   @date:                        Jun 14, 2004
 *   @author                 rbp
 *
 *   $Id: Message.java,v 1.10 2006/05/06 19:47:11 lfrodrigues Exp $
 * ***************************************************************/
package br.pucrio.inf.les.law.communication;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Classe utilizada para representar as mensagens que os agentes utilizam para
 * se comunicar. TODO Verificar como implementar multicast, ou seja, mandar uma
 * mensagem para todos os agentes que desempenham o papel de buyer, por exemplo.
 * 
 * @alias Message
 * @author rbp & guga
 * @version $Revision: 1.10 $
 */
public class Message implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/** Performativa FIPA. Veja: http://www.fipa.org/specs/fipa00037/ */
	public static final String	CANCEL				= "cancel";

	/** Performativa FIPA. Veja: http://www.fipa.org/specs/fipa00037/ */
	public static final String	CONFIRM				= "confirm";

	/** Performativa FIPA. Veja: http://www.fipa.org/specs/fipa00037/ */
	public static final String	CALL_FOR_PROPOSAL	= "cfp";

	/** Performativa FIPA. Veja: http://www.fipa.org/specs/fipa00037/ */
	public static final String	PROPOSE				= "propose";

	/** Performativa FIPA. Veja: http://www.fipa.org/specs/fipa00037/ */
	public static final String	REFUSE				= "refuse";

	/** Performativa FIPA. Veja: http://www.fipa.org/specs/fipa00037/ */
	public static final String	ACCEPT_PROPOSAL		= "accept-proposal";

	/** Performativa FIPA. Veja: http://www.fipa.org/specs/fipa00037/ */
	public static final String	REJECT_PROPOSAL		= "reject-proposal";

	/** Performativa FIPA. Veja: http://www.fipa.org/specs/fipa00037/ */
	public static final String	REQUEST				= "request";

	/** Performativa FIPA. Veja: http://www.fipa.org/specs/fipa00037/ */
	public static final String	INFORM				= "inform";

	/** Performativa FIPA. Veja: http://www.fipa.org/specs/fipa00037/ */
	public static final String	FAILURE				= "failure";

	/** Performativa FIPA. Veja: http://www.fipa.org/specs/fipa00037/ */
	public static final String	NOT_UNDERSTOOD		= "not-understood";

	/**
	 * Remetente da mensagem.
	 * 
	 * @directed true
	 * @label sender
	 */
	private AgentIdentification sender;

	/**
	 * Papél que o sender está desempenhado para enviar a mensagem.
	 */
	private String senderRole;

	
	/**
	 * Lista com os agentes e os pápeis de agentes que irão receber a mensagem
	 */
	private Map<AgentIdentification,String> receivers;

	/**
	 * Campo conversationId da mensagem.
	 */
	private String conversationId;

	/**
	 * Campo performative da mensagem.
	 */
	private String performative;

	/**
	 * Campo conteúdo da mensagem.
	 */
	private Map<String, String> content;

	/**
	 * Campo conteúdo da mensagem.
	 */
	private Map<String, String> userDefined;

	/**
	 * O protocolo na qual esta mensagem está inserida.
	 */
	private String protocol;

	/**
	 * Cria uma mensagem com a performativa passada no parâmetro.
	 * 
	 * @param performative
	 */
	public Message(String performative) {
		this.performative = performative.toLowerCase();
		this.userDefined = new HashMap<String, String>();
		this.content = new HashMap<String, String>();
		this.receivers = new HashMap<AgentIdentification, String>();
	}

	/**
	 * Define o valor de uma chave definida pelo usuário armazenada na mensagem
	 */
	public void addUserDefined(String key, String value) {
		userDefined.put(key, value);
	}

	/**
	 * Obtém o valor de uma chave definida pelo usuário armazenada na mensagem
	 */
	public String getUserDefined(String key) {
		return (String) userDefined.get(key);
	}

	public Map<String, String> getAllUserDefined() {
		return userDefined;
	}

	/**
	 * Adiciona um destinatário à mensagem
	 */
	public void addReceiver(AgentIdentification receiverAid, String receiverRole) {
		receivers.put(receiverAid, receiverRole);
	}

	/**
	 * Obtém o papel de um agente destinatário
	 */
	public String getReceiverRole(AgentIdentification aid) {
		return (String) receivers.get(aid);
	}

	public Map<AgentIdentification, String> getAllReceivers() {
		return receivers;
		
	}
	
	/**
	 * @deprecated should use getReceiver
	 * @return
	 */
	public AgentIdentification getFirstReceiver()
	{
		return receivers.keySet().iterator().next();
	}
	
	/**
	 * @deprecated should use getReceiverRole
	 * @return
	 */
	public String getFirstReceiverRole()
	{
		return receivers.values().iterator().next();
	}
	
	

	/**
	 * Retorna um Map correspondendo ao campo content da mensagem.
	 */
	public Map<String, String> getContent() {
		return this.content;
	}

	public String getContentValue(String key) {
		return content.get(key);
	}

	public void setContentValue(String key, String value) {
		content.put(key, value);
	}

	public void setContent(Map<String, String> content) {
		this.content = content;
	}

	/**
	 * Cria uma nova mensagem para ser enviada como resposta desta mensagem.
	 * Mantendo os campos referentes a cena e a organizacao pela qual a mensagem foi enviada
	 */
	public Message createReply(AgentIdentification newSender, String newSenderRole) {
		Message reply = new Message(this.performative);

		reply.conversationId = conversationId;
		reply.performative = performative;
		
		reply.receivers = new HashMap<AgentIdentification, String>();
		reply.receivers.put(sender,senderRole);
		
		reply.sender = newSender;
		reply.senderRole = newSenderRole;
		reply.userDefined = new HashMap<String, String>(userDefined);
		reply.protocol = protocol;
		
		String orgId = getContentValue(MessageContentConstants.KEY_ORGANIZATION_EXECUTION_ID);
		if(orgId != null)
		{
			reply.setContentValue(MessageContentConstants.KEY_ORGANIZATION_EXECUTION_ID, orgId);
		}
		
		String sceneId = getContentValue(MessageContentConstants.KEY_SCENE_EXECUTION_ID);
		if(sceneId != null)
		{
			reply.setContentValue(MessageContentConstants.KEY_SCENE_EXECUTION_ID,sceneId);
		}
		
		return reply;
	}

	/**
	 * Retorna o conversationId desta mensagem. O ConversationId é útil para o
	 * gerenciamento de contexto das conversações. Permitindo que várias trocas
	 * de mensagens possam ser identificadas como partes de um mesmo contexto.
	 */
	public String getConversationId() {
		return this.conversationId;
	}

	/**
	 * Retorna a performativa utilizada nesta mensagem.
	 */
	public String getPerformative() {
		return this.performative;
	}

	/**
	 * Define o remetente da mensagem.
	 * 
	 * @param sender
	 *            o remetente da mensagem
	 * @param senderRole
	 *            o papel desempenhado pelo remetente da mensagem
	 * @hipothesis O senderRole pode ser nulo.
	 * @author rbp
	 */
	public void setSender(AgentIdentification sender, String senderRole) {
		setSender(sender);
		this.senderRole = senderRole;
	}
	
	public void setSender(AgentIdentification sender)
	{
		this.sender = sender;
	}

	/**
	 * Retorna o remetente da mensagem.
	 * 
	 * @return sender
	 */
	public AgentIdentification getSender() {
		return this.sender;
	}

	/**
	 * Define o conversationId da mensagem.
	 */
	public void setConversationId(String conversationId) {
		this.conversationId = conversationId;
	}

	public void setProtocol(String protocol) {
		this.protocol = protocol;
	}

	public String getProtocol() {
		return protocol;
	}

	/**
	 * Formata esta mensagem no padrão:<br>
	 * message(performative,sender(senderName,senderRole),receiver(receiverName,receiverRole),content(content)).
	 * 
	 * @return a mensagem formatada
	 * @pre a mensagem já deve estar completa, ou seja, seus campos já devem ter
	 *      sidos definidos
	 * @hipothesis Assume que se o conteúdo da mensagem contiver separadores de
	 *             linha, ele é identificado pelo caracter \n.
	 * @author rbp
	 */
	public String format() {

		StringBuffer buffer = new StringBuffer();

		buffer.append("message(");
		buffer.append(this.performative);
		buffer.append(",sender(");
		if (sender != null)
			buffer.append(sender.getName());
		buffer.append(",");
		if (senderRole != null)
			buffer.append(this.senderRole);
		buffer.append("),receivers(");
		
		for(Map.Entry<AgentIdentification,String> entry : receivers.entrySet())
		{
			buffer.append("[" + entry.getKey() + "," + entry.getValue() + "]");			
		}

		buffer.append("),content( ");

		for (Map.Entry entry : content.entrySet()) {
			buffer.append("[" + entry.getKey() + "," + entry.getValue() + "]");
		}
		
		buffer.append("),userDefined");
		for(Map.Entry entry : userDefined.entrySet())
		{
			buffer.append("[" + entry.getKey() + "," + entry.getValue() + "]");
		}

		return buffer.toString();
	}

	public String toString() {
		return "conv-id[" + this.conversationId + "] protocol[" + protocol
				+ "] " + this.format();
	}


	/**
	 * Retorna o papél desempenhado pelo remetente desta mensagem.
	 * 
	 * @return O papel desempenhado
	 * @author rbp
	 */
	public String getSenderRole() {
		return senderRole;
	}

	/**
	 * Define uma nova performativa para a mensagem. A classe Message possui um
	 * conjunto atributos de classe que definem as performativas.
	 * 
	 * @param performative
	 *            a Performativa
	 * @author rbp
	 */
	public void setPerformative(String performative) {
		this.performative = performative;
	}

	/**
	 * Verifica se a performativa da mensagem é a mesma que a recebida como
	 * parâmetro
	 * 
	 * @param performative
	 *            uma Performativa
	 * @return true caso sejam igual, false caso sejam diferentes
	 * @author guga
	 */
	public boolean checkPerformative(String performative) {
		return this.performative.equals(performative);
	}

	private boolean equalsAux(Object obj1, Object obj2)
	{
		if(obj1 == null && obj2 == null)
		{
			return true;
		}
		else if((obj1 == null && obj2 != null)||(obj1 != null && obj2 == null))
		{
			return false;
		}
		else
		{
			return obj1.equals(obj2);
		}
	}

	public boolean equals(Object obj)
	{
		if ((obj instanceof Message) == false)
		{
			return false;
		}

		Message message = (Message) obj;

		return equalsAux(message.getSender(), getSender()) && equalsAux(message.getAllReceivers(), getAllReceivers())
				&& equalsAux(message.getPerformative(), getPerformative())
				&& equalsAux(message.getProtocol(), getProtocol())
				&& equalsAux(message.getConversationId(), getConversationId())
				&& equalsAux(message.getContent(), getContent())
				&& equalsAux(message.getAllUserDefined(), getAllUserDefined());
	}
}
