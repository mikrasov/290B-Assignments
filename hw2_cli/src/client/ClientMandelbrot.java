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
	
	private final JobMandelbrot job;
    
    
    private static final JobMandelbrot[] JOBS = {
    	//Set 0: HW2 Full set
    	new JobMandelbrot(-0.7510975859375, 0.1315680625,  0.01611, 1024, 512),
    	
    	//Set 1: Test set from HW1
    	new JobMandelbrot(-2.0, -2.0, 4.0, 256, 64)
    };
    
	
    protected ClientMandelbrot(String domainName, JobMandelbrot job) throws RemoteException, MalformedURLException, NotBoundException{
		super("Mandelbrot Set Visualizer", new JobRunner<Integer[][]>(job, domainName));
		this.job = job;
    }
    
	public ClientMandelbrot(String domainName, double LOWER_LEFT_X, double LOWER_LEFT_Y, double EDGE_LENGTH, int N_PIXELS, int ITERATION_LIMIT ) throws RemoteException, MalformedURLException, NotBoundException{
		this(domainName, new JobMandelbrot(LOWER_LEFT_X, LOWER_LEFT_Y, EDGE_LENGTH, N_PIXELS, ITERATION_LIMIT) );
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
		
        ClientMandelbrot clientMandelbrot = new ClientMandelbrot(domain, JOBS[taskNum]);
        clientMandelbrot.begin();
        Integer[][] result = clientMandelbrot.run();
        clientMandelbrot.add(clientMandelbrot.getLabel(result));
        clientMandelbrot.end();
	}
}
