/* ****************************************************************
 *   @filename:		LawElementId.java
 *   @projectname:  Law
 *   @date:			Sep 8, 2004
 *   @author 		Rodrigo - LES (PUC-Rio)
 * 	 
 *   $Id$
 * ***************************************************************/
package br.pucrio.inf.les.law.model;

import java.io.Serializable;

import br.pucrio.inf.les.law.util.IdGenerator;

/**
 * @author Rodrigo - LES (PUC-Rio)
 * @version $Revision$
 */
public class Id implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    private String id = null;

    public Id() {
        this(IdGenerator.getInstance().getNewId());
    }

    public Id(String id) {
        this.id = id;
    }

    public Id(int id) {
        this.id = Integer.toString(id);
    }

    /*
     * @see java.lang.Object#equals(java.lang.Object)
     */
    public boolean equals(Object obj) {
        try {
            return id.equals(((Id) obj).id);
        } catch (Exception e) {
            return false;
        }
    }

    /*
     * @see java.lang.Object#hashCode()
     */
    public int hashCode() {
        return id.hashCode();
    }

    public String getAsString() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String toString() {
        return id;
    }

    
}

/*******************************************************************************
 * UPDATE LOG: $Log$
 * UPDATE LOG: Revision 1.2  2006/02/01 20:18:45  lfrodrigues
 * UPDATE LOG: mudificações no pacote model para melhor modularização do projeto em api cliente e servidor
 * UPDATE LOG:
 * UPDATE LOG: Revision 1.1  2005/12/05 18:29:48  rbp
 * UPDATE LOG: Versão inicial
 * UPDATE LOG: Revision 1.1 2005/08/17 17:48:14 lfrodrigues
 * instalacao do maven como ferramenta de build automatico Revision 1.1
 * 2005/05/10 16:22:57 guga Inicio Refactoring Revision 1.6 2005/04/29 21:40:55
 * guga Modificações na estrutura e primeira versao da implementacao do TAC SCM
 * Revision 1.5 2004/12/16 16:08:35 rbp Included scene lifecycle and also the
 * specification of who can create and participate of a scene. Revision 1.4
 * 2004/12/14 21:00:19 rbp Constructor removed ... no usage Revision 1.3
 * 2004/11/24 01:06:04 rbp Main modifications: - Allow multiple instances of the
 * same scene - Introducing a 3 layer mapping from XML: XML --> Descriptor Layer
 * --> Model Layer - Framework of actions introduced; now it is possible use
 * java code as consequence of some law configuration. - Implementation of a
 * case study in the context of product trading using SWT and JFace ( it
 * requires to configure the eclipse for running the application) - Some bugs
 * were fixed altough I don't remember of them. Revision 1.1 2004/09/22 01:04:38
 * rbp This version contains severals modifications: - Package renaming -
 * Specification of laws from XML language - Added a Sax based Xml parser for
 * reading from XML - Some hard-coded code were removed and placed in the
 * Config.properties file - Added the simple application example Revision 1.1
 * 2004/09/10 17:49:44 rbp Main modifications: Message is not an event anymore.
 * The information can be carried through the InfoCarrier object. TriggerManager
 * complete modification. Now it allows multiple norm and clock instances.
 ******************************************************************************/