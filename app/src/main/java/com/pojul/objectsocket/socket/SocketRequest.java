package com.pojul.objectsocket.socket;

import com.pojul.objectsocket.message.BaseMessage;
import com.pojul.objectsocket.message.ResponseMessage;
import com.pojul.objectsocket.utils.LogUtil;

import java.io.IOException;

/**
 * Created by gqb on 2018/5/30.
 */

public class SocketRequest {

    protected long defaultTimeOut = 30000;
    private static SocketRequest mSocketRequest;

    public static SocketRequest getInstance() {
        if(mSocketRequest == null) {
            synchronized (SocketRequest.class) {
                if(mSocketRequest == null) {
                    mSocketRequest = new SocketRequest();
                }
            }
        }
        return mSocketRequest;
    }

    public long getDefaultTimeOut() {
        return defaultTimeOut;
    }

    public void setDefaultTimeOut(long defaultTimeOut) {
        this.defaultTimeOut = defaultTimeOut;
    }

    public void resuestConn(final IRequestConn mIRequestConn, final String host, final int port){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    ClientSocket mClientSocket = new ClientSocket(host, port);
                    mIRequestConn.onFinished(mClientSocket);
                } catch (final IOException e) {
                    mIRequestConn.onError(e.getMessage());
                }
            }
        }).start();
    }

    public void resuest(ClientSocket mClientSocket, BaseMessage message, final IRequest mIRequest){
        resuest(mClientSocket, message, mIRequest, defaultTimeOut);
    }

    public void resuest(ClientSocket mClientSocket, BaseMessage message, final IRequest mIRequest, long timeout){
        if(mClientSocket == null || mClientSocket.getmSocket() == null){
            LogUtil.i(getClass().getName(), "ClientSocket is not conneted");
            mIRequest.onError("与服务器连接已断开");
            return;
        }
        RequestTimeOut.getInstance().addMonitorTask(message.getMessageUid(), 
        		new TimeOutTask((System.currentTimeMillis() + timeout), mIRequest));
        mClientSocket.sendData(message);
    }

    public interface IRequest{
        public void onError(String msg);
        public void onFinished(ResponseMessage mResponse);
    }

    public interface IRequestConn{
        public void onError(String msg);
        public void onFinished(ClientSocket clientSocket);
    }

}
