package com.yu.drysister.Bean;

import java.io.Serializable;

/**
 * CREATED BY DY ON 2019/7/10.
 * TIME BY 16:34.
 **/
public  class ResultsBean implements Serializable {
    /**
     * _id : 5c6a4ae99d212226776d3256
     * createdAt : 2019-02-18T06:04:25.571Z
     * desc : 2019-02-18
     * publishedAt : 2019-02-18T06:05:41.975Z
     * source : web
     * type : 福利
     * url : https://ws1.sinaimg.cn/large/0065oQSqly1g0ajj4h6ndj30sg11xdmj.jpg
     * used : true
     * who : lijinshanmx
     */
//        public ResultsBean(String _id
//                , String createdAt
//                , String desc
//                , String publishedAt
//                , String source
//                , String type
//                , String url
//                , String used
//                , String who) {
//
//        }

    private String _id;
    private String createdAt;
    private String desc;
    private String publishedAt;
    private String source;
    private String type;
    private String url;
    private String used;
    private String who;

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getPublishedAt() {
        return publishedAt;
    }

    public void setPublishedAt(String publishedAt) {
        this.publishedAt = publishedAt;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String isUsed() {
        return used;
    }

    public void setUsed(String used) {
        this.used = used;
    }

    public String getWho() {
        return who;
    }

    public void setWho(String who) {
        this.who = who;
    }
}
