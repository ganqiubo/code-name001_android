package com.pojul.objectsocket.socket;

import com.pojul.objectsocket.message.BaseMessage;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;

public class ClientSocket {

	protected Socket mSocket;
	protected SocketReceiver mSocketReceiver;
	protected SocketReceiver.ISocketReceiver recListener;
	protected SocketSender mSocketSender;
	protected SocketSender.ISocketSender senderListener;
	protected  OnStatusChangedListener mOnStatusChangedListener;
	//chatId为用户名
	protected String chatId;
	protected String deviceType;

	public void setmOnStatusChangedListener(OnStatusChangedListener mOnStatusChangedListener) {
		this.mOnStatusChangedListener = mOnStatusChangedListener;
	}

	public String getDeviceType() {
		return deviceType;
	}

	public void setDeviceType(String deviceType) {
		this.deviceType = deviceType;
	}

	public ClientSocket(String host, int port) throws IOException  {
		super();
		// TODO Auto-generated constructor stub
		mSocket = new Socket();
		SocketAddress remoteAddr = new InetSocketAddress(host, port);
		mSocket.connect(remoteAddr, 20000);
		mSocketSender = new SocketSender(mSocket, this);
		mSocketReceiver = new SocketReceiver(mSocket, this);
	}

	public ClientSocket(Socket mSocket) {
		super();
		this.mSocket = mSocket;
		mSocketSender = new SocketSender(mSocket, this);
		mSocketReceiver = new SocketReceiver(mSocket, this);
	}
	
	public Socket getmSocket() {
		return mSocket;
	}
	public void setmSocket(Socket mSocket) {
		this.mSocket = mSocket;
	}

	public void sendData(BaseMessage data) {
		if(mSocketSender == null) {
			mSocketSender = new SocketSender(mSocket, this);
		}
		mSocketSender.sendMessage(data);
	}
	
	public void sendDataAndClose(BaseMessage data) {
		if(mSocketSender == null) {
			mSocketSender = new SocketSender(mSocket, this);
		}
		mSocketSender.sendMessage(data);
		mSocketSender.closeConnWhenFinish();
	}
	
	public void setSenderListener(SocketSender.ISocketSender mISocketSender) {
		this.senderListener = mISocketSender;
		if(mSocketSender != null) {
			mSocketSender.setSenderListener(mISocketSender);
		}
	}
	
	public void setRecListener(SocketReceiver.ISocketReceiver mISocketReceiver) {
		this.recListener = mISocketReceiver;
		if(recListener != null) {
			mSocketReceiver.setReceiverListener(mISocketReceiver);
		}
	}
	
	public void closeConn() {
		if(mSocket != null) {
			if(mOnStatusChangedListener != null) {
				mOnStatusChangedListener.onConnClosed();
			}
			try {
				stopRec();
				stopSend();
				mSocket.shutdownInput();
				mSocket.shutdownOutput();
				mSocket.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				if(recListener != null) {
					recListener.onError(e);
				}
			}
			mSocket = null;
		}
	}
	
	public void stopRec() {
		if(mSocketReceiver != null) {
			mSocketReceiver.stopReceive();
		}
	}
	
	public void stopSend() {
		mSocketSender.stopSend();
	}

	public String getChatId() {
		return chatId;
	}

	public void setChatId(String chatId) {
		this.chatId = chatId;
	}
	
	public interface OnStatusChangedListener{
		public void onConnClosed();
	}
	
}
