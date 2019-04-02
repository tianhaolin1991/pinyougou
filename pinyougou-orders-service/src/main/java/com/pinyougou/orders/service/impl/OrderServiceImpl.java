package com.pinyougou.orders.service.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.pinyougou.mapper.TbOrderItemMapper;
import com.pinyougou.mapper.TbPayLogMapper;
import com.pinyougou.pojo.*;
import com.pinyougou.pojogroup.Cart;
import com.pinyougou.pojogroup.OrderExport;
import org.springframework.beans.factory.annotation.Autowired;
import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.pinyougou.mapper.TbOrderMapper;
import com.pinyougou.pojo.TbOrderExample.Criteria;
import com.pinyougou.orders.service.OrderService;

import entity.PageResult;
import org.springframework.data.redis.core.RedisTemplate;
import utils.IdWorker;

/**
 * 服务实现层
 *
 * @author Administrator
 */
@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private TbOrderMapper orderMapper;
    @Autowired
    private TbOrderItemMapper orderItemMapper;
    @Autowired
    private IdWorker idWorker;
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private TbPayLogMapper payLogMapper;


    /**
     * 查询全部
     */
    @Override
    public List<TbOrder> findAll() {
        return orderMapper.selectByExample(null);
    }

    /**
     * 按分页查询
     */
    @Override
    public PageResult findPage(int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        Page<TbOrder> page = (Page<TbOrder>) orderMapper.selectByExample(null);
        return new PageResult(page.getTotal(), page.getResult());
    }

    /**
     * 增加
     */
    @Override
    public void add(TbOrder order) {
        //1.从redis中取出购物车信息
        String userId = order.getUserId();
        List<Cart> cartList = (List<Cart>) redisTemplate.boundHashOps("cartList").get(userId);

        //2.将购物车信息存入数据库
        BigDecimal totalFee = new BigDecimal("0");
        ArrayList<String> orderList = new ArrayList<>();
        for (Cart cart : cartList) {
            TbOrder tbOrder = new TbOrder();
            tbOrder.setOrderId(idWorker.nextId()+"");
            tbOrder.setPayment(order.getPayment());
            tbOrder.setStatus("1");
            tbOrder.setCreateTime(new Date());
            tbOrder.setUpdateTime(new Date());
            tbOrder.setUserId(userId);
            tbOrder.setReceiverAreaName(order.getReceiverAreaName());
            tbOrder.setReceiver(order.getReceiver());
            tbOrder.setReceiverMobile(order.getReceiverMobile());
            //从控制层来
            tbOrder.setSourceType(order.getSourceType());
            tbOrder.setSellerId(cart.getSellerId());

            BigDecimal money = new BigDecimal(0);
            List<TbOrderItem> itemList = cart.getItemList();
            for (TbOrderItem orderItem : itemList) {
                orderItem.setId(idWorker.nextId()+"");

                orderItem.setOrderId(tbOrder.getOrderId());
                orderItem.setSellerId(cart.getSellerId());
                money = money.add(orderItem.getTotalFee());
                orderItemMapper.insert(orderItem);
            }
            tbOrder.setPayment(money);
            orderMapper.insert(tbOrder);
            totalFee = totalFee.add(money);
            orderList.add(tbOrder.getOrderId()+"");
        }
        if ("1".equals(order.getPaymentType())) {//如果是微信支付
            //生成支付日志
            TbPayLog payLog = new TbPayLog();
            payLog.setOutTradeNo(idWorker.nextId() + "");
            payLog.setCreateTime(new Date());
            payLog.setTotalFee(totalFee.multiply(new BigDecimal("100")));
            payLog.setUserId(userId);
            String listIds = orderList.toString().replace("[","").replace("]","").replace(" ","");
            payLog.setOrderList(listIds);
            payLog.setPayType("1");

            payLogMapper.insert(payLog);
            redisTemplate.boundHashOps("payLog").put(userId,payLog);
        }

        //3.清空redis中的购物车内容
        redisTemplate.boundHashOps("cartList").delete(order.getUserId());

    }


    /**
     * 修改
     */
    @Override
    public void update(TbOrder order) {
        orderMapper.updateByPrimaryKey(order);
    }

    /**
     * 根据ID获取实体
     *
     * @param id
     * @return
     */
    @Override
    public TbOrder findOne(String id) {
        return orderMapper.selectByPrimaryKey(id);
    }

    /**
     * 批量删除
     */
    @Override
    public void delete(String[] ids) {
        for (String id : ids) {
            orderMapper.deleteByPrimaryKey(id);
        }
    }


    @Override
    public PageResult findPage(TbOrder order, int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);

        TbOrderExample example = new TbOrderExample();
        Criteria criteria = example.createCriteria();

        if (order != null) {
            if (order.getPaymentType() != null && order.getPaymentType().length() > 0) {
                criteria.andPaymentTypeLike("%" + order.getPaymentType() + "%");
            }
            if (order.getPostFee() != null && order.getPostFee().length() > 0) {
                criteria.andPostFeeLike("%" + order.getPostFee() + "%");
            }
            if (order.getStatus() != null && order.getStatus().length() > 0) {
                criteria.andStatusLike("%" + order.getStatus() + "%");
            }
            if (order.getShippingName() != null && order.getShippingName().length() > 0) {
                criteria.andShippingNameLike("%" + order.getShippingName() + "%");
            }
            if (order.getShippingCode() != null && order.getShippingCode().length() > 0) {
                criteria.andShippingCodeLike("%" + order.getShippingCode() + "%");
            }
            if (order.getUserId() != null && order.getUserId().length() > 0) {
                criteria.andUserIdLike("%" + order.getUserId() + "%");
            }
            if (order.getBuyerMessage() != null && order.getBuyerMessage().length() > 0) {
                criteria.andBuyerMessageLike("%" + order.getBuyerMessage() + "%");
            }
            if (order.getBuyerNick() != null && order.getBuyerNick().length() > 0) {
                criteria.andBuyerNickLike("%" + order.getBuyerNick() + "%");
            }
            if (order.getBuyerRate() != null && order.getBuyerRate().length() > 0) {
                criteria.andBuyerRateLike("%" + order.getBuyerRate() + "%");
            }
            if (order.getReceiverAreaName() != null && order.getReceiverAreaName().length() > 0) {
                criteria.andReceiverAreaNameLike("%" + order.getReceiverAreaName() + "%");
            }
            if (order.getReceiverMobile() != null && order.getReceiverMobile().length() > 0) {
                criteria.andReceiverMobileLike("%" + order.getReceiverMobile() + "%");
            }
            if (order.getReceiverZipCode() != null && order.getReceiverZipCode().length() > 0) {
                criteria.andReceiverZipCodeLike("%" + order.getReceiverZipCode() + "%");
            }
            if (order.getReceiver() != null && order.getReceiver().length() > 0) {
                criteria.andReceiverLike("%" + order.getReceiver() + "%");
            }
            if (order.getInvoiceType() != null && order.getInvoiceType().length() > 0) {
                criteria.andInvoiceTypeLike("%" + order.getInvoiceType() + "%");
            }
            if (order.getSourceType() != null && order.getSourceType().length() > 0) {
                criteria.andSourceTypeLike("%" + order.getSourceType() + "%");
            }
            if (order.getSellerId() != null && order.getSellerId().length() > 0) {
                criteria.andSellerIdLike("%" + order.getSellerId() + "%");
            }

        }

        Page<TbOrder> page = (Page<TbOrder>) orderMapper.selectByExample(example);
        return new PageResult(page.getTotal(), page.getResult());
    }

    public void updateOrderStatus(String out_trade_no,String transaction_id) {
        //1.修改支付日志状态
        TbPayLog payLog = payLogMapper.selectByPrimaryKey(out_trade_no);
        payLog.setPayTime(new Date());
        payLog.setTradeState("1");//已支付
        payLog.setTransactionId(transaction_id);//交易号
        payLogMapper.updateByPrimaryKey(payLog);
        //2.更改订单数据
        //2.修改订单状态
        String orderList = payLog.getOrderList();//获取订单号列表
        String[] orderIds = orderList.split(",");//获取订单号数组

        for (String orderId : orderIds) {
            TbOrder order = orderMapper.selectByPrimaryKey(orderId);
            if (order != null) {
                order.setStatus("2");//已付款
                orderMapper.updateByPrimaryKey(order);
            }
        }

        //3.清除redis缓存
        //清除redis缓存数据
        redisTemplate.boundHashOps("payLog").delete(payLog.getUserId());
    }

    /**
     * 通过userId和status查找orderList
     * @param userId
     * @return
     */
    @Override
    public PageResult findOrderListByUserIdAndStatus(String userId, String status,Integer pageNum,Integer pageSize) {
        if(pageNum==null||pageNum.equals("")){
            pageNum = 1;
        }
        if(pageSize==null||pageSize.equals("")){
            pageSize = 5;
        }

        PageHelper.startPage(pageNum,pageSize);
        Page page = (Page)orderMapper.findOrderListByUserIdAndStatus(userId, status);
        return new PageResult<TbOrder>(page.getTotal(),page.getResult());

    }

    /**
     * 用户从用户中心付款时添加payLog
     * @param orderId
     */
    @Override
    public String addPayLogFromUserOrderCenter(String orderId) {
        //1.根据OrderId获取orderItemList
        TbOrder order = orderMapper.selectByPrimaryKey(orderId);

        //2.生成支付日志
        TbPayLog payLog = new TbPayLog();
        payLog.setOutTradeNo(idWorker.nextId() + "");
        payLog.setCreateTime(new Date());
        payLog.setTotalFee(order.getPayment().multiply(new BigDecimal(100)));
        payLog.setUserId(order.getUserId());
        payLog.setOrderList(orderId+"");
        payLog.setPayType("1");

        payLogMapper.insert(payLog);
        redisTemplate.boundHashOps("payLog").put(order.getUserId(),payLog);

        return payLog.getOutTradeNo();
    }

    /**
     * 确认收货
     * @param orderId
     */
    @Override
    public void transactionConfirm(String orderId) {
        TbOrder order = orderMapper.selectByPrimaryKey(orderId);
        order.setStatus("5");
        orderMapper.updateByPrimaryKey(order);
    }

    /**
     * 取消收货
     * @param orderId
     */
    @Override
    public void transactionCancel(String orderId) {
        TbOrder order = orderMapper.selectByPrimaryKey(orderId);
        order.setStatus("3");
        orderMapper.updateByPrimaryKey(order);
    }

    /**
     * 根据商家id查询orderList
     * @param sellerId
     * @return
     */
    @Override
    public PageResult findNotSendOrdersBySeller(String sellerId,Integer pageNum,Integer pageSize) {
        PageHelper.startPage(pageNum,pageSize);
        Page page = (Page) orderMapper.findOrderListBySellerIdAndStatus(sellerId,"2");
        return new PageResult(page.getTotal(),page.getResult());
    }

    /**
     * 将订单的状态改为已发货
     * @param orderId
     */
    @Override
    public void sendGoods(String orderId) {
        TbOrder order = orderMapper.selectByPrimaryKey(orderId);
        order.setStatus("4");
        orderMapper.updateByPrimaryKey(order);
    }

    /**
     * 根据sellerId,status,startTime,endTime来查询
     * @param sellerId
     * @param status
     * @param startTime
     * @param endTime
     * @return
     */
    @Override
    public List<OrderExport> findOrderListByTimeAreaAndStatus(String sellerId, String status, Date startTime, Date endTime) {
        return orderMapper.findOrderExportByTimeAreaAndStatus(sellerId,status,startTime,endTime);
    }



}