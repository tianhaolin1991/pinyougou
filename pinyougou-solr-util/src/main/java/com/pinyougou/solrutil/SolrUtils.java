package com.pinyougou.solrutil;

import com.alibaba.fastjson.JSON;
import com.pinyougou.mapper.TbItemMapper;
import com.pinyougou.pojo.TbItem;
import com.pinyougou.pojo.TbItemExample;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public class SolrUtils {

    @Autowired
    private TbItemMapper itemMapper;
    @Autowired
    private SolrTemplate solrTemplate;

    private void importItemData(){
        TbItemExample example = new TbItemExample();
        TbItemExample.Criteria criteria = example.createCriteria();
        criteria.andStatusEqualTo("1");

        List<TbItem> tbItems = itemMapper.selectByExample(example);

        for (TbItem tbItem : tbItems) {
            Map<String,String> specMap = JSON.parseObject(tbItem.getSpec(), Map.class);
            tbItem.setSpecMap(specMap);
        }
        solrTemplate.saveBeans(tbItems);
        solrTemplate.commit();

    }


    public static void main(String[] args){
        ApplicationContext ac = new ClassPathXmlApplicationContext("classpath*:spring/applicationContext*.xml");
        SolrUtils solarUtils = ac.getBean("solrUtils",SolrUtils.class);
        solarUtils.importItemData();
    }
}
