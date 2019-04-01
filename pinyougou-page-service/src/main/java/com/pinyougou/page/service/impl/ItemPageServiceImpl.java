package com.pinyougou.page.service.impl;

import com.pinyougou.mapper.TbGoodsDescMapper;
import com.pinyougou.mapper.TbGoodsMapper;
import com.pinyougou.mapper.TbItemCatMapper;
import com.pinyougou.mapper.TbItemMapper;
import com.pinyougou.page.service.ItemPageService;
import com.pinyougou.pojo.*;
import freemarker.template.Configuration;
import freemarker.template.Template;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfig;

import java.io.*;
import java.util.HashMap;
import java.util.List;

@Service
public class ItemPageServiceImpl implements ItemPageService {

    @Value("${outputPath}")
    private String outputPath;
    @Autowired
    private FreeMarkerConfig freeMarkerConfig;

    @Autowired
    private TbGoodsMapper goodsMapper;
    @Autowired
    private TbGoodsDescMapper goodsDescMapper;
    @Autowired
    private TbItemCatMapper itemCatMapper;
    @Autowired
    private TbItemMapper itemMapper;

    @Override
    public boolean genItemPage(Long[] goodsIds) {
        try {
            for (Long goodsId : goodsIds) {
                System.out.println("进入方法");

                Configuration configuration = freeMarkerConfig.getConfiguration();
                Template template = configuration.getTemplate("itemPageTemplate.html");
                HashMap<String, Object> map = new HashMap<>();

                TbGoods goods = goodsMapper.selectByPrimaryKey(goodsId);
                TbGoodsDesc goodsDesc = goodsDescMapper.selectByPrimaryKey(goodsId);
                String category1 = itemCatMapper.selectByPrimaryKey(goods.getCategory1Id()).getName();
                String category2 = itemCatMapper.selectByPrimaryKey(goods.getCategory2Id()).getName();
                String category3 = itemCatMapper.selectByPrimaryKey(goods.getCategory3Id()).getName();

                TbItemExample example = new TbItemExample();
                TbItemExample.Criteria criteria = example.createCriteria();
                criteria.andGoodsIdEqualTo(goodsId);
                criteria.andStatusEqualTo("1");
                example.setOrderByClause("is_default desc");
                List<TbItem> itemList = itemMapper.selectByExample(example);

                map.put("goods", goods);
                map.put("goodsDesc", goodsDesc);
                map.put("category1", category1);
                map.put("category2", category2);
                map.put("category3", category3);
                map.put("itemList", itemList);

                FileOutputStream fos = new FileOutputStream(new File(outputPath + goodsId + ".html"));
                OutputStreamWriter out = new OutputStreamWriter(fos, "UTF-8");
                template.process(map, out);
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

    }

    @Override
    public void deleteItemPage(Long[] goodsIds) {
        for (Long goodsId : goodsIds) {
            File file = new File(outputPath + goodsId + ".html");
            file.delete();
        }
    }
}
