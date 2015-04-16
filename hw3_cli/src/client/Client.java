package client;

import java.awt.BorderLayout;
import java.awt.Container;
import java.io.FileWriter;
import java.io.IOException;
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

    private Log log;
    
	public Client( final String title, final JobRunner<T> jobRunner, final Log log ) 
    {     
		this.log = log;
        setTitle( title );
        setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
        
        this.jobRunner = jobRunner;
    }
    
    public void begin() { 
    	log.log("Component, Time (ms)");
    	clientStartTime = System.nanoTime(); 
    }
    
    public void end()  { 
    	log.log( "Client Total,"+( System.nanoTime() - clientStartTime) / 1000000.0 );
        log.log("");
    	log.close();
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
