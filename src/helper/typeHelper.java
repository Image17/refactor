package helper;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;

import p.vo.AccessModifiers;
import p.vo.Field;
import p.vo.Type;

public class typeHelper {
	
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
	
	public static void findChildren(String typeName, String packageName) {
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
		
		System.out.println(children);
	}
	
	public static void findParentInterfaces(String typeName, String packageName) {
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
		
		System.out.println(parentInterfaces);
	}
	
	public static void findChildrenInterfaces(String typeName, String packageName) {
		List<Type> childrenInterfaces = new ArrayList<>();
		Type type = TypeHolder.typesByName.get(typeName);
		ArrayDeque<String> dq = new ArrayDeque<>();
		
		for (Type typeIterator : TypeHolder.allTypes) {
			if (typeIterator.implementedInterfaces.contains(typeName)) {
				dq.add(typeIterator.typeName);
			}
		}
		
		while (!dq.isEmpty()) {
			String t = dq.pop();
		}
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
	
	public static boolean foundInParent(String variableName, String packageName, String variableClass) {
		for (Type parent : findParents(variableClass, packageName)) {
			for (Field field : parent.getFields()) {
				if (field.getIdentifier().equals(variableName) && isVisibleParent(field, packageName, parent) == false) {
					// Variable found and is visible to child.
					return true;
				}
			}
		}
		
		// Variable wasn't found in any local or instance field in parent classes.
		return false;
	}
	
	private static boolean isVisibleParent(Field field, String packageName, Type parent) {
		//Determine the child and parent package relationship.
		//All variables can be accessed within same package unless AccessModifier is private.
		//Public variables can only be accessed in different packages.
		if (packageName.equals(parent.packageName) && field.getAccessModifier() != AccessModifiers.PRIVATE) {
			return true;			
		} 
		
		if (packageName.equals(parent.packageName) == false && 
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
