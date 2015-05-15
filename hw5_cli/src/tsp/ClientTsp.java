package tsp;

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

import util.Log;
import api.Result;
import api.SharedState;
import api.Space;

public class ClientTsp extends JFrame{

	/** Serial ID	 */
	private static final long serialVersionUID = 6911008092238762097L;
	private static final int NUM_PIXALS = 600;
  
	private static final double[][] CITIES_12 = 
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
	
	private static final double[][] CITIES_13 = 
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
        { 3, 6 },
		{ 4, 4 },
    };
	
	private static final double[][] CITIES_16 = 
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
		{ 3, 6 },
		{ 4, 4 },
		{ 5, 4 },
		{ 5, 5 },
		{ 4, 5 }
	};
		
	protected Space<ChunkTsp> space;
	protected double[][] cities;
	
	public ClientTsp(Space<ChunkTsp> space, double[][] cities) {
		setTitle( "TSP" );
		setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
		this.space = space;
		this.cities = cities;
	}

	public JLabel getLabel( final Integer[] tour ) {
        // display the graph graphically, as it were
        // get minX, maxX, minY, maxY, assuming they 0.0 <= mins
        double minX = cities[0][0], maxX = cities[0][0];
        double minY = cities[0][1], maxY = cities[0][1];
        for ( double[] city : cities )
        {
            if ( city[0] < minX )
                minX = city[0];
            if ( city[0] > maxX )
                maxX = city[0];
            if ( city[1] < minY )
                minY = city[1];
            if ( city[1] > maxY )
                maxY = city[1];
        }

        // scale points to fit in unit square
        final double side = Math.max( maxX - minX, maxY - minY );
        double[][] scaledCities = new double[cities.length][2];
        for ( int i = 0; i < cities.length; i++ )
        {
            scaledCities[i][0] = ( cities[i][0] - minX ) / side;
            scaledCities[i][1] = ( cities[i][1] - minY ) / side;
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
        for ( int i = 1; i < cities.length; i++ )
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
        for ( int i = 0; i < cities.length; i++ )
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
	
    public void add( final JLabel jLabel ) {
        final Container container = getContentPane();
        container.setLayout( new BorderLayout() );
        container.add( new JScrollPane( jLabel ), BorderLayout.CENTER );
        pack();
        setVisible( true );
    }

    @SuppressWarnings("unchecked")
	public static void main(String[] args) throws RemoteException, InterruptedException, MalformedURLException, NotBoundException{
		String domain = (args.length > 0)? args[0] : "localhost";
		int numCities = (args.length > 1)? Integer.parseInt(args[1]) : CITIES_16.length;
		boolean branchAndBound = (args.length > 2)? Boolean.parseBoolean(args[2]) : true;
		
		Log.startLog("tsp-client.csv");
		System.out.println("Starting Client Targeting Space @ "+domain);

		String url = "rmi://" + domain + ":" + Space.DEFAULT_PORT + "/" + Space.DEFAULT_NAME;
		
		Space<ChunkTsp> space = (Space<ChunkTsp>) Naming.lookup(url);  
		double[][] cities = (numCities == 12? CITIES_12: (numCities == 13?CITIES_13: CITIES_16));
		
		ClientTsp client = new ClientTsp(space, cities );
		
		System.out.println("Number of Cities:\t"+cities.length);

		Log.log("Component, Time (ms)");
    
		long clientStartTime = System.nanoTime();
		
		SharedState initial = branchAndBound? new StateTsp(cities): new StateTspStatic(cities);
		space.setTask( new TaskTsp(cities), initial);
		Result<ChunkTsp> result =  space.getSolution();
         
		
		List<Integer> finalCities = result.getValue().getBestOrder();
		client.add( client.getLabel( finalCities.toArray( new Integer[0] ) ) );
		
		Log.log("TSP, Result: "+result.getValue());
		Log.log( "Client Time,"+( System.nanoTime() - clientStartTime) / 1000000.0 );
		Log.log("T1, "+result.getRunTime());
		Log.log("Tinf, "+result.getCriticalLengthOfParents());
		Log.close();
	}
}
