package com.andy.toolbox.bean;

import java.util.List;

/**
 * Created by luofan on 2019/3/19.
 */
public class PageBean<T> {

    private int pn;
    private int ps;
    private int tc;
    private List<T> dataList;

    public int getPageNumber() {
        return pn;
    }

    public void setPn(int pn) {
        this.pn = pn;
    }

    public int getPageSize() {
        return ps;
    }

    public void setPs(int ps) {
        this.ps = ps;
    }

    public int getTc() {
        return tc;
    }

    public void setTc(int tc) {
        this.tc = tc;
    }

    public List<T> getDataList() {
        return dataList;
    }

    public void setDataList(List<T> dataList) {
        this.dataList = dataList;
    }
}
