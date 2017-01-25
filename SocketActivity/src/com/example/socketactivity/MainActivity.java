package com.example.socketactivity;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;


import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends Activity {

	static final String NAME="BJS";
	InetAddress serverAddress;
	Socket socket;
	
	static TextView txtReceived;
	EditText txtMessage;
	CommsThread commsThread;
	
	//private Button Send;
	
	static Handler UIupdater=new Handler(){
		@Override
		public void handleMessage(Message msg){
			int numOfBytesReceived=msg.arg1;
			byte[] buffer=(byte[])msg.obj;
			String strReceived=new String(buffer) ;
			strReceived=strReceived.substring(
					0,numOfBytesReceived);
			txtReceived.setText(
					txtReceived.getText().toString()+strReceived);
		}	
	};
	
	private class CreateCommThreadTask extends AsyncTask<Void,Integer,Void>{
		@Override
		protected Void doInBackground(Void...params){
			try{
				serverAddress=InetAddress.getByName("10.11.116.5");//"192.168.155.1"
				socket=new Socket(serverAddress,500);
				commsThread=new CommsThread(socket);
				commsThread.start();
				sendToServer(NAME);
			}
			catch(UnknownHostException e){
				Log.d("Sockets",e.getLocalizedMessage());
			}
			catch(IOException e){
				Log.d("Sockets",e.getLocalizedMessage());
			}
			return null;
		}
		
	}
	
	private class WriteToServerTask extends AsyncTask<byte[],Void,Void>{
		protected Void doInBackground(byte[]...data){
			commsThread.write(data[0]);
			return null;
		}
	}
	
	private class CloseSocketTask extends AsyncTask<Void,Void,Void>{
		@Override
		protected Void doInBackground(Void...params){
			try{
				socket.close();
			}
			catch(IOException e){
				Log.d("Sockets",e.getLocalizedMessage());
			}
			return null;
		}
	}
	
	
	
	
	

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		System.out.print("Login");
		txtMessage=(EditText)findViewById(R.id.txtMessage);
		//Send = (Button)findViewById(R.id.MyButton);
		//Send.setOnClickListener(new CalculateListener());
		//myButton = (Button)findViewById(R.id.MyButton);
		txtReceived=(TextView)findViewById(R.id.txtReceived);
	}
	
	/*class CalculateListener implements OnClickListener{

		@Override
		public void onClick(View v) {
			sendToServer(txtMessage.getText().toString());
		}
	}*/
		
	public void onClickSend(View View){
		System.out.print("button");
		sendToServer(txtMessage.getText().toString());
	}
	public void sendToServer(String message){
		byte[] theByteArray=message.getBytes();
		new WriteToServerTask().execute(theByteArray);
	}
	
	@Override
	public void onResume(){
		super.onResume();
		new CreateCommThreadTask().execute();
	}
	
	@Override
	public void onPause(){
		super.onPause();
		new CloseSocketTask().execute();
	}
	
}
	
	
