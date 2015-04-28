package util;

import java.io.FileWriter;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import client.Client;

public class Log {

	public static final boolean VERBOSE_DEBUG = true;
	
	private static FileWriter log;

    public Log(String logName) {
        System.out.println("Starting log at: "+logName);
        try {
            log = new FileWriter(logName+".csv");
        } catch (IOException e) {
            System.err.println("Error Writing log: "+logName);
        }
    }
	
	public void startLog(String filename){
		try {
			log = new FileWriter(filename);
		} catch (IOException e) {
			System.err.println("Error starting log '"+filename+"'");
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
	
	public static void debug(String str){
		if(VERBOSE_DEBUG) System.out.println(str);
	}
	
	public static void debugln(String str){
		debug(str+"\n");
	}
	
	public void close(){
		if(log != null) try {
			log.close();
		} catch (IOException e) {
			System.err.println("Error closing log");
		}
		debugln("Ended log");
	}
	
	public static void flush(){
		if(log != null) try {
			log.flush();
		} catch (IOException e) {
			System.err.println("Error flushing log");
		}
	}
}
