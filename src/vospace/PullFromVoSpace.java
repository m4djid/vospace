package vospace;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.json.JSONObject;

import uws.UWSException;
import uws.job.ErrorType;
import uws.job.JobThread;
import uws.job.Result;
import uws.job.UWSJob;
import uws.service.UWSUrl;

public class PullFromVoSpace extends JobThread {
	
	String target, protocol, securityMethod, view, retour, redirect;
	private List<String> fileList = new ArrayList<>();
	String storage = "/home/bouchair/PycharmProjects/VOSpace/storage";
	String resultDir = "/home/bouchair/PycharmProjects/VOSpace/static/job";
	String url = "http://130.79.128.185/job/";
	
	public PullFromVoSpace(UWSJob job, String target, String protocol, String securityMethod, String view) throws NullPointerException {
		super(job);
		this.target = target;
		this.protocol = protocol;
		this.securityMethod = securityMethod;
		this.view = view;
	}
	
	private void getFiles(File dir) {
		File[] files = dir.listFiles();
		if(files.length>0 && files!=null) {
			for(File f: files) {
				if(f.isFile()) 
					fileList.add(f.getAbsolutePath());
				else 
					getFiles(f);		
			}
		}
	}
	
	private void toZip(String path, String zip) {
		File dir = new File(path);
		getFiles(dir);
		
		try {
			FileOutputStream fo = new FileOutputStream(zip);
			ZipOutputStream zo = new ZipOutputStream(fo);
			
			for(String fPath: fileList) {
				String name = fPath.substring(dir.getAbsolutePath().length(), fPath.length());
				ZipEntry zipE = new ZipEntry(name);
				zo.putNextEntry(zipE);
				
				FileInputStream fi = new FileInputStream(fPath);
				byte[] buffer = new byte[1024];
				int lenght;
				while ((lenght = fi.read(buffer))>0) {
					zo.write(buffer, 0, lenght);	
				}
				zo.closeEntry();
				fi.close();
			}
			zo.close();
			fo.close();
			
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}
	
	@Override
	protected void jobWork() throws UWSException, InterruptedException {
		
		long startTime = System.currentTimeMillis();

		
		System.out.println("**************************************************");
		System.out.println("******** Execution du job "+job.getJobId()+" *********");
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
		
		// Check if the file exists in database
		try {
			temp = vo.getNode(vo.query(nodeFile, nodeParent,nodeAncestor));
			if (temp!=null) {
				System.out.println("Node exists");
				System.out.println(temp);
			}
			else {
				System.out.println("Node Not Found");
			}

		} catch (Exception e1) {
			throw new UWSException(UWSException.INTERNAL_SERVER_ERROR, e1, "Error on node : "+nodeFile+" !", ErrorType.TRANSIENT);
		}

		JSONObject json = new JSONObject(temp);
		String path = json.getString("path").toString();
		XmlResponse xml = new XmlResponse();
		System.out.println(storage+"/"+path);
		
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
		if(view.equals("ivo://ivoa.net/vospace/core#zip")) {
			redirect = url+job.getJobId()+"/"+nodeFile+".zip";
			try {
				toZip(storage+"/"+path, resultDir+"/"+job.getJobId()+"/"+nodeFile+".zip");
			} catch (Exception e) {
				e.printStackTrace();
			}
			retour = xml.pullFromXML(target, protocol, redirect, view);
		}
		else {
			redirect = "http://130.79.128.185/vospace/storage/"+path;
			retour = xml.pullFromXML(target, protocol, redirect, "_");
		}
		
		try {   
	        // Write the result:
	        BufferedWriter writer = new BufferedWriter(new FileWriter(f));
	        writer.write(retour);
	        writer.close();
	         
	        // Add it to the results list of this job:
	        job.addResult(new Result(job, "transferDetails", "xml", url+job.getJobId()+"/results/transferDetails"));
	        job.addResult(new Result(job, "uri", "dataNode", this.target));
	         
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
