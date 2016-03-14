package br.pucrio.inf.les.law.component.criticality;

import java.util.Hashtable;
import java.util.Stack;

import org.xml.sax.Attributes;

import br.pucrio.inf.les.law.execution.DescriptorTable;
import br.pucrio.inf.les.law.component.ElementDescriptor;
import br.pucrio.inf.les.law.component.criticality.CriticalityAnalysisDescriptor;
import br.pucrio.inf.les.law.model.Id;
import br.pucrio.inf.les.law.model.LawException;
import br.pucrio.inf.les.law.parser.handler.base.ComposedHandler;

public class CriticalityAnalysisHandler extends ComposedHandler{

	public CriticalityAnalysisHandler(Stack<Object> stack, DescriptorTable table, Hashtable<Id, Object> handlerCache) {
		super(stack, table, handlerCache);
	}

	@Override
	public void process(String namespaceURI, String localName, String qName, Attributes attr) throws LawException {
		//Recupera o descritor da organização/cena no qual a analise de criticalidade foi definida
		ElementDescriptor desc= (ElementDescriptor) stack.peek();
		
		// Criação do descritor
		CriticalityAnalysisDescriptor criticalityAnalysis = new CriticalityAnalysisDescriptor(getId());
		
		// Relaciona a cena e o descritor
		desc.setCriticalityAnalysisDescriptor(criticalityAnalysis);

		table.add(criticalityAnalysis);
        stack.push(criticalityAnalysis);
	}

	@Override
	public void solvePendencies() throws LawException {
		// TODO Auto-generated method stub
		
	}

}
