package client;

import api.Closure;
import api.Space;
import util.Log;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;

/**
 * Created by Roman on 4/27/15.
 */
public class JobRunnerRemote<R> extends JobRunner<R> {

    private static final int CHECK_TIME = 100;

    public JobRunnerRemote(String domain, Closure<R> task) throws MalformedURLException, RemoteException, NotBoundException {
        super(task);

        String url = "rmi://" + domain + ":" + Space.PORT + "/" + Space.SERVICE_NAME;
        space = (Space<R>) Naming.lookup(url);
    }
}
