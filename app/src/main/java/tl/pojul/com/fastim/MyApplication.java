package tl.pojul.com.fastim;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.PowerManager;
import android.support.multidex.MultiDex;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.utils.DistanceUtil;
import com.lahm.library.EasyProtectorLib;
import com.lahm.library.EmulatorCheckCallback;
import com.marswin89.marsdaemon.DaemonApplication;
import com.marswin89.marsdaemon.DaemonConfigurations;
import com.pojul.fastIM.entity.NearByPeople;
import com.pojul.fastIM.entity.User;
import com.pojul.fastIM.message.other.NotifyHasRecommend;
import com.pojul.fastIM.message.other.NotifyAcceptFriend;
import com.pojul.fastIM.message.other.NotifyFriendReq;
import com.pojul.fastIM.message.other.NotifyManagerNotify;
import com.pojul.fastIM.message.other.NotifyPayMemberOk;
import com.pojul.fastIM.message.request.LoginByTokenReq;
import com.pojul.fastIM.message.request.UpdateLocReq;
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
import com.umeng.commonsdk.UMConfigure;
import com.umeng.socialize.PlatformConfig;
import com.umeng.socialize.UMShareConfig;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;

import info.guardianproject.netcipher.client.TlsOnlySocketFactory;
import me.jessyan.progressmanager.ProgressManager;
import okhttp3.OkHttpClient;
import tl.pojul.com.fastim.Media.AudioManager;
import tl.pojul.com.fastim.Media.VibrateManager;
import tl.pojul.com.fastim.View.activity.ChatFileDownloadActivity;
import tl.pojul.com.fastim.View.activity.LockScreenActivity;
import tl.pojul.com.fastim.View.activity.LoginActivity;
import tl.pojul.com.fastim.View.activity.MainActivity;
import tl.pojul.com.fastim.View.activity.RegistActivity;
import tl.pojul.com.fastim.View.activity.TagReplyActivity;
import tl.pojul.com.fastim.View.activity.WebviewActivity;
import tl.pojul.com.fastim.View.broadcast.MarsDaemonReceiver1;
import tl.pojul.com.fastim.View.broadcast.MarsDaemonReceiver2;
import tl.pojul.com.fastim.View.broadcast.NetWorkStateReceiver;
import tl.pojul.com.fastim.View.service.MarsDaemonService2;
import tl.pojul.com.fastim.View.service.SocketConnService;
import tl.pojul.com.fastim.dao.MySQLiteHelper;
import tl.pojul.com.fastim.http.download.DownLoadCallBack;
import tl.pojul.com.fastim.http.download.DownLoadManager;
import tl.pojul.com.fastim.http.download.DownloadTask;
import tl.pojul.com.fastim.map.baidu.LocationManager;
import tl.pojul.com.fastim.map.baidu.MapConfigManager;
import tl.pojul.com.fastim.socket.upload.PicUploadManager;
import tl.pojul.com.fastim.util.AddFriendUtil;
import tl.pojul.com.fastim.util.ConversationUtil;
import tl.pojul.com.fastim.util.FileUtil;
import tl.pojul.com.fastim.util.KeyguardGalleryUtil;
import tl.pojul.com.fastim.util.NetWorkUtil;
import tl.pojul.com.fastim.util.NotifyUtil;
import tl.pojul.com.fastim.util.SPUtil;

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
    private PowerManager.WakeLock wakeLock;
    public static String currentReplyActivity = "";
    public static int startActivityCount;

    private boolean showLocToNearBy = true;
    private Thread locUpdateThread;
    private BDLocation lastUpdateLoc;
    private long lastUpdateLocMilli;
    private AddFriendUtil addFriendUtil = new AddFriendUtil();

    public static boolean hasRecomdMess;
    public static boolean hasRecomdProple;
    public static boolean loginOut = false;
    private boolean isUpdateApk = false;

    public static List<String> tagMessLabels = new ArrayList<String>() {{
        add("运动");
        add("求助");
        add("旅行");
        add("找室友");
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
        add("壁纸");
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
        add("旅行");
    }};

    private OkHttpClient mOkHttpClient;
    private int lastUpdateApkProgress = 0;

    static {//static 代码段可以防止内存泄露
        //设置全局的Header构建器
        SmartRefreshLayout.setDefaultRefreshHeaderCreater(new DefaultRefreshHeaderCreater() {
            @Override
            public RefreshHeader createRefreshHeader(Context context, RefreshLayout layout) {
                return new ClassicsHeader(context).setSpinnerStyle(SpinnerStyle.Translate);//指定为经典Header，默认是 贝塞尔雷达Header
            }
        });
    }

    /*private TelephonyManager mTelephonyManager;
    private PhoneStateListener mPhoneStateListener;
    private boolean isCalling = false;*/

    public static MyApplication getApplication() {
        return myApplication;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        String processName = getProcessName(this);
        Log.e(TAG, "processName: " + processName);
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

        /**
         *分享
         **/
        /*UMConfigure.init(this,"5c4c1a86f1f556b484000125"
                ,"umeng",UMConfigure.DEVICE_TYPE_PHONE,"");*/
        UMConfigure.init(this,UMConfigure.DEVICE_TYPE_PHONE,"");
        UMConfigure.setLogEnabled(true);
        PlatformConfig.setWeixin("wx4ebfa21ac55aa743", "5c017d2f6ec59c46620d82c044a1985a");


        //user = SPUtil.getInstance().getUser();

        /*pm = (PowerManager) this.getSystemService(Context.POWER_SERVICE);
        //保持cpu一直运行，不管屏幕是否黑屏
        wakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "CPUKeepRunning");
        wakeLock.acquire();*/
        Log.e(TAG, "onCreate");
        //startService(new Intent(getApplicationContext(), SocketConnService.class));

        registerActivityLifecycleCallbacks(activityLifecycleCallbacks);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            try{
                SSLContext sslcontext = SSLContext.getInstance("TLS");
                sslcontext.init(null, null, null);
                SSLSocketFactory noSSLv3Factory = new TlsOnlySocketFactory(sslcontext.getSocketFactory());
                this.mOkHttpClient = ProgressManager.getInstance().with(new OkHttpClient.Builder())
                        .sslSocketFactory(noSSLv3Factory)
                        .build();
            }catch (Exception e){
                this.mOkHttpClient = ProgressManager.getInstance().with(new OkHttpClient.Builder())
                        .build();
            }
        }else{
            this.mOkHttpClient = ProgressManager.getInstance().with(new OkHttpClient.Builder())
                    .build();
        }


        if(showLocToNearBy){
            startUpdateLocService();
        }

        IntentFilter screenStatusIF = new IntentFilter();
        screenStatusIF.addAction(Intent.ACTION_SCREEN_ON);
        screenStatusIF.addAction(Intent.ACTION_SCREEN_OFF);
        registerReceiver(mScreenStatusReceiver, screenStatusIF);

        //listenPhoneState();


        Log.e(TAG, "checkIsRunningInEmulator: " + EasyProtectorLib.checkIsRunningInEmulator(getApplicationContext(),
                emulatorInfo -> {}) +
            "checkIsXposedExist: " + EasyProtectorLib.checkIsXposedExist());

    }

    /*private void listenPhoneState() {
        mTelephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        mPhoneStateListener = new PhoneStateListener() {
            @Override
            public void onCallStateChanged(int state, String incomingNumber) {
                switch (state) {
                    case TelephonyManager.CALL_STATE_IDLE:
                        isCalling = false;se
                        break;
                    default:
                        isCalling = true;
                        break;
                }
            }
        };
        if (mTelephonyManager != null) {
            mTelephonyManager.listen(mPhoneStateListener, PhoneStateListener.LISTEN_CALL_STATE);
        }
    }*/

    public void startUpdateLocService() {
        this.showLocToNearBy = true;
        locUpdateThread = new Thread(() -> {
            while(showLocToNearBy){
                LocationManager.getInstance().registerLocationListener(new LocationManager.ILocationListener() {
                    @Override
                    public void onReceive(BDLocation bdLocation) {
                        //Log.e("locUpdateService","sucesses");
                        if(lastUpdateLoc == null || (System.currentTimeMillis() - lastUpdateLocMilli) > 30 * 60 * 1000
                                || DistanceUtil.getDistance(new LatLng(bdLocation.getLatitude(), bdLocation.getLongitude()),
                                new LatLng(lastUpdateLoc.getLatitude(), lastUpdateLoc.getLongitude())) > 200){
                            updateLoc(bdLocation);
                        }
                        LocationManager.getInstance().unRegisterLocationListener(this);
                    }

                    @Override
                    public void onFail(String msg) {
                        //.e("locUpdateService","fail");
                        LocationManager.getInstance().unRegisterLocationListener(this);
                    }
                });
                try {
                    Thread.sleep(60 * 1000);
                } catch (InterruptedException e) {
                    break;
                }
            }
        });
        locUpdateThread.start();
    }

    private void updateLoc(BDLocation bdLocation) {
        Log.e("updateLoc","updateLoc");
        NearByPeople nearByPeople = new NearByPeople();
        nearByPeople.setCountry(bdLocation.getCountry());
        nearByPeople.setProvince(bdLocation.getProvince());
        nearByPeople.setCity(bdLocation.getCity());
        nearByPeople.setDistrict(bdLocation.getDistrict());
        nearByPeople.setAddr(bdLocation.getAddrStr());
        nearByPeople.setLongitude(bdLocation.getLongitude());
        nearByPeople.setLatitude(bdLocation.getLatitude());
        nearByPeople.setAltitude(bdLocation.getAltitude());
        UpdateLocReq req = new UpdateLocReq();
        req.setNearByPeople(nearByPeople);
        new SocketRequest().request(ClientSocket, req, new SocketRequest.IRequest() {
            @Override
            public void onError(String msg) {}

            @Override
            public void onFinished(ResponseMessage mResponse){
                lastUpdateLoc = bdLocation;
                lastUpdateLocMilli = System.currentTimeMillis();
            }
        });
    }

    /*private void acquareWakeLock() {
        pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        wakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "CPUKeepRunning");
        wakeLock.acquire();
    }

    private void unAcquareWakeLock(){
        if(wakeLock != null){
            wakeLock.release();
            wakeLock = null;
        }
    }
*/
    public void stopUpdateLocService(){
        this.showLocToNearBy = false;
        if(locUpdateThread != null){
            locUpdateThread.interrupt();
        }
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
        unregisterReceiver(mScreenStatusReceiver);
        /*if(wakeLock != null){
            wakeLock.release();
            wakeLock = null;
        }*/
        /*if (mTelephonyManager != null) {
            mTelephonyManager.listen(mPhoneStateListener, PhoneStateListener.LISTEN_NONE);
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
                    if(message instanceof NotifyPayMemberOk){
                        notifyPayMemberOk((NotifyPayMemberOk)message);
                        return;
                    }
                    if(message instanceof NotifyHasRecommend){
                        notifyHasRecommend((NotifyHasRecommend) message);
                        return;
                    }
                    if(message instanceof NotifyFriendReq){
                        addFriendUtil.onNotifyFriendReq(((NotifyFriendReq)message), getApplicationContext());
                        return;
                    }
                    if(message instanceof NotifyAcceptFriend){
                        addFriendUtil.onNotifyAcceptFriend(((NotifyAcceptFriend)message), getApplicationContext());
                        return;
                    }

                    if(message instanceof NotifyManagerNotify){
                        notifyManagerNoyify((NotifyManagerNotify)message);
                        return;
                    }

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

    private void notifyManagerNoyify(NotifyManagerNotify message) {
        Intent intent = new Intent(getApplicationContext(), TagReplyActivity.class);
        intent.putExtra("tagMessUid", message.getMessUid());
        NotifyUtil.notify("通知公告消息", "来自" + message.getManagerNickname(), message.getMessageTitle(),
                message.getManagerPhoto(), intent, getApplicationContext());
    }

    private void notifyPayMemberOk(NotifyPayMemberOk message) {
        User user = SPUtil.getInstance().getUser();
        if(user == null){
            return;
        }
        user.setNumberValidTime(message.getValidDate());
        SPUtil.getInstance().putUser(user);
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        NotifyUtil.notify("系统消息", "会员充值成功", ("会员有效期至" + message.getValidDate().split(" ")[0]),
                user.getPhoto().getFilePath(), intent, getApplicationContext());
    }

    /**
     * 暂时弃用
     * */
    private void notifyHasRecommend(NotifyHasRecommend message) {
        /*if(message.getRecommendtype() == 1){
            hasRecomdMess = true;
        }else if(message.getRecommendtype() == 2){
            hasRecomdProple = true;
        }
        if(mainActivity != null && mainActivity.homeFragment != null
                && mainActivity.homeFragment.homeChoiceFragment != null){
            mainActivity.homeFragment.homeChoiceFragment.notifyHasRecomds();
        }*/
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
                    reConn(false);
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
                    /*if(!MyApplication.isConnecting) {
                        showShortToas("lose connection");
                    }*/
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

    public synchronized void reConn(boolean ignoreIsConn) {
        if(isConnecting){
            return;
        }
        if(!NetWorkUtil.isNetWorkable(getApplicationContext())){
            return;
        }
        /*if(!ignoreIsConn && isConnected() *//*|| isConnecting*//*){
            return;
        }*/
        if(isConnected()){
            return;
        }
        String arrays = SPUtil.getInstance().getArrays();
        if(SPUtil.getInstance().getUser() == null || arrays == null || arrays.isEmpty()){
            if(loginOut){
                loginOut = false;
                return;
            }
            if(mainActivity != null && startActivityCount > 0){
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
                MyApplication.ClientSocket = clientSocket;
                //MyApplication.ClientSocket.setHeartbeat(4* 60 * 1000);
                MyApplication.ClientSocket.setHeartbeat(60 * 1000);
                MyApplication.getApplication().registerSocketRecListerer();
                MyApplication.getApplication().registerSocketSendListerer();
                MyApplication.getApplication().registerSocketStatusListerer();
                MyApplication.getApplication().registSendProgListerer();
                tokenLogin(arrays);
            }
        }, Constant.HOST, Constant.PORT);
    }

    public synchronized void tokenLogin(String arrays) {
        if(!isConnecting){
            return;
        }
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
                LoginResponse loginResponse = (LoginResponse) mResponse;
                if (loginResponse.getCode() == 200) {
                    SPUtil.getInstance().putUser(loginResponse.getUser());
                    SPUtil.getInstance().putArrays(loginResponse.getTokenId(), loginResponse.getUser().getUserName());
                    MyApplication.ClientSocket.setTokenId(loginResponse.getTokenId());
                }
                isConnecting = false;
            }
        });
    }

    private BroadcastReceiver mScreenStatusReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.e(TAG, "mScreenStatusReceiver: " + intent.getAction());
            if (Intent.ACTION_SCREEN_ON.equals(intent.getAction())) {
                /*if (!isCalling) {
                    Intent lockScreenIntent = new Intent(getApplicationContext(), LockScreenActivity.class);
                    lockScreenIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(lockScreenIntent);
                }*/
            } else if (Intent.ACTION_SCREEN_OFF.equals(intent.getAction())) {
                //1：不可用; 2: 会员可用; 3: 可以体验; 其他: 体验过期时间
                /*long validStatus = new KeyguardGalleryUtil().validStatus();
                Log.e("validStatus", "validStatus: " + validStatus);
                if(SPUtil.getInstance().getInt(SPUtil.SHOW_KEYGUARD_GALLERY, 1) == 1 &&
                        validStatus != 1 && validStatus != 3) {*/
                if(SPUtil.getInstance().getInt(SPUtil.SHOW_KEYGUARD_GALLERY, 1) == 1){
                    Intent lockScreenIntent = new Intent(getApplicationContext(), LockScreenActivity.class);
                    lockScreenIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(lockScreenIntent);
                }
            }
        }
    };

    public void updateApk(){
        if(isUpdateApk){
            return;
        }
        File file = new File((SPUtil.getInstance().getString(tl.pojul.com.fastim.util.Constant.BASE_STORAGE_PATH) + "/footstep/apk/"));
        if (!file.exists() && !file.mkdirs()) {
            showShortToas("创建下载文件失败");
            return;
        }
        File fileApk = new File(file.getAbsolutePath() + "/footstep.apk");
        if(fileApk.exists()){
            fileApk.delete();
        }
        isUpdateApk = true;
        lastUpdateApkProgress = 0;
        DownLoadManager.getInstance().downloadFile("http://"+Constant.HOST+":8080/resources/app/detail/footstep_signed1.0.apk",
                (file.getAbsolutePath() + "/footstep.apk"),
                new DownloadCallBack());
    }

    class DownloadCallBack extends DownLoadCallBack {
        @Override
        public void downloadFail(DownloadTask task) {
            super.downloadFail(task);
            isUpdateApk = false;
            NotifyUtil.unNotifyUpdateApk(getApplicationContext());
        }

        @Override
        public void downloadProgress(DownloadTask task) {
            super.downloadProgress(task);
            Log.e(TAG, "progress: " + task.getProgress());
            if((task.getProgress()-lastUpdateApkProgress) < 2){
                return;
            }
            lastUpdateApkProgress = task.getProgress();
            NotifyUtil.notifyUpdateApk(task.getProgress(), getApplicationContext());
        }

        @Override
        public void downloadCompete(DownloadTask task) {
            super.downloadCompete(task);
            isUpdateApk = false;
            NotifyUtil.unNotifyUpdateApk(getApplicationContext());
            File file = new File((SPUtil.getInstance().getString(tl.pojul.com.fastim.util.Constant.BASE_STORAGE_PATH) +
                    "/footstep/apk/footstep.apk"));
            if(!file.exists()){
                showLongToas("下载文件不存在");
                return;
            }
            try{
                Intent intent = new Intent();
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.setAction(Intent.ACTION_VIEW);  //设置intent的Action属性
                String type = FileUtil.getMIMEType(file);  //获取文件file的MIME类型

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    Uri contentUri = FileProvider.getUriForFile(getApplicationContext(), BuildConfig.APPLICATION_ID + ".fileProvider", file);
                    intent.setDataAndType(contentUri, "application/vnd.android.package-archive");
                } else {
                    intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                }

                //intent.setDataAndType(/*uri*/Uri.fromFile(file), type);   //设置intent的data和Type属性。
                startActivity(intent);     //比如说你的MIME类型是打开邮箱，但是你手机里面没装邮箱客户端，就会报错。
            }catch (Exception e){
                showShortToas("找不到对应的文件查看器");
            }
        }
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
            if(NetWorkUtil.isNetWorkable(getApplicationContext()) && !isConnected() &&
                    !(activity instanceof RegistActivity) && !(activity instanceof WebviewActivity)){
                reConn(false);
            }
        }

        @Override
        public void onActivityResumed(Activity activity) {

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
