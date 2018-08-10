package tl.pojul.com.fastim;

import android.app.Application;
import android.content.Context;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.MapView;
import com.pojul.objectsocket.message.BaseMessage;
import com.pojul.objectsocket.message.MessageHeader;
import com.pojul.objectsocket.message.StringFile;
import com.pojul.objectsocket.socket.ClientSocket;
import com.pojul.objectsocket.socket.SocketReceiver;
import com.pojul.objectsocket.socket.SocketSender;
import com.pojul.objectsocket.utils.Constant;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.DefaultRefreshHeaderCreater;
import com.scwang.smartrefresh.layout.api.RefreshHeader;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.constant.SpinnerStyle;
import com.scwang.smartrefresh.layout.header.ClassicsHeader;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import tl.pojul.com.fastim.Media.AudioManager;
import tl.pojul.com.fastim.Media.VibrateManager;
import tl.pojul.com.fastim.dao.MySQLiteHelper;
import tl.pojul.com.fastim.map.baidu.LocationManager;
import tl.pojul.com.fastim.map.baidu.MapConfigManager;
import tl.pojul.com.fastim.socket.upload.PicUploadManager;
import tl.pojul.com.fastim.util.SPUtil;

/**
 * Created by gqb on 2018/5/30.
 */

public class MyApplication extends Application {

    public static int SCREEN_WIDTH;

    public static int SCREEN_HEIGHT;

    private static MyApplication myApplication = null;

    public static ClientSocket ClientSocket;

    private static final String TAG = "MyApplication";

    protected List<IReceiveMessage> IReceiveMessages = new ArrayList<>();
    protected List<ISendMessage> ISendMessage = new ArrayList<>();
    protected List<ISendProgress> ISendProgress = new ArrayList<>();

    public static String exitChatRoomUid = "";

    static {//static 代码段可以防止内存泄露
        //设置全局的Header构建器
        SmartRefreshLayout.setDefaultRefreshHeaderCreater(new DefaultRefreshHeaderCreater() {
            @Override
            public RefreshHeader createRefreshHeader(Context context, RefreshLayout layout) {
                return new ClassicsHeader(context).setSpinnerStyle(SpinnerStyle.Translate);//指定为经典Header，默认是 贝塞尔雷达Header
            }
        });
    }

    public static MyApplication getApplication() {
        return myApplication;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        myApplication = this;
        Constant.STORAGE_TYPE = 0;
        SPUtil.Instance(getApplicationContext());
        MySQLiteHelper.Instance(getApplicationContext());
        AudioManager.Instance(getApplicationContext());
        VibrateManager.Instance(getApplicationContext());
        SDKInitializer.initialize(getApplicationContext());
        LocationManager.Instance(getApplicationContext());
        initData();

    }

    private void initData() {
        if("".equals(SPUtil.getInstance().getString(tl.pojul.com.fastim.util.Constant.BASE_STORAGE_PATH))){
            SPUtil.getInstance().putString(tl.pojul.com.fastim.util.Constant.BASE_STORAGE_PATH, (Environment.getExternalStorageDirectory().getAbsolutePath()));
        }
        MapConfigManager.getInstance();
    }



    @Override
    public void onTerminate() {
        super.onTerminate();
        closeConn();
        PicUploadManager.getInstance().onDestory();
        LocationManager.getInstance().onDestory();
    }

    public void closeConn(){
        if(ClientSocket != null && ClientSocket.getmSocket() != null){
            ClientSocket.closeConn();
        }
    }

    public boolean isConnected(){
        if(ClientSocket != null && ClientSocket.getmSocket() != null){
            return true;
        }
        return false;
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
                new Handler(Looper.getMainLooper()).post(()->{
                    message.setIsSend(1);
                    for(int i = 0; i< IReceiveMessages.size(); i++){
                        IReceiveMessage iReceiveMessage = IReceiveMessages.get(i);
                        if(iReceiveMessage != null){
                            iReceiveMessage.receiveMessage(message);
                        }
                    }
                });
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
                new Handler(Looper.getMainLooper()).post(()->{
                    for(int i = 0; i< ISendMessage.size(); i++){
                        ISendMessage iSendMessage = ISendMessage.get(i);
                        if(iSendMessage != null){
                            iSendMessage.onSendFinish(mBaseMessage);
                        }
                    }
                });
            }

            @Override
            public void onSendFail(BaseMessage mBaseMessage) {
                new Handler(Looper.getMainLooper()).post(()->{
                    for(int i = 0; i< ISendMessage.size(); i++){
                        ISendMessage iSendMessage = ISendMessage.get(i);
                        if(iSendMessage != null){
                            iSendMessage.onSendFail(mBaseMessage);
                        }
                    }
                });
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
                new Handler(Looper.getMainLooper()).post(()->{
                    showLongToas("lose connection");
                    Log.e(TAG, "onConnClosed");
                });
                //showLongToas("lose connection");
            }
        });
    }

    public void registSendProgListerer(){
        if(ClientSocket == null || ClientSocket.getmSocket() == null){
            return;
        }
        ClientSocket.setSendProgressListerer(new SocketSender.SendProgressListerer() {
            @Override
            public void progress(BaseMessage message, int progress) {
                new Handler(Looper.getMainLooper()).post(()->{
                    for(int i =0; i< ISendProgress.size(); i++){
                        ISendProgress iSendProgress = ISendProgress.get(i);
                        if(iSendProgress != null){
                            iSendProgress.progress(message, progress);
                        }
                    }
                });
            }
            @Override
            public void finish(BaseMessage message) {
                new Handler(Looper.getMainLooper()).post(()->{
                    for(int i =0; i< ISendProgress.size(); i++){
                        ISendProgress iSendProgress = ISendProgress.get(i);
                        if(iSendProgress != null){
                            iSendProgress.finish(message);
                        }
                    }
                });
            }
        });
    }

    public void registerReceiveMessage(IReceiveMessage iReceiveMessage){
        synchronized (IReceiveMessages){
            if(iReceiveMessage != null){
                IReceiveMessages.add(iReceiveMessage);
            }
        }
    }

    public void unRegisterReceiveMessage(IReceiveMessage iReceiveMessage){
        synchronized (IReceiveMessages){
            if(iReceiveMessage != null){
                IReceiveMessages.remove(iReceiveMessage);
            }
        }
    }

    public void registerSendMessage(ISendMessage iSendMessage){
        synchronized (ISendMessage){
            if(iSendMessage != null){
                ISendMessage.add(iSendMessage);
            }
        }
    }

    public void unRegisterSendProgress(ISendProgress iSendProgress){
        synchronized (ISendProgress){
            if(iSendProgress != null){
                ISendProgress.remove(iSendProgress);
            }
        }
    }

    public void registerSendProgress(ISendProgress iSendProgress){
        synchronized (ISendProgress){
            if(iSendProgress != null){
                ISendProgress.add(iSendProgress);
            }
        }
    }

    public void unRegisterSendMessage(ISendMessage iSendMessage){
        synchronized (ISendMessage){
            if(iSendMessage != null){
                ISendMessage.remove(iSendMessage);
            }
        }
    }



    public interface ISendMessage{
        void onSendFinish(BaseMessage message);
        void onSendFail(BaseMessage message);
    }

    public interface IReceiveMessage{
        void receiveMessage(BaseMessage message);
    }

    public interface ISendProgress{
        void progress(BaseMessage message, int progress);
        void finish(BaseMessage message);
    }

    public void showShortToas(String msg){
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    public void showLongToas(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
    }
}
