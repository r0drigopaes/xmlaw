package br.pucrio.inf.les.law.model;

import junit.framework.TestCase;

public class IdTest extends TestCase {

    public void testEquals() {
        Id id = new Id(9);
        Id id2 = new Id(9);
        assertEquals(id, id2);
    }

    public void testNew() {
        Id id = new Id();
        assertNotNull(id.getAsString());
    }

    public void testGetAsString() {
        Id id = new Id(9);
        Id id2 = new Id(9);

        assertEquals(id.getAsString(), id2.getAsString());
    }
}
