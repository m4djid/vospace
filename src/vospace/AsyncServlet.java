package vospace;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.net.UnknownHostException;

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
import uws.job.JobList;
import uws.job.JobThread;
import uws.job.UWSJob;
import uws.service.UWSServlet;

public class AsyncServlet extends UWSServlet {
	private static final long serialVersionUID = 1L;

	@Override
	public void initUWS() throws UWSException{
		addJobList(new JobList("transfers"));
		System.out.println("**************************************************");
		System.out.println("************ Lancement du service UWS ************");
		System.out.println("**************************************************");
	}

	@Override
	public JobThread createJobThread(UWSJob job) throws UWSException{
		Document doc = null;
		String target = null, direction = null, protocol = null, view = null, securityMethod = null;
		String jobType = null;
		boolean keepBytes = false;
		String XML = job.getJobInfo().getXML("String");
		DocumentBuilder docbuilder;
		
		try {
			docbuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			InputSource is = new InputSource();
			is.setCharacterStream(new StringReader(XML));
			doc = docbuilder.parse(is);
		} catch (ParserConfigurationException | SAXException | IOException e) {
			e.printStackTrace();
			throw new UWSException(UWSException.INTERNAL_SERVER_ERROR, e, ErrorType.TRANSIENT);
		}
		
		try {
			Node n = doc.getFirstChild();
			NodeList nl = n.getChildNodes();
			
			for(int i=0; i<nl.getLength();i++) {
				Node t = nl.item(i);
				if(t.getNodeName().equals("vos:target")) {
					target = t.getTextContent();
				}
				else if (t.getNodeName().equals("vos:direction")) {
					direction = t.getTextContent();
					if (direction.equals("pullFromVoSpace")){
						jobType = "pull";
					}
				}
				else if (t.getNodeName().equals("vos:keepBytes")) {
					jobType = "movecopy";
					 if(t.getTextContent().equals("True") | t.getTextContent().equals("true")){
						 keepBytes = true;
					 };
				}
				else if (t.getNodeName().equals("vos:protocol")) {
					protocol = t.getAttributes().getNamedItem("uri").getNodeValue();
				}
				else if (t.getNodeName().equals("vos:view")) {
					view = t.getAttributes().getNamedItem("uri").getNodeValue();
				}	
			}
		} catch (DOMException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		if (jobType.equals("movecopy")){
			return new MoveCopy(job, target, direction, keepBytes);
		}
		if (jobType.equals("pull")){
			return new PullFromVoSpace(job, target, protocol, securityMethod, view);
		}
		
		else 
			throw new UWSException("Impossible to create a job inside the jobs list \"" + job.getJobList().getName() + "\" !");
	}
	
}
	
//	@Override
//	protected void writeHomePage(UWSUrl requestUrl, HttpServletRequest req, HttpServletResponse resp, JobOwner user) throws UWSException, ServletException, IOException{
//		PrintWriter out = resp.getWriter();
//
//		out.println("<html><head><title>UWS4 example (using UWSServlet)</title></head><body>");
//		out.println("<h1>UWS v4 Example (using UWSServlet)</h1");
//		out.println("<p>Hello, this is an example of a use of the library UWS v4.1 !</p>");
//		out.println("<p>Below is the list of all available jobs lists:</p>");
//
//		out.println("<ul>");
//		for(JobList jl : this){
//			out.println("<li>" + jl.getName() + " - " + jl.getNbJobs() + " jobs - <a href=\"" + requestUrl.listJobs(jl.getName()) + "\">" + requestUrl.listJobs(jl.getName()) + "</a></li>");
//		}
//		out.println("</ul>");
//	}



