package br.pucrio.inf.les.law.component.norm;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Stack;
import java.util.Vector;

import org.xml.sax.Attributes;

import br.pucrio.inf.les.law.component.ElementDescriptorKeeper;
import br.pucrio.inf.les.law.execution.DescriptorTable;
import br.pucrio.inf.les.law.model.Id;
import br.pucrio.inf.les.law.model.LawException;
import br.pucrio.inf.les.law.parser.handler.base.AbstractHandler;

public class DeactivatedNormRefHandler extends AbstractHandler {
	protected Hashtable pendingList = new Hashtable();

	public DeactivatedNormRefHandler(Stack<Object> stack, DescriptorTable table, Hashtable<Id, Object> handlerCache) {
		super(stack, table, handlerCache);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void process(String namespaceURI, String localName, String qName,
			Attributes attr) throws LawException {
		
		ElementDescriptorKeeper normDescriptorKeeper = (ElementDescriptorKeeper) stack.peek(); 
		//ref não pode ser diferente
		String normRef = attr.getValue("ref");

		Vector vetor = (Vector) pendingList.get(normRef);
		
		if (vetor == null){
			vetor = new Vector();
		}
		
		vetor.add(normDescriptorKeeper);
		pendingList.put(normRef, vetor);

	}

	@Override
	public void solvePendencies() throws LawException {
		Enumeration keys = pendingList.keys();
		
		while(keys.hasMoreElements()){
			
			String key = (String) keys.nextElement();
			
			NormDescriptor norm = (NormDescriptor) handlerCache.get(key);
			
			Vector list = (Vector) pendingList.get(key);
			for (Iterator iter = list.iterator(); iter.hasNext();) {
				ElementDescriptorKeeper normDescriptorKeeper = (ElementDescriptorKeeper) iter.next();
				normDescriptorKeeper.addActivatedDescriptor(norm);
			}
		}

	}

}
