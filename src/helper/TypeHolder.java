/**
* CSC 600 Assignment #3
*
* On my honor, Tyler Hetland, this assignment is my own work and I have
* not shared my solution with anyone.
*
*/

package helper;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import p.vo.Field;
import p.vo.Type;

/**
 * @author Image17
 *
 */
public class TypeHolder {

	private TypeHolder() {
		this.allTypes = new HashSet<>();
		this.typesByName = new HashMap<>();
	}

	public static Set<Type> allTypes;
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
		
		typeHelper.findParentInterfaces("D", "test");
		
		
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

	

}
