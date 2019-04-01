package com.pinyougou.cart.service;

import com.pinyougou.pojogroup.Cart;

import java.util.List;

public interface CartService {

    /**
     * 根据itemID将商品添加到购物车
     * @param itemId
     * @param cart
     * @return
     */
    public List<Cart> addItemToCart(Long itemId, Integer num ,List<Cart> cart);

    /**
     * 以用户名为二级key来存储cartList
     * @param username
     * @param cartList
     */
    public void setCartListToRedis(String username,List<Cart> cartList);


    /**
     * 通过用户名来取出redis中的购物车
     * @param username
     * @return
     */
    public List<Cart> getCartListFromRedis(String username);


    /**
     * 合并
     * @param cartList1
     * @param cartList2
     * @return
     */
    public List<Cart> mergeCartList(List<Cart> cartList1,List<Cart> cartList2);
}
