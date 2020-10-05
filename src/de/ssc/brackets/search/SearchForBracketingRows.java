package de.ssc.brackets.search;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.sanselan.ImageReadException;
import org.apache.sanselan.Sanselan;
import org.apache.sanselan.common.IImageMetadata;
import org.apache.sanselan.formats.jpeg.JpegImageMetadata;
import org.apache.sanselan.formats.tiff.TiffField;
import org.apache.sanselan.formats.tiff.constants.ExifTagConstants;

import de.ssc.brackets.data.ImageData;
import de.ssc.brackets.settings.BS_CLConstants;

public class SearchForBracketingRows {
	private static Logger logger = Logger.getLogger(SearchForBracketingRows.class);
	private static List<ImageData> images = new ArrayList<ImageData>();

	public void scannFolder(String folder) {
		logger.info("suche in "+folder);
		if(!folder.endsWith("\\")) folder = folder+"\\";
		BS_CLConstants.actualPath = folder;
		List<ImageData[]> bracketingRow = new ArrayList<ImageData[]>();
		int counterBracketing = 0;
		images.clear();
		String[] imageFiles = readImageFilesFromFolder(BS_CLConstants.actualPath, "JPG");
		for (int i = 0; i < imageFiles.length; i++) {
			String exp = "";
			String expDouble = "";
			File file = new File(BS_CLConstants.actualPath+imageFiles[i]);
			try {
				ImageData image = new ImageData(file);
				IImageMetadata meta = Sanselan.getMetadata(file);
				if (meta instanceof JpegImageMetadata) {
					TiffField exposure = ((JpegImageMetadata) meta).findEXIFValue(ExifTagConstants.EXIF_TAG_EXPOSURE_COMPENSATION);
					if(exposure != null) {
				    	exp = String.valueOf(exposure.getIntValue());
				    	expDouble = String.valueOf(exposure.getDoubleValue());
				    	logger.info("file "+imageFiles[i]+" exp "+exp+" double "+expDouble);
				    	image.setExp(exp);
				    	image.setExpDouble(expDouble);
				    }
				}
				images.add(image);
			}
			catch (ImageReadException e) {
				e.printStackTrace();
			}
			catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		//TODO suche nach Belichtungsreihen 
		List<Integer> bracketingRows = new ArrayList<Integer>();
		for (int i = 0; i < BS_CLConstants.expTypes.size(); i++) {
			int number = BS_CLConstants.expTypes.get(i).length;
			logger.info("Belichtungsreihe aus "+number+" Bildern");
			if(!bracketingRows.contains(number)) {
				bracketingRows.add(number);
			}
		}
		Collections.sort(bracketingRows, Collections.reverseOrder());
		for (Integer br : bracketingRows) {
			logger.info("Suche nach Belichtungsreihen mit "+br+" Aufnahmen...");
			ImageData[] filesFound = new ImageData[br];
			
			for (int i = 0; i < images.size(); i++) {
				for (int j = 0; j < br; j++) {
					if(i+j < images.size()) {
						filesFound[j] = images.get(i+j);
						//System.out.println(images.get(i+j).getCopySourcePath().toString()+images.get(i+j).getExp());
					}
				}
				for (int j = 0; j < BS_CLConstants.expTypes.size(); j++) {
					if(BS_CLConstants.expTypes.get(j).length == br) {
						String [] expValues = BS_CLConstants.expTypes.get(j);
						boolean [] hits = new boolean[br];
						for (int k = 0; k < expValues.length; k++) {
							if(filesFound[k] != null) {
								if(expValues[k].equals(filesFound[k].getExp())) {
									hits[k] = true;
								}
								if(expValues[k].equals(filesFound[k].getExpDouble())) {
									hits[k] = true;
								}	
							}
						}
						boolean hit = true;
						for (int k = 0; k < hits.length; k++) {
							if(!hits[k]) {
								hit = false;
							}
						}
						if(hit && checkCorrectOrder(filesFound)) {
							logger.info("Belichtungsreihe gefunden...");
							bracketingRow.add(filesFound);
							for (int k = 0; k < filesFound.length; k++) {
								logger.info(filesFound[k].getCopySourcePath().toString());
								logger.info(filesFound[k].getExp());
							}
							filesFound = new ImageData[br];
						}
					}
				}
				//logger.info(i+" + br = "+(i+br)+" gesamt "+images.size());
			}
		}
		//showFiles(bracketingRow);
		copyBracketingRows(bracketingRow);
	}
	
	private String[] readImageFilesFromFolder(String foldername, String imageType) {
		File file = new File(foldername);
		String[] files = file.list(new FilenameFilter() 
		{
		  @Override
		  public boolean accept(File current, String name) {
			  File actualFile = new File(current, name);
			  if(actualFile.isFile() && actualFile.getName().toUpperCase().endsWith(imageType.toUpperCase())) {
				  return true;
			  }
			  else {
				  return false;
			  }
			  //return new File(current, name).isFile();
		  }
		});
		logger.info("in "+foldername+" "+files.length+" dateien gefunden");
		return files;
	}
	
	/**
	 * Pruefen, ob der Dateiname entsprechend hochgezaehlt wird
	 * @param filesFound
	 * @return
	 */
	private boolean checkCorrectOrder(ImageData[] filesFound) {
		String filename = filesFound[0].getImageFile().getName();
		filename = filename.substring(1, filename.lastIndexOf("."));
		int number = Integer.parseInt(filename);
		for (int i = 1; i < (filesFound.length); i++) {
			number = number + 1;
			if(!filesFound[i].getImageFile().getName().contains(String.valueOf(number))) {
				return false;
			}
		}
		return true;
	}
	
	private void copyBracketingRows(List<ImageData[]> bracketingRow) {
		logger.info(bracketingRow.size()+" gefunden");
		
		File file = new File(BS_CLConstants.actualPath+BS_CLConstants.folderHDR);
		if(!file.isDirectory()) file.mkdirs();
		
		for (int j = 0; j < bracketingRow.size(); j++) {
			ImageData[] filesHDR = bracketingRow.get(j);
			try {
				logger.info("++++++++++++++++++++++++++++++");
				logger.info("Belichtungsreihe "+j+" wird kopiert/verschoben...");
				logger.info("++++++++++++++++++++++++++++++");
				
				for (int i = 0; i < filesHDR.length; i++) {
					Path copySourcePath = Paths.get(BS_CLConstants.actualPath+filesHDR[i].getImageFile().getName());
					Path copyTargetPath = Paths.get(BS_CLConstants.actualPath+BS_CLConstants.folderHDR+File.separator+filesHDR[i].getImageFile().getName());
					if(!BS_CLConstants.move) {
						Files.copy( copySourcePath, copyTargetPath );	
					}
					else {
						Files.move(copySourcePath, copyTargetPath);
					}
				}				
			}
			catch (IOException e) {
				if(Logger.getRootLogger().getLevel().toString().equals("DEBUG")) e.printStackTrace();
				logger.error(e);
			}
		}
	}
}