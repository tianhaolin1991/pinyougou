package com.pinyougou.search.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.search.service.ItemSearchService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Map;

@Controller
@RequestMapping("/search")
public class SearchController {

    @Reference
    private ItemSearchService itemSearchService;

    @RequestMapping("/search.do")
    @ResponseBody
    public Map<String,Object> search(@RequestBody Map<String, String> searchMap){
        return itemSearchService.search(searchMap);
    }
}
