package com.dreasyLib.comm;

import android.os.Bundle;
import android.os.Message;

public class MessageUtils {

	public static final String DATA="data";
	public static final String PROGRESS="progress";
	public static final String TYPE="type";
	public static final String EVENT="event";
	public static final int MSG_COMPLETE=0;
	public static final int MSG_PROGRESS=1;	
	public static final int MSG_ERROR=2;

	public static final int EVT_POST=0;
	public static final int EVT_DOWLOAD=1;	
	public static final int EVT_UPLOAD=2;	
	
	
	public static Message OnErrorMsg(String p_string,int p_type)
	{
		Message mess=new Message();
		Bundle b = new Bundle();
		b.putInt(EVENT, p_type);
		b.putInt(TYPE, MSG_ERROR);
		b.putString(DATA,p_string);
		mess.setData(b);
		return mess;
	}	
	
	public static Message OnCompleteMsg(String p_string,int p_type)
	{
		Message mess=new Message();
		Bundle b = new Bundle();
		b.putInt(EVENT, p_type);
		b.putInt(TYPE, MSG_COMPLETE);
		b.putString(DATA,p_string);
		mess.setData(b);
		return mess;
	}
	
	public static Message OnProgress(String p_string,int p_progress,int p_type)
	{
		Message mess=new Message();
		Bundle b = new Bundle();
		b.putInt(EVENT, p_type);
		b.putInt(TYPE, MSG_PROGRESS);
		b.putString(DATA,p_string);
		b.putInt(PROGRESS, p_progress);
		mess.setData(b);
		return mess;
	}
	
}
