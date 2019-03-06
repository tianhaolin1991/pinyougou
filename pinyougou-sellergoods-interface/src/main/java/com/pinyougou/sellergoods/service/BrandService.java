package com.pinyougou.sellergoods.service;

import com.github.pagehelper.PageInfo;
import com.pinyougou.pojo.TbBrand;

import java.util.List;

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
    PageInfo findPage(Integer pageNum, Integer pageSize);

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
    PageInfo findPage(TbBrand tbBrand, Integer pageNum, Integer pageSize);
}
