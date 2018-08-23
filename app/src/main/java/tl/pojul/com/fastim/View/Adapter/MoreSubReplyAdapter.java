package tl.pojul.com.fastim.View.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.pojul.fastIM.entity.MoreSubReply;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import tl.pojul.com.fastim.R;
import tl.pojul.com.fastim.View.widget.PolygonImage.view.PolygonImageView;
import tl.pojul.com.fastim.util.DateUtil;
import tl.pojul.com.fastim.util.GlideUtil;

public class MoreSubReplyAdapter extends RecyclerView.Adapter<MoreSubReplyAdapter.MyViewHolder> {

    private Context mContext;
    private List<MoreSubReply> mList;

    public MoreSubReplyAdapter(Context mContext, List<MoreSubReply> mList) {
        this.mContext = mContext;
        this.mList = mList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_more_sub_reply, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        MoreSubReply moreSubReply = mList.get(position);
        GlideUtil.setImageBitmap(moreSubReply.getPhoto(), holder.subReplyPhoto);
        holder.subNickName.setText(moreSubReply.getNickName());
        if(moreSubReply.getCertificate() == 1){
            holder.subReplyCertificate.setText("已实名认证");
        }else{
            holder.subReplyCertificate.setText("未实名认证");
        }
        if(moreSubReply.getSex() == 0){
            holder.subReplySex.setImageResource(R.drawable.woman);
        }else{
            holder.subReplySex.setImageResource(R.drawable.man);
        }
        holder.replyTime.setText(DateUtil.getHeadway(moreSubReply.getTimeMilli()));
        holder.subReplyContent.setText(moreSubReply.getText());
    }

    @Override
    public int getItemCount() {
        return mList != null ? mList.size() : 0;
    }

    public void addData(List<MoreSubReply> tempMore) {
        synchronized (mList){
            mList.addAll(0, tempMore);
            notifyItemRangeInserted(0, tempMore.size());
        }
    }

    static class MyViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.sub_reply_photo)
        PolygonImageView subReplyPhoto;
        @BindView(R.id.sub_nick_name)
        TextView subNickName;
        @BindView(R.id.sub_reply_certificate)
        TextView subReplyCertificate;
        @BindView(R.id.sub_reply_sex)
        ImageView subReplySex;
        @BindView(R.id.reply_time)
        TextView replyTime;
        @BindView(R.id.sub_reply_content)
        TextView subReplyContent;

        public MyViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

}
