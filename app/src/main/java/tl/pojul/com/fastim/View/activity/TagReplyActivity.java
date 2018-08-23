package tl.pojul.com.fastim.View.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.baidu.location.BDLocation;
import com.google.gson.Gson;
import com.pojul.fastIM.entity.CommunityRoom;
import com.pojul.fastIM.entity.User;
import com.pojul.fastIM.message.chat.CommunityMessage;
import com.pojul.fastIM.message.chat.ReplyMessage;
import com.pojul.fastIM.message.chat.SubReplyMessage;
import com.pojul.fastIM.message.chat.TagCloseMessage;
import com.pojul.fastIM.message.chat.TagCommuMessage;
import com.pojul.fastIM.message.request.CheckTagEffictiveReq;
import com.pojul.fastIM.message.request.CloseTagMessReq;
import com.pojul.fastIM.message.request.GetCommunRoomByMessReq;
import com.pojul.fastIM.message.request.GetReplysReq;
import com.pojul.fastIM.message.request.GetTagMessByUidReq;
import com.pojul.fastIM.message.request.ReplyMessReq;
import com.pojul.fastIM.message.response.CheckTagEffictiveResp;
import com.pojul.fastIM.message.response.GetCommunRoomByMessResp;
import com.pojul.fastIM.message.response.GetReplysResp;
import com.pojul.fastIM.message.response.GetTagMessByUidResp;
import com.pojul.objectsocket.message.BaseMessage;
import com.pojul.objectsocket.message.ResponseMessage;
import com.pojul.objectsocket.socket.SocketRequest;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadmoreListener;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import tl.pojul.com.fastim.MyApplication;
import tl.pojul.com.fastim.R;
import tl.pojul.com.fastim.View.Adapter.ReplyAdapter;
import tl.pojul.com.fastim.View.widget.PolygonImage.view.PolygonImageView;
import tl.pojul.com.fastim.dao.ConversationDao;
import tl.pojul.com.fastim.map.baidu.LocationManager;
import tl.pojul.com.fastim.socket.Converter.HistoryChatConverter;
import tl.pojul.com.fastim.util.ArrayUtil;
import tl.pojul.com.fastim.util.CustomTimeDown;
import tl.pojul.com.fastim.util.DateUtil;
import tl.pojul.com.fastim.util.DialogUtil;
import tl.pojul.com.fastim.util.GlideUtil;
import tl.pojul.com.fastim.util.MyDistanceUtil;
import tl.pojul.com.fastim.util.NotifyUtil;
import tl.pojul.com.fastim.util.SPUtil;

public class TagReplyActivity extends BaseActivity implements CustomTimeDown.OnTimeDownListener {


    @BindView(R.id.back)
    ImageView back;
    @BindView(R.id.own_photo)
    PolygonImageView ownPhoto;
    @BindView(R.id.own_nick_name)
    TextView ownNickName;
    @BindView(R.id.owner_certificate)
    TextView ownerCertificate;
    @BindView(R.id.owner_sex)
    ImageView ownerSex;
    @BindView(R.id.time)
    TextView time;
    @BindView(R.id.header)
    RelativeLayout header;
    @BindView(R.id.header_line)
    View headerLine;
    @BindView(R.id.reply_list)
    RecyclerView replyList;
    @BindView(R.id.smart_refresh)
    SmartRefreshLayout smartRefresh;
    @BindView(R.id.add)
    ImageView add;
    @BindView(R.id.input)
    EditText input;
    @BindView(R.id.send)
    Button send;
    @BindView(R.id.input1_rl)
    RelativeLayout input1Rl;
    @BindView(R.id.more_message)
    RecyclerView moreMessage;
    @BindView(R.id.search_content)
    EditText searchContent;
    @BindView(R.id.search_submit)
    TextView searchSubmit;
    @BindView(R.id.search_engine)
    TextView searchEngine;
    @BindView(R.id.search_pic_title)
    RelativeLayout searchPicTitle;
    @BindView(R.id.search_pics)
    RecyclerView searchPics;
    @BindView(R.id.rl_search_pic)
    RelativeLayout rlSearchPic;
    @BindView(R.id.tag_reply_rl)
    RelativeLayout tagReplyRl;
    @BindView(R.id.close_tag_mess)
    TextView closeTagMess;
    @BindView(R.id.tags)
    TextView tags;

    private TagCommuMessage tagCommuMessage;
    private List<String> picPaths = new ArrayList<>();
    private User user;
    private CustomTimeDown customTimeDown;
    private BDLocation mBDLocation;
    private CommunityRoom communityRoom;
    private static final int INIT = 1001;
    private List<ReplyMessage> mlist = new ArrayList<>();
    private ReplyAdapter replyAdapter;
    private String tagMessUid;

    private String lastReplyuid;
    private int resultCode = RESULT_CANCELED;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tag_reply);
        ButterKnife.bind(this);

        mHandler.sendEmptyMessageDelayed(INIT, 100);

    }

    private void init() {
        user = SPUtil.getInstance().getUser();
        if (user == null) {
            finish();
            return;
        }
        String json = getIntent().getStringExtra("TagCommuMessage");
        tagMessUid = getIntent().getStringExtra("tagMessUid");
        if (json == null && tagMessUid == null) {
            finish();
            return;
        }
        if(json != null){
            try {
                tagCommuMessage = new Gson().fromJson(json, TagCommuMessage.class);
            } catch (Exception e) {
            }
            if (tagCommuMessage == null) {
                finish();
                return;
            }
        }

        if(tagCommuMessage != null){
            initView();
        }

        if(tagCommuMessage != null){
            getChatRoom();
            reqReplys(true);
            checkEffictive();
            reqMessage();
        }else if(tagMessUid != null){
            getTagMess(tagMessUid);
        }
    }

    private void initView() {
        MyApplication.currentReplyActivity = tagCommuMessage.getMessageUid();
        replyList.setLayoutManager(new LinearLayoutManager(this));
        replyAdapter = new ReplyAdapter(this, mlist, user, tagCommuMessage);
        replyList.setAdapter(replyAdapter);
        replyList.setHasFixedSize(true);
        replyList.setNestedScrollingEnabled(false);

        GlideUtil.setImageBitmap(tagCommuMessage.getPhoto().getFilePath(), ownPhoto);
        ownNickName.setText(tagCommuMessage.getNickName());
        if (tagCommuMessage.getCertificate() == 0) {
            ownerCertificate.setText("未实名认证");
        } else {
            ownerCertificate.setText("已实名认证");
        }
        if (tagCommuMessage.getUserSex() == 0) {
            ownerSex.setImageResource(R.drawable.woman);
        } else {
            ownerSex.setImageResource(R.drawable.man);
        }
        time.setText(DateUtil.getHeadway(tagCommuMessage.getTimeMill()));
        add.setVisibility(View.GONE);
        input.setHint("我来说两句...");
        tags.setText(ArrayUtil.toCommaSplitStr(tagCommuMessage.getLabels()));
        updateEffictiveUi();

        smartRefresh.setEnableRefresh(false);
        smartRefresh.setPrimaryColors(Color.parseColor("#898989"));
        smartRefresh.setEnableLoadmore(true);
        smartRefresh.setOnLoadmoreListener(new OnLoadmoreListener() {
            @Override
            public void onLoadmore(RefreshLayout refreshlayout) {
                reqReplys(false);
            }
        });

        customTimeDown = new CustomTimeDown(Long.MAX_VALUE, 30 * 1000);
        customTimeDown.setOnTimeDownListener(this);
        customTimeDown.start();

        MyApplication.getApplication().registerSendMessage(iSendMessage);
        MyApplication.getApplication().registerReceiveMessage(iReceiveMessage);

        new ConversationDao().updateUnReadNumByUid(tagCommuMessage.getMessageUid() ,0);
    }

    private void getTagMess(String tagMessUid) {
        GetTagMessByUidReq req = new GetTagMessByUidReq();
        req.setTagMessUid(tagMessUid);
        DialogUtil.getInstance().showLoadingSimple(this, getRootView());
        new SocketRequest().request(MyApplication.ClientSocket, req, new SocketRequest.IRequest() {
            @Override
            public void onError(String msg) {
                runOnUiThread(()->{
                    DialogUtil.getInstance().dimissLoadingDialog();
                    showShortToas(msg);
                    finish();
                });
            }

            @Override
            public void onFinished(ResponseMessage mResponse) {
                runOnUiThread(()->{
                    DialogUtil.getInstance().dimissLoadingDialog();
                    if(mResponse.getCode() == 200){
                        CommunityMessage communityMessage = new HistoryChatConverter()
                                .converCommunityMess(((GetTagMessByUidResp)mResponse).getCommunityMessEntity(), true);
                        if(communityMessage == null || !(communityMessage instanceof TagCommuMessage)){
                            showShortToas("数据错误");
                            finish();
                            return;
                        }
                        tagCommuMessage = (TagCommuMessage) communityMessage;
                        getChatRoom();
                        reqReplys(true);
                        reqMessage();
                        initView();
                    }else{
                        showShortToas(mResponse.getMessage());
                        finish();
                    }
                });
            }
        });
    }

    private void checkEffictive() {
        CheckTagEffictiveReq checkTagEffictiveReq = new CheckTagEffictiveReq();
        checkTagEffictiveReq.setTagMessUid(tagCommuMessage.getMessageUid());
        new SocketRequest().request(MyApplication.ClientSocket, checkTagEffictiveReq, new SocketRequest.IRequest() {
            @Override
            public void onError(String msg) {}

            @Override
            public void onFinished(ResponseMessage mResponse) {
                runOnUiThread(() -> {
                    if(mResponse.getCode() == 200){
                        int checkEffictive = ((CheckTagEffictiveResp)mResponse).getEffictive();
                        if(checkEffictive != tagCommuMessage.getIsEffective()){
                            tagCommuMessage.setIsEffective(checkEffictive);
                            resultCode = RESULT_OK;
                            updateEffictiveUi();
                        }
                    }
                });
            }
        });
    }

    private void updateEffictiveUi() {
        if(tagCommuMessage.getIsEffective() == 1){
            closeTagMess.setClickable(false);
            closeTagMess.setText("已失效");
        }else{
            if(user.getUserName().equals(tagCommuMessage.getFrom())){
                closeTagMess.setText("关闭该消息");
                closeTagMess.setClickable(true);
            }else{
                closeTagMess.setText("有效");
                closeTagMess.setClickable(false);
            }
        }
    }

    private void reqMessage() {
        ReplyMessReq replyMessReq = new ReplyMessReq();
        replyMessReq.setReplyMessUid(tagCommuMessage.getMessageUid());
        replyMessReq.setReqCode(1);
        new SocketRequest().request(MyApplication.ClientSocket, replyMessReq, new SocketRequest.IRequest() {
            @Override
            public void onError(String msg) {
                runOnUiThread(() -> {
                    showShortToas("请求消息失败");
                });
            }

            @Override
            public void onFinished(ResponseMessage mResponse) {
                runOnUiThread(() -> {
                });
            }
        });
    }

    private void reqReplys(boolean newStart) {
        GetReplysReq getReplysReq = new GetReplysReq();
        if (newStart) {
            lastReplyuid = null;
            DialogUtil.getInstance().showLoadingSimple(this, getRootView());
        }
        getReplysReq.setLastMessUid(lastReplyuid);
        getReplysReq.setNum(10);
        getReplysReq.setReplyTagMessUid(tagCommuMessage.getMessageUid());
        new SocketRequest().request(MyApplication.ClientSocket, getReplysReq, new SocketRequest.IRequest() {
            @Override
            public void onError(String msg) {
                runOnUiThread(() -> {
                    smartRefresh.finishLoadmore();
                    DialogUtil.getInstance().dimissLoadingDialog();
                    showShortToas(msg);
                });
            }

            @Override
            public void onFinished(ResponseMessage mResponse) {
                runOnUiThread(() -> {
                    smartRefresh.finishLoadmore();
                    DialogUtil.getInstance().dimissLoadingDialog();
                    if (mResponse.getCode() == 200) {
                        List<ReplyMessage> tempReplys = ((GetReplysResp) mResponse).getReplyMessages();
                        if (tempReplys.size() < 10) {
                            smartRefresh.setEnableLoadmore(false);
                        }
                        replyAdapter.addMoreData(tempReplys);
                        if (tempReplys.size() > 0) {
                            lastReplyuid = tempReplys.get((tempReplys.size() - 1)).getMessageUid();
                        }
                    } else {
                        smartRefresh.setEnableLoadmore(false);
                        showShortToas(mResponse.getMessage());
                    }
                });
            }
        });
    }

    private void getChatRoom() {
        if (tagCommuMessage == null) {
            showShortToas("数据错误");
            finish();
            return;
        }
        GetCommunRoomByMessReq req = new GetCommunRoomByMessReq();
        req.setCommunMessUid(tagCommuMessage.getMessageUid());
        new SocketRequest().request(MyApplication.ClientSocket, req, new SocketRequest.IRequest() {
            @Override
            public void onError(String msg) {
                runOnUiThread(() -> {
                    showShortToas("获取社区信息失败");
                });
            }

            @Override
            public void onFinished(ResponseMessage mResponse) {
                runOnUiThread(() -> {
                    if (mResponse.getCode() == 200) {
                        communityRoom = ((GetCommunRoomByMessResp) mResponse).getCommunityRoom();
                    } else {
                        showShortToas("获取社区信息失败");
                    }
                });
            }
        });
    }

    @OnClick({R.id.back, R.id.own_photo, R.id.send, R.id.close_tag_mess})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.back:
                finishResult();
                break;
            case R.id.own_photo:
                break;
            case R.id.send:
                if (input.getText().toString().isEmpty()) {
                    showShortToas("内容不能为空");
                    return;
                }
                sendReplyMessage(input.getText().toString());
                break;
            case R.id.close_tag_mess:
                DialogUtil.getInstance().showPromptDialog(TagReplyActivity.this, "确定关闭该消息",
                        "关闭该消息后将不再提供与该消息相关的回复提醒功能");
                DialogUtil.getInstance().setDialogClick(str -> {
                    if("确定".equals(str)){
                        closeTagmess();
                    }
                });
                break;
        }
    }

    private void closeTagmess() {
        CloseTagMessReq closeTagMessReq = new CloseTagMessReq();
        closeTagMessReq.setTagMessUid(tagCommuMessage.getMessageUid());
        DialogUtil.getInstance().showLoadingSimple(this, getRootView());
        new SocketRequest().request(MyApplication.ClientSocket, closeTagMessReq, new SocketRequest.IRequest() {
            @Override
            public void onError(String msg) {
                runOnUiThread(()->{
                    showShortToas(msg);
                    DialogUtil.getInstance().dimissLoadingDialog();
                });
            }

            @Override
            public void onFinished(ResponseMessage mResponse) {
                runOnUiThread(()->{
                    DialogUtil.getInstance().dimissLoadingDialog();
                    if(mResponse.getCode() == 200){
                        closeTagMess.setText("已失效");
                        closeTagMess.setClickable(false);
                        tagCommuMessage.setIsEffective(1);
                        resultCode = RESULT_OK;
                    }else{
                        showShortToas(mResponse.getMessage());
                    }
                });
            }
        });
    }

    private LocationManager.ILocationListener iLocationListener = new LocationManager.ILocationListener() {
        @Override
        public void onReceive(BDLocation bdLocation) {
            LocationManager.getInstance().unRegisterLocationListener(iLocationListener);
            mBDLocation = bdLocation;
        }

        @Override
        public void onFail(String msg) {
            LocationManager.getInstance().unRegisterLocationListener(iLocationListener);
            showShortToas("位置获取失败");
        }
    };

    private void sendReplyMessage(String text) {
        if (!MyApplication.getApplication().isConnected()) {
            showShortToas("与服务器已断开连接");
            return;
        }
        if (mBDLocation == null) {
            showShortToas("获取不到位置信息");
            return;
        }
        if (communityRoom == null) {
            showShortToas("获取社区信息失败");
            getChatRoom();
            return;
        }
        input.setText("");
        ReplyMessage replyMessage = new ReplyMessage();
        replyMessage.setFrom(user.getUserName());
        replyMessage.setReplyMessageUid(tagCommuMessage.getMessageUid());
        replyMessage.setIsSpaceTravel(MyDistanceUtil.isSpaceTravel(communityRoom, mBDLocation, user.getShowCommunityLoc()));
        replyMessage.setUserSex(user.getSex());
        replyMessage.setCertificate(user.getCertificate());
        replyMessage.setNickName(user.getNickName());
        replyMessage.setPhoto(user.getPhoto());
        replyMessage.setText(text);
        replyMessage.setTimeMilli(System.currentTimeMillis());
        MyApplication.ClientSocket.sendData(replyMessage);
        replyAdapter.addData(replyMessage);
        replyList.smoothScrollToPosition(1);
    }

    public void sendSubReplyMessage(String subText, String replyUid) {
        if (!MyApplication.getApplication().isConnected()) {
            showShortToas("与服务器已断开连接");
            return;
        }
        if (mBDLocation == null) {
            showShortToas("获取不到位置信息");
            return;
        }
        if (communityRoom == null) {
            showShortToas("获取社区信息失败");
            getChatRoom();
            return;
        }
        SubReplyMessage subReplyMessage = new SubReplyMessage();
        subReplyMessage.setReplyTagMessUid(tagCommuMessage.getMessageUid());
        subReplyMessage.setFrom(user.getUserName());
        subReplyMessage.setReplyMessageUid(replyUid);
        subReplyMessage.setUserName(user.getUserName());
        subReplyMessage.setNickName(user.getNickName());
        subReplyMessage.setIsSpaceTravel(MyDistanceUtil.isSpaceTravel(communityRoom, mBDLocation, user.getShowCommunityLoc()));
        subReplyMessage.setText(subText);
        subReplyMessage.setTimeMilli(System.currentTimeMillis());
        MyApplication.ClientSocket.sendData(subReplyMessage);
        replyAdapter.addSubData(subReplyMessage);
    }

    private void finishResult(){
        Intent intent = new Intent();
        intent.putExtra("tagMessUid", tagCommuMessage.getMessageUid());
        intent.putExtra("isEffictive", tagCommuMessage.getIsEffective());
        setResult(resultCode, intent);
        finish();
    }

    private MyHandler mHandler = new MyHandler(this);

    static class MyHandler extends Handler {
        //注意下面的“”类是MyHandler类所在的外部类，即所在的activity或者fragment
        WeakReference<TagReplyActivity> activity;

        MyHandler(TagReplyActivity activity) {
            this.activity = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (activity.get() == null) {
                return;
            }
            switch (msg.what) {
                case INIT:
                    activity.get().init();
                    break;
            }
        }
    }

    private MyApplication.IReceiveMessage iReceiveMessage = new MyApplication.IReceiveMessage() {
        @Override
        public void receiveMessage(BaseMessage message) {
            if (message instanceof ReplyMessage) {
                replyAdapter.addData((ReplyMessage) message);
                if(MyApplication.startActivityCount <= 0){
                    NotifyUtil.notifyChatMess("回复消息",
                            (message.getFrom() + "回复了你\"" + tagCommuMessage.getTitle() + "\""),
                            ((ReplyMessage) message).getText(),
                            ((ReplyMessage) message).getPhoto().getFilePath(),
                            MyApplication.getApplication().getApplicationContext());
                }
            }
            if (message instanceof SubReplyMessage) {
                replyAdapter.addSubData((SubReplyMessage) message);
            }
            if(message instanceof TagCloseMessage){
                closeTagMess.setText("已失效");
                closeTagMess.setClickable(false);
                tagCommuMessage.setIsEffective(1);
                resultCode = RESULT_OK;
            }
            //showLongToas("iReceiveMessage--->" + message.getFrom());
        }
    };

    private MyApplication.ISendMessage iSendMessage = new MyApplication.ISendMessage() {

        @Override
        public void onSendFinish(BaseMessage message) {
            if (message instanceof ReplyMessage) {
                replyAdapter.onSendFinish((ReplyMessage) message);
            } else if (message instanceof SubReplyMessage) {
                message.setIsSend(1);
                replyAdapter.onSubSendFinish((SubReplyMessage) message);
            }
        }

        @Override
        public void onSendFail(BaseMessage message) {
            if (message instanceof ReplyMessage) {
                replyAdapter.onSendFinish((ReplyMessage) message);
            } else if (message instanceof SubReplyMessage) {
                message.setIsSend(-1);
                replyAdapter.onSubSendFinish((SubReplyMessage) message);
            }
        }
    };

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_UP) {
            finishResult();
        }
        return true;
    }

    @Override
    public void onTick(long l) {
        LocationManager.getInstance().registerLocationListener(iLocationListener);
    }

    @Override
    public void OnFinish() {

    }

    @Override
    protected void onDestroy() {
        DialogUtil.getInstance().dimissLoadingDialog();
        if(tagCommuMessage != null){
            ReplyMessReq replyMessReq = new ReplyMessReq();
            replyMessReq.setReplyMessUid(tagCommuMessage.getMessageUid());
            replyMessReq.setReqCode(2);
            new SocketRequest().request(MyApplication.ClientSocket, replyMessReq, new SocketRequest.IRequest() {
                @Override
                public void onError(String msg) {
                }

                @Override
                public void onFinished(ResponseMessage mResponse) {
                }
            });
        }
        try{
            LocationManager.getInstance().unRegisterLocationListener(iLocationListener);
            MyApplication.getApplication().unRegisterSendMessage(iSendMessage);
            MyApplication.getApplication().unRegisterReceiveMessage(iReceiveMessage);
            MyApplication.currentReplyActivity = "";
        }catch (Exception e){
            replyAdapter.onDestroy();
            customTimeDown.cancel();
            customTimeDown = null;
        }
        super.onDestroy();
    }

    public View getRootView() {
        return tagReplyRl;
    }

}
