package com.pinyougou.user.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.pojo.TbAreas;
import com.pinyougou.pojo.TbCities;
import com.pinyougou.pojo.TbProvinces;
import com.pinyougou.user.service.AreaService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/area")
public class AreaController {

    @Reference
    private AreaService areaService;

    @RequestMapping("/findProvinceList")
    public List<TbProvinces> findProvinceLis(){
        List<TbProvinces> provinceList = areaService.findProvinceList();
        return provinceList;
    }

    @RequestMapping("/findCityListByProvinceId")
    public List<TbCities> findCityListByProvinceId(String provinceId){
        List<TbCities> cityList = areaService.findCityListByProvinceId(provinceId);
        return cityList;
    }

    @RequestMapping("/findAreaListByCityId")
    public List<TbAreas> findAreaListByCityId(String cityId){
        List<TbAreas> areaList = areaService.findAreaListByCityId(cityId);
        return areaList;
    }

}
