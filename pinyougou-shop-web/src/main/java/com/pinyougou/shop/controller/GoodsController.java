package com.pinyougou.shop.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.pojo.TbGoods;
import com.pinyougou.pojogroup.GoodsGroup;
import com.pinyougou.sellergoods.service.GoodsService;
import com.pinyougou.shop.service.UserService;
import entity.PageResult;
import entity.Result;
import junit.textui.ResultPrinter;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * controller
 *
 * @author Administrator
 */
@RestController
@RequestMapping("/goods")
public class GoodsController {

    @Reference
    private GoodsService goodsService;

    @RequestMapping("/add")
    public Result add(@RequestBody GoodsGroup goodsGroup) {
        try {
            TbGoods goods = goodsGroup.getGoods();
            String sellerId = SecurityContextHolder.getContext().getAuthentication().getName();
            goods.setSellerId(sellerId);
            goodsService.add(goodsGroup);
            return new Result(true, "添加成功!");
        } catch (Exception e) {
            e.getStackTrace();
            return new Result(false, "添加失败!");
        }

    }

    @RequestMapping("/update")
    public Result update(@RequestBody GoodsGroup goodsGroup) {
        GoodsGroup goods2 = goodsService.findOne(goodsGroup.getGoods().getId());
        String seller = SecurityContextHolder.getContext().getAuthentication().getName();
        if (!goods2.getGoods().getSellerId().equals(seller) || !goodsGroup.getGoods().getSellerId().equals(seller)) {
            return new Result(false, "非法操作!");
        }
        try {
            goodsService.update(goodsGroup);
            return new Result(true, "修改成功!");
        } catch (Exception e) {
            e.getStackTrace();
            return new Result(false, "修改失败!");
        }

    }

    @RequestMapping("/search")
    public PageResult search(Integer currentPage, Integer pageSize, @RequestBody TbGoods goods) {
        String seller = SecurityContextHolder.getContext().getAuthentication().getName();
        goods.setSellerId(seller);
        return goodsService.findPage(goods, currentPage, pageSize);
    }

    @RequestMapping("/findOne")
    public GoodsGroup findOne(Long id) {
        return goodsService.findOne(id);
    }

}
