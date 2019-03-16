package com.pinyougou.search.service;

import java.util.List;
import java.util.Map;

public interface ItemSearchService {

    /**
     * 根据搜索条件在solr中查询数据
     */
    Map<String,Object> search(Map<String,Object> searchMap);

    /**
     * 导入数据
     * @param list
     */
    void importList(List list);

    /**
     * 根据商品的id批量从solr索引库中删除
     * @param goodsIds
     */
    void deleteByGoodsIds(List goodsIds);
}
