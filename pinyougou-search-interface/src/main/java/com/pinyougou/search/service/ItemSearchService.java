package com.pinyougou.search.service;

import java.util.Map;

public interface ItemSearchService {

    /**
     * 根据搜索条件在solr中查询数据
     */
    Map<String,Object> search(Map<String,String> searchMap);
}
