package p.vo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Field {
	String identifier;
	AccessModifiers accessModifier;
	String key;	
	Map<String, Set<Integer>> startPositionsByFilePath;
	
	
	public Field(String id, AccessModifiers accessModifier, String key) {
		this.identifier = id;
		this.accessModifier = accessModifier;
		this.key = key;
		this.startPositionsByFilePath = new HashMap<>();
	}
	
	public String getIdentifier() {
		return this.identifier;
	}
	
	public String getKey() {
		return this.key;
	}
	
	public AccessModifiers getAccessModifier() {
		return this.accessModifier;
	}
	
	public Map<String, Set<Integer>> getStartPositionsByFilePath() {
		return this.startPositionsByFilePath;
	}
	
	public void addStartPosition(String filePath, int startPos) {
		Set<Integer> startPositions = startPositionsByFilePath.get(filePath);
		
		if (startPositions != null) {
			startPositions.add(startPos);
		} else {
			startPositions = new HashSet<>();
			startPositions.add(startPos);
			startPositionsByFilePath.put(filePath, startPositions);
		}
	}

	@Override
	public String toString() {
		return "Field [identifier=" + identifier + ", accessModifier=" + accessModifier + ", key=" + key
				+ ", startPositionsByFilePath=" + startPositionsByFilePath + "]";
	}


	

}
