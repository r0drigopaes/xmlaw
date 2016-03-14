package br.pucrio.inf.les.law.component;

import br.pucrio.inf.les.law.event.Masks;
import br.pucrio.inf.les.law.execution.DescriptorTable;
import br.pucrio.inf.les.law.model.Id;
import br.pucrio.inf.les.law.model.LawException;
import br.pucrio.inf.les.law.parser.handler.base.AbstractHandler;

import java.util.Hashtable;
import java.util.Stack;

import org.xml.sax.Attributes;

public class DeactivationElementRefHandler extends AbstractHandler {

    public DeactivationElementRefHandler(Stack<Object> stack,
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

        try {
            elementDescriptor.addDeactivation(new Id(ref.getElementRef()),
                    Masks.getMask(ref.getElementType()));
        } catch (NullPointerException e) {
            throw new LawException("There is no mask for event type: "
                    + ref.getElementType() + ", at " + elementDescriptor,
                    LawException.MASKS_INCONSISTENT);
        }

    }

    @Override
    public void solvePendencies() throws LawException {
        // TODO Auto-generated method stub

    }

}
