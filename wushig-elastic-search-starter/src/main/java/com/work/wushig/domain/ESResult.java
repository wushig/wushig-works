package com.work.wushig.domain;

import com.alibaba.fastjson.JSONObject;

import java.util.List;

/**
 * wushig-works-main
 * com.work.wushig.domain
 * 高建军
 * 2022/6/8
 */
public class ESResult<T> {

    private long took;

    private long total;

    private List<T> rows;

    private JSONObject aggregations;

    public long getTook() {
        return took;
    }

    public void setTook(long took) {
        this.took = took;
    }

    public long getTotal() {
        return total;
    }

    public void setTotal(long total) {
        this.total = total;
    }

    public List<T> getRows() {
        return rows;
    }

    public void setRows(List<T> rows) {
        this.rows = rows;
    }

    public JSONObject getAggregations() {
        return aggregations;
    }

    public void setAggregations(JSONObject aggregations) {
        this.aggregations = aggregations;
    }
}
