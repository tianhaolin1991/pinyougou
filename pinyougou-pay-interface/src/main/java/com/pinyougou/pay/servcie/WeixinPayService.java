package com.pinyougou.pay.servcie;

import java.util.Map;

public interface WeixinPayService {

    /**
     * 生成本地支付订单
     * @return
     */
    Map<String,String> createNativeOrder(String out_trade_no,String total_fee);


    /**
     * 根据订单号来查询订单支付情况
     * @param out_trade_no
     * @return
     */
    Map<String,String> getPayResult(String out_trade_no);

    /**
     *
     */
    public Map closePay(String out_trade_no);
}
