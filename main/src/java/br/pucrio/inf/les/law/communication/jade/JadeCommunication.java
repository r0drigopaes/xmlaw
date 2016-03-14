/* ****************************************************************
 *   @filename:		JadeCommunication.java
 *   @projectname:  Law
 *   @author 		
 * 	 
 *   $Id: JadeCommunication.java,v 1.12 2006/05/30 21:46:53 lfrodrigues Exp $
 * ***************************************************************/
package br.pucrio.inf.les.law.communication.jade;

import java.util.Map;

import jade.core.Agent;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.wrapper.AgentContainer;
import jade.wrapper.AgentController;
import jade.wrapper.ControllerException;
import jade.wrapper.StaleProxyException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.pucrio.inf.les.law.communication.AgentIdentification;
import br.pucrio.inf.les.law.communication.ICommunication;
import br.pucrio.inf.les.law.communication.Message;
import br.pucrio.inf.les.law.communication.asf.ASFCommunication;

/**
 * Esta classe implementa a interface de comunica��o ICommunication. Para isto,
 * utiliza-se o JADE como uma camada de comunica��o.
 * 
 * @author Rodrigo Paes
 * @version $Revision: 1.12 $
 */
public abstract class JadeCommunication implements ICommunication {
    /**
     * Logger for this class
     */
    private static final Log LOG = LogFactory.getLog(JadeCommunication.class);

    /*
     * #br.pucrio.inf.les.law.mediator.communication.jade.JadePlatform
     * Dependency_Link1
     */
    /**
     * Para manter a compatibilidade com o formato de mensagem do framework de
     * Laws � preciso definir um Slot na mensagem de JADE que identifique qual �
     * o papel do receiver. Esse slot � identificado atrav�s deste atributo. No
     * framework de Laws, n�o � poss�vel especificar v�rios destinat�rios. Por
     * isso, o atributo RECEIVER_ROLE pode ser adicionado diretamenta como um
     * slot da classe ACLMessage. Caso fosse poss�vel no framework de laws
     * especificar v�rios destinat�rios, ent�o � preciso dizer qual o papel de
     * cada destinat�rio. Desta forma, n�o � mais poss�vel especificar o
     * RECEIVER_ROLE na mensagem ACLMessage (pois s�o v�rios). Ent�o o
     * RECEIVER_ROLE deveria ser especificado como um slot em cada um dos AID
     * dos receivers da mensagem. Note que com esta mudan�a seria necess�rio
     * modificar a classe JadeMessageWrapper.
     */
    public final static String RECEIVER_ROLE = "receiverRole";
    
    /**
	 * Lista com os agentes e os p�peis de agentes que ir�o receber a mensagem
	 */
    public final static String RECEIVERS = "receivers";

    /**
     * Slot da mensagem Jade que identifica o papel do remetente.
     * 
     * @see JadeCommunication#RECEIVER_ROLE
     */
    public final static String SENDER_ROLE = "senderRole";
    
    /**
     * Transforma as mensagens JADE em objetos Message e vice-versa.
     */
    protected JadeMessageWrapper messageWrapper;

    /**
     * Refer�ncia para o container Jade no qual este agente JADE est�
     * executando.
     */
    private AgentContainer container;
    protected Agent jadeAgent;

    /**
     * Instancia uma camada de comunica��o utilizando o Jade.
     * 
     * @param name
     *            O nome do agente. Ex.: sender
     * @author rbp ****************************************************
     */
    public JadeCommunication(String name) {
        if (LOG.isDebugEnabled()){
            LOG.debug("Creating jade communication: "+name);
        }
        messageWrapper = JadeMessageWrapper.getInstance();
        container = JadePlatform.createContainer();
        jadeAgent = new Agent();
        
        if (LOG.isDebugEnabled()){
            LOG.debug("middle: "+name);
        }
        
        try {
            AgentController agentController = this.container.acceptNewAgent(
                    name, jadeAgent);

            agentController.start();

        } catch (StaleProxyException e) {
            LOG.error(e.getMessage(), e);
        } catch	(ControllerException ce){
        	LOG.error(ce.getMessage(), ce);
        }
        
        if (LOG.isDebugEnabled()){
            LOG.debug("[JADE communication] " + jadeAgent.getName());
        }
    }

    /**
     * Envia uma mensagem.
     */
    public abstract void send(Message msg);

    /**
     * Espera pelo recebimento de uma mensagem.
     * 
     * @return
     */
    public Message waitForMessage() {
        ACLMessage aclMessage = null;
        while (aclMessage == null) {
            aclMessage = jadeAgent.receive();
            if (aclMessage == null) {
                try {
                    Thread.sleep(20);
                } catch (InterruptedException e) {
                }
            }
        }

        try {
			return messageWrapper.transform(aclMessage);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
    }
        
    public Message waitRightMessage(String messageId) {
        MessageTemplate template = MessageTemplate.MatchConversationId(messageId);
        ACLMessage aclMessage = null;
        while (aclMessage == null) {
            aclMessage = jadeAgent.receive(template);            
            if (aclMessage == null) {
                try {
                    Thread.sleep(20);
                } catch (InterruptedException e) {
                }
            }
        }

        try {
			return messageWrapper.transform(aclMessage);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
    }

    public Message waitForMessage(long milliseconds) {
        int increment = 250;
        long elapsed = 0;
        ACLMessage aclMessage = null;
        while (aclMessage == null && (milliseconds > elapsed)) {
            aclMessage = jadeAgent.receive();
            if (aclMessage == null) {
                try {
                    Thread.sleep(increment);
                    elapsed += increment;
                } catch (InterruptedException e) {
                }
            }
        }

        if (aclMessage == null) {
            return null;
        }

        try {
			return messageWrapper.transform(aclMessage);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
    }

    /**
     * Retorna a primeira mensagem da fila de mensagens recebidas e retira a
     * mensagem da fila.
     * 
     * @return Message caso tenha alguma mensagem na fila e null caso contr�rio.
     */
    public Message nextMessage() {
        ACLMessage receivedMessage = jadeAgent.receive();
        if (receivedMessage != null) {
            try {
				return messageWrapper.transform(receivedMessage);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return null;
			}
        } else {
            return null;
        }
    }
    
    public void close()
    {
    	try
    	{
    		container.kill();
    	}
    	catch(StaleProxyException spe)
    	{
    		LOG.error("Error killing container",spe);
    	}
    }

}
