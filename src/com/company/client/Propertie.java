package com.company.client;

/**
 * Created by wanghuiwen on 17-3-14.
 */
public class Propertie {
    private String baseDir;
    private String suffix;
    //���ڱ��
    private String bayonetId;
    //��������
    private String orientation;
    //������
    private String lane;
    //�ϴ���ɺ��ƶ������·����
    private String moveTo;
    private int port;
    private  String IP;
    private  String picture;

    public String getBaseDir() {
        return baseDir;
    }

    public void setBaseDir(String baseDir) {
        this.baseDir = baseDir;
    }

    public String getSuffix() {
        return suffix;
    }

    public void setSuffix(String suffix) {
        this.suffix = suffix;
    }

    public String getBayonetId() {
        return bayonetId;
    }

    public void setBayonetId(String bayonetId) {
        this.bayonetId = bayonetId;
    }

    public String getOrientation() {
        return orientation;
    }

    public void setOrientation(String orientation) {
        this.orientation = orientation;
    }

    public String getLane() {
        return lane;
    }

    public void setLane(String lane) {
        this.lane = lane;
    }

    public String getMoveTo() {
        return moveTo;
    }

    public void setMoveTo(String moveTo) {
        this.moveTo = moveTo;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getIP() {
        return IP;
    }

    public void setIP(String IP) {
        this.IP = IP;
    }

    public String getPicture() {
        return picture;
    }
    public void setPicture(String picture) {
        this.picture = picture;
    }
}

