package com.pojul.objectsocket.socket;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import com.pojul.objectsocket.message.BaseMessage;
import com.pojul.objectsocket.socket.SocketReceiver.RecProgressListerer;
import com.pojul.objectsocket.socket.SocketSender.SendProgressListerer;
import com.pojul.objectsocket.utils.LogUtil;

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
	protected int connTimeOut = 8000;
	protected SendProgressListerer sendProgressListerer;
	protected RecProgressListerer recProgressListerer;
	protected String tokenId;

	public void setmOnStatusChangedListener(OnStatusChangedListener mOnStatusChangedListener) {
		this.mOnStatusChangedListener = mOnStatusChangedListener;
	}

	public String getDeviceType() {
		return deviceType;
	}

	public void setDeviceType(String deviceType) {
		this.deviceType = deviceType;
	}

	public ClientSocket(String host, int port) throws UnknownHostException, IOException  {
		super();
		// TODO Auto-generated constructor stub
		//mSocket = new Socket(host, port);
		mSocket = new Socket();
		mSocket.connect(new InetSocketAddress(host, port), connTimeOut);
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
	
	public void setSendProgressListerer(SendProgressListerer sendProgressListerer) {
		this.sendProgressListerer = sendProgressListerer;
		if(mSocketSender != null) {
			mSocketSender.setSendProgressListerer(sendProgressListerer);
		}
	}

	public void setRecProgressListerer(RecProgressListerer recProgressListerer) {
		this.recProgressListerer = recProgressListerer;
		if(mSocketReceiver != null) {
			mSocketReceiver.setRecProgressListerer(recProgressListerer);
		}
	}
	
	public void closeConn() {
		if(mSocket != null) {
			try {
				stopRec();
				stopSend();
				/*mSocket.shutdownInput();
				mSocket.shutdownOutput();*/
				mSocket.close();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				if(recListener != null) {
					recListener.onError(e);
				}
			}finally {
				LogUtil.d(getClass().getName(), "closeConn");
				if(mOnStatusChangedListener != null) {
					mOnStatusChangedListener.onConnClosed();
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

	public void setHeartbeat(long heartbeatInterval){
		if(mSocketSender != null){
			mSocketSender.setHeartbeat(heartbeatInterval);
		}
	}

	public void stopHeartbeat(){
		if(mSocketSender != null){
			mSocketSender.stopHeartbeat();
		}
	}

	public String getChatId() {
		return chatId;
	}

	public void setChatId(String chatId) {
		this.chatId = chatId;
	}
	
	public String getTokenId() {
		return tokenId;
	}

	public void setTokenId(String tokenId) {
		this.tokenId = tokenId;
	}

	public void setSaveFilePath(String saveFilePath, String saveFileUrl) {
		if(mSocketReceiver != null) {
			mSocketReceiver.setSaveFilePath(saveFilePath, saveFileUrl);
		}
	}
	
	public interface OnStatusChangedListener{
		public void onConnClosed();
	}

}
