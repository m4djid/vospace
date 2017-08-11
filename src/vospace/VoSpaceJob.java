package vospace;

import java.io.IOException;
import java.io.StringReader;
import java.net.UnknownHostException;
import java.time.LocalDateTime;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.json.JSONException;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import uws.UWSException;
import uws.job.JobThread;
import uws.job.UWSJob;

public class VoSpaceJob extends JobThread {

	public VoSpaceJob(UWSJob j) throws NullPointerException {
		super(j);
	}
	String uriPull = "ivo://ivoa.net/vospace/core#httpget";
	String uriPush = "ivo://ivoa.net/vospace/core#httpput";

	@Override
	protected void jobWork() throws UWSException, InterruptedException {
		System.out.println("Job work");
		long startTime = System.currentTimeMillis();
		Document doc = null;
		String target = null, direction = null, protocol = null, view;
		String jobType = null;
		boolean keepBytes = false;
		DocumentBuilder docbuilder;
		try {
			docbuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			InputSource is = new InputSource();
			is.setCharacterStream(new StringReader(job.getJobInfo().getXML("String")));
			doc = docbuilder.parse(is);
			System.out.println("string to doc");
		} catch (ParserConfigurationException | SAXException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("Node");
		try {
			Node n = doc.getFirstChild();
			NodeList nl = n.getChildNodes();
			
			for(int i=0; i<nl.getLength();i++) {
				Node t = nl.item(i);
				if(t.getNodeName().equals("vos:target")) {
					target = t.getTextContent();
				}
				if (t.getNodeName().equals("vos:direction")) {
					direction = t.getTextContent();
					if (direction.equals("pullFromVoSpace")){
						System.out.println(direction);
						jobType = "pull";
					}
				}
				if (t.getNodeName().equals("vos:keepBytes")) {
					jobType = "movecopy";
					System.out.println("move");
					 if(t.getTextContent().equals("True") | t.getTextContent().equals("true")){
						 keepBytes = true;
					 };
				}
				if (t.getNodeName().equals("protocol")) {
					protocol = t.getAttributes().toString();
				}
			}
		} catch (DOMException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		
		System.out.println("**************************************************");
		System.out.println("******** Execution du job "+job.getJobId()+" *********");
		System.out.println("Début : " +LocalDateTime.now());
		System.out.println("target : "+target);
		System.out.println("destination : "+direction);
		System.out.println("protocol : "+protocol);
		System.out.println("keepBytes : "+keepBytes);
		System.out.println("**************************************************");
		
		
		if (jobType.equals("movecopy")){
			System.out.println("-----");
			MoveCopy mc = new MoveCopy();
			try {
				mc.moveNode(target, direction, keepBytes);
			} catch (UnknownHostException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		if (jobType.equals("pull")){
			PullFromVoSpace p = new PullFromVoSpace();
			p.pullFromVoSpace(target);
		}
		
		long endTime   = System.currentTimeMillis();
		long totalTime = endTime - startTime;
		System.out.println("Job : "+job.getJobId()+" terminé en "+totalTime);
		
//		String xml = job.getJobInfo().getXML("target");
//		int executionTime = 5;
//		int count = 1;
//		while(!isInterrupted() && count < executionTime){
//			Thread.sleep(1000);
//			System.out.println(count);
//			count++;
//			if(count==executionTime) {
//				System.out.println("Hello World!");
//			}
//		}
//		System.out.println(xml);
		
		
		if (isInterrupted())
			throw new InterruptedException();
		
	
	}


	
	
}
