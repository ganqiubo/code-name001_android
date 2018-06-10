package com.pojul.objectsocket.socket;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.LinkedList;
import com.pojul.objectsocket.message.BaseMessage;
import com.pojul.objectsocket.message.ResponseMessage;
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
	private boolean isWait = false;
	
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
					try {
						Thread.sleep(messageCheckInterval);
					} catch (InterruptedException e1) {
						// TODO Auto-generated catch block
						LogUtil.i(TAG, e1.toString());
						LogUtil.dStackTrace(e1);
					}
					synchronized (mMessageQuene) {
						if(mMessageQuene.size() <= 0) {
							try {
								isWait = true;
								mMessageQuene.wait();
							} catch (InterruptedException e) {
								// TODO Auto-generated catch block
								LogUtil.d(getClass().getName(), e.toString());
								LogUtil.dStackTrace(e);
							}
						}
					}
					isWait = false;
					if(stopSend) {
						return;
					}
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
								mSocketEntityParser.stop();
							}
							onSendError(mMessage, e);
							LogUtil.i(TAG, e.toString());
							LogUtil.dStackTrace(e);
						}
					}
				}
			}
		});
		socketSendThread.start();
	}

	protected void onSendError(BaseMessage mMessage, Exception e) {
		if(RequestTimeOut.getInstance().isRequestMessage(mMessage.getMessageUid())){
			RequestTimeOut.getInstance().onRequestError((ResponseMessage) mMessage, e);
		}else if(senderListener != null) {
			senderListener.onSendError(e);
		}
	}

	protected void onSendFinish(BaseMessage mMessage) {
		if(senderListener != null) {
			senderListener.onSendFinish(mMessage);
		}
	}

	public void sendMessage(BaseMessage mBaseMessage) {
		putMessage(mBaseMessage);
	}
	
	protected void putMessage(BaseMessage mBaseMessage) {
		synchronized (mMessageQuene) {
			if(mBaseMessage != null) {
				mMessageQuene.addLast(mBaseMessage);
				if(isWait && mMessageQuene.size() > 0) {
					mMessageQuene.notifyAll();
				}
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
					onSendFinish(message);
					if(closeConnWhenFinish) {
						clientSocket.closeConn();
					}
				}

				@Override
				public void onParserError(BaseMessage message, Exception e) {
					// TODO Auto-generated method stub
					onSendError(message, e);
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
		synchronized (mMessageQuene) {
			mMessageQuene.notifyAll();
		}
		if(mSocketEntityParser != null) {
			mSocketEntityParser.stop();
		}
	}

	public void closeConnWhenFinish() {
		closeConnWhenFinish = true;
	}
	
}
