package com.pinyougou.user.service;

import com.pinyougou.pojo.TbAreas;
import com.pinyougou.pojo.TbCities;
import com.pinyougou.pojo.TbProvinces;

import java.util.List;

public interface AreaService {

    /**
     * 找到provinceList
     * @return
     */
    List<TbProvinces> findProvinceList();

    /**
     * 根据provinceId查找cityList
     * @param provinceId
     * @return
     */
    List<TbCities> findCityListByProvinceId(String provinceId);

    /**
     * 根据cityId查找areaList
     * @param cityId
     * @return
     */
    List<TbAreas> findAreaListByCityId(String cityId);
}
