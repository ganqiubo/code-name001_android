package com.pojul.objectsocket.socket;

import com.pojul.objectsocket.message.BaseMessage;
import com.pojul.objectsocket.message.RequestMessage;
import com.pojul.objectsocket.message.ResponseMessage;
import com.pojul.objectsocket.utils.LogUtil;

import java.io.IOException;

/**
 * Created by gqb on 2018/5/30.
 */

public class SocketRequest {

    protected long defaultTimeOut = 25000;

    public long getDefaultTimeOut() {
        return defaultTimeOut;
    }

    public void setDefaultTimeOut(long defaultTimeOut) {
        this.defaultTimeOut = defaultTimeOut;
    }

    public void requestConn(final IRequestConn mIRequestConn, final String host, final int port){
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

    /**
     * @param mClientSocket
     *@param message
     * message mIRequest
     */
    public void request(ClientSocket mClientSocket, RequestMessage message, final IRequest mIRequest){
        request(mClientSocket, message, mIRequest, defaultTimeOut);
    }

    public void request(ClientSocket mClientSocket, RequestMessage message, final IRequest mIRequest, long timeout){
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
