package com.overseas.exports.data;

public class FloatingItemInfo {
    private String name; // 子按钮名称
    private String icon; // 子按钮icon
    private String linkUrl; // 链接地址
    private boolean isHasNewMsg; // 是否有新消息

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getLinkUrl() {
        return linkUrl;
    }

    public void setLinkUrl(String linkUrl) {
        this.linkUrl = linkUrl;
    }

    public boolean isHasNewMsg() {
        return isHasNewMsg;
    }

    public void setHasNewMsg(boolean hasNewMsg) {
        this.isHasNewMsg = hasNewMsg;
    }

}
