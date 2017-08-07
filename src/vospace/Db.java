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
	
	public String getNode(BasicDBObject query) throws UnknownHostException {
	Document myDoc;
	MongoClient m = new MongoClient();
	try {
		MongoDatabase database = m.getDatabase("vospace");
		MongoCollection<Document> collection = database.getCollection("VOSpaceFiles");
		myDoc = collection.find(query).first();
		myDoc.remove("_id");
		m.close();
//		System.out.println(myDoc);
		if (myDoc!=null) {
			return myDoc.toJson();
		}
	} catch (Exception e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
		m.close();
	} 
	return "NodeNotFound";
	}
	
	public void setNode(BasicDBObject query, BasicDBObject setQuery) {
		MongoClient m = new MongoClient();
		MongoDatabase database = m.getDatabase("vospace");
		MongoCollection<Document> collection = database.getCollection("VOSpaceFiles");
		collection.updateOne(query, setQuery);
		m.close();
	}
	
	public BasicDBObject query(String node, String parent, List<String> ancestor) {
		BasicDBObject query = new BasicDBObject();
		query.put("node", node);
		query.put("parent", parent);
		query.put("ancestor", ancestor);
		return query;
	}
	
	private BasicDBObject query(String node, String parent) {
		BasicDBObject query = new BasicDBObject();
		query.put("node", node);
		query.put("parent", parent);
		return query;
	}
	
	public boolean isBusy(String node, String parent, List<String> ancestor) throws JSONException, UnknownHostException {
		BasicDBObject query = query(node, parent, ancestor);
		String temp = getNode(query);
		JSONObject myNode = new JSONObject(temp);
		if(myNode.get("busy").equals("False")) {
			return false;
		}else {
			return true;
		}
		
	}
	
	private boolean isBusy(String node, String parent) throws UnknownHostException, JSONException {
		BasicDBObject query = query(node, parent);
		String temp = getNode(query);
		JSONObject myNode = new JSONObject(temp);
		if(myNode.get("busy").equals("False")) {
			return false;
		}else {
			return true;
		}
	}
	
	public void setBusy(String node, String parent, List<String> ancestor) throws UnknownHostException, JSONException {
		BasicDBObject query = query(node, parent, ancestor);
		BasicDBObject busy = new BasicDBObject();
		busy.put("busy", "True");
		BasicDBObject setQuery = new BasicDBObject();
		setQuery.append("$set", busy);
		if(isBusy(node, parent, ancestor) == false) {
			setNode(query, setQuery);
			System.out.println("Set busy to true");
		}else {
			System.out.println("Already True");
		}
	}
	
	public void setBusy(String node, String parent) throws UnknownHostException, JSONException {
		BasicDBObject query = query(node, parent);
		BasicDBObject busy = new BasicDBObject();
		busy.put("busy", "True");
		BasicDBObject setQuery = new BasicDBObject();
		setQuery.append("$set", busy);
		if(isBusy(node, parent) == false) {
			setNode(query, setQuery);
			System.out.println("Set busy to true");
		}else {
			System.out.println("Already True");
		}
	}
	
	public void unsetBusy(String node, String parent, List<String> ancestor) throws UnknownHostException, JSONException {
		BasicDBObject query = query(node, parent, ancestor);
		BasicDBObject busy = new BasicDBObject();
		busy.put("busy", "False");
		BasicDBObject setQuery = new BasicDBObject();
		setQuery.append("$set", busy);
		if(isBusy(node, parent, ancestor) == true) {
			setNode(query, setQuery);
			System.out.println("Set busy to false");
		}else {
			System.out.println("Already False");
		}
	}
	
	
	
	
	
	

}
