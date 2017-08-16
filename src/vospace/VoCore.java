package vospace;

import java.net.UnknownHostException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.mongodb.BasicDBObject;


public class VoCore extends Db{

	String racine = "/home/bouchair/PycharmProjects/VOSpace/storage";
	
	//Generate a name for the node when the value is ".auto"
	String auto() {
		String suuid = UUID.randomUUID().toString();
		String[] ruuid = suuid.split("-");
		return ruuid[0];
	}
	
	
	//Parse the uri in a Map
	Map<String, List<String>> parsePath(String uri) {
		String[] uri_parts = getPathToString(uri, "!vospace");
		String uriPath = uri_parts[1];
		int count = Paths.get(uriPath).getNameCount();
		Map<String, List<String>> path = new HashMap<String, List<String>>();
		if(Paths.get(uriPath).getFileName().toString().equals(".auto")) {
			String t = auto();
			uriPath = uriPath.replaceAll(".auto", t);	
		}
		path.put("path", new ArrayList<>());
		path.get("path").add(uriPath);
		path.put("node", new ArrayList<>());
		path.get("node").add(Paths.get(uriPath).getFileName().toString());
		path.put("parent", new ArrayList<>());
		path.put("ancestor", new ArrayList<>());
		if(count>1) {
			path.get("parent").add(Paths.get(uriPath).getName(count-2).toString());
			for (int i = 0; i < count-2; i++) {
				path.get("ancestor").add(Paths.get(uriPath).getName(i).toString());
			}
		}
		else {
			path.get("parent").add("");
		}
		return path;
	}
	
	//Split the string
	String[] getPathToString(String uri, String cut) {
		return uri.split(cut);
	}
	
	
	
}
