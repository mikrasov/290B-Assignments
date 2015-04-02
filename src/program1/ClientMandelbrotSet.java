package program1;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.net.MalformedURLException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import tasks.TaskMandelbrotSet;

/**
 *
 * @author Peter Cappello
 */
public class ClientMandelbrotSet extends Client<Integer[][]>
{
    private static final double LOWER_LEFT_X = -2.0;
    private static final double LOWER_LEFT_Y = -2.0;
    private static final double EDGE_LENGTH = 4.0;
    private static final int N_PIXELS = 256;
    private static final int ITERATION_LIMIT = 64;
    
    public ClientMandelbrotSet() throws RemoteException, NotBoundException, MalformedURLException 
    { 
        super( "Mandelbrot Set Visualizer", "localhost",
               new TaskMandelbrotSet( LOWER_LEFT_X, LOWER_LEFT_Y, EDGE_LENGTH, N_PIXELS, 
                                                       ITERATION_LIMIT) ); 
    }
    
    /**
     * Run the MandelbrotSet visualizer client.
     * @param args unused 
     * @throws java.rmi.RemoteException 
     */
    public static void main( String[] args ) throws Exception
    {  
        System.setSecurityManager( new SecurityManager() );
        final ClientMandelbrotSet client = new ClientMandelbrotSet();
        client.begin();
        Integer[][] value = client.runTask();
        client.add( client.getLabel( value ) );
        client.end();
    }
    
    public JLabel getLabel( Integer[][] counts )
    {
        final Image image = new BufferedImage( N_PIXELS, N_PIXELS, BufferedImage.TYPE_INT_ARGB );
        final Graphics graphics = image.getGraphics();
        for ( int i = 0; i < counts.length; i++ )
            for ( int j = 0; j < counts.length; j++ )
            {
                graphics.setColor( getColor( counts[i][j] ) );
                graphics.fillRect( i, N_PIXELS - j, 1, 1 );
            }
        final ImageIcon imageIcon = new ImageIcon( image );
        return new JLabel( imageIcon );
    }
    
    private Color getColor( int iterationCount )
    {
        return iterationCount == ITERATION_LIMIT ? Color.BLACK : Color.WHITE;
    }
}
