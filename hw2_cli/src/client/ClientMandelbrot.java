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
	
	private static final int DEFAULT_NUM_LOCAL_NODES = 4;
	
    private static final JobMandelbrot[] JOBS = {
    	//Set 0: HW2 Full set
    	new JobMandelbrot(-0.7510975859375, 0.1315680625,  0.01611, 1024, 512),
    	
    	//Set 1: Test set from HW1
    	new JobMandelbrot(-2.0, -2.0, 4.0, 256, 64)
    };
    
    private final JobMandelbrot job;
    
    protected ClientMandelbrot(JobMandelbrot job, JobRunner<Integer[][]> runner) throws RemoteException, MalformedURLException, NotBoundException{
		super("Mandelbrot Set Visualizer", runner);
		this.job = job;
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
		
		
		System.out.println("Starting Client 'Mandelbrot' on Space @ "+domain);
		
        ClientMandelbrot clientMandelbrot;
        
        if(domain.equalsIgnoreCase("jvm"))
        	clientMandelbrot = new ClientMandelbrot(JOBS[taskNum], new JobRunnerLocal<Integer[][]>(JOBS[taskNum],DEFAULT_NUM_LOCAL_NODES));
        else
        	clientMandelbrot = new ClientMandelbrot(JOBS[taskNum], new JobRunnerRemote<Integer[][]>(JOBS[taskNum],domain));
        
        clientMandelbrot.begin();
        Integer[][] result = clientMandelbrot.run();
        clientMandelbrot.add(clientMandelbrot.getLabel(result));
        clientMandelbrot.end();
	}
}
