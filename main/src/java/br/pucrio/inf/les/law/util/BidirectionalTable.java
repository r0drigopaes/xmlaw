/* ****************************************************************
 *   @filename:		BidirectionalTable.java
 *   @projectname:  Law
 *   @date:			Sep 15, 2004
 *   @author 		Rodrigo - LES (PUC-Rio)
 * 	 
 *   $Id$
 * ***************************************************************/
package br.pucrio.inf.les.law.util;

import java.util.Collection;
import java.util.Hashtable;

/**
 * @author Rodrigo - LES (PUC-Rio)
 * @version $Revision$
 */
public class BidirectionalTable {

    private Hashtable<Object,Object> hash = new Hashtable<Object,Object>();

    private Hashtable<Object,Object> invHash = new Hashtable<Object,Object>();

    public void put(Object key, Object value) {
        hash.put(key, value);
        invHash.put(value, key);
    }

    public Object getByKey(Object key) {
        return hash.get(key);
    }

    public Object getByValue(Object value) {
        return invHash.get(value);
    }
    
    public Collection getKeys(){
        return hash.keySet();
    }

}

/*******************************************************************************
 * UPDATE LOG: $Log$
 * UPDATE LOG: Revision 1.1  2005/12/05 18:29:48  rbp
 * UPDATE LOG: Versão inicial
 * UPDATE LOG: Revision 1.1 2005/08/17
 * 17:48:14 lfrodrigues instalacao do maven como ferramenta de build automatico
 * Revision 1.1 2005/05/10 16:22:59 guga Inicio Refactoring Revision 1.3
 * 2004/11/24 01:06:04 rbp Main modifications: - Allow multiple instances of the
 * same scene - Introducing a 3 layer mapping from XML: XML --> Descriptor Layer
 * --> Model Layer - Framework of actions introduced; now it is possible use
 * java code as consequence of some law configuration. - Implementation of a
 * case study in the context of product trading using SWT and JFace ( it
 * requires to configure the eclipse for running the application) - Some bugs
 * were fixed altough I don't remember of them. Revision 1.1 2004/09/22 01:04:37
 * rbp This version contains severals modifications: - Package renaming -
 * Specification of laws from XML language - Added a Sax based Xml parser for
 * reading from XML - Some hard-coded code were removed and placed in the
 * Config.properties file - Added the simple application example
 ******************************************************************************/