package client;

import java.awt.BorderLayout;
import java.awt.Container;
import java.rmi.RemoteException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;

public class Client<T> extends JFrame
{
    /** Generated Serial ID	 */
	private static final long serialVersionUID = 6912770951537378627L;

	final protected JobRunner<T> jobRunner;
    
    protected T taskReturnValue;
    private long clientStartTime;

	public Client( final String title, final JobRunner<T> jobRunner ) 
    {     
        setTitle( title );
        setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
        
        this.jobRunner = jobRunner;
    }
    
    public void begin() { clientStartTime = System.nanoTime(); }
    
    public void end() 
    { 
        Logger.getLogger( Client.class.getCanonicalName() )
            .log(Level.INFO, "Client time: {0} ms.", ( System.nanoTime() - clientStartTime) / 1000000 );
    }
    
    public void add( final JLabel jLabel )
    {
        final Container container = getContentPane();
        container.setLayout( new BorderLayout() );
        container.add( new JScrollPane( jLabel ), BorderLayout.CENTER );
        pack();
        setVisible( true );
    }
  
    public T run() throws RemoteException{
    	return jobRunner.run();
    }
	

}
