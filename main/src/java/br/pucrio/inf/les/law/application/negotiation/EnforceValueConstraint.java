package br.pucrio.inf.les.law.application.negotiation;

import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.pucrio.inf.les.law.component.constraint.AbstractConstraint;

/**
 * @author mgatti
 */
public class EnforceValueConstraint extends AbstractConstraint{

	private double maxPrice;
	private double price;
	
	private static final Log LOG = LogFactory.getLog(EnforceValueConstraint.class);

	public EnforceValueConstraint(Map<String, Object> parameters) {
		super(parameters);
	}
	
	@Override
	public boolean constrain(Map<String, Object> parameters) {
		// TODO Auto-generated method stub
		return false;
	}
}
