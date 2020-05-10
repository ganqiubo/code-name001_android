package tl.pojul.com.fastim.View.activity;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.baidu.location.BDLocation;
import com.baidu.mapapi.utils.DistanceUtil;
import com.google.gson.Gson;
import com.pojul.fastIM.entity.CommunityRoom;
import com.pojul.fastIM.entity.User;
import com.pojul.fastIM.message.chat.CommunityMessage;
import com.pojul.fastIM.message.chat.TagCommuMessage;
import com.pojul.objectsocket.message.BaseMessage;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import tl.pojul.com.fastim.MyApplication;
import tl.pojul.com.fastim.R;
import tl.pojul.com.fastim.View.Adapter.PicPickerAdapter;
import tl.pojul.com.fastim.View.widget.FlowTagView;
import tl.pojul.com.fastim.View.widget.UserFilterView;
import tl.pojul.com.fastim.map.baidu.LocationManager;
import tl.pojul.com.fastim.util.ArrayUtil;
import tl.pojul.com.fastim.util.DialogUtil;
import tl.pojul.com.fastim.util.MyDistanceUtil;
import tl.pojul.com.fastim.util.SPUtil;

public class TagMessageActivity extends BaseActivity {

    @BindView(R.id.header)
    RelativeLayout header;
    @BindView(R.id.header_line)
    View headerLine;
    @BindView(R.id.message_title)
    EditText messageTitle;
    @BindView(R.id.message_labels)
    FlowTagView messageLabels;
    @BindView(R.id.self_label)
    EditText selfLabelEt;
    @BindView(R.id.message_note)
    EditText messageNote;
    @BindView(R.id.message_img)
    RecyclerView messageImg;
    @BindView(R.id.sure)
    TextView sure;
    @BindView(R.id.filter_view)
    UserFilterView userFilterView;
    @BindView(R.id.label_note)
    TextView labelNote;
    @BindView(R.id.filter_ll)
    LinearLayout filterLl;
    @BindView(R.id.level)
    TextView level;

    private TagCommuMessage tagCommuMessage;
    private User user;
    private String manager;

    private List<String> labels = MyApplication.tagMessLabels;

    /*private List<String> labels = new ArrayList<String>() {{
        add("运动");
        add("求助");
        add("旅行");
        add("找室友");
        *//*add("交友");
        add("无聊");
        add("找CP");
        add("找女友");
        add("找男友");*//*
        add("房屋出租");
        add("租房");
        add("招聘");
        add("宠物");
    }};*/
    private PicPickerAdapter picPickerAdapter;
    private List<String> pics = new ArrayList<String>() {{
        add("添加");
    }};

    private int tageMessType = 0;
    private String defaultTag = "all";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tag_message);
        ButterKnife.bind(this);

        user = SPUtil.getInstance().getUser();
        if (user == null) {
            finish();
            return;
        }
        tageMessType = getIntent().getIntExtra("TagMessageType", 0);
        defaultTag = getIntent().getStringExtra("Tags");
        manager = getIntent().getStringExtra("manager");
        if (tageMessType == 1 && defaultTag == null) {
            finish();
            return;
        }
        if (tageMessType == 1) {
            if(!"all".equals(defaultTag)){
                labelNote.setVisibility(View.GONE);
                messageLabels.setVisibility(View.GONE);
                selfLabelEt.setText(defaultTag);
                selfLabelEt.setTextColor(Color.GRAY);
                selfLabelEt.setEnabled(false);
            }
            sure.setText("发布");
        }

        messageLabels.datas(labels)
                .listener((view, position) -> {
                    //showShortToas("选中了:" + position);
                }).commit();

        messageImg.setLayoutManager(new GridLayoutManager(this, 3));
        picPickerAdapter = new PicPickerAdapter(this, pics);
        messageImg.setAdapter(picPickerAdapter);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode < 100) {
            userFilterView.onActivityResult(requestCode, resultCode, data);
        } else {
            picPickerAdapter.onActivityResult(requestCode, resultCode, data);
        }
    }

    @OnClick({R.id.sure, R.id.level})
    public void onViewClicked(View v) {
        switch (v.getId()) {
            case R.id.sure:
                if ("".equals(messageTitle.getText().toString())) {
                    showShortToas("标题不能为空");
                    return;
                }
                if ("".equals(messageNote.getText().toString()) && picPickerAdapter.getPics().size() <= 0) {
                    showShortToas("内容不能为空");
                    return;
                }
                if(tageMessType == 0){
                    tagCommuMessage = setCommunityMessage();
                    Intent intent = new Intent();
                    intent.putExtra("TagCommuMessage", new Gson().toJson(tagCommuMessage));
                    setResult(RESULT_OK, intent);
                    finish();
                }else{
                    tagCommuMessage = setCommunityMessage();
                    DialogUtil.getInstance().showLoadingSimple(TagMessageActivity.this, getWindow().getDecorView());
                    MyApplication.getApplication().registerSendMessage(iSendMessage);
                    LocationManager.getInstance().registerLocationListener(iLocationListener);
                }
                break;
            case R.id.level:
                showLevelPop();
                break;
        }
    }

    private void showLevelPop(){
        List<String> datas = new ArrayList<>();
        datas.add("普通");
        datas.add("重要");
        datas.add("紧急");
        DialogUtil.getInstance().showPopList(this, datas, level);
        DialogUtil.getInstance().setDialogClick(str -> {
            if(!str.equals(level.getText().toString())){
                level.setText(str);
            }
            //level.setText(str);
        });
    }

    private LocationManager.ILocationListener iLocationListener = new LocationManager.ILocationListener() {
        @Override
        public void onReceive(BDLocation bdLocation) {
            LocationManager.getInstance().unRegisterLocationListener(iLocationListener);
            if(tagCommuMessage == null){
                showShortToas("fail");
                DialogUtil.getInstance().dimissLoadingDialog();
                LocationManager.getInstance().unRegisterLocationListener(iLocationListener);
                MyApplication.getApplication().registerSendMessage(iSendMessage);
                return;

            }
            uploadPrivMess(tagCommuMessage, bdLocation);
        }

        @Override
        public void onFail(String msg) {
            DialogUtil.getInstance().dimissLoadingDialog();
            LocationManager.getInstance().unRegisterLocationListener(iLocationListener);
            MyApplication.getApplication().registerSendMessage(iSendMessage);
            showShortToas("位置获取失败");
        }
    };

    private void uploadPrivMess(TagCommuMessage tagCommuMessage, BDLocation bdLocation) {
        tagCommuMessage.setMessagePrivate(1);
        if (!MyApplication.getApplication().isConnected()) {
            showShortToas(getString(R.string.disconnected));
            return;
        }
        tagCommuMessage.setCommunityName(bdLocation.getDistrict());
        tagCommuMessage.setFrom(user.getUserName());
        tagCommuMessage.setTo(bdLocation.getProvince() + "_" + bdLocation.getCity());

        CommunityRoom communityRoom = new CommunityRoom();
        communityRoom.setCommunitySubtype("市");
        communityRoom.setCountry(bdLocation.getCountry());
        communityRoom.setProvince(bdLocation.getProvince());
        communityRoom.setCity(bdLocation.getCity());

        tagCommuMessage.setIsSpaceTravel(MyDistanceUtil.isSpaceTravel(communityRoom, bdLocation, user.getShowCommunityLoc()));
        tagCommuMessage.setUserSex(user.getSex());
        tagCommuMessage.setCertificate(user.getCertificate());
        tagCommuMessage.setNickName(user.getNickName());
        tagCommuMessage.setPhoto(user.getPhoto());
        tagCommuMessage.setTimeMill(System.currentTimeMillis());

        MyApplication.ClientSocket.sendData(tagCommuMessage);
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    private TagCommuMessage setCommunityMessage() {
        tagCommuMessage = new TagCommuMessage();

        tagCommuMessage.setUserSex(user.getSex());
        tagCommuMessage.setIsEffective(user.getCertificate());
        tagCommuMessage.setNickName(user.getNickName());
        tagCommuMessage.setPhoto(user.getPhoto());

        tagCommuMessage.setPics(picPickerAdapter.getPics());
        tagCommuMessage.setTitle("" + messageTitle.getText().toString());
        tagCommuMessage.setText("" + messageNote.getText().toString());

        List<String> messageTags = new ArrayList<>();
        messageTags.addAll(messageLabels.getSelectTags());
        String selfLabel = (selfLabelEt.getText().toString().replace("，", ",")).replace(" ", "");
        messageTags.addAll(ArrayUtil.toCommaSplitList(selfLabel));
        tagCommuMessage.setLabels(messageTags);
        /*if (selfLabel.length() > 0) {
            tagCommuMessage.setLabels((messageLabels.getTags()
                    .replace(" ", "") + "," + selfLabel));
        } else {
            tagCommuMessage.setLabels((messageLabels.getTags().replace(" ", "")));
        }*/
        tagCommuMessage.setChatType(3);
        tagCommuMessage.setUserFilter(userFilterView.getUserFilter());
        if("紧急".equals(level.getText().toString())){
            tagCommuMessage.setLevel(1);
        }else if("重要".equals(level.getText().toString())){
            tagCommuMessage.setLevel(2);
        }else{
            tagCommuMessage.setLevel(3);
        }
        if(manager != null && manager.equals(user.getUserName())){
            tagCommuMessage.setManagerNotrify(true);
        }else{
            tagCommuMessage.setManagerNotrify(false);
        }
        return tagCommuMessage;
    }

    private MyApplication.ISendMessage iSendMessage = new MyApplication.ISendMessage() {

        @Override
        public void onSendFinish(BaseMessage message) {
            if(tagCommuMessage == null || message == null || message.getMessageUid() == null
                    || !message.getMessageUid().equals(tagCommuMessage.getMessageUid()) ){
                return;
            }
            DialogUtil.getInstance().dimissLoadingDialog();
            showLongToas("发布成功");
            finish();
        }

        @Override
        public void onSendFail(BaseMessage message) {
            if(tagCommuMessage == null || message == null || message.getMessageUid() == null
                    || !message.getMessageUid().equals(tagCommuMessage.getMessageUid()) ){
                return;
            }
            DialogUtil.getInstance().dimissLoadingDialog();
            MyApplication.getApplication().unRegisterSendMessage(iSendMessage);
            showLongToas("发布失败");
        }
    };

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_UP) {
            if (!"".equals(messageNote.getText().toString()) || !"".equals(messageTitle.getText().toString()) ||
                    picPickerAdapter.getPics().size() > 0) {
                DialogUtil.getInstance().showPromptDialog(this, "", "标签消息内容没有保存，确定要退出当前操作？");
                DialogUtil.getInstance().setDialogClick(str -> {
                    if ("确定".equals(str)) {
                        finish();
                    }
                });
                return true;
            }
        }
        return super.onKeyUp(keyCode, event);
    }

    @Override
    protected void onDestroy() {
        LocationManager.getInstance().unRegisterLocationListener(iLocationListener);
        MyApplication.getApplication().unRegisterSendMessage(iSendMessage);
        super.onDestroy();
    }
}
