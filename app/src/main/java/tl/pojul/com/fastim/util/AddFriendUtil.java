package tl.pojul.com.fastim.util;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.google.gson.Gson;
import com.pojul.fastIM.entity.AddFriend;
import com.pojul.fastIM.entity.Conversation;
import com.pojul.fastIM.entity.Friend;
import com.pojul.fastIM.entity.User;
import com.pojul.fastIM.message.other.NotifyAcceptFriend;
import com.pojul.fastIM.message.other.NotifyFriendReq;

import tl.pojul.com.fastim.MyApplication;
import tl.pojul.com.fastim.View.activity.AddFriendActivity;
import tl.pojul.com.fastim.View.activity.MainActivity;
import tl.pojul.com.fastim.View.activity.MyPageActivity;
import tl.pojul.com.fastim.View.activity.SingleChatRoomActivity;
import tl.pojul.com.fastim.View.fragment.ChatFragment;
import tl.pojul.com.fastim.converter.UserConverter;
import tl.pojul.com.fastim.dao.ConversationDao;

public class AddFriendUtil {

    public void onNotifyFriendReq(NotifyFriendReq message, Context context) {
        AddFriend addFriend = message.getAddFriend();
        User user = SPUtil.getInstance().getUser();
        if(addFriend == null || user == null || user.getUserName().equals(addFriend.getReqUserName())){
            return;
        }
        String title;
        if(addFriend.getReqType() == 1){
            title = "好友请求消息";
        }else if(addFriend.getReqType() == 2){
            title = "打招呼消息";
        }else{
            return;
        }
        Intent intent = new Intent(context, AddFriendActivity.class);
        NotifyUtil.notify(title, addFriend.getReqUserInfo().getNickName(), addFriend.getReqText(), addFriend.getReqUserInfo().getPhoto().getFilePath(), intent, context);
    }

    public void onNotifyAcceptFriend(NotifyAcceptFriend message, Context context) {
        User user = SPUtil.getInstance().getUser();
        Friend friend = new UserConverter().converterToFriend(message.getReqedUserInfo());
        if(user == null || friend == null || user.getUserName().equals(friend.getUserName())){
            return;
        }
        String title;
        if(message.getType() == 2){
            ConversationDao conversationDao = new ConversationDao();
            if(!conversationDao.isSingleConversationExit(friend.getUserName(), user.getUserName())){
                Conversation conversation = new Conversation();
                conversation.setConversationName(friend.getNickName());
                conversation.setConversationFrom(friend.getUserName());
                conversation.setConversationPhoto(friend.getPhoto().getFilePath());
                conversation.setConversationLastChat(message.getReqText());
                conversation.setConversationLastChattime(DateUtil.getFormatDate());
                conversation.setConversationOwner(user.getUserName());
                conversation.setUnreadMessage(0);
                conversation.setConversionUid("");
                conversation.setConversationType(1);
                conversationDao.insertConversation(conversation);
            }
        }else if(message.getType() == 1){
            MainActivity mainActivity = MyApplication.getApplication().getMainActivity();
            ChatFragment chatFragment = null;
            if (mainActivity != null) {
                chatFragment = mainActivity.chatFragment;
            }
            if (chatFragment != null && chatFragment.friendsFragment != null && chatFragment.friendsFragment.friendsAdapter != null) {
                chatFragment.friendsFragment.getFriends(false);
            }
        }
        if(message.getType() == 1){
            title = "好友请求消息";
        }else if(message.getType() == 2){
            title = "打招呼消息";
        }else{
            return;
        }
        Intent intent = new Intent(context, SingleChatRoomActivity.class);
        Bundle bundle=new Bundle();
        bundle.putInt("chat_room_type", 1);
        bundle.putString("chat_room_name", friend.getNickName());
        bundle.putString("friend", new Gson().toJson(friend));
        intent.putExtras(bundle);
        NotifyUtil.notify(title, (friend.getNickName()  + " 同意了你的请求"), message.getReqText(), friend.getPhoto().getFilePath(), intent, context);
    }
}
