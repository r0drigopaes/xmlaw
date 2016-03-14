package br.pucrio.inf.les.law.communication;


/**
 * Interface de comunicação que serve como abstração para a camada de
 * comunicação dos agentes.
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
     * que invocou este método até que uma mensagem seja recebida.
     * 
     * @return a mensagem recebida.
     */
    public Message waitForMessage();

    /**
     * Espera por uma mensagem por um tempo maximo de expiração
     * 
     * @param milliseconds -
     *            tempo de expiração
     * @return null se o tempo expirar ou mensagem recebida
     */
    public Message waitForMessage(long milliseconds);

    /**
     * Retorna a primeira mensagem da lista de mensagens recebidas e retira a
     * mensagem da lista. Note que esta lista pode ser implementada de várias
     * formas, como uma fila ou pilha, por exemplo.
     * 
     * @return Message caso tenha alguma mensagem na lista e null caso
     *         contrário.
     */
    public Message nextMessage();
    
    public void close();
    
    public Message waitRightMessage(String conversationId);
}