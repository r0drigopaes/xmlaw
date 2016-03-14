package br.pucrio.inf.les.law.component.message;

import java.util.Hashtable;
import java.util.Stack;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.xml.sax.Attributes;

import br.pucrio.inf.les.law.component.protocol.ProtocolDescriptor;
import br.pucrio.inf.les.law.execution.DescriptorTable;
import br.pucrio.inf.les.law.model.Id;
import br.pucrio.inf.les.law.model.LawException;
import br.pucrio.inf.les.law.parser.handler.base.ComposedHandler;

public class MessageHandler extends ComposedHandler {
    /**
     * Logger for this class
     */
    private static final Log LOG = LogFactory.getLog(MessageHandler.class);

    public MessageHandler(Stack<Object> stack, DescriptorTable table,
            Hashtable<Id, Object> handlerCache) {
        super(stack, table, handlerCache);
    }

    @Override
    public void process(String namespaceURI, String localName, String qName,
            Attributes attr) throws LawException {
        MessageDescriptor messageDescriptor = new MessageDescriptor();
        messageDescriptor.setId(getId());
        String performative = attr.getValue("performative");
        messageDescriptor.setPerformative(performative);
        
        //TODO inserir o tal do content aqui
        ProtocolDescriptor protocolDescriptor = (ProtocolDescriptor)stack.peek();
        protocolDescriptor.addMessageDescriptor(messageDescriptor);

        stack.push(messageDescriptor);

        // Coloco no cache
        handlerCache.put(messageDescriptor.getId(), messageDescriptor);
    }

    @Override
    public void solvePendencies() throws LawException {
        // TODO Auto-generated method stub

    }

}
