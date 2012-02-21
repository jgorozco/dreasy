package com.dreasyLib.comm.network;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Calendar;

import javax.net.ServerSocketFactory;

import android.os.Handler;

import com.dreasyLib.comm.MessageUtils;

public class ServerSocketThread extends Thread {

	public static int TCP=0;	
	public static int UDP=1;
	
	private int myType;//
	private int myPort;
	private Handler myHandler;
	
	//TCP elements
	public ServerSocket serversock;
	
	//UDP elements
	public DatagramSocket datSocket;
	//
	
	
	public ServerSocketThread (int type,int port,Handler handler)
	{
		myType=type;
		myHandler=handler;
		myPort=port;
	}
	

	public void run()
	{
		
		if (myType==TCP)
		{
			createServerTCP();
		}else
		{
			createServerUDP();			
		}
		
	}
	
	

	@Override
	public void destroy() {
		super.destroy();
		closeServer();
	}


	public void closeServer()
	{
		while (!serversock.isClosed())
		{
		try {
			if (myType==TCP)
			{
				serversock.close();	
			}else
			{
				closeUDPSocket();
			}
			
		} catch (Exception e) {
			myHandler.sendMessage(MessageUtils.OnErrorMsg(e.getMessage(), -1));
			e.printStackTrace();
		}
		}
	}


	private void closeUDPSocket() {
		while (!datSocket.isClosed())
		{
			datSocket.close();
			myHandler.sendMessage(MessageUtils.OnCompleteMsg("Cerando conexion UDP",-1));
		}
	}
	
	
	private void createServerTCP() {
		myHandler.sendMessage(MessageUtils.OnProgress("Servidor creado", 5, -1));
		try {
			if (serversock!=null)
			{
				serversock.close();
				serversock=null;
			}
			serversock=ServerSocketFactory.getDefault().createServerSocket(myPort);
			myHandler.sendMessage(MessageUtils.OnProgress("servidor escuchando en ["+NetworkUtils.getLocalIpAddress()+":"+String.valueOf(myPort)+"]", 10, -1));
			boolean end=false;
			while (!end)
			{
				long initCalendar=Calendar.getInstance().getTimeInMillis();
				Socket s= serversock.accept();
				myHandler.sendMessage(MessageUtils.OnProgress("Conexion aceptada en ["+String.valueOf(s.getInetAddress().toString())+"]", 20, -1));
				BufferedReader br=new BufferedReader(new InputStreamReader(s.getInputStream()));
				String request=br.readLine();
				myHandler.sendMessage(MessageUtils.OnProgress("Mensaje recibido ["+request+"]", 30, -1));
				OutputStream out=s.getOutputStream();
				PrintWriter output = new PrintWriter(s.getOutputStream(),true);
				String st=requestGenerator(request);
				myHandler.sendMessage(MessageUtils.OnProgress("generada respuesta ["+st+"]", 40, -1));
				output.println(st);
				myHandler.sendMessage(MessageUtils.OnProgress("Mensaje mensaje enviado ["+st+"]", 50, -1));
				output.flush();
				output.close();
				out.flush();
				out.close();
				s.close();
				end=st.equals("");
				long endCalendar=Calendar.getInstance().getTimeInMillis();
				long diff=endCalendar-initCalendar;
				myHandler.sendMessage(MessageUtils.OnCompleteMsg(String.valueOf(diff), -1));
			}
			serversock.close();

		} catch (Exception e) {
			myHandler.sendMessage(MessageUtils.OnErrorMsg(e.getMessage(), -1));
			e.printStackTrace();
		}

		
	}
	
	public String requestGenerator(String readLine) {
		if ("quit".equals(readLine))
		{
			return "";
		}
		return "getting["+readLine+"]";
	}


	private void createServerUDP() {
		try {			
			String text;
			byte[] message = new byte[1500];
			boolean end=false;
			while (!end)
			{
				long initCalendar=Calendar.getInstance().getTimeInMillis();
				DatagramPacket p = new DatagramPacket(message, message.length);
				myHandler.sendMessage(MessageUtils.OnProgress("Creando servidor UDP ["+String.valueOf(myPort)+"]", 20, -1));
				datSocket = new DatagramSocket(myPort);
				//myHandler.sendMessage(MessageUtils.OnProgress("escuchando servidor udp...", 30, -1));
				datSocket.receive(p);
				//myHandler.sendMessage(MessageUtils.OnProgress("recibido mensaje de ["+s.getRemoteSocketAddress().toString()+"]", 50, -1));
				text = new String(message, 0, p.getLength());
				myHandler.sendMessage(MessageUtils.OnProgress("Recibido["+text+"] de []  ", 60, -1));
				String message2=requestGenerator(text);
				int msg_length=message2.length();
				byte[] mess = message2.getBytes();
				myHandler.sendMessage(MessageUtils.OnProgress("enviando["+text+"] de []  ", 80, -1));
				DatagramPacket p2 = new DatagramPacket(mess, msg_length,p.getSocketAddress());
				datSocket.send(p2);
				end=message2.equals("");
				long endCalendar=Calendar.getInstance().getTimeInMillis();
				long diff=endCalendar-initCalendar;
				myHandler.sendMessage(MessageUtils.OnCompleteMsg(String.valueOf(diff), -1));
				closeUDPSocket();
			}
			
			
		} catch (Exception e) {
			myHandler.sendMessage(MessageUtils.OnErrorMsg("Error!:"+e.getMessage(), -1));
			closeUDPSocket();
			e.printStackTrace();
		}
		
	}



	

}
