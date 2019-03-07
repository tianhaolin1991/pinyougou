package com.pinyougou.sellergoods.service;

import com.pinyougou.pojo.TbBrand;
import entity.PageResult;

import java.util.List;
import java.util.Map;

public interface BrandService {

    /**
     * 查询所有
     * @return
     */
    List<TbBrand> findAll();

    /**
     * 分页查询
     * @return
     */
    PageResult findPage(Integer pageNum, Integer pageSize);

    /**
     * 新增品牌
     * @param tbBrand
     */
    void add(TbBrand tbBrand);

    /**
     * 批量删除
     * @param ids
     */
    void delete(long[] ids);

    /**
     * 修改品牌信息
     * @param tbBrand
     */
    void update(TbBrand tbBrand);

    /**
     * 根据id查询
     * @param id
     * @return
     */
    TbBrand findOne(long id);

    /**
     * 模糊查询
     * @param tbBrand
     * @param pageNum
     * @param pageSize
     * @return
     */
    PageResult findPage(TbBrand tbBrand, Integer pageNum, Integer pageSize);


    /**
     * TypeTemplate商品类型模板编辑选项
     * @return
     */
    List<Map> findOptionList();
}
