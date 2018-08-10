package tl.pojul.com.fastim.View.Adapter;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.pojul.fastIM.entity.CommunityRoom;

import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import tl.pojul.com.fastim.R;
import tl.pojul.com.fastim.View.activity.BaseActivity;
import tl.pojul.com.fastim.View.activity.CommunityChatActivity;
import tl.pojul.com.fastim.View.widget.PolygonImage.view.PolygonImageView;
import tl.pojul.com.fastim.util.MyDistanceUtil;

public class CommunityAdapter extends RecyclerView.Adapter<CommunityAdapter.MyViewHolder> {

    private Context mContext;
    private List<CommunityRoom> mList;
    private OnItemClickListener mOnItemClickListener;

    public CommunityAdapter(Context mContext, List<CommunityRoom> mList) {
        this.mContext = mContext;
        this.mList = mList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_community_list, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        CommunityRoom communityRoom = mList.get(position);
        holder.communityName.setText(communityRoom.getName());
        holder.communityDistance.setText("100m");
        holder.communityType.setText(communityRoom.getCommunitySubtype());
        holder.itemCommunityLl.setOnClickListener(v->{
            Bundle bundle = new Bundle();
            bundle.putString("CommunityRoom", new Gson().toJson(communityRoom));
            ((BaseActivity)mContext).startActivity(CommunityChatActivity.class, bundle);
        });
        holder.communityDistance.setText(MyDistanceUtil.getDisttanceStr(communityRoom.getDistance()));
    }

    @Override
    public int getItemCount() {
        return mList != null ? mList.size() : 0;
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.community_photo)
        PolygonImageView communityPhoto;
        @BindView(R.id.community_name)
        TextView communityName;
        @BindView(R.id.community_top_message)
        TextView communityTopMessage;
        @BindView(R.id.community_distance)
        TextView communityDistance;
        @BindView(R.id.community_type)
        TextView communityType;
        @BindView(R.id.item_rl)
        RelativeLayout itemRl;
        @BindView(R.id.item_community_ll)
        LinearLayout itemCommunityLl;

        public MyViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public List<CommunityRoom> getmList() {
        return mList;
    }

    public void setmList(List<CommunityRoom> mList) {
        this.mList = mList;
    }

    public interface OnItemClickListener {
        void onClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.mOnItemClickListener = onItemClickListener;
    }

    public void addCommunity(CommunityRoom communityRoom) {
        synchronized (mList) {
            for (int i = 0; i < mList.size(); i++) {
                CommunityRoom community = mList.get(i);
                if (community.getName().equals(communityRoom.getName())) {
                    return;
                }
            }
            mList.add(communityRoom);
            Collections.sort(mList);
            notifyDataSetChanged();
        }
    }

}
