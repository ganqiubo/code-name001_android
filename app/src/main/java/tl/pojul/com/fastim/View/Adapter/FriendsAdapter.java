package tl.pojul.com.fastim.View.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.pojul.fastIM.entity.Friend;

import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import tl.pojul.com.fastim.R;

public class FriendsAdapter extends RecyclerView.Adapter<FriendsAdapter.MyViewHolder> {

    private Context mContext;

    public List<Friend> getmList() {
        return mList;
    }

    public void setmList(List<Friend> mList) {
        this.mList = mList;
    }

    private List<Friend> mList;
    private FriendsAdapter.OnItemClickListener mOnItemClickListener;

    public FriendsAdapter(Context mContext, List<Friend> mList) {
        this.mContext = mContext;
        this.mList = mList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_friends_list, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        if (mOnItemClickListener != null) {
            holder.itemRl.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mOnItemClickListener.onClick(position);
                }
            });
        }
        Friend friend = mList.get(position);
        holder.friendNickname.setText(friend.getNickName());
        holder.friendAutograph.setText(friend.getAutograph() == null ? "" : friend.getAutograph());
        if (friend.getPhoto() != null) {
            Glide.with(mContext).load(friend.getPhoto()).into(holder.friendPhoto);
        } else {
            Glide.with(mContext).load(R.drawable.photo_default).into(holder.friendPhoto);
        }
        if (friend.getUnreadMessage() > 0) {
            holder.unreadMessage.setVisibility(View.VISIBLE);
            holder.unreadMessage.setText(("" + friend.getUnreadMessage()));
        } else {
            holder.unreadMessage.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return mList != null ? mList.size() : 0;
    }

    static class MyViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.friend_photo)
        ImageView friendPhoto;
        @BindView(R.id.friend_nickname)
        TextView friendNickname;
        @BindView(R.id.friend_autograph)
        TextView friendAutograph;
        @BindView(R.id.unread_message)
        TextView unreadMessage;
        @BindView(R.id.item_rl)
        RelativeLayout itemRl;

        public MyViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public void unreadUnmChanged(HashMap<String, Integer> each) {
        if (mList == null) {
            return;
        }
        for (int i = 0; i < mList.size(); i++) {
            Friend friend = mList.get(i);
            if (each.get(friend.getUserName()) != null) {
                friend.setUnreadMessage(each.get(friend.getUserName()));
            }
        }
        this.notifyDataSetChanged();
    }

    public interface OnItemClickListener {
        void onClick(int position);
    }

    public void setOnItemClickListener(FriendsAdapter.OnItemClickListener onItemClickListener) {
        this.mOnItemClickListener = onItemClickListener;
    }

}
