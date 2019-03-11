package com.pinyougou.shop.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.pojo.TbItem;
import com.pinyougou.pojo.TbItemCat;
import com.pinyougou.sellergoods.service.ItemCatService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/itemCat")
public class ItemCatController {

    @Reference
    private ItemCatService itemCatService;
    @RequestMapping("/findOne")
    public TbItemCat findOne(Long id){
        return itemCatService.findOne(id);
    }
    /**
     * 通过parentId来查询商品分类
     * @param parentId
     * @return
     */
    @RequestMapping("/findItemsByParentId")
    public List<TbItemCat> findItemsByParentId(Long parentId){
        return itemCatService.findItemsByParentId(parentId);
    }

    @RequestMapping("/findAll")
    public List<TbItemCat> findAll(){
        return itemCatService.findAll();
    }
}
