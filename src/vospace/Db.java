package vospace;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import org.bson.Document;

import com.mongodb.BasicDBObject;
import com.mongodb.MongoClient;
import com.mongodb.client.FindIterable;
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
	//		myDoc.remove("_id");
			m.close();
	//		System.out.println(myDoc);
			if (myDoc!=null) {
				myDoc.remove("_id");
				return myDoc.toJson();
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			m.close();
		} 
		return "NodeNotFound";
	}
	
	public List<String> getNode(BasicDBObject query, String s) throws UnknownHostException {
		FindIterable<Document> myDoc;
		List<String> retourDoc = new ArrayList<String>();
		MongoClient m = new MongoClient();
		try {
			MongoDatabase database = m.getDatabase("vospace");
			MongoCollection<Document> collection = database.getCollection("VOSpaceFiles");
			myDoc = collection.find(query);
			if (myDoc != null) {
				for(Document doc : myDoc) {
					doc.remove("_id");
				    retourDoc.add(doc.toJson());
				}
			}
			m.close();
			return retourDoc;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			m.close();
		}
		return retourDoc; 
	}
	
	public void setNode(BasicDBObject query, BasicDBObject setQuery, String qty) {
		MongoClient m = new MongoClient();
		MongoDatabase database = m.getDatabase("vospace");
		MongoCollection<Document> collection = database.getCollection("VOSpaceFiles");
		if(qty.equals("one")){
			collection.updateOne(query, setQuery);
		}
		else if(qty.equals("many")){
			collection.updateMany(query, setQuery);
		}
		m.close();
	}
	
	public BasicDBObject query(String node, String parent, List<String> ancestor) {
		BasicDBObject query = new BasicDBObject();
		query.put("node", node);
		query.put("parent", parent);
		query.put("ancestor", ancestor);
		return query;
	}
	
	public BasicDBObject query(String node, String parent) {
		BasicDBObject query = new BasicDBObject();
		query.put("node", node);
		query.put("parent", parent);
		return query;
	}
	
	public BasicDBObject query(String parent, List<String> ancestor) {
		BasicDBObject query = new BasicDBObject();
		query.put("parent", parent);
		query.put("ancestor", ancestor);
		return query;
	}
	
	public BasicDBObject query(String ancestor) {
		BasicDBObject query = new BasicDBObject();
		query.put("ancestor", ancestor);
		return query;
	}
	
//	public boolean isBusy(String node, String parent, List<String> ancestor) throws JSONException, UnknownHostException {
//		BasicDBObject query = query(node, parent, ancestor);
//		String temp = getNode(query);
//		JSONObject myNode = new JSONObject(temp);
//		if(myNode.get("busy").equals("False")) {
//			return false;
//		}else {
//			return true;
//		}
//		
//	}
	
//	public boolean isBusy(BasicDBObject query) throws UnknownHostException, JSONException {
//		List<String> temp = new ArrayList<String>();
//		temp.addAll(getNode(query, "m"));
//		if(temp!=null) {
//			for (String string : temp) {
//				JSONObject myNode = new JSONObject(temp);
//				if(myNode.get("busy").equals("True")) {
//					return true;
//				}
//			}
//		}
//		return false;
//	}
	
//	private boolean isBusy(String ancestor) throws UnknownHostException, JSONException {
//		BasicDBObject query = query(ancestor);
//		List<String> temp = new ArrayList<String>();
//		temp.addAll(getNode(query, "m"));
//		if(temp!=null) {
//			for (String string : temp) {
//				JSONObject myNode = new JSONObject(temp);
//				if(myNode.get("busy").equals("True")) {
//					return true;
//				}
//			}
//		}
//		return false;
//		
//	}
	
//	public void setBusy(String node, String parent, List<String> ancestor) throws UnknownHostException, JSONException {
//		BasicDBObject query = query(node, parent, ancestor);
//		BasicDBObject busy = new BasicDBObject();
//		busy.put("busy", "True");
//		BasicDBObject setQuery = new BasicDBObject();
//		setQuery.append("$set", busy);
//		if(isBusy(query(node, parent, ancestor)) == false) {
//			setNode(query, setQuery, "one");
//		}else {
//		}
//	}
	
//	public void setBusy(String parent, List<String> ancestor) throws UnknownHostException, JSONException {
//		BasicDBObject query = query(parent, ancestor);
//		BasicDBObject busy = new BasicDBObject();
//		busy.put("busy", "True");
//		BasicDBObject setQuery = new BasicDBObject();
//		setQuery.append("$set", busy);
//		if(isBusy(query(parent, ancestor)) == false) {
//			setNode(query, setQuery, "many");
//			System.out.println("Set busy to true");
//		}else {
//		}
//	}
	
//	public void setBusy(BasicDBObject query) throws UnknownHostException, JSONException {;
//		BasicDBObject busy = new BasicDBObject();
//		busy.put("busy", "True");
//		BasicDBObject setQuery = new BasicDBObject();
//		setQuery.append("$set", busy);
//		if(isBusy(query) == false) {
//			setNode(query, setQuery, "many");
//		}else {
//		}
//	}
//	
//	public void unsetBusy(String node, String parent, List<String> ancestor) throws UnknownHostException, JSONException {
//		BasicDBObject query = query(node, parent, ancestor);
//		BasicDBObject busy = new BasicDBObject();
//		busy.put("busy", "False");
//		BasicDBObject setQuery = new BasicDBObject();
//		setQuery.append("$set", busy);
//		if(isBusy(query(node, parent, ancestor)) == true) {
//			setNode(query, setQuery, "one");
//		}else {
//			System.out.println("Already False");
//		}
//	}
	
//	public void unsetBusy(String parent, List<String> ancestor) throws UnknownHostException, JSONException {
//		BasicDBObject query = query(parent, ancestor);
//		BasicDBObject busy = new BasicDBObject();
//		busy.put("busy", "False");
//		BasicDBObject setQuery = new BasicDBObject();
//		setQuery.append("$set", busy);
//		if(isBusy(query(parent, ancestor)) == true) {
//			setNode(query, setQuery, "many");
//		}else {
//			System.out.println("Already False");
//		}
//	}
//	
//	public void unsetBusy(BasicDBObject query) throws UnknownHostException, JSONException {
//		BasicDBObject busy = new BasicDBObject();
//		busy.put("busy", "False");
//		BasicDBObject setQuery = new BasicDBObject();
//		setQuery.append("$set", busy);
//		if(isBusy(query) == true) {
//			setNode(query, setQuery, "many");
//		}else {
//			System.out.println("Already False");
//		}
//	}
	
	
	
	
	
	

}
