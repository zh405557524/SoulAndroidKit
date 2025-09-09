package com.soul.common.module.bluetooth.ble;//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//


import java.io.Serializable;

public class BleCommunication implements Serializable {
    private static final long serialVersionUID = 6687644565867279708L;
    private String cmd;
    private String value;
    private int leftBattery;
    private int rightBattery;
    private int boxBattery;
    private long cnt;
    private int frequency;
    private Type type;
    private String mac;
    private String name;

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMac() {
        return this.mac;
    }

    public void setMac(String mac) {
        this.mac = mac;
    }

    public int getLeftBattery() {
        return this.leftBattery;
    }

    public void setLeftBattery(int leftBattery) {
        this.leftBattery = leftBattery;
    }

    public int getRightBattery() {
        return this.rightBattery;
    }

    public void setRightBattery(int rightBattery) {
        this.rightBattery = rightBattery;
    }

    public int getBoxBattery() {
        return this.boxBattery;
    }

    public void setBoxBattery(int boxBattery) {
        this.boxBattery = boxBattery;
    }

    public long getCnt() {
        return this.cnt;
    }

    public void setCnt(long cnt) {
        this.cnt = cnt;
    }

    public int getFrequency() {
        return this.frequency;
    }

    public void setFrequency(int frequency) {
        this.frequency = frequency;
    }

    public BleCommunication(Type type) {
        this.type = type;
    }

    public BleCommunication(String cmd) {
        this.cmd = cmd;
    }

    public String getCmd() {
        return this.cmd;
    }

    public void setCmd(String cmd) {
        this.cmd = cmd;
    }

    public String getValue() {
        return this.value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public Type getType() {
        return this.type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public String toString() {
        return "BleCommunication{cmd='" + this.cmd + '\'' + ", value='" + this.value + '\'' + ", leftBattery=" + this.leftBattery + ", rightBattery=" + this.rightBattery + ", boxBattery=" + this.boxBattery + ", cnt=" + this.cnt + ", frequency=" + this.frequency + ", type=" + this.type + ", mac='" + this.mac + '\'' + '}';
    }

    public static enum Type {
        HEARTBEAT,
        DOUBLE_CLICK,
        CERTIFICATION;

        private Type() {
        }
    }
}
