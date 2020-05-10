package tl.pojul.com.fastim.View.Adapter;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.pojul.fastIM.entity.ExtendUploadPic;
import com.pojul.fastIM.entity.Pic;
import com.pojul.fastIM.entity.UploadPic;
import com.pojul.fastIM.message.request.CollectUploadPicReq;
import com.pojul.fastIM.message.request.LikeUploadPicReq;
import com.pojul.fastIM.message.request.ThumbupUploadPicReq;
import com.pojul.objectsocket.message.ResponseMessage;
import com.pojul.objectsocket.socket.SocketRequest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import tl.pojul.com.fastim.MyApplication;
import tl.pojul.com.fastim.R;
import tl.pojul.com.fastim.View.activity.BaseActivity;
import tl.pojul.com.fastim.View.activity.GalleryActivity;
import tl.pojul.com.fastim.View.activity.MyPageActivity;
import tl.pojul.com.fastim.View.activity.PicCommentActivity;
import tl.pojul.com.fastim.View.widget.PolygonImage.view.PolygonImageView;
import tl.pojul.com.fastim.util.ArrayUtil;
import tl.pojul.com.fastim.util.DateUtil;
import tl.pojul.com.fastim.util.DensityUtil;
import tl.pojul.com.fastim.util.GlideUtil;
import tl.pojul.com.fastim.util.SPUtil;

public class PicsAdapter extends RecyclerView.Adapter<PicsAdapter.MyViewHolder> {

    private Context mContext;
    private List<ExtendUploadPic> mList;
    private int itemWidth;
    private HashMap<Integer, Integer> itemHeight = new HashMap<>();
    private int gridCount;

    public PicsAdapter(Context mContext, List<ExtendUploadPic> mList, int gridCount) {
        this.mContext = mContext;
        this.mList = mList;
        this.gridCount = gridCount;
        if(gridCount == 1){
            itemWidth = MyApplication.SCREEN_WIDTH;
        }else{
            itemWidth = (int) ((MyApplication.SCREEN_WIDTH - DensityUtil.dp2px(mContext, 0)) * 1.0f / 2);
        }
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_extend_upload_pic, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        ExtendUploadPic uploadPic = mList.get(position);
        switch(uploadPic.getGalleryType()){
            case "脚步":
                bindFootStep(holder, uploadPic, position);
                break;
            case "unsplash":
                bindUnsplash(holder, uploadPic, position);
                break;
            case "pexels":
                bindPexels(holder, uploadPic, position);
                break;
            case "pixabay":
                bindPixabay(holder, uploadPic, position);
                break;
        }
        if (uploadPic.getPics() == null || uploadPic.getPics().size() <= 0) {
            return;
        }
        Pic firstPic = uploadPic.getPics().get(0);
        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) holder.pic.getLayoutParams();
        float heightWidthRate = 0;
        if (firstPic.getWidth() != 0) {
            heightWidthRate = firstPic.getHeight() * 1.0f / firstPic.getWidth();
        }

        if (!itemHeight.containsKey(position)) {
            layoutParams.width = itemWidth;
            layoutParams.height = (int) (itemWidth * heightWidthRate);
            itemHeight.put(position, layoutParams.height);
        } else {
            layoutParams.width = itemWidth;
            layoutParams.height = itemHeight.get(position);
        }
        holder.pic.setLayoutParams(layoutParams);

        if (uploadPic.getPics() != null) {
            holder.num.setText((uploadPic.getPics().size() + "张"));
        }

        holder.galleryLl.setOnClickListener(v -> {
            holder.gallery.performClick();
            holder.num.performClick();
            //Intent intent = new Intent(mContext, GalleryActivity.class);
            Bundle bundle = new Bundle();
            bundle.putStringArrayList("urls", ArrayUtil.getUrls(uploadPic.getPics()));
            ((BaseActivity) mContext).startActivity(GalleryActivity.class, bundle);
        });
        holder.pic.setOnClickListener(v->{
            Bundle bundle = new Bundle();
            bundle.putStringArrayList("urls", ArrayUtil.getUrls(uploadPic.getPics()));
            ((BaseActivity) mContext).startActivity(GalleryActivity.class, bundle);
        });
        if (uploadPic.getHasLiked() > 0) {
            holder.likeIv.setSelected(true);
            holder.likeTv.setSelected(true);
            holder.likeRl.setOnClickListener(v -> {
                if (uploadPic.isUploading()) {
                    return;
                }
                uploadPic.setUploading(true);
                holder.thumbProgress.setVisibility(View.VISIBLE);
                reqLikePic(uploadPic, 1, position);
            });
        } else {
            holder.likeIv.setSelected(false);
            holder.likeTv.setSelected(false);
            holder.likeRl.setOnClickListener(v -> {
                if (uploadPic.isUploading()) {
                    return;
                }
                uploadPic.setUploading(true);
                holder.thumbProgress.setVisibility(View.VISIBLE);
                reqLikePic(uploadPic, 0, position);
            });
        }
        if (uploadPic.getHasCollected() > 0) {
            holder.followIv.setSelected(true);
            holder.followNumTv.setSelected(true);
            holder.followRl.setOnClickListener(v -> {
                if (uploadPic.isUploading()) {
                    return;
                }
                uploadPic.setUploading(true);
                holder.thumbProgress.setVisibility(View.VISIBLE);
                reqCollectPic(uploadPic, 1, position);
            });
        } else {
            holder.followIv.setSelected(false);
            holder.followNumTv.setSelected(false);
            holder.followRl.setOnClickListener(v -> {
                if (uploadPic.isUploading()) {
                    return;
                }
                uploadPic.setUploading(true);
                holder.thumbProgress.setVisibility(View.VISIBLE);
                reqCollectPic(uploadPic, 0, position);
            });
        }
        holder.thumbUpTv.setText(("" + uploadPic.getThumbUpNum()));
        if (uploadPic.getHasThubmUp() > 0) {
            holder.thumbUpTv.setSelected(true);
            holder.thumbUpIv.setSelected(true);
            holder.thumbUpRl.setOnClickListener(null);
        } else {
            holder.thumbUpTv.setSelected(false);
            holder.thumbUpIv.setSelected(false);
            holder.thumbUpRl.setOnClickListener(v -> {
                if (uploadPic.isUploading()) {
                    return;
                }
                uploadPic.setUploading(true);
                holder.thumbProgress.setVisibility(View.VISIBLE);
                reqThumbUpPic(uploadPic, position);
            });
        }
        if (uploadPic.isUploading()) {
            holder.thumbProgress.setVisibility(View.VISIBLE);
        } else {
            holder.thumbProgress.setVisibility(View.GONE);
        }
        holder.commentNumLl.setOnClickListener(v->{
            toComment(uploadPic, holder);
        });

        if(uploadPic.getPicComments() == null || uploadPic.getPicComments().size() <= 0){
            holder.commentTopLl.setVisibility(View.GONE);
        }else{
            holder.commentTopLl.setVisibility(View.VISIBLE);
            holder.commentTop1Ll.setVisibility(View.GONE);
            holder.commentTop2Ll.setVisibility(View.GONE);
            holder.commentTop3Ll.setVisibility(View.GONE);
            if(uploadPic.getPicComments().size() >= 1){
                holder.commentTop1Ll.setVisibility(View.VISIBLE);
                Glide.with(mContext).load(uploadPic.getPicComments().get(0).getPhoto()).into(holder.commentTop1Photo);
                holder.commentTop1Name.setText(uploadPic.getPicComments().get(0).getNickName()+": ");
                holder.commentTop1Text.setText(uploadPic.getPicComments().get(0).getCommentText());
            }
            if(uploadPic.getPicComments().size() >= 2){
                holder.commentTop2Ll.setVisibility(View.VISIBLE);
                Glide.with(mContext).load(uploadPic.getPicComments().get(1).getPhoto()).into(holder.commentTop2Photo);
                holder.commentTop2Name.setText(uploadPic.getPicComments().get(1).getNickName()+": ");
                holder.commentTop2Text.setText(uploadPic.getPicComments().get(1).getCommentText());
            }
            if(uploadPic.getPicComments().size() >= 3){
                holder.commentTop3Ll.setVisibility(View.VISIBLE);
                Glide.with(mContext).load(uploadPic.getPicComments().get(2).getPhoto()).into(holder.commentTop3Photo);
                holder.commentTop3Name.setText(uploadPic.getPicComments().get(2).getNickName()+": ");
                holder.commentTop3Text.setText(uploadPic.getPicComments().get(2).getCommentText());
            }
        }
        if(uploadPic.getComments() <= 0){
            holder.commentNum.setText("评论>");
        }else{
            holder.commentNum.setText("评论·" + uploadPic.getComments() + ">");
        }
    }

    private void toComment(ExtendUploadPic uploadPic, MyViewHolder holder) {
        Bundle bundle = new Bundle();
        if("脚步".equals(uploadPic.getGalleryType())){
            bundle.putString("picId", "" + uploadPic.getId());
            bundle.putString("themeStr", uploadPic.getUplodPicTheme());
            bundle.putString("locStr", (uploadPic.getUploadPicProvince() + uploadPic.getUploadPicCity()));
        }else{
            bundle.putString("picId", uploadPic.getThirdUid());
            bundle.putString("locStr", uploadPic.getUploadPicCity());
        }
        bundle.putString("picNickName", uploadPic.getNickName());
        bundle.putString("timeStr", holder.time.getText().toString());
        bundle.putString("photoStr", uploadPic.getPhoto());
        bundle.putString("gallery", uploadPic.getGalleryType());
        bundle.putStringArrayList("picUrls", ArrayUtil.getUrls(uploadPic.getPics()));
        ((BaseActivity)mContext).startActivity(PicCommentActivity.class, bundle);
    }

    private void bindPexels(MyViewHolder holder, ExtendUploadPic uploadPic, int position) {
        Glide.with(mContext).load(R.drawable.empty_photo).into(holder.photo);
        holder.photo.setOnClickListener(null);
        holder.nickName.setVisibility(View.GONE);
        holder.sex.setVisibility(View.GONE);
        holder.theme.setVisibility(View.GONE);
        holder.time.setVisibility(View.GONE);
        holder.location.setVisibility(View.GONE);
        if (uploadPic.getPics() != null && uploadPic.getPics().size() > 0) {
            if(gridCount == 1){
                GlideUtil.setImageBitmapNoOptions(uploadPic.getPics().get(0).getUploadPicUrl().getFilePath(), holder.pic);
            }else{
                GlideUtil.setImageBitmapNoOptions(uploadPic.getBrosePic(), holder.pic);
            }
        }
    }

    private void bindPixabay(MyViewHolder holder, ExtendUploadPic uploadPic, int position) {
        GlideUtil.setImageBitmapNoOptions(uploadPic.getPhoto(), holder.photo);
        holder.nickName.setText(uploadPic.getNickName());
        holder.sex.setVisibility(View.GONE);
        holder.theme.setVisibility(View.GONE);
        holder.theme.setText("图片来源：pixabay");
        holder.time.setText("");
        if (uploadPic.getUploadPicCity() != null && !uploadPic.getUploadPicCity().equals("null")) {
            holder.location.setText("" + uploadPic.getUploadPicCity());
        } else {
            holder.location.setText("");
        }
        if (uploadPic.getPics() != null && uploadPic.getPics().size() > 0) {
            GlideUtil.setImageBitmapNoOptions(uploadPic.getBrosePic(), holder.pic);
        }
    }

    private void bindUnsplash(MyViewHolder holder, ExtendUploadPic uploadPic, int position) {
        GlideUtil.setImageBitmapNoOptions(uploadPic.getPhoto(), holder.photo);
        holder.photo.setOnClickListener(null);
        holder.nickName.setText(uploadPic.getNickName());
        holder.sex.setVisibility(View.GONE);
        holder.theme.setVisibility(View.GONE);
        holder.theme.setText("");
        String date = uploadPic.getUploadPicTime().split("T")[0] + " 00:00:00";
        holder.time.setText(DateUtil.getHeadway(DateUtil.convertTimeToLong(date)));
        if (uploadPic.getUploadPicCity() != null && !uploadPic.getUploadPicCity().equals("null")) {
            holder.location.setText("" + uploadPic.getUploadPicCity());
        } else {
            holder.location.setText("");
        }
        if (uploadPic.getPics() != null && uploadPic.getPics().size() > 0) {
            if(gridCount == 1){
                GlideUtil.setImageBitmapNoOptions(uploadPic.getPics().get(0).getUploadPicUrl().getFilePath(), holder.pic);
            }else{
                GlideUtil.setImageBitmapNoOptions(uploadPic.getBrosePic(), holder.pic);
            }
        }
    }

    private void bindFootStep(MyViewHolder holder, ExtendUploadPic uploadPic, int position) {
        GlideUtil.setImageBitmapNoOptions(uploadPic.getPhoto(), holder.photo);
        holder.photo.setOnClickListener(v->{
            Bundle bundle = new Bundle();
            bundle.putString("userName", uploadPic.getUserName());
            ((BaseActivity)mContext).startActivity(MyPageActivity.class, bundle);
        });
        holder.nickName.setText(uploadPic.getNickName());
        if ("脚步".equals(uploadPic.getSex())) {
            holder.sex.setVisibility(View.VISIBLE);
            holder.sex.setImageResource(uploadPic.getSex() == 0 ? R.drawable.woman : R.drawable.man);
        } else {
            holder.sex.setVisibility(View.GONE);
        }
        if (uploadPic.getUplodPicTheme() != null && !uploadPic.getUplodPicTheme().isEmpty()) {
            holder.theme.setText(uploadPic.getUplodPicTheme());
            holder.theme.setVisibility(View.VISIBLE);
        } else {
            holder.theme.setVisibility(View.GONE);
            holder.theme.setText("");
        }
        holder.time.setText(DateUtil.getHeadway(DateUtil.convertTimeToLong(uploadPic.getUploadPicTime())));
        holder.location.setText(( "" +
                ((uploadPic.getUploadPicProvince() == null || "null".equals(uploadPic.getUploadPicProvince())) ? "" : uploadPic.getUploadPicProvince())
                 + ((uploadPic.getUploadPicCity() == null || "null".equals(uploadPic.getUploadPicCity())) ? "" : uploadPic.getUploadPicCity()) ));
        if (uploadPic.getPics() != null && uploadPic.getPics().size() > 0) {
            GlideUtil.setImageBitmapNoOptions(uploadPic.getPics().get(0).getUploadPicUrl().getFilePath(), holder.pic);
        }
    }

    private void reqThumbUpPic(ExtendUploadPic uploadPic, int position) {
        ThumbupUploadPicReq req = new ThumbupUploadPicReq();
        req.setGallery(uploadPic.getGalleryType());
        if("脚步".equals(uploadPic.getGalleryType())){
            req.setThumbupUpUserId(SPUtil.getInstance().getUser().getId());
            req.setUploadPicId(uploadPic.getId());
        }else if("unsplash".equals(uploadPic.getGalleryType()) || "pixabay".equals(uploadPic.getGalleryType())){
            req.setUid(uploadPic.getThirdUid());
            req.setUrl(uploadPic.getPics().get(0).getUploadPicUrl().getFilePath());
        }else if("pexels".equals(uploadPic.getGalleryType())){
            req.setUrl(uploadPic.getPics().get(0).getUploadPicUrl().getFilePath());
        }
        new SocketRequest().request(MyApplication.ClientSocket, req, new SocketRequest.IRequest() {
            @Override
            public void onError(String msg) {
                new Handler(Looper.getMainLooper()).post(() -> {
                    uploadPic.setUploading(false);
                    Toast.makeText(mContext, msg, Toast.LENGTH_SHORT).show();
                    notifyItemChanged(position);
                });
            }

            @Override
            public void onFinished(ResponseMessage mResponse) {
                new Handler(Looper.getMainLooper()).post(() -> {
                    uploadPic.setUploading(false);
                    if (mResponse.getCode() == 200) {
                        uploadPic.setThumbUpNum((uploadPic.getThumbUpNum() + 1));
                        uploadPic.setHasThubmUp(1);
                    } else {
                        Toast.makeText(mContext, mResponse.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                    notifyItemChanged(position);
                });
            }
        });
    }

    private void reqCollectPic(ExtendUploadPic uploadPic, int type, int position) {
        CollectUploadPicReq req = new CollectUploadPicReq();
        req.setType(type);
        req.setGallery(uploadPic.getGalleryType());
        if("脚步".equals(uploadPic.getGalleryType())){
            req.setCollectUserId(SPUtil.getInstance().getUser().getId());
            req.setUploadPicId(uploadPic.getId());
        }else if("unsplash".equals(uploadPic.getGalleryType()) || "pixabay".equals(uploadPic.getGalleryType())){
            req.setUid(uploadPic.getThirdUid());
            req.setUrl(uploadPic.getPics().get(0).getUploadPicUrl().getFilePath());
        }else if("pexels".equals(uploadPic.getGalleryType())){
            req.setUrl(uploadPic.getPics().get(0).getUploadPicUrl().getFilePath());
        }
        new SocketRequest().request(MyApplication.ClientSocket, req, new SocketRequest.IRequest() {
            @Override
            public void onError(String msg) {
                new Handler(Looper.getMainLooper()).post(() -> {
                    uploadPic.setUploading(false);
                    Toast.makeText(mContext, msg, Toast.LENGTH_SHORT).show();
                    notifyItemChanged(position);
                });
            }

            @Override
            public void onFinished(ResponseMessage mResponse) {
                new Handler(Looper.getMainLooper()).post(() -> {
                    uploadPic.setUploading(false);
                    if (mResponse.getCode() == 200) {
                        if (type == 0) {
                            uploadPic.setHasCollected(1);
                        } else {
                            uploadPic.setHasCollected(0);
                        }
                    } else {
                        Toast.makeText(mContext, mResponse.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                    notifyItemChanged(position);
                });
            }
        });
    }

    private void reqLikePic(ExtendUploadPic uploadPic, int type, int position) {
        LikeUploadPicReq req = new LikeUploadPicReq();
        req.setGallery(uploadPic.getGalleryType());
        req.setType(type);
        if("脚步".equals(uploadPic.getGalleryType())){
            req.setLikeUserId(SPUtil.getInstance().getUser().getId());
            req.setUploadPicId(uploadPic.getId());
        }else if("unsplash".equals(uploadPic.getGalleryType()) || "pixabay".equals(uploadPic.getGalleryType())){
            req.setUid(uploadPic.getThirdUid());
            req.setUrl(uploadPic.getPics().get(0).getUploadPicUrl().getFilePath());
        }else if("pexels".equals(uploadPic.getGalleryType())){
            req.setUrl(uploadPic.getPics().get(0).getUploadPicUrl().getFilePath());
        }
        new SocketRequest().request(MyApplication.ClientSocket, req, new SocketRequest.IRequest() {
            @Override
            public void onError(String msg) {
                new Handler(Looper.getMainLooper()).post(() -> {
                    uploadPic.setUploading(false);
                    Toast.makeText(mContext, msg, Toast.LENGTH_SHORT).show();
                    notifyItemChanged(position);
                });
            }

            @Override
            public void onFinished(ResponseMessage mResponse) {
                new Handler(Looper.getMainLooper()).post(() -> {
                    uploadPic.setUploading(false);
                    if (mResponse.getCode() == 200) {
                        if (type == 0) {
                            uploadPic.setHasLiked(1);
                        } else {
                            uploadPic.setHasLiked(0);
                        }
                    } else {
                        Toast.makeText(mContext, mResponse.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                    notifyItemChanged(position);
                });
            }
        });
    }

    @Override
    public int getItemCount() {
        return mList != null ? mList.size() : 0;
    }

    static class MyViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.photo)
        PolygonImageView photo;
        @BindView(R.id.nick_name)
        TextView nickName;
        @BindView(R.id.sex)
        ImageView sex;
        @BindView(R.id.theme)
        TextView theme;
        @BindView(R.id.time)
        TextView time;
        @BindView(R.id.gallery)
        ImageView gallery;
        @BindView(R.id.num)
        TextView num;
        @BindView(R.id.gallery_ll)
        LinearLayout galleryLl;
        @BindView(R.id.pic)
        ImageView pic;
        @BindView(R.id.like_iv)
        ImageView likeIv;
        @BindView(R.id.like_tv)
        TextView likeTv;
        @BindView(R.id.like_rl)
        RelativeLayout likeRl;
        @BindView(R.id.thumb_up_iv)
        ImageView thumbUpIv;
        @BindView(R.id.thumb_up_tv)
        TextView thumbUpTv;
        @BindView(R.id.thumb_up_rl)
        RelativeLayout thumbUpRl;
        @BindView(R.id.follow_iv)
        ImageView followIv;
        @BindView(R.id.follow_num_tv)
        TextView followNumTv;
        @BindView(R.id.follow_rl)
        RelativeLayout followRl;
        @BindView(R.id.thumb_progress)
        ProgressBar thumbProgress;
        @BindView(R.id.location)
        TextView location;

        @BindView(R.id.comment_num)
        TextView commentNum;
        @BindView(R.id.comment_num_ll)
        LinearLayout commentNumLl;
        @BindView(R.id.comment_top_ll)
        LinearLayout commentTopLl;
        @BindView(R.id.comment_top1_ll)
        LinearLayout commentTop1Ll;
        @BindView(R.id.comment_top2_ll)
        LinearLayout commentTop2Ll;
        @BindView(R.id.comment_top3_ll)
        LinearLayout commentTop3Ll;
        @BindView(R.id.comment_top1_photo)
        PolygonImageView commentTop1Photo;
        @BindView(R.id.comment_top2_photo)
        PolygonImageView commentTop2Photo;
        @BindView(R.id.comment_top3_photo)
        PolygonImageView commentTop3Photo;
        @BindView(R.id.comment_top1_name)
        TextView commentTop1Name;
        @BindView(R.id.comment_top2_name)
        TextView commentTop2Name;
        @BindView(R.id.comment_top3_name)
        TextView commentTop3Name;
        @BindView(R.id.comment_top1_text)
        TextView commentTop1Text;
        @BindView(R.id.comment_top2_text)
        TextView commentTop2Text;
        @BindView(R.id.comment_top3_text)
        TextView commentTop3Text;

        public MyViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public void addDatas(List<ExtendUploadPic> uploadPics) {
        if (uploadPics ==null || uploadPics.size() <= 0) {
            return;
        }
        synchronized (mList) {
            int position = mList.size();
            mList.addAll(uploadPics);
            notifyItemRangeInserted(position, uploadPics.size());
        }
    }

    public void clearData() {
        synchronized (mList){
            mList.clear();
            notifyDataSetChanged();
            itemHeight.clear();
        }
    }
}
