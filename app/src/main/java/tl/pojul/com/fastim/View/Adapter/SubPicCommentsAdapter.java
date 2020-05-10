package tl.pojul.com.fastim.View.Adapter;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.pojul.fastIM.entity.PicComment;
import com.pojul.fastIM.message.chat.PicCommuMessage;
import com.pojul.fastIM.message.chat.SubReplyMessage;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import tl.pojul.com.fastim.R;
import tl.pojul.com.fastim.View.activity.BaseActivity;
import tl.pojul.com.fastim.View.activity.MyPageActivity;
import tl.pojul.com.fastim.util.DateUtil;

public class SubPicCommentsAdapter extends RecyclerView.Adapter<SubPicCommentsAdapter.MyViewHolder> {

    private Context mContext;
    private List<PicComment> mList;

    public SubPicCommentsAdapter(Context mContext, List<PicComment> mList) {
        this.mContext = mContext;
        this.mList = mList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_reply_sub, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        PicComment picComment = mList.get(position);
        holder.subNickName.setText((picComment.getNickName() + "："));
        holder.subNickName.setOnClickListener(v->{
            Bundle bundle = new Bundle();
            bundle.putString("userName", picComment.getCommentUserName());
            ((BaseActivity)mContext).startActivity(MyPageActivity.class, bundle);
        });
        holder.subContent.setText(picComment.getCommentText());
        holder.subReplyTime.setText(DateUtil.getHeadway(picComment.getTimeMilli()));
        holder.subProgress.setVisibility(View.GONE);
    }

    @Override
    public int getItemCount() {
        return mList != null ? mList.size() : 0;
    }

    static class MyViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.sub_nick_name)
        TextView subNickName;
        @BindView(R.id.sub_content)
        TextView subContent;
        @BindView(R.id.sub_reply_time)
        TextView subReplyTime;
        @BindView(R.id.sub_progress)
        ProgressBar subProgress;

        public MyViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

}
