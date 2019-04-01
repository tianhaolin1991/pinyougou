package com.pinyougou.user.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.orders.service.OrderService;
import com.pinyougou.pojo.TbOrder;
import entity.PageResult;
import entity.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/order")
public class OrderController {

    @Reference
    private OrderService orderService;

    @RequestMapping("/findOrderListByUserIdAndStatus")
    public PageResult findOrderListByUserIdAndStatus(String status, Integer pageNum, Integer pageSize){
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();
        return orderService.findOrderListByUserIdAndStatus(userId,status,pageNum,pageSize);
    }

    @RequestMapping("/addPayLogFromUserOrderCenter")
    public Result addPayLogFromUserOrderCenter(String orderId){

        try {
            String out_trade_id = orderService.addPayLogFromUserOrderCenter(orderId);
            return new Result(true,out_trade_id);
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false,"未知错误");
        }
    }

    @RequestMapping("/transactionConfirm")
    public Result transactionConfirm(String orderId){

        try {
            orderService.transactionConfirm(orderId);
            return new Result(true,"确认成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false,"确认失败");
        }
    }

    @RequestMapping("/transactionCancel")
    public Result transactionCanCel(String orderId){

        try {
            orderService.transactionCancel(orderId);
            return new Result(true,"取消成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false,"取消失败");
        }
    }
}
