package com.advance.supplier.google;

import com.advance.itf.AdvancePrivacyController;
import com.advance.itf.AdvanceSupplierBridge;

public class GoogleGlobalConfig implements AdvanceSupplierBridge {
   @Override
   public void setCustomPrivacy(AdvancePrivacyController advancePrivacyController) {

   }

   @Override
   public String getSDKVersion() {
      return "24.7.0";
   }

   @Override
   public void setPersonalRecommend(boolean allow) {

   }
}
