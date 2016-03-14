package br.pucrio.inf.les.law.communication.socket;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.UnknownHostException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.pucrio.inf.les.law.communication.Message;
import br.pucrio.inf.les.law.model.LawException;



/**
 * @author mgatti
 *
 */
public class SocketCommunication extends Thread{
	
	private static final Log LOG = LogFactory.getLog(SocketCommunication.class);
	
	private Queue queue = new Queue();
	private ServerSocket socket = null;
	private boolean isRunning = false;
	
	public SocketCommunication(){
		try {
			
			socket = new ServerSocket();			
			
			LOG.debug("[Comunicação Socket] porta: " + socket.getLocalPort());
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public SocketCommunication(int port){
		try {
			
			socket = new ServerSocket(port);			
			
			//Log.log.debug("[Comunicação Socket] porta: " + strPort);
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public int getPort(){
		return socket.getLocalPort();
	}
	
	public SocketAddress getLocalSocketAddress(){
		return socket.getLocalSocketAddress();
	}
	
	
	
	/* (non-Javadoc)
	 * @see br.pucrio.inf.les.law.shared.agent.communication.ICommunication#send(br.pucrio.inf.les.law.shared.agent.communication.Message)
	 */
	public void send(Message msg) {
		try {
			if (msg.getFirstReceiver().getAllAddresses().iterator().hasNext()){
				String address = (String) msg.getFirstReceiver().getAllAddresses().iterator().next();
				Socket clientSocket = new Socket(address,msg.getFirstReceiver().getPort());
				
				ObjectOutputStream out = new ObjectOutputStream(clientSocket.getOutputStream());
				out.writeObject(msg);
				out.close();
				clientSocket.close();
			}else{
				//throw new LawException("A mensagem não possui o endereço do destinatário!",LawException.AGENT_RECEIVER_ADDRESS_NOT_AVAILABLE);
			}
		/*} catch (LawException e) {
			e.printStackTrace();*/
		}  catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void send(Message msg, String address, int portNumber) throws LawException {
		try {

			Socket clientSocket = new Socket(address,portNumber);
			
			ObjectOutputStream out = new ObjectOutputStream(clientSocket.getOutputStream());
			out.writeObject(msg);
			out.close();
			clientSocket.close();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	
	public Message receiveMessage(){
		try {				
			Socket clientSocket = socket.accept();
			ObjectInputStream in = 
			        new ObjectInputStream(
			            clientSocket.getInputStream());
			
			Message msg = (Message)in.readObject();
			return msg;
		
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	public Message waitForMessage() {
		Message message = null;
        while (message == null) {
        	message = receiveMessage();
            if (message == null) {
                try {
                    Thread.sleep(20);
                } catch (InterruptedException e) {
                }
            }
        }
		return message;
	}

	/* (non-Javadoc)
	 * @see br.pucrio.inf.les.law.shared.agent.communication.ICommunication#waitForMessage(long)
	 */
	public Message waitForMessage(long milliseconds) {
		int increment = 250;
        long elapsed = 0;
		Message message = null;
        while (message == null && (milliseconds > elapsed)) {
        	message = receiveMessage();
            if (message == null) {
                try {
                    Thread.sleep(increment);
                    elapsed += increment;
                } catch (InterruptedException e) {
                }
            }
        }
		return message;
	}

	/* (non-Javadoc)
	 * @see br.pucrio.inf.les.law.shared.agent.communication.ICommunication#nextMessage()
	 */
	public Message nextMessage() {
		if (queue.size()>0){
			return (Message) queue.firstElement();
		}else{
			return null;
		}
	}
	
	public void run(){
		while(isRunning){
			Message msg = receiveMessage();
			queue.add( msg );				
		}
	}

}
