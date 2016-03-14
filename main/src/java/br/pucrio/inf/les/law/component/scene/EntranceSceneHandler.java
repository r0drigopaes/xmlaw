package br.pucrio.inf.les.law.component.scene;

import java.util.HashSet;
import java.util.Hashtable;
import java.util.Stack;

import org.xml.sax.Attributes;

import br.pucrio.inf.les.law.component.protocol.State;
import br.pucrio.inf.les.law.component.role.RoleDescriptor;
import br.pucrio.inf.les.law.execution.DescriptorTable;
import br.pucrio.inf.les.law.model.Id;
import br.pucrio.inf.les.law.model.LawException;
import br.pucrio.inf.les.law.parser.handler.base.ComposedHandler;

public class EntranceSceneHandler extends ComposedHandler 
{
	public EntranceSceneHandler(Stack<Object> stack, DescriptorTable table, Hashtable<Id, Object> handlerCache) {
		super(stack, table, handlerCache);		
	}

	@Override
	public void process(String namespaceURI, String localName, String qName,Attributes attr) throws LawException 
	{
		SceneDescriptor sceneDescriptor = (SceneDescriptor)stack.peek();
		String role_ref = attr.getValue("role_ref");
		String limit = attr.getValue("limit");
		
		if(role_ref == null)
		{
			throw new LawException("Could not find role_ref attribute in scene ["+ sceneDescriptor.getId() +"] entrance",LawException.ROLE_NOT_DECLARED);
		}
		
		if(limit == null)
		{
			throw new LawException("Could not find limit attribute in scene ["+ sceneDescriptor.getId() +"] entrance",LawException.MISSING_CONFIGURATION);
		}
		
		RoleDescriptor roleDescriptor = (RoleDescriptor)handlerCache.get(new Id(role_ref));
		
		sceneDescriptor.addEntranceRole(roleDescriptor,new HashSet<State>(), new Integer(limit));
		
		stack.push(roleDescriptor);
	}

	public void solvePendencies() throws LawException 
	{
	

	}

}
