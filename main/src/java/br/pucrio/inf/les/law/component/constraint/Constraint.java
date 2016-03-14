package br.pucrio.inf.les.law.component.constraint;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;

import br.pucrio.inf.les.law.model.LawException;

/**
 * @author 	 mgatti
 */
public class Constraint{
	private String semantics;
	private String className;
	private String id;
	public Constraint( String id, String className, String semantics ){
		this.id = id;
		this.className = className;
		this.semantics = semantics;
	}
	public String getClassName() {
		return className;
	}
	public String getSemantics() {
		return semantics;
	}
	
	public AbstractConstraint makeConstraint(Map<String, Object> parameters) throws LawException {
		AbstractConstraint condition = null;
		try {
		
			Class aClass = Class.forName(this.getClassName());
			Class[] args = {Map.class};
			Constructor constructor = aClass.getConstructor(args);
			Object[] params = {parameters};
			condition = (AbstractConstraint)constructor.newInstance(params);

		} catch (NoSuchMethodException e) {
			throw new LawException("The constraint's constructor of ["+getClassName()+"] was not found",LawException.CONSTRUCTOR_INVOCATION);
		} catch (InvocationTargetException e) {
			throw new LawException("The constraint's constructor of  ["+getClassName()+"] was not found",LawException.CONSTRUCTOR_INVOCATION);
		} catch (InstantiationException e) {
			throw new LawException("The constraint class specified ["+getClassName()+"] has not a default constructor",LawException.CONSTRUCTOR_INVOCATION);
		} catch (IllegalAccessException e) {
			throw new LawException(e.getMessage(),LawException.ILLEGAL_ACCESS);
		} catch (ClassNotFoundException e) {
			throw new LawException("The constraint class specified ["+getClassName()+"] was not found.",LawException.CLASS_NOT_FOUND);
		} catch (ClassCastException e){
			throw new LawException("The class specified ["+getClassName()+"] does not implement the interface IConstraint.",LawException.CLASS_NOT_AS_EXPECTED);
		}
		return condition;
	}
	
}
