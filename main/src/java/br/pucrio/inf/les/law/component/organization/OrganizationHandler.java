package br.pucrio.inf.les.law.component.organization;

import java.util.Hashtable;
import java.util.Stack;

import org.xml.sax.Attributes;

import br.pucrio.inf.les.law.execution.DescriptorTable;
import br.pucrio.inf.les.law.model.Id;
import br.pucrio.inf.les.law.model.LawException;
import br.pucrio.inf.les.law.parser.handler.base.ComposedHandler;

public class OrganizationHandler extends ComposedHandler{

    public OrganizationHandler(Stack<Object> stack, DescriptorTable table, Hashtable<Id, Object> handlerCache) {
        super(stack, table, handlerCache);
    }

    @Override
    public void process(String namespaceURI, String localName, String qName, Attributes attr) throws LawException {
        
        String name = attr.getValue("name");
        
        OrganizationDescriptor organizatioDescritor = new OrganizationDescriptor(getId(), name);
        
        stack.push(organizatioDescritor);
        table.add(organizatioDescritor);
    }

    @Override
    public void solvePendencies() throws LawException {
        // TODO Auto-generated method stub
        
    }

}
