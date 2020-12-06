package p.actions;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class RenameAction {
	
	public static void changeVariableName(String currName, String newName, Map<String, Set<Integer>> startPositionsByFilePath, Map<String, String> sourceByFilePath) {
		
		int nameLength = currName.length();
		for (String filePath : startPositionsByFilePath.keySet()) {
			Set<Integer> startPositions = startPositionsByFilePath.get(filePath);
			String source = sourceByFilePath.get(filePath);
			
			for (Integer startPos : sortHighToLow(startPositions)) {
				source = source.substring(0, startPos) + newName + source.substring(startPos + nameLength);
			}
			
			// write file back 
			
			PrintStream output;
			try {
				output = new PrintStream(new File(filePath)); 
				output.println(source);
			} catch (FileNotFoundException e) { // TODO Auto-generated catch block e.printStackTr
			}
		}
	}
	
	private static List<Integer> sortHighToLow(Set<Integer> startPositions) {
		List<Integer> sorted = new ArrayList<>(startPositions);
		Collections.sort(sorted, Collections.reverseOrder());
		
		return sorted;
	}

}
