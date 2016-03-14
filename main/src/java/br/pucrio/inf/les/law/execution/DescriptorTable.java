package br.pucrio.inf.les.law.execution;

import br.pucrio.inf.les.law.model.Id;

import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class DescriptorTable {
	/**
	 * Logger for this class
	 */
	private static final Log LOG = LogFactory.getLog(DescriptorTable.class);

	private Hashtable<Id, IDescriptor> table = new Hashtable<Id, IDescriptor>();

	public void add(IDescriptor descriptor){
		assert(descriptor!=null);
		assert(descriptor.getId()!=null);
		
		if (this.table.containsValue(descriptor) || this.table.containsKey(descriptor.getId())){
			LOG.warn("Something is trying to add the same descriptor in the descriptor table. Descriptor: "+descriptor);
			return;
		}		
		
		this.table.put(descriptor.getId(),descriptor);
	}

	public IDescriptor get(Id descriptorId) {
        IDescriptor descriptor = this.table.get(descriptorId);
		if (descriptor==null){
			LOG.warn("Attempt to retrieve a descriptor that was not added to the definition table. Id: "+ descriptorId);
		}
		return descriptor;
	}
	
	public List<IDescriptor> getAllDescriptors()
	{
		return new LinkedList<IDescriptor>(table.values());
	}

}
