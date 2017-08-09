package vospace;

import java.util.ArrayList;
import java.util.List;
import java.io.IOException;
import java.io.StringReader;
import java.net.UnknownHostException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.json.JSONException;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

import uws.UWSException;
import uws.job.JobThread;
import uws.job.UWSJob;

public class PullFromVoSpace extends JobThread {

	public PullFromVoSpace(UWSJob j) throws NullPointerException {
		super(j);
	}

	@Override
	protected void jobWork() throws UWSException, InterruptedException {
		
		Document doc = null;
		String target = null, direction = null, protocol, view;
		String jobType = null;
		boolean keepBytes = false;
		System.out.println("**************************************************");
		System.out.println("******** Execution du job "+job.getJobId()+" *********");
		System.out.println("**************************************************");

		DocumentBuilder docbuilder;
		try {
			docbuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			InputSource is = new InputSource();
			is.setCharacterStream(new StringReader(job.getJobInfo().getXML("String")));
			doc = docbuilder.parse(is);
		} catch (ParserConfigurationException | SAXException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Node n = doc.getFirstChild();
		NodeList nl = n.getChildNodes();
		
		for(int i=0; i<nl.getLength();i++) {
			Node t = nl.item(i);
			if(t.getNodeName().equals("vos:target")) {
				target = t.getTextContent();
			}
			if (t.getNodeName().equals("vos:direction")) {
				direction = t.getTextContent();
			}
			if (t.getNodeName().equals("vos:keepBytes")) {
				jobType = "movecopy";
				 if(t.getTextContent().equals("True") | t.getTextContent().equals("true")){
					 keepBytes = true;
				 };
			}
		}
		
		
		if (jobType.equals("movecopy")){
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
		
		
		/*try{
			Result result = createResult("Report");	
			
			BufferedOutputStream output = new BufferedOutputStream(getResultOutput(result));
			output.write(("node : " + nodePath + ", time : " + executionTime).getBytes("ISO-8859-1"));
			output.close();

			publishResult(result);

		}catch(IOException e){
			throw new UWSException(UWSException.INTERNAL_SERVER_ERROR, e, "Impossible to write the result file of the Job " + job.getJobId() + " !", ErrorType.TRANSIENT);
		}*/
	}


	
	
}
