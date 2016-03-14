package br.pucrio.inf.les.law.component.constraint;

import java.util.Map;


/**
 * @author 	 mgatti
 */
public abstract class AbstractConstraint {
	
	protected Map<String, Object> parameters;
	
	private AbstractConstraint(){}
	
	public AbstractConstraint(Map<String, Object> parameters){
		this.parameters = parameters;
	}
	
	public abstract boolean constrain(Map<String, Object> parameters);

}