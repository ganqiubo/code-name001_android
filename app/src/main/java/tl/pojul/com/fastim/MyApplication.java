package tl.pojul.com.fastim;

import android.app.Application;
import android.widget.Toast;

import com.pojul.objectsocket.message.BaseMessage;
import com.pojul.objectsocket.message.MessageHeader;
import com.pojul.objectsocket.message.StringFile;
import com.pojul.objectsocket.socket.ClientSocket;
import com.pojul.objectsocket.socket.SocketReceiver;
import com.pojul.objectsocket.socket.SocketSender;

/**
 * Created by gqb on 2018/5/30.
 */

public class MyApplication extends Application {
    private static MyApplication myApplication = null;

    public static ClientSocket ClientSocket;

    public static MyApplication getApplication() {
        return myApplication;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        myApplication = this;
    }

    public void registerSocketRecListerer(){
        if(ClientSocket == null || ClientSocket.getmSocket() == null){
            return;
        }
        ClientSocket.setRecListener(new SocketReceiver.ISocketReceiver() {
            @Override
            public void onReadHead(MessageHeader header) {

            }

            @Override
            public String onReadFile(StringFile mStringFile) {
                return null;
            }

            @Override
            public void onReadEntity(BaseMessage message) {

            }

            @Override
            public void onReadFinish() {

            }

            @Override
            public void onError(Exception e) {

            }
        });
    }

    public void registerSocketSendListerer(){
        if(ClientSocket == null || ClientSocket.getmSocket() == null){
            return;
        }
        ClientSocket.setSenderListener(new SocketSender.ISocketSender() {
            @Override
            public void onSendFinish(BaseMessage mBaseMessage) {

            }

            @Override
            public void onSendFail(BaseMessage mBaseMessage) {

            }

            @Override
            public void onNullMessage(BaseMessage mBaseMessage) {

            }

            @Override
            public void onSendError(Exception e) {

            }
        });
    }

    public void registerSocketStatusListerer(){
        if(ClientSocket == null || ClientSocket.getmSocket() == null){
            return;
        }
        ClientSocket.setmOnStatusChangedListener(new ClientSocket.OnStatusChangedListener(){
            @Override
            public void onConnClosed() {
                showLongToas("lose connection");
            }
        });
    }

    public void showShortToas(String msg){
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    public void showLongToas(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
    }
}
