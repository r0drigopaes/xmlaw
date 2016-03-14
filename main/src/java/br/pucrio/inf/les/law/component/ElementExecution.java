package br.pucrio.inf.les.law.component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.pucrio.inf.les.law.communication.AgentIdentification;
import br.pucrio.inf.les.law.component.criticality.CriticalityAnalysisExecution;
import br.pucrio.inf.les.law.component.protocol.ProtocolExecution;
import br.pucrio.inf.les.law.component.role.RoleDescriptor;
import br.pucrio.inf.les.law.component.scene.SceneExecution;
import br.pucrio.inf.les.law.event.Event;
import br.pucrio.inf.les.law.event.IObserver;
import br.pucrio.inf.les.law.event.Masks;
import br.pucrio.inf.les.law.event.Subject;
import br.pucrio.inf.les.law.execution.Context;
import br.pucrio.inf.les.law.execution.IDescriptor;
import br.pucrio.inf.les.law.execution.IExecution;
import br.pucrio.inf.les.law.execution.IExecution.ExecutionState;
import br.pucrio.inf.les.law.model.Id;
import br.pucrio.inf.les.law.model.LawException;

public class ElementExecution implements IExecution, IObserver {
	
	/**
	 * Logger for this class
	 */
	private static final Log LOG = LogFactory.getLog(ElementExecution.class);
	
	protected Context context;

	protected Id id;

	protected CriticalityAnalysisExecution criticality;

	protected IDescriptor descriptor;

	private ExecutionState executionState = ExecutionState.RUNNING;

	public ElementExecution(Context context, 
							IDescriptor descriptor,
							CriticalityAnalysisExecution criticalityExecution){
        this.context = context;
        this.descriptor = descriptor;
        this.criticality = criticalityExecution;
	}

	public Context getContext() {
		return this.context;
	}

	public void setId(Id newId) {
		this.id = newId;
	}

	public Id getId() {
		return this.id;
	}

	public void stopExecution() {
	}

	public IDescriptor getDescriptor() {
		return this.descriptor;
	}

	public void setDescriptor(IDescriptor descriptor) {
		this.descriptor = descriptor;
	}

	public boolean shouldStop(Event event) throws LawException {
		return true;
	}

	public ExecutionState getExecutionState() {
		if (criticality!=null){
			if (criticality.getExecutionState().equals(ExecutionState.FINISHED)){ 
				return this.executionState;
			}
		}		
		return ExecutionState.RUNNING;
	}

	public void setExecutionState(ExecutionState state) {
		this.executionState = state;
	}

	public void update(Event event) throws LawException {
		// TODO Auto-generated method stub

	}

	public void notifySubjectDeath(Subject subject) {
		// TODO Auto-generated method stub
		
	}
	
	
	
}
