package client;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.net.MalformedURLException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.ImageIcon;
import javax.swing.JLabel;

public class ClientTSP extends Client<List<Integer>> {

	/** Generated Serial ID	 */
	private static final long serialVersionUID = 6128849463397846561L;
	
	private static final int NUM_PIXALS = 600;
	private static final int DEFAULT_NUM_LOCAL_NODES = 4;
	
	private static final JobTSP[] JOBS= 
	{
		//Set 0: Full Set of 12 cities for HW2
		new JobTSP(  
			new double[][]{
				{ 1, 1 },
		    	{ 8, 1 },
		    	{ 8, 8 },
		    	{ 1, 8 },
		    	{ 2, 2 },
		    	{ 7, 2 },
		    	{ 7, 7 },
		    	{ 2, 7 },
		    	{ 3, 3 },
		    	{ 6, 3 },
		    	{ 6, 6 },
		    	{ 3, 6 }
			}
		),
		
		// SET 1: Test Set of 10 cities
		new JobTSP( new double[][]{
				{ 6, 3 },
		        { 2, 2 },
		        { 5, 8 },
		        { 1, 5 },
		        { 1, 6 },
		        { 2, 7 },
		        { 2, 8 },
		        { 6, 5 },
		        { 1, 3 },
		        { 6, 6 }
			}
		),
		
		//Set 2: Full Set of 12 cities for HW2
				new JobTSP(  
					new double[][]{
						{ 1, 1 },
				    	{ 8, 1 },
				    	{ 8, 8 },
				    	{ 1, 8 },
				    	{ 2, 2 },
				    	{ 7, 2 },
				    	{ 7, 7 },
				    	{ 2, 7 },
				    	{ 3, 3 },
				    	{ 6, 3 },
				    	{ 6, 6 }
					}
				),
	
    };
   
	private final JobTSP job;
	
	protected ClientTSP(JobTSP job, JobRunner<List<Integer>> runner, Log log)
			throws RemoteException, NotBoundException, MalformedURLException {
		super("Traveling Salesman", runner, log);
		this.job = job;
	}
	
	public JLabel getLabel( final Integer[] tour )
    {
        // display the graph graphically, as it were
        // get minX, maxX, minY, maxY, assuming they 0.0 <= mins
        double minX = job.getCities()[0][0], maxX = job.getCities()[0][0];
        double minY = job.getCities()[0][1], maxY = job.getCities()[0][1];
        for ( double[] cities : job.getCities() ) 
        {
            if ( cities[0] < minX ) 
                minX = cities[0];
            if ( cities[0] > maxX ) 
                maxX = cities[0];
            if ( cities[1] < minY ) 
                minY = cities[1];
            if ( cities[1] > maxY ) 
                maxY = cities[1];
        }

        // scale points to fit in unit square
        final double side = Math.max( maxX - minX, maxY - minY );
        double[][] scaledCities = new double[job.getCities().length][2];
        for ( int i = 0; i < job.getCities().length; i++ )
        {
            scaledCities[i][0] = ( job.getCities()[i][0] - minX ) / side;
            scaledCities[i][1] = ( job.getCities()[i][1] - minY ) / side;
        }

        final Image image = new BufferedImage( NUM_PIXALS, NUM_PIXALS, BufferedImage.TYPE_INT_ARGB );
        final Graphics graphics = image.getGraphics();

        final int margin = 10;
        final int field = NUM_PIXALS - 2*margin;
        // draw edges
        graphics.setColor( Color.BLUE );
        int x1, y1, x2, y2;
        int city1 = tour[0], city2;
        x1 = margin + (int) ( scaledCities[city1][0]*field );
        y1 = margin + (int) ( scaledCities[city1][1]*field );
        for ( int i = 1; i < job.getCities().length; i++ )
        {
            city2 = tour[i];
            x2 = margin + (int) ( scaledCities[city2][0]*field );
            y2 = margin + (int) ( scaledCities[city2][1]*field );
            graphics.drawLine( x1, y1, x2, y2 );
            x1 = x2;
            y1 = y2;
        }
        city2 = tour[0];
        x2 = margin + (int) ( scaledCities[city2][0]*field );
        y2 = margin + (int) ( scaledCities[city2][1]*field );
        graphics.drawLine( x1, y1, x2, y2 );

        // draw vertices
        final int VERTEX_DIAMETER = 6;
        graphics.setColor( Color.RED );
        for ( int i = 0; i < job.getCities().length; i++ )
        {
            int x = margin + (int) ( scaledCities[i][0]*field );
            int y = margin + (int) ( scaledCities[i][1]*field );
            graphics.fillOval( x - VERTEX_DIAMETER/2,
                               y - VERTEX_DIAMETER/2,
                              VERTEX_DIAMETER, VERTEX_DIAMETER);
        }
        final ImageIcon imageIcon = new ImageIcon( image );
        return new JLabel( imageIcon );
    }

	public static void main(String[] args) throws RemoteException, MalformedURLException, NotBoundException {

		String domain = (args.length > 0)? args[0] : "localhost";
		int taskNum = (args.length > 1)? Integer.parseInt(args[1]) : 0;
		String logName = (args.length > 2)? args[2] : "TSP";
		
		System.out.println("Starting Client 'TSP' on Space @ "+domain+" Running Task "+taskNum);
		
		Log log = new Log(logName);
		JobTSP job = JOBS[taskNum];
        job.setLog(log);
        
		ClientTSP clientTSP;
		if(domain.equalsIgnoreCase("jvm"))
			clientTSP = new ClientTSP(job, new JobRunnerLocal<List<Integer>>(job, DEFAULT_NUM_LOCAL_NODES), log ); 
		else
			clientTSP = new ClientTSP(job, new JobRunnerRemote<List<Integer>>(job, domain), log ); 
		clientTSP.begin();
        final List<Integer> result = clientTSP.run();
        clientTSP.add(clientTSP.getLabel(result.toArray(new Integer[0])));
        clientTSP.end();
	}
}
