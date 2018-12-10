package tl.pojul.com.fastim.View.Adapter;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.pojul.fastIM.entity.LocUser;
import java.util.List;
import butterknife.BindView;
import butterknife.ButterKnife;
import tl.pojul.com.fastim.R;
import tl.pojul.com.fastim.View.activity.BaseActivity;
import tl.pojul.com.fastim.View.activity.MyPageActivity;
import tl.pojul.com.fastim.View.widget.PolygonImage.view.PolygonImageView;
import tl.pojul.com.fastim.util.DateUtil;
import tl.pojul.com.fastim.util.GlideUtil;
import tl.pojul.com.fastim.util.MyDistanceUtil;
import tl.pojul.com.fastim.util.SPUtil;

public class NearByUserAdapter extends RecyclerView.Adapter<NearByUserAdapter.MyViewHolder> {

    private Context mContext;
    private List<LocUser> mList;

    public NearByUserAdapter(Context mContext, List<LocUser> mList) {
        this.mContext = mContext;
        this.mList = mList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_nearby_user, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        LocUser locUser = mList.get(position);
        GlideUtil.setImageBitmapNoOptions(locUser.getPhoto(), holder.photo);
        holder.nickName.setText(locUser.getNickName());
        holder.age.setText((locUser.getAge() + "岁"));
        holder.sex.setImageResource(locUser.getSex() == 0?R.drawable.woman:R.drawable.man);
        if(locUser.getOccupation() == null || locUser.getOccupation().isEmpty()){
            holder.occupation.setVisibility(View.GONE);
        }else{
            holder.occupation.setVisibility(View.VISIBLE);
            holder.occupation.setText(locUser.getOccupation());
        }
        holder.distance.setText(MyDistanceUtil.getDisttanceStr(locUser.getDistance()));
        holder.time.setText(DateUtil.getHeadway(DateUtil.convertTimeToLong(locUser.getUpdateTime())));
        holder.nearbyUserLl.setOnClickListener(v->{

        });
        holder.photo.setOnClickListener(v->{
            Bundle bundle = new Bundle();
            bundle.putString("userName", locUser.getUserName());
            ((BaseActivity)mContext).startActivity(MyPageActivity.class, bundle);
        });
    }

    @Override
    public int getItemCount() {
        return mList != null ? mList.size() : 0;
    }


    public void addDatas(List<LocUser> locUsers) {
        if (locUsers == null || locUsers.size() <= 0) {
            return;
        }
        synchronized (mList) {
            int position = mList.size();
            mList.addAll(locUsers);
            notifyItemRangeInserted(position, locUsers.size());
        }
    }

    public void clearData(){
        synchronized (mList){
            mList.clear();
            notifyDataSetChanged();
        }
    }

    static class MyViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.photo)
        PolygonImageView photo;
        @BindView(R.id.nick_name)
        TextView nickName;
        @BindView(R.id.age)
        TextView age;
        @BindView(R.id.sex)
        ImageView sex;
        @BindView(R.id.occupation)
        TextView occupation;
        @BindView(R.id.time)
        TextView time;
        @BindView(R.id.distance)
        TextView distance;
        @BindView(R.id.nearby_user_ll)
        LinearLayout nearbyUserLl;

        public MyViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

}
