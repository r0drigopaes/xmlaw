package br.pucrio.inf.les.law.communication;


/**
 * Interface de comunica��o que serve como abstra��o para a camada de
 * comunica��o dos agentes.
 * 
 * @alias ICommunication
 * @author guga & rbp
 * @version $Revision: 1.5 $
 */
public interface ICommunication {

    /* #br.pucrio.inf.les.law.mediator.communication.Message Dependency_Link1 */

    /**
     * Sends the message to the addressees specified.
     * 
     * @param msg
     *            the message to be sent.
     */
    public void send(Message msg);

    /**
     * Espera pelo recebimento de uma mensagem. Essa chamada bloqueia o objeto
     * que invocou este m�todo at� que uma mensagem seja recebida.
     * 
     * @return a mensagem recebida.
     */
    public Message waitForMessage();

    /**
     * Espera por uma mensagem por um tempo maximo de expira��o
     * 
     * @param milliseconds -
     *            tempo de expira��o
     * @return null se o tempo expirar ou mensagem recebida
     */
    public Message waitForMessage(long milliseconds);

    /**
     * Retorna a primeira mensagem da lista de mensagens recebidas e retira a
     * mensagem da lista. Note que esta lista pode ser implementada de v�rias
     * formas, como uma fila ou pilha, por exemplo.
     * 
     * @return Message caso tenha alguma mensagem na lista e null caso
     *         contr�rio.
     */
    public Message nextMessage();
    
    public void close();
    
    public Message waitRightMessage(String conversationId);
}