package vospace;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.json.JSONException;

import java.net.UnknownHostException;
import java.nio.file.Paths;


public class VoCore {

	private String origin;
	private String direction;

	private String auto() {
		String suuid = UUID.randomUUID().toString();
		String[] ruuid = suuid.split("-");
		return ruuid[0];
	}
	
	private Map<String, List<String>> path(String uri) {
		String[] uri_parts = uri.split("!vospace");
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
	
	public boolean moveNode(String origin, String direction) throws UnknownHostException, JSONException {
		Db db = new Db();
		Map<String, List<String>> origin_path = path(origin);
		Map<String, List<String>> direction_path = path(direction);
		String originFile = origin_path.get("node").get(0);
		String directionFile = direction_path.get("node").get(0);
		if (directionFile.equals(".auto")) {
			directionFile = auto();
		}
		String originParent = origin_path.get("parent").get(0);
		String directionParent = direction_path.get("parent").get(0);
		List<String> originAncestor = origin_path.get("ancestor");
		List<String> directionAncestor = direction_path.get("ancestor");
		
		db.setBusy(originFile, originParent, originAncestor);
		return false;
	}
}
