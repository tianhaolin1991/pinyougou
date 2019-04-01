package com.pinyougou.search.service.impl;

import com.alibaba.fastjson.JSON;
import com.pinyougou.pojo.TbItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.jms.*;
import java.util.Arrays;
import java.util.List;

@Component
public class DeleteItemListMessageListener implements MessageListener {

    @Autowired
    private ItemSearchServiceImpl itemSearchService;

    @Override
    public void onMessage(Message message) {
        ObjectMessage itemMessage = (ObjectMessage) message;
        try {
            Long[] ids = (Long[])itemMessage.getObject();
            List<Long> idList = Arrays.asList(ids);
            itemSearchService.deleteByGoodsIds(idList);
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }
}
