package client;

import java.awt.Color;
import java.net.MalformedURLException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.List;

import javax.swing.ImageIcon;

import tasks.TaskTsp;
import tasks.ChunkTsp;


public class ClientTsp extends Client<ChunkTsp>{

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

	public ClientTsp(String domain) throws MalformedURLException, RemoteException, NotBoundException {
		super("TSP", domain, new TaskTsp(CITIES));
	}

	@Override
	public String toString() {
		return "";
	}

	public JLabel getLabel( final Integer[] tour )
    {
        Logger.getLogger( ClientEuclideanTsp.class.getCanonicalName() ).log(Level.INFO, tourToString( tour ) );

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
    
    private String tourToString( Integer[] cities )
    {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append( "Tour: " );
        for ( Integer city : cities )
        {
            stringBuilder.append( city ).append( ' ' );
        }
        return stringBuilder.toString();
    }

	public static void main(String[] args) throws RemoteException{
		
		String domain = (args.length > 0)? args[0] : "localhost";
		
		ClientTsp client = null;
		try {
			client = new ClientTsp(domain);
		} catch (MalformedURLException | RemoteException | NotBoundException e) {
			System.err.println("No Space found at '"+domain+"'");
			System.err.println(e);
			System.exit(0);
		}
		client.begin();
		ChunkTsp result = client.run();
		List<Integer> finalCities = result.getValue().getBestOrder();
		client.add( client.getLabel( finalCities.toArray( new Integer[0] ) ) );
		client.end();
		
		Log.log(client +", Result: "+result);
		System.out.println(client +" = "+result);
		client.closeLog();
	}
}