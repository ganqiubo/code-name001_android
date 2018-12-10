package tl.pojul.com.fastim.View.Adapter;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.pojul.fastIM.entity.ExtendUser;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import tl.pojul.com.fastim.R;
import tl.pojul.com.fastim.View.activity.BaseActivity;
import tl.pojul.com.fastim.View.activity.MyPageActivity;
import tl.pojul.com.fastim.View.widget.PolygonImage.view.PolygonImageView;
import tl.pojul.com.fastim.util.GlideUtil;

public class UserListAdapter extends RecyclerView.Adapter<UserListAdapter.MyViewHolder> {

    private Context mContext;
    private List<ExtendUser> mList;

    public UserListAdapter(Context mContext, List<ExtendUser> mList) {
        this.mContext = mContext;
        this.mList = mList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_brief_user_info, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        ExtendUser extendUser = mList.get(position);
        holder.nickName.setText(extendUser.getNickName());
        holder.sex.setImageResource(extendUser.getSex()==0?R.drawable.woman:R.drawable.man);
        holder.age.setText(extendUser.getAge() + "岁");
        holder.certificat.setText(extendUser.getCertificate()==0?"未实名认证":"已实名认证");
        GlideUtil.setImageBitmapNoOptions(extendUser.getPhoto().getFilePath(), holder.photo);
        holder.photo.setOnClickListener(v->{
            Bundle bundle = new Bundle();
            bundle.putString("userName", extendUser.getUserName());
            ((BaseActivity)mContext).startActivity(MyPageActivity.class, bundle);
        });

        /*if (extendUser.getIsFriend() <= 0){
            holder.chat.setVisibility(View.GONE);
            holder.chat.setOnClickListener(null);
            holder.addFriend.setVisibility(View.VISIBLE);
            holder.addFriend.setOnClickListener(v->{

            });
        }else{
            holder.addFriend.setVisibility(View.GONE);
            holder.addFriend.setOnClickListener(null);
            holder.chat.setVisibility(View.VISIBLE);
            holder.chat.setOnClickListener(v->{

            });
        }*/
    }

    @Override
    public int getItemCount() {
        return mList != null ? mList.size() : 0;
    }


    public void addDatas(List<ExtendUser> extendUsers) {
        if(extendUsers == null || extendUsers.size() <= 0){
            return;
        }
        synchronized (mList){
            int position = mList.size();
            mList.addAll(extendUsers);
            notifyItemRangeInserted(position, extendUsers.size());
        }
    }



    static class MyViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.photo)
        PolygonImageView photo;
        @BindView(R.id.nick_name)
        TextView nickName;
        @BindView(R.id.sex)
        ImageView sex;
        @BindView(R.id.age)
        TextView age;
        @BindView(R.id.certificat)
        TextView certificat;
        @BindView(R.id.chat)
        ImageView chat;
        @BindView(R.id.add_friend)
        ImageView addFriend;
        @BindView(R.id.user_simple_rl)
        RelativeLayout userSimpleRl;

        public MyViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

}
