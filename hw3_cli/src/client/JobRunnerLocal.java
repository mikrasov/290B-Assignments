package client;

import api.Closure;
import system.ComputerImp;
import system.SpaceImp;
import util.Log;

import java.net.MalformedURLException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;

/**
 * Created by Roman on 4/27/15.
 */
public class JobRunnerLocal<R> extends JobRunner<R> {

    int numComputers;

    public JobRunnerLocal(Closure<R> task, int numComputers, Log log) throws MalformedURLException, RemoteException, NotBoundException {
        super(task);
        this.numComputers = numComputers;
        this.space = new SpaceImp<R>(log);

        //Start local Space
        Thread spaceRunner = new Thread( new Runnable() {

            @Override
            public void run() {
                try {
                    space.startSpace();
                } catch (RemoteException e) {}
            }
        });

        spaceRunner.start();

        for(int i=0; i<numComputers; i++)
            space.register( new ComputerImp("Local Computer"+i));

    }
}
