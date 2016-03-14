package br.pucrio.inf.les.law.component.scene;

import java.util.Hashtable;
import java.util.Stack;

import org.xml.sax.Attributes;

import br.pucrio.inf.les.law.component.role.RoleDescriptor;
import br.pucrio.inf.les.law.execution.DescriptorTable;
import br.pucrio.inf.les.law.model.Id;
import br.pucrio.inf.les.law.model.LawException;
import br.pucrio.inf.les.law.parser.handler.base.AbstractHandler;

public class CreatorSceneHandler extends AbstractHandler 
{
	
	
	public CreatorSceneHandler(Stack<Object> stack, DescriptorTable table, Hashtable<Id, Object> handlerCache) {
		super(stack, table, handlerCache);		
	}

	public void process(String namespaceURI, String localName, String qName,Attributes attr) throws LawException 
	{
		SceneDescriptor sceneDescriptor = (SceneDescriptor)stack.peek();
		String role_ref = attr.getValue("role_ref");
		
		if(role_ref == null)
		{
			throw new LawException("Could not find role_ref attribute for scene ["+ sceneDescriptor.getId() + "] creator",LawException.ROLE_NOT_DECLARED);
		}
		
		RoleDescriptor roleDescriptor = (RoleDescriptor)handlerCache.get(new Id(role_ref));
		sceneDescriptor.addCreationRole(roleDescriptor);
	}

	public void solvePendencies() throws LawException 
	{	
	}

}
