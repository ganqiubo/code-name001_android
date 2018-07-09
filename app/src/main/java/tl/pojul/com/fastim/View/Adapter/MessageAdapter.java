package tl.pojul.com.fastim.View.Adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

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
import com.pojul.objectsocket.utils.LogUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import tl.pojul.com.fastim.Media.AudioManager;
import tl.pojul.com.fastim.Media.VideoManager;
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

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.BaseMessageHolder> {

    private Context mContext;
    private List<ChatMessage> mList;
    private User user;
    private Friend friend;
    private static final String TAG = "MessageAdapter";
    private HashMap<Integer, Integer> progressWidths = new HashMap<>();

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
    public BaseMessageHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        synchronized (mList) {
            View view;
            switch (viewType) {
                case 1:
                    view = LayoutInflater.from(mContext).inflate(R.layout.item_text_message, parent, false);
                    return new TextMessageHolder(view);
                case 2:
                    view = LayoutInflater.from(mContext).inflate(R.layout.item_pic_message, parent, false);
                    return new PicMessageHolder(view);
                case 3:
                    view = LayoutInflater.from(mContext).inflate(R.layout.item_audio_message, parent, false);
                    return new AudioMessageHolder(view);
                case 4:
                    view = LayoutInflater.from(mContext).inflate(R.layout.item_pic_message, parent, false);
                    return new PicMessageHolder(view);
                case 5:
                    view = LayoutInflater.from(mContext).inflate(R.layout.item_file_message, parent, false);
                    return new FileMessageHolder(view);
                case 6:
                    view = LayoutInflater.from(mContext).inflate(R.layout.item_video_message, parent, false);
                    return new VideoMessageHolder(view);
                case 7:
                    view = LayoutInflater.from(mContext).inflate(R.layout.item_date_message, parent, false);
                    return new DateMessageHolder(view);
                default:
                    view = LayoutInflater.from(mContext).inflate(R.layout.item_text_message, parent, false);
                    return new PicMessageHolder(view);
            }
        }
    }

    @Override
    public void onBindViewHolder(BaseMessageHolder holder, int position) {
        synchronized (mList) {
            int type = getItemViewType(position);
            switch (type) {
                case 1:
                    bindTextMessageHolder((TextMessageHolder) holder, position);
                    break;
                case 2:
                    bindPicMessageHolder((PicMessageHolder) holder, position);
                    break;
                case 3:
                    bindAudioMessageHolder((AudioMessageHolder) holder, position);
                    break;
                case 4:
                    bindNetPicMessageHolder((PicMessageHolder) holder, position);
                    break;
                case 5:
                    bindFileMessageHolder((FileMessageHolder) holder, position);
                    break;
                case 6:
                    bindVideoMessageHolder((VideoMessageHolder) holder, position);
                    break;
                case 7:
                    bindDateMessageHolder((DateMessageHolder) holder, position);
                    break;
                default:
                    break;
            }
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

    private void bindBaseMessageHolder(BaseMessageHolder holder, int position) {
        ChatMessage chatMessage = mList.get(position);
        holder.llTextMessage.setVisibility(View.VISIBLE);
        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) holder.llTextMessage.getLayoutParams();
        if (user.getUserName().equals(chatMessage.getFrom())) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                layoutParams.removeRule(RelativeLayout.ALIGN_PARENT_LEFT);
            }
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
            holder.friendPhoto.setVisibility(View.GONE);
            holder.myPhoto.setVisibility(View.VISIBLE);
            Glide.with(mContext).load(user.getPhoto()).into(holder.myPhoto);
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                layoutParams.removeRule(RelativeLayout.ALIGN_PARENT_RIGHT);
            }
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
    }

    private void bindTextMessageHolder(TextMessageHolder holder, int position) {
        bindBaseMessageHolder(holder, position);
        TextChatMessage chatMessage = (TextChatMessage) mList.get(position);
        holder.text.setText(chatMessage.getText());
    }

    private void bindPicMessageHolder(PicMessageHolder holder, int position) {
        PicMessage picMessage = (PicMessage) mList.get(position);
        bindBaseMessageHolder(holder, position);
        if (picMessage.getPic().getFilePath() != null && !"".equals(picMessage.getPic().getFilePath())) {
            RequestOptions options = new RequestOptions();
            options.placeholder(R.drawable.pic)
                    .error(R.drawable.pic)
                    .fallback(R.drawable.pic);
            if (picMessage.getPic().getStorageType() == StorageType.LOCAL) {
                File file = new File(picMessage.getPic().getFilePath());
                Glide.with(mContext).load(file).apply(options).thumbnail(0.1f).into(holder.pic);
            } else {
                Glide.with(mContext).load(picMessage.getPic().getFilePath()).apply(options).thumbnail(0.1f).into(holder.pic);
            }
            holder.pic.setOnClickListener(v -> {
                Toast.makeText(mContext, "pic click", Toast.LENGTH_SHORT).show();
                try {
                    DialogUtil.getInstance().showDetailImgDialogPop(mContext, picMessage, holder.pic, DialogUtil.POP_TYPR_IMG);
                } catch (Exception e) {
                }
            });
        } else {
            Glide.with(mContext).load(R.drawable.pic).into(holder.pic);
        }
    }

    /*class ImageWidthRequest<Bitmap> implements RequestListener<Bitmap> {
        @Override
        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Bitmap> target, boolean isFirstResource) {
            return false;
        }

        @Override
        public boolean onResourceReady(Bitmap resource, Object model, Target<Bitmap> target, DataSource dataSource, boolean isFirstResource) {
            Log.e("", "onResourceReady");
            return false;
        }
    }*/

    private void bindAudioMessageHolder(AudioMessageHolder holder, int position) {
        AudioMessage audioMessage = (AudioMessage) mList.get(position);
        bindBaseMessageHolder(holder, position);
        if (user.getUserName().equals(audioMessage.getFrom())) {
            holder.audio.setRotation(180);
        } else {
            holder.audio.setRotation(0);
            holder.rlAudio.setOnClickListener(v -> {

            });
        }
        holder.rlAudio.setOnClickListener(v -> {
            AudioManager.getInstance().playShortSound(R.raw.record_end);
            if (audioMessage.getAudio().getStorageType() == StorageType.LOCAL) {
                if (new File(audioMessage.getAudio().getFilePath()).exists()) {
                    AudioManager.getInstance().playLocalAudio(audioMessage.getAudio().getFilePath());
                }
            } else {
                AudioManager.getInstance().playNetAudio(audioMessage.getAudio().getFilePath());
            }
        });

    }

    private void bindNetPicMessageHolder(PicMessageHolder holder, int position) {
        NetPicMessage netPicMessage = (NetPicMessage) mList.get(position);
        bindBaseMessageHolder(holder, position);
        RequestOptions options = new RequestOptions();
        options.placeholder(R.drawable.pic)
                .error(R.drawable.pic)
                .fallback(R.drawable.pic);
        Glide.with(mContext).load(netPicMessage.getThumbURL().getFilePath()).apply(options).into(holder.pic);
        holder.pic.setOnClickListener(v -> {
            try {
                DialogUtil.getInstance().showDetailImgDialogPop(mContext, netPicMessage, holder.pic, DialogUtil.POP_TYPR_IMG);
            } catch (Exception e) {
            }
        });
    }

    private void bindFileMessageHolder(FileMessageHolder holder, int position) {
        FileMessage fileMessage = (FileMessage) mList.get(position);
        bindBaseMessageHolder(holder, position);
        holder.fileName.setText(fileMessage.getFile().getFileName());
        holder.rlFile.setOnClickListener(v -> {
            Intent intent = new Intent(mContext, ChatFileDownloadActivity.class);
            intent.putExtra("fileMessage", new Gson().toJson(fileMessage));
            mContext.startActivity(intent);
        });

        if(progressWidths.get(position) == null) {
            int width = holder.fileIcon.getWidth() + holder.fileName.getWidth();
            int minWidth = DensityUtil.dp2px(mContext, 70);
            if(width < minWidth){
                width = minWidth;
            }
            progressWidths.put(position, width);
        }
        int progressWidth = progressWidths.get(position);
        holder.progressBar1.getLayoutParams().width =  progressWidth;
        holder.progressBar1.getLayoutParams().height =  DensityUtil.dp2px(mContext, 3);
        holder.progressBar1.setProgress(fileMessage.getSendProgress());
    }

    private void bindVideoMessageHolder(VideoMessageHolder holder, int position){
        VideoMessage videoMessage = (VideoMessage) mList.get(position);
        bindBaseMessageHolder(holder, position);
        holder.play.setOnClickListener(view->{
            Intent intent = new Intent(mContext, VideoActivity.class);
            intent.putExtra("videoMessage", new Gson().toJson(videoMessage));
            mContext.startActivity(intent);
        });
        if(videoMessage.getFirstPic() != null){
            RequestOptions options = new RequestOptions();
            options.placeholder(R.drawable.pic)
                    .error(R.drawable.pic)
                    .fallback(R.drawable.pic);
            Glide.with(mContext).load(videoMessage.getFirstPic().getFilePath()).apply(options).into(holder.pic);
        }

        if(videoMessage.getVideoWidth() > 0 && videoMessage.getVideoHeight() > 0 && videoMessage.getIsSend() == 0){
            int progressWidth;
            if(videoMessage.getVideoHeight() > videoMessage.getVideoWidth()){
                if(progressWidths.get(position) == null){
                    float scale = videoMessage.getVideoWidth()*1.0f / videoMessage.getVideoHeight();
                    int width = (int) (DensityUtil.dp2px(mContext, 100) * scale);
                    progressWidths.put(position, width);
                }
                progressWidth = progressWidths.get(position) - 6;
            }else{
                progressWidth = DensityUtil.dp2px(mContext, 100) - 6;
            }
            holder.progressBar1.getLayoutParams().width =  progressWidth- 6;
            holder.progressBar1.getLayoutParams().height =  DensityUtil.dp2px(mContext, 3);
            holder.progressBar1.setProgress(videoMessage.getSendProgress());
        }
    }

    private void bindDateMessageHolder(DateMessageHolder holder, int position) {
        DateMessage dateMessage = (DateMessage) mList.get(position);
        holder.llTextMessage.setVisibility(View.GONE);
        holder.date.setText(dateMessage.getDate());
    }

    class BaseMessageHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.friend_photo)
        PolygonImageView friendPhoto;
        @BindView(R.id.my_photo)
        PolygonImageView myPhoto;
        @BindView(R.id.ll_text_message)
        LinearLayout llTextMessage;
        @BindView(R.id.progressBar1)
        ProgressBar progressBar1;

        public BaseMessageHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    class TextMessageHolder extends BaseMessageHolder {

        @BindView(R.id.text)
        TextView text;

        public TextMessageHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    class PicMessageHolder extends BaseMessageHolder {

        @BindView(R.id.pic)
        ImageView pic;

        public PicMessageHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    class AudioMessageHolder extends BaseMessageHolder {

        @BindView(R.id.audio)
        ImageView audio;
        @BindView(R.id.rl_audio)
        RelativeLayout rlAudio;

        public AudioMessageHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    class FileMessageHolder extends BaseMessageHolder {

        @BindView(R.id.file_icon)
        ImageView fileIcon;
        @BindView(R.id.file_name)
        TextView fileName;
        @BindView(R.id.rl_file)
        RelativeLayout rlFile;

        public FileMessageHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    class VideoMessageHolder extends BaseMessageHolder {

        @BindView(R.id.pic)
        ImageView pic;
        @BindView(R.id.play)
        ImageView play;

        public VideoMessageHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    class DateMessageHolder extends BaseMessageHolder {

        @BindView(R.id.date)
        TextView date;

        public DateMessageHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public void onSendFinish(BaseMessage message) {
        synchronized (mList) {
            for (int i = (mList.size() - 1); i >= 0; i--) {
                if (message.getMessageUid() != null && message.getMessageUid().equals(mList.get(i).getMessageUid())) {
                    mList.get(i).setIsSend(1);
                    notifyItemChanged(i);
                    break;
                }
            }
        }
    }

    public void updateSendProgress(BaseMessage message, int progress) {
        synchronized (mList) {
            for (int i = (mList.size() - 1); i >= 0; i--) {
                if (message.getMessageUid() != null && message.getMessageUid().equals(mList.get(i).getMessageUid())) {
                    if(message instanceof FileMessage || message instanceof VideoMessage){
                        mList.get(i).setSendProgress(progress);
                        notifyItemChanged(i);
                    }
                    break;
                }
            }
        }
    }

    public void onSendFail(BaseMessage message) {
        synchronized (mList) {
            for (int i = (mList.size() - 1); i >= 0; i--) {
                if (message.getMessageUid() != null && message.getMessageUid().equals(mList.get(i).getMessageUid())) {
                    mList.get(i).setIsSend(-1);
                    notifyItemChanged(i);
                    break;
                }
            }
        }
    }

    public void addHistoryChat(ArrayList<Message> messages) {
        /**
         * 添加时间检测
         * */
        synchronized (mList){
            List<ChatMessage> chatMessages = new HistoryChatConverter().converter(messages);
            if(chatMessages.size() > 0 && mList.size() > 1 && (mList.get(0) instanceof DateMessage) &&
                    !DateUtil.isDiffDay(chatMessages.get(chatMessages.size() -1).getSendTime(), mList.get(1).getSendTime())){
                mList.remove(0);
                notifyItemRemoved(0);
            }
            mList.addAll(0, chatMessages);
            this.notifyItemRangeInserted(0, chatMessages.size());
        }
    }

    public void receiveMessage(ChatMessage message) {
        /**
         * 添加时间检测
         * */
        synchronized (mList) {
            if(isDiffDayWithPrev(message)){
                mList.add(new DateMessage(DateUtil.transformToRoughDate(message.getSendTime())));
                notifyItemInserted((mList.size() - 1));
            }
            mList.add(message);
            notifyItemInserted((mList.size() - 1));
        }
    }

    public void addMessage(ChatMessage message) {
        /**
         * 添加时间检测
         * */
        synchronized (mList) {
            if(isDiffDayWithPrev(message)){
                mList.add(new DateMessage(DateUtil.transformToRoughDate(message.getSendTime())));
                notifyItemInserted((mList.size() - 1));
            }
            mList.add(message);
            notifyItemInserted((mList.size() - 1));
        }
    }

    public boolean isDiffDayWithPrev(ChatMessage message){
        if(mList.size() == 0){
            return true;
        }
        if(DateUtil.isDiffDay(message.getSendTime(), mList.get((mList.size() -1)).getSendTime())){
            return true;
        }else{
            return false;
        }
    }

}
