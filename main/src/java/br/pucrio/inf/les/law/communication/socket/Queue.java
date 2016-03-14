package br.pucrio.inf.les.law.communication.socket;

import java.util.List;
import java.util.Vector;

public class Queue extends Vector implements IList{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Remove o objeto do início da fila e o retorna
	 * @return O primeiro objeto da fila, null caso a fila esteja vazia.
	 */
	public Object pop(){
		if (this.size()>0){
			Object first = this.get(0);
			this.remove(0);
			return first;
		}else{
			return null;
		}
	}
	
	/**
	 * Adiciona um objeto ao final da fila
	 * @param item
	 */
	public void push(Object item){
		this.add(item);
	}
	
	/**
	 * Adiciona uma lista de objetos ao final da fila.
	 * @param item
	 */
	public void pushAll(List itens){
		this.addAll(itens);
	}
	
	/**
	 * Retorna true se a fila estiver vazia
	 */
	public boolean isEmpty(){
		return this.size()==0;
	}

}
