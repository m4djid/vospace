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
import uws.job.ErrorType;
import uws.job.JobThread;
import uws.job.UWSJob;

public class MoveCopy extends JobThread {

	String target, direction;
	boolean keepBytes;
	public MoveCopy(UWSJob j, String target, String direction, boolean keepBytes) throws NullPointerException {
		super(j);
		this.target = target;
		this.direction = direction;
		this.keepBytes = keepBytes;
	}
	String uriPull = "ivo://ivoa.net/vospace/core#httpget";
	String uriPush = "ivo://ivoa.net/vospace/core#httpput";

	@Override
	protected void jobWork() throws UWSException, InterruptedException {
		System.out.println("Job work");
		long startTime = System.currentTimeMillis();

				
		System.out.println("**************************************************");
		System.out.println("******** Execution du job "+job.getJobId()+" *********");
		System.out.println("Début : " +LocalDateTime.now());
		System.out.println("target : "+this.target);
		System.out.println("destination : "+this.direction);
		System.out.println("keepBytes : "+this.keepBytes);
		System.out.println("**************************************************");
		
		

			MC mc = new MC();
			try {
				mc.moveNode(target, direction, keepBytes);
			} catch (UnknownHostException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (JSONException e) {
				e.printStackTrace();
				throw new UWSException(UWSException.INTERNAL_SERVER_ERROR, e, "Error while updating the database !", ErrorType.TRANSIENT);
			} catch (IOException e) {
				e.printStackTrace();
				throw new UWSException(UWSException.INTERNAL_SERVER_ERROR, e, "Error while moving/copying the node to " +this.direction, ErrorType.TRANSIENT);
			}

		long endTime   = System.currentTimeMillis();
		long totalTime = endTime - startTime;
		System.out.println("Job : "+job.getJobId()+" terminé en "+totalTime);
		
		
		
		if (isInterrupted())
			throw new InterruptedException();
		
	
	}


	
	
}
