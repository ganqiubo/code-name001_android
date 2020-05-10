package com.pojul.fastIM.entity;

import java.util.List;

public class PixabayEntityResults extends BaseEntity{

    private long total;
    private long totalHits;
    private List<PixabayEntity> hits;

    public long getTotal() {
        return total;
    }

    public void setTotal(long total) {
        this.total = total;
    }

    public long getTotalHits() {
        return totalHits;
    }

    public void setTotalHits(long totalHits) {
        this.totalHits = totalHits;
    }

    public List<PixabayEntity> getHits() {
        return hits;
    }

    public void setHits(List<PixabayEntity> hits) {
        this.hits = hits;
    }
}
