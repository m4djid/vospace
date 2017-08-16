package vospace;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.UnknownHostException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.json.JSONObject;

import uws.UWSException;
import uws.job.ErrorType;
import uws.job.JobThread;
import uws.job.Result;
import uws.job.UWSJob;

public class PushToVoSpace extends JobThread {
	
	String target, protocol, securityMethod, view, retour, redirect;
	String storage = "/home/bouchair/PycharmProjects/VOSpace/storage";
	String resultDir = "/home/bouchair/PycharmProjects/VOSpace/static/job";
	String url = "http://130.79.128.185/job/";
	
	public PushToVoSpace(UWSJob job, String target, String protocol, String securityMethod, String view) throws NullPointerException {
		super(job);
		this.target = target;
		this.protocol = protocol;
		this.securityMethod = securityMethod;
		if (view == null)
			this.view = "_";
		else
			this.view = view;
	}

	@Override
	protected void jobWork() throws UWSException, InterruptedException {
		long startTime = System.currentTimeMillis();

		System.out.println("**************************************************");
		System.out.println("******** Execution du job "+job.getJobId()+" *********");
		System.out.println("Type : pushToVoSpace");
		System.out.println("Début : " +LocalDateTime.now());
		System.out.println("target : "+this.target);
		System.out.println("destination : "+this.protocol);
		System.out.println("view : "+this.view);
		System.out.println("*************************************************");
		
		
		VoCore vo = new VoCore();
		Map<String, List<String>> node = vo.parsePath(this.target);
		String nodeFile = node.get("node").get(0);
		String nodeParent = node.get("parent").get(0);
		List<String> nodeAncestor = node.get("ancestor");
		String temp = null;
		JSONObject json;
		// Check if the file already exists in database
		try {
			temp = vo.getNode(vo.query(nodeFile, nodeParent,nodeAncestor));
			if (temp!=null) {
				json = new JSONObject(temp);
				if (json.getJSONObject("properties").getJSONObject("type").getString("type").equals("DataNode")) 
					throw new UWSException(UWSException.INTERNAL_SERVER_ERROR, "Internal Fault : "+nodeFile+" is not a ContainderNode", ErrorType.TRANSIENT);
				
			}
			else {
				System.out.println("Node Not Found");
				throw new UWSException(UWSException.INTERNAL_SERVER_ERROR, "Node Not Found : "+nodeFile, ErrorType.TRANSIENT);
			}

		} catch (UWSException | UnknownHostException e1) {
			throw new UWSException(UWSException.INTERNAL_SERVER_ERROR, e1, ErrorType.TRANSIENT);
		}
		
	
		String path = json.getString("path").toString();
		XmlResponse xml = new XmlResponse();
		
		File file = new File(storage+"/"+path);
		if (file.isFile()) {
			throw new UWSException(UWSException.INTERNAL_SERVER_ERROR, "Operation Not Supported !", ErrorType.TRANSIENT);
		}
		// Create result directory
		
		String fileName = job.getJobId()+"/results/transferDetails";
	    File f = new File(resultDir, fileName);
	    
	    // Build the directory if not existing:
		try {
			if (!f.getParentFile().exists())
			    f.getParentFile().mkdirs();
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	    
        // If the node is requested with a zip view, the node and his subnodes are compressed and stored in a job folder to be retrieved
		
		redirect = "http://130.79.128.185/vospace/upload/"+path;
		retour = xml.responseXML(target, "pushToVoSpace", protocol, redirect, view);
		
		try {   
	        // Write the result:
	        BufferedWriter writer = new BufferedWriter(new FileWriter(f));
	        writer.write(retour);
	        writer.close();
	         
	        // Add it to the results list of this job:
	        job.addResult(new Result(job, "transferDetails", "xml", url+job.getJobId()+"/results/transferDetails"));
	         
	    } catch (IOException e) {
	        // If there is an error, encapsulate it in an UWSException so that an error summary can be published:
	        throw new UWSException(UWSException.INTERNAL_SERVER_ERROR, e, "Impossible to write the result file at \""+f.getAbsolutePath()+"\" !", ErrorType.TRANSIENT);
	    }
		
		
		long endTime   = System.currentTimeMillis();
		long totalTime = endTime - startTime;
		System.out.println("*************************************************");
		System.out.println("******** Job : "+job.getJobId()+" terminé en "+totalTime+ "ms ********");
		System.out.println("*************************************************");
		
		
		if (isInterrupted())
			throw new InterruptedException();
	}

}
