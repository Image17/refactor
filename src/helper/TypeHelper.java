package helper;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;

import p.vo.AccessModifiers;
import p.vo.Field;
import p.vo.LocalField;
import p.vo.Type;

public class TypeHelper {
	
	public static List<Type> findParents(String typeName, String packageName) {
		List<Type> parents = new ArrayList<>();
		Type type = TypeHolder.typesByName.get(typeName);
		String parentName = type.parentName;
		// find parent
		// find parents of parent
		if (!"Object".equals(parentName)) {
			Type parent = TypeHolder.typesByName.get(parentName);
			while (null != parent) {
				parents.add(parent);
				parent = TypeHolder.typesByName.get(parent.parentName);
			}
		}
		
		//System.out.println(parents);
		return parents;
	}
	
	public static List<Type> findChildren(String typeName, String packageName) {
		List<Type> children = new ArrayList<>();
		Type type = TypeHolder.typesByName.get(typeName);
		ArrayDeque<String> dq = new ArrayDeque<>();
		
		if (!type.children.isEmpty()) {
			dq.addAll(type.children);
			
			while (!dq.isEmpty()) {
				String c = dq.pop();
				Type child = TypeHolder.typesByName.get(c);
				children.add(child);
				
				if (!child.children.isEmpty()) {
					dq.addAll(child.children);
				}
			}
			
		}
		
		return children;
	}
	
	public static List<Type> findParentInterfaces(String typeName, String packageName) {
		List<Type> parentInterfaces = new ArrayList<>();
		Type type = TypeHolder.typesByName.get(typeName);
		
		ArrayDeque<String> dq = new ArrayDeque<>();
		
		if (!type.implementedInterfaces.isEmpty()) {
			dq.addAll(type.implementedInterfaces);
			
			while (!dq.isEmpty()) {
				String i = dq.pop();
				Type parentInterface = TypeHolder.typesByName.get(i);
				parentInterfaces.add(parentInterface);
				
				if (!parentInterface.implementedInterfaces.isEmpty()) {
					dq.addAll(parentInterface.implementedInterfaces);
				}
			}
			
		}
		
		return parentInterfaces;
	}
	
	public static List<Type> findChildrenInterfaces(String typeName, String packageName) {
		List<Type> childrenInterfaces = new ArrayList<>();
		ArrayDeque<String> dq = new ArrayDeque<>();
		
		for (Type typeIterator : TypeHolder.allTypes) {
			if (typeIterator.implementedInterfaces.contains(typeName)) {
				dq.add(typeIterator.typeName);
				childrenInterfaces.add(typeIterator);
			}
		}
		
		while (!dq.isEmpty()) {
			
			Type child = TypeHolder.typesByName.get(dq.pop());
			
			for (Type type : TypeHolder.allTypes) {
				if (isChild(type, child.typeName)) {
					dq.add(type.typeName);
					childrenInterfaces.add(type);
				}
			}			
		}
		
		return childrenInterfaces;
	}
	
	public static boolean findVariable(String variableName, String packageName, String variableClass) {
		//TODO: Edit typesByName key to be an object which stores the package and class.
		Type type = TypeHolder.typesByName.get(variableClass);
		for (Field field : type.getFields()) {
			if (variableName.equals(field.getIdentifier())) {
				return true;
			}
		}
		
		// Variable wasn't found.
		return false;
	}
	
	public static boolean isShadowed(String variableName, String packageName, String variableClass) {
		
		//TODO handle interfaces
		if (foundInParent(variableName,packageName,variableClass) || foundInChild(variableName,packageName,variableClass)) {
			return true;
		}
		if (foundInParentInterface(variableName,packageName,variableClass) || foundInChildInterface(variableName,packageName,variableClass)) {
			return true;
		}
		
		return false;
	}
	
	public static boolean foundInParent(String variableName, String packageName, String variableClass) {
		for (Type parent : findParents(variableClass, packageName)) {
			for (Field field : parent.getFields()) {
				if (field.getIdentifier().equals(variableName) && isVisible(field, packageName, parent) == true) {
					// Variable found and is visible to child.
					return true;
				}
			}
		}
		
		// Variable wasn't found in any local or instance field in parent classes.
		return false;
	}
	
	public static boolean foundInChild(String variableName, String packageName, String variableClass) {
		for (Type child : findChildren(variableClass, packageName)) {
			for (Field field : child.getFields()) {
				if (field.getIdentifier().equals(variableName) && isVisible(field, packageName, child) == true) {
					// Variable found and is visible to child.
					return true;
				}
			}
			
			for (LocalField field : child.getLocalFields()) {
				if (field.getIdentifier().equals(variableName)) {
					// Variable found and is visible to child.
					return true;
				}
			}
		}
		
		return false;
	}
	
	public static boolean foundInParentInterface(String variableName, String packageName, String variableClass) {
		for (Type parent : findParentInterfaces(variableClass, packageName)) {
			for (Field field : parent.getFields()) {
				if (field.getIdentifier().equals(variableName) && isVisible(field, packageName, parent) == true) {
					// Variable found and is visible to child.
					return true;
				}
			}
		}
		
		// Variable wasn't found in any local or instance field in parent classes.
		return false;
	}
	
	public static boolean foundInChildInterface(String variableName, String packageName, String variableClass) {
		for (Type child : findChildrenInterfaces(variableClass, packageName)) {
			for (Field field : child.getFields()) {
				if (field.getIdentifier().equals(variableName) && isVisible(field, packageName, child) == true) {
					// Variable found and is visible to child.
					return true;
				}
			}
			
			for (LocalField field : child.getLocalFields()) {
				if (field.getIdentifier().equals(variableName)) {
					// Variable found and is visible to child.
					return true;
				}
			}
		}
		
		return false;
	}
	
	private static boolean isVisible(Field field, String packageName, Type type) {
		//Determine the child and parent package relationship.
		//All variables can be accessed within same package unless AccessModifier is private.
		//Public variables can only be accessed in different packages.
		if (packageName.equals(type.packageName) && field.getAccessModifier() != AccessModifiers.PRIVATE) {
			return true;			
		} 
		
		if (packageName.equals(type.packageName) == false && 
				(field.getAccessModifier() != AccessModifiers.PRIVATE && 
					field.getAccessModifier() != AccessModifiers.NO_MODIFIER)) {
			return true;			
		} 
				
		
		return false;
	}

	private static boolean isChild(Type maybeChild, String typeName) {
		return maybeChild.implementedInterfaces.contains(typeName) || maybeChild.parentName.equals(typeName);
	}

}
