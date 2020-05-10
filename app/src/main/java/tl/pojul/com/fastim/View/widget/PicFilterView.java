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
    @BindView(R.id.pixabay)
    TextView pixabay;
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
    @BindView(R.id.other_note)
    TextView otherNote;
    @BindView(R.id.nearby)
    TextView nearbyTv;
    @BindView(R.id.my_pic)
    TextView myPic;
    @BindView(R.id.my_like)
    TextView myLike;
    @BindView(R.id.my_collect)
    TextView myCollect;
    @BindView(R.id.other_ll)
    LinearLayout otherLl;
    @BindView(R.id.pic_labels_note)
    TextView picLabelsNote;

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
            pixabay.setEnabled(false);
            picFilter.setGallery("脚步");
            nearbyTv.setSelected(true);
        } else {
            unsplash.setEnabled(true);
            pexels.setEnabled(true);
            pixabay.setEnabled(true);
            nearbyTv.setSelected(false);
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
            pixabay.setSelected(false);
            moreLabelNote.setVisibility(GONE);
            moreLabelTitle.setText("更多标签：");
            enableSex();
        } else if ("unsplash".equals(picFilter.getGallery())) {
            footSetp.setSelected(false);
            unsplash.setSelected(true);
            pexels.setSelected(false);
            pixabay.setSelected(false);
            moreLabelNote.setVisibility(GONE);
            moreLabelTitle.setText("更多标签：");
            disableSex();
        } else if("pixabay".equals(picFilter.getGallery())){
            footSetp.setSelected(false);
            unsplash.setSelected(false);
            pixabay.setSelected(true);
            moreLabelTitle.setText("更多标签");
            moreLabelNote.setVisibility(VISIBLE);
            disableSex();
        }else {
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
        if(picFilter.getOther() != null && !picFilter.getOther().isEmpty()){
            picLabels.setEnabled(false);
            picLabelsNote.setEnabled(false);
        }else{
            picLabels.setEnabled(true);
            picLabelsNote.setEnabled(true);
        }
        if (picFilter.getOther() != null && !picFilter.getOther().isEmpty()) {
            if ("附近".equals(picFilter.getOther())) {
                nearbyTv.setSelected(true);
                myPic.setSelected(false);
                myLike.setSelected(false);
                myCollect.setSelected(false);
                footSetp.setSelected(true);
                unsplash.setSelected(false);
                unsplash.setEnabled(false);
                pexels.setSelected(false);
                pexels.setEnabled(false);
                pixabay.setSelected(false);
                pixabay.setEnabled(false);
                man.setEnabled(true);
                woman.setEnabled(true);
                sexNolimit.setEnabled(true);
            } else if ("我的图集".equals(picFilter.getOther())) {
                myPic.setSelected(true);
                nearbyTv.setSelected(false);
                myLike.setSelected(false);
                myCollect.setSelected(false);
                footSetp.setSelected(true);
                unsplash.setSelected(false);
                unsplash.setEnabled(false);
                pexels.setSelected(false);
                pexels.setEnabled(false);
                pixabay.setSelected(false);
                pixabay.setEnabled(false);
                man.setEnabled(true);
                woman.setEnabled(true);
                sexNolimit.setEnabled(true);
            } else if ("我喜欢的".equals(picFilter.getOther())) {
                myLike.setSelected(true);
                nearbyTv.setSelected(false);
                myPic.setSelected(false);
                myCollect.setSelected(false);
                unsplash.setEnabled(true);
                pexels.setEnabled(true);
                pixabay.setEnabled(true);
            } else if ("我的收藏".equals(picFilter.getOther())) {
                myCollect.setSelected(true);
                nearbyTv.setSelected(false);
                myPic.setSelected(false);
                myLike.setSelected(false);
                unsplash.setEnabled(true);
                pexels.setEnabled(true);
                pixabay.setEnabled(true);
            }
        } else {
            nearbyTv.setSelected(false);
            myPic.setSelected(false);
            myLike.setSelected(false);
            myCollect.setSelected(false);
        }
        checkOthers();
    }

    private void checkOthers() {
        if(nearbyTv.isSelected() || myPic.isSelected() || myLike.isSelected() || myCollect.isSelected()){
            picLabelsNote.setEnabled(false);
            picLabels.setEnabled(false);
        }else{
            picLabelsNote.setEnabled(true);
            picLabels.setEnabled(true);
        }
    }

    @OnClick({R.id.foot_setp, R.id.pixabay, R.id.unsplash, R.id.pexels, R.id.man, R.id.woman, R.id.sex_nolimit, R.id.nearby, R.id.my_pic,
            R.id.my_like, R.id.my_collect})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.foot_setp:
                footSetp.setSelected(true);
                unsplash.setSelected(false);
                pexels.setSelected(false);
                pixabay.setSelected(false);
                enableSex();
                moreLabelTitle.setText("更多标签：");
                moreLabelNote.setVisibility(GONE);
                break;
            case R.id.unsplash:
                footSetp.setSelected(false);
                unsplash.setSelected(true);
                pexels.setSelected(false);
                pixabay.setSelected(false);
                disableSex();
                moreLabelTitle.setText("更多标签：");
                moreLabelNote.setVisibility(GONE);
                break;
            case R.id.pexels:
                footSetp.setSelected(false);
                unsplash.setSelected(false);
                pexels.setSelected(true);
                pixabay.setSelected(false);
                disableSex();
                moreLabelTitle.setText("更多标签");
                moreLabelNote.setVisibility(VISIBLE);
                break;
            case R.id.pixabay:
                footSetp.setSelected(false);
                unsplash.setSelected(false);
                pixabay.setSelected(true);
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
            case R.id.nearby:
                nearbyTv.setSelected(!nearbyTv.isSelected());
                if (nearbyTv.isSelected()) {
                    myPic.setSelected(false);
                    myLike.setSelected(false);
                    myCollect.setSelected(false);
                    footSetp.setSelected(true);
                    unsplash.setSelected(false);
                    unsplash.setEnabled(false);
                    pexels.setSelected(false);
                    pexels.setEnabled(false);
                    pixabay.setSelected(false);
                    pixabay.setEnabled(false);
                    man.setEnabled(true);
                    woman.setEnabled(true);
                    sexNolimit.setEnabled(true);
                } else {
                    unsplash.setEnabled(true);
                    pexels.setEnabled(true);
                    pixabay.setEnabled(true);
                }
                checkOthers();
                break;
            case R.id.my_pic:
                myPic.setSelected(!myPic.isSelected());
                if (myPic.isSelected()) {
                    nearbyTv.setSelected(false);
                    myLike.setSelected(false);
                    myCollect.setSelected(false);
                    footSetp.setSelected(true);
                    unsplash.setSelected(false);
                    unsplash.setEnabled(false);
                    pexels.setSelected(false);
                    pexels.setEnabled(false);
                    pixabay.setSelected(false);
                    pixabay.setEnabled(false);
                    man.setEnabled(true);
                    woman.setEnabled(true);
                    sexNolimit.setEnabled(true);
                } else {
                    unsplash.setEnabled(true);
                    pexels.setEnabled(true);
                    pixabay.setEnabled(true);
                }
                checkOthers();
                break;
            case R.id.my_like:
                myLike.setSelected(!myLike.isSelected());
                if (myLike.isSelected()) {
                    nearbyTv.setSelected(false);
                    myPic.setSelected(false);
                    myCollect.setSelected(false);
                    unsplash.setEnabled(true);
                    pexels.setEnabled(true);
                    pixabay.setEnabled(true);
                }
                checkOthers();
                break;
            case R.id.my_collect:
                myCollect.setSelected(!myCollect.isSelected());
                if (myCollect.isSelected()) {
                    nearbyTv.setSelected(false);
                    myPic.setSelected(false);
                    myLike.setSelected(false);
                    unsplash.setEnabled(true);
                    pexels.setEnabled(true);
                    pixabay.setEnabled(true);
                }
                checkOthers();
                break;
        }
    }

    public PicFilter getFilter() {
        PicFilter picFilter = new PicFilter();
        List<String> labels = new ArrayList<>();
        labels.addAll(picLabels.getSelectTags());
        String selfLabel = (moreLabel.getText().toString().replace("，", ","));
        labels.addAll(ArrayUtil.toCommaSplitList(selfLabel));
        picFilter.setLabels(labels);
        if (footSetp.isSelected()) {
            picFilter.setGallery("脚步");
        } else if (unsplash.isSelected()) {
            picFilter.setGallery("unsplash");
        } else if(pexels.isSelected()){
            picFilter.setGallery("pexels");
        }else {
            picFilter.setGallery("pixabay");
        }
        if (man.isSelected()) {
            picFilter.setSex(1);
        } else if (woman.isSelected()) {
            picFilter.setSex(0);
        } else {
            picFilter.setSex(-1);
        }
        if (nearbyTv.isSelected()) {
            picFilter.setOther("附近");
            nearBy = true;
        } else if (myPic.isSelected()) {
            picFilter.setOther("我的图集");
        } else if (myLike.isSelected()) {
            picFilter.setOther("我喜欢的");
        } else if (myCollect.isSelected()) {
            picFilter.setOther("我的收藏");
        }

        if(!nearbyTv.isSelected()){
            nearBy = false;
        }
        picFilter.setNearBy(nearBy);
        return picFilter;
    }

    public void disableThirdGally() {
        unsplash.setEnabled(false);
        pexels.setEnabled(false);
        pixabay.setEnabled(false);
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

    public void showOthers() {
        otherNote.setVisibility(VISIBLE);
        otherLl.setVisibility(VISIBLE);
    }

}
