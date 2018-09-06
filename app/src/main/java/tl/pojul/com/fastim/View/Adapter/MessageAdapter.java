package tl.pojul.com.fastim.View.Adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
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
import com.pojul.fastIM.entity.CommunityMessEntity;
import com.pojul.fastIM.entity.Friend;
import com.pojul.fastIM.entity.Message;
import com.pojul.fastIM.entity.MessageFilter;
import com.pojul.fastIM.entity.Pic;
import com.pojul.fastIM.entity.User;
import com.pojul.fastIM.message.chat.AudioMessage;
import com.pojul.fastIM.message.chat.ChatMessage;
import com.pojul.fastIM.message.chat.CommunityMessage;
import com.pojul.fastIM.message.chat.DateMessage;
import com.pojul.fastIM.message.chat.FileMessage;
import com.pojul.fastIM.message.chat.NetPicMessage;
import com.pojul.fastIM.message.chat.PicMessage;
import com.pojul.fastIM.message.chat.ReplyMessage;
import com.pojul.fastIM.message.chat.TagCommuMessage;
import com.pojul.fastIM.message.chat.TextChatMessage;
import com.pojul.fastIM.message.chat.VideoMessage;
import com.pojul.fastIM.message.request.CommunThumbReq;
import com.pojul.objectsocket.constant.StorageType;
import com.pojul.objectsocket.message.BaseMessage;
import com.pojul.objectsocket.message.ResponseMessage;
import com.pojul.objectsocket.socket.SocketRequest;

import org.w3c.dom.Text;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import tl.pojul.com.fastim.Media.AudioManager;
import tl.pojul.com.fastim.MyApplication;
import tl.pojul.com.fastim.R;
import tl.pojul.com.fastim.View.activity.BaseActivity;
import tl.pojul.com.fastim.View.activity.ChatFileDownloadActivity;
import tl.pojul.com.fastim.View.activity.TagMessageActivity;
import tl.pojul.com.fastim.View.activity.TagReplyActivity;
import tl.pojul.com.fastim.View.activity.VideoActivity;
import tl.pojul.com.fastim.View.widget.PolygonImage.view.PolygonImageView;
import tl.pojul.com.fastim.socket.Converter.HistoryChatConverter;
import tl.pojul.com.fastim.util.AnimatorUtil;
import tl.pojul.com.fastim.util.ArrayUtil;
import tl.pojul.com.fastim.util.DateUtil;
import tl.pojul.com.fastim.util.DensityUtil;
import tl.pojul.com.fastim.util.DialogUtil;
import tl.pojul.com.fastim.util.GlideUtil;
import tl.pojul.com.fastim.util.NumberUtil;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.BaseMessageHolder> {

    private Context mContext;
    private List<ChatMessage> mList;
    private User user;
    private Friend friend;
    private static final String TAG = "MessageAdapter";
    private HashMap<Integer, Integer> progressWidths = new HashMap<>();
    private MessageFilter messageFilter;

    private static final int START_TAG_ACTIVITY = 121;

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
                case 8:
                    view = LayoutInflater.from(mContext).inflate(R.layout.item_tag_message, parent, false);
                    return new TagMessageHolder(view);
                default:
                    view = LayoutInflater.from(mContext).inflate(R.layout.item_text_message, parent, false);
                    return new TextMessageHolder(view);
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
                case 8:
                    bindTagMessageHolder((TagMessageHolder) holder, position);
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
        ChatMessage chatMessage = mList.get(position);
        if (chatMessage instanceof CommunityMessage) {
            chatMessage = ((CommunityMessage) chatMessage).getContent();
        }
        if (chatMessage instanceof TextChatMessage) {
            return 1;
        } else if (chatMessage instanceof PicMessage) {
            return 2;
        } else if (chatMessage instanceof AudioMessage) {
            return 3;
        } else if (chatMessage instanceof NetPicMessage) {
            return 4;
        } else if (chatMessage instanceof FileMessage) {
            return 5;
        } else if (chatMessage instanceof VideoMessage) {
            return 6;
        } else if (chatMessage instanceof DateMessage) {
            return 7;
        } else if (chatMessage instanceof TagCommuMessage) {
            return 8;
        } else {
            return -1;
        }
    }

    private ChatMessage extractMessage(int position) {
        ChatMessage baseChatMessage = mList.get(position);
        ChatMessage chatMessage;
        if (baseChatMessage instanceof CommunityMessage) {
            chatMessage = ((CommunityMessage) baseChatMessage).getContent();
            chatMessage.setFrom(baseChatMessage.getFrom());
            chatMessage.setTo(baseChatMessage.getTo());
            return chatMessage;
        } else {
            return baseChatMessage;
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
            Glide.with(mContext).load(user.getPhoto().getFilePath()).into(holder.myPhoto);
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                layoutParams.removeRule(RelativeLayout.ALIGN_PARENT_RIGHT);
            }
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
            holder.friendPhoto.setVisibility(View.VISIBLE);
            holder.myPhoto.setVisibility(View.GONE);
            if (chatMessage instanceof CommunityMessage) {
                Glide.with(mContext).load(((CommunityMessage) chatMessage).getPhoto().getFilePath()).into(holder.friendPhoto);
            } else {
                Glide.with(mContext).load(friend.getPhoto().getFilePath()).into(holder.friendPhoto);
            }
        }
        if (chatMessage.getIsSend() == 0) {
            holder.progressBar1.setVisibility(View.VISIBLE);
        } else if (chatMessage.getIsSend() == 1) {
            holder.progressBar1.setVisibility(View.GONE);
        } else {
            //发送失败图标
        }
        if (chatMessage instanceof CommunityMessage) {
            holder.nickName.setVisibility(View.VISIBLE);
            if (user.getUserName().equals(chatMessage.getFrom())) {
                holder.nickName.setText(user.getNickName());
                ((LinearLayout.LayoutParams) holder.nickName.getLayoutParams()).gravity = Gravity.RIGHT;
                ((LinearLayout.LayoutParams) holder.rlMessage.getLayoutParams()).gravity = Gravity.RIGHT;
            } else {
                holder.nickName.setText(((CommunityMessage) chatMessage).getNickName());
                ((LinearLayout.LayoutParams) holder.nickName.getLayoutParams()).gravity = Gravity.LEFT;
                ((LinearLayout.LayoutParams) holder.rlMessage.getLayoutParams()).gravity = Gravity.LEFT;
            }
        } else {
            if (user.getUserName().equals(chatMessage.getFrom())) {
                ((LinearLayout.LayoutParams) holder.rlMessage.getLayoutParams()).gravity = Gravity.RIGHT;
            } else {
                ((LinearLayout.LayoutParams) holder.rlMessage.getLayoutParams()).gravity = Gravity.LEFT;
            }
            holder.nickName.setVisibility(View.GONE);
        }
    }

    private void bindTextMessageHolder(TextMessageHolder holder, int position) {
        bindBaseMessageHolder(holder, position);
        TextChatMessage chatMessage = (TextChatMessage) extractMessage(position);
        holder.text.setText(chatMessage.getText());
    }

    private void bindPicMessageHolder(PicMessageHolder holder, int position) {
        PicMessage picMessage = (PicMessage) extractMessage(position);
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
        AudioMessage audioMessage = (AudioMessage) extractMessage(position);
        bindBaseMessageHolder(holder, position);
        if (user.getUserName().equals(audioMessage.getFrom())) {
            holder.audio.setRotation(180);
        } else {
            holder.audio.setRotation(0);
            holder.rlMessage.setOnClickListener(v -> {

            });
        }
        holder.rlMessage.setOnClickListener(v -> {
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
        NetPicMessage netPicMessage = (NetPicMessage) extractMessage(position);
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
        FileMessage fileMessage = (FileMessage) extractMessage(position);
        bindBaseMessageHolder(holder, position);
        holder.fileName.setText(fileMessage.getFile().getFileName());
        holder.rlMessage.setOnClickListener(v -> {
            Intent intent = new Intent(mContext, ChatFileDownloadActivity.class);
            intent.putExtra("fileMessage", new Gson().toJson(fileMessage));
            mContext.startActivity(intent);
        });

        if (progressWidths.get(position) == null) {
            int width = holder.fileIcon.getWidth() + holder.fileName.getWidth();
            int minWidth = DensityUtil.dp2px(mContext, 70);
            if (width < minWidth) {
                width = minWidth;
            }
            progressWidths.put(position, width);
        }
        int progressWidth = progressWidths.get(position);
        holder.progressBar1.getLayoutParams().width = progressWidth;
        holder.progressBar1.getLayoutParams().height = DensityUtil.dp2px(mContext, 3);
        holder.progressBar1.setProgress(fileMessage.getSendProgress());
    }

    private void bindVideoMessageHolder(VideoMessageHolder holder, int position) {
        VideoMessage videoMessage = (VideoMessage) extractMessage(position);
        bindBaseMessageHolder(holder, position);
        holder.play.setOnClickListener(view -> {
            Intent intent = new Intent(mContext, VideoActivity.class);
            intent.putExtra("videoMessage", new Gson().toJson(videoMessage));
            mContext.startActivity(intent);
        });
        if (videoMessage.getFirstPic() != null) {
            RequestOptions options = new RequestOptions();
            options.placeholder(R.drawable.pic)
                    .error(R.drawable.pic)
                    .fallback(R.drawable.pic);
            Glide.with(mContext).load(videoMessage.getFirstPic().getFilePath()).apply(options).into(holder.pic);
        }

        if (videoMessage.getVideoWidth() > 0 && videoMessage.getVideoHeight() > 0 && videoMessage.getIsSend() == 0) {
            int progressWidth;
            if (videoMessage.getVideoHeight() > videoMessage.getVideoWidth()) {
                if (progressWidths.get(position) == null) {
                    float scale = videoMessage.getVideoWidth() * 1.0f / videoMessage.getVideoHeight();
                    int width = (int) (DensityUtil.dp2px(mContext, 100) * scale);
                    progressWidths.put(position, width);
                }
                progressWidth = progressWidths.get(position) - 6;
            } else {
                progressWidth = DensityUtil.dp2px(mContext, 100) - 6;
            }
            holder.progressBar1.getLayoutParams().width = progressWidth - 6;
            holder.progressBar1.getLayoutParams().height = DensityUtil.dp2px(mContext, 3);
            holder.progressBar1.setProgress(videoMessage.getSendProgress());
        }
    }

    private void bindTagMessageHolder(TagMessageHolder holder, int position) {
        TagCommuMessage tagMessage = (TagCommuMessage) mList.get(holder.getAdapterPosition());
        bindBaseMessageHolder(holder, holder.getAdapterPosition());
        if(tagMessage.getHasReport() == 0){
            holder.reportTv.setText("举报");
            holder.reportTv.setSelected(false);
            holder.reportIv.setSelected(false);
            holder.reportRl.setOnClickListener(v -> {
                DialogUtil.getInstance().showReportDialog(mContext, tagMessage);
                DialogUtil.getInstance().setDialogClick(str -> {
                    if("report_sucesses".equals(str)){
                        tagMessage.setHasReport(1);
                        holder.reportTv.setText("已举报");
                        holder.reportRl.setOnClickListener(null);
                        holder.reportTv.setSelected(true);
                        holder.reportIv.setSelected(true);
                    }
                });
            });
        }else{
            holder.reportTv.setText("已举报");
            holder.reportRl.setOnClickListener(null);
            holder.reportTv.setSelected(true);
            holder.reportIv.setSelected(true);
        }
        holder.thumbUpTv.setText(NumberUtil.getNumStr(tagMessage.getThumbsUps()));
        if(tagMessage.isThumbsUping){
            AnimatorUtil.startThumbUpAnimator(holder.addOne);
        }else{
            holder.addOne.setVisibility(View.GONE);
        }
        if(tagMessage.getHsaThumbsUp() == 0){
            holder.thumbUpTv.setSelected(false);
            holder.thumbUpIv.setSelected(false);
            holder.thumbUpRl.setOnClickListener(v -> {
                if(tagMessage.isThumbsUping){
                    return;
                }
                requestCommuThumbUp(holder.getAdapterPosition());
            });
        }else{
            holder.thumbUpTv.setSelected(true);
            holder.thumbUpIv.setSelected(true);
            holder.thumbUpRl.setOnClickListener(null);
        }
        holder.replyRl.setOnClickListener(v -> {
            holder.replyIv.performClick();
            holder.replyNumTv.performClick();
            Bundle bundle = new Bundle();
            bundle.putString("TagCommuMessage", new Gson().toJson(tagMessage));
            ((BaseActivity)mContext).startActivityForResult(TagReplyActivity.class, bundle, START_TAG_ACTIVITY);
        });
        holder.replyNumTv.setText((tagMessage.getReplysNum() + ""));
        if (tagMessage.getTitle() == null || tagMessage.getTitle().isEmpty()) {
            holder.title.setText("");
        } else {
            holder.title.setText(tagMessage.getTitle());
        }
        if (tagMessage.getPics() == null || tagMessage.getPics().size() <= 0) {
            holder.pics.setVisibility(View.GONE);
            holder.img.setVisibility(View.GONE);
        } else {
            //holder.pics.setVisibility(View.VISIBLE);
            if (tagMessage.getPics().size() == 1) {
                holder.pics.setVisibility(View.GONE);
                holder.img.setVisibility(View.VISIBLE);
                GlideUtil.setImageBitmap(tagMessage.getPics().get(0).getUploadPicUrl().getFilePath(), holder.img, 0.5f);
            } else {
                holder.pics.setVisibility(View.VISIBLE);
                holder.img.setVisibility(View.GONE);
                if(tagMessage.getPics().size() == 2){
                    holder.pics.setLayoutManager(new GridLayoutManager(mContext, 2));
                    holder.pics.setAdapter(new TagPicAdapter(mContext, tagMessage.getPics(), true));
                }else if(tagMessage.getPics().size() > 2){
                    holder.pics.setLayoutManager(new GridLayoutManager(mContext, 3));
                    List<Pic> pics = new ArrayList<>();
                    for (int i =0; i < 3; i++){
                        pics.add(tagMessage.getPics().get(i));
                    }
                    holder.pics.setAdapter(new TagPicAdapter(mContext, pics, true));
                }
            }
        }
        if (tagMessage.getText() == null || tagMessage.getText().isEmpty()) {
            holder.note.setVisibility(View.GONE);
        } else {
            holder.note.setVisibility(View.VISIBLE);
            holder.note.setText("\u3000" + tagMessage.getText());
        }
        if (tagMessage.getIsEffective() == 1) {
            holder.isEffective.setText("已失效");
        } else {
            holder.isEffective.setText("有效");
        }
        holder.progressBar1.setProgress(tagMessage.getSendProgress());
        if(tagMessage.getReplys() == null || tagMessage.getReplys().size() <= 0){
            holder.replys.setVisibility(View.GONE);
        }else{
            ReplyMessage replyMessage = tagMessage.getReplys().get(0);
            holder.replys.setVisibility(View.VISIBLE);
            holder.reply1.setText((replyMessage.getNickName() + "："));
            holder.reply1Text.setText(replyMessage.getText());
        }
    }

    private void bindDateMessageHolder(DateMessageHolder holder, int position) {
        DateMessage dateMessage = (DateMessage) mList.get(position);
        holder.llTextMessage.setVisibility(View.GONE);
        holder.date.setText(dateMessage.getDate());
    }

    public void clearData() {
        synchronized (mList){
            mList.clear();
            notifyDataSetChanged();
        }
    }

    private void requestCommuThumbUp(int position){
        if(!(mList.get(position) instanceof TagCommuMessage)){
            return;
        }
        TagCommuMessage tagCommuMessage = (TagCommuMessage) mList.get(position);
        tagCommuMessage.setThumbsUping(true);
        notifyItemChanged(position);
        CommunThumbReq communThumbReq = new CommunThumbReq();
        communThumbReq.setCommunMessageUid(tagCommuMessage.getMessageUid());
        new SocketRequest().request(MyApplication.ClientSocket, communThumbReq, new SocketRequest.IRequest() {
            @Override
            public void onError(String msg) {
                new Handler(Looper.getMainLooper()).post(()->{
                    tagCommuMessage.setThumbsUping(false);
                    Toast.makeText(mContext, "点赞失败", Toast.LENGTH_SHORT).show();
                });
            }

            @Override
            public void onFinished(ResponseMessage mResponse) {
                new Handler(Looper.getMainLooper()).post(()->{
                    if(mResponse.getCode() == 200){
                        tagCommuMessage.setThumbsUping(false);
                        tagCommuMessage.setThumbsUps((tagCommuMessage.getThumbsUps() + 1));
                        tagCommuMessage.setHsaThumbsUp(1);
                        notifyItemChanged(position);
                    }else{
                        Toast.makeText(mContext, "点赞失败", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == START_TAG_ACTIVITY && resultCode == Activity.RESULT_OK){
            String tagMessUid = data.getStringExtra("tagMessUid");
            int isEffictive = data.getIntExtra("isEffictive", 1);
            synchronized (mList){
                for (int i = 0; i < mList.size(); i++) {
                    ChatMessage chatMessage = mList.get(i);
                    if(chatMessage instanceof TagCommuMessage && chatMessage.getMessageUid().equals(tagMessUid)){
                        ((TagCommuMessage)chatMessage).setIsEffective(isEffictive);
                        notifyItemChanged(i);
                        break;
                    }
                }
            }
        }
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
        /*@BindView(R.id.nick_name)*/
        TextView nickName;
        //@BindView(R.id.rl_message)
        RelativeLayout rlMessage;

        public BaseMessageHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            nickName = itemView.findViewById(R.id.nick_name);
            rlMessage = itemView.findViewById(R.id.rl_message);
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

    class TagMessageHolder extends BaseMessageHolder {

        @BindView(R.id.title)
        TextView title;
        @BindView(R.id.pics)
        RecyclerView pics;
        @BindView(R.id.note)
        TextView note;
        @BindView(R.id.content_ll)
        RelativeLayout contentLl;
        @BindView(R.id.reply1)
        TextView reply1;
        @BindView(R.id.reply1_text)
        TextView reply1Text;
        @BindView(R.id.replys)
        LinearLayout replys;
        @BindView(R.id.report_iv)
        ImageView reportIv;
        @BindView(R.id.report_tv)
        TextView reportTv;
        @BindView(R.id.report_rl)
        RelativeLayout reportRl;
        @BindView(R.id.thumb_up_iv)
        ImageView thumbUpIv;
        @BindView(R.id.thumb_up_tv)
        TextView thumbUpTv;
        @BindView(R.id.thumb_up_rl)
        RelativeLayout thumbUpRl;
        @BindView(R.id.reply_iv)
        ImageView replyIv;
        @BindView(R.id.reply_num_tv)
        TextView replyNumTv;
        @BindView(R.id.reply_rl)
        RelativeLayout replyRl;
        @BindView(R.id.is_effective)
        TextView isEffective;
        @BindView(R.id.detail)
        TextView detail;
        @BindView(R.id.img)
        ImageView img;
        @BindView(R.id.thumb_progress)
        ProgressBar thumbProgress;
        @BindView(R.id.add_one)
        TextView addOne;

        public TagMessageHolder(View itemView) {
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
                    BaseMessage mMessage = message;
                    if(message instanceof CommunityMessage){
                        mMessage = ((CommunityMessage) message).getContent();
                    }
                    if (mMessage instanceof FileMessage || mMessage instanceof VideoMessage || message instanceof TagCommuMessage) {
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

    public void addHistoryChat(List<CommunityMessEntity> communityMessEntities) {
        synchronized (mList) {
            List<ChatMessage> chatMessages = new HistoryChatConverter().converter(communityMessEntities, messageFilter);
            if (chatMessages.size() > 0 && mList.size() > 1 && (mList.get(0) instanceof DateMessage) &&
                    !DateUtil.isDiffDay(chatMessages.get(chatMessages.size() - 1).getSendTime(), mList.get(1).getSendTime())) {
                mList.remove(0);
                notifyItemRemoved(0);
            }
            mList.addAll(0, chatMessages);
            this.notifyItemRangeInserted(0, chatMessages.size());
        }
    }

    public void addHistoryChat(ArrayList<Message> messages) {
        /**
         * 添加时间检测
         * */
        synchronized (mList) {
            List<ChatMessage> chatMessages = new HistoryChatConverter().converter(messages);
            if (chatMessages.size() > 0 && mList.size() > 1 && (mList.get(0) instanceof DateMessage) &&
                    !DateUtil.isDiffDay(chatMessages.get(chatMessages.size() - 1).getSendTime(), mList.get(1).getSendTime())) {
                mList.remove(0);
                notifyItemRemoved(0);
            }
            mList.addAll(0, chatMessages);
            this.notifyItemRangeInserted(0, chatMessages.size());
        }
    }

    public void receiveMessage(ChatMessage message) {
        if((message instanceof CommunityMessage) && !messageFilter((CommunityMessage) message, messageFilter)){
            return;
        }
        /**
         * 添加时间检测
         * */
        synchronized (mList) {
            if (isDiffDayWithPrev(message)) {
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
            if (isDiffDayWithPrev(message)) {
                mList.add(new DateMessage(DateUtil.transformToRoughDate(message.getSendTime())));
                notifyItemInserted((mList.size() - 1));
            }
            mList.add(message);
            notifyItemInserted((mList.size() - 1));
        }
    }

    public boolean isDiffDayWithPrev(ChatMessage message) {
        if (mList.size() == 0) {
            return true;
        }
        if (DateUtil.isDiffDay(message.getSendTime(), mList.get((mList.size() - 1)).getSendTime())) {
            return true;
        } else {
            return false;
        }
    }

    public boolean messageFilter(CommunityMessage communityMessage, MessageFilter messageFilter) {
        if(messageFilter == null){
            return true;
        }
        if(messageFilter.getLabels() != null && messageFilter.getLabels().size() > 0){
            if(!(communityMessage instanceof TagCommuMessage)){
                return false;
            }
            TagCommuMessage tagCommuMessage = (TagCommuMessage) communityMessage;
            if(!ArrayUtil.hasIntersecte(messageFilter.getLabels(), tagCommuMessage.getLabels())){
                return false;
            }
        }
        if(messageFilter.getCertificat() != -1 && communityMessage.getCertificate() != 1){
            return false;
        }
        if(messageFilter.getSex() != -1 && messageFilter.getSex() != communityMessage.getUserSex()){
            return false;
        }
        return true;
    }

    public void setMessageFilter(MessageFilter messageFilter) {
        this.messageFilter = messageFilter;
    }
}
