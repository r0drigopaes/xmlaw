package br.pucrio.inf.les.law.application.selic.constraint;

import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.pucrio.inf.les.law.communication.Message;
import br.pucrio.inf.les.law.component.constraint.AbstractConstraint;
import br.pucrio.inf.les.law.event.Event;

public class ConditionNOPRET extends AbstractConstraint{
	
	private static final Log LOG = LogFactory.getLog(ConditionNOPRET.class);

	public ConditionNOPRET(Map<String, Object> parameters) {
		super(parameters);
	}

	public boolean constrain(Map<String, Object> parameters) {

		Message msg = null;
		try{
			msg = (Message) parameters.get(Event.MESSAGE);
		}
		catch(NullPointerException e){
			LOG.debug("ConditionNOPRET could not get NOPRET from parameters, because could not get the message.");
			return true;
		}
		String strNOPRET = msg.getContentValue("NOPRET");
		return !strNOPRET.equals(" ");
	}

}

