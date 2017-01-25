package com.example.socketactivity;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import android.util.Log;

public class CommsThread extends Thread {
	private final Socket socket;
	private final InputStream inputStream;
	private OutputStream outputStream;
	
	public CommsThread(Socket sock){
		socket=sock;
		InputStream tmpIn=null;
		OutputStream tmpOut=null;
		try{
			tmpIn=socket.getInputStream();
			tmpOut=socket.getOutputStream();
		}
		catch(IOException e){
			Log.d("SocketChat",e.getLocalizedMessage());
		}
		inputStream=tmpIn;
		outputStream=tmpOut;
	}
	
	public void run(){
		byte[] buffer=new byte[1024];
		int bytes;
		while(true){
			try{
				bytes=inputStream.read(buffer);
				MainActivity.UIupdater.obtainMessage(
						0,bytes,-1,buffer).sendToTarget();
			}
			catch(IOException e){
				break;
			}
		}
	}
	
	public void write(byte[] bytes){
		try{
			outputStream.write(bytes);
		}
		catch(IOException e){
		}
	}
	
	public void cancel(){
		try{
			socket.close();
		}
		catch(IOException e){};
	}

}









