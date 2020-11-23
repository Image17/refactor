/**
* CSC 600 Assignment #3
*
* On my honor, Tyler Hetland, this assignment is my own work and I have
* not shared my solution with anyone.
*
*/

package p.vo;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author Image17
 *
 */
public class TypeHolder {

	private TypeHolder() {
		this.allTypes = new HashSet<>();
		this.typesByName = new HashMap<>();
	}

	static Set<Type> allTypes;
	public static HashMap<String, Type> typesByName;

	private static TypeHolder singleton = null;

	public static TypeHolder getInstance() {
		if (singleton == null) {
			singleton = new TypeHolder();
		}

		return singleton;
	}

	/**
	 * Gather type visited including type naame, parent object, depth from
	 * java.lang.Object and relative root object that is not java.lang.Object
	 * 
	 * @param typeName
	 * @param parent
	 * @param depth
	 * @param root
	 */
	public static void addType(String typeName, String parent, String root, List<Field> fields,
			List<String> implementedInterfaces, boolean isInterface, String packageName) {
		Type type = new Type(typeName, parent, root, fields, implementedInterfaces, isInterface, packageName);
		allTypes.add(type);
		typesByName.put(typeName, type);
	}

	public static void display() {
		determineChildren();
		for (Type type : allTypes) {
			System.out.println(type);
		}
		
		findParentInterfaces("D", "test");
		
		
		clean();
	}

	/**
	 * Assign children to all nodes, done after visiting to ensure order of classes
	 * visited does not matter
	 */
	private static void determineChildren() {

		// assign children
		for (Type t : allTypes) {
			if (!t.parentName.isEmpty() && !t.parentName.equalsIgnoreCase("Object")) {
				typesByName.get(t.parentName).addChild(t.typeName);
			}
		}
	}

	/**
	 * Wipe out global lists to allow for multiple runs
	 */
	private static void clean() {
		allTypes.clear();
		typesByName.clear();
	}

	private static void findParents(String typeName, String packageName) {
		List<Type> parents = new ArrayList<>();
		Type type = typesByName.get(typeName);
		String parentName = type.parentName;
		// find parent
		// find parents of parent
		if (!"Object".equals(parentName)) {
			Type parent = typesByName.get(parentName);
			while (null != parent) {
				parents.add(parent);
				parent = typesByName.get(parent.parentName);
			}
		}
		
		System.out.println(parents);
	}
	
	private static void findChildren(String typeName, String packageName) {
		List<Type> children = new ArrayList<>();
		Type type = typesByName.get(typeName);
		ArrayDeque<String> dq = new ArrayDeque<>();
		
		if (!type.children.isEmpty()) {
			dq.addAll(type.children);
			
			while (!dq.isEmpty()) {
				String c = dq.pop();
				Type child = typesByName.get(c);
				children.add(child);
				
				if (!child.children.isEmpty()) {
					dq.addAll(child.children);
				}
			}
			
		}
		
		System.out.println(children);
	}
	
	private static void findParentInterfaces(String typeName, String packageName) {
		List<Type> parentInterfaces = new ArrayList<>();
		Type type = typesByName.get(typeName);
		
		ArrayDeque<String> dq = new ArrayDeque<>();
		
		if (!type.implementedInterfaces.isEmpty()) {
			dq.addAll(type.implementedInterfaces);
			
			while (!dq.isEmpty()) {
				String i = dq.pop();
				Type parentInterface = typesByName.get(i);
				parentInterfaces.add(parentInterface);
				
				if (!parentInterface.implementedInterfaces.isEmpty()) {
					dq.addAll(parentInterface.implementedInterfaces);
				}
			}
			
		}
		
		System.out.println(parentInterfaces);
	}
	
	private static void findChildrenInterfaces(String typeName, String packageName) {
		List<Type> childrenInterfaces = new ArrayList<>();
		Type type = typesByName.get(typeName);
		ArrayDeque<String> dq = new ArrayDeque<>();
		
		for (Type typeIterator : allTypes) {
			if (typeIterator.implementedInterfaces.contains(typeName)) {
				dq.add(typeIterator.typeName);
			}
		}
		
		while (!dq.isEmpty()) {
			String t = dq.pop();
		}
	}
	
	private static boolean isChild(Type maybeChild, String typeName) {
		return maybeChild.implementedInterfaces.contains(typeName) || maybeChild.parentName.equals(typeName);
	}

}
