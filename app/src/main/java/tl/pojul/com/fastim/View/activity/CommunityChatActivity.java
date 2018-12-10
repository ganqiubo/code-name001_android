package tl.pojul.com.fastim.View.activity;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.bumptech.glide.Glide;
import com.dalong.marqueeview.MarqueeView;
import com.google.gson.Gson;
import com.pojul.fastIM.entity.CommunityMessEntity;
import com.pojul.fastIM.entity.CommunityRoom;
import com.pojul.fastIM.entity.MessageFilter;
import com.pojul.fastIM.entity.User;
import com.pojul.fastIM.message.chat.ChatMessage;
import com.pojul.fastIM.message.chat.CommunityMessage;
import com.pojul.fastIM.message.chat.TagCommuMessage;
import com.pojul.fastIM.message.chat.TextChatMessage;
import com.pojul.fastIM.message.request.CommunityMessageReq;
import com.pojul.fastIM.message.request.GetTopMessReq;
import com.pojul.fastIM.message.request.HistoryCommunReq;
import com.pojul.fastIM.message.response.GetTopMessResp;
import com.pojul.fastIM.message.response.HistoryCommunResp;
import com.pojul.objectsocket.message.BaseMessage;
import com.pojul.objectsocket.message.ResponseMessage;
import com.pojul.objectsocket.socket.SocketRequest;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import tl.pojul.com.fastim.Factory.ChatMessageFcctory;
import tl.pojul.com.fastim.MyApplication;
import tl.pojul.com.fastim.R;
import tl.pojul.com.fastim.View.Adapter.MessageAdapter;
import tl.pojul.com.fastim.View.Adapter.MoreMessageAdapter;
import tl.pojul.com.fastim.View.Adapter.SearchPicAdapter;
import tl.pojul.com.fastim.View.widget.SmoothLinearLayoutManager;
import tl.pojul.com.fastim.View.widget.marqueeview.SimpleMF;
import tl.pojul.com.fastim.View.widget.marqueeview.SimpleMarqueeView;
import tl.pojul.com.fastim.View.widget.marqueeview.util.OnItemClickListener;
import tl.pojul.com.fastim.map.baidu.LocationManager;
import tl.pojul.com.fastim.socket.Converter.HistoryChatConverter;
import tl.pojul.com.fastim.util.ArrayUtil;
import tl.pojul.com.fastim.util.CustomTimeDown;
import tl.pojul.com.fastim.util.DialogUtil;
import tl.pojul.com.fastim.util.MyDistanceUtil;
import tl.pojul.com.fastim.util.SPUtil;

public class CommunityChatActivity extends ChatRoomActivity implements CustomTimeDown.OnTimeDownListener {

    @BindView(R.id.back)
    ImageView back;
    @BindView(R.id.screen)
    ImageView screen;
    @BindView(R.id.community_name)
    TextView communityName;
    @BindView(R.id.header)
    RelativeLayout header;
    @BindView(R.id.header_line)
    View headerLine;
    @BindView(R.id.stick_ll)
    LinearLayout stickLl;
    @BindView(R.id.add)
    ImageView add;
    @BindView(R.id.input)
    EditText input;
    @BindView(R.id.send)
    Button send;
    @BindView(R.id.input1_rl)
    RelativeLayout input1Rl;
    @BindView(R.id.chat_contents)
    RecyclerView chatMessageList;
    @BindView(R.id.smart_refresh)
    SmartRefreshLayout smartRefresh;
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
    @BindView(R.id.input_rl)
    RelativeLayout inputRl;
    @BindView(R.id.community_chat_root)
    RelativeLayout communityChatRoot;
    @BindView(R.id.marquee_view)
    SimpleMarqueeView marqueeView;

    private MessageAdapter messageAdapter;
    private MoreMessageAdapter moreMessageAdapter;
    private SearchPicAdapter searchPicAdapter;

    private String searchPicWord = "";
    private int chatRoomType = 3;
    private User user;
    private CommunityRoom communityRoom;
    private String chatRoomName;
    private String chatRoomUid;
    private List<ChatMessage> messages = new ArrayList<>();
    private CustomTimeDown customTimeDown;
    private BDLocation mBDLocation;
    private MessageFilter messageFilter;
    private String lastMessageUid;
    private boolean isLoading;
    private static final int INIT = 1001;

    private List<TagCommuMessage> topMessages = new ArrayList<>();
    private long lastTopMessMilli = System.currentTimeMillis();
    private long topMessInterval = 5 * 60 * 1000;
    private boolean isResume;
    private List<SpannableString> topMessSpan;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /*File file = new File((SPUtil.getInstance().getString(Constant.BASE_STORAGE_PATH) + "/BaiduMap/custom_config"));
        if (file.exists()) {
            MapView.setCustomMapStylePath((SPUtil.getInstance().getString(Constant.BASE_STORAGE_PATH) + "/BaiduMap/custom_config"));
        }*/
        setContentView(R.layout.activity_community_chat);
        ButterKnife.bind(this);
        //MapView.setMapCustomEnable(true);

        //initData();
        mHandler.sendEmptyMessageDelayed(INIT, 100);
    }

    private void initData() {

        user = SPUtil.getInstance().getUser();
        try {
            communityRoom = new Gson().fromJson(getIntent().getStringExtra("CommunityRoom"), CommunityRoom.class);
        } catch (Exception e) {
        }
        if (user == null || communityRoom == null) {
            finish();
            return;
        }
        smartRefresh.setEnableLoadmore(false);
        chatRoomName = communityRoom.getName();
        chatRoomUid = communityRoom.getCommunityUid();
        communityName.setText(chatRoomName);

        SmoothLinearLayoutManager layoutmanager = new SmoothLinearLayoutManager(this);
        chatMessageList.setLayoutManager(layoutmanager);
        messageAdapter = new MessageAdapter(this, messages, user, null);
        chatMessageList.setAdapter(messageAdapter);
        chatMessageList.scrollToPosition(messageAdapter.getItemCount() - 1);


        moreMessageAdapter = new MoreMessageAdapter(this, chatRoomType);
        GridLayoutManager layoutManager = new GridLayoutManager(this, 4);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        moreMessage.setLayoutManager(layoutManager);

        StaggeredGridLayoutManager staggeredGridLayoutManager = new StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL);
        searchPics.setLayoutManager(staggeredGridLayoutManager);
        searchPicAdapter = new SearchPicAdapter(this, new ArrayList<>(), 3);
        staggeredGridLayoutManager.setGapStrategy(StaggeredGridLayoutManager.GAP_HANDLING_NONE);
        searchPics.setAdapter(searchPicAdapter);
        searchPics.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                switch (newState) {
                    case RecyclerView.SCROLL_STATE_IDLE:
                        Glide.with(CommunityChatActivity.this).resumeRequests();
                        int[] lastPositions = new int[staggeredGridLayoutManager.getSpanCount()];
                        staggeredGridLayoutManager.findLastVisibleItemPositions(lastPositions);
                        int lastPosition = ArrayUtil.findMax(lastPositions);
                        if (lastPosition >= (staggeredGridLayoutManager.getItemCount() - 1)) {
                            if (!"".equals(searchPicWord)) {
                                searchPicAdapter.searchNetPic(searchPicWord);
                            }
                        }
                        break;
                    case RecyclerView.SCROLL_STATE_DRAGGING:
                        //Glide.with(SingleChatRoomActivity.this).pauseRequests();
                        break;
                    case RecyclerView.SCROLL_STATE_SETTLING:
                        //Glide.with(SingleChatRoomActivity.this).pauseRequests();
                        break;
                }
            }
        });

        reqCommunityMessage();

        smartRefresh.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                getHistoryChat(20, false);
            }
        });

        customTimeDown = new CustomTimeDown(Long.MAX_VALUE, 30 * 1000);
        customTimeDown.setOnTimeDownListener(this);
        customTimeDown.start();

        messageFilter = new MessageFilter();

        MyApplication.getApplication().registerReceiveMessage(iReceiveMessage);
        MyApplication.getApplication().registerSendMessage(iSendMessage);
        MyApplication.getApplication().registerSendProgress(iSendProgress);

        getHistoryChat(20, true);

        getTopMess(6);

        marqueeView.setOnItemClickListener((OnItemClickListener<TextView, SpannableString>) (mView, mData, mPosition) -> {
            //showShortToas("marqueeView position: " + mPosition);
            if(mPosition == -1 || mPosition >= topMessages.size()){
                return;
            }
            Intent intent = new Intent(CommunityChatActivity.this, TagReplyActivity.class);
            Bundle bundle=new Bundle();
            bundle.putString("tagMessUid", topMessages.get(mPosition).getMessageUid());
            intent.putExtras(bundle);
            startActivity(intent);
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        isResume = true;
        if((System.currentTimeMillis() - lastTopMessMilli) > topMessInterval){
            getTopMess(6);
        }
        /*if(topMessStr != null && topMessStr.size() >0 && currTopMess < topMessStr.size()){
            marqueeView.setText(topMessStr.get(currTopMess));
            marqueeView.startScroll();
        }*/
    }

    @Override
    protected void onPause() {
        super.onPause();
        isResume = false;
    }

    private void getTopMess(int num) {
        GetTopMessReq getTopMessReq = new GetTopMessReq();
        getTopMessReq.setCommunityUid(communityRoom.getCommunityUid());
        getTopMessReq.setNum(num);
        lastTopMessMilli = System.currentTimeMillis();
        new SocketRequest().request(MyApplication.ClientSocket, getTopMessReq, new SocketRequest.IRequest() {
            @Override
            public void onError(String msg) {
            }

            @Override
            public void onFinished(ResponseMessage mResponse) {
                runOnUiThread(()->{
                    if (mResponse.getCode() == 200) {
                        List<TagCommuMessage> tempTopMessages = new HistoryChatConverter()
                                .getTopMess(((GetTopMessResp) mResponse).getCommunityMessEntities());
                        setTopMess(tempTopMessages);
                    }
                });
            }
        });
    }

    private void getHistoryChat(int num, boolean newStart) {
        if (isLoading) {
            smartRefresh.finishRefresh(true);
            return;
        }
        isLoading = true;
        if (newStart) {
            lastMessageUid = null;
            DialogUtil.getInstance().showLoadingSimple(this, communityChatRoot);
        }
        HistoryCommunReq historyCommunReq = new HistoryCommunReq(chatRoomUid, lastMessageUid, num);
        historyCommunReq.setMessageFilter(messageFilter);
        new SocketRequest().request(MyApplication.ClientSocket, historyCommunReq, new SocketRequest.IRequest() {
            @Override
            public void onError(String msg) {
                runOnUiThread(() -> {
                    isLoading = false;
                    DialogUtil.getInstance().dimissLoadingDialog();
                    smartRefresh.finishRefresh(false);
                    showShortToas(msg);
                });
            }

            @Override
            public void onFinished(ResponseMessage mResponse) {
                runOnUiThread(() -> {
                    isLoading = false;
                    DialogUtil.getInstance().dimissLoadingDialog();
                    smartRefresh.finishRefresh(true);
                    if (mResponse.getCode() == 200) {
                        List<CommunityMessEntity> reponses = ((HistoryCommunResp) mResponse).getHistoryCommuMessList();
                        if (reponses != null && reponses.size() > 0) {
                            messageAdapter.addHistoryChat(reponses);
                            lastMessageUid = reponses.get(0).getMessageUid();
                        }
                    } else {
                        showShortToas(mResponse.getMessage());
                    }
                });
            }
        });

    }

    private void reqCommunityMessage() {
        CommunityMessageReq request = new CommunityMessageReq();
        request.setCommunityRoom(communityRoom);
        request.setUid(communityRoom.getCommunityUid());
        request.setReqCode(1);
        new SocketRequest().request(MyApplication.ClientSocket, request, new SocketRequest.IRequest() {
            @Override
            public void onError(String msg) {
                runOnUiThread(() -> {
                    showShortToas("获取消息失败");
                });
            }

            @Override
            public void onFinished(ResponseMessage mResponse) {
                runOnUiThread(() -> {
                });
            }
        });
    }

    public void showMoreMessage() {
        moreMessage.setAdapter(moreMessageAdapter);
        moreMessage.setVisibility(View.VISIBLE);
        Animator anim = AnimatorInflater.loadAnimator(this, R.animator.add_rotate);
        anim.setStartDelay(180);
        anim.setTarget(add);
        anim.start();
        add.setSelected(true);
    }

    public void hideMoreMessage() {
        moreMessage.setVisibility(View.GONE);
        Animator anim = AnimatorInflater.loadAnimator(this, R.animator.close_rotate);
        anim.setStartDelay(180);
        anim.setTarget(add);
        anim.start();
        add.setSelected(false);
    }

    @OnClick({R.id.back, R.id.screen, R.id.add, R.id.send, R.id.search_submit})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.back:
                if (DialogUtil.getInstance().isScreenPopShow()) {
                    DialogUtil.getInstance().dimissScreenPop();
                    return;
                }
                finish();
                break;
            case R.id.screen:
                if (DialogUtil.getInstance().isScreenPopShow()) {
                    return;
                }
                DialogUtil.getInstance().showScreenPop(CommunityChatActivity.this, screen, messageFilter, false);
                DialogUtil.getInstance().setScreenPopClick((DialogUtil.ScreenPopClick) messageFilter -> {
                    if (messageFilter != null) {
                        CommunityChatActivity.this.messageFilter = messageFilter;
                        messageAdapter.clearData();
                        messageAdapter.setMessageFilter(messageFilter);
                        getHistoryChat(20, true);
                    }
                });
                break;
            case R.id.add:
                if (add.isSelected()) {
                    if (rlSearchPic.getVisibility() == View.VISIBLE) {
                        hideSearchPic();
                        return;
                    }
                    hideMoreMessage();
                } else {
                    showMoreMessage();
                }
                break;
            case R.id.send:
                if (input.getText() == null || "".equals(input.getText().toString().trim())) {
                    return;
                }
                TextChatMessage textChatMessage = new ChatMessageFcctory().createTextMessage(input.getText().toString(), 3);
                sendChatMessage(new ChatMessageFcctory().createCommunityMessage(textChatMessage));
                input.setText("");
                break;
            case R.id.search_submit:
                searchPicWord = searchContent.getText().toString();
                if ("".equals(searchPicWord)) {
                    showShortToas("搜索内容不能为空");
                    return;
                }
                searchPicAdapter.clearData();
                searchPicAdapter.searchNetPic(searchPicWord);
                break;
        }
    }

    private MyHandler mHandler = new MyHandler(this);

    public void setTopMess(List<TagCommuMessage> tempTopMess) {
        if(tempTopMess.size() > 0){
            topMessages = tempTopMess;
            topMessSpan = new ArrayList<>();
            for (int i = 0; i < topMessages.size(); i++) {
                String text;
                if(topMessages.get(i).getText() != null && !topMessages.get(i).getText().isEmpty()){
                    text = topMessages.get(i).getNickName() + ": " + topMessages.get(i).getText();
                }else{
                    text = topMessages.get(i).getNickName() + ": " + topMessages.get(i).getTitle();
                }
                SpannableString spannableString = new SpannableString(text);
                int split = text.indexOf(":");
                if(split == -1){
                    topMessSpan.add(spannableString);
                    continue;
                }
                ForegroundColorSpan colorSpan = new ForegroundColorSpan(Color.parseColor("#1296db"));
                spannableString.setSpan(colorSpan, 0, split, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
                topMessSpan.add(spannableString);
            }
            SimpleMF<SpannableString> marqueeFactory = new SimpleMF(this);
            marqueeFactory.setData(topMessSpan);
            marqueeView.setMarqueeFactory(marqueeFactory);
            if(topMessSpan.size() <= 1){
                marqueeView.stopFlipping();
            }else{
                marqueeView.startFlipping();
            }
        }
    }

    static class MyHandler extends Handler {
        //注意下面的“”类是MyHandler类所在的外部类，即所在的activity或者fragment
        WeakReference<CommunityChatActivity> activity;

        MyHandler(CommunityChatActivity activity) {
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
                    activity.get().initData();
                    break;
            }
        }
    }

    @Override
    public void sendChatMessage(ChatMessage chatMessage) {
        if (!MyApplication.getApplication().isConnected()) {
            showShortToas(getString(R.string.disconnected));
            return;
        }
        if (!(chatMessage instanceof CommunityMessage)) {
            showShortToas(getString(R.string.send_data_format_error));
            return;
        }
        if (mBDLocation == null) {
            showShortToas(getString(R.string.get_location_fail));
            return;
        }
        CommunityMessage communityMessage = ((CommunityMessage) chatMessage);
        communityMessage.setCommunityName(chatRoomName);
        communityMessage.setFrom(user.getUserName());
        communityMessage.setTo(chatRoomUid);

        communityMessage.setIsSpaceTravel(MyDistanceUtil.isSpaceTravel(communityRoom, mBDLocation, user.getShowCommunityLoc()));
        communityMessage.setUserSex(user.getSex());
        communityMessage.setCertificate(user.getCertificate());
        communityMessage.setNickName(user.getNickName());
        communityMessage.setPhoto(user.getPhoto());
        communityMessage.setTimeMill(System.currentTimeMillis());
        communityMessage.setLatitude(mBDLocation.getLatitude());
        communityMessage.setLongitude(mBDLocation.getLongitude());

        MyApplication.ClientSocket.sendData(communityMessage);
        messageAdapter.addMessage(communityMessage);
        chatMessageList.scrollToPosition(messageAdapter.getItemCount() - 1);
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 121) {
            messageAdapter.onActivityResult(requestCode, resultCode, data);
        } else {
            hideMoreMessage();
            moreMessageAdapter.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void showSearchPic() {
        rlSearchPic.setVisibility(View.VISIBLE);
    }

    public void hideSearchPic() {
        rlSearchPic.setVisibility(View.GONE);
    }

    private MyApplication.IReceiveMessage iReceiveMessage = new MyApplication.IReceiveMessage() {
        @Override
        public void receiveMessage(BaseMessage message) {
            if (message instanceof CommunityMessage) {
                messageAdapter.receiveMessage((ChatMessage) message);
                chatMessageList.scrollToPosition(messageAdapter.getItemCount() - 1);
            }
            //showLongToas("iReceiveMessage--->" + message.getFrom());
        }
    };

    private MyApplication.ISendMessage iSendMessage = new MyApplication.ISendMessage() {

        @Override
        public void onSendFinish(BaseMessage message) {
            if (!(message instanceof CommunityMessage)) {
                return;
            }
            messageAdapter.onSendFinish(message);
        }

        @Override
        public void onSendFail(BaseMessage message) {
            if (!(message instanceof CommunityMessage)) {
                return;
            }
            messageAdapter.onSendFail(message);
        }
    };

    private MyApplication.ISendProgress iSendProgress = new MyApplication.ISendProgress() {
        @Override
        public void progress(BaseMessage message, int progress) {
            if (!(message instanceof CommunityMessage)) {
                return;
            }
            if (progress > 100) {
                progress = 100;
            }
            messageAdapter.updateSendProgress(message, progress);
        }

        @Override
        public void finish(BaseMessage message) {
        }
    };

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_UP) {
            if (rlSearchPic.getVisibility() == View.VISIBLE) {
                hideSearchPic();
                return true;
            } else if (moreMessage.getVisibility() == View.VISIBLE) {
                hideMoreMessage();
                return true;
            }
        }
        return super.onKeyUp(keyCode, event);
    }

    @Override
    public void onTick(long l) {
        //Log.e("onTick", "onTick");
        LocationManager.getInstance().registerLocationListener(iLocationListener);
        if(isResume && (System.currentTimeMillis() - lastTopMessMilli) > topMessInterval){
            getTopMess(6);
        }
    }

    @Override
    public void OnFinish() {

    }

    @Override
    protected void onDestroy() {
        DialogUtil.getInstance().dimissLoadingDialog();
        CommunityMessageReq request = new CommunityMessageReq();
        request.setUid(communityRoom.getCommunityUid());
        request.setReqCode(2);
        new SocketRequest().request(MyApplication.ClientSocket, request, new SocketRequest.IRequest() {
            @Override
            public void onError(String msg) {
            }

            @Override
            public void onFinished(ResponseMessage mResponse) {
            }
        });
        LocationManager.getInstance().unRegisterLocationListener(iLocationListener);
        MyApplication.getApplication().unRegisterReceiveMessage(iReceiveMessage);
        MyApplication.getApplication().unRegisterSendMessage(iSendMessage);
        MyApplication.getApplication().unRegisterSendProgress(iSendProgress);
        customTimeDown.cancel();
        customTimeDown = null;
        super.onDestroy();
    }
}
