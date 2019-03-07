package com.pinyougou.sellergoods.service.impl;



import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.pinyougou.mapper.TbBrandMapper;
import com.pinyougou.pojo.TbBrand;
import com.pinyougou.pojo.TbBrandExample;
import com.pinyougou.sellergoods.service.BrandService;
import entity.PageResult;
import org.springframework.beans.factory.annotation.Autowired;



import java.util.List;
import java.util.Map;

@Service
public class BrandServiceImpl implements BrandService {
    @Autowired
    private TbBrandMapper tbBrandMapper;

    @Override
    public List<TbBrand> findAll() {
        List<TbBrand> tbBrands = tbBrandMapper.selectByExample(null);
        return tbBrands;
    }

    @Override
    public PageResult findPage(Integer pageNum, Integer pageSize) {

        PageHelper.startPage(pageNum,pageSize);

        Page tbBrandsPages = (Page<TbBrand>)tbBrandMapper.selectByExample(null);
        return new PageResult(tbBrandsPages.getTotal(),tbBrandsPages.getResult());
    }

    @Override
    public void add(TbBrand tbBrand) {
        tbBrandMapper.insert(tbBrand);
    }

    @Override
    public void delete(long[] ids) {
        for (long id : ids) {
            tbBrandMapper.deleteByPrimaryKey(id);
        }
    }

    @Override
    public void update(TbBrand tbBrand) {
        tbBrandMapper.updateByPrimaryKey(tbBrand);
    }

    @Override
    public TbBrand findOne(long id) {
        return tbBrandMapper.selectByPrimaryKey(id);
    }

    @Override
    public PageResult findPage(TbBrand tbBrand, Integer pageNum, Integer pageSize) {
        TbBrandExample tbBrandExample = new TbBrandExample();
        TbBrandExample.Criteria criteria = tbBrandExample.createCriteria();
        if(tbBrand!=null){
            if(tbBrand.getName()!=null && tbBrand.getName().length()!=0){
                criteria.andNameLike("%"+tbBrand.getName()+"%");
            }
            if(tbBrand.getFirstChar()!=null&& tbBrand.getFirstChar().length()!=0){
                criteria.andFirstCharLike("%"+tbBrand.getFirstChar()+"%");
            }
        }
        PageHelper.startPage(pageNum,pageSize);
        Page tbBrandsPage = (Page<TbBrand>)tbBrandMapper.selectByExample(tbBrandExample);
        PageResult pageResult  = new PageResult(tbBrandsPage.getTotal(),tbBrandsPage.getResult());
        return pageResult;
    }

    @Override
    public List<Map> findOptionList() {
       return tbBrandMapper.selectOptionList();
    }

}
