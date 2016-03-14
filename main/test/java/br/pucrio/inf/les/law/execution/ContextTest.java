package br.pucrio.inf.les.law.execution;

import junit.framework.TestCase;

public class ContextTest extends TestCase {

    public void testParent() {
        DescriptorTable desc = new DescriptorTable();
        ExecutionManager execution = new ExecutionManager(desc);

        Context root = new Context(execution, null);
        Context child1 = new Context(execution, root);
        Context child2 = new Context(execution, root);

        Context grandson = new Context(execution, child1);

        assertNull(root.getParent());
        assertEquals(child1.getParent(), root);
        assertEquals(child2.getParent(), root);
        assertEquals(grandson.getParent(), child1);
    }
}
