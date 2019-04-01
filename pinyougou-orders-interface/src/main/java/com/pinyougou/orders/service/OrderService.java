package com.pinyougou.orders.service;
import java.util.List;
import com.pinyougou.pojo.TbOrder;

import entity.PageResult;
/**
 * 服务层接口
 * @author Administrator
 *
 */
public interface OrderService {

	/**
	 * 返回全部列表
	 * @return
	 */
	public List<TbOrder> findAll();
	
	
	/**
	 * 返回分页列表
	 * @return
	 */
	public PageResult findPage(int pageNum, int pageSize);
	
	
	/**
	 * 增加
	*/
	public void add(TbOrder order);
	
	
	/**
	 * 修改
	 */
	public void update(TbOrder order);
	

	/**
	 * 根据ID获取实体
	 * @param id
	 * @return
	 */
	public TbOrder findOne(String id);
	
	
	/**
	 * 批量删除
	 * @param ids
	 */
	public void delete(String[] ids);

	/**
	 * 分页
	 * @param pageNum 当前页 码
	 * @param pageSize 每页记录数
	 * @return
	 */
	public PageResult findPage(TbOrder order, int pageNum, int pageSize);


	/**
	 * 用户支付后修改订单状态
	 * @param out_trade_no
	 * @param transaction_id
	 */
	void updateOrderStatus(String out_trade_no,String transaction_id);


	/**
	 * 通过userId和status查找orderList,分页
	 * @param userId
	 * @param status
	 * @param pageNum
	 * @param pageSize
	 * @return
	 */
	PageResult findOrderListByUserIdAndStatus(String userId,String status,Integer pageNum,Integer pageSize);


	/**
	 * 用户从用户中心支付,添加payLog
	 * @param orderId
	 */
	String addPayLogFromUserOrderCenter(String orderId);

	/**
	 * 确认收货
	 * @param orderId
	 */
	void transactionConfirm(String orderId);

	/**
	 * 取消收货
	 * @param orderId
	 */
	void transactionCancel(String orderId);
}
