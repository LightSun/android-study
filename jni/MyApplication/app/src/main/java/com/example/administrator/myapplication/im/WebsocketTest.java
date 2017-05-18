package com.example.administrator.myapplication.im;

import com.google.gson.Gson;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;

public class WebsocketTest {

	private static final URI sUri = URI.create("http://192.168.100.7:8090/sub");
	private WebSocketClient mClient;
	private Gson mGson = new Gson();
	private Thread mHeartThread;

	public WebsocketTest() {
		init();
	}

	private void init() {
		mClient = new WebSocketClient(sUri) {
			@Override
			public void onOpen(ServerHandshake arg0) {
				System.out.println("onOpen: " + arg0.getHttpStatusMessage());
				mHeartThread = new Thread(mBeatHeart);
				mHeartThread.start();
			}

			@Override
			public void onMessage(String arg0) {
				System.out.println("arg0 = " + arg0);
			}

			@Override
			public void onError(Exception arg0) {
				System.out.println("onError");
			}

			@Override
			public void onClose(int arg0, String arg1, boolean arg2) {
				System.out.println("onClose: " + arg1);
			}
		};
	}
	
	public void open(){
		new Thread(new Runnable() {
			@Override
			public void run() {
				mClient.connect();
				/*try {
					mClient.connectBlocking();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}*/
			}
		}).start();
	}
	public void close(){
		mClient.close();
	}

	public static void main(String[] args) {
		WebsocketTest enter = new WebsocketTest();
		enter.init();
		enter.open();
	}

	private Runnable mBeatHeart = new Runnable() {
		@Override
		public void run() {
			HeartInfo info = new HeartInfo();
			String json = mGson.toJson(info);
			System.out.println("heart = " + json);
			while (true) {
				try {
					Thread.sleep(2000);
					mClient.send(json);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	};
	 /*ws.send(JSON.stringify({
         'ver': 1,
         'op': 2,
         'seq': 2,
         'body': {}
     }));*/
	public static class HeartInfo{
		int ver = 1;
		int op = 2;
		int seq = 3;
		String body = "{}";
		@Override
		public String toString() {
			return "HeartInfo [ver=" + ver + ", op=" + op + ", seq=" + seq + ", body=" + body + "]";
		}
		
	}
	
}
