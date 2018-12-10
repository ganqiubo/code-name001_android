package tl.pojul.com.fastim.View.widget;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.pojul.fastIM.entity.UserSelectFilter;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import tl.pojul.com.fastim.R;

public class UserSelectView extends LinearLayout {

    @BindView(R.id.age_range_rsb)
    RangeSeekBar ageRangeRsb;
    @BindView(R.id.age_range_tv)
    TextView ageRangeTv;
    @BindView(R.id.man)
    TextView man;
    @BindView(R.id.woman)
    TextView woman;
    @BindView(R.id.sex_nolimit)
    TextView sexNolimit;
    @BindView(R.id.credit_msb)
    MySeekBar creditMsb;
    @BindView(R.id.credit_tv)
    TextView creditTv;

    private int minAge;
    private int maxAge;
    private int minCredit;

    public UserSelectView(Context context) {
        super(context);
        init();
    }

    public UserSelectView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public UserSelectView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        inflate(getContext(), R.layout.include_user_select, this);
        ButterKnife.bind(this);
    }

    public void setUserSelectFilter(UserSelectFilter userSelectFilter) {
        if (userSelectFilter == null) {
            return;
        }
        ageRangeRsb.setValue(userSelectFilter.getMinAge(), userSelectFilter.getMaxAge());
        ageRangeTv.setText(userSelectFilter.getMinAge() + "- " + userSelectFilter.getMaxAge() + "岁");
        minAge = userSelectFilter.getMinAge();
        maxAge = userSelectFilter.getMaxAge();
        man.setSelected((userSelectFilter.getSex() == 1));
        woman.setSelected((userSelectFilter.getSex() == 0));
        sexNolimit.setSelected((userSelectFilter.getSex() == -1));
        creditMsb.setValue(userSelectFilter.getMinCredit());
        creditTv.setText("> " + userSelectFilter.getMinCredit());
        minCredit = userSelectFilter.getMinCredit();

        ageRangeRsb.setOnRangeChangedListener((view, min, max) -> {
            minAge = (int) min;
            maxAge = (int) max;
            ageRangeTv.setText((minAge + "-" + maxAge + "岁"));
        });

        creditMsb.setOnRangeChangedListener((view, progress) -> {
            minCredit = (int) progress;
            creditTv.setText("> " + minCredit);
        });
    }

    public UserSelectFilter getFilter() {
        UserSelectFilter filter = new UserSelectFilter();
        filter.setMinAge(minAge);
        filter.setMaxAge(maxAge);
        filter.setMinCredit(minCredit);
        if (man.isSelected()) {
            filter.setSex(1);
        } else if (woman.isSelected()) {
            filter.setSex(0);
        } else {
            filter.setSex(-1);
        }
        return filter;
    }

    @OnClick({R.id.man, R.id.woman, R.id.sex_nolimit})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.man:
                man.setSelected(true);
                woman.setSelected(false);
                sexNolimit.setSelected(false);
                break;
            case R.id.woman:
                man.setSelected(false);
                woman.setSelected(true);
                sexNolimit.setSelected(false);
                break;
            case R.id.sex_nolimit:
                man.setSelected(false);
                woman.setSelected(false);
                sexNolimit.setSelected(true);
                break;
        }
    }
}
