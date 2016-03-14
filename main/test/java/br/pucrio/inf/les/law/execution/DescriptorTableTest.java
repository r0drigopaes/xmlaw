package br.pucrio.inf.les.law.execution;

import junit.framework.TestCase;

public class DescriptorTableTest extends TestCase{
    
    public void testAddAndGet(){
        DescriptorTable table = new DescriptorTable();
        IDescriptor descriptor = new StubDescriptor();
        
        table.add(descriptor);
        
        assertEquals( descriptor, table.get(descriptor.getId()) );
    }

}
