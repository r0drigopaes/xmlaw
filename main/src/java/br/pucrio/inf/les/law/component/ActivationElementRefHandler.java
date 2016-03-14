package br.pucrio.inf.les.law.component;

import java.util.Hashtable;
import java.util.Stack;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.xml.sax.Attributes;

import br.pucrio.inf.les.law.event.Masks;
import br.pucrio.inf.les.law.execution.DescriptorTable;
import br.pucrio.inf.les.law.model.Id;
import br.pucrio.inf.les.law.model.LawException;
import br.pucrio.inf.les.law.parser.handler.base.AbstractHandler;

public class ActivationElementRefHandler extends AbstractHandler {

	/**
     * Logger for this class
     */
    private static final Log LOG = LogFactory.getLog(ActivationElementRefHandler.class);
	
	
    public ActivationElementRefHandler(Stack<Object> stack,
            DescriptorTable table, Hashtable<Id, Object> handlerCache) {
        super(stack, table, handlerCache);
    }

    @Override
    public void process(String namespaceURI, String localName, String qName,
            Attributes attr) throws LawException {
        ElementTriggerDescriptor elementDescriptor = (ElementTriggerDescriptor) stack.peek();

        String elementRef = attr.getValue("ref");
        String eventType = attr.getValue("event-type");

        ElementRef ref = new ElementRef(elementRef, (eventType));
        
        LOG.debug("Adicionando como ativação o elemento " + ref.getElementRef() + " para o evento "+ ref.getElementType());
        elementDescriptor.addActivation(new Id(ref.getElementRef()), Masks
                .getMask(ref.getElementType()));

    }

    @Override
    public void solvePendencies() throws LawException {
        // TODO Auto-generated method stub

    }

}
