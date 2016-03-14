package br.pucrio.inf.les.law.component.clock;

import br.pucrio.inf.les.law.execution.DescriptorTable;
import br.pucrio.inf.les.law.execution.TriggerKeeper;
import br.pucrio.inf.les.law.model.Id;
import br.pucrio.inf.les.law.model.LawException;
import br.pucrio.inf.les.law.parser.handler.base.ComposedHandler;

import java.util.Hashtable;
import java.util.Stack;

import org.xml.sax.Attributes;

public class ClockHandler extends ComposedHandler {

    public ClockHandler(Stack<Object> stack, DescriptorTable table,
            Hashtable<Id, Object> handlerCache) {
        super(stack, table, handlerCache);
    }

    @Override
    public void process(String namespaceURI, String localName, String qName,
            Attributes attr) throws LawException {

    	//  It may be either an organizationdescriptor or scenedescriptor.
        TriggerKeeper triggerKeeper = (TriggerKeeper) stack.peek();

        String strType = attr.getValue("type");
        String tickPeriod = attr.getValue("tick-period");
        ClockExecution.Type type = null;
        if (strType.equals("periodic")) {
            type = ClockExecution.Type.PERIODIC;
        } else if (strType.equals("once")) {
            type = ClockExecution.Type.ONCE;
        } else {
            throw new LawException("Clock type invalid: " + strType
                    + " in Clock id " + getId(),
                    LawException.INVALID_CLOCK_TYPE);
        }

        ClockDescriptor clockDescriptor = new ClockDescriptor(getId(), Integer
                .parseInt(tickPeriod), type);

        triggerKeeper.addClockDescriptor(clockDescriptor);

        table.add(clockDescriptor);
        stack.push(clockDescriptor);

    }

    @Override
    public void solvePendencies() throws LawException {
        // TODO Auto-generated method stub

    }

}
