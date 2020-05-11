package tl.pojul.com.fastim.View.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.pojul.fastIM.entity.PicComment;
import com.pojul.fastIM.entity.User;
import com.pojul.fastIM.message.request.GetPicCommentsReq;
import com.pojul.fastIM.message.request.PicCommentReq;
import com.pojul.fastIM.message.response.GetPicCommentsResp;
import com.pojul.fastIM.message.response.PicCommentResp;
import com.pojul.objectsocket.message.ResponseMessage;
import com.pojul.objectsocket.socket.SocketRequest;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadmoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.umeng.socialize.ShareAction;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.UMShareListener;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.media.UMImage;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import tl.pojul.com.fastim.MyApplication;
import tl.pojul.com.fastim.R;
import tl.pojul.com.fastim.View.Adapter.PicReplyAdapter;
import tl.pojul.com.fastim.View.widget.PolygonImage.view.PolygonImageView;
import tl.pojul.com.fastim.View.widget.sowingmap.SowingMap;
import tl.pojul.com.fastim.util.DialogUtil;
import tl.pojul.com.fastim.util.SPUtil;

public class PicCommentActivity extends BaseActivity {

    @BindView(R.id.back)
    ImageView back;
    @BindView(R.id.photo)
    PolygonImageView photo;
    @BindView(R.id.nick_name)
    TextView nickName;
    @BindView(R.id.theme)
    TextView theme;
    @BindView(R.id.location)
    TextView location;
    @BindView(R.id.time)
    TextView time;
    @BindView(R.id.pics)
    SowingMap pics;
    @BindView(R.id.pic)
    ImageView pic;
    @BindView(R.id.comment_num)
    TextView commentNum;
    @BindView(R.id.comments)
    RecyclerView comments;
    @BindView(R.id.refresh)
    SmartRefreshLayout refresh;
    @BindView(R.id.comment_ll)
    LinearLayout commentLl;
    @BindView(R.id.forward_ll)
    LinearLayout forwardLl;
    @BindView(R.id.root_view)
    RelativeLayout rootView;

    private String picId;
    private String picNickName;
    private String timeStr;
    private String locStr;
    private String photoStr;
    private String themeStr;
    private String gallery;

    private int page = 0;
    private int num = 10;
    boolean newStart = true;
    PicReplyAdapter adapter;
    private List<PicComment> picComments = new ArrayList<>();

    private List<String> picUrls/* = new ArrayList<String>() {{
        add("http://e.hiphotos.baidu.com/image/pic/item/fcfaaf51f3deb48fd0e9be27fc1f3a292cf57842.jpg");
        add("http://f.hiphotos.baidu.com/image/pic/item/3812b31bb051f8195bf514a9d6b44aed2f73e705.jpg");
        add("http://c.hiphotos.baidu.com/image/pic/item/09fa513d269759eeef490028befb43166d22df3c.jpg");
        add("http://imglf0.nosdn0.126.net/img/Sk5OZVhRaUZtSFg5bVR3SGtOeTlIQzJCdFRRUUpQYUNRTllSUzNKVVpTcXBMSmNZU2Q5T1pRPT0.jpg?imageView&thumbnail=500x0&quality=96&stripmeta=0&type=jpg");
        add("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1532179773763&di=9f843b3e3a13e0f103711a7ba1a911cf&imgtype=0&src=http%3A%2F%2Fimg.mp.itc.cn%2Fupload%2F20161018%2F3bf24a8ceb974b0f9cf354fdb86d1271_th.jpg");
    }}*/;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pic_comment);
        ButterKnife.bind(this);

        initView();

    }

    private void initView() {
        picId = getIntent().getStringExtra("picId");
        picNickName = getIntent().getStringExtra("picNickName");
        timeStr = getIntent().getStringExtra("timeStr");
        locStr = getIntent().getStringExtra("locStr");
        photoStr = getIntent().getStringExtra("photoStr");
        themeStr = getIntent().getStringExtra("themeStr");
        gallery = getIntent().getStringExtra("gallery");
        picUrls = getIntent().getStringArrayListExtra("picUrls");
        if(picId == null || picId.isEmpty() || "null".equals(picId) || gallery == null ||
                gallery.isEmpty() || picUrls == null || picUrls.size() <= 0){
            finish();
            return;
        }
        if(photoStr != null){
            Glide.with(this).load(photoStr).into(photo);
        }
        if(picNickName != null){
            nickName.setText(picNickName);
        }
        if(themeStr == null || themeStr.isEmpty() || "null".equals(themeStr)){
            theme.setVisibility(View.GONE);
        }else{
            theme.setVisibility(View.VISIBLE);
            theme.setText(themeStr);
        }
        if(locStr == null || locStr.isEmpty() || "null".equals(locStr) || "nullnull".equals(locStr)){
            location.setVisibility(View.GONE);
        }else{
            location.setVisibility(View.VISIBLE);
            location.setText(locStr);
        }
        if(timeStr == null || timeStr.isEmpty() || "null".equals(timeStr)){
            time.setText("");
        }else{
            time.setText(timeStr);
        }
        if (picUrls.size() == 1) {
            pic.setVisibility(View.VISIBLE);
            pics.setVisibility(View.GONE);
            Glide.with(this).load(picUrls.get(0)).into(pic);
            pic.setOnClickListener(v -> {
                Bundle bundle = new Bundle();
                ArrayList<String> urls = new ArrayList<>();
                urls.add(picUrls.get(0));
                bundle.putStringArrayList("urls", urls);
                startActivity(GalleryActivity.class, bundle);
            });
        } else {
            pic.setVisibility(View.GONE);
            pics.setVisibility(View.VISIBLE);
            pics.setImgs(picUrls);
            pics.startLoop();
            pics.setOnItemClickListener(position -> {
                Bundle bundle = new Bundle();
                ArrayList<String> urls = new ArrayList<>();
                urls.add(picUrls.get(position));
                bundle.putStringArrayList("urls", urls);
                startActivity(GalleryActivity.class, bundle);
            });
        }
        refresh.setPrimaryColors(Color.parseColor("#bbbbbb"));
        refresh.setEnableLoadmore(true);
        refresh.setEnableRefresh(true);
        refresh.setOnLoadmoreListener(refreshlayout -> {
            reqComments();
        });
        refresh.setOnRefreshListener(refreshlayout -> {
            newStart = true;
            refresh.setEnableLoadmore(true);
            reqComments();
        });
        LinearLayoutManager manager = new LinearLayoutManager(this);
        comments.setLayoutManager(manager);
        adapter = new PicReplyAdapter(this, picComments);
        comments.setAdapter(adapter);
        comments.setNestedScrollingEnabled(false);

        DialogUtil.getInstance().showLoadingSimple(this, getWindow().getDecorView());
        reqComments();
    }

    private void reqComments() {
        GetPicCommentsReq req = new GetPicCommentsReq();
        int tempPage;
        if(newStart){
            newStart = false;
            adapter.clearData();
            page = 0;
            tempPage = page;
        }else{
            tempPage = page + 1;
        }
        req.setNum(num);
        req.setPage(tempPage);
        req.setPicId(picId);
        new SocketRequest().request(MyApplication.ClientSocket, req, new SocketRequest.IRequest() {
            @Override
            public void onError(String msg) {
                runOnUiThread(()->{
                    refresh.finishRefresh();
                    refresh.finishLoadmore();
                    DialogUtil.getInstance().dimissLoadingDialog();
                    showShortToas(msg);
                });
            }

            @Override
            public void onFinished(ResponseMessage mResponse) {
                runOnUiThread(()->{
                    refresh.finishRefresh();
                    refresh.finishLoadmore();
                    DialogUtil.getInstance().dimissLoadingDialog();
                    if(mResponse.getCode() == 200){
                        GetPicCommentsResp resp = (GetPicCommentsResp) mResponse;
                        adapter.addData(resp.getPicComments());
                        if(resp.getPicComments().size() > 0) {
                            commentNum.setText("评论·" + resp.getTotalComments());
                            page = tempPage;
                        }else{
                            refresh.setEnableLoadmore(false);
                        }
                    }
                });
            }
        });
    }

    @OnClick({R.id.back, R.id.comment_ll, R.id.forward_ll})
    public void onClick(View v){
        switch (v.getId()){
            case R.id.back:
                finish();
                break;
            case R.id.comment_ll:
                comment(0, -1);
                break;
            case R.id.forward_ll:
                sharePic();
                break;
        }
    }

    private void sharePic() {
        String url = picUrls.get(0);
        UMImage image1 = new UMImage(this, url);//网络图片
        new ShareAction(this)
                .withMedia(image1)
                .setDisplayList(SHARE_MEDIA.WEIXIN, SHARE_MEDIA.WEIXIN_CIRCLE)
                .setCallback(umShareListener)
                .open();
        /*UMWeb web = new UMWeb("http://a.hiphotos.baidu.com/image/pic/item/2f738bd4b31c8701e96739342a7f9e2f0608ff0b.jpg");
        UMImage image = new UMImage(this, "http://a.hiphotos.baidu.com/image/pic/item/2f738bd4b31c8701e96739342a7f9e2f0608ff0b.jpg");
        web.setTitle("来自脚步社区.每日一图");
        web.setDescription("描述");
        web.setThumb(image);
        new ShareAction(this)
                .withMedia(web)
                .setDisplayList(SHARE_MEDIA.WEIXIN, SHARE_MEDIA.WEIXIN_CIRCLE)
                .setCallback(umShareListener)
                .open();*/
    }

    private UMShareListener umShareListener = new UMShareListener() {

        @Override
        public void onStart(SHARE_MEDIA share_media) {
        }

        @Override
        public void onResult(SHARE_MEDIA share_media) {
        }

        @Override
        public void onError(SHARE_MEDIA share_media, Throwable throwable) {
        }

        @Override
        public void onCancel(SHARE_MEDIA share_media) {
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        UMShareAPI.get(this).onActivityResult(requestCode,resultCode,data);
    }

    public void comment(int level, long oneLevelId) {
        DialogUtil.getInstance().showSubReplyDialog(this, commentLl, "");
        DialogUtil.getInstance().setDialogClick(str -> {
            DialogUtil.getInstance().showLoadingSimple(PicCommentActivity.this, getWindow().getDecorView());
            PicCommentReq req = new PicCommentReq();
            req.setGallery(gallery);
            req.setLevel(level);
            req.setOneLevelId(oneLevelId);
            req.setText(str);
            req.setUploadPicId(picId);
            new SocketRequest().request(MyApplication.ClientSocket, req, new SocketRequest.IRequest() {
                @Override
                public void onError(String msg) {
                    runOnUiThread(()->{
                        DialogUtil.getInstance().dimissLoadingDialog();
                        showShortToas("操作失败");
                    });
                }

                @Override
                public void onFinished(ResponseMessage mResponse) {
                    runOnUiThread(()->{
                        DialogUtil.getInstance().dimissLoadingDialog();
                        PicCommentResp resp = (PicCommentResp) mResponse;
                        if(resp.getCode() == 200){
                            PicComment picComment = new PicComment();
                            picComment.setId(resp.getInsertId());
                            picComment.setUploadPicId(picId);
                            User user = SPUtil.getInstance().getUser();
                            picComment.setCommentUserName(user.getUserName());
                            picComment.setCommentText(str);
                            picComment.setCommentLevel(level);
                            picComment.setOneLevelId(oneLevelId);
                            picComment.setGallery(gallery);
                            picComment.setNickName(user.getNickName());
                            picComment.setPhoto(user.getPhoto().getFilePath());
                            picComment.setTimeMilli(System.currentTimeMillis());
                            picComment.setSex(user.getSex());
                            if(level == 0){
                                adapter.addTopData(picComment);
                            }else{
                                adapter.addSubCommentData(picComment);
                            }
                        }else{
                            showShortToas("操作失败");
                        }
                    });
                }
            });
        });
    }


    @Override
    protected void onPause() {
        super.onPause();
        if (pics == null || pics.getVisibility() == View.GONE) {
            return;
        }
        pics.stopLoop();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (pics == null || pics.getVisibility() == View.GONE) {
            return;
        }
        pics.startLoop();
    }

    public View getRootView(){
        return rootView;
    }

    @Override
    protected void onDestroy() {
        try {
            pics.onDestory();
        } catch (Exception e) {
        }
        super.onDestroy();
    }
}
