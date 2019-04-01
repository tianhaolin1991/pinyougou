package com.pinyougou.search.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.pinyougou.pojo.TbItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;
import java.util.List;

@Component("importItemListMessageListener")
public class ImportItemListMessageListener implements MessageListener {

    @Autowired
    private ItemSearchServiceImpl itemSearchService;

    @Override
    public void onMessage(Message message) {
        TextMessage itemMessage = (TextMessage) message;
        try {
            List<TbItem> tbItems = JSON.parseArray(itemMessage.getText(), TbItem.class);
            itemSearchService.importList(tbItems);
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }
}
