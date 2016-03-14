package br.pucrio.inf.les.law.component.message;

import br.pucrio.inf.les.law.component.role.RoleDescriptor;
import br.pucrio.inf.les.law.execution.DescriptorTable;
import br.pucrio.inf.les.law.model.Id;
import br.pucrio.inf.les.law.model.LawException;
import br.pucrio.inf.les.law.parser.handler.base.AbstractHandler;

import java.util.Hashtable;
import java.util.Stack;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.xml.sax.Attributes;

public class SenderHandler extends AbstractHandler {

    /**
     * Logger for this class
     */
    private static final Log LOG = LogFactory.getLog(SenderHandler.class);

    public SenderHandler(Stack<Object> stack, DescriptorTable table,
            Hashtable<Id, Object> handlerCache) {
        super(stack, table, handlerCache);
    }

    @Override
    public void process(String namespaceURI, String localName, String qName,
            Attributes attr) throws LawException {
        MessageDescriptor message = (MessageDescriptor) stack.peek();
        String roleRef = attr.getValue("role-ref");
        String roleInstance = attr.getValue("role-instance");

        // TODO precisa colocar o role descriptor no handler
        RoleDescriptor roleDescriptor = (RoleDescriptor) handlerCache
                .get(new Id(roleRef));

        
        if (roleDescriptor==null){
            throw new LawException("Role was not declared: "+roleRef,LawException.ROLE_NOT_DECLARED);
        }

        message.setSender(roleDescriptor, roleInstance);
    }

    @Override
    public void solvePendencies() throws LawException {
        // TODO Auto-generated method stub

    }

}
