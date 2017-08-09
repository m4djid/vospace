package vospace;

import static java.nio.file.StandardCopyOption.ATOMIC_MOVE;

import java.io.IOException;
import java.net.UnknownHostException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import com.mongodb.BasicDBObject;

public class MoveCopy {
	
	private String origin, direction;
	VoCore vo = new VoCore();
	
	public void moveNode(String origin, String direction, boolean keepBytes) throws JSONException, InterruptedException, IOException {
		Db db = new Db();
		
		//Uri parsing into variables
		Map<String, List<String>> origin_ = vo.parsePath(origin);
		Map<String, List<String>> direction_ = vo.parsePath(direction);

		String originFile = origin_.get("node").get(0);
		String directionFile = direction_.get("node").get(0);
		if (directionFile.equals(".auto")) {
			directionFile = vo.auto();
			this.direction= direction.replaceAll(".auto", directionFile);
		}
		String originParent = origin_.get("parent").get(0);
		String directionParent = direction_.get("parent").get(0);
		List<String> originAncestor = origin_.get("ancestor");
		List<String> directionAncestor = direction_.get("ancestor");
		
		//Paths creation
		Path path_origin = Paths.get(vo.racine+vo.getPathToString(origin, "!vospace")[1]);
		if(!db.getNode(db.query(directionFile, directionParent,directionAncestor)).equals("NodeNotFound")) {
			directionAncestor.add(directionParent);
			directionParent = directionFile;
			Path path_direction = Paths.get(vo.racine+vo.getPathToString(this.direction, "!vospace")[1]+"/"+directionFile);
		}
		Path path_direction = Paths.get(vo.racine+vo.getPathToString(this.direction, "!vospace")[1]);
		
		
		//Physical copy
		Files.copy(path_origin, path_direction);
		
		//Copy and edit the target node representation
		BasicDBObject node = db.query(originFile, originParent, originAncestor);					
		String targetNode = db.getNode(node);
		JSONObject js = new JSONObject(targetNode);
		js.remove("node");
		js.remove("parent");
		js.remove("path");
		js.remove("ancestor");
		js.put("node", directionFile);
		js.put("parent", directionParent);
		js.put("path", "nodes"+vo.getPathToString(this.direction, "!vospace")[1]);
		js.put("ancestor", directionAncestor);

		List<JSONObject> insertions = new ArrayList<JSONObject>();
		
		insertions.add(js);

		//Copy and edit children's representations
		//Rank 1
		BasicDBObject query = new BasicDBObject();
		query.put("parent", originFile);
		List<String> newAncestor = directionAncestor;
		newAncestor.add(directionParent);
		newAncestor.add(directionFile);
		List<String> branches = db.getNode(query, "s");
		for (String string : branches) {
			JSONObject json = new JSONObject(string);
			String fPath;
			String temp = vo.getPathToString(origin, "!vospace")[1];
			fPath = vo.getPathToString(json.getString("path"), temp)[1];
			json.remove("parent");
			json.remove("ancestor");
			json.remove("path");
			json.put("parent", directionFile);
			json.put("ancestor", directionAncestor);
			json.put("path", "nodes"+vo.getPathToString(this.direction, "!vospace")[1]+fPath);

			insertions.add(json);
		}
		
		//Rank n
		BasicDBObject subquery = db.query(originFile);
		List<String> subBranches = db.getNode(subquery, "s");
		for (String s : subBranches) {
			JSONObject json = new JSONObject(s);
			String fPath;
			String temp = vo.getPathToString(origin, "!vospace")[1];
			fPath = vo.getPathToString(json.getString("path"), temp)[1];
			Map<String, List<String>> _temp = vo.parsePath(vo.getPathToString(this.direction, "!vospace")[1]+fPath);
			json.remove("ancestor");
			json.remove("path");
			json.put("ancestor", _temp.get("ancestor"));
			json.put("path", "nodes"+vo.getPathToString(this.direction, "!vospace")[1]+fPath);

			insertions.add(json);
		}
		
		//Insert the new representation in database
		db.insert(insertions);
		
		//If the method is a move
		if(keepBytes=false) {
			
			//Delete the node and his subnodes from the database
			db.delete("node", originFile);
			db.delete("parent", originFile);
			db.delete("ancestor", originFile);
			
			//Delete the node on the physical disk
			Files.delete(path_origin);
		}
						

		

	}
}
