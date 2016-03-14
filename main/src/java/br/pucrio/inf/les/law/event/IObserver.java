/* ****************************************************************
 *   @filename:     IObserver.java
 *   @projectname:  Law
 *   @date:         Jun 23, 2004
 *   @author        rbp
 *
 *   $Id$
 * ***************************************************************/
package br.pucrio.inf.les.law.event;

import br.pucrio.inf.les.law.model.LawException;

/**
 * Implementa o padrão Observer. Ver GoF[95].
 * 
 * @alias IObserver
 * @author rbp & guga - LES (PUC-Rio)
 * @version $Revision$
 */
public interface IObserver {

    /**
     * Método que é invocado pelo ISubject avisando da ocorrência de um
     * determinado evento.
     * 
     * @param event
     *            o evento ocorrido.
     * @throws LawException
     * @author rbp
     */
    void update(Event event) throws LawException;
    
    void notifySubjectDeath(Subject subject);
}