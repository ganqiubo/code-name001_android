package com.pojul.objectsocket.socket;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.LinkedList;

import com.pojul.objectsocket.message.BaseMessage;
import com.pojul.objectsocket.parser.SocketEntityParser;
import com.pojul.objectsocket.parser.interfacer.ISocketEntityParser;
import com.pojul.objectsocket.utils.LogUtil;

public class SocketSender{

	private static final String TAG = "SocketSender";
	
	protected Thread socketSendThread;
	protected Socket mSocket;
	
	protected LinkedList<BaseMessage> mMessageQuene = new LinkedList<BaseMessage>(); 
	protected ISocketSender senderListener;
	
	protected SocketEntityParser mSocketEntityParser;
	private ClientSocket clientSocket;
	protected boolean isParsering = false;
	protected DataOutputStream dos;
	protected boolean stopSend = false;
	protected boolean closeConnWhenFinish = false; 
	protected long messageCheckInterval = 500;
	
	public SocketSender(Socket mSocket, ClientSocket clientSocket) {
		super();
		this.mSocket = mSocket;
		this.clientSocket = clientSocket;
		try {
			dos = new DataOutputStream(mSocket.getOutputStream());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			LogUtil.i(TAG, e.toString());
			clientSocket.closeConn();
		}
		startSendThread();
	}
	
	public long getMessageCheckInterval() {
		return messageCheckInterval;
	}

	public void setMessageCheckInterval(long messageCheckInterval) {
		this.messageCheckInterval = messageCheckInterval;
	}

	public void startSendThread() {
		if(mSocket == null) {
			return;
		}
		socketSendThread = new Thread(new Runnable() {
			public void run() {
				while(!stopSend) {
					BaseMessage mMessage = getTopAndRemoveMessage();
					if(mMessage != null) {
						LogUtil.d("senddata", mMessage.toString());
						try {
							sendByteMessage(mMessage);
						} catch (IllegalArgumentException | IllegalAccessException | IOException e) {
							// TODO Auto-generated catch block
							//e.printStackTrace();
							stopSend = true;
							if(mSocketEntityParser != null) {
								mSocketEntityParser.stopSend = true;
							}
							if(senderListener != null) {
								senderListener.onSendError(e);
							}
							LogUtil.i(TAG, e.toString());
						}
					}
					try {
						Thread.sleep(messageCheckInterval);
					} catch (InterruptedException e1) {
						// TODO Auto-generated catch block
						LogUtil.i(TAG, e1.toString());
					}
				}
			}
		});
		socketSendThread.start();
	}
	
	public void sendMessage(BaseMessage mBaseMessage) {
		putMessage(mBaseMessage);
	}
	
	protected void putMessage(BaseMessage mBaseMessage) {
		synchronized (mMessageQuene) {
			if(mBaseMessage != null) {
				mMessageQuene.addLast(mBaseMessage);
			}
		}
	}
	
	protected BaseMessage getTopAndRemoveMessage(){
		synchronized (mMessageQuene) {
			BaseMessage mBaseMessage = null;
			try {
				mBaseMessage= mMessageQuene.getFirst();
			} catch (Exception e) {
				// TODO: handle exception
				
			}
			if(mBaseMessage == null) {
				return null;
			}
			mMessageQuene.removeFirst();
			return mBaseMessage;
		}
	}
	
	public void setSenderListener(ISocketSender mISocketSender) {
		this.senderListener = mISocketSender;
	}
	
	protected void sendByteMessage(BaseMessage mMessage) throws IllegalArgumentException, IllegalAccessException, IOException {
		if(mMessage == null || mSocket == null || dos == null) {
			return;
		}
		if(mSocketEntityParser == null) {
			mSocketEntityParser = new SocketEntityParser(new ISocketEntityParser() {

				@Override
				public void onParser(byte[] mBytes) throws IOException {
					// TODO Auto-generated method stub
					dos.write(mBytes);
					dos.flush();
				}

				@Override
				public void onParserFinish(BaseMessage message) throws IOException {
					// TODO Auto-generated method stub
					if(senderListener != null) {
						senderListener.onSendFinish(message);
					}
					if(closeConnWhenFinish) {
						clientSocket.closeConn();
					}
				}

				@Override
				public void onParserError() {
					// TODO Auto-generated method stub
					//System.out.println(TAG + "：" + e);
				}
			});
		}
		mSocketEntityParser.startParse(mMessage);
	}
	
	public interface ISocketSender {
		void onSendFinish(BaseMessage mBaseMessage);
		
		void onSendFail(BaseMessage mBaseMessage);
		
		void onNullMessage(BaseMessage mBaseMessage);
		
		void onSendError(Exception e);
	}
	
	public void stopSend(){
		stopSend = true;
		if(mSocketEntityParser != null) {
			mSocketEntityParser.stop();
		}
	}

	public void closeConnWhenFinish() {
		closeConnWhenFinish = true;
	}
	
}
