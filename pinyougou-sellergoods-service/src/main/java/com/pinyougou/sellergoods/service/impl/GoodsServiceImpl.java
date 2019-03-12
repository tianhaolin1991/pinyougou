package com.pinyougou.sellergoods.service.impl;
import java.util.Date;
import java.util.List;
import java.util.Map;


import com.pinyougou.mapper.*;
import com.pinyougou.pojo.*;
import com.pinyougou.pojogroup.GoodsGroup;
import org.springframework.beans.factory.annotation.Autowired;
import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.pinyougou.pojo.TbGoodsExample.Criteria;
import com.pinyougou.sellergoods.service.GoodsService;
import com.alibaba.fastjson.JSON;

import entity.PageResult;

/**
 * 服务实现层
 * @author Administrator
 *
 */
@Service
public class GoodsServiceImpl implements GoodsService {

	@Autowired
	private TbGoodsMapper goodsMapper;

	@Autowired
	private TbGoodsDescMapper goodsDescMapper;

	@Autowired
	private TbSellerMapper sellerMapper;

	@Autowired
	private TbBrandMapper tbBrandMapper;

	@Autowired
	private TbItemMapper itemMapper;
	/**
	 * 查询全部
	 */
	@Override
	public List<TbGoods> findAll() {
		TbGoodsExample example = new TbGoodsExample();
		Criteria criteria = example.createCriteria();
		criteria.andIsDeleteIsNull();
		return goodsMapper.selectByExample(null);
	}

	/**
	 * 按分页查询
	 */
	@Override
	public PageResult findPage(int pageNum, int pageSize) {

		TbGoodsExample example = new TbGoodsExample();
		Criteria criteria = example.createCriteria();
		criteria.andIsDeleteIsNull();
		PageHelper.startPage(pageNum, pageSize);
		Page<TbGoods> page=   (Page<TbGoods>) goodsMapper.selectByExample(example);
		return new PageResult(page.getTotal(), page.getResult());
	}

	/**
	 * 增加
	 */
	@Override
	public void add(GoodsGroup goodsGroup) {
		TbGoods tbGoods = goodsGroup.getGoods();
		//将状态变为未审核
		tbGoods.setAuditStatus("0");
		goodsMapper.insert(tbGoods);
		//从商品基本表获取goodsId
		Long goodsId = tbGoods.getId();
		TbGoodsDesc tbGoodsDesc = goodsGroup.getGoodsDesc();
		tbGoodsDesc.setGoodsId(goodsId);
		goodsDescMapper.insert(tbGoodsDesc);

		//添加itemList
		addItemList(goodsGroup);
	}

	private void addItemList(GoodsGroup goodsGroup){
		TbGoods tbGoods = goodsGroup.getGoods();
		TbGoodsDesc tbGoodsDesc =goodsGroup.getGoodsDesc();
		//设置tbItems
		List<TbItem> items = goodsGroup.getItemList();
		//获取SPU
		String spu = tbGoods.getGoodsName();
		//获取第一张图片
		String itemImages = tbGoodsDesc.getItemImages();
		List<Map> imgMaps = JSON.parseArray(itemImages, Map.class);
		String url = "{}";
		if(imgMaps.size()>0){
			url = (String)imgMaps.get(0).get("url");
		}
		//创建时间与update时间
		Date createTime = new Date();
		//获取商家名称
		TbSeller tbSeller = sellerMapper.selectByPrimaryKey(tbGoods.getSellerId());
		String nickName = tbSeller.getNickName();
		//获取品牌名称
		String brand = tbBrandMapper.selectByPrimaryKey(tbGoods.getBrandId()).getName();
		if("1".equals(tbGoods.getIsEnableSpec())){
			for (TbItem item : items) {
				//设置title
				String title = spu;
				String spec = item.getSpec();
				Map<String,Object> specMap =  JSON.parseObject(spec);
				for (String specKey : specMap.keySet()) {
					title += " " + specMap.get(specKey);
				}
				//设置title
				item.setTitle(title);
				//设置图片
				item.setImage(url);
				//设置分类id
				item.setCategoryid(tbGoods.getCategory3Id());
				//设置createTime,updateTime
				item.setCreateTime(createTime);
				item.setUpdateTime(createTime);
				//设置goodsId
				item.setGoodsId(tbGoods.getId());
				//设置seller
				item.setSeller(nickName);
				//设置brand
				item.setBrand(brand);
				item.setImage(itemImages);
				item.setSellerId(tbGoods.getSellerId());

				itemMapper.insert(item);
			}
		}else{
			TbItem item = new TbItem();
			//设置title
			item.setTitle(spu);
			//设置图片
			item.setImage(url);
			item.setCategoryid(tbGoods.getCategory3Id());
			//设置createTime,updateTime
			item.setCreateTime(createTime);
			item.setUpdateTime(createTime);
			//设置goodsId
			item.setGoodsId(tbGoods.getId());
			//设置seller
			item.setSeller(nickName);
			//设置brand
			item.setBrand(brand);
			//设置status
			item.setStatus("1");
			//设置是否默认
			item.setIsDefault("1");
			//设置库存
			item.setNum(99999);
			//设置spec
			item.setSpec("{}");
			item.setImage(itemImages);
			item.setSellerId(tbGoods.getSellerId());
			itemMapper.insert(item);
		}
	}
	
	/**
	 * 修改
	 */
	@Override
	public void update(GoodsGroup goodsGroup){
		TbGoods goods = goodsGroup.getGoods();
		goodsMapper.updateByPrimaryKey(goods);
		TbGoodsDesc goodsDesc = goodsGroup.getGoodsDesc();
		goodsDescMapper.updateByPrimaryKey(goodsDesc);
		Long goodsId = goods.getId();
		List<TbItem> itemList = goodsGroup.getItemList();
		//删除itemMapper
		TbItemExample example = new TbItemExample();
		TbItemExample.Criteria criteria = example.createCriteria();
		criteria.andGoodsIdEqualTo(goodsId);
		itemMapper.deleteByExample(example);
		//添加itemMapper
		addItemList(goodsGroup);
	}



	/**
	 * 批量删除
	 */
	@Override
	public void delete(Long[] ids) {
		for(Long id:ids){
			TbGoods goods = goodsMapper.selectByPrimaryKey(id);
			goods.setIsDelete("1");
			goodsMapper.updateByPrimaryKey(goods);
		}		
	}
	
	
		@Override
	public PageResult findPage(TbGoods goods, int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);
		
		TbGoodsExample example=new TbGoodsExample();
		Criteria criteria = example.createCriteria();
		criteria.andIsDeleteIsNull();
		
		if(goods!=null){			
			if(goods.getSellerId()!=null && goods.getSellerId().length()>0){
				criteria.andSellerIdEqualTo(goods.getSellerId());
			}
			if(goods.getGoodsName()!=null && goods.getGoodsName().length()>0){
				criteria.andGoodsNameLike("%"+goods.getGoodsName()+"%");
			}
			if(goods.getAuditStatus()!=null && goods.getAuditStatus().length()>0){
				criteria.andAuditStatusLike("%"+goods.getAuditStatus()+"%");
			}
			if(goods.getIsMarketable()!=null && goods.getIsMarketable().length()>0){
				criteria.andIsMarketableLike("%"+goods.getIsMarketable()+"%");
			}
			if(goods.getCaption()!=null && goods.getCaption().length()>0){
				criteria.andCaptionLike("%"+goods.getCaption()+"%");
			}
			if(goods.getSmallPic()!=null && goods.getSmallPic().length()>0){
				criteria.andSmallPicLike("%"+goods.getSmallPic()+"%");
			}
			if(goods.getIsEnableSpec()!=null && goods.getIsEnableSpec().length()>0){
				criteria.andIsEnableSpecLike("%"+goods.getIsEnableSpec()+"%");
			}
			if(goods.getIsDelete()!=null && goods.getIsDelete().length()>0){
				criteria.andIsDeleteLike("%"+goods.getIsDelete()+"%");
			}
	
		}
		
		Page<TbGoods> page= (Page<TbGoods>)goodsMapper.selectByExample(example);		
		return new PageResult(page.getTotal(), page.getResult());
	}

	@Override
	public GoodsGroup findOne(Long id) {
		GoodsGroup goodsGroup = new GoodsGroup();
		//查询goods
		TbGoods goods = goodsMapper.selectByPrimaryKey(id);
		goodsGroup.setGoods(goods);
		if(goods.getIsDelete()==null){
			//获取goodsDesc
			TbGoodsDesc goodsDesc = goodsDescMapper.selectByPrimaryKey(id);
			goodsGroup.setGoodsDesc(goodsDesc);
			//获取itemList
			TbItemExample example = new TbItemExample();
			TbItemExample.Criteria criteria = example.createCriteria();
			criteria.andGoodsIdEqualTo(id);
			List<TbItem> itemList = itemMapper.selectByExample(example);
			goodsGroup.setItemList(itemList);
			return goodsGroup;
		}else{
			return null;
		}
	}

	@Override
	public void updateStatus(Long[] ids, String status) {
		for (Long id : ids) {
			TbGoods tbGoods = goodsMapper.selectByPrimaryKey(id);
			tbGoods.setAuditStatus(status);
			goodsMapper.updateByPrimaryKey(tbGoods);
		}
	}

}
