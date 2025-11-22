package com.advance.supplier.google;

import android.app.Activity;
import android.util.Log;

import com.advance.AdvanceSetting;
import com.advance.BaseParallelAdapter;
import com.advance.model.AdvanceError;
import com.advance.utils.AdvanceSplashPlusManager;
import com.advance.utils.LogUtil;
import com.bayes.sdk.basic.itf.BYAbsCallBack;
import com.bayes.sdk.basic.widget.BYScheduleTimer;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.AdapterStatus;
import com.google.android.gms.ads.initialization.InitializationStatus;

public class GoogleUtil implements AdvanceSplashPlusManager.ZoomCall {

    public static synchronized void initGoogle(final BaseParallelAdapter adapter, final InitListener listener) {
        try {
            final String tag = "[GoogleUtil.initGoogle]";
            int customInitStatus = AdvanceGoogleManager.get().initStatus;
            if (customInitStatus == AdvanceGoogleManager.INIT_STATUS_SUCCESS) {
                LogUtil.d(tag + "outer init success");

                if (listener != null) {
                    listener.success();
                }
                AdvanceSetting.getInstance().hasGOOGLEInit = true;
                return;
            } else if (customInitStatus == AdvanceGoogleManager.INIT_STATUS_FAILED) {
                LogUtil.d(tag + "outer init failed");

                if (listener != null) {
                    listener.fail(AdvanceGoogleManager.get().initErrCode, AdvanceGoogleManager.get().initErrMsg);
                }

                return;
            } else if (customInitStatus == AdvanceGoogleManager.INIT_STATUS_CALLING) {
                //如果外部已经调用过init方法，但是还没有结果时，需要等待结果状态返回
                LogUtil.d(tag + "outer init calling ，waiting result check");
                //每N毫秒轮询一次结果
                final boolean[] hasCallBack = {false};
                //轮询最大等待时间，超时后不再关注结果，按照succ处理
                final long scheduleTimeout = 1500L;
                final long startTime = System.currentTimeMillis();

                new BYScheduleTimer(50, new BYAbsCallBack<BYScheduleTimer>() {
                    @Override
                    public void invoke(BYScheduleTimer byScheduleTimer) {

                        try {
                            long cost = System.currentTimeMillis() - startTime;
                            LogUtil.d(tag + "result check running，cost：" + cost);

                            //检查是否超时，如果超时了，按照初始化成功处理，执行后面的广告load
                            if (cost > scheduleTimeout) {
                                LogUtil.d(tag + "result check timeout");

                                if (listener != null && !hasCallBack[0]) {
                                    hasCallBack[0] = true;
                                    listener.success();
                                }
                                AdvanceSetting.getInstance().hasCSJInit = true;

                                if (byScheduleTimer != null) {
                                    byScheduleTimer.cancel();
                                }
                                return;
                            }

                            //检查结果状态
                            int outerInitStatus = AdvanceGoogleManager.get().initStatus;
                            if (outerInitStatus == AdvanceGoogleManager.INIT_STATUS_SUCCESS) {
                                LogUtil.d(tag + "result check: outer init success");

                                if (listener != null && !hasCallBack[0]) {
                                    hasCallBack[0] = true;
                                    listener.success();
                                }
                                AdvanceSetting.getInstance().hasCSJInit = true;

                                if (byScheduleTimer != null) {
                                    byScheduleTimer.cancel();
                                }
                            } else if (outerInitStatus == AdvanceGoogleManager.INIT_STATUS_FAILED) {
                                LogUtil.d(tag + "result check: outer init failed");

                                if (listener != null && !hasCallBack[0]) {
                                    hasCallBack[0] = true;
                                    listener.fail(AdvanceGoogleManager.get().initErrCode, AdvanceGoogleManager.get().initErrMsg);
                                }
                                if (byScheduleTimer != null) {
                                    byScheduleTimer.cancel();
                                }
                            }
                        } catch (Throwable e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
                return;
            }

            boolean hasInit = AdvanceSetting.getInstance().hasGOOGLEInit;
            if (hasInit) {
                LogUtil.simple("[GoogleUtil.initGoogle] already init");
                if (listener != null) {
                    listener.success();
                }
                return;
            }

            new Thread(() -> {
                // Initialize the Google Mobile Ads SDK on a background thread.
                MobileAds.initialize(adapter.getRealContext(), initializationStatus -> {
                    Log.d(tag, "initGoogle: "+initializationStatus);
                });
            }).start();

        } catch (Throwable e) {
            String msg = "google SDK 初始化异常";
            LogUtil.e(msg);
            e.printStackTrace();
            if (listener != null) {
                listener.fail(AdvanceError.ERROR_INIT_DEFAULT, msg);
            }
        }
    }

    @Override
    public void zoomOut(Activity activity) {

    }

    public interface InitListener {
        void success();

        void fail(int code, String msg);
    }
}
