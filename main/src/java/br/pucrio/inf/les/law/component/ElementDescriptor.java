package br.pucrio.inf.les.law.component;

import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.pucrio.inf.les.law.component.criticality.CriticalityAnalysisDescriptor;
import br.pucrio.inf.les.law.execution.Context;
import br.pucrio.inf.les.law.execution.IDescriptor;
import br.pucrio.inf.les.law.execution.IExecution;
import br.pucrio.inf.les.law.execution.Trigger;
import br.pucrio.inf.les.law.execution.TriggerKeeper;
import br.pucrio.inf.les.law.model.Id;
import br.pucrio.inf.les.law.model.LawException;

public class ElementDescriptor extends TriggerKeeper implements IDescriptor {
	
	private static final Log LOG = LogFactory.getLog(ElementDescriptor.class);
	
	private Id id;

	private CriticalityAnalysisDescriptor criticalityAnalysisDescriptor;
	
	public ElementDescriptor(Id id) {
		this.id = id;
	}

	public boolean needContext() {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean shouldCreate(Context context, Map<String, Object> parameters)
			throws LawException {
		// TODO Auto-generated method stub
		return false;
	}

	public IExecution createExecution(Context context,
			Map<String, Object> parameters) throws LawException {
		// TODO Auto-generated method stub
		return null;
	}

	public Id getId() {
        return id;
    }

	public Trigger getTrigger() {
		// TODO Auto-generated method stub
		return null;
	}
	
	public CriticalityAnalysisDescriptor getCriticalityAnalysisDescriptor() {
		return criticalityAnalysisDescriptor;
	}

	public void setCriticalityAnalysisDescriptor(
			CriticalityAnalysisDescriptor criticalityAnalysisDescriptor) {
		this.criticalityAnalysisDescriptor = criticalityAnalysisDescriptor;
	}

}
