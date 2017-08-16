package vospace;

import uws.UWSException;
import uws.job.JobThread;
import uws.job.UWSJob;
import uws.service.UWSServlet;

public class ExtendedUWSServlet extends UWSServlet {
	private static final long serialVersionUID = 1L;

	public ExtendedUWSServlet() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	public JobThread createJobThread(ExtendedUWSJob job) throws UWSException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public JobThread createJobThread(UWSJob arg0) throws UWSException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void initUWS() throws UWSException {
		// TODO Auto-generated method stub
		
	}
}
