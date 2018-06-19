package tl.pojul.com.fastim.View.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.pojul.fastIM.entity.Friend;
import com.pojul.fastIM.entity.User;
import com.pojul.fastIM.message.chat.ChatMessage;
import com.pojul.fastIM.message.chat.TextChatMessage;
import com.pojul.objectsocket.message.BaseMessage;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import tl.pojul.com.fastim.R;
import tl.pojul.com.fastim.View.widget.PolygonImage.view.PolygonImageView;

/**
 * Created by gqb on 2018/6/13.
 */

public class MessageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context mContext;
    private List<ChatMessage> mList;
    private User user;
    private Friend friend;

    public MessageAdapter(Context mContext, List<ChatMessage> mList, User user, Friend friend) {
        this.mContext = mContext;
        this.mList = mList;
        this.user = user;
        this.friend = friend;
    }

    public List<ChatMessage> getChatMessages() {
        return mList;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        switch (viewType) {
            case 1:
                view = LayoutInflater.from(mContext).inflate(R.layout.item_text_message, parent, false);
                return new MessageAdapter.TextMessageHolder(view);
            default:
                view = LayoutInflater.from(mContext).inflate(R.layout.item_text_message, parent, false);
                return new MessageAdapter.TextMessageHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        int type = getItemViewType(position);
        switch (type) {
            case 1:
                bindTextMessageHolder((TextMessageHolder) holder, position);
                break;
            default:
                break;
        }
    }

    @Override
    public int getItemCount() {
        return mList != null ? mList.size() : 0;
    }

    @Override
    public int getItemViewType(int position) {
        if (mList.get(position) instanceof TextChatMessage) {
            return 1;
        } else {
            return -1;
        }
    }

    private void bindTextMessageHolder(TextMessageHolder holder, int position) {
        TextChatMessage chatMessage = (TextChatMessage) mList.get(position);
        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) holder.llTextMessage.getLayoutParams();
        if (user.getUserName().equals(chatMessage.getFrom())) {
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
            holder.friendPhoto.setVisibility(View.GONE);
            holder.myPhoto.setVisibility(View.VISIBLE);
            Glide.with(mContext).load(user.getPhoto()).into(holder.myPhoto);
        } else {
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
            holder.friendPhoto.setVisibility(View.VISIBLE);
            holder.myPhoto.setVisibility(View.GONE);
            Glide.with(mContext).load(friend.getPhoto()).into(holder.friendPhoto);
        }
        if (chatMessage.getIsSend() == 0) {
            holder.progressBar1.setVisibility(View.VISIBLE);
        } else if (chatMessage.getIsSend() == 1) {
            holder.progressBar1.setVisibility(View.GONE);
        } else {
            //发送失败图标
        }
        holder.text.setText(chatMessage.getText());
    }

    class TextMessageHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.friend_photo)
        PolygonImageView friendPhoto;
        @BindView(R.id.text)
        TextView text;
        @BindView(R.id.my_photo)
        PolygonImageView myPhoto;
        @BindView(R.id.ll_text_message)
        LinearLayout llTextMessage;
        @BindView(R.id.progressBar1)
        ProgressBar progressBar1;

        public TextMessageHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public void onSendFinish(BaseMessage message) {
        for (int i = (mList.size() - 1); i >= 0; i--) {
            if (message.getMessageUid() != null && message.getMessageUid().equals(mList.get(i).getMessageUid())) {
                mList.get(i).setIsSend(1);
                notifyItemChanged(i);
                break;
            }
        }
    }

    public void onSendFail(BaseMessage message) {
        for (int i = (mList.size() - 1); i >= 0; i--) {
            if (message.getMessageUid() != null && message.getMessageUid().equals(mList.get(i).getMessageUid())) {
                mList.get(i).setIsSend(-1);
                notifyItemChanged(i);
                break;
            }
        }
    }

    public void receiveMessage(ChatMessage message) {
        mList.add(message);
        notifyItemInserted((mList.size() - 1));
    }

}
