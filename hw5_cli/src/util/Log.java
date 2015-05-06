package util;

import java.io.FileWriter;
import java.io.IOException;

public class Log {

	public static final boolean VERBOSE_DEBUG = false;
	
	private static FileWriter log;
	
	public static void startLog(String filename){
		try {
			log = new FileWriter(filename);
		} catch (IOException e) {
			System.err.println("Error starting log '"+filename+"'");
		}
	}
	
	public static void log(String line){
    	System.out.println(line);
    	
    	if(log != null) try {
			log.write(line+"\n");
		} catch (IOException e) {
			System.out.println("Error Writing to log");
		}
    }
	
	public static void debug(String str){
		if(VERBOSE_DEBUG) System.err.println(" "+str);
	}
	
	public static void close(){
		if(log != null) try {
			log.close();
		} catch (IOException e) {
			System.err.println("Error closing log");
		}
		debug("Ended log");
	}
	
	public static void flush(){
		if(log != null) try {
			log.flush();
		} catch (IOException e) {
			System.err.println("Error flushing log");
		}
	}
}