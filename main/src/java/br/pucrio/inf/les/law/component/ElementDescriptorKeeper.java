package br.pucrio.inf.les.law.component;

import br.pucrio.inf.les.law.execution.IDescriptor;

import java.util.ArrayList;
import java.util.List;


/**
 * @author mgatti
 */
public abstract class ElementDescriptorKeeper{

		private List<IDescriptor> activatedDescriptors = new ArrayList<IDescriptor>();
		private List<IDescriptor> deactivatedDescriptors = new ArrayList<IDescriptor>();
		
		
		/**
		 * Adiciona um descriptor (norma/action) que precisa estar desabilitado
		 */
		public void addDeactivatedDescriptor(IDescriptor deactivatedDescriptor){
			this.deactivatedDescriptors.add(deactivatedDescriptor);
		}	

		public  List<IDescriptor> getDeactivatedDescriptors(){
			return this.deactivatedDescriptors;
		}

		
		/**
		 * Adiciona um descriptor (norma) que precisa estar habilitado
		 */	
		public void addActivatedDescriptor(IDescriptor activatedForDescriptor){
			this.activatedDescriptors.add(activatedForDescriptor);
		}
		public List<IDescriptor> getActivatedDescriptors(){
			return this.activatedDescriptors;
		}

}
