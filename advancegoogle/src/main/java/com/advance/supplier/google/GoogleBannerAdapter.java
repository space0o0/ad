package com.advance.supplier.google;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;

import com.advance.BannerSetting;
import com.advance.custom.AdvanceBannerCustomAdapter;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;

public class GoogleBannerAdapter extends AdvanceBannerCustomAdapter {
    private AdView adView;

    public GoogleBannerAdapter(Activity activity, BannerSetting bannerSetting) {
        super(activity, bannerSetting);
    }

    @Override
    public void orderLoadAd() {
        paraLoadAd();
    }

    @Override
    protected void paraLoadAd() {
        GoogleUtil.initGoogle(this, new GoogleUtil.InitListener() {
            @Override
            public void success() {

            }

            @Override
            public void fail(int code, String msg) {
                handleFailed(code, msg);
            }
        });
    }

    @Override
    protected void adReady() {

    }

    @Override
    public void doDestroy() {
        if (adView != null) {
            View parentView = (View) adView.getParent();
            if (parentView instanceof ViewGroup) {
                ((ViewGroup) parentView).removeView(adView);
            }

            // Destroy the banner ad resources.
            adView.destroy();
        }

        // Drop reference to the banner ad.
        adView = null;
    }

    @Override
    public void show() {
        adView = new AdView(this.getRealContext());
        adView.setAdUnitId("ca-app-pub-3940256099942544/9214589741");
        // [START set_ad_size]
        // Request an anchored adaptive banner with a width of 360.
        adView.setAdSize(AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(this.getRealContext(), 360));
        // [END set_ad_size]

        // Replace ad container with new ad view.
        bannerSetting.getContainer().removeAllViews();
        bannerSetting.getContainer().addView(adView);
    }
}
