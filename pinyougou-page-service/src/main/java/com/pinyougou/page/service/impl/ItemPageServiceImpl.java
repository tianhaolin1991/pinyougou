package com.pinyougou.page.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.pinyougou.mapper.TbGoodsDescMapper;
import com.pinyougou.mapper.TbGoodsMapper;
import com.pinyougou.mapper.TbItemCatMapper;
import com.pinyougou.mapper.TbItemMapper;
import com.pinyougou.page.service.ItemPageService;
import com.pinyougou.pojo.*;
import freemarker.template.Configuration;
import freemarker.template.Template;
import org.apache.commons.io.output.FileWriterWithEncoding;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfig;

import java.io.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    private TbItemMapper itemMapper;
    @Autowired
    private TbItemCatMapper itemCatMapper;


    @Override
    public boolean genItemPage(Long goodsId) {
        System.out.println("进入方法");
        try {
            //配置
            Configuration configuration = freeMarkerConfig.getConfiguration();
            configuration.setDefaultEncoding("UTF-8");
            Template template = configuration.getTemplate("itemPageTemplate.html");
            //设置数据
            Map map = new HashMap();

            TbGoods goods = goodsMapper.selectByPrimaryKey(goodsId);
            TbGoodsDesc goodsDesc = goodsDescMapper.selectByPrimaryKey(goodsId);
            //查找分类
            Long category1Id = goods.getCategory1Id();
            Long category2Id = goods.getCategory2Id();
            Long category3Id = goods.getCategory3Id();
            String category1Name = itemCatMapper.selectByPrimaryKey(category1Id).getName();
            String category2Name = itemCatMapper.selectByPrimaryKey(category2Id).getName();
            String category3Name = itemCatMapper.selectByPrimaryKey(category3Id).getName();

            TbItemExample example = new TbItemExample();
            TbItemExample.Criteria criteria = example.createCriteria();
            criteria.andGoodsIdEqualTo(goodsId);
            example.setOrderByClause("is_default desc");
            List<TbItem> itemList = itemMapper.selectByExample(example);


            map.put("goods",goods);
            map.put("goodsDesc",goodsDesc);
            map.put("category1Name",category1Name);
            map.put("category2Name",category2Name);
            map.put("category3Name",category3Name);
            map.put("itemList",itemList);

            //输出
            OutputStreamWriter out = new OutputStreamWriter(new FileOutputStream(outputPath+goodsId+".html"),"utf-8");
            //Writer out = new FileWriterWithEncoding(outputPath+goodsId+".html","utf-8");
            template.process(map,out);
            out.close();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

    }
}
