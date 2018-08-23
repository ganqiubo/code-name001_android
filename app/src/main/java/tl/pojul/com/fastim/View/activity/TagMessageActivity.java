package tl.pojul.com.fastim.View.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.pojul.fastIM.entity.User;
import com.pojul.fastIM.message.chat.TagCommuMessage;

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
import tl.pojul.com.fastim.util.ArrayUtil;
import tl.pojul.com.fastim.util.DialogUtil;
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

    private TagCommuMessage tagCommuMessage;
    private User user;

    private List<String> labels = MyApplication.tagMessLabels;

    /*private List<String> labels = new ArrayList<String>() {{
        add("运动");
        add("求助");
        add("旅游");
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
        messageLabels.datas(labels)
                .listener(new FlowTagView.OnTagSelectedListener() {
                    @Override
                    public void onTagSelected(FlowTagView view, int position) {
                        //showShortToas("选中了:" + position);
                    }
                }).commit();

        messageImg.setLayoutManager(new GridLayoutManager(this, 3));
        picPickerAdapter = new PicPickerAdapter(this, pics);
        messageImg.setAdapter(picPickerAdapter);
    }

    @Override
        protected void onActivityResult(int requestCode, int resultCode, Intent data) {
            super.onActivityResult(requestCode, resultCode, data);
        if(requestCode < 100){
            userFilterView.onActivityResult(requestCode, resultCode, data);
        }else{
            picPickerAdapter.onActivityResult(requestCode, resultCode, data);
        }
    }

    @OnClick({R.id.sure})
    public void onViewClicked(View v) {
        switch (v.getId()) {
            case R.id.sure:
                if ("".equals(messageTitle.getText().toString()) ) {
                    showShortToas("标题不能为空");
                    return;
                }
                if("".equals(messageNote.getText().toString()) && picPickerAdapter.getPics().size() <= 0){
                    showShortToas("内容不能为空");
                    return;
                }
                tagCommuMessage = setCommunityMessage();
                Intent intent = new Intent();
                intent.putExtra("TagCommuMessage", new Gson().toJson(tagCommuMessage));
                setResult(RESULT_OK, intent);
                finish();
                break;
        }
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
        return tagCommuMessage;
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_UP) {
            if (!"".equals(messageNote.getText().toString()) || !"".equals(messageTitle.getText().toString()) ||
                    picPickerAdapter.getPics().size() > 0) {
                DialogUtil.getInstance().showPromptDialog(this, "", "标签消息内容没有保存，确定要退出当前操作？");
                DialogUtil.getInstance().setDialogClick(str -> {
                    if("确定".equals(str)){
                        finish();
                    }
                });
                return true;
            }
        }
        return super.onKeyUp(keyCode, event);
    }
}
