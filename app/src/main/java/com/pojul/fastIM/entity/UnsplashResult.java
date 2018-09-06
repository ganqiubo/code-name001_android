package com.pojul.fastIM.entity;

import java.util.List;

public class UnsplashResult {

    private int total;
    private int total_pages;
    private List<UnsplashEntity> results;

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public int getTotal_pages() {
        return total_pages;
    }

    public void setTotal_pages(int total_pages) {
        this.total_pages = total_pages;
    }

    public List<UnsplashEntity> getResults() {
        return results;
    }

    public void setResults(List<UnsplashEntity> results) {
        this.results = results;
    }
}
