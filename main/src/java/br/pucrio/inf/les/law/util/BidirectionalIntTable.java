/* ****************************************************************
 *   @filename:		BidirectionalIntTable.java
 *   @projectname:  Law
 *   @date:			Nov 8, 2004
 *   @author 		Rodrigo - LES (PUC-Rio)
 * 	 
 *   $Id$
 * ***************************************************************/
package br.pucrio.inf.les.law.util;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Rodrigo - LES (PUC-Rio)
 * @version $Revision$
 */
public class BidirectionalIntTable {

    private BidirectionalTable table = new BidirectionalTable();

    /**
     * Keeps a cache of all the keys for the table
     */
    private ArrayList<Integer> keysCache = new ArrayList<Integer>();

    public void put(int key, Object value) {
        table.put(new Integer(key), value);
        keysCache.add(key);
    }

    public Object getByKey(int key) {
        return table.getByKey(new Integer(key));
    }

    public int getByValue(Object value) {
        return ((Integer) table.getByValue(value)).intValue();
    }

    public List<Integer> getKeys() {
        return keysCache;
    }
}

/*******************************************************************************
 * UPDATE LOG: $Log$
 * UPDATE LOG: Revision 1.1  2005/12/05 18:29:48  rbp
 * UPDATE LOG: Versão inicial
 * UPDATE LOG: Revision 1.1 2005/08/17
 * 17:48:14 lfrodrigues instalacao do maven como ferramenta de build automatico
 * Revision 1.1 2005/05/10 16:22:59 guga Inicio Refactoring Revision 1.2
 * 2005/04/29 21:40:55 guga Modificações na estrutura e primeira versao da
 * implementacao do TAC SCM Revision 1.1 2004/11/24 01:06:04 rbp Main
 * modifications: - Allow multiple instances of the same scene - Introducing a 3
 * layer mapping from XML: XML --> Descriptor Layer --> Model Layer - Framework
 * of actions introduced; now it is possible use java code as consequence of
 * some law configuration. - Implementation of a case study in the context of
 * product trading using SWT and JFace ( it requires to configure the eclipse
 * for running the application) - Some bugs were fixed altough I don't remember
 * of them.
 ******************************************************************************/
