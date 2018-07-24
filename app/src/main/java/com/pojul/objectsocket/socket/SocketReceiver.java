package com.pojul.objectsocket.socket;

import java.net.Socket;

import com.pojul.objectsocket.message.BaseMessage;
import com.pojul.objectsocket.message.MessageHeader;
import com.pojul.objectsocket.message.ResponseMessage;
import com.pojul.objectsocket.message.StringFile;
import com.pojul.objectsocket.parser.SocketBytesParser;
import com.pojul.objectsocket.parser.interfacer.ISocketBytesParser;
import com.pojul.objectsocket.utils.LogUtil;

public class SocketReceiver {

	protected Thread socketRecThread;
	protected Socket mSocket;
	protected boolean recOnce;
	protected SocketBytesParser mSocketBytesParser;
	private static final String TAG = "SocketReceiver";
	protected ISocketReceiver receiverListener;
	public ClientSocket mClientSocket;
	protected RecProgressListerer recProgressListerer;
	
	public SocketReceiver(Socket mSocket, ClientSocket clientSocket) {
		super();
		this.mSocket = mSocket;
		this.mClientSocket = clientSocket;
		this.recOnce = false;
		startRecThread();
	}

	public SocketReceiver(Socket mSocket, boolean recOnce, ClientSocket clientSocket) {
		super();
		this.mSocket = mSocket;
		this.recOnce = recOnce;
		this.mClientSocket = clientSocket;
		startRecThread();
	}
	
	public void startRecThread() {
		if(mSocket == null) {
			return;
		}
		socketRecThread = new Thread(new Runnable() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				if(mSocketBytesParser == null) {
					mSocketBytesParser = new SocketBytesParser(mSocket, recOnce);
					mSocketBytesParser.setISocketBytesParser(new ISocketBytesParser() {
						
						@Override
						public void onReadHead(MessageHeader header) {
							// TODO Auto-generated method stub
							if(receiverListener != null) {
								receiverListener.onReadHead(header);
							}
						}
						
						@Override
						public void onReadFinish() {
							// TODO Auto-generated method stub
							if(receiverListener != null) {
								receiverListener.onReadFinish();
							}
						}
						
						@Override
						public String onReadFile(StringFile mStringFile) {
							// TODO Auto-generated method stub
							if(receiverListener != null) {
								receiverListener.onReadFile(mStringFile);
							}
							return "";
						}
						
						@Override
						public void onReadEntity(BaseMessage message) {
							// TODO Auto-generated method stub
							onReceiveMessage(message);
							LogUtil.d(TAG, message.toString());
						}

						@Override
						public void onError(Exception e) {
							// TODO Auto-generated method stub
							mClientSocket.closeConn();
							if(receiverListener != null) {
								receiverListener.onError(e);
							}
							LogUtil.dStackTrace(e);
							LogUtil.e(TAG, e.toString());
						}
					});
				}
			}
		});
		socketRecThread.start();
	}

	private void onReceiveMessage(BaseMessage message) {
		if(RequestTimeOut.getInstance().isRequestMessage(message.getMessageUid())){
			RequestTimeOut.getInstance().onRequestFinish((ResponseMessage) message);
		}else if(receiverListener != null) {
			receiverListener.onReadEntity(message);
		}
	}

	public interface ISocketReceiver{
		public void onReadHead(MessageHeader header);
		
		public String onReadFile(StringFile mStringFile);
		
		public void onReadEntity(BaseMessage message);
		
		public void onReadFinish();
		
		public void onError(Exception e);
	}
	
	public void setReceiverListener(ISocketReceiver mISocketReceiver) {
		this.receiverListener = mISocketReceiver;
	}
	
	public void setRecProgressListerer(RecProgressListerer recProgressListerer) {
		this.recProgressListerer = recProgressListerer;
		if(mSocketBytesParser != null) {
			mSocketBytesParser.setRecProgressListerer(recProgressListerer);
		}
	}
	
	public void setSaveFilePath(String saveFilePath, String saveFileUrl) {
		if(mSocketBytesParser != null) {
			mSocketBytesParser.setSaveFilePath(saveFilePath, saveFileUrl);
		}
	}

	public void stopReceive() {
		if(mSocketBytesParser != null) {
			mSocketBytesParser.stop();
		}
	}
	
	public interface RecProgressListerer{
		public void progress(MessageHeader header, int progress);
		public void finish(MessageHeader header);
	}
	
}
