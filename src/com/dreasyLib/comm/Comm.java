package com.dreasyLib.comm;

import java.io.File;
import java.io.FileInputStream;
import java.util.Hashtable;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.client.CookieStore;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.params.ClientPNames;
import org.apache.http.cookie.Cookie;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.InputStreamBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import android.util.Log;

public class Comm extends Thread {
	public static final int METHOD_DEFAULT=0;
	public static final int METHOD_GET=1;
	public static final int METHOD_POST=2;
	public static final int METHOD_AUTH_GAE=3;
	public static final int METHOD_UPLOAD=4;
	public static final String PARAM_URL_PARAMS="url_params";
	public static final String PARAM_POST_CONTENT="post_content";	
	public static final String PARAM_AUTH_COOKIE="auth_prams";	
	public static final String PARAM_AUTH_TOKEN="auth_prams";	
	public static final String PARAM_UPLOAD_FILE="upload_url";	
	private int _method;
	String _Uri;
	Hashtable _Params;
	OnCommEvent _CommEvent;

	public interface OnCommEvent
	{

		public void OnMessage(String string);
		public void OnComplete(Object response);
		public void OnProcess(int percent, String data);
		public void OnError(Error error);
	}

	public Comm (int Method, String p_uri, Hashtable params, OnCommEvent myCommEvent) {
		super();
		_method=Method;
		_Uri=p_uri;
		_CommEvent=myCommEvent;
		_Params=params;
		start();
	}

	public void run () {
		switch (_method) {
		case METHOD_DEFAULT:
			Comm._Get(_Uri,_Params,_CommEvent);
			break;
		case METHOD_GET:
			Comm._Get(_Uri,_Params,_CommEvent);
			break;
		case METHOD_POST:
			Comm._Post(_Uri,_Params,_CommEvent);
			break;		
		case METHOD_AUTH_GAE:
			Comm._authGae(_Uri,_Params,_CommEvent);
			break;	
		case METHOD_UPLOAD:
			Comm._UploadFile(_Uri,_Params,_CommEvent);
			break;	
		default:
			break;
		}
	}


	private static void _authGae(String _Uri, Hashtable _Params,	OnCommEvent _CommEvent) {
		if (!_Params.containsKey(PARAM_AUTH_TOKEN))
		{
			_CommEvent.OnError(new Error("NO_TOKEN"));
		}else{
			DefaultHttpClient client=new DefaultHttpClient();
			String token=(String)_Params.get(PARAM_AUTH_TOKEN);
			client.getParams().setBooleanParameter(ClientPNames.HANDLE_REDIRECTS, false);
			String url=_Uri+"/_ah/login?continue=http://localhost/&auth=" + token;
			Log.d("Tag", "CompleteUrl"+url);
			HttpGet http_get = new HttpGet(url);
			HttpResponse response;
			try {
				response = client.execute(http_get);
				if(response.getStatusLine().getStatusCode() != 302){
					_CommEvent.OnError(new Error("ERROR_EXEC:"+String.valueOf(response.getStatusLine().getStatusCode())));
				}else
				{
					boolean done=false;
					for(Cookie cookie : client.getCookieStore().getCookies()) {
						Log.d("TAG","cookie:"+cookie.getName()+"/"+cookie.getValue());
						if ((cookie.getName().equals("ACSID"))||(cookie.getName().equals("SACSID")))
						{
							done=true;
							Log.d("TAG","Sending cookie");
							_CommEvent.OnComplete(client.getCookieStore());
						}
					}
					if (!done){
						_CommEvent.OnError(new Error("NO_COOKIE_FOUND"));
					}
				}
			} catch (Exception e) {
				_CommEvent.OnError(new Error(e.getMessage(),e.getCause()));
			}
		}
	}

	static public  void GET(String url,Hashtable data,OnCommEvent myCommEvent)
	{
		new Comm(METHOD_GET, url, data, myCommEvent);
	}

	static public void POST(String url,Hashtable data,OnCommEvent myCommEvent)
	{
		new Comm(METHOD_POST, url, data, myCommEvent);
	}
	static public  void AUTH_GAE(String url,Hashtable data,OnCommEvent myCommEvent)
	{
		new Comm(METHOD_AUTH_GAE, url, data, myCommEvent);
	}
	
	static public  void UPLOAD(String url,Hashtable data,OnCommEvent myCommEvent)
	{
		new Comm(METHOD_UPLOAD, url, data, myCommEvent);
	}
	
	static private void _UploadFile(String uri,Hashtable params,OnCommEvent myCommEvent)
	{//apikey 8fa56c86424ccda4dee0107ffbe4a1b1
		Log.d("sending","sending file");
		if (params.containsKey(PARAM_UPLOAD_FILE))
		{
			Log.d("sending", (String)params.get(PARAM_UPLOAD_FILE));
			String urlFile=(String)params.get(PARAM_UPLOAD_FILE);
			String url=uri;
			DefaultHttpClient client=new DefaultHttpClient();
			String postContent="";
			if ((params!=null)&&(params.containsKey(PARAM_URL_PARAMS)))
			{
				url= UrlTools.encodeUrl(uri, (List<BasicNameValuePair>) params.get(PARAM_URL_PARAMS));
			}
			if ((params!=null)&&(params.containsKey(PARAM_POST_CONTENT)))
			{
				postContent=(String)params.get(PARAM_POST_CONTENT);
			}
			
			
			try {
				FileInputStream fileInputStream = new FileInputStream(urlFile);
				File fd=new File(urlFile);
				Log.d("sending", "uploading:"+fd.getName()+" with size:"+String.valueOf(fd.length()));
				HttpPost httpPost=new HttpPost(url);
				ExtendedInputStream exin=new ExtendedInputStream(fileInputStream, myCommEvent, fd.length());
				MultipartEntity mpe=new MultipartEntity();
				//TODO create entity dinamicaly from params
				mpe.addPart("key", new StringBody("8fa56c86424ccda4dee0107ffbe4a1b1"));
				mpe.addPart("image",new InputStreamBody(exin,"photo.jpg"));
			
				httpPost.setEntity(mpe);
				HttpResponse response=client.execute(httpPost);
				String output=EntityUtils.toString(response.getEntity());
				myCommEvent.OnComplete(output);
			} catch (Exception e) {
				Log.d("sending","FAIL!"+e.getMessage());
				myCommEvent.OnError(new Error(e.getMessage(), e.getCause()));
			}
		
		}else{
			Log.d("sending", "NO FILE!");
		}

	}



	static private void _Get(String uri,Hashtable params,OnCommEvent myCommEvent)
	{
		String url=uri;
		DefaultHttpClient client=new DefaultHttpClient();
		if ((params!=null)&&(params.containsKey(PARAM_URL_PARAMS)))
		{
			url= UrlTools.encodeUrl(uri, (List<BasicNameValuePair>) params.get(PARAM_URL_PARAMS));
		}
		HttpGet httpGet=new HttpGet(url);
		if ((params!=null)&&(params.containsKey(PARAM_AUTH_COOKIE)))
		{
			client.setCookieStore((CookieStore) params.get(PARAM_AUTH_COOKIE));
		}
		try {
			myCommEvent.OnProcess(5,"");
			HttpResponse httpResponse= client.execute(httpGet);
			myCommEvent.OnProcess(100,"");
			String output=EntityUtils.toString(httpResponse.getEntity());
			myCommEvent.OnComplete(output);
		} catch (Exception e) {
			myCommEvent.OnError(new Error(e.getMessage(), e.getCause()));
		}
	}

	static private void _Post(String uri,Hashtable params,OnCommEvent myCommEvent)
	{
		String url=uri;
		DefaultHttpClient client=new DefaultHttpClient();
		String postContent="";
		if ((params!=null)&&(params.containsKey(PARAM_URL_PARAMS)))
		{
			url= UrlTools.encodeUrl(uri, (List<BasicNameValuePair>) params.get(PARAM_URL_PARAMS));
		}
		if ((params!=null)&&(params.containsKey(PARAM_POST_CONTENT)))
		{
			postContent=(String)params.get(PARAM_POST_CONTENT);
		}
		HttpPost httpPost=new HttpPost(url);
		try {
			myCommEvent.OnProcess(5,"");
			httpPost.setEntity(new StringEntity(postContent));
			//TODO intentarlo con multipart, para chequear la subida
			HttpResponse httpResponse= client.execute(httpPost);
			myCommEvent.OnProcess(100,"");
			String output=EntityUtils.toString(httpResponse.getEntity());
			myCommEvent.OnComplete(output);

		} catch (Exception e) {
			myCommEvent.OnError(new Error(e.getMessage(), e.getCause()));
		}
	}



}
