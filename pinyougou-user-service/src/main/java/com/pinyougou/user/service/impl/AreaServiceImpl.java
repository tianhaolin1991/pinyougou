package com.pinyougou.user.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.pinyougou.mapper.TbAreasMapper;
import com.pinyougou.mapper.TbCitiesMapper;
import com.pinyougou.mapper.TbProvincesMapper;
import com.pinyougou.pojo.*;
import com.pinyougou.user.service.AreaService;
import org.springframework.beans.factory.annotation.Autowired;

import java.awt.geom.Area;
import java.util.List;

@Service
public class AreaServiceImpl implements AreaService {
    @Autowired
    private TbProvincesMapper provincesMapper;
    @Autowired
    private TbCitiesMapper citiesMapper;
    @Autowired
    private TbAreasMapper areasMapper;
    @Override
    public List<TbProvinces> findProvinceList() {
        return provincesMapper.selectByExample(null);
    }

    @Override
    public List<TbCities> findCityListByProvinceId(String provinceId) {
        TbCitiesExample example = new TbCitiesExample();
        TbCitiesExample.Criteria criteria = example.createCriteria();
        criteria.andProvinceidEqualTo(provinceId);
        return citiesMapper.selectByExample(example);

    }

    @Override
    public List<TbAreas> findAreaListByCityId(String cityId) {
        TbAreasExample example = new TbAreasExample();
        TbAreasExample.Criteria criteria = example.createCriteria();
        criteria.andCityidEqualTo(cityId);
        return areasMapper.selectByExample(example);
    }
}
