package client;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.net.MalformedURLException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;

import javax.swing.ImageIcon;
import javax.swing.JLabel;

public class ClientMandelbrot extends Client<Integer[][]> {

	/** Generated Serial ID	 */
	private static final long serialVersionUID = 3652205624558233024L;
	
	private static final int DEFAULT_NUM_LOCAL_NODES = 1;
	
	
	/**
	 * Set of all Mandelbrot tests this job can run
	 * 
	 *  Set 0: Full HW2 Set
	 *  Set 1: HW 1 Set
	 */
    public static final JobMandelbrot[] JOBS = {
    	//Set 0: HW2 Full set
    	new JobMandelbrot(-0.7510975859375, 0.1315680625,  0.01611, 1024, 512),
    	
    	//Set 1: Test set from HW1
    	new JobMandelbrot(-2.0, -2.0, 4.0, 256, 64)
    };
    
    private final JobMandelbrot job;
    
    protected ClientMandelbrot(JobMandelbrot job, JobRunner<Integer[][]> runner, Log log) throws RemoteException, MalformedURLException, NotBoundException{
		super("Mandelbrot Set Visualizer", runner, log);
		this.job = job;	
		System.out.println("Client parameters are as follows: ");
		System.out.println("Lower X: " + job.getLOWER_LEFT_X());
		System.out.println("Lower Y: " + job.getLOWER_LEFT_Y());
		System.out.println("Iteration Limit: " + job.getITERATION_LIMIT());
		System.out.println("Edge Length: " + job.getEDGE_LENGTH());
		System.out.println("N Pixels: " + job.getN_PIXELS());
    }

    
    public JLabel getLabel( Integer[][] counts )
    {
        final Image image = new BufferedImage( job.getN_PIXELS(), job.getN_PIXELS(), BufferedImage.TYPE_INT_ARGB );
        final Graphics graphics = image.getGraphics();
        for ( int i = 0; i < counts.length; i++ )
            for ( int j = 0; j < counts.length; j++ )
            {
                graphics.setColor( getColor( counts[i][j] ) );
                graphics.fillRect( i, job.getN_PIXELS() - j, 1, 1 );
            }
        final ImageIcon imageIcon = new ImageIcon( image );
        return new JLabel( imageIcon );
    }
    
    private Color getColor( int iterationCount )
    {
        return iterationCount == job.getITERATION_LIMIT() ? Color.BLACK : Color.WHITE;
    }

    public static void main(String[] args) throws RemoteException, MalformedURLException, NotBoundException {

		String domain = (args.length > 0)? args[0] : "localhost";
		int taskNum = (args.length > 1)? Integer.parseInt(args[1]) : 0;
		String logName = (args.length > 2)? args[2] : "Mandelbrot";

		
		System.out.println("Starting Client 'Mandelbrot' on Space @ "+domain);
				
		Log log = new Log(logName);
		
		JobMandelbrot job = JOBS[taskNum];
        job.setLog(log);
        
        ClientMandelbrot clientMandelbrot;
        if(domain.equalsIgnoreCase("jvm"))
        	clientMandelbrot = new ClientMandelbrot(job, new JobRunnerLocal<Integer[][]>(job,DEFAULT_NUM_LOCAL_NODES), log);
        else
        	clientMandelbrot = new ClientMandelbrot(job, new JobRunnerRemote<Integer[][]>(job,domain), log);
        
        clientMandelbrot.begin();
        Integer[][] result = clientMandelbrot.run();
        clientMandelbrot.add(clientMandelbrot.getLabel(result));
        clientMandelbrot.end();
	}
}
