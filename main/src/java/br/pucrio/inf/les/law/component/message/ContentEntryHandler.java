package br.pucrio.inf.les.law.component.message;

import br.pucrio.inf.les.law.execution.DescriptorTable;
import br.pucrio.inf.les.law.model.Id;
import br.pucrio.inf.les.law.model.LawException;
import br.pucrio.inf.les.law.parser.handler.base.AbstractHandler;

import java.util.Hashtable;
import java.util.Stack;

import org.xml.sax.Attributes;

public class ContentEntryHandler extends AbstractHandler {

    public ContentEntryHandler(Stack<Object> stack, DescriptorTable table,
            Hashtable<Id, Object> handlerCache) {
        super(stack, table, handlerCache);
    }

    @Override
    public void process(String namespaceURI, String localName, String qName,
            Attributes attr) throws LawException {
        MessageDescriptor message = (MessageDescriptor) stack.peek();
        String key = attr.getValue("key");
        String value = attr.getValue("value");        
        message.addToContent(key,value);
    }

    @Override
    public void solvePendencies() throws LawException {
        // TODO Auto-generated method stub

    }

}
