package br.pucrio.inf.les.law.component.scene;

import java.util.Hashtable;
import java.util.Stack;

import org.xml.sax.Attributes;

import br.pucrio.inf.les.law.component.protocol.State;
import br.pucrio.inf.les.law.component.role.RoleDescriptor;
import br.pucrio.inf.les.law.execution.DescriptorTable;
import br.pucrio.inf.les.law.model.Id;
import br.pucrio.inf.les.law.model.LawException;
import br.pucrio.inf.les.law.parser.handler.base.AbstractHandler;

public class EntranceSceneStateHandler extends AbstractHandler 
{
	public EntranceSceneStateHandler(Stack<Object> stack, DescriptorTable table, Hashtable<Id, Object> handlerCache) {
		super(stack, table, handlerCache);		
	}

	@Override
	public void process(String namespaceURI, String localName, String qName,Attributes attr) throws LawException 
	{
		String stateRef = attr.getValue("ref");		
		State state = (State)handlerCache.get(new Id(stateRef));
		
		RoleDescriptor roleDescriptor = (RoleDescriptor)stack.pop();		
		SceneDescriptor sceneDescriptor = (SceneDescriptor)stack.peek();
		
		sceneDescriptor.addStateToEntranceRole(roleDescriptor,state);
		
		stack.push(roleDescriptor);
	}

	@Override
	public void solvePendencies() throws LawException {
		// TODO Auto-generated method stub

	}

}
