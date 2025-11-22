package com.advance.supplier.google;

class AdvanceGoogleManager {
    //默认值，会执行advance原有google初始化逻辑
    public static final int INIT_STATUS_DEFAULT = 0;
    //以下三个状态，会影响google初始化执行逻辑，由外部APP调用改变
    public static final int INIT_STATUS_CALLING = 1;
    public static final int INIT_STATUS_SUCCESS = 2;
    public static final int INIT_STATUS_FAILED = 3;

    //记录初始化状态
    protected int initStatus = INIT_STATUS_DEFAULT;

    //初始化失败信息记录
    protected int initErrCode;
    protected String initErrMsg;


    //内部初始化状态标记
    public int innerInitStatus = INIT_STATUS_DEFAULT;
    public int innerInitErrCode;
    public String innerInitErrMsg;

    public AdvanceGoogleManager() {
    }

    public static AdvanceGoogleManager get() {
        return configHolder.configIns;
    }

    public static class configHolder {
        private static final AdvanceGoogleManager configIns = new AdvanceGoogleManager();
    }
}
