package tl.pojul.com.fastim.View.Adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.pojul.fastIM.entity.Pic;
import com.pojul.objectsocket.utils.FileClassUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import tl.pojul.com.fastim.R;
import tl.pojul.com.fastim.util.URIUtil;

public class PicPickerAdapter extends RecyclerView.Adapter<PicPickerAdapter.MyViewHolder> {

    private Context mContext;
    private List<String> mList;
    private static final int REQUEST_CODE_IMAGE = 1001;

    public PicPickerAdapter(Context mContext, List<String> mList) {
        this.mContext = mContext;
        this.mList = mList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_upload_img, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        String path = mList.get(position);
        if (path == null) {
            return;
        }
        if ("添加".equals(path)) {
            holder.itemUploadPicDelete.setVisibility(View.GONE);
            if (mList.size() > 9) {
                holder.itemUploadPic.setVisibility(View.GONE);
                return;
            }
            holder.itemUploadPic.setVisibility(View.VISIBLE);
            holder.itemUploadPic.setImageResource(R.drawable.add_pic_normal);
            holder.itemUploadPic.setOnClickListener(view -> {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");//设置类型，我这里是任意类型，任意后缀的可以这样写。
                ((Activity) mContext).startActivityForResult(intent, REQUEST_CODE_IMAGE);
            });
        } else {
            holder.itemUploadPic.setOnClickListener(null);
            holder.itemUploadPic.setVisibility(View.VISIBLE);
            holder.itemUploadPicDelete.setVisibility(View.VISIBLE);
            holder.itemUploadPicDelete.setOnClickListener(view -> {
                deleteImg(position);
            });
            RequestOptions options = new RequestOptions();
            options.placeholder(R.drawable.pic)
                    .error(R.drawable.pic)
                    .fallback(R.drawable.pic);
            if (FileClassUtil.isHttpUrl(path)) {
                Glide.with(mContext).load(path).apply(options).into(holder.itemUploadPic);
            } else {
                File file = new File(path);
                if (!file.exists()) {
                    Toast.makeText(mContext, "文件不存在", Toast.LENGTH_SHORT).show();
                    deleteImg(position);
                    return;
                }
                Glide.with(mContext).load(file).apply(options).into(holder.itemUploadPic);
            }
        }
    }

    private void deleteImg(int position) {
        mList.remove(position);
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return mList != null ? mList.size() : 0;
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE_IMAGE && resultCode == Activity.RESULT_OK) {
            Uri uri = data.getData();
            String path = URIUtil.getRealPathFromURI(mContext, uri);
            File file = new File(path);
            if (!file.exists()) {
                Toast.makeText(mContext, "文件不存在", Toast.LENGTH_SHORT).show();
                return;
            }
            mList.add((mList.size() - 1), path);
            notifyDataSetChanged();
        }

    }

    static class MyViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.item_upload_pic)
        ImageView itemUploadPic;
        @BindView(R.id.item_upload_pic_delete)
        ImageView itemUploadPicDelete;

        public MyViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public List<Pic> getPics(){
        List<Pic> pics = new ArrayList<>();
        for(int i =0; i <= (mList.size() -2); i++){
            Pic pic = new Pic();
            pic.setUploadPicUrl(FileClassUtil.createStringFile(mList.get(i)));
            pics.add(pic);
        }
        return pics;
    }

}
