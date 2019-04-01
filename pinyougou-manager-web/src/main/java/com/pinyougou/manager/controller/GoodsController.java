package com.pinyougou.manager.controller;
import java.util.List;

import com.alibaba.fastjson.JSON;
import com.pinyougou.pojo.TbItem;
import com.pinyougou.pojogroup.GoodsGroup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.pojo.TbGoods;
import com.pinyougou.sellergoods.service.GoodsService;

import entity.PageResult;
import entity.Result;

import javax.jms.*;


/**
 * controller
 * @author Administrator
 *
 */
@RestController
@RequestMapping("/goods")
public class GoodsController {

	@Reference
	private GoodsService goodsService;

	@Autowired
	private JmsTemplate jmsTemplate;

	@Autowired
    @Qualifier("importItemListDestination")
	private Destination importItemListDestination;

	@Autowired
    @Qualifier("deleteItemListDestination")
	private Destination deleteItemListDestination;

	@Autowired
    @Qualifier("genHtmlTopicDestination")
    private Destination genHtmlTopicDestination;

	@Autowired
    @Qualifier("deleteHtmlTopicDestination")
    private Destination deleteHtmlTopicDestination;
	
	/**
	 * 返回全部列表
	 * @return
	 */
	@RequestMapping("/findAll")
	public List<TbGoods> findAll(){			
		return goodsService.findAll();
	}
	
	
	/**
	 * 返回全部列表
	 * @return
	 */
	@RequestMapping("/findPage")
	public PageResult  findPage(Integer pageNum,Integer pageSize){			
		return goodsService.findPage(pageNum,pageSize);
	}

	
	/**
	 * 修改
	 * @param goods
	 * @return
	 */
	@RequestMapping("/update")
	public Result update(@RequestBody GoodsGroup goods){
		try {
			goodsService.update(goods);
			return new Result(true, "修改成功");
		} catch (Exception e) {
			e.printStackTrace();
			return new Result(false, "修改失败");
		}
	}

	@RequestMapping("/updateStatus")
	public Result checked(Long[] ids,String status){
		try {
			goodsService.updateStatus(ids,status);
			if("1".equals(status)){
				List<TbItem> itemList = goodsService.findItemListByGoodsIdListAndStatus(ids, status);

				//searchService.importList(itemList);
                importListToSolr(itemList);
				//生成页面
				genItemPage(ids);
			}
			return new Result(true, "执行成功");
		} catch (Exception e) {
			e.printStackTrace();
			return new Result(false, "执行失败");
		}
	}

	/**
	 * 获取实体
	 * @param id
	 * @return
	 */
	@RequestMapping("/findOne")
	public GoodsGroup findOne(Long id){
		return goodsService.findOne(id);		
	}
	
	/**
	 * 批量删除
	 * @param ids
	 * @return
	 */
	@RequestMapping("/delete")
	public Result delete(Long [] ids){
		try {
			goodsService.delete(ids);
			//searchService.deleteByGoodsIds(Arrays.asList(ids));
            deleteByGoodsIdsFromSolr(ids);
            deleteItemPage(ids);
			return new Result(true, "删除成功"); 
		} catch (Exception e) {
			e.printStackTrace();
			return new Result(false, "删除失败");
		}
	}
	
		/**
	 * 查询+分页
	 * @param
	 * @param page
	 * @param rows
	 * @return
	 */
	@RequestMapping("/search")
	public PageResult search(@RequestBody TbGoods goods, int page, int rows  ){
		return goodsService.findPage(goods, page, rows);		
	}


	private void importListToSolr(List<TbItem> itemList){

        String items= JSON.toJSONString(itemList);

        jmsTemplate.send(importItemListDestination, new MessageCreator() {
            @Override
            public Message createMessage(Session session) throws JMSException {
                return  session.createTextMessage(items);
            }
        });
    }

    private void deleteByGoodsIdsFromSolr(Long[] ids){
        jmsTemplate.send(deleteItemListDestination, new MessageCreator() {
            @Override
            public Message createMessage(Session session) throws JMSException {
                return session.createObjectMessage(ids);
            }
        });
    }

    private void genItemPage(Long[] goodsId){

        jmsTemplate.send(genHtmlTopicDestination, new MessageCreator() {
            @Override
            public Message createMessage(Session session) throws JMSException {
                return session.createObjectMessage(goodsId);
            }
        });

    }

    private void deleteItemPage(Long[] goodsId){

        jmsTemplate.send(deleteHtmlTopicDestination, new MessageCreator() {
            @Override
            public Message createMessage(Session session) throws JMSException {
                return session.createObjectMessage(goodsId);
            }
        });

    }
}
