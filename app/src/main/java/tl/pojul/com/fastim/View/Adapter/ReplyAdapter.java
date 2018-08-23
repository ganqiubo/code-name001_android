package tl.pojul.com.fastim.View.Adapter;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.pojul.fastIM.entity.BaseEntity;
import com.pojul.fastIM.entity.User;
import com.pojul.fastIM.message.chat.ReplyMessage;
import com.pojul.fastIM.message.chat.SubReplyMessage;
import com.pojul.fastIM.message.chat.TagCommuMessage;
import com.pojul.objectsocket.message.BaseMessage;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import tl.pojul.com.fastim.R;
import tl.pojul.com.fastim.View.activity.BaseActivity;
import tl.pojul.com.fastim.View.activity.MoreSubReplyActivity;
import tl.pojul.com.fastim.View.activity.TagReplyActivity;
import tl.pojul.com.fastim.View.widget.JustifyTextView;
import tl.pojul.com.fastim.View.widget.PolygonImage.view.PolygonImageView;
import tl.pojul.com.fastim.View.widget.sowingmap.SowingMap;
import tl.pojul.com.fastim.socket.Converter.SubReplyConverter;
import tl.pojul.com.fastim.util.DateUtil;
import tl.pojul.com.fastim.util.DialogUtil;
import tl.pojul.com.fastim.util.GlideUtil;

public class ReplyAdapter extends RecyclerView.Adapter<ReplyAdapter.BaseViewHolder> {

    private Context mContext;
    private List<ReplyMessage> mList;
    private User user;

    private TagCommuMessage tagCommuMessage;
    private static final int HEADER = 1;
    private static final int CONTENT = 2;
    private List<String> picPaths = new ArrayList<>();

    private SowingMap sowingMap;


    public ReplyAdapter(Context mContext, List<ReplyMessage> mList, User user, TagCommuMessage tagCommuMessage) {
        this.mList = new ArrayList<>();
        this.mList.add(new ReplyMessage());
        this.mList.addAll(mList);
        this.mContext = mContext;
        this.user = user;
        this.tagCommuMessage = tagCommuMessage;
    }

    @Override
    public BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == HEADER) {
            View view = LayoutInflater.from(mContext).inflate(R.layout.item_tag_reply_header, parent, false);
            return new HeaderViewHolder(view);
        } else {
            View view = LayoutInflater.from(mContext).inflate(R.layout.item_reply, parent, false);
            return new ReplysViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(BaseViewHolder holder, int position) {
        int type = getItemViewType(position);
        if (type == HEADER) {
            if (sowingMap == null) {
                bindHeader((HeaderViewHolder) holder, position);
            }
        } else {
            bindReplys((ReplysViewHolder) holder, position);
        }
    }

    @Override
    public int getItemCount() {
        return mList != null ? mList.size() : 0;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return HEADER;
        } else {
            return CONTENT;
        }
    }

    private void bindHeader(HeaderViewHolder holder, int position) {
        sowingMap = holder.messagePics;
        if (tagCommuMessage.getTitle() == null || "".equals(tagCommuMessage.getTitle())) {
            holder.title.setVisibility(View.GONE);
        } else {
            holder.title.setText(tagCommuMessage.getTitle());
        }
        if (tagCommuMessage.getPics() == null || tagCommuMessage.getPics().size() <= 0) {
            holder.messagePics.setVisibility(View.GONE);
        } else {
            for (int i = 0; i < tagCommuMessage.getPics().size(); i++) {
                String path = tagCommuMessage.getPics().get(i).getUploadPicUrl().getFilePath();
                if (path == null || "".equals(path)) {
                    continue;
                }
                picPaths.add(path);
            }
            holder.messagePics.setImgs(picPaths);
            holder.messagePics.startLoop();
        }
        if (tagCommuMessage.getContent() == null || tagCommuMessage.getText().isEmpty()) {
            holder.messageContent.setVisibility(View.GONE);
        } else {
            holder.messageContent.setText(("\u3000\u3000" + tagCommuMessage.getText()));
        }
    }

    private void bindReplys(ReplysViewHolder holder, int position) {
        ReplyMessage replyMessage = mList.get(position);
        GlideUtil.setImageBitmap(replyMessage.getPhoto().getFilePath(), holder.replyPhoto, 0.5f);
        holder.replyNickName.setText(replyMessage.getNickName());
        if (replyMessage.getCertificate() == 0) {
            holder.replyCertificate.setText("未实名认证");
        } else {
            holder.replyCertificate.setText("已实名认证");
        }
        if (replyMessage.getUserSex() == 0) {
            holder.replySex.setImageResource(R.drawable.woman);
        } else {
            holder.replySex.setImageResource(R.drawable.man);
        }
        holder.replyTime.setText(DateUtil.getHeadway(replyMessage.getTimeMilli()));
        if (replyMessage.getHasThumbUp() == 0) {
            holder.replyThumbUp.setSelected(false);
            holder.replyThumbNum.setSelected(false);
            holder.replyThumbUp.setOnClickListener(view -> {

            });
        } else {
            holder.replyThumbUp.setSelected(true);
            holder.replyThumbNum.setSelected(true);
            holder.replyThumbUp.setOnClickListener(null);
        }
        holder.replyThumbNum.setText(("" + replyMessage.getHasThumbUp()));
        holder.replyContent.setText(replyMessage.getText());
        if (replyMessage.getSubReplys() == null) {
            replyMessage.setSubReplys(new SubReplyConverter().converter(replyMessage, replyMessage.getSubReplyStrs()));
        }
        if (replyMessage.getSubReplys() != null && replyMessage.getSubReplys().size() > 0) {
            holder.replyTwices.setVisibility(View.VISIBLE);
            holder.replyTwices.setLayoutManager(new LinearLayoutManager(mContext));
            SubReplyAdapter subReplyAdapter = new SubReplyAdapter(mContext, replyMessage.getSubReplys());
            holder.replyTwices.setAdapter(subReplyAdapter);
        } else {
            holder.replyTwices.setVisibility(View.GONE);
        }
        if (replyMessage.getIsSend() == 0) {
            holder.progressBar1.setVisibility(View.VISIBLE);
        } else if (replyMessage.getIsSend() == 1) {
            holder.progressBar1.setVisibility(View.GONE);
        } else {
            //发送失败图标
        }
        holder.replyTwice.setOnClickListener(v -> {
            DialogUtil.getInstance().showSubReplyDialog(mContext, ((TagReplyActivity) mContext).getRootView(), replyMessage.getNickName());
            DialogUtil.getInstance().setDialogClick(str -> {
                ((TagReplyActivity) mContext).sendSubReplyMessage(str, replyMessage.getMessageUid());
            });
        });
        if(replyMessage.isHasMoreSubReply()){
            holder.moreSubreply.setVisibility(View.VISIBLE);
            holder.moreSubreply.setOnClickListener(v->{
                Bundle bundle = new Bundle();
                bundle.putString("replyMessUid", replyMessage.getMessageUid());
                ((BaseActivity)mContext).startActivity(MoreSubReplyActivity.class, bundle);
            });
        }else{
            holder.moreSubreply.setVisibility(View.GONE);
            holder.moreSubreply.setOnClickListener(null);
        }
    }


    public void addData(ReplyMessage replyMessage) {
        synchronized (mList) {
            if (replyMessage != null) {
                mList.add(1, replyMessage);
                this.notifyItemInserted(1);
            }
        }
    }

    public void addData(List<ReplyMessage> replyMessages) {
        synchronized (mList) {
            if (replyMessages != null) {
                mList.addAll(1, replyMessages);
                this.notifyItemRangeInserted(1, replyMessages.size());
            }
        }
    }

    public void addMoreData(List<ReplyMessage> replyMessages) {
        synchronized (mList) {
            if (replyMessages != null) {
                mList.addAll(replyMessages);
                this.notifyItemRangeInserted(mList.size(), replyMessages.size());
            }
        }
    }

    public void onDestroy() {
        if (sowingMap != null) {
            sowingMap.onDestory();
        }
    }

    public void onSendFinish(ReplyMessage message) {
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

    public void onSendFail(ReplyMessage message) {
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

    public void addSubData(SubReplyMessage subReplyMessage) {
        synchronized (mList) {
            for (int i = (mList.size() - 1); i >= 0; i--) {
                if (subReplyMessage.getReplyMessageUid() != null
                        && subReplyMessage.getReplyMessageUid().equals(mList.get(i).getMessageUid())) {
                    if (mList.get(i).getSubReplys() == null) {
                        List<SubReplyMessage> subReplys = new ArrayList<>();
                        mList.get(i).setSubReplys(subReplys);
                    }
                    mList.get(i).getSubReplys().add(subReplyMessage);
                    notifyItemChanged(i);
                    break;
                }
            }
        }
    }

    public void onSubSendFinish(SubReplyMessage message) {
        synchronized (mList) {
            for (int i = (mList.size() - 1); i >= 0; i--) {
                if (message.getReplyMessageUid() != null && message.getReplyMessageUid().equals(mList.get(i).getMessageUid())) {
                    notifyItemChanged(i);
                    break;
                }
            }
        }
    }

    static class BaseViewHolder extends RecyclerView.ViewHolder {

        public BaseViewHolder(View itemView) {
            super(itemView);
        }
    }

    static class HeaderViewHolder extends BaseViewHolder {

        @BindView(R.id.title)
        TextView title;
        @BindView(R.id.message_pics)
        SowingMap messagePics;
        @BindView(R.id.message_content)
        JustifyTextView messageContent;

        public HeaderViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    static class ReplysViewHolder extends BaseViewHolder {
        @BindView(R.id.reply_photo)
        PolygonImageView replyPhoto;
        @BindView(R.id.reply_nick_name)
        TextView replyNickName;
        @BindView(R.id.reply_certificate)
        TextView replyCertificate;
        @BindView(R.id.reply_sex)
        ImageView replySex;
        @BindView(R.id.reply_time)
        TextView replyTime;
        @BindView(R.id.reply_twice)
        ImageView replyTwice;
        @BindView(R.id.reply_thumb_up)
        ImageView replyThumbUp;
        @BindView(R.id.reply_thumb_num)
        TextView replyThumbNum;
        @BindView(R.id.reply_content)
        TextView replyContent;
        @BindView(R.id.reply_twices)
        RecyclerView replyTwices;
        @BindView(R.id.progressBar1)
        ProgressBar progressBar1;
        @BindView(R.id.more_subreply)
        TextView moreSubreply;

        public ReplysViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
