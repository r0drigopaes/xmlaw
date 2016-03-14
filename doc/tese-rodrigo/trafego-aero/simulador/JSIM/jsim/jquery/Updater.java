/******************************************************************
 * @(#) Updater.java   1.3
 *
 * Copyright (c) 2000 John Miller
 * All Right Reserved
 *-----------------------------------------------------------------
 * Permission to use, copy, modify and distribute this software and
 * its documentation without fee is hereby granted provided that
 * this copyright notice appears in all copies.
 * WE MAKE NO REPRESENTATIONS OR WARRANTIES ABOUT THE SUITABILITY 
 * OF THE SOFTWARE, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT
 * LIMITED TO THE IMPLIED WARRANTIES OF MERCHANTABILITY, FITNESS
 * FOR A PARTICULAR PURPOSE, OR NON-INFRINGEMENT. WE SHALL NOT BE
 * LIABLE FOR ANY DAMAGES SUFFERED BY ANY USER AS A RESULT OFUSING,
 * MODIFYING OR DISTRIBUTING THIS SOFTWARE OR ITS DERIVATIVES.
 *-----------------------------------------------------------------
 *  
 * @version 1.3 (12 December 2000)
 * @author  Xueqin Huang, John Miller
 */

package jsim.jquery;

import java.sql.Connection;
import java.util.Vector;

import jsim.jmessage.*;

/***************************************************************
 * The Updater class is called by the DBAcess class to update/store an object.
 * This class is more of an adapter to separate the general database access
 * operations from the specific database storage. The benefit is that changes
 * the object will not require modifications to the general purpose DBAccess
 * class. Instead, only this and the class that deals with the specific storage
 * needs to be changed.
 */
public class Updater 
{
  /***************************************************************
   * Updates an object.
   * @param obj		The object to be updated/stored
   * @exception		java.lang.Exception
   */
   void update (DBAccess dbaccess, Object obj) throws Exception
   {
	if (obj instanceof Store) {
		FederateInserter.insert (dbaccess, obj);
	} else {
		System.out.println ("Updater: unknow update target!");
	}; // if

   }; // update

}; // class Updater
