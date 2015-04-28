package client;

import java.awt.BorderLayout;
import java.awt.Container;
import java.net.MalformedURLException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;

import util.Log;
import api.Closure;

public class Client<R> extends JFrame{

	 /** Generated Serial ID	 */
	private static final long serialVersionUID = 6912770951537378627L;

	final protected JobRunner<R> jobRunner;
    
    protected R taskReturnValue;
    private long clientStartTime;
    private Log log;

	public Client( final String title, String domain, Closure<R> task, int numComputers, Log log ) throws MalformedURLException, RemoteException, NotBoundException  {
        setTitle( title );
        setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
        this.log = log;

        if(domain.equalsIgnoreCase("jvm")){
            this.jobRunner = new JobRunnerLocal<R>(task, numComputers, log);
        } else {
            this.jobRunner = new JobRunnerRemote<R>(domain, task);

        }
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
    	log.log("Component, Time (ms)");
    	clientStartTime = System.nanoTime(); 
    	R result = jobRunner.run();
    	log.log( "Client Total,"+( System.nanoTime() - clientStartTime) / 1000000.0 );
    	log.log("");
    	log.close();
    	return result;
    }
}
