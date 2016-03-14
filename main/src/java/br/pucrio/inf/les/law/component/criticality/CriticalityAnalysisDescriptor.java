package br.pucrio.inf.les.law.component.criticality;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import br.pucrio.inf.les.law.execution.Context;
import br.pucrio.inf.les.law.execution.IDescriptor;
import br.pucrio.inf.les.law.execution.IExecution;
import br.pucrio.inf.les.law.execution.Trigger;
import br.pucrio.inf.les.law.model.Id;
import br.pucrio.inf.les.law.model.LawException;

public class CriticalityAnalysisDescriptor implements IDescriptor{
	private Id id;
	private List<Weight> weigths = new ArrayList<Weight>();
	private List<EventDescriptor> increaseDescriptors = new ArrayList<EventDescriptor>();
	private List<EventDescriptor> decreaseDescriptors = new ArrayList<EventDescriptor>();
	
	public CriticalityAnalysisDescriptor (Id descriptorId){
		this.id = descriptorId;
	}
	
	public void addWeight(Weight weight){
		weigths.add(weight);
	}
	
	public void addIncreaseDescriptors(EventDescriptor increase){
		increaseDescriptors.add(increase);
	}
	
	public void addDecreaseDescriptors(EventDescriptor decrease){
		decreaseDescriptors.add(decrease);
	}
	
	public boolean needContext() {
		return false;
	}

	public boolean shouldCreate(Context context, Map<String, Object> parameters) throws LawException {
		return true;
	}

	public IExecution createExecution(Context context, 
			Map<String, Object> parameters) throws LawException {
		return new CriticalityAnalysisExecution(context,this,weigths,increaseDescriptors,decreaseDescriptors);
	}

	public Id getId() {
		return this.id;
	}

	public Trigger getTrigger() {
		// TODO Auto-generated method stub
		return null;
	}

}
