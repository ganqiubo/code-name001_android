package com.pojul.fastIM.entity;

import com.google.gson.Gson;

public class Report extends BaseEntity{

    private int id;
    private String reportMessageUid;
    private String reportReason;
    private String detail;
    private String reporter;
    private String beReporter;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getReportMessageUid() {
        return reportMessageUid;
    }

    public void setReportMessageUid(String reportMessageUid) {
        this.reportMessageUid = reportMessageUid;
    }

    public String getReportReason() {
        return reportReason;
    }

    public void setReportReason(String reportReason) {
        this.reportReason = reportReason;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public String getReporter() {
        return reporter;
    }

    public void setReporter(String reporter) {
        this.reporter = reporter;
    }

    public String getBeReporter() {
        return beReporter;
    }

    public void setBeReporter(String beReporter) {
        this.beReporter = beReporter;
    }

    @Override
    public String toString() {
        return new Gson().toJson(this);
    }
}
