package com.dreasyLib.comm;

import java.net.URLEncoder;
import java.util.List;

import org.apache.http.message.BasicNameValuePair;

public class UrlTools {

	
	public static String encodeUrl(String baseurl,List<BasicNameValuePair> params)
	{
		String url=baseurl;
		String separator="?";
		for (BasicNameValuePair basicNameValuePair : params) {
			url=url+separator+URLEncoder.encode(basicNameValuePair.getName())+"="+URLEncoder.encode(basicNameValuePair.getValue());
			separator="&";
		}
		return url;
	}
	
	public static String getParam(List<BasicNameValuePair> params,String param)
	{
		String returned="";
		for (BasicNameValuePair basicNameValuePair : params) {
			if(basicNameValuePair.getName().equals(param))
			{
				returned=basicNameValuePair.getValue();
			}
		}
		return returned;
		
	}
}
