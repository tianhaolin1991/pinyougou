package com.pinyougou.search.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.pinyougou.pojo.TbItem;
import com.pinyougou.search.service.ItemSearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.core.query.Criteria;
import org.springframework.data.solr.core.query.Query;
import org.springframework.data.solr.core.query.SimpleQuery;
import org.springframework.data.solr.core.query.result.ScoredPage;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service(timeout = 5000)
public class ItemSearchServiceImpl implements ItemSearchService {

    @Autowired
    private SolrTemplate solrTemplate;

    @Override
    public Map<String, Object> search(Map<String, String> searchMap) {

        //1.接收搜索关键词
        String keywords = searchMap.get("keywords");
        //2.根据keywords配置query对象
        Query query = new SimpleQuery("*:*");
        Criteria criteria = new Criteria("item_keywords");
        criteria.is(keywords);
        query.addCriteria(criteria);
        //3.创建搜索
        ScoredPage<TbItem> page = solrTemplate.queryForPage(query, TbItem.class);
        List<TbItem> itemList = page.getContent();
        //4.将搜索结果放入结果集
        HashMap<String, Object> result = new HashMap<>();
        result.put("list", itemList);
        return result;
    }
}
