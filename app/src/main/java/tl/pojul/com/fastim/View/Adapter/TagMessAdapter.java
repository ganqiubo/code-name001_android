package tl.pojul.com.fastim.View.Adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.widget.GridLayoutManager;
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

import com.google.gson.Gson;
import com.pojul.fastIM.entity.Pic;
import com.pojul.fastIM.entity.User;
import com.pojul.fastIM.message.chat.ChatMessage;
import com.pojul.fastIM.message.chat.ReplyMessage;
import com.pojul.fastIM.message.chat.TagCommuMessage;
import com.pojul.fastIM.message.request.CommunThumbReq;
import com.pojul.objectsocket.message.ResponseMessage;
import com.pojul.objectsocket.socket.SocketRequest;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import tl.pojul.com.fastim.MyApplication;
import tl.pojul.com.fastim.R;
import tl.pojul.com.fastim.View.activity.BaseActivity;
import tl.pojul.com.fastim.View.activity.GalleryActivity;
import tl.pojul.com.fastim.View.activity.MyPageActivity;
import tl.pojul.com.fastim.View.activity.PrivateReplyActivity;
import tl.pojul.com.fastim.View.activity.TagReplyActivity;
import tl.pojul.com.fastim.View.widget.JustifyTextView;
import tl.pojul.com.fastim.View.widget.PolygonImage.view.PolygonImageView;
import tl.pojul.com.fastim.View.widget.TransitImage.TransitImageView;
import tl.pojul.com.fastim.util.ArrayUtil;
import tl.pojul.com.fastim.util.DateUtil;
import tl.pojul.com.fastim.util.DialogUtil;
import tl.pojul.com.fastim.util.GlideUtil;
import tl.pojul.com.fastim.util.MyDistanceUtil;
import tl.pojul.com.fastim.util.NumberUtil;
import tl.pojul.com.fastim.util.SPUtil;

public class TagMessAdapter extends RecyclerView.Adapter<TagMessAdapter.ViewHolder> {

    private Context mContext;
    private List<TagCommuMessage> mList = new ArrayList<>();
    private User user;
    private static final int START_TAG_ACTIVITY = 121;
    private boolean showUserPhoto;

    public TagMessAdapter(Context mContext, List<TagCommuMessage> mList) {
        this.mContext = mContext;
        this.mList = mList;
        user = SPUtil.getInstance().getUser();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_tag_message_browse, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        bindTagMessage(holder, position);

    }

    @Override
    public int getItemCount() {
        return mList != null ? mList.size() : 0;
    }


    private void bindTagMessage(ViewHolder holder, int position) {
        TagCommuMessage tagMessage = mList.get(position);

        GlideUtil.setImageBitmapNoOptions(tagMessage.getPhoto().getFilePath(), holder.photo);
        holder.photo.setOnClickListener(v->{
            Bundle bundle = new Bundle();
            bundle.putString("userName", tagMessage.getFrom());
            ((BaseActivity)mContext).startActivity(MyPageActivity.class, bundle);
        });
        holder.nickName.setText(tagMessage.getNickName());
        holder.age.setText((tagMessage.getAge() + "岁"));
        holder.distance.setText(MyDistanceUtil.getDisttanceStr(tagMessage.getDistance()));
        holder.sex.setImageResource(tagMessage.getUserSex() == 0?R.drawable.woman:R.drawable.man);

        holder.time.setText(DateUtil.getHeadway(tagMessage.getTimeMill()));
        if (tagMessage.getHasReport() == 0) {
            holder.reportTv.setText("举报");
            holder.reportTv.setSelected(false);
            holder.reportIv.setSelected(false);
            holder.reportRl.setOnClickListener(v -> {
                DialogUtil.getInstance().showReportDialog(mContext, tagMessage);
                DialogUtil.getInstance().setDialogClick(str -> {
                    if ("report_sucesses".equals(str)) {
                        tagMessage.setHasReport(1);
                        holder.reportTv.setText("已举报");
                        holder.reportRl.setOnClickListener(null);
                        holder.reportTv.setSelected(true);
                        holder.reportIv.setSelected(true);
                    }
                });
            });
        } else {
            holder.reportTv.setText("已举报");
            holder.reportRl.setOnClickListener(null);
            holder.reportTv.setSelected(true);
            holder.reportIv.setSelected(true);
        }
        holder.thumbUpTv.setText(NumberUtil.getNumStr(tagMessage.getThumbsUps()));
        if (tagMessage.isThumbsUping) {
            holder.thumbProgress.setVisibility(View.VISIBLE);
        } else {
            holder.thumbProgress.setVisibility(View.GONE);
        }
        if (tagMessage.getHsaThumbsUp() == 0) {
            holder.thumbUpTv.setSelected(false);
            holder.thumbUpIv.setSelected(false);
            holder.thumbUpRl.setOnClickListener(v -> {
                if (tagMessage.isThumbsUping) {
                    return;
                }
                tagMessage.isThumbsUping = true;
                holder.thumbProgress.setVisibility(View.VISIBLE);
                requestCommuThumbUp(holder.getAdapterPosition());
            });
        } else {
            holder.thumbUpTv.setSelected(true);
            holder.thumbUpIv.setSelected(true);
            holder.thumbUpRl.setOnClickListener(null);
        }
        holder.replyRl.setOnClickListener(v -> {
            holder.replyIv.performClick();
            holder.replyNumTv.performClick();
            Bundle bundle = new Bundle();
            if(tagMessage.getMessagePrivate() == 0 || user.getUserName().equals(tagMessage.getFrom())){
                bundle.putString("TagCommuMessage", new Gson().toJson(tagMessage));
                ((BaseActivity) mContext).startActivityForResult(TagReplyActivity.class, bundle, START_TAG_ACTIVITY);
            }else{
                bundle.putInt("chat_room_type", 4);
                bundle.putString("friend_user_name", tagMessage.getFrom());
                bundle.putString("tag_mess_uid", tagMessage.getMessageUid());
                bundle.putString("tag_mess_title", tagMessage.getTitle());
                ((BaseActivity) mContext).startActivity(PrivateReplyActivity.class, bundle);
            }
        });
        if(tagMessage.getMessagePrivate() == 0 || user.getUserName().equals(tagMessage.getFrom())){
            holder.replyNumTv.setText((tagMessage.getReplysNum() + ""));
        }else{
            holder.replyNumTv.setText("私信");
        }

        if (tagMessage.getTitle() == null || tagMessage.getTitle().isEmpty()) {
            holder.title.setText("");
        } else {
            holder.title.setText(tagMessage.getTitle());
        }
        if (tagMessage.getPics() == null || tagMessage.getPics().size() <= 0) {
            holder.pics.setVisibility(View.GONE);
            holder.img.setVisibility(View.GONE);
            holder.gallerLl.setVisibility(View.GONE);
        } else {
            holder.gallerLl.setVisibility(View.VISIBLE);
            holder.galleryTv.setText(tagMessage.getPics().size() + "张");
            holder.gallerLl.setOnClickListener(v->{
                holder.galleryIv.performClick();
                holder.galleryTv.performClick();
                Bundle bundle = new Bundle();
                bundle.putStringArrayList("urls", ArrayUtil.getUrls(tagMessage.getPics()));
                ((BaseActivity)mContext).startActivity(GalleryActivity.class, bundle);
            });
            if (tagMessage.getPics().size() == 1) {
                holder.pics.setVisibility(View.GONE);
                holder.img.setVisibility(View.VISIBLE);
                GlideUtil.setImageBitmap(tagMessage.getPics().get(0).getUploadPicUrl().getFilePath(), holder.img, 0.5f);
            } else {
                holder.pics.setVisibility(View.VISIBLE);
                holder.img.setVisibility(View.GONE);
                if (tagMessage.getPics().size() <= 5) {
                    holder.pics.setLayoutManager(new GridLayoutManager(mContext, tagMessage.getPics().size()));
                    holder.pics.setAdapter(new TagPicAdapter(mContext, tagMessage.getPics(), false));
                } else if (tagMessage.getPics().size() > 5) {
                    holder.pics.setLayoutManager(new GridLayoutManager(mContext, 5));
                    List<Pic> pics = new ArrayList<>();
                    for (int i = 0; i < 5; i++) {
                        pics.add(tagMessage.getPics().get(i));
                    }
                    holder.pics.setAdapter(new TagPicAdapter(mContext, pics, false));
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
        if (tagMessage.getReplys() == null || tagMessage.getReplys().size() <= 0) {
            holder.replys.setVisibility(View.GONE);
        } else {
            ReplyMessage replyMessage = tagMessage.getReplys().get(0);
            holder.replys.setVisibility(View.VISIBLE);
            holder.reply1.setText((replyMessage.getNickName() + "："));
            holder.reply1Text.setText(replyMessage.getText());
        }

        holder.detail.setOnClickListener(v->{
            Bundle bundle = new Bundle();
            bundle.putString("TagCommuMessage", new Gson().toJson(tagMessage));
            ((BaseActivity) mContext).startActivityForResult(TagReplyActivity.class, bundle, START_TAG_ACTIVITY);
        });
    }

    private void requestCommuThumbUp(int position) {
        TagCommuMessage tagCommuMessage = mList.get(position);
        CommunThumbReq communThumbReq = new CommunThumbReq();
        communThumbReq.setCommunMessageUid(tagCommuMessage.getMessageUid());
        new SocketRequest().request(MyApplication.ClientSocket, communThumbReq, new SocketRequest.IRequest() {
            @Override
            public void onError(String msg) {
                new Handler(Looper.getMainLooper()).post(() -> {
                    tagCommuMessage.setThumbsUping(false);
                    Toast.makeText(mContext, "点赞失败", Toast.LENGTH_SHORT).show();
                });
            }

            @Override
            public void onFinished(ResponseMessage mResponse) {
                new Handler(Looper.getMainLooper()).post(() -> {
                    tagCommuMessage.setThumbsUping(false);
                    if (mResponse.getCode() == 200) {
                        tagCommuMessage.setThumbsUps((tagCommuMessage.getThumbsUps() + 1));
                        tagCommuMessage.setHsaThumbsUp(1);
                        notifyItemChanged(position);
                    } else {
                        Toast.makeText(mContext, "点赞失败", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == START_TAG_ACTIVITY && resultCode == Activity.RESULT_OK) {
            String tagMessUid = data.getStringExtra("tagMessUid");
            int isEffictive = data.getIntExtra("isEffictive", 1);
            synchronized (mList) {
                for (int i = 0; i < mList.size(); i++) {
                    ChatMessage chatMessage = mList.get(i);
                    if (chatMessage instanceof TagCommuMessage && chatMessage.getMessageUid().equals(tagMessUid)) {
                        ((TagCommuMessage) chatMessage).setIsEffective(isEffictive);
                        notifyItemChanged(i);
                        break;
                    }
                }
            }
        }
    }

    public void addDatas(List<TagCommuMessage> tagCommuMessages){
        if(tagCommuMessages == null || tagCommuMessages.size() <= 0){
            return;
        }
        synchronized (mList){
            int position = mList.size();
            mList.addAll(tagCommuMessages);
            notifyItemRangeInserted(position, tagCommuMessages.size());
        }
    }

    public void clearData() {
        synchronized (mList){
            mList.clear();
            notifyDataSetChanged();
        }
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.photo)
        PolygonImageView photo;
        @BindView(R.id.nick_name)
        TextView nickName;
        @BindView(R.id.age)
        TextView age;
        @BindView(R.id.distance)
        TextView distance;
        @BindView(R.id.sex)
        ImageView sex;
        @BindView(R.id.user_simple_rl)
        RelativeLayout userSimpleRl;
        @BindView(R.id.time)
        TextView time;
        @BindView(R.id.detail)
        TextView detail;
        @BindView(R.id.is_effective)
        TextView isEffective;
        @BindView(R.id.gallery_ll)
        LinearLayout gallerLl;
        @BindView(R.id.gallery_iv)
        ImageView galleryIv;
        @BindView(R.id.gallery_tv)
        TextView galleryTv;
        @BindView(R.id.header)
        RelativeLayout header;
        @BindView(R.id.line1)
        View line1;
        @BindView(R.id.pics)
        RecyclerView pics;
        @BindView(R.id.img)
        TransitImageView img;
        @BindView(R.id.title)
        TextView title;
        @BindView(R.id.note)
        JustifyTextView note;
        @BindView(R.id.reply1)
        TextView reply1;
        @BindView(R.id.reply1_text)
        TextView reply1Text;
        @BindView(R.id.replys)
        LinearLayout replys;
        @BindView(R.id.line3)
        View line3;
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
        @BindView(R.id.thumb_progress)
        ProgressBar thumbProgress;
        @BindView(R.id.rl_message)
        RelativeLayout rlMessage;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public boolean isShowUserPhoto() {
        return showUserPhoto;
    }

    public void setShowUserPhoto(boolean showUserPhoto) {
        this.showUserPhoto = showUserPhoto;
    }
}
