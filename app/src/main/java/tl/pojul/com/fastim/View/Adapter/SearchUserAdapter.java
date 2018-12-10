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
import com.pojul.fastIM.entity.User;

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

public class SearchUserAdapter extends RecyclerView.Adapter<SearchUserAdapter.MyViewHolder> {

    private Context mContext;
    private List<User> mList;
    private OnItemClickListener onItemClickListener;

    public SearchUserAdapter(Context mContext, List<User> mList) {
        this.mContext = mContext;
        this.mList = mList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_search_user, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        User user = mList.get(position);
        GlideUtil.setImageBitmapNoOptions(user.getPhoto().getFilePath(), holder.photo);
        holder.nickName.setText(user.getNickName());
        holder.age.setText(("" + user.getAge()));
        holder.sex.setImageResource(user.getSex() == 0?R.drawable.woman:R.drawable.man);
        if(user.getOccupation() == null || user.getOccupation().isEmpty()){
            holder.occupation.setVisibility(View.GONE);
        }else{
            holder.occupation.setVisibility(View.VISIBLE);
            holder.occupation.setText(user.getOccupation());
        }
        holder.nearbyUserLl.setOnClickListener(v->{
            if(onItemClickListener != null){
                onItemClickListener.onItemClick(position);
            }
        });
        holder.photo.setOnClickListener(v->{
            Bundle bundle = new Bundle();
            bundle.putString("userName", user.getUserName());
            ((BaseActivity)mContext).startActivity(MyPageActivity.class, bundle);
        });
    }

    @Override
    public int getItemCount() {
        return mList != null ? mList.size() : 0;
    }


    public void addDatas(List<User> users) {
        if (users == null || users.size() <= 0) {
            return;
        }
        synchronized (mList) {
            int position = mList.size();
            mList.addAll(users);
            notifyItemRangeInserted(position, users.size());
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
        @BindView(R.id.nearby_user_ll)
        LinearLayout nearbyUserLl;

        public MyViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public interface OnItemClickListener{
        void onItemClick(int position);
    }

}
