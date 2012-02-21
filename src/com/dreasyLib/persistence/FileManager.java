package com.dreasyLib.persistence;

import java.io.File;

public class FileManager {

	private String appDirectory="/mnt/sdcard/testapp";
	private File _appFilePath;
	

	public FileManager(String p_appDirectory) {
		super();
		appDirectory = p_appDirectory;
		_appFilePath=new File(appDirectory);
		if (!_appFilePath.exists())
		{
			_appFilePath.mkdir();
		}
	}

	
	public String GetFilePath(String p)
	{
		return appDirectory+"/"+p;
	}
	public String Mkdir(String path)
	{
		File newDir=new File(appDirectory+path);
		newDir.mkdirs();
		return appDirectory+path;
		/*
Log.d("TESTAPP", "LocalDir["+appDirectory+path+"]");		
		if (newDir.canWrite())
		{
Log.d("TESTAPP", "LocalDir["+appDirectory+path+"]");	
			newDir.mkdirs();
			newDir.mkdirs();
		}else
		{
			return "";
		}
		*/
	}
	
	public boolean ExistDir(String path)
	{
		File newDir=new File(appDirectory+path);
		return newDir.exists();
	}

	public boolean ExistFile(String path)
	{
		File newDir=new File(appDirectory+path);
		return newDir.exists();
	}	
	
}
