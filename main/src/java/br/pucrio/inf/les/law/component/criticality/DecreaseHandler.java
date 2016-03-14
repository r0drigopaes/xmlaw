package br.pucrio.inf.les.law.component.criticality;

import java.util.Hashtable;
import java.util.Stack;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.xml.sax.Attributes;

import br.pucrio.inf.les.law.event.Masks;
import br.pucrio.inf.les.law.execution.DescriptorTable;
import br.pucrio.inf.les.law.model.Id;
import br.pucrio.inf.les.law.model.LawException;
import br.pucrio.inf.les.law.parser.handler.base.ComposedHandler;

public class DecreaseHandler extends ComposedHandler{

	private static final Log LOG = LogFactory.getLog(DecreaseHandler.class);
	
	public DecreaseHandler(Stack<Object> stack, DescriptorTable table, Hashtable<Id, Object> handlerCache) {
		super(stack, table, handlerCache);
	}

	@Override
	public void process(String namespaceURI, String localName, String qName, Attributes attr) throws LawException {
		
		int eventType = Masks.getMask(attr.getValue("event-type"));
		if (!checkEventType(eventType)){
			throw new LawException("O tipo de evento " + attr.getValue("event-type") + " não pode ser especificado para a análise da criticalidade.", LawException.INVALID_EVENT_TYPE_SPECIFIED);
		}
		
		String eventId = attr.getValue("event-id") ;
		double value = Double.parseDouble(attr.getValue("value")); 
		EventDescriptor decrease = new EventDescriptor(id,eventId,eventType,value);
		
		CriticalityAnalysisDescriptor criticalityAnalysisDescriptor = (CriticalityAnalysisDescriptor) stack.peek(); //CriticalityAnalysis
		criticalityAnalysisDescriptor.addDecreaseDescriptors(decrease);
		table.add(decrease);
		stack.push(decrease);
		
	}
	
	public boolean checkEventType(int eventType){
		if ((eventType == Masks.ROLE_ACTIVATION) ||
			(eventType == Masks.ROLE_DEACTIVATION) ||
			(eventType == Masks.CLOCK_ACTIVATION) ||
			(eventType == Masks.CLOCK_DEACTIVATION) ||
			(eventType == Masks.NORM_ACTIVATION) ||
			(eventType == Masks.TRANSITION_ACTIVATION) ||
			(eventType == Masks.NORM_DEACTIVATION) ){
			return true;
		}else{
			return false;
		}
	}

	@Override
	public void solvePendencies() throws LawException {
	}

}
