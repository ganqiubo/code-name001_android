package tl.pojul.com.fastim.View.widget;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.pojul.fastIM.entity.MessageFilter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import tl.pojul.com.fastim.MyApplication;
import tl.pojul.com.fastim.R;
import tl.pojul.com.fastim.util.ArrayUtil;

public class MessageFilterView extends LinearLayout {

    @BindView(R.id.message_labels)
    FlowTagView messageLabels;
    @BindView(R.id.more_label)
    EditText moreLabel;
    @BindView(R.id.certificat)
    TextView certificat;
    @BindView(R.id.certificat_nolimit)
    TextView certificatNolimit;
    @BindView(R.id.man)
    TextView man;
    @BindView(R.id.woman)
    TextView woman;
    @BindView(R.id.sex_nolimit)
    TextView sexNolimit;
    @BindView(R.id.efficative)
    TextView efficative;
    @BindView(R.id.efficative_nolimit)
    TextView efficativeNolimit;

    private List<String> labels = MyApplication.tagMessLabels;

    private MessageFilter messageFilter;

    public MessageFilterView(Context context) {
        super(context);
        init();
    }

    public MessageFilterView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public MessageFilterView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        inflate(getContext(), R.layout.include_message_filter, this);
        ButterKnife.bind(this);

        messageLabels.datas(labels)
                .listener((view, position) -> {
                    //showShortToas("选中了:" + position);
                }).commit();
    }

    public void setFilter(MessageFilter messageFilter) {
        if (messageFilter == null) {
            return;
        }
        this.messageFilter = messageFilter;
        if (messageFilter.getLabels() != null && messageFilter.getLabels().size() > 0) {
            HashMap<String, List<String>> maps = ArrayUtil.getSelfLabel(labels, messageFilter.getLabels());
            List<String> normalLabels = maps.get("normalLabels");
            List<String> selfLabels = maps.get("selfLabels");
            messageLabels.setSelectedTagStrs(normalLabels);
            String selfLabelsStr = ArrayUtil.toCommaSplitStr(selfLabels);
            moreLabel.setText(selfLabelsStr);
        } else {
            messageLabels.setSelectedTagStrs(new ArrayList<>());
            moreLabel.setText("");
        }
        if (messageFilter.getCertificat() == -1) {
            certificat.setSelected(false);
            certificatNolimit.setSelected(true);
        } else {
            certificat.setSelected(true);
            certificatNolimit.setSelected(false);
        }
        if (messageFilter.getSex() == -1) {
            sexNolimit.setSelected(true);
            man.setSelected(false);
            woman.setSelected(false);
        } else if (messageFilter.getSex() == 0) {
            sexNolimit.setSelected(false);
            man.setSelected(false);
            woman.setSelected(true);
        } else {
            sexNolimit.setSelected(false);
            man.setSelected(true);
            woman.setSelected(false);
        }
        if(messageFilter.getEfficative() == -1){
            efficativeNolimit.setSelected(true);
            efficative.setSelected(false);
        }else{
            efficativeNolimit.setSelected(false);
            efficative.setSelected(true);
        }
    }

    @OnClick({R.id.certificat, R.id.certificat_nolimit, R.id.man, R.id.woman, R.id.sex_nolimit, R.id.efficative, R.id.efficative_nolimit})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.certificat:
                certificat.setSelected(true);
                certificatNolimit.setSelected(false);
                break;
            case R.id.certificat_nolimit:
                certificat.setSelected(false);
                certificatNolimit.setSelected(true);
                break;
            case R.id.man:
                sexNolimit.setSelected(false);
                man.setSelected(true);
                woman.setSelected(false);
                break;
            case R.id.woman:
                sexNolimit.setSelected(false);
                man.setSelected(false);
                woman.setSelected(true);
                break;
            case R.id.sex_nolimit:
                sexNolimit.setSelected(true);
                man.setSelected(false);
                woman.setSelected(false);
                break;
            case R.id.efficative:
                efficative.setSelected(true);
                efficativeNolimit.setSelected(false);
                break;
            case R.id.efficative_nolimit:
                efficative.setSelected(false);
                efficativeNolimit.setSelected(true);
                break;
        }
    }

    public void resetFilter() {
        MessageFilter messageFilter = new MessageFilter();
        setFilter(messageFilter);
    }

    public MessageFilter getFilter() {
        MessageFilter messageFilter = new MessageFilter();
        List<String> labels = new ArrayList<>();
        labels.addAll(messageLabels.getSelectTags());
        String selfLabel = (moreLabel.getText().toString().replace("，", ",")).replace(" ", "");
        labels.addAll(ArrayUtil.toCommaSplitList(selfLabel));
        messageFilter.setLabels(labels);
        if (certificat.isSelected()) {
            messageFilter.setCertificat(1);
        } else {
            messageFilter.setCertificat(-1);
        }
        if (man.isSelected()) {
            messageFilter.setSex(1);
        } else if (woman.isSelected()) {
            messageFilter.setSex(0);
        } else {
            messageFilter.setSex(-1);
        }
        if(efficative.isSelected()){
            messageFilter.setEfficative(0);
        }else{
            messageFilter.setEfficative(-1);
        }
        return messageFilter;
    }


}
