package de.ssc.brackets.data;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.log4j.Logger;

import de.ssc.brackets.settings.BS_CLConstants;

public class ImageData
{
	private static Logger logger = Logger.getLogger(ImageData.class);
	private File imageFile = null;
	private String exp = "";
	private Path copySourcePath = null;
	private String expDouble = "";
	
	public ImageData(File imageFile)
	{
		this.imageFile = imageFile;
		this.setCopySourcePath(Paths.get(BS_CLConstants.actualPath+imageFile.getName()));
	}

	public File getImageFile()
	{
		return imageFile;
	}

	public void setImageFile(File imageFile)
	{
		this.imageFile = imageFile;
	}

	public String getExp()
	{
		return exp;
	}

	public void setExp(String exp)
	{
		this.exp = exp;
	}

	public Path getCopySourcePath()
	{
		return copySourcePath;
	}

	public void setCopySourcePath(Path copySourcePath)
	{
		this.copySourcePath = copySourcePath;
	}

	public String getExpDouble()
	{
		return expDouble;
	}

	public void setExpDouble(String expDouble)
	{
		this.expDouble = expDouble;
	}
}