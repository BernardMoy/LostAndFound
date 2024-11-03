package com.example.lostandfound;

public class VerificationData {
    private String code;
    private long timestamp;

    public VerificationData(String code, long timestamp) {
        this.code = code;
        this.timestamp = timestamp;
    }


    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
