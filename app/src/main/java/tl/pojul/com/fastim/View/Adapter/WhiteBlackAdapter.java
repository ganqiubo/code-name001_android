package tl.pojul.com.fastim.View.Adapter;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.pojul.fastIM.entity.User;
import com.pojul.fastIM.entity.UserSelect;
import com.pojul.fastIM.entity.WhiteBlack;
import com.pojul.fastIM.entity.WhiteBlackSelect;

import java.nio.BufferUnderflowException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import tl.pojul.com.fastim.R;
import tl.pojul.com.fastim.View.activity.BaseActivity;
import tl.pojul.com.fastim.View.activity.UserSelectActivity;
import tl.pojul.com.fastim.util.DialogUtil;
import tl.pojul.com.fastim.util.SPUtil;

public class WhiteBlackAdapter extends RecyclerView.Adapter<WhiteBlackAdapter.MyViewHolder> {

    private Context mContext;
    private List<WhiteBlackSelect> mList;
    private String type;
    private boolean canSelect;
    private boolean canDelete;

    public WhiteBlackAdapter(Context mContext, List<WhiteBlackSelect> mList, String type, boolean canSelect, boolean canDelete) {
        this.mContext = mContext;
        this.mList = mList;
        this.type = type;
        this.canSelect = canSelect;
        this.canDelete = canDelete;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_whiteblacks, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        WhiteBlackSelect whiteBlackSelect = mList.get(holder.getAdapterPosition());
        WhiteBlack whiteBlack = whiteBlackSelect.getWhiteBlack();
        holder.name.setText(whiteBlack.getName());
        holder.updateTime.setText(whiteBlack.getUpdateTime());
        holder.view.setOnClickListener(v->{
            Bundle bundle = new Bundle();
            bundle.putString("type", "ViewWhiteBlack");
            bundle.putString("key", whiteBlackSelect.getWhiteBlack().getName());
            ((BaseActivity)mContext).startActivity(UserSelectActivity.class, bundle);
        });
        if(canDelete){
            holder.delete.setVisibility(View.VISIBLE);
            holder.delete.setOnClickListener(v->{
                deleteItem(holder.getAdapterPosition());
            });
        }else{
            holder.delete.setVisibility(View.GONE);
            holder.delete.setOnClickListener(null);
        }

        if(canSelect){
            holder.select.setSelected(whiteBlackSelect.isSelected());
            holder.select.setVisibility(View.VISIBLE);
            holder.select.setOnClickListener(v->{
                holder.select.setSelected(!holder.select.isSelected());
                whiteBlackSelect.setSelected(holder.select.isSelected());
            });
        }else{
            holder.select.setVisibility(View.GONE);
            holder.select.setOnClickListener(null);
        }
    }

    private void deleteItem(int adapterPosition) {
        String typeName = "白名单";
        if("black".equals(type)){
            typeName = "黑名单";
        }
        DialogUtil.getInstance().showPromptDialog(mContext, ("删除‘" + mList.get(adapterPosition).getWhiteBlack().getName() + "'"), ("确定要删除这条" + typeName + "?"));
        DialogUtil.getInstance().setDialogClick(str -> {
            if("确定".equals(str)){
                synchronized (mList){
                    SPUtil.getInstance().removeWhiteBlack(mList.get(adapterPosition).getWhiteBlack().getName());
                    mList.remove(adapterPosition);
                    notifyItemRemoved(adapterPosition);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mList != null ? mList.size() : 0;
    }

    public List<UserSelect> getSelectUsers() {
        List<UserSelect> userSelects = new ArrayList<>();
        for (int i = 0; i < mList.size(); i++) {
            if(mList.get(i).isSelected()){
                userSelects.addAll(getSelectUsers(mList.get(i).getWhiteBlack()));
            }
        }
        return userSelects;
    }

    public List<UserSelect> getSelectUsers(WhiteBlack whiteBlack) {
        List<UserSelect> userSelects = new ArrayList<>();
        for (int i = 0; i < whiteBlack.getUsers().size(); i++) {
            UserSelect userSelect = new UserSelect();
            userSelect.setSelected(true);
            userSelect.setUser(whiteBlack.getUsers().get(i));
            userSelects.add(userSelect);
        }
        return userSelects;
    }

    static class MyViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.name)
        TextView name;
        @BindView(R.id.update_time)
        TextView updateTime;
        @BindView(R.id.delete)
        ImageView delete;
        @BindView(R.id.view)
        ImageView view;
        @BindView(R.id.select)
        ImageView select;

        public MyViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

}
