package tl.pojul.com.fastim.View.widget;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.pojul.fastIM.entity.Report;
import com.pojul.fastIM.message.chat.TagCommuMessage;
import com.pojul.fastIM.message.request.ReportReasonReq;
import com.pojul.fastIM.message.request.ReportReq;
import com.pojul.fastIM.message.response.ReportReasonResp;
import com.pojul.objectsocket.message.ResponseMessage;
import com.pojul.objectsocket.socket.SocketRequest;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import tl.pojul.com.fastim.MyApplication;
import tl.pojul.com.fastim.R;
import tl.pojul.com.fastim.util.ArrayUtil;
import tl.pojul.com.fastim.util.SPUtil;

public class ReportView extends LinearLayout {

    @BindView(R.id.report_reason)
    FlowTagView reportReason;
    @BindView(R.id.other_reason)
    EditText otherReason;
    @BindView(R.id.detail)
    EditText detail;
    @BindView(R.id.progress)
    ProgressBar progress;

    private List<String> reasons = new ArrayList<>();
    private boolean isReport = false;

    public ReportView(Context context) {
        super(context);
        init();
    }

    public ReportView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ReportView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        inflate(getContext(), R.layout.include_report_view, this);
        ButterKnife.bind(this);

        getReasons();

    }

    public void getReasons() {
        new SocketRequest().request(MyApplication.ClientSocket, new ReportReasonReq(), new SocketRequest.IRequest() {
            @Override
            public void onError(String msg) {
                new Handler(Looper.getMainLooper()).post(() -> {
                    Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show();
                });
            }

            @Override
            public void onFinished(ResponseMessage mResponse) {
                new Handler(Looper.getMainLooper()).post(() -> {
                    if (mResponse.getCode() == 200) {
                        reasons = ((ReportReasonResp) mResponse).getReportReasons();
                        reportReason.datas(reasons)
                                .listener(new FlowTagView.OnTagSelectedListener() {
                                    @Override
                                    public void onTagSelected(FlowTagView view, int position) {
                                        //showShortToas("选中了:" + position);
                                    }
                                }).commit();
                    } else {
                        Toast.makeText(getContext(), mResponse.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    public Report getReport(TagCommuMessage tagCommuMessage) {
        Report report = new Report();
        report.setReportMessageUid(tagCommuMessage.getMessageUid());
        String reasons = ArrayUtil.toCommaSplitStr(reportReason.getSelectTags());
        String otherReasonStr = (otherReason.getText().toString().replace("，", ",")).replace(" ", "");
        if (!otherReasonStr.isEmpty()) {
            reasons = reasons + "," + otherReasonStr;
        }
        report.setReportReason(reasons);
        report.setDetail(detail.getText().toString());
        report.setReporter(SPUtil.getInstance().getUser().getUserName());
        report.setBeReporter(tagCommuMessage.getFrom());
        return report;
    }

    public void requsetReport(TagCommuMessage tagCommuMessage, IReport iReport) {
        if (isReport) {
            return;
        }
        Report report = getReport(tagCommuMessage);
        if (report.getReportReason() == null || report.getReportReason().isEmpty()) {
            Toast.makeText(getContext(), "举报原因不能为空", Toast.LENGTH_SHORT).show();
            return;
        }
        ReportReq reportReq = new ReportReq();
        reportReq.setReport(report);
        isReport = true;
        progress.setVisibility(VISIBLE);
        new SocketRequest().request(MyApplication.ClientSocket, reportReq, new SocketRequest.IRequest() {
            @Override
            public void onError(String msg) {
                new Handler(Looper.getMainLooper()).post(() -> {
                    isReport = false;
                    progress.setVisibility(GONE);
                    Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show();
                    if (iReport != null) {
                        iReport.reportResult("sucesses");
                    }
                });
            }

            @Override
            public void onFinished(ResponseMessage mResponse) {
                new Handler(Looper.getMainLooper()).post(() -> {
                    isReport = false;
                    progress.setVisibility(GONE);
                    if (mResponse.getCode() == 200) {
                        Toast.makeText(getContext(), "举报成功，系统将审核您举报内容的真实性", Toast.LENGTH_LONG).show();
                        if (iReport != null) {
                            iReport.reportResult("sucesses");
                        }
                    } else {
                        Toast.makeText(getContext(), "举报失败", Toast.LENGTH_SHORT).show();
                        if (iReport != null) {
                            iReport.reportResult("fail");
                        }
                    }
                });
            }
        });
    }

    public interface IReport {
        public void reportResult(String result);
    }

}
