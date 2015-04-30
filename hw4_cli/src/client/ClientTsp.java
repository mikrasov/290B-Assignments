package client;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;

import api.Space;
import api.Task;
import tasks.ChunkTsp;
import tasks.TaskTsp;
import util.Log;

public class ClientTsp extends JFrame{

	/** Serial ID	 */
	private static final long serialVersionUID = 6911008092238762097L;
	private static final int NUM_PIXALS = 600;
    
	private static final double[][] CITIES = 
    {
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
    };
	
	protected Space<ChunkTsp> space;

	public ClientTsp(String domain) throws MalformedURLException, RemoteException, NotBoundException {
		setTitle( "TSP" );
		setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
		String url = "rmi://" + domain + ":" + Space.DEFAULT_PORT + "/" + Space.DEFAULT_NAME;
		space = (Space<ChunkTsp>) Naming.lookup(url);  
	}

	public JLabel getLabel( final Integer[] tour )
    {
        // display the graph graphically, as it were
        // get minX, maxX, minY, maxY, assuming they 0.0 <= mins
        double minX = CITIES[0][0], maxX = CITIES[0][0];
        double minY = CITIES[0][1], maxY = CITIES[0][1];
        for ( double[] cities : CITIES ) 
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
        double[][] scaledCities = new double[CITIES.length][2];
        for ( int i = 0; i < CITIES.length; i++ )
        {
            scaledCities[i][0] = ( CITIES[i][0] - minX ) / side;
            scaledCities[i][1] = ( CITIES[i][1] - minY ) / side;
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
        for ( int i = 1; i < CITIES.length; i++ )
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
        for ( int i = 0; i < CITIES.length; i++ )
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
	
	 
    public void add( final JLabel jLabel )
    {
        final Container container = getContentPane();
        container.setLayout( new BorderLayout() );
        container.add( new JScrollPane( jLabel ), BorderLayout.CENTER );
        pack();
        setVisible( true );
    }
    
    
    public ChunkTsp runTask(double[][] cities) throws RemoteException, InterruptedException{
    	
    	space.setTask( new TaskTsp(cities));
        return space.getSolution();
    }

	public static void main(String[] args) throws RemoteException, InterruptedException{
		String domain = (args.length > 0)? args[0] : "localhost";

		ClientTsp client = null;
		try {
			client = new ClientTsp(domain);
		} catch (MalformedURLException | RemoteException | NotBoundException e)  {
            System.err.println("No Space found at '"+domain+"'");
            System.err.println(e);
            System.exit(0);
        } 

		Log.startLog("tsp-client.csv");
		Log.log("Component, Time (ms)");
    
		long clientStartTime = System.nanoTime(); 
		ChunkTsp result = client.runTask(CITIES);
         
		List<Integer> finalCities = result.getBestOrder();
		client.add( client.getLabel( finalCities.toArray( new Integer[0] ) ) );
		
		Log.log(client +", Result: "+result);
		System.out.println(client +" = "+result);
		Log.log( "Client Total,"+( System.nanoTime() - clientStartTime) / 1000000.0 );
		Log.close();
	}
}
