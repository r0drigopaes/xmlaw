/******************************************************************
 * Copyright (c) 2005, John Miller
 * All rights reserved.
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *   
 * 1. Redistributions of source code must retain the above
 *    copyright notice, this list of conditions and the following
 *    disclaimer. 
 * 2. Redistributions in binary form must reproduce the above
 *    copyright notice, this list of conditions and the following
 *    disclaimer in the documentation and/or other materials
 *    provided with the distribution. 
 * 3. Neither the name of the University of Georgia nor the names
 *    of its contributors may be used to endorse or promote
 *    products derived from this software without specific prior
 *    written permission. 
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND 
 * CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, 
 * INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF 
 * MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE 
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR 
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, 
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, 
 * BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR 
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
 * HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR 
 * OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, 
 * EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 * @(#) AnimationMessage.java
 * @version     1.3, 28 Jan 2005
 * @author	Matt Perry
 */

 package jsim.animator;

 import javax.swing.*;
 import java.awt.*;
 import java.lang.*;

 /************************************************************************
  * This class implements a message which is passed from a Simulation to an
  * Animation in the JSIM simulation package.
  * A message specifies one of the operations defined in the Animator interface 
  */

 public class AnimationMessage {

	private int id; 			//unique identifier for entity 
	private Object [] parameters; 	//other parameters for the command 
	private double time;  			//time in simulation 
	private String operation;  		//"create" , transform Operations , "destroy"

  /************************************************************************
   * Constructor for AnimationMessage
   * @param id 		id of entity 
   * @param parameters 	other parameters for the command
   * @param time	timestamp for message
   * @param operation	operation to perform
   */

   public AnimationMessage (int id, Object [] parameters, double time, String operation) {
	
	this.id = id;
	this.parameters = parameters;
	this.time = time;
	this.operation = operation;
   } //Constructor

  /**************************************************************************
   * This method compares 2 AnimationMessage objects to see which one has
   * the most recent timestamp
   * @param am		The AnimationMessage to compare this to
   * @return	0 if equal, >0 if this is newer, <0 if this is older
   */

   public int CompareTo (AnimationMessage am) {

	if (this.time < am.getTime()) {
		return -1;
	   }
	if (this.time > am.getTime()) {
		return 1;
	   }
	return 0;
   } //CompareTO

  /***************************************************************************
   * This method Compares to AnimatiorMessages to determine if they are equivalent
   * @param am 	The AnimationMessage to compare this to
   * @return true if they are equivalent false otherwise
   */

   public boolean equals (AnimationMessage am) {
	
	return (this.id == am.getId());
   } //equals

  /****************************************************************************
   * @return id of this AnimationMessage
   */

   public int getId() {
	return this.id;
   }

  /****************************************************************************
   * @return parameters of this AnimationMessage
   */
 
   public Object [] getParameters() {
	return this.parameters;
   }

  /*****************************************************************************
   * @return time of this AnimationMessage
   */

   public double getTime() {
	return this.time;
   }

  /*****************************************************************************
   * @return operation of this AnimationMessage
   */

   public String getOperation() {
	return this.operation;
   }

  /*****************************************************************************
   * Serializes this AnimationMessage for debugging purposes
   * @return String value for this AnimationMessage
   */

   public String toString() {

	String str = "Entity " + id + 
		     " : time " + time + " : operation " + operation;
	return str;
   } //toString()

 } //AnimationMessage 
