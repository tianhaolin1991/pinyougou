package com.pinyougou.search.service.impl;


import com.alibaba.dubbo.config.annotation.Service;
import com.pinyougou.pojo.TbItem;
import com.pinyougou.search.service.ItemSearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.core.query.*;
import org.springframework.data.solr.core.query.result.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service(timeout = 5000)
public class ItemSearchServiceImpl implements ItemSearchService {

    @Autowired
    private SolrTemplate solrTemplate;
    @Autowired
    private RedisTemplate redisTemplate;

    @Override
    public Map<String, Object> search(Map<String, Object> searchMap) {
        HashMap<String, Object> resultMap = new HashMap<>();
        String keywords = (String)searchMap.get("keywords");
        String replace = keywords.replace(" ", "");
        searchMap.put("keywords",replace);

        if(keywords==null || "".equals(keywords)){
            Query query = new SimpleQuery("*:*");
            ScoredPage<TbItem> page = solrTemplate.queryForPage(query, TbItem.class);
            resultMap.put("content",page.getContent());
        }else{
            //根据searchMap查询
            resultMap.putAll(searchByKeywords(searchMap));
            //分组查询出该keywords对应的itemCategory
            List<String> categoryList = (List<String>)searchCategoryList(searchMap).get("categoryList");
            resultMap.putAll(searchCategoryList(searchMap));
            //从redis中查询brandList和specList
            String category = (String)searchMap.get("category");

            if(category!=null&&!"".equals(category)){
                Long categoryId = (Long)redisTemplate.boundHashOps("itemCat").get(category);
                resultMap.putAll(searchBrandAndSpecList(categoryId));
            }else{
                if(categoryList.size()>0){
                    Long categoryId = (Long)redisTemplate.boundHashOps("itemCat").get(categoryList.get(0));
                    resultMap.putAll(searchBrandAndSpecList(categoryId));
                }
            }
        }

        return resultMap;
    }

    private Map<String, Object> searchByKeywords(Map<String, Object> searchMap) {
        //1.获取关键字
        String keywords = (String) searchMap.get("keywords");
        //2.创建条件对象
        Criteria criteria = new Criteria("item_keywords").is(keywords);

        HighlightQuery query = new SimpleHighlightQuery();


        //3.创建高亮条件
        HighlightOptions highlightOptions = new HighlightOptions();
        highlightOptions.addField("item_title");
        highlightOptions.setSimplePrefix("<em style='color:red'>");
        highlightOptions.setSimplePostfix("</em>");

        query.addCriteria(criteria);
        query.setHighlightOptions(highlightOptions);

        //分页
        Integer currentPage =(Integer) searchMap.get("currentPage");
        Integer pageSize = (Integer) searchMap.get("pageSize");
        if(currentPage==null){
            currentPage = 1;
        }
        if(pageSize==null){
            pageSize = 30;
        }
        query.setOffset((currentPage-1)*pageSize);
        query.setRows(pageSize);

        //4.设置过滤条件
        //4.1分类
        if(searchMap.get("category")!=null&&!"".equals(searchMap.get("category"))){
            Criteria filterCriteria = new Criteria("item_category").is(searchMap.get("category"));
            FilterQuery filterQuery = new SimpleFilterQuery(filterCriteria);
            query.addFilterQuery(filterQuery);
        }
        //4.2.品牌
        if(searchMap.get("brand")!=null&&!"".equals(searchMap.get("brand"))){
            Criteria filterCriteria = new Criteria("item_brand").is(searchMap.get("brand"));
            FilterQuery filterQuery = new SimpleFilterQuery(filterCriteria);
            query.addFilterQuery(filterQuery);
        }
        //4.3规格
        if(searchMap.get("spec")!=null){
            Map<String, String> specMap = (Map)searchMap.get("spec");
            for (String key : specMap.keySet()) {
                Criteria filterCriteria = new Criteria("item_spec_" + key).is(specMap.get(key));
                FilterQuery filterQuery = new SimpleFilterQuery(filterCriteria);
                query.addFilterQuery(filterQuery);
            }
        }
        //4.4价格
        if(searchMap.get("price")!=null && !"".equals(searchMap.get("price"))){
            String[] prices = ((String)searchMap.get("price")).split("-");
            String lowPrice = prices[0];
            String highPrice = prices[1];

            Criteria lowCriteria = new Criteria("item_price").greaterThanEqual(lowPrice);
            FilterQuery lowFilterQuery = new SimpleFilterQuery(lowCriteria);
            query.addFilterQuery(lowFilterQuery);
            if(!"*".equals(highPrice)){
                Criteria highCriteria= new Criteria("item_price").lessThanEqual(highPrice);
                FilterQuery highFilterQuery = new SimpleFilterQuery(highCriteria);
                query.addFilterQuery(highFilterQuery);
            }
        }
        //4.5排序
        String sortType = (String)searchMap.get("sortType");
        String sortField = (String)searchMap.get("sortField");
        if(sortType!=null && !"".equals(sortType) && sortField!=null && !"".equals(sortField)){
            if(sortType.equals("ASC")){
                Sort sort=new Sort(Sort.Direction.ASC, "item_"+sortField);
                query.addSort(sort);
            }
            if(sortType.equals("DESC")){
                Sort sort=new Sort(Sort.Direction.DESC, "item_"+sortField);
                query.addSort(sort);
            }
        }

        //5.查询,获取高亮结果
        HighlightPage<TbItem> page = solrTemplate.queryForHighlightPage(query, TbItem.class);
        //6.获取高亮入口
        List<HighlightEntry<TbItem>> highlighted = page.getHighlighted();
        for (HighlightEntry<TbItem> highlightEntry : highlighted) {
            //获取高亮结果集
            List<HighlightEntry.Highlight> highlightList = highlightEntry.getHighlights();
            if(highlightList!=null && highlightList.get(0).getSnipplets()!=null){
                //获取高亮域
                HighlightEntry.Highlight highlightField = highlightList.get(0);
                //获取高亮结果
                String highlightContent = highlightField.getSnipplets().get(0);
                //将高亮结果放入content
                highlightEntry.getEntity().setTitle(highlightContent);
            }
        }
        //7.返回结果
        HashMap resultMap = new HashMap<String,Object>();
        resultMap.put("content",page.getContent());

        //将分页结果加入resultMap
        resultMap.put("totalPages",page.getTotalPages());
        resultMap.put("total",page.getTotalElements());
        return resultMap;

    }

    private Map<String, Object> searchCategoryList(Map<String, Object> searchMap) {
        //0.创建hashMap
        HashMap<String, Object> resultMap = new HashMap<>();
        ArrayList<String> list = new ArrayList<>();
        //1.获取关键字
        String keywords = (String)searchMap.get("keywords");
        //2.创建条件
        Criteria criteria = new Criteria("item_keywords").is(keywords);
        //3.配置分组条件
        GroupOptions groupOptions = new GroupOptions();
        groupOptions.addGroupByField("item_category");
        //4.创建Query对象
        Query query = new SimpleQuery(criteria);
        query.setGroupOptions(groupOptions);

        //5.获取分组页面
        GroupPage<TbItem> page = solrTemplate.queryForGroupPage(query, TbItem.class);
        //6.获取分组结果
        GroupResult<TbItem> groupResult = page.getGroupResult("item_category");
        //7.获取分组入口页
        Page<GroupEntry<TbItem>> groupEntryPage = groupResult.getGroupEntries();
        //8.获取分组入口
        List<GroupEntry<TbItem>> groupEntryList = groupEntryPage.getContent();
        for (GroupEntry<TbItem> entry : groupEntryList) {
            String category = entry.getGroupValue();
            list.add(category);
        }

        //9.返回结果
        resultMap.put("categoryList",list);
        return resultMap;

    }

    private Map<String,Object> searchBrandAndSpecList(Long categoryId){
        HashMap<String, Object> resultMap = new HashMap<>();
        //通过categoryId查询brandList
        List<Map> brandList = (List<Map>)redisTemplate.boundHashOps("brandList").get(categoryId);
        //通过categoryId查询specList
        List<Map> specList = (List<Map>)redisTemplate.boundHashOps("specList").get(categoryId);
        resultMap.put("brandList",brandList);
        resultMap.put("specList",specList);
        return resultMap;
    }

    @Override
    public void importList(List itemList){
        solrTemplate.saveBeans(itemList);
        solrTemplate.commit();
    }

    @Override
    public void deleteByGoodsIds(List goodsIds) {
        Criteria criteria = new Criteria("item_goodsid").in(goodsIds);
        Query query = new SimpleQuery(criteria);
        solrTemplate.delete(query);
        solrTemplate.commit();
    }
}
