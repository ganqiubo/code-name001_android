package tl.pojul.com.fastim.View.Adapter;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.pojul.fastIM.entity.UploadPic;

import java.io.File;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import tl.pojul.com.fastim.R;
import tl.pojul.com.fastim.View.activity.BaseActivity;
import tl.pojul.com.fastim.View.activity.UploadPicActivity;
import tl.pojul.com.fastim.dao.UploadPicDao;
import tl.pojul.com.fastim.util.DialogUtil;

public class SavedDataAdapter extends RecyclerView.Adapter<SavedDataAdapter.MyViewHolder> {

    private Context mContext;
    private List<UploadPic> mList;

    public SavedDataAdapter(Context mContext, List<UploadPic> mList) {
        this.mContext = mContext;
        this.mList = mList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_saved_data, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        bindData(holder, position);
    }

    private void bindData(MyViewHolder holder, int position) {
        UploadPic uploadPic = mList.get(position);
        if (uploadPic.getPics() != null && uploadPic.getPics().size() > 0
                && new File(uploadPic.getPics().get(0).getUploadPicUrl().getFilePath()).exists()) {
            Glide.with(mContext).load(new File(uploadPic.getPics().get(0).getUploadPicUrl().getFilePath())).into(holder.savedDataPic);
        } else {
            Glide.with(mContext).load(R.drawable.uploading).into(holder.savedDataPic);
        }
        if (uploadPic.getUplodPicTheme() != null && !"".equals(uploadPic.getUplodPicTheme())) {
            holder.savedDataTheme.setText(uploadPic.getUplodPicTheme());
        } else {
            holder.savedDataTheme.setText("无主题");
        }
        if (uploadPic.getUploadPicTime() != null) {
            holder.savedDataDate.setText(uploadPic.getUploadPicTime());
        } else {
            holder.savedDataDate.setText("");
        }
        holder.savedDataUpload.setOnClickListener(view -> {
            startUploadActivity(uploadPic);
        });
        holder.savedDataEdit.setOnClickListener(view -> {
            startUploadActivity(uploadPic);
        });
        holder.savedDataDelete.setOnClickListener(view -> {
            DialogUtil.getInstance().showPromptDialog(mContext, "删除数据",  "确定删除本条数据?");
            DialogUtil.getInstance().setDialogClick(str->{
                if("确定".equals(str)){
                    deleteData(position);
                }
            });
        });
    }

    private void deleteData(int position) {
        UploadPic uploadPic = mList.get(position);
        int result = new UploadPicDao().deleteUploadPic(uploadPic.getId());
        if(result >= 0){
            mList.remove(position);
            notifyDataSetChanged();
        }
    }


    public void startUploadActivity(UploadPic uploadPic) {
        Bundle bundle = new Bundle();
        bundle.putInt("uploadPicEditMode", 1);
        Gson gs = new GsonBuilder().disableHtmlEscaping().create();
        bundle.putString("savedUploadPic", gs.toJson(uploadPic));
        ((BaseActivity) mContext).startActivity(UploadPicActivity.class, bundle);
    }

    @Override
    public int getItemCount() {
        return mList != null ? mList.size() : 0;
    }

    static class MyViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.saved_data_pic)
        ImageView savedDataPic;
        @BindView(R.id.saved_data_theme)
        TextView savedDataTheme;
        @BindView(R.id.saved_data_date)
        TextView savedDataDate;
        @BindView(R.id.saved_data_upload)
        ImageView savedDataUpload;
        @BindView(R.id.saved_data_edit)
        ImageView savedDataEdit;
        @BindView(R.id.saved_data_delete)
        ImageView savedDataDelete;

        public MyViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public void setDatas(List<UploadPic> uploadPics) {
        this.mList = uploadPics;
        notifyDataSetChanged();
    }

}
