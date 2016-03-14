/* ****************************************************************
 *   @filename:		ActionDescriptor.java
 *   @projectname:  Law
 *   @date:			Nov 23, 2004
 *   @author 		Rodrigo - LES (PUC-Rio)
 * 	 
 *   $Id: ActionDescriptor.java,v 1.5 2006/07/04 21:10:33 mgatti Exp $
 * ***************************************************************/
package br.pucrio.inf.les.law.component.action;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.pucrio.inf.les.law.component.ElementTriggerDescriptor;
import br.pucrio.inf.les.law.event.Event;
import br.pucrio.inf.les.law.event.Masks;
import br.pucrio.inf.les.law.execution.Context;
import br.pucrio.inf.les.law.execution.IExecution;
import br.pucrio.inf.les.law.model.Id;
import br.pucrio.inf.les.law.model.LawException;

/**
 * @author 	 Rodrigo  - LES (PUC-Rio)
 * @version  $Revision: 1.5 $
 */
public class ActionDescriptor extends ElementTriggerDescriptor {
	
	/**
     * Logger for this class
     */
    private static final Log LOG = LogFactory.getLog(ActionDescriptor.class);

	private String actionClass;
	private ActionExecution action;
	
	public ActionDescriptor( Id id, String actionClass){
		super(id);
		this.actionClass = actionClass;
	}
	
	public String toString(){
		return "Action " + getId();
	}
	
	public boolean isDeactivatedBy(Event event) {
		return true;
	}
	
	private ActionExecution getActionClassInstance(Context context, 
												   Map<String,Object> parameters, 
												   String actionName) throws LawException {
		try {
		
			Class className = Class.forName(actionName);
			Class[] args = {Context.class, Map.class, ElementTriggerDescriptor.class};
			Constructor constructor = className.getConstructor(args);
			Object[] params = {context, parameters, this};
			return (ActionExecution)constructor.newInstance(params);
		
		} catch (NoSuchMethodException e) {
			throw new LawException("Action constructor was not found",LawException.CLASS_NO_SUCH_METHOD);
		} catch (InvocationTargetException e) {
			throw new LawException("Action constructor was not found",LawException.CLASS_NO_SUCH_METHOD);
		} catch (ClassNotFoundException e) {
			throw new LawException("Action class was not found",LawException.CLASS_NOT_FOUND);
		} catch (InstantiationException e) {
			throw new LawException("Action class does not have the default constructor or it is not visible",LawException.CONSTRUCTOR_INVOCATION);
		} catch (IllegalAccessException e) {
			throw new LawException("It was not possible to instanciate the action class, access denied.",LawException.ILLEGAL_ACCESS);
		}
	}
	
	public IExecution createExecution(Context context, Map<String, Object> parameters) throws LawException {
		if (action == null){
			//first fire the event
			Event event = new Event(getId(), Masks.ACTION_ACTIVATION,getId());
			event.setContent(parameters);
	        context.fire(event);
			action = getActionClassInstance(context,parameters, actionClass);
		}
		return action;
	}

	public boolean needContext() {
		return false;
	}

	public boolean shouldCreate(Context context, Map<String, Object> parameters) throws LawException {
		return true;
	}

}