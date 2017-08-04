package vospace;

import java.net.UnknownHostException;
import java.util.List;

import org.bson.Document;

import com.mongodb.BasicDBObject;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.json.*;



public class Db {
		
	
	private MongoCollection<Document> collection() {
		MongoClient m = ConnectionFactory.CONNECTION.getClient();
		MongoDatabase database = m.getDatabase("vospace");
		MongoCollection<Document> collection = database.getCollection("VOSpaceFiles");
		return collection;
	}
	
	public String getNode(BasicDBObject query) throws UnknownHostException {
	Document myDoc;
//	BasicDBObject query = new BasicDBObject();
//	query.put("node", node);
//	query.put("parent", parent);
//	query.put("ancestor", ancestor);
	
	try {
//		MongoDatabase database = m.getDatabase("vospace");
//		MongoCollection<Document> collection = database.getCollection("VOSpaceFiles");
		myDoc = collection().find(query).first();
		myDoc.remove("_id");
//		System.out.println(myDoc.toJson());
		if (myDoc!=null) {
			return myDoc.toJson();
		}
	} catch (Exception e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	return "NodeNotFound";
	}
	
	public void setBusy(String node, String parent, List<String> ancestor) throws UnknownHostException, JSONException {
		BasicDBObject query = new BasicDBObject();
		query.put("node", node);
		query.put("parent", parent);
		query.put("ancestor", ancestor);
		BasicDBObject busy = new BasicDBObject();
		busy.put("busy", "True");
		BasicDBObject setQuery = new BasicDBObject();
		setQuery.append("$set", busy);
		String temp = getNode(query);
		JSONObject myNode = new JSONObject(temp);
		System.out.println(myNode.get("busy"));
		if(myNode.get("busy").equals("False")) {
			collection().updateOne(query, setQuery);
		}
		System.out.println("**************************************************");
		System.out.println(getNode(query));
		System.out.println("**************************************************");
	}

}
