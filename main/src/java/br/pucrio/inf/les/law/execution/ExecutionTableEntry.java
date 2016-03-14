package br.pucrio.inf.les.law.execution;

import br.pucrio.inf.les.law.model.Id;

class ExecutionTableEntry {

	private Id executionId;

	private Id definitionId;

	private IExecution value;
	
	private Context context;

	public ExecutionTableEntry(Id executionId, Id definitionId, IExecution value, Context context) {
		this.executionId = executionId;
		this.definitionId = definitionId;
		this.value = value;
		this.context = context;
	}
	
	public IExecution getValue(){
		return this.value;
	}
	
	public boolean hasEqualsContext(Context context){
		return this.context.equals(context);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof ExecutionTableEntry) {
			ExecutionTableEntry entry = (ExecutionTableEntry) obj;
			return entry.executionId.equals(this.executionId);
		}
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return this.executionId.hashCode();
	}

}
