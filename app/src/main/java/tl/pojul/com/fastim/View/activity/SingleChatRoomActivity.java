package tl.pojul.com.fastim.View.activity;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.pojul.fastIM.entity.Friend;
import com.pojul.fastIM.entity.User;
import com.pojul.fastIM.message.chat.ChatMessage;
import com.pojul.fastIM.message.chat.PicMessage;
import com.pojul.fastIM.message.chat.TextChatMessage;
import com.pojul.objectsocket.message.BaseMessage;
import com.pojul.objectsocket.message.StringFile;
import com.pojul.objectsocket.socket.UidUtil;
import com.pojul.objectsocket.utils.StorageType;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuRecyclerView;

import java.io.File;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import tl.pojul.com.fastim.MyApplication;
import tl.pojul.com.fastim.R;
import tl.pojul.com.fastim.View.Adapter.ConversationAdapter;
import tl.pojul.com.fastim.View.Adapter.MessageAdapter;
import tl.pojul.com.fastim.View.Adapter.MoreMessageAdapter;
import tl.pojul.com.fastim.dao.ConversationDao;
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
    @BindView(R.id.add)
    ImageView add;
    @BindView(R.id.input1_rl)
    RelativeLayout input1Rl;
    @BindView(R.id.more_message)
    RecyclerView moreMessage;
    private Friend friend;
    private int chatRoomType;
    private String chatRoomName;
    private String chatRoomUid;
    private User user;
    private MessageAdapter messageAdapter;
    private MoreMessageAdapter moreMessageAdapter;
    private int REQUEST_CODE_IMAGE = 1;

    private int REMOVE_UNREAD_MESSAGE = 1001;

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
        chatRoomUid = UidUtil.getSingleChatRoomUid(friend.getUserName(), SPUtil.getInstance().getUser().getUserName());
        chatroomName.setText(chatRoomName);
        MyApplication.getApplication().registerReceiveMessage(iReceiveMessage);
        MyApplication.getApplication().registerSendMessage(iSendMessage);

        LinearLayoutManager layoutmanager = new LinearLayoutManager(this);
        chatMessageList.setLayoutManager(layoutmanager);
        messageAdapter = new MessageAdapter(this, ConversationAdapter.getChatRoomMessages(chatRoomUid), user, friend);
        ConversationAdapter.addAndRemoveUnReadMessage(chatRoomUid, friend.getUserName());
        chatMessageList.setAdapter(messageAdapter);
        chatMessageList.scrollToPosition(messageAdapter.getItemCount() - 1);

        moreMessageAdapter = new MoreMessageAdapter(this);
        moreMessageAdapter.setOnItemClickListener(moreMessageItemClick);
        GridLayoutManager layoutManager = new GridLayoutManager(this,4);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        moreMessage.setLayoutManager(layoutManager);
    }

    @OnClick({R.id.send, R.id.add})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.send:
                if (null == input.getText().toString() || "".equals(input.getText().toString())) {
                    return;
                }
                sendTextChatMessage();
                break;
            case R.id.add:
                if(add.isSelected()){
                    hideMoreMessage();
                }else{
                    showMoreMessage();
                }
                break;
        }
    }

    private MoreMessageAdapter.OnItemClickListener moreMessageItemClick = new MoreMessageAdapter.OnItemClickListener(){
        @Override
        public void onItemClick(int position) {
            switch (position) {
                case 0:
                    startPicPicker();
                    break;
                case 1:
                    break;
            }
        }
    };

    private void startPicPicker() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, REQUEST_CODE_IMAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_IMAGE && resultCode == RESULT_OK) {
            Uri uri = data.getData();
            Cursor cursor = getContentResolver().query(uri, null, null, null,null);
            if (cursor != null && cursor.moveToFirst()) {
                String path = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA));
                sendPicMessage(path);
            }
        }
    }

    private void showMoreMessage() {
        moreMessage.setAdapter(moreMessageAdapter);
        moreMessage.setVisibility(View.VISIBLE);
        Animator anim = AnimatorInflater.loadAnimator(SingleChatRoomActivity.this, R.animator.add_rotate);
        anim.setStartDelay(180);
        anim.setTarget(add);
        anim.start();
        add.setSelected(true);
    }

    private void hideMoreMessage() {
        moreMessage.setVisibility(View.GONE);
        Animator anim = AnimatorInflater.loadAnimator(SingleChatRoomActivity.this, R.animator.close_rotate);
        anim.setStartDelay(180);
        anim.setTarget(add);
        anim.start();
        add.setSelected(false);
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
        messageAdapter.addMessage(message);
        input.setText("");
        chatMessageList.scrollToPosition(messageAdapter.getItemCount() - 1);
    }

    private void sendPicMessage(String path){
        File file = new File(path);
        if(!file.exists()){
            showShortToas("图片不存在");
            return;
        }
        PicMessage picMessage = new PicMessage();
        picMessage.setFrom(user.getUserName());
        picMessage.setTo(friend.getUserName());
        picMessage.setChatType(1);
        StringFile stringFile = new StringFile(StorageType.LOCAL);
        stringFile.setFilePath(path);
        stringFile.setFileType("img");
        int index = path.lastIndexOf("/");
        if(index == -1){
            index = 0;
        }
        stringFile.setFileName(path.substring((index + 1) , path.length() ));
        picMessage.setPic(stringFile);
        MyApplication.ClientSocket.sendData(picMessage);
        messageAdapter.addMessage(picMessage);
        chatMessageList.scrollToPosition(messageAdapter.getItemCount() - 1);
        hideMoreMessage();
    }

    private MyApplication.IReceiveMessage iReceiveMessage = new MyApplication.IReceiveMessage() {
        @Override
        public void receiveMessage(BaseMessage message) {
            if (friend.getUserName().equals(message.getFrom()) && message instanceof ChatMessage) {
                messageAdapter.receiveMessage((ChatMessage) message);
                chatMessageList.scrollToPosition(messageAdapter.getItemCount() - 1);

                Message msg = new Message();
                msg.what = REMOVE_UNREAD_MESSAGE;
                msg.obj = message;
                mHandler.sendMessageDelayed(msg, 100);
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

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case 1001:
                    ConversationAdapter.removeUnReadMessage((ChatMessage)msg.obj);
                    break;
            }
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        MyApplication.getApplication().unRegisterReceiveMessage(iReceiveMessage);
        MyApplication.getApplication().unRegisterSendMessage(iSendMessage);
        mHandler.removeCallbacksAndMessages(null);
    }
}
