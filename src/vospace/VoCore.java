package vospace;

import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;


public class VoCore {

	String racine = "/home/bouchair/PycharmProjects/VOSpace/nodes";
	

	String auto() {
		String suuid = UUID.randomUUID().toString();
		String[] ruuid = suuid.split("-");
		return ruuid[0];
	}
	
	Map<String, List<String>> parsePath(String uri) {
		String[] uri_parts = getPathToString(uri, "!vospace");
		int count = Paths.get(uri_parts[1]).getNameCount();
		Map<String, List<String>> path = new HashMap<String, List<String>>();
		path.put("node", new ArrayList<>());
		path.get("node").add(Paths.get(uri_parts[1]).getFileName().toString());
		path.put("parent", new ArrayList<>());
		path.get("parent").add(Paths.get(uri_parts[1]).getName(count-2).toString());
		path.put("ancestor", new ArrayList<>());
		for (int i = 0; i < count-2; i++) {
			path.get("ancestor").add(Paths.get(uri_parts[1]).getName(i).toString());
		}
		return path;
	}
	
	String[] getPathToString(String uri, String cut) {
		return uri.split(cut);
	}
	
	
	
}
