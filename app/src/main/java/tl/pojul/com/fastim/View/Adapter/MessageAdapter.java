package tl.pojul.com.fastim.View.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.pojul.fastIM.message.chat.ChatMessage;
import com.pojul.fastIM.message.chat.TextChatMessage;

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

    public MessageAdapter(Context mContext, List<ChatMessage> mList) {
        this.mContext = mContext;
        this.mList = mList;
    }

    @Override
    public FriendsAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        switch (viewType) {
            case 1:
                view = LayoutInflater.from(mContext).inflate(R.layout.item_text_message, parent, false);
                return new FriendsAdapter.MyViewHolder(view);
            default:
                view = LayoutInflater.from(mContext).inflate(R.layout.item_text_message, parent, false);
                return new FriendsAdapter.MyViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        int type = getItemViewType(position);
        switch (type){
            case 1:
                bindTextMessageHolder((TextMessageHolder)holder, position);
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
            return 0;
        } else {
            return -1;
        }
    }

    private void bindTextMessageHolder(TextMessageHolder holder, int position) {
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

        public TextMessageHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

}
