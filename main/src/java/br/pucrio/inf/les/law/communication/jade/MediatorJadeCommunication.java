package br.pucrio.inf.les.law.communication.jade;

import jade.lang.acl.ACLMessage;
import br.pucrio.inf.les.law.communication.Message;

public class MediatorJadeCommunication extends JadeCommunication
{

	public MediatorJadeCommunication(String name)
	{
		super(name);
	}
	
	/**
     * Envia uma mensagem.
     */
    public void send(Message msg) {
        ACLMessage message = this.messageWrapper.transform(msg);
        jadeAgent.send(message);
    }

}
