package tl.pojul.com.fastim.View.Adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.pojul.fastIM.entity.AddFriend;
import com.pojul.fastIM.entity.Conversation;
import com.pojul.fastIM.entity.Friend;
import com.pojul.fastIM.entity.User;
import com.pojul.fastIM.message.request.AcceptFriendReq;
import com.pojul.objectsocket.message.ResponseMessage;
import com.pojul.objectsocket.socket.SocketRequest;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import tl.pojul.com.fastim.MyApplication;
import tl.pojul.com.fastim.R;
import tl.pojul.com.fastim.View.activity.BaseActivity;
import tl.pojul.com.fastim.View.activity.MainActivity;
import tl.pojul.com.fastim.View.activity.MyPageActivity;
import tl.pojul.com.fastim.View.activity.SingleChatRoomActivity;
import tl.pojul.com.fastim.View.fragment.ChatFragment;
import tl.pojul.com.fastim.View.widget.PolygonImage.view.PolygonImageView;
import tl.pojul.com.fastim.converter.UserConverter;
import tl.pojul.com.fastim.dao.ConversationDao;
import tl.pojul.com.fastim.util.ConversationUtil;
import tl.pojul.com.fastim.util.DateUtil;
import tl.pojul.com.fastim.util.DialogUtil;
import tl.pojul.com.fastim.util.GlideUtil;
import tl.pojul.com.fastim.util.SPUtil;

public class AddFriendReqAdapter extends RecyclerView.Adapter<AddFriendReqAdapter.MyViewHolder> {

    private Context mContext;
    private List<AddFriend> mList;

    public AddFriendReqAdapter(Context mContext, List<AddFriend> mList) {
        this.mContext = mContext;
        this.mList = mList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_friend_req, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        AddFriend addFriend = mList.get(position);
        GlideUtil.setImageBitmapNoOptions(addFriend.getReqUserInfo().getPhoto().getFilePath(), holder.photo);
        if(addFriend.getReqType() == 1){
            holder.title.setText(addFriend.getReqUserInfo().getNickName() + " 请求添加你为好友");
        }else{
            holder.title.setText(addFriend.getReqUserInfo().getNickName() + " 向你打了声招呼");
        }
        holder.age.setText((addFriend.getReqUserInfo().getAge() + "岁"));
        holder.sex.setImageResource((addFriend.getReqUserInfo().getSex() == 0 ? R.drawable.woman : R.drawable.man));
        holder.reqText.setText(addFriend.getReqText());
        holder.time.setText(DateUtil.getHeadway(DateUtil.convertTimeToLong(addFriend.getReqTime())));
        if(addFriend.getReqStatus() == 0){
            holder.accepted.setText("同意");
            holder.accepted.setEnabled(true);
            holder.accepted.setOnClickListener(v->{
                acceptAddFriend(addFriend, position);
            });
        } else {
            holder.accepted.setText("已同意");
            holder.accepted.setEnabled(false);
            holder.accepted.setOnClickListener(null);
        }
        holder.photo.setOnClickListener(v->{
            Bundle bundle = new Bundle();
            bundle.putString("userName", addFriend.getReqUserName());
            ((BaseActivity)mContext).startActivity(MyPageActivity.class, bundle);
        });
    }

    private void acceptAddFriend(AddFriend addFriend, int position) {
        User user = SPUtil.getInstance().getUser();
        if(user == null){
            Toast.makeText(mContext, "数据错误", Toast.LENGTH_SHORT).show();
            return;
        }
        AcceptFriendReq req = new AcceptFriendReq();
        req.setReqUserName(addFriend.getReqUserName());
        req.setReqedUserName(addFriend.getReqedUserName());
        req.setType(addFriend.getReqType());
        req.setReqedUserInfo(user);
        req.setReqText(addFriend.getReqText());
        DialogUtil.getInstance().showLoadingSimple(mContext, ((BaseActivity)mContext).getWindow().getDecorView());
        new SocketRequest().request(MyApplication.ClientSocket, req, new SocketRequest.IRequest() {
            @Override
            public void onError(String msg) {
                new Handler(Looper.getMainLooper()).post(()->{
                    DialogUtil.getInstance().dimissLoadingDialog();
                    Toast.makeText(mContext, msg, Toast.LENGTH_SHORT).show();
                });
            }

            @Override
            public void onFinished(ResponseMessage mResponse) {
                new Handler(Looper.getMainLooper()).post(()->{
                    DialogUtil.getInstance().dimissLoadingDialog();
                    if(mResponse.getCode() == 200){
                        if(addFriend.getReqType() == 1){
                            addFriend(addFriend);
                            addFriend.setReqStatus(1);
                        }else if(addFriend.getReqType() == 2){
                            addGreet(addFriend, user);
                            addFriend.setReqStatus(1);
                        }
                        notifyItemChanged(position);
                    }else{
                        Toast.makeText(mContext, mResponse.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    private void addGreet(AddFriend addFriend, User user) {
        Friend friend = new UserConverter().converterToFriend(addFriend.getReqUserInfo());
        ConversationDao conversationDao = new ConversationDao();
        if(!conversationDao.isSingleConversationExit(friend.getUserName(), user.getUserName())){
            Conversation conversation = new Conversation();
            conversation.setConversationName(friend.getNickName());
            conversation.setConversationFrom(friend.getUserName());
            conversation.setConversationPhoto(friend.getPhoto().getFilePath());
            conversation.setConversationLastChat(addFriend.getReqText());
            conversation.setConversationLastChattime(DateUtil.getFormatDate());
            conversation.setConversationOwner(user.getUserName());
            conversation.setUnreadMessage(0);
            conversation.setConversionUid("");
            conversation.setConversationType(1);
            conversationDao.insertConversation(conversation);
        }
        Intent intent = new Intent(mContext, SingleChatRoomActivity.class);
        Bundle bundle=new Bundle();
        bundle.putInt("chat_room_type", 1);
        bundle.putString("chat_room_name", addFriend.getReqUserInfo().getNickName());
        bundle.putString("friend", new Gson().toJson(friend));
        intent.putExtras(bundle);
        mContext.startActivity(intent);
    }

    private void addFriend(AddFriend addFriend) {
        MainActivity mainActivity = MyApplication.getApplication().getMainActivity();
        ChatFragment chatFragment = null;
        if (mainActivity != null) {
            chatFragment = mainActivity.chatFragment;
        }
        if (chatFragment != null && chatFragment.friendsFragment != null && chatFragment.friendsFragment.friendsAdapter != null) {
            chatFragment.friendsFragment.getFriends(false);
        }
        Friend friend = new UserConverter().converterToFriend(addFriend.getReqUserInfo());
        Intent intent = new Intent(mContext, SingleChatRoomActivity.class);
        Bundle bundle=new Bundle();
        bundle.putInt("chat_room_type", 1);
        bundle.putString("chat_room_name", addFriend.getReqUserInfo().getNickName());
        bundle.putString("friend", new Gson().toJson(friend));
        intent.putExtras(bundle);
        mContext.startActivity(intent);
    }

    @Override
    public int getItemCount() {
        return mList != null ? mList.size() : 0;
    }


    public void addDatas(List<AddFriend> addFriends) {
        if (addFriends == null || addFriends.size() <= 0) {
            return;
        }
        synchronized (mList) {
            int position = mList.size();
            mList.addAll(addFriends);
            notifyItemRangeInserted(position, addFriends.size());
        }
    }

    public void clearData() {
        synchronized (mList) {
            mList.clear();
            notifyDataSetChanged();
        }
    }

    static class MyViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.photo)
        PolygonImageView photo;
        @BindView(R.id.title)
        TextView title;
        @BindView(R.id.age)
        TextView age;
        @BindView(R.id.sex)
        ImageView sex;
        @BindView(R.id.req_text)
        TextView reqText;
        @BindView(R.id.time)
        TextView time;
        @BindView(R.id.accepted)
        TextView accepted;
        @BindView(R.id.time_ll)
        LinearLayout timeLl;

        public MyViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

}
