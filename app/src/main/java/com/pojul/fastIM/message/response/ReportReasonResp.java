package com.pojul.fastIM.message.response;

import com.pojul.objectsocket.message.ResponseMessage;
import java.util.List;

public class ReportReasonResp extends ResponseMessage{

    private List<String> reportReasons;

    public List<String> getReportReasons() {
        return reportReasons;
    }

    public void setReportReasons(List<String> reportReasons) {
        this.reportReasons = reportReasons;
    }
}
