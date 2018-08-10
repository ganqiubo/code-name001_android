package tl.pojul.com.fastim.View.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.pojul.fastIM.entity.User;
import com.pojul.fastIM.entity.UserSelect;
import com.pojul.fastIM.entity.WhiteBlack;

import java.lang.ref.WeakReference;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import tl.pojul.com.fastim.R;
import tl.pojul.com.fastim.View.Adapter.SelectUserAdapter;
import tl.pojul.com.fastim.util.DateUtil;
import tl.pojul.com.fastim.util.DialogUtil;
import tl.pojul.com.fastim.util.SPUtil;

public class WhiteBlackActivity extends BaseActivity {

    @BindView(R.id.back)
    ImageView back;
    @BindView(R.id.search)
    ImageView search;
    @BindView(R.id.title)
    TextView title;
    @BindView(R.id.header)
    RelativeLayout header;
    @BindView(R.id.header_line)
    View headerLine;
    @BindView(R.id.friend_list_tv)
    TextView friendListTv;
    @BindView(R.id.friend_list)
    RelativeLayout friendList;
    @BindView(R.id.local_list_tv)
    TextView localListTv;
    @BindView(R.id.local_list)
    RelativeLayout localList;
    @BindView(R.id.save_tv)
    TextView saveTv;
    @BindView(R.id.list)
    RecyclerView list;
    @BindView(R.id.sure)
    TextView sure;

    private String type;
    private static final int REQUEST_CODE_USER = 1001;
    private List<UserSelect> mlist = new ArrayList<>();
    private SelectUserAdapter selectUserAdapter;
    private static final int INIT = 101;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_white_black);
        ButterKnife.bind(this);

        type = getIntent().getStringExtra("type");
        if(type == null || (!"white".equals(type) && !"black".equals(type))){
            finish();
            return;
        }
        if("white".equals(type)){
            title.setText("白名单");
            localListTv.setText("本地白名单");
        }else{
            title.setText("黑名单");
            localListTv.setText("本地黑名单");
        }

        mHandler.sendEmptyMessageDelayed(INIT, 100);

    }

    public void init(){
        String json = getIntent().getStringExtra("tousers");
        if(json !=null){
            List<User> users = new Gson().fromJson(json, new TypeToken<List<User>>(){}.getType());
            for (int i = 0; i < users.size(); i++) {
                UserSelect userSelect = new UserSelect();
                userSelect.setUser(users.get(i));
                userSelect.setSelected(true);
                mlist.add(userSelect);
            }
        }

        LinearLayoutManager layoutmanager = new LinearLayoutManager(this);
        list.setLayoutManager(layoutmanager);
        selectUserAdapter = new SelectUserAdapter(this, mlist, true, false);
        list.setAdapter(selectUserAdapter);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQUEST_CODE_USER && resultCode == Activity.RESULT_OK){
            Gson gson = new Gson();
            Type founderListType = new TypeToken<ArrayList<UserSelect>>(){}.getType();
            String json = data.getExtras().getString("UserSelect");
            List<UserSelect> userSelects = gson.fromJson(json, founderListType);
            selectUserAdapter.addList(userSelects);
        }
    }

    @OnClick({R.id.back, R.id.search, R.id.friend_list, R.id.local_list, R.id.save_tv, R.id.sure})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.back:
                finish();
                break;
            case R.id.search:
                break;
            case R.id.friend_list:
                Bundle bundle = new Bundle();
                bundle.putString("type", "friend");
                startActivityForResult(UserSelectActivity.class, bundle, REQUEST_CODE_USER);
                break;
            case R.id.local_list:
                bundle = new Bundle();
                bundle.putString("type", type);
                bundle.putString("subType", "select");
                startActivityForResult(WhiteBlackListActivity.class, bundle, REQUEST_CODE_USER);
                break;
            case R.id.save_tv:
                List<User> users = selectUserAdapter.getAllUsers();
                if(users.size() <= 0){
                    showShortToas("保存名单不能为空");
                    return;
                }
                DialogUtil.getInstance().showSaveWhiteBlackDialog(WhiteBlackActivity.this, type);
                DialogUtil.getInstance().setDialogClick(str -> {
                    if(!"取消".equals(str)){
                        WhiteBlack whiteBlack = new WhiteBlack();
                        whiteBlack.setName(str);
                        whiteBlack.setType(type);
                        whiteBlack.setUpdateTime(DateUtil.getFormatDate());
                        whiteBlack.setUsers(users);
                        SPUtil.getInstance().addWhiteBlack(whiteBlack);
                        showShortToas("保存成功");
                        DialogUtil.getInstance().setDialogClick(null);
                    }
                });
                break;
            case R.id.sure:
                users = selectUserAdapter.getAllUsers();
                Intent intent = new Intent();
                intent.putExtra("Users", new Gson().toJson(users));
                setResult(RESULT_OK, intent);
                finish();
                break;
        }
    }

    private MyHandler mHandler = new MyHandler(this);
    static class MyHandler extends Handler {
        //注意下面的“”类是MyHandler类所在的外部类，即所在的activity或者fragment
        WeakReference<WhiteBlackActivity> activity;

        MyHandler(WhiteBlackActivity activity) {
            this.activity = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (activity.get() == null) {
                return;
            }
            switch (msg.what) {
                case INIT:
                    activity.get().init();
                    break;
            }
        }
    }

}
