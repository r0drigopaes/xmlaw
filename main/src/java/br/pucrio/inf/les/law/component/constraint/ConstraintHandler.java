/*
 * Created on 11/05/2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package br.pucrio.inf.les.law.component.constraint;

import java.util.Hashtable;
import java.util.Stack;

import org.xml.sax.Attributes;

import br.pucrio.inf.les.law.execution.DescriptorTable;
import br.pucrio.inf.les.law.model.Id;
import br.pucrio.inf.les.law.model.LawException;
import br.pucrio.inf.les.law.parser.handler.base.AbstractHandler;

/**
 * @author mgatti
 */
public class ConstraintHandler extends AbstractHandler {
	
	public ConstraintHandler(Stack<Object> stack, DescriptorTable table, Hashtable<Id, Object> handlerCache) {
		super(stack, table, handlerCache);
	}

	public void process(String namespaceURI, String localName, String qName,
			Attributes attr) {
		
		IConstraint handler = (IConstraint) stack.peek();
		
		String conditionClass	= attr.getValue("class");				
		String semantic	= attr.getValue("semantics");	
		Constraint descriptor = new Constraint(id, conditionClass, semantic);
		handler.addConstraint(descriptor);
		
	}

	public void solvePendencies() throws LawException {}

}
