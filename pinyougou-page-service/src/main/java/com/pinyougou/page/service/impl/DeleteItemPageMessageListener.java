package com.pinyougou.page.service.impl;

import com.pinyougou.page.service.ItemPageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;

@Component("deleteItemPageMessageListener")
public class DeleteItemPageMessageListener implements MessageListener {
    @Autowired
    private ItemPageService itemPageService;
    @Override
    public void onMessage(Message message) {
        try {
            ObjectMessage objectMessage = (ObjectMessage) message;
            Long[] goodsIds = (Long[])objectMessage.getObject();
            itemPageService.deleteItemPage(goodsIds);
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }
}
