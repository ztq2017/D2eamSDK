package com.overseas.exports.platfrom;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;


import com.overseas.exports.common.TwitterRestClient;
import com.overseas.exports.task.SdkAsyncHttpStandardResponseHandler;


public class RePayHelper {
    /*
        每次支付前，先将订单保存在本地，如果有回调，就删除本地的（主要为了解决用户支付后没有点完成或者无法回调到游戏）
        如果失败就不会被清除，下次调用前先判断有无临时订单，有就先处理完异常订单
     */
    private String mSpfKeyTempOrderNum = "temp_order_num"; // 临时订单的订单号
    private String mSpfKeyTempMoney = "temp_order_money"; // 临时订单的支付金额
    private String mSpfKeyTempProductId = "temp_order_product_id"; // 临时订单的支付商品ID
    private String mSpfKeyTempBase64EncodedPublicKey = "temp_order_product_base64EncodedPublicKey"; // 临时订单的支付商品ID
    private String mSpfKeyTempPurchaseData = "temp_order_purchase_data"; // 临时订单的google支付 返回的参数
    private String mSpfKeyTempDataSignature = "temp_order_data_signature"; // 临时订单的google 支付返回的参数
    private SharedPreferences mSpf;

    public RePayHelper(Activity activity) {
        mSpf = activity.getSharedPreferences("google_pay", Context.MODE_PRIVATE);

    }

    /**
     * 是否有补单
     *
     * @return boolean
     */
    public boolean hasReOrder() {
        return !TextUtils.isEmpty(mSpf.getString(mSpfKeyTempOrderNum, null));
    }

    public String getBase64EncodedPublicKey() {
        return mSpf.getString(mSpfKeyTempBase64EncodedPublicKey, null);
    }

    /**
     * 获取需要补单的订单号
     *
     * @return 订单号
     */
    public String getOrderNum() {
        return mSpf.getString(mSpfKeyTempOrderNum, null);
    }

    /**
     * 获取需要补单的订单金额
     *
     * @return 订单金额
     */
    public int getMoney() {
        return mSpf.getInt(mSpfKeyTempMoney, 1);
    }

    /**
     * 获取需要补单的订单商品ID
     *
     * @return 商品ID
     */
    public String getProductId() {
        return mSpf.getString(mSpfKeyTempProductId, null);
    }

    public String getPurchaseData() {
        return mSpf.getString(mSpfKeyTempPurchaseData, null);
    }

    public String getDataSignature() {
        return mSpf.getString(mSpfKeyTempDataSignature, null);
    }

    /**
     * 补单
     *
     * @param postJson 请求参数(Json对象)
     */
    public void repay(String url, String postJson, SdkAsyncHttpStandardResponseHandler callbackListener) {
        TwitterRestClient.post(url, postJson, callbackListener);
    }

    /**
     * 保存订单信息
     *
     * @param orderNum               订单id
     * @param tenCoin                订单金额
     * @param productId              商品ID
     * @param base64EncodedPublicKey Google PublicKey
     * @param purchaseData           Google支付成功返回数据
     * @param dataSignature          Google支付成功返回数据
     */
    public void saveOrder(String orderNum, int tenCoin, String productId, String base64EncodedPublicKey, String purchaseData, String dataSignature) {
        mSpf.edit()
                .putString(mSpfKeyTempOrderNum, orderNum)
                .putInt(mSpfKeyTempMoney, tenCoin)
                .putString(mSpfKeyTempProductId, productId)
                .putString(mSpfKeyTempBase64EncodedPublicKey, base64EncodedPublicKey)
                .putString(mSpfKeyTempPurchaseData, purchaseData)
                .putString(mSpfKeyTempDataSignature, dataSignature)
                .apply();
    }

    /**
     * 清除补单信息
     */
    public void clearReOrder() {
        mSpf.edit()
                .remove(mSpfKeyTempOrderNum)
                .remove(mSpfKeyTempMoney)
                .remove(mSpfKeyTempProductId)
                .remove(mSpfKeyTempBase64EncodedPublicKey)
                .remove(mSpfKeyTempPurchaseData)
                .remove(mSpfKeyTempDataSignature)
                .apply();
    }


}
