package client;

import java.io.FileWriter;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Log {

	private FileWriter log;
	
	public Log() {}
	
	public Log(String logName) {
		System.out.println("Starting log at: "+logName);
		try {	
			log = new FileWriter(logName+".csv");
		} catch (IOException e) {
			System.err.println("Error Writing log: "+logName);
		}
	}

	public void log(String line){
    	Logger.getLogger( Client.class.getCanonicalName() )
        .log(Level.INFO, line);
    
    	if(log != null) try {
			log.write(line+"\n");
		} catch (IOException e) {
			System.err.println("Error Writing to log");
		}
    }
	
	public void close(){
		if(log != null) try {
			log.close();
		} catch (IOException e) {
			System.err.println("Error closing log");
		}
		System.out.println("Ended log");
	}
}
