package vospace;

import uws.UWSException;
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
		if (job.getJobList().getName().equals("transfers")) 
			return new VoSpaceJob(job);
		
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



