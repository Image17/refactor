/**
* CSC 600 Assignment #3
*
* On my honor, Tyler Hetland, this assignment is my own work and I have
* not shared my solution with anyone.
*
*/

package p.vo;

import java.util.ArrayList;
import java.util.List;

public class Type {

	public String typeName;
	public String parentName;
	public String root;
	public List<String> children;
	public List<Field> fields;
	public List<String> implementedInterfaces;
	public List<LocalField> localFields;
	public boolean isInterface;
	public String packageName;
	
	public Type(String name, String parentName, String root, List<Field> fields, List<String> implementedInterfaces,
			boolean isInterface, String packageName) {
		this.typeName = name;
		this.parentName = parentName;
		this.children = new ArrayList<>();
		this.root = root;
		this.fields = fields;
		this.implementedInterfaces = implementedInterfaces;
		this.isInterface = isInterface;
		localFields = new ArrayList<>();
		this.packageName = packageName;
	}

	public void addChild(String child) {
		children.add(child);
	}
	
	public void addLocalField(LocalField localField) {
		localFields.add(localField);
	}
	
	public List<Field> getFields() { 
		return this.fields; 
	}

	// auto-gen methods for HashSet use
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((typeName == null) ? 0 : typeName.hashCode());
		return result;
	}

	@Override
	public String toString() {
		return "Type [typeName=" + typeName + ", parentName=" + parentName + ", root=" + root + ", children=" + children
				+ ", fields=" + fields + ", implementedInterfaces=" + implementedInterfaces + ", localFields="
				+ localFields + ", isInterface=" + isInterface + ", packageName=" + packageName + "]";
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Type other = (Type) obj;
		if (children == null) {
			if (other.children != null)
				return false;
		} else if (!children.equals(other.children))
			return false;
		if (typeName == null) {
			if (other.typeName != null)
				return false;
		} else if (!typeName.equals(other.typeName))
			return false;
		return true;
	}

}
