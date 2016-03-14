package br.pucrio.inf.les.law.util;

import br.pucrio.inf.les.law.util.IdGenerator;
import junit.framework.TestCase;

public class IdGeneratorTest extends TestCase{

    public void testGetNewId(){
        IdGenerator generator = IdGenerator.getInstance();
        int id1 = generator.getNewId();
        int id2 = generator.getNewId();
        
        assertTrue( id1 == id2-1);
    }
}
