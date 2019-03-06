package com.pinyougou.sellergoods.service.impl;



import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.pinyougou.mapper.TbBrandMapper;
import com.pinyougou.pojo.TbBrand;
import com.pinyougou.pojo.TbBrandExample;
import com.pinyougou.sellergoods.service.BrandService;
import org.springframework.beans.factory.annotation.Autowired;



import java.util.List;

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
    public PageInfo findPage(Integer pageNum, Integer pageSize) {

        PageHelper.startPage(pageNum,pageSize);
        List<TbBrand> tbBrands = tbBrandMapper.selectByExample(null);
        PageInfo pageInfo = new PageInfo<>(tbBrands);
        return pageInfo;
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
    public PageInfo findPage(TbBrand tbBrand, Integer pageNum, Integer pageSize) {
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
        List<TbBrand> tbBrands = tbBrandMapper.selectByExample(tbBrandExample);
        PageInfo tbBrandPageInfo = new PageInfo<>(tbBrands);
        return tbBrandPageInfo;
    }

}
