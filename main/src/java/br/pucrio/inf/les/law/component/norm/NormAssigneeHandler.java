package br.pucrio.inf.les.law.component.norm;

import br.pucrio.inf.les.law.component.role.RoleDescriptor;
import br.pucrio.inf.les.law.execution.DescriptorTable;
import br.pucrio.inf.les.law.model.Id;
import br.pucrio.inf.les.law.model.LawException;
import br.pucrio.inf.les.law.parser.handler.base.AbstractHandler;

import java.util.Hashtable;
import java.util.Stack;

import org.xml.sax.Attributes;

public class NormAssigneeHandler extends AbstractHandler {

    public NormAssigneeHandler(Stack<Object> stack, DescriptorTable table,
            Hashtable<Id, Object> handlerCache) {
        super(stack, table, handlerCache);
    }

    @Override
    public void process(String namespaceURI, String localName, String qName,
            Attributes attr) throws LawException {
        NormDescriptor normDescriptor = (NormDescriptor) stack.peek();
        String roleRef = attr.getValue("role-ref");
        String roleInstance = attr.getValue("role-instance");

        RoleDescriptor roleDescriptor = (RoleDescriptor) handlerCache
                .get(new Id(roleRef));
        
        if (roleDescriptor==null){
            throw new LawException("Role was not declared: "+roleRef,LawException.ROLE_NOT_DECLARED);
        }
        
        normDescriptor.addAssignee(roleDescriptor, roleInstance);
    }

    @Override
    public void solvePendencies() throws LawException {
        // TODO Auto-generated method stub

    }

}
