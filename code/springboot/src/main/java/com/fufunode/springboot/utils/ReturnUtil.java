package com.fufunode.springboot.utils;

public class ReturnUtil {
    private boolean result; // 返回调用情况的
    private ReturnUtilData data;

    public boolean isResult() {
        return result;
    }

    public void setResult(boolean result) {
        this.result = result;
    }

    public ReturnUtilData getData() {
        return data;
    }

    public void setData(ReturnUtilData data) {
        this.data = data;
    }

    public ReturnUtil() {

    }

    public ReturnUtil(boolean result, boolean flag, String message, Object data) {
        this.result = result;
        this.data = new ReturnUtilData();
        this.data.setData(data);
        this.data.setFlag(flag);
        this.data.setMessage(message);
    }
}
