package com.pinyougou.cart.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.pinyougou.cart.service.CartService;
import com.pinyougou.mapper.TbItemMapper;
import com.pinyougou.pojo.TbItem;
import com.pinyougou.pojo.TbOrderItem;
import com.pinyougou.pojogroup.Cart;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
public class CartServiceImpl implements CartService {

    @Autowired
    private TbItemMapper itemMapper;
    @Autowired
    private RedisTemplate redisTemplate;

    @Override
    public List<Cart> addItemToCart(Long itemId, Integer num, List<Cart> cartList) {
        //1.根据itemId从数据库中查找item
        TbItem item = itemMapper.selectByPrimaryKey(itemId);
        if (item == null || item.equals("")) {
            throw new RuntimeException("商品不存在");
        }
        if (!item.getStatus().equals("1")) {
            throw new RuntimeException("商品已下架");
        }

        //2.获取商家id
        String sellerId = item.getSellerId();
        //3.判断cart中是否存在sellerId

        Cart cart = searchCartBySellerId(cartList, sellerId);
        //4.1如果不存在,则创建一个新的购物车对象
        if (cart==null) {
            //设置cart的值
             cart = setNewCartInstance(item,num);
            cartList.add(cart);
        } else {
            //如果有该商家,则看商家明细中是否有该商品

            List<TbOrderItem> itemList = cart.getItemList();
            TbOrderItem orderItem = searchOrderItemByItemId(itemList, itemId);
            if(orderItem==null){
                //5.1. 如果没有，新增购物车明细
                orderItem=createOrderItem(item,num);
                cart.getItemList().add(orderItem);
            }else {
                //计算总价
                BigDecimal totalFee = orderItem.getTotalFee();
                BigDecimal price = orderItem.getPrice();
                Integer totalNum = orderItem.getNum();

                totalNum = totalNum + num;
                if (totalNum > 0) {
                    totalFee = price.multiply(new BigDecimal(totalNum));
                    orderItem.setNum(totalNum);
                    orderItem.setTotalFee(totalFee);
                } else {
                    //如果总数为0,则移除商品
                    itemList.remove(orderItem);
                    int itemListSize = itemList.size();
                    if (itemListSize == 0) {//如果商家商品为0,则移除商家
                        cartList.remove(cart);
                    }
                }
            }
        }
        return cartList;
    }

    private TbOrderItem createOrderItem(TbItem item,Integer num){
        if(num<=0){
            throw new RuntimeException("数量非法");
        }

        TbOrderItem orderItem=new TbOrderItem();
        orderItem.setGoodsId(item.getGoodsId());
        orderItem.setItemId(item.getId());
        orderItem.setNum(num);
        orderItem.setPicPath(item.getImage());
        orderItem.setPrice(item.getPrice());
        orderItem.setSellerId(item.getSellerId());
        orderItem.setTitle(item.getTitle());
        orderItem.setTotalFee(item.getPrice().multiply(new BigDecimal(num)));
        return orderItem;
    }
    private Cart setNewCartInstance(TbItem item,Integer num){
        Cart cart = new Cart();
        cart.setSellerName(item.getSeller());
        cart.setSellerId(item.getSellerId());

        List<TbOrderItem> itemList = new ArrayList<>();
        TbOrderItem orderItem = new TbOrderItem();
        orderItem.setSellerId(item.getSellerId());
        orderItem.setTitle(item.getTitle());
        orderItem.setGoodsId(item.getGoodsId());
        orderItem.setItemId(item.getId());
        orderItem.setNum(num);
        orderItem.setPrice(item.getPrice());
        orderItem.setTotalFee(item.getPrice().multiply(new BigDecimal(num)));
        orderItem.setPicPath(item.getImage());
        itemList.add(orderItem);

        cart.setItemList(itemList);

        return cart;
    }

    private Cart searchCartBySellerId(List<Cart> cartList, String sellerId){
        for(Cart cart:cartList){
            if(cart.getSellerId().equals(sellerId)){
                return cart;
            }
        }
        return null;
    }

    private TbOrderItem searchOrderItemByItemId(List<TbOrderItem> orderItemList ,Long itemId ){
        for(TbOrderItem orderItem :orderItemList){
            if(orderItem.getItemId().longValue()==itemId.longValue()){
                return orderItem;
            }
        }
        return null;
    }



    @Override
    public void setCartListToRedis(String username, List<Cart> cartList) {
        redisTemplate.boundHashOps("cartList").put(username,cartList);
    }

    @Override
    public List<Cart> getCartListFromRedis(String username) {
        List<Cart> cartList = (List<Cart>) redisTemplate.boundHashOps("cartList").get(username);
        return cartList;
    }

    @Override
    public List<Cart> mergeCartList(List<Cart> cartList1, List<Cart> cartList2) {
        for (Cart cart : cartList2) {
            for (TbOrderItem orderItem : cart.getItemList()) {
                addItemToCart(orderItem.getItemId(),orderItem.getNum(),cartList1);
            }
        }
        return cartList1;
    }
}
