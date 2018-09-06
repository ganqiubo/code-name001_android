package tl.pojul.com.fastim.View.Adapter;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.pojul.fastIM.entity.ResourceIdTitle;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import tl.pojul.com.fastim.MyApplication;
import tl.pojul.com.fastim.R;
import tl.pojul.com.fastim.View.activity.BaseActivity;
import tl.pojul.com.fastim.View.activity.FollowedUsersActivity;
import tl.pojul.com.fastim.View.activity.LikeUserActivity;
import tl.pojul.com.fastim.View.activity.MyPageActivity;
import tl.pojul.com.fastim.View.activity.PicBroseActivity;
import tl.pojul.com.fastim.View.activity.UserPicsActivity;
import tl.pojul.com.fastim.View.activity.UserTagMessActivity;
import tl.pojul.com.fastim.util.RandomUtil;
import tl.pojul.com.fastim.util.SPUtil;

public class TabAdapter extends RecyclerView.Adapter<TabAdapter.MyViewHolder> {

    private Context mContext;
    private List<ResourceIdTitle> mList;
    private String type;

    private List<Integer> allColors;

    private List<Integer> colors = new ArrayList();

    public TabAdapter(Context mContext, List<ResourceIdTitle> mList, String type) {
        this.mContext = mContext;
        this.mList = mList;
        this.type = type;
        allColors = new ArrayList<>();
        allColors.add(Color.parseColor("#1C81A9"));
        allColors.add(Color.parseColor("#86b929"));
        allColors.add(Color.parseColor("#1296db"));
        allColors.add(Color.parseColor("#cc090a"));
        allColors.add(Color.parseColor("#776e1c"));
        allColors.add(Color.parseColor("#9e4812"));

        colors = RandomUtil.randomArrays(allColors, mList.size());
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_tab, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        ResourceIdTitle resourceIdTitle = mList.get(position);
        holder.iv.setImageResource(resourceIdTitle.getResourceId());
        holder.note.setText(resourceIdTitle.getTitle());
        if(resourceIdTitle.getTitle().equals("对象")){
            if(SPUtil.getInstance().getUser().getSex() == 0){
                holder.note.setText("找个男盆友");
            }else{
                holder.note.setText("找个女盆友");
            }
        }else if(resourceIdTitle.getTitle().equals("伴侣")){
            if(SPUtil.getInstance().getUser().getSex() == 0){
                holder.note.setText("找个老公");
            }else{
                holder.note.setText("找个老婆");
            }
        }
        /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && position < colors.size()) {
            holder.iv.setImageTintList(ColorStateList.valueOf(colors.get(position)));
            holder.note.setTextColor(colors.get(position));
        }*/

        holder.tabRl.setOnClickListener((View v) ->{
            switch (holder.note.getText().toString()){
                case "主页":
                    Bundle bundle = new Bundle();
                    bundle.putString("userName", SPUtil.getInstance().getUser().getUserName());
                    ((BaseActivity)mContext).startActivity(MyPageActivity.class, bundle);
                    break;
                case "自拍/写真":
                    bundle = new Bundle();
                    String json = new Gson().toJson(SPUtil.getInstance().getUser());
                    bundle.putString("user", json);
                    ((BaseActivity)mContext).startActivity(UserPicsActivity.class, bundle);
                    break;
                case "发布的消息":
                    bundle = new Bundle();
                    bundle.putString("userName", SPUtil.getInstance().getUser().getUserName());
                    ((BaseActivity)mContext).startActivity(UserTagMessActivity.class, bundle);
                    break;
                case "喜欢的人":
                    ((BaseActivity)mContext).startActivity(LikeUserActivity.class, null);
                    break;
                case "关注的人":
                    ((BaseActivity)mContext).startActivity(FollowedUsersActivity.class, null);
                    break;
                case "精选":
                    bundle = new Bundle();
                    bundle.putString("filter", "精选");
                    ((BaseActivity)mContext).startActivity(PicBroseActivity.class, bundle);
                    break;
                case "附近":
                    bundle = new Bundle();
                    bundle.putString("filter", "附近");
                    ((BaseActivity)mContext).startActivity(PicBroseActivity.class, bundle);
                    break;
                case "风景":
                    bundle = new Bundle();
                    bundle.putString("filter", "风景");
                    ((BaseActivity)mContext).startActivity(PicBroseActivity.class, bundle);
                    break;
                case "生活":
                    bundle = new Bundle();
                    bundle.putString("filter", "生活");
                    ((BaseActivity)mContext).startActivity(PicBroseActivity.class, bundle);
                    break;
                case "建筑":
                    bundle = new Bundle();
                    bundle.putString("filter", "建筑");
                    ((BaseActivity)mContext).startActivity(PicBroseActivity.class, bundle);
                    break;
                case "更多":
                    if("pic".equals(type)){
                        bundle = new Bundle();
                        bundle.putString("filter", "更多");
                        ((BaseActivity)mContext).startActivity(PicBroseActivity.class, bundle);
                    }else{

                    }
                    break;
                /*case "":
                    break;*/
            }
        });
    }

    @Override
    public int getItemCount() {
        return mList != null ? mList.size() : 0;
    }

    static class MyViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.iv)
        ImageView iv;
        @BindView(R.id.note)
        TextView note;
        @BindView(R.id.tab_rl)
        LinearLayout tabRl;

        public MyViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

}
