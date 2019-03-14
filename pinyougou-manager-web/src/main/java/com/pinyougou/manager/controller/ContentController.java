package com.pinyougou.manager.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.content.service.ContentService;
import com.pinyougou.pojo.TbContent;
import entity.PageResult;
import entity.Result;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/content")
public class ContentController {

    @Reference
    private ContentService contentService;

    @RequestMapping("/findAll")
    public List<TbContent> findAll(){
        List<TbContent> contentList = contentService.findAll();
        return contentList;
    }

    @RequestMapping("/findOne")
    public TbContent findOne(Long id){
        return contentService.findOne(id);
    }

    @RequestMapping("/add")
    public Result add(@RequestBody TbContent content){
        try {
            contentService.add(content);
            return new Result(true,"添加成功!");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(true,"添加失败!");
        }
    }

    @RequestMapping("/update")
    public Result update(@RequestBody TbContent content){
        try {
            contentService.update(content);
            return new Result(true,"添加成功!");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false,"添加失败!");
        }
    }

    @RequestMapping("/search")
    public PageResult search(Integer page, Integer rows, @RequestBody TbContent content){
        return contentService.findPage(content, page,rows);
    }

    @RequestMapping("dele")
    public Result delete(Long[] ids){
        try {
            contentService.delete(ids);
            return new Result(true,"删除成功!");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false,"删除失败!");
        }
    }
}
