package tl.pojul.com.fastim.View.widget;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.pojul.fastIM.entity.AgeRange;
import com.pojul.fastIM.entity.User;
import com.pojul.fastIM.entity.UserFilter;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import tl.pojul.com.fastim.R;
import tl.pojul.com.fastim.View.activity.BaseActivity;
import tl.pojul.com.fastim.View.activity.WhiteBlackActivity;
import tl.pojul.com.fastim.util.ArrayUtil;
import tl.pojul.com.fastim.util.DialogUtil;

public class UserFilterView extends LinearLayout {

    @BindView(R.id.all_cb)
    CheckBox allCb;
    @BindView(R.id.white_black_cb)
    CheckBox whiteBlackCb;
    @BindView(R.id.white_list_rb)
    RadioButton whiteListRb;
    @BindView(R.id.white_list_tv)
    TextView whiteListTv;
    @BindView(R.id.white_list_iv)
    ImageView whiteListIv;
    @BindView(R.id.black_list_rb)
    RadioButton blackListRb;
    @BindView(R.id.black_list_tv)
    TextView blackListTv;
    @BindView(R.id.black_list_iv)
    ImageView blackListIv;
    @BindView(R.id.age_range_cb)
    CheckBox ageRangeCb;
    @BindView(R.id.age_range_rsb)
    RangeSeekBar ageRangeRsb;
    @BindView(R.id.age_range_tv)
    TextView ageRangeTv;
    @BindView(R.id.sex_cb)
    CheckBox sexCb;
    @BindView(R.id.sex_man_rb)
    RadioButton sexManRb;//1
    @BindView(R.id.sex_woman_rb)
    RadioButton sexWomanRb;//0
    @BindView(R.id.certification_cb)
    CheckBox certificationCb;
    @BindView(R.id.white_black_note)
    TextView iteBlackNote;
    @BindView(R.id.credit_cb)
    CheckBox creditCb;
    @BindView(R.id.credit_msb)
    MySeekBar creditMsb;
    @BindView(R.id.credit_tv)
    TextView creditTv;

    private UserFilter filter;
    private int minAge;
    private int maxAge;
    private int credit;

    private static final int REQUEST_CODE_WHITE = 1;
    private static final int REQUEST_CODE_BLACK = 2;

    private List<User> whiteUsers;
    private List<User> blackUsers;

    public UserFilterView(Context context) {
        super(context);
        init();
    }

    public UserFilterView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public UserFilterView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        inflate(getContext(), R.layout.include_filter, this);
        ButterKnife.bind(this);
        filter = new UserFilter();
        filter.setFilterEnabled(false);
        allCb.setOnCheckedChangeListener(checkChanged);
        whiteBlackCb.setOnCheckedChangeListener(checkChanged);
        whiteListRb.setOnCheckedChangeListener(checkChanged);
        blackListRb.setOnCheckedChangeListener(checkChanged);
        ageRangeCb.setOnCheckedChangeListener(checkChanged);
        sexCb.setOnCheckedChangeListener(checkChanged);
        sexManRb.setOnCheckedChangeListener(checkChanged);
        sexWomanRb.setOnCheckedChangeListener(checkChanged);
        certificationCb.setOnCheckedChangeListener(checkChanged);
        creditCb.setOnCheckedChangeListener(checkChanged);

        iteBlackNote.setOnClickListener(onClick);
        whiteListIv.setOnClickListener(onClick);
        blackListIv.setOnClickListener(onClick);

        whiteListRb.setChecked(true);
        sexManRb.setChecked(true);
        allCb.setChecked(true);

        ageRangeRsb.setValue(0, 0);
        ageRangeTv.setText((0 + "-" + 0 + "岁"));
        ageRangeRsb.setOnRangeChangedListener((view, min, max) -> {
            //Toast.makeText(getContext(), min + "::" + max, Toast.LENGTH_SHORT).show();
            minAge = (int) min;
            maxAge = (int) max;
            ageRangeTv.setText((minAge + "-" + maxAge + "岁"));
        });

        creditMsb.setOnRangeChangedListener((view, progress) -> {
            credit = (int)progress;
            creditTv.setText("> " + credit);
        });
    }

    private CompoundButton.OnCheckedChangeListener checkChanged = (buttonView, isChecked) -> {
        switch (buttonView.getId()) {
            case R.id.all_cb:
                if (isChecked) {
                    whiteBlackCb.setChecked(false);
                    ageRangeCb.setChecked(false);
                    sexCb.setChecked(false);
                    certificationCb.setChecked(false);
                    creditCb.setChecked(false);
                }
                break;
            case R.id.white_black_cb:
                if (isChecked) {
                    allCb.setChecked(false);
                } else {
                    checkFilterEnables();
                }
                break;
            case R.id.white_list_rb:
                if (isChecked) {
                    whiteBlackCb.setChecked(true);
                    blackListRb.setChecked(false);
                } else {
                    blackListRb.setChecked(true);
                }
                break;
            case R.id.black_list_rb:
                if (isChecked) {
                    whiteBlackCb.setChecked(true);
                    whiteListRb.setChecked(false);
                } else {
                    whiteListRb.setChecked(true);
                }
                break;
            case R.id.age_range_cb:
                if (isChecked) {
                    allCb.setChecked(false);
                } else {
                    checkFilterEnables();
                }
                break;
            case R.id.sex_cb:
                if (isChecked) {
                    allCb.setChecked(false);
                } else {
                    checkFilterEnables();
                }
                break;
            case R.id.sex_man_rb:
                sexCb.setChecked(true);
                break;
            case R.id.sex_woman_rb:
                sexCb.setChecked(true);
                break;
            case R.id.credit_cb:
                if (isChecked) {
                    allCb.setChecked(false);
                } else {
                    checkFilterEnables();
                }
                break;
            case R.id.certification_cb:
                if (isChecked) {
                    allCb.setChecked(false);
                } else {
                    checkFilterEnables();
                }
                break;
        }
    };

    private void checkFilterEnables() {
        if (!whiteBlackCb.isChecked() && !ageRangeCb.isChecked() &&
                !sexCb.isChecked() && !certificationCb.isChecked() && !creditCb.isChecked()) {
            allCb.setChecked(true);
        }
    }

    private OnClickListener onClick = v -> {
        switch (v.getId()) {
            case R.id.white_black_note:
                DialogUtil.getInstance().showPromptDialog(getContext(), "黑白名单说明",
                        "•白名单：只有白名单内的用户可以看到这条消息，设置白名单后年龄、性别等过滤条件将会失效" +
                        "\n\n•黑名单：除了黑名单内的用户其他人均可以看到这条消息，设置黑名单后年龄、性别等过滤条件依然有效");
                break;
            case R.id.white_list_iv:
                Bundle bundle = new Bundle();
                bundle.putString("type", "white");
                if(whiteUsers != null && whiteUsers.size() > 0){
                    bundle.putString("tousers", new Gson().toJson(whiteUsers));
                }
                ((BaseActivity)getContext()).startActivityForResult(WhiteBlackActivity.class, bundle, REQUEST_CODE_WHITE);
                break;
            case R.id.black_list_iv:
                bundle = new Bundle();
                bundle.putString("type", "black");
                if(blackUsers != null && blackUsers.size() > 0){
                    bundle.putString("tousers", new Gson().toJson(blackUsers));
                }
                ((BaseActivity)getContext()).startActivityForResult(WhiteBlackActivity.class, bundle, REQUEST_CODE_BLACK);
                break;
        }
    };

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE_WHITE && resultCode == Activity.RESULT_OK) {
            String json = data.getExtras().getString("Users");
            if(json ==null){
                return;
            }
            whiteUsers = new Gson().fromJson(json, new TypeToken<List<User>>(){}.getType());
            whiteListTv.setText(ArrayUtil.getUserNicknames(whiteUsers));
        }else if(requestCode == REQUEST_CODE_BLACK && resultCode == Activity.RESULT_OK){
            String json = data.getExtras().getString("Users");
            if(json ==null){
                return;
            }
            blackUsers = new Gson().fromJson(json, new TypeToken<List<User>>(){}.getType());
            blackListTv.setText(ArrayUtil.getUserNicknames(blackUsers));
        }
    }

    public UserFilter getUserFilter() {
        filter = new UserFilter();
        if (allCb.isChecked()) {
            filter.setFilterEnabled(false);
        } else {
            filter.setFilterEnabled(true);
        }
        if (whiteBlackCb.isChecked()) {
            filter.setBlackListEnabled(true);
            filter.setWhiteListEnabled(false);
            if(blackListRb.isChecked()){
                if(blackUsers == null || blackUsers.size() <= 0){
                    filter.setBlackListEnabled(false);
                }else{
                    filter.setBlackListNames(ArrayUtil.getUserNames(blackUsers));
                }
            }else{
                filter.setWhiteListEnabled(true);
                filter.setBlackListEnabled(false);
                if(whiteUsers == null || whiteUsers.size() <= 0){
                    filter.setWhiteListEnabled(false);
                }else{
                    filter.setWhiteListNames(ArrayUtil.getUserNames(whiteUsers));
                }
            }
        } else {
            filter.setWhiteListEnabled(false);
        }
        if (ageRangeCb.isChecked() && minAge != 0 && maxAge != 120) {
            filter.setAgeEnabled(true);
            filter.setMinAge(minAge);
            filter.setMaxAge(maxAge);
        } else {
            filter.setAgeEnabled(false);
        }
        if (sexCb.isChecked()) {
            filter.setSexEnabled(true);
            if (sexManRb.isChecked()) {
                filter.setSex(1);
            } else {
                filter.setSex(0);
            }
        } else {
            filter.setSexEnabled(false);
        }
        if (creditCb.isChecked() && credit > 0) {
            filter.setCreditEnabled(true);
            filter.setCredit(credit);
        } else {
            filter.setCreditEnabled(false);
        }
        if (certificationCb.isChecked()) {
            filter.setCertificatEnabled(true);
        } else {
            filter.setCertificatEnabled(false);
        }
        return filter;
    }

}
