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
	
	private static final double LOWER_LEFT_X = -0.7510975859375;
    private static final double LOWER_LEFT_Y = 0.1315680625;
    private static final double EDGE_LENGTH = 0.01611;
    private static final int N_PIXELS = 1024;
    private static final int ITERATION_LIMIT = 512;
	
	public ClientMandelbrot(String domainName) throws RemoteException, MalformedURLException, NotBoundException{
		super("Mandelbrot Set Visualizer", new JobRunner<Integer[][]>(new JobMandelbrot(LOWER_LEFT_X, LOWER_LEFT_Y, EDGE_LENGTH, N_PIXELS, ITERATION_LIMIT), domainName));
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

	public static void main(String[] args) throws RemoteException, MalformedURLException, NotBoundException {

		String domain;
		if(args.length > 0)
			domain = args[0];
		else
			domain = "localhost";
		
		System.out.println("Starting Client 'Mandelbrot' on Space @ "+domain);
		
        ClientMandelbrot clientMandelbrot = new ClientMandelbrot(domain);
        clientMandelbrot.begin();
        Integer[][] result = clientMandelbrot.run();
        clientMandelbrot.add(clientMandelbrot.getLabel(result));
        clientMandelbrot.end();
	}
}
