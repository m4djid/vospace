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
	
	public void moveNode(String origin, String direction, boolean keepBytes) throws UnknownHostException, JSONException, InterruptedException {

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
		Db db = new Db();
		Path path_origin = Paths.get(vo.racine+vo.getPathToString(origin, "!vospace")[1]);
		if(!db.getNode(db.query(directionFile, directionParent,directionAncestor)).equals("NodeNotFound")) {
			directionAncestor.add(directionParent);
			directionParent = directionFile;
			Path path_direction = Paths.get(vo.racine+vo.getPathToString(this.direction, "!vospace")[1]+"/"+directionFile);
		}
		Path path_direction = Paths.get(vo.racine+vo.getPathToString(this.direction, "!vospace")[1]);
//			if(!db.isBusy(db.query(originFile, originParent, originAncestor))) {
//				db.setBusy(originFile, originParent, originAncestor);
//				List<String> t = new ArrayList<String>(originAncestor);
//				t.add(originParent);
//				db.setBusy(db.query(originFile, t));
					try {
						Files.copy(path_origin, path_direction, ATOMIC_MOVE);
//						String[] args = new String[] {"/bin/bash", "-c", "your_command", "with", "args"};
//						Process proc = new ProcessBuilder(args).start();
//						BasicDBObject node = db.query(originFile, originParent, originAncestor);
//						BasicDBObject updateNode = new BasicDBObject("$set", new BasicDBObject("node", directionFile)
//								.append("parent", directionParent)
//								.append("ancestor", directionAncestor).append("path", "nodes"+vo.getPathToString(this.direction, "!vospace")[1]));
//		
//						db.setNode(node, updateNode,"one");
//						BasicDBObject query = db.query(originFile, originParent);
//						List<String> newAncestor = directionAncestor;
//						newAncestor.add(directionParent);
//						newAncestor.add(directionFile);
//						List<String> branches = db.getNode(query, "s");
//						for (String string : branches) {
//							JSONObject json = new JSONObject(string);
//							String tPath;
//							String fPath;
//							if(json.getString("parent").equals(originFile)) {
//								tPath = json.getString("path");
//								String temp = vo.getPathToString(origin, "!vospace")[1];
//								fPath = vo.getPathToString(tPath, temp)[1];
//								BasicDBObject newquery = db.query(directionFile, directionAncestor);
//								BasicDBObject updateChildren = new BasicDBObject("$set", new BasicDBObject("node", directionFile)
//										.append("ancestor", directionAncestor).append("path", vo.racine+vo.getPathToString(this.direction, "!vospace")[1]+fPath));
//								db.setNode(newquery, updateChildren, "one");
//							}
//							else if(json.getJSONArray("ancestor").toString().contains(originFile)) {
//								tPath = json.getString("path");
//								String temp = vo.getPathToString(origin, "!vospace")[1];
//								fPath = vo.getPathToString(tPath, temp)[1];
//								
//								// Replace the ancestor's list value
//								List<String> tancestor = new ArrayList<String>();
//								for(int i=0;i<json.getJSONArray("ancestor").length();i++){ 
//									tancestor.add(json.getJSONArray("ancestor").get(i).toString());
//								} 
//								for(int j=0;j<newAncestor.size();j++) {
//									tancestor.set(j, newAncestor.get(j));
//								}
//								BasicDBObject newquery = db.query(directionFile, directionAncestor);
//								BasicDBObject updateGChildren = new BasicDBObject("$set", new BasicDBObject("ancestor", tancestor)
//										.append("path", vo.racine+vo.getPathToString(this.direction, "!vospace")[1]+fPath));
//								db.setNode(newquery, updateGChildren, "one");
//							}
//						}
						if(keepBytes=false) {
							
						}
						
//						String test = db.getNode(db.query(directionFile, directionParent, directionAncestor));
//						if (!test.equals("")){
//							db.unsetBusy(directionFile, directionParent, directionAncestor);
//						}
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
	
//			}
		

	}
}
