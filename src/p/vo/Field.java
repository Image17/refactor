package p.vo;

public class Field {


	String identifier;
	AccessModifiers accessModifier;
	
	public Field(String id, AccessModifiers accessModifier) {
		this.identifier = id;
		this.accessModifier = accessModifier;
	}
	
	
	@Override
	public String toString() {
		return "Field [identifier=" + identifier + ", accessModifier=" + accessModifier + "]";
	}

}
