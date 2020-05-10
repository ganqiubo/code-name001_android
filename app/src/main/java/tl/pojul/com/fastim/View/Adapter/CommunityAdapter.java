package tl.pojul.com.fastim.View.Adapter;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.pojul.fastIM.entity.CommunityMessEntity;
import com.pojul.fastIM.entity.CommunityRoom;
import com.pojul.fastIM.message.chat.TagCommuMessage;
import com.pojul.objectsocket.utils.LogUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import butterknife.BindView;
import butterknife.ButterKnife;
import tl.pojul.com.fastim.R;
import tl.pojul.com.fastim.View.activity.BaseActivity;
import tl.pojul.com.fastim.View.activity.CommuDeatilActivity;
import tl.pojul.com.fastim.View.activity.CommunityChatActivity;
import tl.pojul.com.fastim.View.widget.PolygonImage.view.PolygonImageView;
import tl.pojul.com.fastim.View.widget.marqueeview.SimpleMF;
import tl.pojul.com.fastim.View.widget.marqueeview.SimpleMarqueeView;
import tl.pojul.com.fastim.util.MyDistanceUtil;
import tl.pojul.com.fastim.util.RandomUtil;

public class CommunityAdapter extends RecyclerView.Adapter<CommunityAdapter.MyViewHolder> {

    private Context mContext;
    private List<CommunityRoom> mList;
    private OnItemClickListener mOnItemClickListener;
    private HashMap<String, List<String>> topmesses;

    private HashMap<String, Integer> communityPhotos = new HashMap<>();

    public CommunityAdapter(Context mContext, List<CommunityRoom> mList) {
        this.mContext = mContext;
        this.mList = mList;
        communityPhotos.put("省", R.drawable.province);
        communityPhotos.put("区/县", R.drawable.district);
        communityPhotos.put("市", R.drawable.city);
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

        List<String> topMess;
        if(topmesses == null || topmesses.size() <= 0
                || topmesses.get(communityRoom.getCommunityUid()) == null || topmesses.get(communityRoom.getCommunityUid()).size() <= 0){
            topMess = new ArrayList<String>(){{add("暂无置顶消息");}};
        }else{
            topMess = topmesses.get(communityRoom.getCommunityUid());
        }

        List<SpannableString> topMessSpan = new ArrayList<>();
        for (int i = 0; i < topMess.size(); i++) {
            SpannableString spannableString = new SpannableString(topMess.get(i));
            int split = topMess.get(i).indexOf(":");
            if(split == -1){
                topMessSpan.add(spannableString);
                continue;
            }
            ForegroundColorSpan colorSpan = new ForegroundColorSpan(Color.parseColor("#1296db"));
            spannableString.setSpan(colorSpan, 0, split, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
            topMessSpan.add(spannableString);
        }
        SimpleMF<SpannableString> marqueeFactory = new SimpleMF(mContext);
        marqueeFactory.setData(topMessSpan);
        //marqueeFactory.setData(new ArrayList<String>(){{add("说的是几乎都是巍峨u挖卡卡卡是");add("是觉得合适的时间毫无");}});
        holder.communityTopMessage.setMarqueeFactory(marqueeFactory);
        if(topMess.size() <= 1){
            holder.communityTopMessage.stopFlipping();
        }else{
            holder.communityTopMessage.startFlipping();
            holder.communityTopMessage.setFlipInterval(RandomUtil.getRandomRange(6000, 15 * 1000));
        }
        holder.communityPhoto.setOnClickListener(v->{
            Bundle bundle = new Bundle();
            bundle.putString("commun_room", new Gson().toJson(communityRoom));
            ((BaseActivity)mContext).startActivity(CommuDeatilActivity.class, bundle);
        });
        if(communityRoom.getPhoto() != null &&
                 !communityRoom.getPhoto().isEmpty()){
            Glide.with(mContext).load(communityRoom.getPhoto()).into(holder.communityPhoto);
        }else{
            Glide.with(mContext).load(R.drawable.photo_default).into(holder.communityPhoto);
        }
        if(communityRoom.getFollows() > 0){
            holder.follows.setVisibility(View.VISIBLE);
            holder.followsIv.setVisibility(View.VISIBLE);
            holder.follows.setText(("" + communityRoom.getFollows()));
        }else{
            holder.follows.setVisibility(View.GONE);
            holder.followsIv.setVisibility(View.GONE);
        }
        /*if(communityPhotos.get(communityRoom.getCommunitySubtype()) != null){
            holder.communityPhoto.setImageResource(communityPhotos.get(communityRoom.getCommunitySubtype()));
        }else{
            holder.communityPhoto.setImageResource(R.drawable.community_normal);
        }*/
    }

    @Override
    public int getItemCount() {
        return mList != null ? mList.size() : 0;
    }

    public void setBasewInfo(List<CommunityRoom> communityRooms) {
        if(communityRooms == null){
            return;
        }
        for (int i = 0; i < communityRooms.size(); i++) {
            setItemBaseInfo(communityRooms.get(i));
        }
    }

    private void setItemBaseInfo(CommunityRoom communityRoom) {
        synchronized (mList){
            for (int i = 0; i < mList.size(); i++) {
                CommunityRoom rawCommunityRoom = mList.get(i);
                if(rawCommunityRoom.getCommunityUid().equals(communityRoom.getCommunityUid())){
                    rawCommunityRoom.setFollows(communityRoom.getFollows());
                    rawCommunityRoom.setPhoto(communityRoom.getPhoto());
                    break;
                }
            }
        }
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.community_photo)
        PolygonImageView communityPhoto;
        @BindView(R.id.community_name)
        TextView communityName;
        @BindView(R.id.community_top_message)
        SimpleMarqueeView communityTopMessage;
        @BindView(R.id.community_distance)
        TextView communityDistance;
        @BindView(R.id.community_type)
        TextView communityType;
        @BindView(R.id.item_rl)
        RelativeLayout itemRl;
        @BindView(R.id.item_community_ll)
        LinearLayout itemCommunityLl;
        @BindView(R.id.follows_iv)
        ImageView followsIv;
        @BindView(R.id.follows)
        TextView follows;

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
        LogUtil.e(communityRoom.getName()+"<----addCommunity--->");
        synchronized (mList) {
            for (int i = 0; i < mList.size(); i++) {
                CommunityRoom community = mList.get(i);
                if (community.getName().equals(communityRoom.getName())) {
                    return;
                }
            }
            LogUtil.e(communityRoom.getName()+"<----addCommunity111--->");
            mList.add(communityRoom);
            Collections.sort(mList);
            notifyDataSetChanged();
        }
    }

    public List<String> getCommunityUids() {
        List<String> communityUids = new ArrayList<>();
        for (int i =0; i < mList.size(); i ++){
            CommunityRoom communityRoom = mList.get(i);
            if(communityRoom == null || communityRoom.getCommunityUid() == null){
                return communityUids;
            }
            communityUids.add(communityRoom.getCommunityUid());
        }
        return communityUids;
    }

    public void refreshTops(HashMap<String, List<String>> topmesses) {
        this.topmesses = topmesses;
        notifyDataSetChanged();
    }

}
