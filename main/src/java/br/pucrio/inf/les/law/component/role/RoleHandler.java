package br.pucrio.inf.les.law.component.role;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.Hashtable;
import java.util.Stack;

import org.xml.sax.Attributes;

import br.pucrio.inf.les.law.component.organization.OrganizationDescriptor;
import br.pucrio.inf.les.law.execution.DescriptorTable;
import br.pucrio.inf.les.law.model.Id;
import br.pucrio.inf.les.law.model.LawException;
import br.pucrio.inf.les.law.parser.handler.base.AbstractHandler;

public class RoleHandler extends AbstractHandler{
    /**
     * Logger for this class
     */
    private static final Log LOG = LogFactory.getLog(RoleHandler.class);

    public RoleHandler(Stack<Object> stack, DescriptorTable table, Hashtable<Id, Object> handlerCache) {
        super(stack, table, handlerCache);
    }

    @Override
    public void process(String namespaceURI, String localName, String qName, Attributes attr) throws LawException 
    {    	
    	RoleDescriptor roleDescriptor = new RoleDescriptor(getId());
        handlerCache.put(roleDescriptor.getId(),roleDescriptor);    
        
        OrganizationDescriptor organizationDescriptor = (OrganizationDescriptor)stack.peek();
        organizationDescriptor.addRoleDescriptors(roleDescriptor);
    }

    @Override
    public void solvePendencies() throws LawException {
        // TODO Auto-generated method stub
        
    }

}
