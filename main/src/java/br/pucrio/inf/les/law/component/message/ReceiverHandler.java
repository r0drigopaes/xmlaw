package br.pucrio.inf.les.law.component.message;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.pucrio.inf.les.law.component.role.RoleDescriptor;
import br.pucrio.inf.les.law.execution.DescriptorTable;
import br.pucrio.inf.les.law.model.Id;
import br.pucrio.inf.les.law.model.LawException;
import br.pucrio.inf.les.law.parser.handler.base.AbstractHandler;

import java.util.Hashtable;
import java.util.Stack;

import org.xml.sax.Attributes;

public class ReceiverHandler extends AbstractHandler {
    /**
     * Logger for this class
     */
    private static final Log LOG = LogFactory.getLog(ReceiverHandler.class);

    public ReceiverHandler(Stack<Object> stack, DescriptorTable table,
            Hashtable<Id, Object> handlerCache) {
        super(stack, table, handlerCache);
    }

    @Override
    public void process(String namespaceURI, String localName, String qName,
            Attributes attr) throws LawException {
        MessageDescriptor message = (MessageDescriptor) stack.peek();
        String roleRef = attr.getValue("role-ref");
        String roleInstance = attr.getValue("role-instance");
        String strMultiplicity = attr.getValue("multiplicity");
        
        //TODO Definir constantes para as multiplicidades. 
        int multiplicity = 1;
        if (strMultiplicity!=null){
            Integer.parseInt(strMultiplicity);
        }else{
            LOG.debug("Receiver "+roleRef+" has no multiplicity defined, assuming 1");
        }

        // TODO precisa colocar o role descriptor no handler
        RoleDescriptor roleDescriptor = (RoleDescriptor) handlerCache
                .get(new Id(roleRef));
        
        if (roleDescriptor==null){
            throw new LawException("Role was not declared: "+roleRef,LawException.ROLE_NOT_DECLARED);
        }
        
        message.addReceiver(roleDescriptor, roleInstance, multiplicity);
    }

    @Override
    public void solvePendencies() throws LawException {
        // TODO Auto-generated method stub

    }

}
