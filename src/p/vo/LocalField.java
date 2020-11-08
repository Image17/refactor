package p.vo;

public class LocalField {
	String identifier;
	
	public LocalField(String identifier) {
		this.identifier = identifier;
	}

	public String getIdentifier() {
		return identifier;
	}

	public void setIdentifier(String identifier) {
		this.identifier = identifier;
	}

	@Override
	public String toString() {
		return "LocalField [identifier=" + identifier + "]";
	}
	
	
}
