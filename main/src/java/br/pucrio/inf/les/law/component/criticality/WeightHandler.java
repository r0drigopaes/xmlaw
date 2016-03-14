package br.pucrio.inf.les.law.component.criticality;

import java.util.Hashtable;
import java.util.Stack;

import org.xml.sax.Attributes;

import br.pucrio.inf.les.law.execution.DescriptorTable;
import br.pucrio.inf.les.law.model.Id;
import br.pucrio.inf.les.law.model.LawException;
import br.pucrio.inf.les.law.parser.handler.base.AbstractHandler;

public class WeightHandler extends AbstractHandler{

	public WeightHandler(Stack<Object> stack, DescriptorTable table, Hashtable<Id, Object> handlerCache) {
		super(stack, table, handlerCache);
	}

	@Override
	public void process(String namespaceURI, String localName, String qName, Attributes attr) throws LawException {
		CriticalityAnalysisDescriptor criticalityAnalysisDescriptor = (CriticalityAnalysisDescriptor) stack.peek(); //CriticalityAnalysis
		String ref = attr.getValue("ref") ;
		double value = Double.parseDouble(attr.getValue("value")); 

		Weight weight = new Weight(ref,value);
		criticalityAnalysisDescriptor.addWeight(weight);
		
	}

	@Override
	public void solvePendencies() throws LawException {
		// TODO Auto-generated method stub
		
	}

}
