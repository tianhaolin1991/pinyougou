package com.pinyougou.sellergoods.service;
import java.util.List;
import com.pinyougou.pojo.TbGoods;

import com.pinyougou.pojo.TbItemCat;
import com.pinyougou.pojogroup.GoodsGroup;
import entity.PageResult;
/**
 * 服务层接口
 * @author Administrator
 *
 */
public interface GoodsService {

	/**
	 * 返回全部列表
	 * @return
	 */
	public List<TbGoods> findAll();
	
	
	/**
	 * 返回分页列表
	 * @return
	 */
	public PageResult findPage(int pageNum, int pageSize);
	
	
	/**
	 * 增加
	*/
	public void add(GoodsGroup goodsGroup);
	
	
	/**
	 * 修改
	 */
	public void update(GoodsGroup goodsGroup);

	
	
	/**
	 * 批量删除(修改状态)
	 * @param ids
	 */
	public void delete(Long[] ids);

	/**
	 * 分页
	 * @param pageNum 当前页 码
	 * @param pageSize 每页记录数
	 * @return
	 */
	public PageResult findPage(TbGoods goods, int pageNum, int pageSize);

	/**
	 * 根据商品的id来获取商品的全部信息
	 * @return
	 */
	GoodsGroup findOne(Long id);

	/**
	 * 修改商品的status,1为审核通过,2为驳回
	 * @param ids
	 * @param status
	 */
	void updateStatus(Long[] ids, String status);
}
