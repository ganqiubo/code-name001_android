package tl.pojul.com.fastim.View.activity;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.pojul.fastIM.entity.Friend;
import com.pojul.fastIM.entity.User;
import com.pojul.fastIM.message.chat.ChatMessage;
import com.pojul.fastIM.message.chat.DateMessage;
import com.pojul.fastIM.message.chat.TextChatMessage;
import com.pojul.fastIM.message.request.HistoryChatReq;
import com.pojul.fastIM.message.response.HistoryChatResp;
import com.pojul.objectsocket.message.BaseMessage;
import com.pojul.objectsocket.message.RequestMessage;
import com.pojul.objectsocket.message.ResponseMessage;
import com.pojul.objectsocket.socket.SocketRequest;
import com.pojul.objectsocket.utils.UidUtil;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuRecyclerView;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import tl.pojul.com.fastim.Media.AudioManager;
import tl.pojul.com.fastim.Factory.ChatMessageFcctory;
import tl.pojul.com.fastim.Media.VideoManager;
import tl.pojul.com.fastim.MyApplication;
import tl.pojul.com.fastim.R;
import tl.pojul.com.fastim.View.Adapter.ConversationAdapter;
import tl.pojul.com.fastim.View.Adapter.MessageAdapter;
import tl.pojul.com.fastim.View.Adapter.MoreMessageAdapter;
import tl.pojul.com.fastim.View.Adapter.SearchPicAdapter;
import tl.pojul.com.fastim.View.widget.SmoothLinearLayoutManager;
import tl.pojul.com.fastim.util.ArrayUtil;
import tl.pojul.com.fastim.util.DateUtil;
import tl.pojul.com.fastim.util.SPUtil;

public class SingleChatRoomActivity extends ChatRoomActivity {

    @BindView(R.id.header)
    RelativeLayout header;
    @BindView(R.id.smart_refresh)
    SmartRefreshLayout smartRefresh;
    @BindView(R.id.input)
    EditText input;
    @BindView(R.id.send)
    Button send;
    @BindView(R.id.input_rl)
    RelativeLayout inputRl;
    @BindView(R.id.chatroom_name)
    TextView chatroomName;
    @BindView(R.id.chat_message_list)
    SwipeMenuRecyclerView chatMessageList;
    @BindView(R.id.add)
    ImageView add;
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
    private Friend friend;
    private int chatRoomType;
    private String chatRoomName;
    private String chatRoomUid;
    private User user;
    private MessageAdapter messageAdapter;
    private MoreMessageAdapter moreMessageAdapter;
    private SearchPicAdapter searchPicAdapter;

    private String searchPicWord = "";

    private final static int INIT_DATA = 1002;
    private final static int SCROLL_TO_TOP = 1003;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_room);
        ButterKnife.bind(this);

        mHandler.sendEmptyMessageDelayed(INIT_DATA, 300);

        init();

    }

    private void init() {
        chatRoomType = getIntent().getIntExtra("chat_room_type", 1);
        chatRoomName = getIntent().getStringExtra("chat_room_name");
        user = SPUtil.getInstance().getUser();
        try {
            friend = new Gson().fromJson(getIntent().getStringExtra("friend"), Friend.class);
        } catch (Exception e) {
        }
        if (chatRoomName == null || user == null || 1 != chatRoomType || friend == null) {
            finish();
            return;
        }
        chatRoomUid = UidUtil.getSingleChatRoomUid(friend.getUserName(), SPUtil.getInstance().getUser().getUserName());
        MyApplication.getApplication().exitChatRoomUid = chatRoomUid;
        chatroomName.setText(chatRoomName);
        MyApplication.getApplication().registerReceiveMessage(iReceiveMessage);
        MyApplication.getApplication().registerSendMessage(iSendMessage);
        MyApplication.getApplication().registerSendProgress(iSendProgress);
    }

    public void initData(){
        SmoothLinearLayoutManager layoutmanager = new SmoothLinearLayoutManager(this);
        chatMessageList.setLayoutManager(layoutmanager);
        messageAdapter = new MessageAdapter(this, ConversationAdapter.getChatRoomMessages(chatRoomUid), user, friend);
        ConversationAdapter.addAndRemoveUnReadMessage(chatRoomUid, friend.getUserName());
        int unReadNum = ConversationAdapter.getUnReadMessageNum(friend.getUserName(), user.getUserName());
        if(unReadNum > 0){
            getHistoryChat(unReadNum);
        }
        chatMessageList.setAdapter(messageAdapter);
        chatMessageList.scrollToPosition(messageAdapter.getItemCount() - 1);

        moreMessageAdapter = new MoreMessageAdapter(this, 1);
        GridLayoutManager layoutManager = new GridLayoutManager(this, 4);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        moreMessage.setLayoutManager(layoutManager);

        StaggeredGridLayoutManager staggeredGridLayoutManager = new StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL);
        searchPics.setLayoutManager(staggeredGridLayoutManager);
        searchPicAdapter = new SearchPicAdapter(this, new ArrayList<>(), 1);
        staggeredGridLayoutManager.setGapStrategy(StaggeredGridLayoutManager.GAP_HANDLING_NONE);
        searchPics.setAdapter(searchPicAdapter);
        searchPics.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                switch (newState){
                    case RecyclerView.SCROLL_STATE_IDLE:
                        Glide.with(SingleChatRoomActivity.this).resumeRequests();
                        int[] lastPositions = new int[staggeredGridLayoutManager.getSpanCount()];
                        staggeredGridLayoutManager.findLastVisibleItemPositions(lastPositions);
                        int lastPosition = ArrayUtil.findMax(lastPositions);
                        if(lastPosition >= (staggeredGridLayoutManager.getItemCount() - 1)){
                            if(!"".equals(searchPicWord)){
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

        smartRefresh.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                getHistoryChat(10);
            }
        });

    }

    public void getHistoryChat(int num){
        String lastSendTime = DateUtil.getFormatDate();
        if(messageAdapter.getChatMessages() != null && messageAdapter.getChatMessages().size() > 0){
            if(messageAdapter.getChatMessages().get(0) instanceof DateMessage && messageAdapter.getChatMessages().size() > 1){
                lastSendTime = messageAdapter.getChatMessages().get(1).getSendTime();
            }else if(!(messageAdapter.getChatMessages().get(0) instanceof DateMessage)){
                lastSendTime = messageAdapter.getChatMessages().get(0).getSendTime();
            }
        }
        HistoryChatReq historyChatReq = new HistoryChatReq(lastSendTime, chatRoomUid, num);
        new SocketRequest().request(MyApplication.ClientSocket, historyChatReq, new SocketRequest.IRequest() {
            @Override
            public void onError(String msg) {
                runOnUiThread(() -> {
                    smartRefresh.finishRefresh(false);
                    showShortToas(msg);
                });
            }
            @Override
            public void onFinished(ResponseMessage mResponse) {
                runOnUiThread(() -> {
                    ConversationAdapter.setUnReadDb(friend.getUserName(), user.getUserName(), 0);
                    smartRefresh.finishRefresh(true);
                    if(mResponse.getCode() == 200){
                        messageAdapter.addHistoryChat(((HistoryChatResp)mResponse).getMessages());
                    }else{
                        showShortToas(mResponse.getMessage());
                    }
                });
            }
        });
    }

    @OnClick({R.id.send, R.id.add, R.id.search_submit, R.id.search_engine})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.send:
                if (input.getText() == null || "".equals(input.getText().toString().trim())) {
                    return;
                }
                TextChatMessage textChatMessage = new ChatMessageFcctory().createTextMessage(input.getText().toString(),1);
                sendChatMessage(textChatMessage);
                input.setText("");
                break;
            case R.id.add:
                if (add.isSelected()) {
                    if(rlSearchPic.getVisibility() == View.VISIBLE){
                        hideSearchPic();
                        return;
                    }
                    hideMoreMessage();
                } else {
                    showMoreMessage();
                }
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
            case R.id.search_engine:
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        hideMoreMessage();
        moreMessageAdapter.onActivityResult(requestCode, resultCode, data);
    }

    public void showMoreMessage() {
        moreMessage.setAdapter(moreMessageAdapter);
        moreMessage.setVisibility(View.VISIBLE);
        Animator anim = AnimatorInflater.loadAnimator(SingleChatRoomActivity.this, R.animator.add_rotate);
        anim.setStartDelay(180);
        anim.setTarget(add);
        anim.start();
        add.setSelected(true);
    }

    public void hideMoreMessage() {
        moreMessage.setVisibility(View.GONE);
        Animator anim = AnimatorInflater.loadAnimator(SingleChatRoomActivity.this, R.animator.close_rotate);
        anim.setStartDelay(180);
        anim.setTarget(add);
        anim.start();
        add.setSelected(false);
    }

    @Override
    public void showSearchPic() {
        rlSearchPic.setVisibility(View.VISIBLE);
    }

    public void hideSearchPic() {
        rlSearchPic.setVisibility(View.GONE);
    }

    public void scrollToTop(){
        chatMessageList.smoothScrollToPosition(0);
    }

    @Override
    public void sendChatMessage(ChatMessage chatMessage) {
        if (!MyApplication.getApplication().isConnected()) {
            showShortToas("与服务器已断开连接");
            return;
        }
        chatMessage.setFrom(user.getUserName());
        chatMessage.setTo(friend.getUserName());
        MyApplication.ClientSocket.sendData(chatMessage);
        messageAdapter.addMessage(chatMessage);
        chatMessageList.scrollToPosition(messageAdapter.getItemCount() - 1);
    }

    private MyApplication.IReceiveMessage iReceiveMessage = new MyApplication.IReceiveMessage() {
        @Override
        public void receiveMessage(BaseMessage message) {
            if (friend.getUserName().equals(message.getFrom()) && message instanceof ChatMessage) {
                messageAdapter.receiveMessage((ChatMessage) message);
                chatMessageList.scrollToPosition(messageAdapter.getItemCount() - 1);
            }
            showLongToas("iReceiveMessage--->" + message.getFrom());
        }
    };

    private MyApplication.ISendMessage iSendMessage = new MyApplication.ISendMessage() {

        @Override
        public void onSendFinish(BaseMessage message) {
            messageAdapter.onSendFinish(message);
        }

        @Override
        public void onSendFail(BaseMessage message) {
            messageAdapter.onSendFail(message);
        }
    };

    private MyApplication.ISendProgress iSendProgress = new MyApplication.ISendProgress() {
        @Override
        public void progress(BaseMessage message, int progress) {
            if(progress > 100){
                progress = 100;
            }
            messageAdapter.updateSendProgress(message, progress);
        }
        @Override
        public void finish(BaseMessage message) {}
    };

    private MyHandler mHandler = new MyHandler(this);
    static class MyHandler extends Handler{
        //注意下面的“”类是MyHandler类所在的外部类，即所在的activity或者fragment
        WeakReference<SingleChatRoomActivity> activity;
        MyHandler(SingleChatRoomActivity activity) {
            this.activity = new WeakReference<>(activity);
        }
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if(activity.get() == null){
                return;
            }
            switch (msg.what) {
                case INIT_DATA:
                    activity.get().initData();
                    break;
                case SCROLL_TO_TOP:
                    activity.get().scrollToTop();
                    break;
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        MyApplication.getApplication().unRegisterReceiveMessage(iReceiveMessage);
        MyApplication.getApplication().unRegisterSendMessage(iSendMessage);
        MyApplication.getApplication().unRegisterSendProgress(iSendProgress);
        mHandler.removeCallbacksAndMessages(null);
        AudioManager.getInstance().stopPlaySound();
        VideoManager.getInstance().stopPlay();
        MyApplication.getApplication().exitChatRoomUid = "";
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_UP) {
            if(rlSearchPic.getVisibility() == View.VISIBLE){
                hideSearchPic();
                return true;
            }else if (moreMessage.getVisibility() == View.VISIBLE) {
                hideMoreMessage();
                return true;
            }
        }
        return super.onKeyUp(keyCode, event);
    }

}
