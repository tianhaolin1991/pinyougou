package com.pinyougou.mapper;

import com.pinyougou.pojo.TbOrderItem;
import com.pinyougou.pojo.TbOrderItemExample;
import java.util.List;

import com.pinyougou.pojogroup.OrderGroup;
import org.apache.ibatis.annotations.Param;

public interface TbOrderItemMapper {
    int countByExample(TbOrderItemExample example);

    int deleteByExample(TbOrderItemExample example);

    int deleteByPrimaryKey(Long id);

    int insert(TbOrderItem record);

    int insertSelective(TbOrderItem record);

    List<TbOrderItem> selectByExample(TbOrderItemExample example);

    TbOrderItem selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("record") TbOrderItem record, @Param("example") TbOrderItemExample example);

    int updateByExample(@Param("record") TbOrderItem record, @Param("example") TbOrderItemExample example);

    int updateByPrimaryKeySelective(TbOrderItem record);

    int updateByPrimaryKey(TbOrderItem record);

    /**
     * 根据orderId查询OrderItemList
     * @param orderId
     * @return
     */
    List<TbOrderItem> findByOrderId(Long orderId);
}