package vospace;

import java.net.UnknownHostException;

import org.json.JSONException;

import uws.UWSException;
import uws.job.JobThread;
import uws.job.UWSJob;

public class PullFromVoSpace extends JobThread {

	public PullFromVoSpace(UWSJob j) throws NullPointerException {
		super(j);
	}

	@Override
	protected void jobWork() throws UWSException, InterruptedException {
		VoCore vo = new VoCore();
		System.out.println("**************************************************");
		System.out.println("******** Execution du job "+job.getJobId()+" *********");
		System.out.println("**************************************************");
		try {
			vo.moveNode("vos://example.com!vospace/iyapici/CDS/testMadjid", "vos://example.com!vospace/mydir/.auto");
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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
