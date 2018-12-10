package tl.pojul.com.fastim.View.Adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.pojul.fastIM.entity.Conversation;
import com.pojul.fastIM.entity.Friend;
import com.pojul.fastIM.message.chat.ChatMessage;
import com.pojul.fastIM.message.chat.CommunityMessage;
import com.pojul.fastIM.message.other.NotifyChatClosed;
import com.pojul.fastIM.message.other.NotifyReplyMess;
import com.pojul.fastIM.message.request.ChatLegalReq;
import com.pojul.fastIM.message.request.CloseGreetChatReq;
import com.pojul.fastIM.message.request.GetConversionInfoRequest;
import com.pojul.fastIM.message.response.ChatLegalResp;
import com.pojul.fastIM.message.response.GetConversionInfoResponse;
import com.pojul.objectsocket.message.BaseMessage;
import com.pojul.objectsocket.message.RequestMessage;
import com.pojul.objectsocket.message.ResponseMessage;
import com.pojul.objectsocket.socket.SocketRequest;
import com.pojul.objectsocket.utils.UidUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import tl.pojul.com.fastim.Media.VibrateManager;
import tl.pojul.com.fastim.MyApplication;
import tl.pojul.com.fastim.R;
import tl.pojul.com.fastim.View.activity.MainActivity;
import tl.pojul.com.fastim.View.activity.SingleChatRoomActivity;
import tl.pojul.com.fastim.View.activity.TagReplyActivity;
import tl.pojul.com.fastim.View.fragment.FriendsFragment;
import tl.pojul.com.fastim.View.widget.PolygonImage.view.PolygonImageView;
import tl.pojul.com.fastim.converter.UserConverter;
import tl.pojul.com.fastim.dao.ConversationDao;
import tl.pojul.com.fastim.socket.Converter.HistoryChatConverter;
import tl.pojul.com.fastim.util.ConversationUtil;
import tl.pojul.com.fastim.util.DateUtil;
import tl.pojul.com.fastim.util.DialogUtil;
import tl.pojul.com.fastim.util.NotifyUtil;
import tl.pojul.com.fastim.util.SPUtil;

public class ConversationAdapter extends RecyclerView.Adapter<ConversationAdapter.MyViewHolder> {

    private Context mContext;
    private List<Conversation> mList;
    public int totalUnreadUum = 0;
    public HashMap<String, Integer> each = new HashMap<>();
    private OnItemClickListener mOnItemClickListener;
    public static HashMap<String, List<ChatMessage>> unReadMessage = new HashMap<>();
    public static HashMap<String, List<ChatMessage>> chatRoomMessages = new HashMap<>();

    public ConversationAdapter(Context mContext, List<Conversation> mList) {
        this.mContext = mContext;
        this.mList = mList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_conversations_list, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Conversation conversation = mList.get(position);
        holder.itemRl.setOnClickListener(v -> {
            if (conversation.getConversationType() == 1) {
                startChatRoom(conversation);
            } else if (conversation.getConversationType() == 3) {
                startTagReplyRoom(conversation);
            }
        });

        if (conversation.getConversationPhoto() != null && !"".equals(conversation.getConversationPhoto())
                && !"null".equals(conversation.getConversationPhoto())) {
            Glide.with(mContext).load(conversation.getConversationPhoto()).into(holder.conversationPhoto);
        } else {
            Glide.with(mContext).load(R.drawable.photo_default).into(holder.conversationPhoto);
        }
        holder.conversationName.setText(conversation.getConversationName());
        holder.conversationLastChat.setText(conversation.getConversationLastChat());
        holder.conversationLastChattime.setText(DateUtil.getConversationDate(conversation.getConversationLastChattime()));

        if(conversation.getConversationType() == 3){
            if(conversation.getUnreadMessage() > 0 || conversation.isHasUnreadMessage()){
                holder.hasUnreadMessage.setVisibility(View.VISIBLE);
                holder.unreadMessage.setVisibility(View.GONE);
            }else{
                holder.unreadMessage.setVisibility(View.GONE);
                holder.hasUnreadMessage.setVisibility(View.GONE);
            }
        }else{
            if (conversation.getUnreadMessage() > 0) {
                holder.unreadMessage.setVisibility(View.VISIBLE);
                holder.unreadMessage.setText(("" + conversation.getUnreadMessage()));
                holder.hasUnreadMessage.setVisibility(View.GONE);
            } else {
                holder.unreadMessage.setVisibility(View.GONE);
                holder.hasUnreadMessage.setVisibility(View.GONE);
            }

        }


    }

    @Override
    public int getItemCount() {
        return mList != null ? mList.size() : 0;
    }

    static class MyViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.conversation_photo)
        PolygonImageView conversationPhoto;
        @BindView(R.id.conversation_name)
        TextView conversationName;
        @BindView(R.id.conversation_last_chat)
        TextView conversationLastChat;
        @BindView(R.id.conversation_last_chattime)
        TextView conversationLastChattime;
        @BindView(R.id.unread_message)
        TextView unreadMessage;
        @BindView(R.id.item_rl)
        RelativeLayout itemRl;
        @BindView(R.id.has_unread_message)
        TextView hasUnreadMessage;

        public MyViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public void receiveMessage(BaseMessage message) {
        if (message instanceof CommunityMessage || message instanceof NotifyChatClosed) {
            return;
        }
        if (message instanceof NotifyReplyMess) {
            onRecNotifyReplyMess((NotifyReplyMess) message);
            return;
        }
        if (message instanceof ChatMessage && ((ChatMessage) message).getChatType() == 4) {
            onRecPrivTagMess((ChatMessage) message);
            return;
        }
        synchronized (mList) {
            if (mList == null) {
                return;
            }
            if (message instanceof ChatMessage) {
                String chatRoomUid = UidUtil.getChatRoomUid((ChatMessage) message);
                if (MyApplication.getApplication().exitChatRoomUid.equals(chatRoomUid)) {
                    return;
                }
            }
            boolean containConversation = false;
            for (int i = 0; i < mList.size(); i++) {
                Conversation conversation = mList.get(i);
                if (conversation.getConversationFrom().equals(message.getFrom())
                        && conversation.getConversationOwner().equals(SPUtil.getInstance().getUser().getUserName())
                        && conversation.getConversationType() == 1) {
                    int unReadUum = new ConversationDao().getUnreadNum(message.getFrom(), SPUtil.getInstance().getUser().getUserName());
                    conversation.setUnreadMessage(unReadUum + 1);
                    conversation.setConversationLastChat(ConversationUtil.getNoteText(message));
                    conversation.setConversationLastChattime(message.getSendTime());
                    new ConversationDao().updateConversationChat(conversation);
                    this.notifyDataSetChanged();
                    notyfyMess(conversation);
                    containConversation = true;
                    notifyUnReadNum();
                    break;
                }
            }

            if (!containConversation) {
                if (message instanceof ChatMessage) {
                    requestConversationInfo((ChatMessage) message);
                } else {
                }
            }
        }

        if (message instanceof ChatMessage) {
            putUnReadMessage((ChatMessage) message);
        }
    }

    private void onRecPrivTagMess(ChatMessage chatMessage) {
        Conversation conversation = getPrivTagMessCon(chatMessage.getMessageUid());
        if (conversation != null) {
            ConversationUtil.updatePriTagMess(chatMessage, new ConversationDao(), conversation);
            notifyDataSetChanged();
        } else {

        }
    }

    private void onRecNotifyReplyMess(NotifyReplyMess message) {
        Conversation conversation = getReplyCon(message.getReplyTagMessUid());
        if (conversation != null) {
            ConversationUtil.updateNotifyReply(message, new ConversationDao(), conversation);
            notifyDataSetChanged();
        } else {
            conversation = ConversationUtil.insertNotifyReply(message, new ConversationDao());
            synchronized (mList) {
                mList.add(conversation);
                notifyDataSetChanged();
                //notifyItemInserted((mList.size() - 1));
            }
        }
        notyfyMess(conversation);
    }

    private void notyfyMess(Conversation conversation) {
        if (MyApplication.startActivityCount > 0) {
            VibrateManager.getInstance().vibrate(100);
        } else {
            NotifyUtil.notifyChatMess(conversation, MyApplication.getApplication().getApplicationContext());
        }
        /*if(SPUtil.getInstance().getInt(SPUtil.VIBRATE_TAGMESS_REPLY, 1) == 1){
            VibrateManager.getInstance().vibrate(100);
        }
        if(SPUtil.getInstance().getInt(SPUtil.NOTIFY_TAGMESS_REPLY, 1) == 1){
            NotifyUtil.notifyChatMess(conversation, MyApplication.getApplication().getApplicationContext());
        }*/
    }

    public Conversation getReplyCon(String uid) {
        for (int i = 0; i < mList.size(); i++) {
            Conversation conversation = mList.get(i);
            if (conversation.getConversionUid() != null && conversation.getConversionUid().equals(uid) && conversation.getConversationType() == 3) {
                return conversation;
            }
        }
        return null;
    }

    public Conversation getPrivTagMessCon(String uid) {
        for (int i = 0; i < mList.size(); i++) {
            Conversation conversation = mList.get(i);
            if (conversation.getConversionUid() != null && conversation.getConversionUid().equals(uid) && conversation.getConversationType() == 4) {
                return conversation;
            }
        }
        return null;
    }


    private void requestConversationInfo(ChatMessage message) {
        Conversation conversation = new Conversation();
        conversation.setUnreadMessage(1);
        conversation.setConversationFrom(message.getFrom());
        conversation.setConversationLastChat(ConversationUtil.getNoteText(message));
        conversation.setConversationLastChattime(message.getSendTime());
        conversation.setConversationOwner(SPUtil.getInstance().getUser().getUserName());
        conversation.setConversationType(message.getChatType());

        GetConversionInfoRequest request = new GetConversionInfoRequest();
        request.setRequestUrl("GetConversionInfo");
        request.setChatRoomType(message.getChatType());
        request.setConversionFrom(message.getFrom());
        request.setOwner(SPUtil.getInstance().getUser().getUserName());
        new SocketRequest().request(MyApplication.ClientSocket, request, new SocketRequest.IRequest() {
            @Override
            public void onError(String msg) {
                updatePhotoName(conversation, null, "?不存在?");
            }

            @Override
            public void onFinished(ResponseMessage mResponse) {
                new Handler(Looper.getMainLooper()).post(() -> {
                    GetConversionInfoResponse response = (GetConversionInfoResponse) mResponse;
                    if (response.getCode() == 200) {
                        updatePhotoName(conversation,
                                response.getConversation().getConversationPhoto(),
                                response.getConversation().getConversationName());
                    } else {
                        updatePhotoName(conversation, null, "?不存在?");
                    }
                });
            }
        });
    }

    public void updatePhotoName(Conversation conversation, String photo, String name) {
        conversation.setConversationPhoto(photo);
        conversation.setConversationName(name);
        new ConversationDao().insertConversation(conversation);
        mList.add(conversation);
        ConversationAdapter.this.notifyDataSetChanged();
        notyfyMess(conversation);
        notifyUnReadNum();
    }

    public void notifyUnReadNum() {
        if (mContext != null && mList != null) {
            totalUnreadUum = 0;
            for (int i = 0; i < mList.size(); i++) {
                if (mList.get(i) == null) {
                    continue;
                }
                totalUnreadUum = totalUnreadUum + mList.get(i).getUnreadMessage();
                each.put(mList.get(i).getConversationFrom(), mList.get(i).getUnreadMessage());
            }
            MainActivity mainActivity = ((MainActivity) mContext);
            //FriendsFragment friendsFragment = null;
            /*if(mainActivity.chatFragment != null){
                friendsFragment = mainActivity.chatFragment.friendsFragment;
            }
            if (friendsFragment != null && friendsFragment.friendsAdapter != null) {
                friendsFragment.friendsAdapter.unreadUnmChanged(each);
            }*/
            if (mainActivity != null) {
                mainActivity.unreadUnmChanged(totalUnreadUum);
            }
        }
    }

    public static int getUnReadMessageNum(String from, String to) {
        return new ConversationDao().getUnreadNum(from, to);
    }

    public static void putUnReadMessage(ChatMessage message) {
        synchronized (unReadMessage) {
            String chatRoomUid = UidUtil.getChatRoomUid(message);
            if (!unReadMessage.containsKey(chatRoomUid)) {
                unReadMessage.put(chatRoomUid, new ArrayList<ChatMessage>() {{
                    add(message);
                }});
            } else {
                unReadMessage.get(chatRoomUid).add(message);
            }
        }
    }

    public static void removeUnReadMessage(ChatMessage message) {
        String chatRoomUid = UidUtil.getChatRoomUid(message);
        synchronized (unReadMessage) {
            if (!unReadMessage.containsKey(chatRoomUid)) {
                return;
            }
            unReadMessage.get(chatRoomUid).remove(message);
            ConversationDao conversationDao = new ConversationDao();
            int unRead = conversationDao.getUnreadNum(message.getFrom(), message.getTo());
            if (unRead > 0) {
                conversationDao.updateUnreadNum(message.getFrom(), SPUtil.getInstance().getUser().getUserName(), (unRead - 1));
            }
        }
    }

    public static void clearUnReadMessage(String chatRoomUid, String from, int type) {
        synchronized (unReadMessage) {
            if (!unReadMessage.containsKey(chatRoomUid)) {
                return;
            }
            unReadMessage.remove(chatRoomUid);
            setUnReadDb(from, SPUtil.getInstance().getUser().getUserName(), 0);
        }
    }

    public static void setUnReadDb(String from, String to, int num) {
        new ConversationDao().updateUnreadNum(from, to, num);
    }

    public static List getChatRoomMessages(String chatRoomUid) {
        synchronized (chatRoomMessages) {
            if (!chatRoomMessages.containsKey(chatRoomUid)) {
                chatRoomMessages.put(chatRoomUid, new ArrayList<>());
            }
            return chatRoomMessages.get(chatRoomUid);
        }
    }

    public static void addAndRemoveUnReadMessage(String chatRoomUid, String from) {
        synchronized (chatRoomMessages) {
            if (!chatRoomMessages.containsKey(chatRoomUid)) {
                chatRoomMessages.put(chatRoomUid, new ArrayList<>());
            }
            /**
             * 添加时间检测
             * */
            if (unReadMessage.get(chatRoomUid) != null) {
                List<ChatMessage> chatMessages = HistoryChatConverter.insertDate(unReadMessage.get(chatRoomUid));
                List<ChatMessage> chatRoomUidMessages = chatRoomMessages.get(chatRoomUid);
                if (chatMessages.size() > 1 && chatRoomUidMessages.size() > 0 && !DateUtil.isDiffDay(chatMessages.get(1).getSendTime(),
                        chatRoomUidMessages.get(chatRoomUidMessages.size() - 1).getSendTime())) {
                    chatMessages.remove(0);
                }
                chatRoomMessages.get(chatRoomUid).addAll(chatMessages);
            }
        }
        clearUnReadMessage(chatRoomUid, from, 1);
    }

    private void startChatRoom(Conversation conversation) {
        ChatLegalReq req = new ChatLegalReq();
        req.setUserNameOwn(conversation.getConversationOwner());
        req.setUserNameFriend(conversation.getConversationFrom());
        new SocketRequest().request(MyApplication.ClientSocket, req, new SocketRequest.IRequest() {
            @Override
            public void onError(String msg) {
                new Handler(Looper.getMainLooper()).post(()->{
                    Toast.makeText(mContext, msg, Toast.LENGTH_SHORT).show();
                });
            }

            @Override
            public void onFinished(ResponseMessage mResponse) {
                new Handler(Looper.getMainLooper()).post(()->{
                    if(mResponse.getCode() == 200){
                        ChatLegalResp resp = (ChatLegalResp) mResponse;
                        if(resp.getLegal() == 3){
                            Toast.makeText(mContext, "该会话已失效", Toast.LENGTH_SHORT).show();
                        }else{
                            if(resp.getUser() == null){
                                Toast.makeText(mContext, "数据错误", Toast.LENGTH_SHORT).show();
                            }else{
                                Friend friend = new UserConverter().converterToFriend(resp.getUser());
                                Intent intent = new Intent(mContext, SingleChatRoomActivity.class);
                                Bundle bundle = new Bundle();
                                bundle.putInt("chat_room_type", 1);
                                bundle.putString("chat_room_name", conversation.getConversationName());
                                bundle.putString("friend", new Gson().toJson(friend));
                                intent.putExtras(bundle);
                                mContext.startActivity(intent);
                            }
                        }
                    }else{
                        Toast.makeText(mContext, mResponse.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });


        /*FriendsFragment friendsFragment = null;
        if (((MainActivity) mContext).chatFragment != null) {
            friendsFragment = ((MainActivity) mContext).chatFragment.friendsFragment;
        }*/
        /*if (friendsFragment != null && friendsFragment.friendsAdapter != null) {
            Friend friend = null;
            friend = friendsFragment.friendsAdapter.getFriendByUserName(conversation.getConversationFrom());
            if (friend != null) {
                bundle.putString("friend", new Gson().toJson(friend));
                intent.putExtras(bundle);
                mContext.startActivity(intent);
            }
        }*/
    }

    public void deleteItem(int position){
        Conversation conversation = mList.get(position);
        if(conversation.getConversationType() == 1){
            ChatLegalReq req = new ChatLegalReq();
            req.setUserNameOwn(conversation.getConversationOwner());
            req.setUserNameFriend(conversation.getConversationFrom());
            new SocketRequest().request(MyApplication.ClientSocket, req, new SocketRequest.IRequest() {
                @Override
                public void onError(String msg) {
                    new Handler(Looper.getMainLooper()).post(()->{
                        Toast.makeText(mContext, msg, Toast.LENGTH_SHORT).show();
                    });
                }

                @Override
                public void onFinished(ResponseMessage mResponse) {
                    new Handler(Looper.getMainLooper()).post(()->{
                        if(mResponse.getCode() == 200){
                            ChatLegalResp resp = (ChatLegalResp) mResponse;
                            if(resp.getLegal() == 2){
                                DialogUtil.getInstance().showPromptDialog(mContext, "删除会话", "确定关闭该会话，关闭后将不能接收到对方消息");
                                DialogUtil.getInstance().setDialogClick(str -> {
                                    if("确定".equals(str)){
                                        closeGreetChat(conversation.getConversationOwner(), conversation.getConversationFrom(), position);
                                    }
                                });
                            }else{
                                deleteLocalData(position);
                            }
                        }else{
                            Toast.makeText(mContext, mResponse.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            });
        }else{
            deleteLocalData(position);
        }
    }


    private void closeGreetChat(String conversationOwner, String conversationFrom, int position) {
        CloseGreetChatReq req = new CloseGreetChatReq();
        req.setFromUserName(conversationOwner);
        req.setToUserName(conversationFrom);
        DialogUtil.getInstance().showLoadingSimple(mContext, ((Activity)mContext).getWindow().getDecorView());
        new SocketRequest().request(MyApplication.ClientSocket, req, new SocketRequest.IRequest() {
            @Override
            public void onError(String msg) {
                new Handler(Looper.getMainLooper()).post(()->{
                    Toast.makeText(mContext, msg, Toast.LENGTH_SHORT).show();
                    DialogUtil.getInstance().dimissLoadingDialog();
                });
            }

            @Override
            public void onFinished(ResponseMessage mResponse) {
                new Handler(Looper.getMainLooper()).post(()->{
                    DialogUtil.getInstance().dimissLoadingDialog();
                    if(mResponse.getCode() == 200){
                        deleteLocalData(position);
                    }else{
                        Toast.makeText(mContext, mResponse.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    public void deleteLocalData(int position){
        synchronized (mList){
            Conversation conversation = mList.get(position);
            if(conversation.getConversationType() == 1){
                new ConversationDao().deleteSingleChat(conversation.getConversationOwner(), conversation.getConversationFrom());
            }else if(conversation.getConversationType() == 3){
                new ConversationDao().deleteTagReply(conversation.getConversionUid(), conversation.getConversationOwner());
            }
            mList.remove(position);
            notifyDataSetChanged();
            if(conversation.getUnreadMessage() > 0){
                notifyUnReadNum();
            }
        }
    }

    private void startTagReplyRoom(Conversation conversation) {
        Intent intent = new Intent(mContext, TagReplyActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("tagMessUid", conversation.getConversionUid());
        intent.putExtras(bundle);
        mContext.startActivity(intent);
    }

    public interface OnItemClickListener {
        void onClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.mOnItemClickListener = onItemClickListener;
    }

}
