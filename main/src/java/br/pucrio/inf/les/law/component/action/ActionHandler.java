package br.pucrio.inf.les.law.component.action;

import java.util.Hashtable;
import java.util.Stack;

import org.xml.sax.Attributes;

import br.pucrio.inf.les.law.execution.DescriptorTable;
import br.pucrio.inf.les.law.execution.TriggerKeeper;
import br.pucrio.inf.les.law.model.Id;
import br.pucrio.inf.les.law.model.LawException;
import br.pucrio.inf.les.law.parser.handler.base.ComposedHandler;

/**
 * @author mgatti
 */
public class ActionHandler extends ComposedHandler {

	public ActionHandler(Stack<Object> stack, DescriptorTable table,
			Hashtable<Id, Object> handlerCache) {
		super(stack, table, handlerCache);
	}

	
	public void process(String namespaceURI, String localName, String qName,
			Attributes attr) throws LawException {
		TriggerKeeper triggerKeeper = (TriggerKeeper) stack.peek();

		String className = attr.getValue("class");
		 
		ActionDescriptor actionDescriptor = new ActionDescriptor( getId(), className);
		
		triggerKeeper.addActionDescriptor(actionDescriptor);

        table.add(actionDescriptor);
        stack.push(actionDescriptor);

	}

	public void solvePendencies() throws LawException {}

}
