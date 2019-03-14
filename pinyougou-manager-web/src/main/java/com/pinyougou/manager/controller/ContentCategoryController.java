package com.pinyougou.manager.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.content.service.ContentCategoryService;
import com.pinyougou.pojo.TbContentCategory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/contentCategory")
public class ContentCategoryController {

    @Reference
    private ContentCategoryService contentCategoryService;

    @RequestMapping("/findAll")
    public List<TbContentCategory> findAll(){
        List<TbContentCategory> contentCategory = contentCategoryService.findAll();
        return contentCategory;
    }
}
