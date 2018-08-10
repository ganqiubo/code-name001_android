package tl.pojul.com.fastim.View.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.pojul.fastIM.entity.User;
import com.pojul.fastIM.entity.UserSelect;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import tl.pojul.com.fastim.R;
import tl.pojul.com.fastim.View.widget.PolygonImage.view.PolygonImageView;
import tl.pojul.com.fastim.util.GlideUtil;

public class SelectUserAdapter extends RecyclerView.Adapter<SelectUserAdapter.MyViewHolder> {

    private Context mContext;
    private List<UserSelect> mList;
    private boolean canRemove;
    private boolean canSelect;

    public SelectUserAdapter(Context mContext, List<UserSelect> mList, boolean canRemove, boolean canSelect) {
        this.mContext = mContext;
        this.mList = mList;
        this.canRemove = canRemove;
        this.canSelect = canSelect;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_user_select, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        UserSelect userSelect = mList.get(holder.getAdapterPosition());
        if(!canRemove){
            holder.remove.setVisibility(View.GONE);
            holder.remove.setOnClickListener(null);
        }else{
            holder.remove.setVisibility(View.VISIBLE);
            holder.remove.setOnClickListener(v->{
                removeUser(holder.getAdapterPosition());
            });
        }
        GlideUtil.setImageBitmap(userSelect.getUser().getPhoto().getFilePath(), holder.photo, 1.0f);
        holder.nickName.setText(userSelect.getUser().getNickName());
        if(userSelect.getUser().getSex() == 1){
            holder.sex.setImageResource(R.drawable.man);
        }else{
            holder.sex.setImageResource(R.drawable.woman);
        }
        if(canSelect){
            holder.select.setVisibility(View.VISIBLE);
            if(userSelect.isSelected()){
                holder.select.setSelected(true);
            }else{
                holder.select.setSelected(false);
            }
            holder.select.setOnClickListener(v->{
                holder.select.setSelected(!holder.select.isSelected());
                userSelect.setSelected(holder.select.isSelected());
            });
        }else{
            holder.select.setVisibility(View.GONE);
            holder.select.setOnClickListener(null);
        }

    }

    @Override
    public int getItemCount() {
        return mList != null ? mList.size() : 0;
    }


    private void removeUser(int position) {
        synchronized (mList){
            mList.remove(position);
            notifyItemRemoved(position);
        }
    }

    public void selectedAll() {
        synchronized (mList){
            for(int i = 0; i < mList.size(); i++){
                mList.get(i).setSelected(true);
            }
            notifyDataSetChanged();
        }
    }

    public void unSelectedAll() {
        synchronized (mList){
            for(int i = 0; i < mList.size(); i++){
                mList.get(i).setSelected(false);
            }
            notifyDataSetChanged();
        }
    }

    public List<UserSelect> getSeletedUser() {
        List<UserSelect> userSelects = new ArrayList<>();
        for (int i =0; i < mList.size(); i++){
            UserSelect userSelect = mList.get(i);
            if(userSelect.isSelected()){
                userSelects.add(userSelect);
            }
        }
        return userSelects;
    }

    public void addList(List<UserSelect> userSelects) {
        if(userSelects.size() <= 0){
            return;
        }
        for (int i = 0; i < userSelects.size(); i++) {
            UserSelect userSelect = userSelects.get(i);
            if(containsUser(userSelect.getUser().getUserName())){
                continue;
            }
            synchronized (mList) {
                mList.add(userSelects.get(i));
            }
        }
        notifyDataSetChanged();
    }

    public boolean containsUser(String userName){
        for(int i =0; i < mList.size(); i++){
            if(mList.get(i).getUser().getUserName().equals(userName)){
                return true;
            }
        }
        return false;
    }

    public List<User> getAllUsers() {
        List<User> users = new ArrayList<>();
        for (int i = 0; i < mList.size(); i++) {
            users.add(mList.get(i).getUser());
        }
        return users;
    }

    static class MyViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.photo)
        PolygonImageView photo;
        @BindView(R.id.nick_name)
        TextView nickName;
        @BindView(R.id.sex)
        ImageView sex;
        @BindView(R.id.select)
        ImageView select;
        @BindView(R.id.remove)
        ImageView remove;

        public MyViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

        }
    }

}
