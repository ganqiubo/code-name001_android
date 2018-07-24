package tl.pojul.com.fastim.View.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;

import com.baidu.location.BDLocation;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.pojul.fastIM.entity.Pic;
import com.pojul.fastIM.entity.UploadPic;
import com.pojul.fastIM.message.request.UploadPicReq;
import com.pojul.objectsocket.message.ResponseMessage;
import com.pojul.objectsocket.socket.SocketRequest;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import tl.pojul.com.fastim.MyApplication;
import tl.pojul.com.fastim.R;
import tl.pojul.com.fastim.View.Adapter.UploadImgAdapter;
import tl.pojul.com.fastim.View.widget.FlowTagView;
import tl.pojul.com.fastim.dao.UploadPicDao;
import tl.pojul.com.fastim.socket.upload.PicUploadManager;
import tl.pojul.com.fastim.util.ArrayUtil;
import tl.pojul.com.fastim.util.DateUtil;
import tl.pojul.com.fastim.util.DialogUtil;
import tl.pojul.com.fastim.util.SPUtil;

public class UploadPicActivity extends BaseActivity {

    @BindView(R.id.upload_pic_labels)
    FlowTagView uploadPicLabels;
    @BindView(R.id.show_vague)
    RadioButton showVague;
    @BindView(R.id.show_accurate)
    RadioButton showAccurate;
    @BindView(R.id.show_location_note)
    TextView showLocationNote;
    @BindView(R.id.get_location)
    TextView getLocation;
    @BindView(R.id.location)
    TextView location;
    @BindView(R.id.upload_img)
    RecyclerView uploadImgs;
    @BindView(R.id.upload)
    TextView upload;
    @BindView(R.id.location_ll)
    LinearLayout locationLl;
    @BindView(R.id.upload_pic_theme)
    EditText uploadPicTheme;
    @BindView(R.id.loc_note)
    EditText locNote;
    @BindView(R.id.self_label)
    EditText selfLabelEt;

    private UploadImgAdapter uploadImgAdapter;
    //private UploadPic uploadPic;
    private int uploadPicType;//1: 普通图片 2: 位置图片
    private BDLocation mBDLocation;
    private int uploadPicEditMode;//0: 非修改模式; 1: 本地修改模式 2: 网络修改模式 3:上传失败修改
    private UploadPic savedUploadPic;
    private LocationInfo locationInfo;

    private List<String> pics = new ArrayList<String>() {{
        add("添加");
    }};

    private List<String> datas1 = new ArrayList<String>() {{
        add("风景");
        add("生活");
        add("美食");
        add("建筑");
        add("自拍");
        add("摄影");
        add("手机壁纸");
        add("文艺");
        add("清新");
        add("美女");
        add("萝莉");
        add("迷人");
        add("萌宠");
        add("写真");
        add("沙滩");
        add("大海");
        add("古典");
        add("唯美");
        add("内涵");
        add("可爱");
        add("校花");
        add("家居");
        add("旅游");
    }};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_pic);
        ButterKnife.bind(this);

        initView();

    }

    private void initView() {
        uploadPicType = getIntent().getIntExtra("uploadPicType", 2);
        uploadPicEditMode = getIntent().getIntExtra("uploadPicEditMode", 0);
        if(uploadPicEditMode == 1 || uploadPicEditMode == 3){
            try{
                Gson gs = new GsonBuilder().disableHtmlEscaping().create();
                savedUploadPic = gs.fromJson(getIntent().getStringExtra("savedUploadPic"), UploadPic.class);
                uploadPicType = savedUploadPic.getUploadPicType();
            } catch (Exception e){savedUploadPic = null;}
            if(savedUploadPic == null){
                showShortToas("数据异常");
                finish();
                return;
            }
        }
        uploadPicLabels.datas(datas1)
                .listener(new FlowTagView.OnTagSelectedListener() {
                    @Override
                    public void onTagSelected(FlowTagView view, int position) {
                        //showShortToas("选中了:" + position);
                    }
                }).commit();
        showAccurate.setChecked(true);
        uploadImgs.setLayoutManager(new GridLayoutManager(this, 3));
        uploadImgAdapter = new UploadImgAdapter(this, pics);
        uploadImgs.setAdapter(uploadImgAdapter);

        if (uploadPicType == 1) {
            locationLl.setVisibility(View.GONE);
        } else {
            locationLl.setVisibility(View.VISIBLE);
        }
        if(uploadPicEditMode == 1 || uploadPicEditMode == 3){
            setDatas();
        }
    }

    private void setDatas() {
        if(savedUploadPic.getUplodPicTheme() != null){
            uploadPicTheme.setText(savedUploadPic.getUplodPicTheme());
        }else{
            uploadPicTheme.setText("");
        }
        if(savedUploadPic.getUplodPicLabel() != null && savedUploadPic.getUplodPicLabel().length() > 0){
            String[] allLabels = savedUploadPic.getUplodPicLabel().split(",");
            List<String> selfLabels = new ArrayList<>();
            List<Integer> labels = new ArrayList<>();
            for(int i = 0; i < allLabels.length; i++){
                if(allLabels == null){
                    continue;
                }
                int position = containLabel(allLabels[i]);
                if(position != -1){
                    labels.add(position);
                }else{
                    selfLabels.add(allLabels[i]);
                }
            }
            selfLabelEt.setText(ArrayUtil.toCommaSplitStr(selfLabels));
            new Handler(getMainLooper()).postDelayed(()->{
                uploadPicLabels.setSelectedTags(labels);
            }, 200);
        }
        if(savedUploadPic.getPics() != null && savedUploadPic.getPics().size() > 0){
            List<String> savedPics = new ArrayList<>();
            for (int i = 0; i < savedUploadPic.getPics().size(); i++){
                Pic pic = savedUploadPic.getPics().get(i);
                if(pic == null){
                    continue;
                }
                savedPics.add(pic.getUploadPicUrl().getFilePath());
            }
            pics.addAll(0, savedPics);
            uploadImgAdapter.notifyDataSetChanged();
        }

        //地理位置
        if(uploadPicType == 1){
            return;
        }

        locationInfo = new LocationInfo();
        locationInfo.country = savedUploadPic.getUploadPicCountry();
        locationInfo.province = savedUploadPic.getUploadPicProvince();
        locationInfo.city = savedUploadPic.getUploadPicCity();
        locationInfo.district = savedUploadPic.getUploadPicDistrict();
        locationInfo.longitude = savedUploadPic.getUploadPicLongitude();
        locationInfo.latitude = savedUploadPic.getUploadPicLatitude();
        locationInfo.altitude = savedUploadPic.getUploadPicAltitude();
        locationInfo.picLocType = savedUploadPic.getPicLocType();

        if(savedUploadPic.getUploadPicAddr() != null){
            location.setText(savedUploadPic.getUploadPicAddr());
        }
        if(savedUploadPic.getUploadPicLocnote() != null){
            locNote.setText(savedUploadPic.getUploadPicLocnote());
        }
        if(savedUploadPic.getUploadPicLocshow() == 1){
            showVague.setChecked(true);
        }else{
            showAccurate.setChecked(true);
        }
    }

    public int containLabel(String label){
        for(int i = 0; i < datas1.size(); i++){
            if(datas1.get(i).equals(label)){
                return i;
            }
        }
        return -1;
    }

    @OnClick({R.id.show_location_note, R.id.get_location, R.id.upload, R.id.upload_pic_save, R.id.upload_pic_back})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.show_location_note:
                DialogUtil.getInstance().showNoteDialog(UploadPicActivity.this,
                        "显示位置说明",
                        "模糊位是置指图片将会显示在地图附近范围内的随机位置上;\n精确位置将会按照图片的实际地理位置显示");
                break;
            case R.id.get_location:
                MyApplication.getApplication().registerLocationListener(iLocationListener);
                MyApplication.getApplication().requireLocAddr(true);
                MyApplication.getApplication().mLocationClient.restart();
                getLocation.setText("获取中...");
                break;
            case R.id.upload:
                uploadPic();
                break;
            case R.id.upload_pic_save:
                saveUploadPic();
                break;
            case R.id.upload_pic_back:
                finish();
                break;
        }
    }

    private void saveUploadPic() {
        UploadPic uploadPic = getUploadPic();
        if(uploadPic == null){
            showShortToas("数据错误");
            return;
        }
        int result = -1;
        if(uploadPicEditMode == 0){
            result = new UploadPicDao().saveUploadPic(uploadPic, SPUtil.getInstance().getUser().getUserName(), 0);
        }else if(uploadPicEditMode == 1){
            result = new UploadPicDao().modifyUploadPic(uploadPic, savedUploadPic.getId(), SPUtil.getInstance().getUser().getUserName(), 0);
        }else if(uploadPicEditMode == 2){

        }else if(uploadPicEditMode == 3){
            result = new UploadPicDao().modifyUploadPic(uploadPic, savedUploadPic.getId(), SPUtil.getInstance().getUser().getUserName(), 1);
        }
        if(uploadPicEditMode == 0 || uploadPicEditMode == 1 || uploadPicEditMode == 3){
            if(result >= 0){
                showShortToas("保存成功");
                this.finish();
            }else{
                showShortToas("保存失败");
            }
        }
    }

    private MyApplication.ILocationListener iLocationListener = new MyApplication.ILocationListener() {
        @Override
        public void onReceive(BDLocation bdLocation) {
            location.setText(bdLocation.getAddrStr());
            MyApplication.getApplication().unRegisterLocationListener(iLocationListener);
            mBDLocation = bdLocation;
            getLocation.setText("获取位置");
        }

        @Override
        public void onFail(String msg) {
            MyApplication.getApplication().unRegisterLocationListener(iLocationListener);
            getLocation.setText("重新获取");
            showShortToas("位置获取失败");
        }
    };

    public UploadPic getUploadPic(){
        List<Pic> pics = uploadImgAdapter.getPics();
        UploadPic uploadPic = new UploadPic();
        uploadPic.setUploadPicType(uploadPicType);
        uploadPic.setUplodPicTheme(uploadPicTheme.getText().toString());
        String selfLabel = (selfLabelEt.getText().toString().replace("，", ",")).replace(" ", "");
        if (selfLabel.length() > 0) {
            uploadPic.setUplodPicLabel((uploadPicLabels.getTags()
                    .replace(" ", "") + "," + selfLabel));
        } else {
            uploadPic.setUplodPicLabel((uploadPicLabels.getTags().replace(" ", "")));
        }
        uploadPic.setPics(pics);
        if (uploadPicType == 2) {
            if(mBDLocation != null){
                uploadPic.setUploadPicCountry(mBDLocation.getCountry());
                uploadPic.setUploadPicProvince(mBDLocation.getProvince());
                uploadPic.setUploadPicCity(mBDLocation.getCity());
                uploadPic.setUploadPicDistrict(mBDLocation.getDistrict());
                uploadPic.setUploadPicLongitude(mBDLocation.getLongitude());
                uploadPic.setUploadPicLatitude(mBDLocation.getLatitude());
                uploadPic.setUploadPicAltitude(mBDLocation.getAltitude());
                if (mBDLocation.getLocType() == 61) {
                    uploadPic.setPicLocType(1);
                } else if (mBDLocation.getLocType() == 161) {
                    uploadPic.setPicLocType(2);
                }
            }else if(locationInfo != null){
                uploadPic.setUploadPicCountry(locationInfo.country);
                uploadPic.setUploadPicProvince(locationInfo.province);
                uploadPic.setUploadPicCity(locationInfo.city);
                uploadPic.setUploadPicDistrict(locationInfo.district);
                uploadPic.setUploadPicLongitude(locationInfo.longitude);
                uploadPic.setUploadPicLatitude(locationInfo.latitude);
                uploadPic.setUploadPicAltitude(locationInfo.altitude);
                uploadPic.setPicLocType(locationInfo.picLocType);
            }
            uploadPic.setUploadPicAddr(location.getText().toString());
            uploadPic.setUploadPicLocnote(locNote.getText().toString());
            uploadPic.setUploadPicLocshow(showAccurate.isChecked() ? 2 : 1);
        }
        uploadPic.setUploadPicTime(DateUtil.getFormatDate());
        return uploadPic;
    }

    public void uploadPic() {
        if (!MyApplication.getApplication().isConnected()) {
            showShortToas("与服务器已断开连接");
            return;
        }
        List<Pic> pics = uploadImgAdapter.getPics();
        if (pics.size() <= 0) {
            showShortToas("上传图片不能为空");
            return;
        }
        if (uploadPicType == 2) {
            if ("".equals(location.getText().toString()) && mBDLocation == null) {
                showShortToas("地理位置不能为空");
                return;
            }
        }
        UploadPic uploadPic = getUploadPic();
        if(uploadPic == null){
            showShortToas("数据错误");
            return;
        }

        long uploadPicId = -1;
        if((uploadPicEditMode == 1 || uploadPicEditMode == 3) && savedUploadPic != null){
            uploadPicId = savedUploadPic.getId();
        }
        PicUploadManager.getInstance().addTask(uploadPic, uploadPicId, 0);
        showShortToas("已添加到上传任务中");
        finish();

        /*new SocketRequest().resuest(MyApplication.ClientSocket, uploadPicReq, new SocketRequest.IRequest() {
            @Override
            public void onError(String msg) {
                runOnUiThread(() -> {
                    showShortToas(msg);
                });
            }

            @Override
            public void onFinished(ResponseMessage mResponse) {
                runOnUiThread(() -> {
                    if (mResponse.getCode() == 200) {
                        showShortToas("上传成功");
                        if((uploadPicEditMode == 1 || uploadPicEditMode == 3) && savedUploadPic != null){
                            new UploadPicDao().deleteUploadPic(savedUploadPic.getId());
                        }
                    } else {
                        showShortToas(mResponse.getMessage());
                    }
                });
            }
        });*/

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        uploadImgAdapter.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        MyApplication.getApplication().unRegisterLocationListener(iLocationListener);
    }

    class LocationInfo{
        public String country;
        public String province;
        public String city;
        public String district;
        public double longitude;
        public double latitude;
        public double altitude;
        public int picLocType;
    }

}
