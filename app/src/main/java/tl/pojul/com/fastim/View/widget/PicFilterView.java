package tl.pojul.com.fastim.View.widget;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.pojul.fastIM.entity.PicFilter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import tl.pojul.com.fastim.MyApplication;
import tl.pojul.com.fastim.R;
import tl.pojul.com.fastim.util.ArrayUtil;

public class PicFilterView extends LinearLayout {

    @BindView(R.id.pic_labels)
    FlowTagView picLabels;
    @BindView(R.id.more_label)
    EditText moreLabel;
    @BindView(R.id.foot_setp)
    TextView footSetp;
    @BindView(R.id.unsplash)
    TextView unsplash;
    @BindView(R.id.pexels)
    TextView pexels;
    @BindView(R.id.man)
    TextView man;
    @BindView(R.id.woman)
    TextView woman;
    @BindView(R.id.sex_nolimit)
    TextView sexNolimit;
    @BindView(R.id.label_ll)
    LinearLayout labelLl;
    @BindView(R.id.more_label_note)
    TextView moreLabelNote;
    @BindView(R.id.more_label_title)
    TextView moreLabelTitle;

    private PicFilter picFilter;
    private boolean nearBy;

    public PicFilterView(Context context) {
        super(context);
        init();
    }

    public PicFilterView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public PicFilterView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {

        inflate(getContext(), R.layout.include_pic_filter, this);
        ButterKnife.bind(this);

        pexels.setEnabled(false);
        picLabels.datas(MyApplication.picLabels)
                .listener((view, position) -> {
                    //showShortToas("选中了:" + position);
                }).commit();

    }

    public void setFilter(PicFilter picFilter, boolean hideLabel) {
        if (picFilter == null) {
            return;
        }
        this.picFilter = picFilter;
        this.nearBy = picFilter.isNearBy();
        if (nearBy) {
            unsplash.setEnabled(false);
            pexels.setEnabled(false);
            picFilter.setGallery("脚步");
        } else {
            unsplash.setEnabled(true);
            pexels.setEnabled(true);
        }
        if (hideLabel) {
            labelLl.setVisibility(GONE);
        } else {
            labelLl.setVisibility(VISIBLE);
        }
        if (picFilter.getLabels() != null && picFilter.getLabels().size() > 0) {
            HashMap<String, List<String>> maps = ArrayUtil.getSelfLabel(MyApplication.picLabels, picFilter.getLabels());
            List<String> normalLabels = maps.get("normalLabels");
            List<String> selfLabels = maps.get("selfLabels");
            picLabels.setSelectedTagStrs(normalLabels);
            String selfLabelsStr = ArrayUtil.toCommaSplitStr(selfLabels);
            moreLabel.setText(selfLabelsStr);
        } else {
            picLabels.setSelectedTagStrs(new ArrayList<>());
            moreLabel.setText("");
        }
        if ("脚步".equals(picFilter.getGallery())) {
            footSetp.setSelected(true);
            unsplash.setSelected(false);
            pexels.setSelected(false);
            moreLabelNote.setVisibility(GONE);
            moreLabelTitle.setText("更多标签：");
            enableSex();
        } else if ("unsplash".equals(picFilter.getGallery())) {
            footSetp.setSelected(false);
            unsplash.setSelected(true);
            pexels.setSelected(false);
            moreLabelNote.setVisibility(GONE);
            moreLabelTitle.setText("更多标签：");
            disableSex();
        } else {
            footSetp.setSelected(false);
            unsplash.setSelected(false);
            pexels.setSelected(true);
            moreLabelTitle.setText("更多标签");
            moreLabelNote.setVisibility(VISIBLE);
            disableSex();
        }
        if (picFilter.getSex() == -1) {
            sexNolimit.setSelected(true);
            man.setSelected(false);
            woman.setSelected(false);
        } else if (picFilter.getSex() == 0) {
            sexNolimit.setSelected(false);
            man.setSelected(false);
            woman.setSelected(true);
        } else {
            sexNolimit.setSelected(false);
            man.setSelected(true);
            woman.setSelected(false);
        }
    }

    @OnClick({R.id.foot_setp, R.id.unsplash, R.id.pexels, R.id.man, R.id.woman, R.id.sex_nolimit})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.foot_setp:
                footSetp.setSelected(true);
                unsplash.setSelected(false);
                pexels.setSelected(false);
                enableSex();
                moreLabelTitle.setText("更多标签：");
                moreLabelNote.setVisibility(GONE);
                break;
            case R.id.unsplash:
                footSetp.setSelected(false);
                unsplash.setSelected(true);
                pexels.setSelected(false);
                disableSex();
                moreLabelTitle.setText("更多标签：");
                moreLabelNote.setVisibility(GONE);
                break;
            case R.id.pexels:
                footSetp.setSelected(false);
                unsplash.setSelected(false);
                pexels.setSelected(true);
                disableSex();
                moreLabelTitle.setText("更多标签");
                moreLabelNote.setVisibility(VISIBLE);
                break;
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

    public PicFilter getFilter() {
        PicFilter picFilter = new PicFilter();
        picFilter.setNearBy(nearBy);
        List<String> labels = new ArrayList<>();
        labels.addAll(picLabels.getSelectTags());
        String selfLabel = (moreLabel.getText().toString().replace("，", ","));
        labels.addAll(ArrayUtil.toCommaSplitList(selfLabel));
        picFilter.setLabels(labels);
        if (footSetp.isSelected()) {
            picFilter.setGallery("脚步");
        } else if (unsplash.isSelected()) {
            picFilter.setGallery("unsplash");
        } else {
            picFilter.setGallery("pexels");
        }
        if (man.isSelected()) {
            picFilter.setSex(1);
        } else if (woman.isSelected()) {
            picFilter.setSex(0);
        } else {
            picFilter.setSex(-1);
        }
        return picFilter;
    }

    public void disableThirdGally() {
        unsplash.setEnabled(false);
        pexels.setEnabled(false);
        footSetp.setEnabled(false);
    }

    public void disableSex() {
        man.setEnabled(false);
        woman.setEnabled(false);
        sexNolimit.setEnabled(false);
    }

    public void enableSex() {
        man.setEnabled(true);
        woman.setEnabled(true);
        sexNolimit.setEnabled(true);
    }

}
