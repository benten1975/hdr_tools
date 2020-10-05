package de.ssc.brackets.settings;

import java.util.ArrayList;
import java.util.List;

public class BS_CLConstants {
	public static String fileNameConf = "conf.properties";
	
	/**
	 * Definition wie die Belichtungsreihen aussehen muessen
	 */
	public static List<String[]> expTypes = new ArrayList<String[]>();
	//public static String[] exps = new String[3];
	public static String[] exps = new String[5];
	
	public static String [] pic_types;
	
	/**
	 * Verzeichnis in das die gefundenen Belichtungsreihen kopiert/verschoben werden
	 */
	public static String folderHDR = "HDR";
	
	public static String actualPath = "";
	
	public static boolean move = true;
}
