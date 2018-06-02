package tl.pojul.com.fastim.net;

import android.os.Handler;
import android.os.Looper;

import com.pojul.objectsocket.message.BaseMessage;
import com.pojul.objectsocket.socket.ClientSocket;
import com.pojul.objectsocket.utils.LogUtil;

import java.io.IOException;

import tl.pojul.com.fastim.MyApplication;
import tl.pojul.com.fastim.net.Entity.TimeOutTask;
import tl.pojul.com.fastim.util.Constant;

/**
 * Created by gqb on 2018/5/30.
 */

public class SocketRequest {

    public void initClientSocket(final SocketRequest.IRequest mIRequest){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    ClientSocket mClientSocket = new ClientSocket(Constant.HOST, Constant.PORT);
                    MyApplication.ClientSocket = mClientSocket;
                    mIRequest.onFinished();
                } catch (final IOException e) {
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            mIRequest.onError(e.getMessage());
                        }
                    });
                }
            }
        }).start();
    }

    public void resuest(BaseMessage message, final SocketRequest.IRequest mIRequest, long timeout){
        if(MyApplication.ClientSocket == null || MyApplication.ClientSocket.getmSocket() == null){
            LogUtil.i(getClass().getName(), "ClientSocket is not conneted");
            return;
        }
        RequestTimeOut.getInstance().addMonitorTask("", new TimeOutTask(timeout, mIRequest));
        MyApplication.ClientSocket.sendData(message);
    }


    public interface IRequest{
        public void onError(String msg);
        public void onFinished();
    }

}
