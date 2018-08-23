package tl.pojul.com.fastim.View.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.pojul.fastIM.message.chat.SubReplyMessage;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import tl.pojul.com.fastim.R;
import tl.pojul.com.fastim.util.DateUtil;

public class SubReplyAdapter extends RecyclerView.Adapter<SubReplyAdapter.MyViewHolder> {

    private Context mContext;
    private List<SubReplyMessage> mList;

    public SubReplyAdapter(Context mContext, List<SubReplyMessage> mList) {
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
        SubReplyMessage subReplyMessage = mList.get(position);
        holder.subNickName.setText((subReplyMessage.getNickName() + "："));
        holder.subContent.setText(subReplyMessage.getText());
        holder.subReplyTime.setText(DateUtil.getHeadway(subReplyMessage.getTimeMilli()));
        if (subReplyMessage.getIsSend() == 0) {
            holder.subProgress.setVisibility(View.VISIBLE);
        } else if (subReplyMessage.getIsSend() == 1) {
            holder.subProgress.setVisibility(View.GONE);
        } else {
            //发送失败图标
        }
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
