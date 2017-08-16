package vospace;

import java.io.File;
import java.util.List;

import org.apache.commons.io.FileUtils;

import uws.job.ErrorSummary;
import uws.job.Result;
import uws.job.UWSJob;
import uws.job.parameters.UWSParameters;
import uws.job.user.JobOwner;

public class ExtendedUWSJob extends UWSJob{
	private static final long serialVersionUID = 1L;
	private String resultDir = "/home/bouchair/PycharmProjects/VOSpace/static/job/";
	
	public ExtendedUWSJob(JobOwner owner, UWSParameters params, String requestID) {
		super(owner, params, requestID);
		// TODO Auto-generated constructor stub
	}

	public ExtendedUWSJob(JobOwner owner, UWSParameters params) {
		super(owner, params);
		// TODO Auto-generated constructor stub
	}

	public ExtendedUWSJob(String arg0, JobOwner arg1, UWSParameters arg2, long arg3, long arg4, long arg5,
			List<Result> arg6, ErrorSummary arg7) throws NullPointerException {
		super(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7);
		// TODO Auto-generated constructor stub
	}

	public ExtendedUWSJob(UWSParameters params) {
		super(params);
		
		// TODO Auto-generated constructor stub
	}

	@Override
	public void clearResources() {
		// TODO Auto-generated method stub
		super.clearResources();
		try {
	        File f = new File(resultDir+getJobId());
	        if (f.exists() && f.canWrite())
	        	FileUtils.forceDelete(f);
	    } catch (Exception e) {System.err.println("### UWS ERROR: "+e.getMessage()+" ###"); e.printStackTrace();}
	}
}
