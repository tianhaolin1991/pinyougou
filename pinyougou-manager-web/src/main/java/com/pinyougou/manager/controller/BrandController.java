package com.pinyougou.manager.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.pojo.TbBrand;
import com.pinyougou.sellergoods.service.BrandService;

import entity.PageResult;
import entity.Result;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/brand")
public class BrandController {
    @Reference
    private BrandService brandService;

    @RequestMapping("/findAll")
    @ResponseBody
    public List<TbBrand> findAll(){
        System.out.println(brandService);
        List<TbBrand> tbBrands = brandService.findAll();
        return tbBrands;
    }

    @RequestMapping("/findPage")
    public @ResponseBody
    PageResult findPage(Integer pageNum, Integer pageSize){
        PageResult brandPage = brandService.findPage(pageNum, pageSize);
        return brandPage;
    }

    @RequestMapping("/add")
    @ResponseBody
    public Result add(@RequestBody TbBrand tbBrand){
        try {
            brandService.add(tbBrand);
            return new Result(true,"增加成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false,e.getMessage());
        }
    }

    @RequestMapping("/update")
    @ResponseBody
    public Result update(@RequestBody TbBrand tbBrand){
        try {
            brandService.update(tbBrand);
            return new Result(true,"更新成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(true,e.getMessage());
        }
    }

    @RequestMapping("/findOne")
    @ResponseBody
    public TbBrand findOne(Long id){
       return brandService.findOne(id);
    }

    @RequestMapping("/delete")
    @ResponseBody
    public Result delete(long[] ids){
        try {
            brandService.delete(ids);
            return new Result(true,"删除成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(true,e.getMessage());
        }
    }

    @RequestMapping("/search")
    @ResponseBody
    public PageResult search(@RequestBody TbBrand tbBrand,Integer pageNum,Integer pageSize){
        PageResult page = brandService.findPage(tbBrand, pageNum, pageSize);
        return page;
    }

    @RequestMapping("/findOptionList")
    @ResponseBody
    public List<Map> findListOption(){
        List<Map> optionList = brandService.findOptionList();
        return optionList;
    }
}
