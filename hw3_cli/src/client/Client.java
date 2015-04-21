package client;

import java.awt.BorderLayout;
import java.awt.Container;
import java.net.MalformedURLException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;

import api.Closure;

public class Client<R> extends JFrame{

	 /** Generated Serial ID	 */
	private static final long serialVersionUID = 6912770951537378627L;

	final protected JobRunner<R> jobRunner;
    
    protected R taskReturnValue;
    private long clientStartTime;

	public Client( final String title, String domain, Closure<R> task ) throws MalformedURLException, RemoteException, NotBoundException  {     
        setTitle( title );
        setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
        
        this.jobRunner = new JobRunner<R>(domain, task);
    }
    
    public void add( final JLabel jLabel )
    {
        final Container container = getContentPane();
        container.setLayout( new BorderLayout() );
        container.add( new JScrollPane( jLabel ), BorderLayout.CENTER );
        pack();
        setVisible( true );
    }
  
    public R run() throws RemoteException{
    	Log.log("Component, Time (ms)");
    	clientStartTime = System.nanoTime(); 
    	R result = jobRunner.run();
    	Log.log( "Client Total,"+( System.nanoTime() - clientStartTime) / 1000000.0 );
    	Log.log("");
    	Log.close();
    	return result;
    }
}
