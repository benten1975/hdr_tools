package de.ssc.brackets.start;

import java.io.File;
import java.io.FileInputStream;
import java.util.Enumeration;
import java.util.Properties;

import org.apache.log4j.Logger;

import de.ssc.brackets.search.SearchForBracketingRows;
import de.ssc.brackets.settings.BS_CLConstants;

public class BracketingSearchCLStart {
	private static Logger logger = Logger.getLogger(BracketingSearchCLStart.class);
	
	public static void main(String[] args) {
		try {
			File file = new File(BS_CLConstants.fileNameConf);
			
			if(file.exists()) {
				Properties confProperties = new Properties();
				confProperties.load(new FileInputStream(file));
				initProperties(confProperties);
				
				SearchForBracketingRows search = new SearchForBracketingRows();
				search.scannFolder(args[0]);
			}
		}
		catch (Exception e) {
			if(Logger.getRootLogger().getLevel().toString().equals("DEBUG")) e.printStackTrace();
			logger.error(e);
		}
	}

	private static void initProperties(Properties property) {
		logger.info("read properties file");
		Enumeration<Object> keys = property.keys();
		while(keys.hasMoreElements()) {
			String key = (String) keys.nextElement();
			if(key.startsWith("ExposureBracketingCount")) {
				logger.info(key);
				String keyValue = key.substring(key.indexOf("Count")+5, key.length());
				logger.info(keyValue);
				int valueOfKey = Integer.parseInt(property.getProperty(key));
				String newKey = "expValue"+keyValue+"_";
				
				logger.info("valueOfKey "+valueOfKey);
				String [] valueEXP = new String[valueOfKey];
				for (int i = 1; i <= valueOfKey; i++) {
					String actKey = newKey+i;
					logger.info("newKey "+actKey);	
					valueEXP[i-1] = property.getProperty(actKey);
				}
				BS_CLConstants.expTypes.add(valueEXP);
			}
		}
		BS_CLConstants.folderHDR = property.getProperty("folderHDR");
		if(BS_CLConstants.folderHDR == null)
		{
			BS_CLConstants.folderHDR = "HDR";
		}
		String pic_type = "";
		pic_type = property.getProperty("PIC_TYPES");
		BS_CLConstants.pic_types = pic_type.split(",");
		for (int i = 0; i < BS_CLConstants.pic_types.length; i++) {
			logger.info("BildType = "+BS_CLConstants.pic_types[i]);
		}
	}
	
}
