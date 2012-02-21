package com.dreasyLib.persistence;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class ShareObjectManager {
	private Context myContext;
	private SharedPreferences sharedPrefs;

	private static String SHARED_NAME="SharedPrefs";
	
	public ShareObjectManager(Context p_context) {
		super();
		this.myContext = p_context;
		sharedPrefs=myContext.getSharedPreferences(SHARED_NAME, 0);
	}
	
	public String LoadConfig(String key)
	{
		return sharedPrefs.getString(key, "");
	}
	
	public void SaveConfig(String key,String value)
	{
		Editor edit = sharedPrefs.edit();
		edit.putString(key, value);
		edit.commit();
	}	
	
	
}
