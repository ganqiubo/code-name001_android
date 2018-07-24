package tl.pojul.com.fastim.View.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.pojul.fastIM.entity.UploadPic;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import tl.pojul.com.fastim.R;
import tl.pojul.com.fastim.util.FileUtil;

public class UploadPicRecordAdapter extends RecyclerView.Adapter<UploadPicRecordAdapter.MyViewHolder> {

    private Context mContext;
    private List<UploadPic> mList;

    public UploadPicRecordAdapter(Context mContext, List<UploadPic> mList) {
        this.mContext = mContext;
        this.mList = mList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_uploadpic_record, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        UploadPic uploadPic = mList.get(position);
        RequestOptions options = new RequestOptions();
        options.placeholder(R.drawable.pic)
                .error(R.drawable.pic)
                .fallback(R.drawable.pic);
        if (uploadPic.getPics() != null && uploadPic.getPics().size() > 0) {
            Glide.with(mContext).load(uploadPic.getPics().get(0).getUploadPicUrl().getFilePath()).apply(options).into(holder.pic);
        } else {
            Glide.with(mContext).load(R.drawable.pic).into(holder.pic);
        }
        if (uploadPic.getUplodPicTheme() != null && !"".equals(uploadPic.getUplodPicTheme())) {
            holder.theme.setText(uploadPic.getUplodPicTheme());
        } else {
            holder.theme.setText("无主题");
        }
        if (uploadPic.getUploadPicTime() != null) {
            holder.date.setText(uploadPic.getUploadPicTime());
        } else {
            holder.date.setText("");
        }
    }

    @Override
    public int getItemCount() {
        return mList != null ? mList.size() : 0;
    }

    static class MyViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.pic)
        ImageView pic;
        @BindView(R.id.theme)
        TextView theme;
        @BindView(R.id.date)
        TextView date;
        @BindView(R.id.show_inmap)
        ImageView showInmap;
        @BindView(R.id.detail)
        ImageView detail;

        public MyViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public void addRecordsEnd(List<UploadPic> uploadPics) {
        if (uploadPics == null || uploadPics.size() <= 0) {
            return;
        }
        synchronized (mList) {
            int lastPosition = mList.size();
            mList.addAll(uploadPics);
            notifyItemRangeInserted(lastPosition, uploadPics.size());
        }
    }

    public void addRecordsTop(UploadPic uploadPics) {
        if (uploadPics == null) {
            return;
        }
        synchronized (mList) {
            mList.add(0, uploadPics);
            notifyItemInserted(0);
        }
    }

}
