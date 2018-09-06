package tl.pojul.com.fastim.View.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.pojul.fastIM.entity.Pic;
import com.pojul.fastIM.message.chat.PicMessage;
import com.pojul.objectsocket.utils.FileClassUtil;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import tl.pojul.com.fastim.R;
import tl.pojul.com.fastim.util.DialogUtil;
import tl.pojul.com.fastim.util.GlideUtil;

public class TagPicAdapter extends RecyclerView.Adapter<TagPicAdapter.MyViewHolder> {

    private Context mContext;
    private List<Pic> mList;
    private boolean showPhotoView;

    public TagPicAdapter(Context mContext, List<Pic> mList, boolean showPhotoView) {
        this.mContext = mContext;
        this.mList = mList;
        this.showPhotoView = showPhotoView;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_multi_image, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Pic pic = mList.get(position);
        GlideUtil.setImageBitmap(pic.getUploadPicUrl().getFilePath(), holder.img, 0.5f);
        holder.img.setOnClickListener(v->{
            if(!showPhotoView){
                return;
            }
            Toast.makeText(mContext, "TagPic Click", Toast.LENGTH_SHORT).show();
            PicMessage picMessage = new PicMessage();
            picMessage.setPic(FileClassUtil.createStringFile(pic.getUploadPicUrl().getFilePath()));
            try {
                DialogUtil.getInstance().showDetailImgDialogPop(mContext, picMessage, holder.img, DialogUtil.POP_TYPR_IMG);
            } catch (Exception e) {
            }
        });
    }

    @Override
    public int getItemCount() {
        return mList != null ? mList.size() : 0;
    }

    static class MyViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.img)
        ImageView img;

        public MyViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

}
