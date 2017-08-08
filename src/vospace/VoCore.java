package vospace;

import static java.nio.file.StandardCopyOption.ATOMIC_MOVE;

import java.io.IOException;
import java.net.UnknownHostException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.json.JSONException;

import com.mongodb.BasicDBObject;


public class VoCore {

	private String racine = "/home/bouchair/PycharmProjects/VOSpace/nodes";
	private String origin, direction;

	private String auto() {
		String suuid = UUID.randomUUID().toString();
		String[] ruuid = suuid.split("-");
		return ruuid[0];
	}
	
	private Map<String, List<String>> parsePath(String uri) {
		String[] uri_parts = getPathToString(uri);
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
	
	private String[] getPathToString(String uri) {
		return uri.split("!vospace");
	}
	
	public void moveNode(String origin, String direction) throws UnknownHostException, JSONException, InterruptedException {
		
		Map<String, List<String>> origin_ = parsePath(origin);
		Map<String, List<String>> direction_ = parsePath(direction);
		
		String originFile = origin_.get("node").get(0);
		String directionFile = direction_.get("node").get(0);
		if (directionFile.equals(".auto")) {
			directionFile = auto();
			 this.direction= direction.replaceAll(".auto", directionFile);
		}
		String originParent = origin_.get("parent").get(0);
		String directionParent = direction_.get("parent").get(0);
		List<String> originAncestor = origin_.get("ancestor");
		List<String> directionAncestor = direction_.get("ancestor");
		Db db = new Db();
		if(db.isBusy(originFile, originParent, originAncestor)) {
			System.out.println("**************************************************");
			System.out.println("******************** Node Busy *******************");
			System.out.println("**************************************************");
		}
		else {
			db.setBusy(originFile, originParent, originAncestor);
			
			Path path_origin = Paths.get(racine+getPathToString(origin)[1]);
			System.out.println("origine : "+ path_origin);
			Path path_direction = Paths.get(racine+getPathToString(this.direction)[1]);
			System.out.println("direction "+ path_direction);
			try {
				Files.move(path_origin, path_direction, ATOMIC_MOVE);
				BasicDBObject query = db.query(originFile, originParent, originAncestor);
//				BasicDBObject updateMtime = new BasicDBObject("$set",
//					    new BasicDBObject("properties.mtime.mtime", "Modified "+LocalDateTime.now()));
				BasicDBObject updateNode = new BasicDBObject("$set", new BasicDBObject("node", directionFile)
					    .append("parent", directionParent)
					    .append("ancestor", directionAncestor).append("path", "nodes"+getPathToString(this.direction)[1]));
				
				db.setNode(query, updateNode,"one");
				String test = db.getNode(db.query(directionFile, directionParent, directionAncestor));
				if (!test.equals("")){
					db.unsetBusy(directionFile, directionParent, directionAncestor);
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		
	}
}
