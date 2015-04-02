package program1;


import java.awt.BorderLayout;
import java.awt.Container;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;

/**
 *
 * @author Peter Cappello
 * @param <T> return type the Task that this Client executes.
 */
public class Client<T> extends JFrame
{
    final protected Task<T> task;
          final private Computer computer;
                protected T taskReturnValue;
                private long clientStartTime;
    
    public Client( final String title, final String domainName, final Task<T> task ) 
            throws RemoteException, NotBoundException, MalformedURLException
    {     
        this.task = task;
        setTitle( title );
        setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
        
        String url = "rmi://" + domainName + ":" + Computer.PORT + "/" + Computer.SERVICE_NAME;
        computer = ( domainName == null ) ? new ComputerImpl() : (Computer) Naming.lookup( url );
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
    
    public T runTask() throws RemoteException
    {
        computer.execute( task );
        final long taskStartTime = System.nanoTime();
        final T value = computer.execute( task );
        final long taskRunTime = ( System.nanoTime() - taskStartTime ) / 1000000;
        Logger.getLogger( Client.class.getCanonicalName() )
            .log(Level.INFO, "Task {0}Task time: {1} ms.", new Object[]{ task, taskRunTime } );
        return value;
    }
}
