package client;

import java.io.FileWriter;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Log {

	public static final boolean VERBOSE_DEBUG = true;
	public static final String LOG_NAME = "fib.csv";
	
	
	private static FileWriter log;
	static{
		try {
			log = new FileWriter(LOG_NAME);
		} catch (IOException e) {
			System.err.println("Error starting log '"+LOG_NAME+"'");
		}		
	}
	
	public static void log(String line){
    	Logger.getLogger( Client.class.getCanonicalName() )
        .log(Level.INFO, line);
    
    	if(log != null) try {
			log.write(line+"\n");
		} catch (IOException e) {
			System.err.println("Error Writing to log");
		}
    }
	
	public static void debug(String str){
		if(VERBOSE_DEBUG) System.out.println(str);
	}
	
	public static void debugln(String str){
		debug(str+"\n");
	}
	
	public static void close(){
		if(log != null) try {
			log.close();
		} catch (IOException e) {
			System.err.println("Error closing log");
		}
		debugln("Ended log");
	}
}
