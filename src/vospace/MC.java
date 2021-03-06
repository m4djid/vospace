package vospace;

import java.io.File;
import java.io.IOException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.json.JSONException;
import org.json.JSONObject;

import com.mongodb.BasicDBObject;

import uws.UWSException;
import uws.job.ErrorType;

public class MC extends VoCore {

	
	public void moveNode(String origin, String direction, boolean keepBytes) throws JSONException, InterruptedException, IOException, UWSException {
		//Uri parsing into variables
		Map<String, List<String>> origin_ = parsePath(origin);
		Map<String, List<String>> direction_ = parsePath(direction);

		String originFile = origin_.get("node").get(0);
		String directionFile = direction_.get("node").get(0);
		String originParent = origin_.get("parent").get(0);
		String directionParent = direction_.get("parent").get(0);
		List<String> originAncestor = origin_.get("ancestor");
		List<String> directionAncestor = direction_.get("ancestor");
		
		//Paths creation
		
		String path_origin = racine+origin_.get("path").get(0);
		String path_direction = null;
		try {
			if(getNode(query(directionFile, directionParent,directionAncestor)) != null) {
				while (getNode(query(directionFile, directionParent,directionAncestor)) != null) {
					directionAncestor.add(directionParent);
					directionParent = directionFile;	
				}
				path_direction = racine+direction_.get("path").get(0)+"/"+directionFile;
			}
			else {
				path_direction = racine+direction_.get("path").get(0);
			}
		} catch (Exception e1) {
			e1.printStackTrace();
			throw new UWSException(UWSException.INTERNAL_SERVER_ERROR, e1, ErrorType.TRANSIENT);
		}
		
		//Physical copy
		System.out.println(path_origin);
		System.out.println(path_direction);
		File file = new File(path_origin);
		File file2 = new File(path_direction);
		
		if(file != null && file2 != null) {
			boolean isDirectory = file.isDirectory(); 
			boolean isFile =      file.isFile();
			if(file.isFile()==true) {
				try {
					FileUtils.copyFileToDirectory(file, file2);
				} catch (Exception e) {
					e.printStackTrace();
					throw new UWSException(UWSException.INTERNAL_SERVER_ERROR, e, ErrorType.TRANSIENT);
				}
			}
			else if (file.isDirectory()==true) {
			    try {
					FileUtils.copyDirectory(file, file2);
					System.out.println("Copie dossier " +file.isDirectory());
				} catch (Exception e) {
					throw new UWSException(UWSException.INTERNAL_SERVER_ERROR, e, ErrorType.TRANSIENT);
				}
			}
		}
		
		try {
			System.out.println(direction_.get("path").get(0));
			updateDB(origin, originFile, originParent, originAncestor, directionFile, directionParent, directionAncestor, direction_.get("path").get(0));
		} catch (Exception e) {
			e.printStackTrace();
			throw new UWSException(UWSException.INTERNAL_SERVER_ERROR, e, ErrorType.TRANSIENT);
		}
		
		//If the method is a move
		
		if(keepBytes==false) {
			
			//Delete the node and his subnodes from the database
			
			try {
				delete("node", originFile);
				delete("parent", originFile);
				delete("ancestor", originFile);
			} catch (Exception e1) {
				e1.printStackTrace();
				throw new UWSException(UWSException.INTERNAL_SERVER_ERROR, e1, ErrorType.TRANSIENT);
			}
			
			//Delete the node on the physical disk
			
			try {
				FileUtils.forceDelete(file);
			} catch (Exception e) {
				throw new UWSException(UWSException.INTERNAL_SERVER_ERROR, e, "Impossible to delete", ErrorType.TRANSIENT);
			}
		}
		
	}
	
	private void updateDB(String origin, String originFile, String originParent, List<String>originAncestor, String directionFile, String directionParent, List<String>directionAncestor, String path) throws UnknownHostException {
		//Copy and edit the target node representation
		BasicDBObject node = query(originFile, originParent, originAncestor);					
		String targetNode = getNode(node);
		JSONObject js = new JSONObject(targetNode);
		js.remove("node");
		js.remove("parent");
		js.remove("path");
		js.remove("ancestor");
		js.put("node", directionFile);
		js.put("parent", directionParent);
		js.put("path", "storage"+path);
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
		List<String> branches = getNode(query, "s");
		for (String string : branches) {
			JSONObject json = new JSONObject(string);
			String fPath;
			String temp = getPathToString(origin, "!vospace")[1];
			fPath = getPathToString(json.getString("path"), temp)[1];
			json.remove("parent");
			json.remove("ancestor");
			json.remove("path");
			json.put("parent", directionFile);
			json.put("ancestor", directionAncestor);
			json.put("path", "storage"+path+fPath);

			insertions.add(json);
		}
		
		//Rank n
		BasicDBObject subquery = query(originFile);
		List<String> subBranches = getNode(subquery, "s");
		for (String s : subBranches) {
			JSONObject json = new JSONObject(s);
			String fPath;
			String temp = getPathToString(origin, "!vospace")[1];
			fPath = getPathToString(json.getString("path"), temp)[1];
			Map<String, List<String>> _temp = parsePath(path+fPath);
			json.remove("ancestor");
			json.remove("path");
			json.put("ancestor", _temp.get("ancestor"));
			json.put("path", "storage"+path+fPath);

			insertions.add(json);
		}
		
		System.out.println("etape 3* insertion database");
		//Insert the new representation in database
		System.out.println("Insertion");
		System.out.println(insertions);
		insert(insertions);
		System.out.println("Succès");
	}
}
