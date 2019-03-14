package com.pinyougou.protal.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.content.service.ContentService;
import com.pinyougou.pojo.TbContent;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
@RequestMapping("/content")
public class ContentController {
    @Reference
    private ContentService contentService;

    @RequestMapping("/findByCategoryId")
    @ResponseBody
    public List<TbContent> findByCategoryId(Long categoryId){
        return contentService.findByCategoryId(categoryId);
    }
}
