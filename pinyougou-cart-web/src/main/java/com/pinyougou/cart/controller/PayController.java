package com.pinyougou.cart.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.orders.service.OrderService;
import com.pinyougou.pay.servcie.WeixinPayService;
import com.pinyougou.pojo.TbPayLog;
import entity.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/pay")
public class PayController {

    @Reference
    private WeixinPayService weixinPayService;
    @Reference
    private OrderService orderService;
    @Autowired
    private RedisTemplate redisTemplate;

    @RequestMapping("/generateNativePayOR")
    @ResponseBody
    public Map<String, String> generateNativePayOR() {
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();
        TbPayLog payLog = (TbPayLog)redisTemplate.boundHashOps("payLog").get(userId);
        if(payLog!=null){
            BigDecimal totalFee = payLog.getTotalFee();
            String outTradeNo = payLog.getOutTradeNo();
            Map<String, String> nativeOrder = weixinPayService.createNativeOrder(outTradeNo, totalFee.intValue()+"");
            System.out.println(totalFee);
            System.out.println(nativeOrder.get("code_url"));
            return nativeOrder;
        }else{
            return new HashMap<>();
        }

    }

    @RequestMapping("/getPayResult")
    @ResponseBody
    public Result getPayResult(String out_trade_no) {

        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        int times = 0;

        while (true) {
            System.out.println("监听中...");
            Map<String, String> payResult = weixinPayService.getPayResult(out_trade_no);
            String trade_state = payResult.get("trade_state");
            System.out.println(trade_state);

            if (trade_state == null) {
                return new Result(false,"支付失败");
            }
            if ("SUCCESS".equals(trade_state)) {
                orderService.updateOrderStatus(out_trade_no,payResult.get("transaction_id"));
                return new Result(true, "支付成功");
            }


            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            if (times > 10) {
                System.out.println("支付超时");
                return new Result(false, "支付超时");
            }

            times ++;
        }
    }
}
