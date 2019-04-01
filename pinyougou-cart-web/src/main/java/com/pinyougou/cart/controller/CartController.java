package com.pinyougou.cart.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.pinyougou.cart.service.CartService;
import com.pinyougou.pojogroup.Cart;
import entity.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import utils.CookieUtil;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/cart")
public class CartController {

    @Reference
    private CartService cartService;
    @Autowired
    private HttpServletRequest request;
    @Autowired
    private HttpServletResponse response;


    @RequestMapping("/findCartList")
    public List<Cart> findCartList() {

        //1.从cookie中获取购物车信息
        String stringCartList = CookieUtil.getCookieValue(request, "cartList", "UTF-8");
        if (stringCartList == null || stringCartList.equals("")) {
            stringCartList = "[]";
        }
        List<Cart> cartList = JSON.parseArray(stringCartList, Cart.class);

        //2.获取用户信息
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        if (username.equals("anonymousUser")) {
            System.out.println("从cookie中获取购物车列表");
            return cartList;
        } else {
            List<Cart> cartList_redis = cartService.getCartListFromRedis(username);
            if(cartList_redis==null){
                cartList_redis = new ArrayList<>();
            }
            if (cartList.size() != 0) {
                cartList = cartService.mergeCartList(cartList, cartList_redis);
                CookieUtil.deleteCookie(request, response, "cartList");
                cartService.setCartListToRedis(username, cartList);
                System.out.println("合并cartList");
                return cartList;
            }
            System.out.println("从redis中获取数据");
            return cartList_redis;
        }


    }

    @RequestMapping("/addItemToCartList")
    @CrossOrigin(origins = "http://localhost:9105",allowCredentials = "true")
    public Result addItemToCart(Long itemId, Integer num) {
        //response.setHeader("access-control-allow-origin","http://localhost:9105");
        //response.setHeader("access-control-allow-credentials","true");
        try {
            //0.获取用户信息
            String username = SecurityContextHolder.getContext().getAuthentication().getName();
            //1.findCartList
            List<Cart> cartList = findCartList();
            //2.将商品加入购物车
            cartList = cartService.addItemToCart(itemId, num, cartList);

            if (username.equals("anonymousUser")) {
                //3.将购物车信息加入cookie
                String stringCartList = JSON.toJSONString(cartList);
                CookieUtil.setCookie(request, response, "cartList", stringCartList, 3600 * 24, "UTF-8");
            } else {
                //3.将购物车信息加入redis
                cartService.setCartListToRedis(username, cartList);
            }
            return new Result(true, "添加成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, "添加失败");
        }
    }

}
