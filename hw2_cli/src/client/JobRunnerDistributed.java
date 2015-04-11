package client;

import api.Space;

public class JobRunnerDistributed extends JobRunner {

	private Space local;

	public JobRunnerDistributed(Job job) {
		super(job);
		// TODO Auto-generated constructor stub
		
		//String url = "rmi://" + domainName + ":" + Space.PORT + "/" + Space.SERVICE_NAME;
        //space = ( domainName == null ) ? new SpaceAbstract() : (Space) Naming.lookup( url );
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		
	}

}
