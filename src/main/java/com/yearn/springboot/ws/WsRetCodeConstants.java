package com.yearn.springboot.ws;

/**
 * web socket recode枚举
 * Created by Zhou_Bing on 2019/6/3.
 */
public enum  WsRetCodeConstants {
    REPAIR_LIST_REFRESH(101,"维修单查询页面列表刷新"),
    MAINTAIN_LIST_REFRESH(201,"维护单查询页面列表刷新"),
    FCR_LIST_REFRESH(901,"故障件维修查询页面列表刷新");


    private Integer key;
    private String value;

    WsRetCodeConstants(Integer key, String value) {
        this.key = key;
        this.value = value;
    }

    public Integer getKey() {
        return key;
    }

    public void setKey(Integer key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
