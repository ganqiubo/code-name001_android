package com.pojul.fastIM.message.request;

import com.pojul.fastIM.entity.Report;
import com.pojul.objectsocket.message.RequestMessage;

public class ReportReq extends RequestMessage{

    private Report report;

    public ReportReq() {
        super();
        setRequestUrl("ReportReq");
    }

    public Report getReport() {
        return report;
    }

    public void setReport(Report report) {
        this.report = report;
    }
}
