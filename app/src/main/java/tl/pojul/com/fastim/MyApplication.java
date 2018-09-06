package tl.pojul.com.fastim;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.PowerManager;
import android.support.multidex.MultiDex;
import android.util.Log;
import android.widget.Toast;

import com.baidu.mapapi.SDKInitializer;
import com.marswin89.marsdaemon.DaemonApplication;
import com.marswin89.marsdaemon.DaemonConfigurations;
import com.pojul.fastIM.entity.User;
import com.pojul.fastIM.message.request.LoginByTokenReq;
import com.pojul.fastIM.message.response.LoginResponse;
import com.pojul.objectsocket.message.BaseMessage;
import com.pojul.objectsocket.message.MessageHeader;
import com.pojul.objectsocket.message.ResponseMessage;
import com.pojul.objectsocket.message.StringFile;
import com.pojul.objectsocket.socket.ClientSocket;
import com.pojul.objectsocket.socket.SocketReceiver;
import com.pojul.objectsocket.socket.SocketRequest;
import com.pojul.objectsocket.socket.SocketSender;
import com.pojul.objectsocket.utils.Constant;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.DefaultRefreshHeaderCreater;
import com.scwang.smartrefresh.layout.api.RefreshHeader;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.constant.SpinnerStyle;
import com.scwang.smartrefresh.layout.header.ClassicsHeader;
import com.yanzhenjie.album.Album;
import com.yanzhenjie.album.AlbumConfig;

import java.util.ArrayList;
import java.util.List;

import me.jessyan.progressmanager.ProgressManager;
import okhttp3.Call;
import okhttp3.OkHttpClient;
import tl.pojul.com.fastim.Media.AudioManager;
import tl.pojul.com.fastim.Media.VibrateManager;
import tl.pojul.com.fastim.View.activity.LoginActivity;
import tl.pojul.com.fastim.View.activity.MainActivity;
import tl.pojul.com.fastim.View.broadcast.MarsDaemonReceiver1;
import tl.pojul.com.fastim.View.broadcast.MarsDaemonReceiver2;
import tl.pojul.com.fastim.View.broadcast.NetWorkStateReceiver;
import tl.pojul.com.fastim.View.service.MarsDaemonService2;
import tl.pojul.com.fastim.View.service.SocketConnService;
import tl.pojul.com.fastim.dao.MySQLiteHelper;
import tl.pojul.com.fastim.map.baidu.LocationManager;
import tl.pojul.com.fastim.map.baidu.MapConfigManager;
import tl.pojul.com.fastim.socket.upload.PicUploadManager;
import tl.pojul.com.fastim.util.ConversationUtil;
import tl.pojul.com.fastim.util.NetWorkUtil;
import tl.pojul.com.fastim.util.SPUtil;

/**
 * Created by gqb on 2018/5/30.
 */

public class MyApplication extends DaemonApplication {

    public static int SCREEN_WIDTH;

    public static int SCREEN_HEIGHT;

    private static MyApplication myApplication = null;

    public static ClientSocket ClientSocket;

    private static final String TAG = "MyApplication";

    protected List<IReceiveMessage> IReceiveMessages = new ArrayList<>();
    protected List<ISendMessage> ISendMessage = new ArrayList<>();
    protected List<ISendProgress> ISendProgress = new ArrayList<>();

    public static String exitChatRoomUid = "";
    private NetWorkStateReceiver netWorkStateReceiver;

    private MainActivity mainActivity;
    public static boolean isConnecting;
    //private User user;
    private PowerManager pm;
    //private PowerManager.WakeLock wakeLock;
    public static String currentReplyActivity = "";
    public static int startActivityCount;

    public static List<String> tagMessLabels = new ArrayList<String>() {{
        add("运动");
        add("求助");
        add("旅游");
        add("找室友");
        /*add("交友");
        add("无聊");
        add("找CP");
        add("找女友");
        add("找男友");*/
        add("房屋出租");
        add("租房");
        add("招聘");
        add("宠物");
    }};

    public static List<String> picLabels =  new ArrayList<String>() {{
        add("风景");
        add("生活");
        add("美食");
        add("建筑");
        add("自拍");
        add("摄影");
        add("手机壁纸");
        add("文艺");
        add("清新");
        add("美女");
        add("萝莉");
        add("迷人");
        add("宠物");
        add("写真");
        add("沙滩");
        add("大海");
        add("古典");
        add("唯美");
        add("内涵");
        add("可爱");
        add("校花");
        add("家居");
        add("旅游");
    }};

    private OkHttpClient mOkHttpClient;

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
        String processName = getProcessName(this);
        if(!"tl.pojul.com.fastim:process1".equals(processName)){
            return;
        }
        myApplication = this;
        Constant.STORAGE_TYPE = 0;
        SPUtil.Instance(getApplicationContext());
        MySQLiteHelper.Instance(getApplicationContext());
        AudioManager.Instance(getApplicationContext());
        VibrateManager.Instance(getApplicationContext());
        SDKInitializer.initialize(getApplicationContext());
        LocationManager.Instance(getApplicationContext());
        initData();
        initNetWorkReceive();
        //user = SPUtil.getInstance().getUser();

        /*pm = (PowerManager) this.getSystemService(Context.POWER_SERVICE);
        //保持cpu一直运行，不管屏幕是否黑屏
        wakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "CPUKeepRunning");
        wakeLock.acquire();*/
        Log.e(TAG, "onCreate");
        //startService(new Intent(getApplicationContext(), SocketConnService.class));

        registerActivityLifecycleCallbacks(activityLifecycleCallbacks);
        this.mOkHttpClient = ProgressManager.getInstance().with(new OkHttpClient.Builder())
                .build();
    }

    private void initNetWorkReceive() {
        netWorkStateReceiver = new NetWorkStateReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(netWorkStateReceiver, filter);
    }

    private void initData() {
        if("".equals(SPUtil.getInstance().getString(tl.pojul.com.fastim.util.Constant.BASE_STORAGE_PATH))){
            SPUtil.getInstance().putString(tl.pojul.com.fastim.util.Constant.BASE_STORAGE_PATH, (Environment.getExternalStorageDirectory().getAbsolutePath()));
        }
        MapConfigManager.getInstance();
    }

    private String getProcessName(Context context) {
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> runningApps = am.getRunningAppProcesses();
        if (runningApps == null) {
            return null;
        }
        for (ActivityManager.RunningAppProcessInfo proInfo : runningApps) {
            if (proInfo.pid == android.os.Process.myPid()) {
                if (proInfo.processName != null) {
                    return proInfo.processName;
                }
            }
        }
        return null;
    }

    @Override
    public void onTerminate() {
        closeConn();
        PicUploadManager.getInstance().onDestory();
        LocationManager.getInstance().onDestory();
        if(netWorkStateReceiver != null){
            unregisterReceiver(netWorkStateReceiver);
        }
        /*if(wakeLock != null){
            wakeLock.release();
        }*/
        unregisterActivityLifecycleCallbacks(activityLifecycleCallbacks);
        super.onTerminate();
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
                    if(mainActivity == null){
                        ConversationUtil.onRecMessWhileBack(message, getApplicationContext());
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

    public void notifyMessReceive(BaseMessage message) {

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
                if(NetWorkUtil.isNetWorkable(getApplicationContext()) && !isConnected()){
                    reConn();
                }
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

    public void setMainActivity(MainActivity mainActivity){
        this.mainActivity = mainActivity;
    }

    public MainActivity getMainActivity(){
        return mainActivity;
    }

    @Override
    protected DaemonConfigurations getDaemonConfigurations() {
        DaemonConfigurations.DaemonConfiguration configuration1 = new DaemonConfigurations.DaemonConfiguration("tl.pojul.com.fastim:process1", SocketConnService.class.getCanonicalName(), MarsDaemonReceiver1.class.getCanonicalName());
        DaemonConfigurations.DaemonConfiguration configuration2 = new DaemonConfigurations.DaemonConfiguration("tl.pojul.com.fastim:process2", MarsDaemonService2.class.getCanonicalName(), MarsDaemonReceiver2.class.getCanonicalName());
        //DaemonConfigurations.DaemonListener listener = new MyDaemonListener();
        return new DaemonConfigurations(configuration1, configuration2);//listener can be null
        //return new DaemonConfigurations(configuration1, configuration2, listener);
    }

    public void reConn() {
        if(isConnected() || isConnecting){
            return;
        }
        String arrays = SPUtil.getInstance().getArrays();
        if(SPUtil.getInstance().getUser() == null || arrays == null || arrays.isEmpty()){
            if(mainActivity != null){
                Intent intent =new Intent(getApplicationContext(), LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
            return;
        }
        isConnecting = true;
        new SocketRequest().requestConn(new SocketRequest.IRequestConn() {
            @Override
            public void onError(String msg) {
                Log.e("reConn", msg);
                isConnecting = false;
            }

            @Override
            public void onFinished(ClientSocket clientSocket) {
                isConnecting = false;
                MyApplication.ClientSocket = clientSocket;
                //MyApplication.ClientSocket.setHeartbeat(4* 60 * 1000);
                MyApplication.getApplication().registerSocketRecListerer();
                MyApplication.getApplication().registerSocketSendListerer();
                MyApplication.getApplication().registerSocketStatusListerer();
                MyApplication.getApplication().registSendProgListerer();
                tokenLogin(arrays);
            }
        }, Constant.HOST, Constant.PORT);
    }

    public void tokenLogin(String arrays) {
        LoginByTokenReq loginByTokenReq = new LoginByTokenReq();
        loginByTokenReq.setDeviceType("Android");
        loginByTokenReq.setTokenId(arrays);
        new SocketRequest().request(ClientSocket, loginByTokenReq, new SocketRequest.IRequest() {
            @Override
            public void onError(String msg) {
                Log.e("reConn1", msg);
                isConnecting = false;
                new Handler(Looper.getMainLooper()).post(()->{
                    if(mainActivity != null){
                        Intent intent =new Intent(getApplicationContext(), LoginActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                    }
                });
            }

            @Override
            public void onFinished(ResponseMessage mResponse) {
                isConnecting = false;
                LoginResponse loginResponse = (LoginResponse) mResponse;
                if (loginResponse.getCode() == 200) {
                    SPUtil.getInstance().putUser(loginResponse.getUser());
                    SPUtil.getInstance().putArrays(loginResponse.getTokenId(), loginResponse.getUser().getUserName());
                    MyApplication.ClientSocket.setTokenId(loginResponse.getTokenId());
                }
            }
        });
    }

    @Override
    public void attachBaseContextByDaemon(Context base) {
        super.attachBaseContextByDaemon(base);
        MultiDex.install(base);
    }

    private ActivityLifecycleCallbacks activityLifecycleCallbacks = new ActivityLifecycleCallbacks() {
        /**
         * application下的每个Activity声明周期改变时，都会触发以下的函数。
         */
        @Override
        public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
        }

        @Override
        public void onActivityStarted(Activity activity) {
            startActivityCount = startActivityCount + 1;
        }

        @Override
        public void onActivityResumed(Activity activity) {
            if(NetWorkUtil.isNetWorkable(getApplicationContext()) && !isConnected()){
                reConn();
            }
        }

        @Override
        public void onActivityPaused(Activity activity) {
        }

        @Override
        public void onActivityStopped(Activity activity) {
            startActivityCount = startActivityCount - 1;
        }

        @Override
        public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
        }

        @Override
        public void onActivityDestroyed(Activity activity) {
        }
    };

    public OkHttpClient getOkHttpClient() {
        return mOkHttpClient;
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

    /*public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }*/

    public void showShortToas(String msg){
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    public void showLongToas(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
    }
}
