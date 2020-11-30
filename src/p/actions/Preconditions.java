package p.actions;

import p.vo.Type;
import helper.TypeHolder;
import helper.typeHelper;
import p.vo.Field;

public class Preconditions {
	private static String prevName = "";
	private static String newName = "";
	private static String variableClass = "";
	private static String variablePackage = "";
	
	public Preconditions(String prevName, String newName, String variableClass, String variablePackage) {
		this.prevName = prevName;
		this.newName = newName;
		this.variableClass = variableClass;
		this.variablePackage = variablePackage;
	}
	
	public static boolean checkPreconditions() {
		/*
		 * Precondition checks:
		 * 	1) Variable being renamed must exist.
		 *  2) Renaming variable must not result in a duplicate name.
		 *  3) Renaming variable must not result in variable shadowing.
		 * */
		
		if (variableExists() == true) {
			System.out.println("Fail: Provided variable doesn't exist within the provided package and class.");
			return false;
		}
		
		if (isDuplicateName() == true) {
			System.out.println("Fail: Renaming this variable would result in a duplicate name.");
			return false;
		}
		
		if (isShadowed() == true) {
			System.out.println("Fail: Renaming this variable would result in variable shadowing.");
			return false;
		}
		
		
		// All precondition checks have been satisfied.
		return true;
	}
	
	
	
	private static boolean variableExists() {
		//Determine if the variable exists within the provided package and class.
		return typeHelper.findVariable(prevName, variablePackage, variableClass);
	}
	
	private static boolean isDuplicateName() { 
		//Determine if the new variable name already exists within the provided class.
		return typeHelper.findVariable(newName, variablePackage, variableClass); 
	}
		
	
	private static boolean isShadowed() {
		//Determine if the new variable name already exists within
		return true; 	
	}
	
	
	

}
