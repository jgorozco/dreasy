package com.dreasyLib.comm.network;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Calendar;

import android.os.Handler;

import com.dreasyLib.comm.MessageUtils;

public class ClientSocketThread extends Thread {

	public static int TCP=0;	
	public static int UDP=1;
	
	private int myType;//
	private int myPort;
	private String myServerIp;
	private Handler myHandler;
	private String myMessage;
	//TCP elements

	
	//UDP elements
	
	
	public ClientSocketThread (int type,String message ,String ipaddr,int port,Handler handler)
	{
		myType=type;
		myHandler=handler;
		myPort=port;
		myServerIp=ipaddr;
		myMessage=message;
	}
	

	public void run()
	{
		
		if (myType==TCP)
		{
			createClientTCP();
		}else
		{
			createClientUDP();			
		}
		
	}


	private void createClientUDP() {
		try {
			long initCalendar=Calendar.getInstance().getTimeInMillis();
		    String message=myMessage;
			int server_port = myPort;
			DatagramSocket s;
			s = new DatagramSocket();
			InetAddress local = InetAddress.getByName(myServerIp);
			myHandler.sendMessage(MessageUtils.OnProgress("conexion creada UDP["+local.getHostAddress()+"]",10,-1));
			int msg_length=message.length();
			byte[] mess = message.getBytes();
			DatagramPacket p = new DatagramPacket(mess, msg_length,local,server_port);
			myHandler.sendMessage(MessageUtils.OnProgress("enviando paquete["+message+"]",30,-1));
			s.send(p);
			
			byte[] message2 = new byte[1500];
			DatagramPacket p2 = new DatagramPacket(message2, message2.length);
			myHandler.sendMessage(MessageUtils.OnProgress("Recibiendo mensaje...",60,-1));
			s.receive(p2);
			String text = new String(message2, 0, p2.getLength());
			myHandler.sendMessage(MessageUtils.OnProgress("Recibiendo paquete["+text+"]",80,-1));
			s.close();
			long endCalendar=Calendar.getInstance().getTimeInMillis();
			long diff=endCalendar-initCalendar;
			myHandler.sendMessage(MessageUtils.OnCompleteMsg(String.valueOf(diff), -1));
			
		} catch (Exception e) {
			myHandler.sendMessage(MessageUtils.OnErrorMsg("error en UDP["+e.getMessage()+"]",-1));
			e.printStackTrace();
		}

		
	}


	private void createClientTCP() {
		Socket s=null;
		try {
			long initCalendar=Calendar.getInstance().getTimeInMillis();
			s=new Socket(myServerIp, myPort);
			myHandler.sendMessage(MessageUtils.OnProgress("conexion establecida TCP["+s.getInetAddress().getHostAddress()+"]",10,-1));
			OutputStream out = s.getOutputStream();
		    PrintWriter output = new PrintWriter(out);
		    String message="SINC envio desde cliente";
		    myHandler.sendMessage(MessageUtils.OnProgress("enviando datos... ["+message+"]",20,-1));
		    output.println(message);
			output.flush();
			out.flush();
		    BufferedReader input = new BufferedReader(new InputStreamReader(s.getInputStream()));
		    myHandler.sendMessage(MessageUtils.OnProgress("recibiendo  datos...",50,-1));
	        String st = input.readLine();
	        myHandler.sendMessage(MessageUtils.OnProgress("recibido["+st+"] y cerrando conexion",80,-1));
	        s.close();	    
	        long endCalendar=Calendar.getInstance().getTimeInMillis();
			long diff=endCalendar-initCalendar;
			myHandler.sendMessage(MessageUtils.OnCompleteMsg(String.valueOf(diff), -1));
		}  catch (Exception e) {
			if (s!=null)
			{
				while (!s.isClosed())
				{
					try {
						s.close();
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}
			}
			myHandler.sendMessage(MessageUtils.OnErrorMsg("Error ["+e.getMessage()+"]...",-1));
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
}
