package com.pinyougou.pay.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.wxpay.sdk.WXPay;
import com.github.wxpay.sdk.WXPayUtil;
import com.pinyougou.pay.servcie.WeixinPayService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import utils.HttpClient;

import java.util.HashMap;
import java.util.Map;

@Service
public class WeixinPayServiceImpl implements WeixinPayService {
    @Value("${appid}")
    private String appId;
    @Value("${partner}")
    private String mch_id;
    @Value("${partnerkey}")
    private String partnerkey;
    @Value("${notifyurl}")
    private String notifyurl;

    @Autowired
    private WXPayUtil wxPayUtil;

    @Override
    public Map<String, String> createNativeOrder(String out_trade_no, String total_fee) {

        try {
            //1.封装数据
            HashMap<String, String> map = new HashMap<>();
            map.put("appid", appId);
            map.put("mch_id", mch_id);
            map.put("nonce_str", wxPayUtil.generateNonceStr());
            map.put("body", "品优购商城");
            map.put("out_trade_no", out_trade_no);
            map.put("total_fee", total_fee);
            map.put("spbill_create_ip", "127.0.0.1");
            map.put("notify_url", notifyurl);
            map.put("trade_type", "NATIVE");
            String signedXml = wxPayUtil.generateSignedXml(map, partnerkey);

            //2.发送数据
            HttpClient client = new HttpClient("https://api.mch.weixin.qq.com/pay/unifiedorder");
            client.setHttps(true);
            client.setXmlParam(signedXml);
            client.post();

            //3.接收数据
            String response = client.getContent();
            boolean signatureValid = wxPayUtil.isSignatureValid(response, partnerkey);
            if (signatureValid) {
                HashMap<String, String> nativeOrder = new HashMap<>();
                Map<String, String> responseMap = wxPayUtil.xmlToMap(response);
                String return_code = responseMap.get("return_code");
                String result_code = responseMap.get("result_code");
                if (result_code.equals("SUCCESS") && return_code.equals("SUCCESS")) {
                    nativeOrder.put("code_url", responseMap.get("code_url"));
                    nativeOrder.put("out_trade_no", out_trade_no);
                    nativeOrder.put("total_fee", total_fee);
                }
                return nativeOrder;
            }
            return new HashMap<>();
        } catch (Exception e) {
            e.printStackTrace();
            return new HashMap<>();
        }
    }

    @Override
    public Map<String, String> getPayResult(String out_trade_no) {

        try {
            //1.封装数据
            HashMap<String, String> map = new HashMap<>();
            map.put("appid", appId);
            map.put("mch_id", mch_id);
            map.put("nonce_str", wxPayUtil.generateNonceStr());
            map.put("out_trade_no", out_trade_no);
            String signedXml = wxPayUtil.generateSignedXml(map, partnerkey);
            //2.发送请求调用接口
            HttpClient client = new HttpClient("https://api.mch.weixin.qq.com/pay/orderquery");
            client.setHttps(true);
            client.setXmlParam(signedXml);
            client.post();
            //3.获取返回结果
            String response = client.getContent();
            boolean signatureValid = wxPayUtil.isSignatureValid(response, partnerkey);

            if (signatureValid) {
                Map<String, String> responseMap = wxPayUtil.xmlToMap(response);
                String return_code = responseMap.get("return_code");
                String result_code = responseMap.get("result_code");
                if (return_code.equals("SUCCESS") && result_code.equals("SUCCESS")) {
                    String trade_state = responseMap.get("trade_state");
                    HashMap<String, String> resultMap = new HashMap<>();
                    resultMap.put("trade_state", trade_state);
                    if (trade_state.equals("SUCCESS")) {
                        resultMap.put("out_trade_no", responseMap.get("out_trade_no"));
                        resultMap.put("total_fee", responseMap.get("total_fee"));
                        resultMap.put("transaction_id",responseMap.get("transaction_id"));
                    }
                    return resultMap;
                }
            }

            HashMap<String, String> resultMap = new HashMap<>();
            return resultMap;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }



    }

    @Override
    public Map closePay(String out_trade_no) {
        //1.封装参数
        Map param=new HashMap();
        param.put("appid", appId);
        param.put("mch_id", mch_id);
        param.put("out_trade_no", out_trade_no);
        param.put("nonce_str", WXPayUtil.generateNonceStr());
        try {
            String xmlParam = WXPayUtil.generateSignedXml(param, partnerkey);
            //2.发送请求
            HttpClient httpClient=new HttpClient("https://api.mch.weixin.qq.com/pay/closeorder");
            httpClient.setHttps(true);
            httpClient.setXmlParam(xmlParam);
            httpClient.post();

            //3.获取结果
            String xmlResult = httpClient.getContent();
            Map<String, String> map = WXPayUtil.xmlToMap(xmlResult);
            System.out.println("调动查询API返回结果："+xmlResult);

            return map;
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return null;
        }
    }


}
