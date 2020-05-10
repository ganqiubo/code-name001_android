package tl.pojul.com.fastim.View.Adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
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

import com.bumptech.glide.Glide;
import com.pojul.fastIM.entity.Pic;
import com.pojul.fastIM.entity.PicComment;
import com.pojul.fastIM.entity.User;
import com.pojul.fastIM.message.chat.ReplyMessage;
import com.pojul.fastIM.message.chat.SubReplyMessage;
import com.pojul.fastIM.message.chat.TagCommuMessage;
import com.pojul.fastIM.message.request.CommentThumbupReq;
import com.pojul.fastIM.message.request.PicCommentReq;
import com.pojul.objectsocket.message.ResponseMessage;
import com.pojul.objectsocket.socket.SocketRequest;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import tl.pojul.com.fastim.MyApplication;
import tl.pojul.com.fastim.R;
import tl.pojul.com.fastim.View.activity.BaseActivity;
import tl.pojul.com.fastim.View.activity.MoreSubReplyActivity;
import tl.pojul.com.fastim.View.activity.MyPageActivity;
import tl.pojul.com.fastim.View.activity.PicCommentActivity;
import tl.pojul.com.fastim.View.activity.TagReplyActivity;
import tl.pojul.com.fastim.View.widget.JustifyTextView;
import tl.pojul.com.fastim.View.widget.PolygonImage.view.PolygonImageView;
import tl.pojul.com.fastim.View.widget.sowingmap.SowingMap;
import tl.pojul.com.fastim.socket.Converter.SubReplyConverter;
import tl.pojul.com.fastim.util.DateUtil;
import tl.pojul.com.fastim.util.DialogUtil;
import tl.pojul.com.fastim.util.GlideUtil;
import tl.pojul.com.fastim.util.SPUtil;

public class PicReplyAdapter extends RecyclerView.Adapter<PicReplyAdapter.ReplysViewHolder> {

    private Context mContext;
    private List<PicComment> mList;

    public PicReplyAdapter(Context mContext, List<PicComment> mList) {
        this.mList = mList;
        this.mContext = mContext;
    }

    @Override
    public ReplysViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_reply, parent, false);
        return new ReplysViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ReplysViewHolder holder, int position) {
        bindReplys(holder, position);
    }

    @Override
    public int getItemCount() {
        return mList != null ? mList.size() : 0;
    }


    private void bindReplys(ReplysViewHolder holder, int position) {
        PicComment picComment = mList.get(position);
        holder.line.setBackgroundColor(Color.WHITE);
        Glide.with(mContext).load(picComment.getPhoto()).into(holder.replyPhoto);
        holder.replyPhoto.setOnClickListener(v->{
            Bundle bundle = new Bundle();
            bundle.putString("userName", picComment.getCommentUserName());
            ((BaseActivity)mContext).startActivity(MyPageActivity.class, bundle);
        });
        holder.replyNickName.setTextColor(Color.parseColor("#454545"));
        holder.replyNickName.setText(picComment.getNickName());
        holder.replyCertificate.setText(DateUtil.getHeadway(picComment.getTimeMilli()));
        holder.replySex.setImageResource(picComment.getSex() == 0 ? R.drawable.woman : R.drawable.man);
        holder.replyTime.setVisibility(View.GONE);
        holder.replyThumbNum.setVisibility(View.VISIBLE);
        holder.replyThumbUp.setVisibility(View.VISIBLE);
        if(picComment.getHasThumbUp() == 0){
            holder.replyThumbNum.setSelected(false);
            holder.replyThumbUp.setSelected(false);
            holder.replyThumbUp.setOnClickListener(v->{
                thumpUpComment(picComment, SPUtil.getInstance().getUser().getUserName(), position);
            });
        }else{
            holder.replyThumbNum.setSelected(true);
            holder.replyThumbUp.setSelected(true);
            holder.replyThumbUp.setOnClickListener(null);
        }
        holder.replyThumbNum.setText((picComment.getThumpups() + ""));
        holder.replyTwice.setOnClickListener(v->{
            comment(picComment);
        });
        holder.replyContent.setText(picComment.getCommentText());
        if (picComment.getSubComments() != null && picComment.getSubComments().size() > 0) {
            holder.replyTwices.setVisibility(View.VISIBLE);
            holder.replyTwices.setLayoutManager(new LinearLayoutManager(mContext));
            SubPicCommentsAdapter subAdapter = new SubPicCommentsAdapter(mContext, picComment.getSubComments());
            holder.replyTwices.setAdapter(subAdapter);
        } else {
            holder.replyTwices.setVisibility(View.GONE);
        }
    }

    private void thumpUpComment(PicComment picComment, String userName, int position) {
        CommentThumbupReq req= new CommentThumbupReq();
        req.setUserName(userName);
        req.setCommentId(("" + picComment.getId()));
        DialogUtil.getInstance().showLoadingSimple(mContext, ((PicCommentActivity)mContext).getRootView());
        new SocketRequest().request(MyApplication.ClientSocket, req, new SocketRequest.IRequest() {
            @Override
            public void onError(String msg) {
                ((PicCommentActivity) mContext).runOnUiThread(()->{
                    DialogUtil.getInstance().dimissLoadingDialog();
                    Toast.makeText(mContext, "操作失败", Toast.LENGTH_SHORT).show();
                });
            }

            @Override
            public void onFinished(ResponseMessage mResponse) {
                ((PicCommentActivity) mContext).runOnUiThread(()->{
                    if (mResponse.getCode() == 200){
                        picComment.setThumpups((picComment.getThumpups() + 1));
                        picComment.setHasThumbUp(1);
                        notifyItemChanged(position);
                    }else{
                        Toast.makeText(mContext, "操作失败", Toast.LENGTH_SHORT).show();
                    }
                    DialogUtil.getInstance().dimissLoadingDialog();
                });
            }
        });
    }

    private void comment(PicComment picComment) {
        ((PicCommentActivity)mContext).comment(1, picComment.getId());
    }

    public void addData(List<PicComment> picComments) {
        synchronized (mList){
            mList.addAll(picComments);
            notifyDataSetChanged();
        }
    }

    public void addTopData(PicComment picComment) {
        synchronized (mList){
            mList.add(0, picComment);
            notifyDataSetChanged();
        }
    }

    public void addSubCommentData(PicComment subComment) {
        synchronized (mList){
            for (int i = 0; i < mList.size(); i++) {
                PicComment picComment = mList.get(i);
                if(picComment.getId() == subComment.getOneLevelId()){
                    if(picComment.getSubComments() == null){
                        List<PicComment> subComments = new ArrayList<>();
                        subComments.add(subComment);
                        picComment.setSubComments(subComments);
                    }else{
                        picComment.getSubComments().add(0, subComment);
                    }
                    notifyItemChanged(i);
                    return;
                }
            }
        }
    }

    public void clearData() {
        synchronized (mList){
            mList.clear();
            notifyDataSetChanged();
        }
    }

    static class ReplysViewHolder extends RecyclerView.ViewHolder {
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
        @BindView(R.id.line)
        View line;

        public ReplysViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
