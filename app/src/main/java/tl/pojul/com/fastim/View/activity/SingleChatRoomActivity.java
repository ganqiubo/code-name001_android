package tl.pojul.com.fastim.View.activity;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.pojul.fastIM.entity.Friend;
import com.pojul.fastIM.entity.User;
import com.pojul.fastIM.message.chat.ChatMessage;
import com.pojul.fastIM.message.chat.TextChatMessage;
import com.pojul.objectsocket.message.BaseMessage;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuRecyclerView;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import tl.pojul.com.fastim.MyApplication;
import tl.pojul.com.fastim.R;
import tl.pojul.com.fastim.View.Adapter.MessageAdapter;
import tl.pojul.com.fastim.util.SPUtil;

public class SingleChatRoomActivity extends BaseActivity {

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
    private Friend friend;
    private int chatRoomType;
    private String chatRoomName;
    private User user;
    private MessageAdapter messageAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_room);
        ButterKnife.bind(this);

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
        chatroomName.setText(chatRoomName);
        MyApplication.getApplication().registerReceiveMessage(iReceiveMessage);
        MyApplication.getApplication().registerSendMessage(iSendMessage);

        LinearLayoutManager layoutmanager = new LinearLayoutManager(this);
        chatMessageList.setLayoutManager(layoutmanager);
        messageAdapter = new MessageAdapter(this, new ArrayList<ChatMessage>(), user, friend);
        chatMessageList.setAdapter(messageAdapter);
    }

    @OnClick({R.id.send})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.send:
                if (null == input.getText().toString() || "".equals(input.getText().toString())) {
                    return;
                }
                sendTextChatMessage();
                break;
        }
    }

    private void sendTextChatMessage() {
        if (!MyApplication.getApplication().isConnected()) {
            return;
        }
        TextChatMessage message = new TextChatMessage();
        message.setFrom(user.getUserName());
        message.setTo(friend.getUserName());
        message.setChatType(1);
        message.setText(input.getText().toString());
        MyApplication.ClientSocket.sendData(message);
        messageAdapter.getChatMessages().add(message);
        messageAdapter.notifyDataSetChanged();
        input.setText("");
    }

    private MyApplication.IReceiveMessage iReceiveMessage = new MyApplication.IReceiveMessage() {
        @Override
        public void receiveMessage(BaseMessage message) {
            if(friend.getUserName().equals(message.getFrom()) && message instanceof ChatMessage){
                messageAdapter.receiveMessage((ChatMessage)message);
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        MyApplication.getApplication().unRegisterReceiveMessage(iReceiveMessage);
        MyApplication.getApplication().unRegisterSendMessage(iSendMessage);
    }
}
