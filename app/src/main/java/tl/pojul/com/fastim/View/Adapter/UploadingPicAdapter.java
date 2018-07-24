package tl.pojul.com.fastim.View.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.pojul.fastIM.entity.UploadPic;

import java.io.File;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import tl.pojul.com.fastim.MyApplication;
import tl.pojul.com.fastim.R;
import tl.pojul.com.fastim.socket.upload.PicUploadManager;
import tl.pojul.com.fastim.util.FileUtil;

public class UploadingPicAdapter extends RecyclerView.Adapter<UploadingPicAdapter.MyViewHolder> {

    private Context mContext;
    private List<PicUploadManager.UploadPicTask> mList;

    public UploadingPicAdapter(Context mContext, List<PicUploadManager.UploadPicTask> mList) {
        this.mContext = mContext;
        this.mList = mList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_uploading_data, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        bindData(holder, position);
    }

    private void bindData(MyViewHolder holder, int position) {
        PicUploadManager.UploadPicTask uploadPicTask = mList.get(position);
        UploadPic uploadPic = uploadPicTask.uploadPicReq.getUploadPic();
        if (uploadPic.getPics() != null && uploadPic.getPics().size() > 0
                && new File(uploadPic.getPics().get(0).getUploadPicUrl().getFilePath()).exists()) {
            Glide.with(mContext).load(new File(uploadPic.getPics().get(0).getUploadPicUrl().getFilePath())).into(holder.uploadingPicIcon);
        } else {
            Glide.with(mContext).load(R.drawable.uploading).into(holder.uploadingPicIcon);
        }
        if (uploadPic.getUplodPicTheme() != null && !"".equals(uploadPic.getUplodPicTheme())) {
            holder.uploadingPicTheme.setText(uploadPic.getUplodPicTheme() + "(" + FileUtil.getDataSize(uploadPicTask.fileSize) + ")");
        } else {
            holder.uploadingPicTheme.setText("无主题" + "(" + FileUtil.getDataSize(uploadPicTask.fileSize) + ")");
        }
        if (uploadPic.getUploadPicTime() != null) {
            holder.uploadingPicDate.setText(uploadPic.getUploadPicTime());
        } else {
            holder.uploadingPicDate.setText("");
        }
        if(uploadPicTask.isUploading){
            holder.uploadingPicProg.setVisibility(View.VISIBLE);
            holder.uploadingPicProg.setProgress(uploadPicTask.uploadProgress);
            holder.uploadingPicStatus.setText("上传中");
            holder.uploadingPicUpload.setVisibility(View.GONE);
            holder.uploadingPicUpload.setOnClickListener(null);
            holder.uploadingPicDelete.setVisibility(View.GONE);
            holder.uploadingPicDelete.setOnClickListener(null);
        }else{
            holder.uploadingPicProg.setVisibility(View.GONE);
            holder.uploadingPicStatus.setText("上传失败");
            holder.uploadingPicUpload.setVisibility(View.VISIBLE);
            holder.uploadingPicUpload.setOnClickListener(v->{
                if(PicUploadManager.getInstance().isTaskValid(uploadPicTask)){
                    if (!MyApplication.getApplication().isConnected()) {
                        Toast.makeText(mContext, "与服务器已断开连接", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    PicUploadManager.getInstance().uploadPic(uploadPicTask, uploadPic.getId());
                }else{
                    Toast.makeText(mContext, "图片已被删除，请重新添加", Toast.LENGTH_SHORT).show();
                }
            });
            holder.uploadingPicDelete.setOnClickListener(v->{
                deleteData(position);
            });
        }
    }

    private void deleteData(int position) {
        PicUploadManager.UploadPicTask uploadPicTask = mList.get(position);
        mList.remove(position);
        PicUploadManager.getInstance().deleteItem(uploadPicTask);
        notifyItemChanged(position);
    }

    @Override
    public int getItemCount() {
        return mList != null ? mList.size() : 0;
    }

    static class MyViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.uploading_pic_icon)
        ImageView uploadingPicIcon;
        @BindView(R.id.uploading_pic_theme)
        TextView uploadingPicTheme;
        @BindView(R.id.uploading_pic_date)
        TextView uploadingPicDate;
        @BindView(R.id.uploading_pic_prog)
        ProgressBar uploadingPicProg;
        @BindView(R.id.uploading_pic_delete)
        ImageView uploadingPicDelete;
        @BindView(R.id.uploading_pic_upload)
        ImageView uploadingPicUpload;
        @BindView(R.id.uploading_pic_status)
        TextView uploadingPicStatus;

        public MyViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public void setDatas(List<PicUploadManager.UploadPicTask> uploadPicTasks) {
        this.mList = uploadPicTasks;
        notifyDataSetChanged();
    }

    public void updateItem(String uid){
        if(uid == null){
            return;
        }
        for(int i = 0; i < mList.size(); i++){
            PicUploadManager.UploadPicTask uploadPicTask = mList.get(i);
            if(uid.equals(uploadPicTask.uid)){
                notifyItemChanged(i);
                break;
            }
        }
    }

}
