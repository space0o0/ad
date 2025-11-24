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
import com.google.android.gms.ads.RequestConfiguration;
import com.google.android.gms.ads.initialization.AdapterStatus;
import com.google.android.gms.ads.initialization.InitializationStatus;

import java.util.Arrays;

public class GoogleUtil implements AdvanceSplashPlusManager.ZoomCall {

    public static synchronized void initGoogle(final BaseParallelAdapter adapter, final InitListener listener) {
        try {
            final String tag = "[GoogleUtil.initGoogle]";

            String eMsg;
            if (adapter == null) {
                eMsg = tag + " initAD failed BaseParallelAdapter null";
                LogUtil.e(eMsg);
                if (listener != null) {
                    listener.fail(AdvanceError.ERROR_INIT_DEFAULT, eMsg);
                }
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

//            MobileAds.setRequestConfiguration(
//                    new RequestConfiguration.Builder()
//                            .setTestDeviceIds(Arrays.asList("ABCDEF012345"))
//                            .build());

            new Thread(() -> {
                // Initialize the Google Mobile Ads SDK on a background thread.
                MobileAds.initialize(adapter.getRealContext(), initializationStatus -> {
                    Log.d(tag, "initGoogle: "+initializationStatus.toString());
                    AdvanceGoogleManager.get().initStatus = AdvanceGoogleManager.INIT_STATUS_SUCCESS;
                    listener.success();
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
