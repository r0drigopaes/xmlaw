//Source file: D:\\MAÍRA\\2006.1.0\\XMLAW\\Projeto\\src\\xmlaw\\component\\norm\\NormHandler.java

package br.pucrio.inf.les.law.component.norm;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.Hashtable;
import java.util.Stack;

import org.xml.sax.Attributes;

import br.pucrio.inf.les.law.component.norm.NormDescriptor;
import br.pucrio.inf.les.law.execution.DescriptorTable;
import br.pucrio.inf.les.law.execution.TriggerKeeper;
import br.pucrio.inf.les.law.model.Id;
import br.pucrio.inf.les.law.model.LawException;
import br.pucrio.inf.les.law.parser.handler.base.ComposedHandler;

public class NormHandler extends ComposedHandler {
    /**
     * Logger for this class
     */
    private static final Log LOG = LogFactory.getLog(NormHandler.class);

	public NormHandler(Stack<Object> stack, DescriptorTable table,
			Hashtable<Id, Object> handlerCache) {
		super(stack, table, handlerCache);
	}

	public void process(String namespaceURI, String localName, String qName,
			Attributes attr) throws LawException {
		TriggerKeeper triggerKeeper = (TriggerKeeper) stack.peek();

        String strType = attr.getValue("type");
        NormExecution.Type type = null;
        if (strType.equals("permission")) {
            type = NormExecution.Type.PERMISSION;
        } else if (strType.equals("obligation")) {
            type = NormExecution.Type.OBLIGATION;
        } else if (strType.equals("prohibition")) {
            type = NormExecution.Type.PROHIBITION;
        } else {
            throw new LawException("Norm type invalid: " + strType
                    + " in Norm id " + getId(),
                    LawException.INVALID_NORM_TYPE);
        }

        NormDescriptor normDescriptor = new NormDescriptor(getId(), type);

        triggerKeeper.addNormDescriptor(normDescriptor);

        table.add(normDescriptor);
        stack.push(normDescriptor);

	}

	@Override
	public void solvePendencies() throws LawException {
		// TODO Auto-generated method stub

	}
}
