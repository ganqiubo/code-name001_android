package tl.pojul.com.fastim.View.Adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.gson.Gson;
import com.pojul.fastIM.entity.Friend;
import com.pojul.fastIM.entity.Message;
import com.pojul.fastIM.entity.User;
import com.pojul.fastIM.message.chat.AudioMessage;
import com.pojul.fastIM.message.chat.ChatMessage;
import com.pojul.fastIM.message.chat.DateMessage;
import com.pojul.fastIM.message.chat.FileMessage;
import com.pojul.fastIM.message.chat.NetPicMessage;
import com.pojul.fastIM.message.chat.PicMessage;
import com.pojul.fastIM.message.chat.TextChatMessage;
import com.pojul.fastIM.message.chat.VideoMessage;
import com.pojul.objectsocket.constant.StorageType;
import com.pojul.objectsocket.message.BaseMessage;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import tl.pojul.com.fastim.Media.AudioManager;
import tl.pojul.com.fastim.R;
import tl.pojul.com.fastim.View.activity.ChatFileDownloadActivity;
import tl.pojul.com.fastim.View.activity.VideoActivity;
import tl.pojul.com.fastim.View.widget.PolygonImage.view.PolygonImageView;
import tl.pojul.com.fastim.socket.Converter.HistoryChatConverter;
import tl.pojul.com.fastim.util.DateUtil;
import tl.pojul.com.fastim.util.DensityUtil;
import tl.pojul.com.fastim.util.DialogUtil;

/**
 * Created by gqb on 2018/6/13.
 */

public class CommunityMessageAdapter extends RecyclerView.Adapter<CommunityMessageAdapter.BaseMessageHolder> {

    private Context mContext;
    private List<ChatMessage> mList;
    private User user;
    private static final String TAG = "CommunityMessageAdapter";

    public CommunityMessageAdapter(Context mContext, List<ChatMessage> mList, User user, Friend friend) {
        this.mContext = mContext;
        this.mList = mList;
        this.user = user;
    }

    public List<ChatMessage> getChatMessages() {
        return mList;
    }

    @Override
    public BaseMessageHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(BaseMessageHolder holder, int position) {
        synchronized (mList) {
            int type = getItemViewType(position);
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
        } else if (mList.get(position) instanceof PicMessage) {
            return 2;
        } else if (mList.get(position) instanceof AudioMessage) {
            return 3;
        } else if (mList.get(position) instanceof NetPicMessage) {
            return 4;
        } else if (mList.get(position) instanceof FileMessage) {
            return 5;
        } else if (mList.get(position) instanceof VideoMessage) {
            return 6;
        } else if (mList.get(position) instanceof DateMessage) {
            return 7;
        } else {
            return -1;
        }
    }

    class BaseMessageHolder extends RecyclerView.ViewHolder {

        public BaseMessageHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

}
