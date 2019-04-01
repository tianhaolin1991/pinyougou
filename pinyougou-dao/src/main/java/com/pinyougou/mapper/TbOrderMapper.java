package com.pinyougou.mapper;

import com.pinyougou.pojo.TbOrder;
import com.pinyougou.pojo.TbOrderExample;
import java.util.List;

import com.pinyougou.pojogroup.OrderGroup;
import org.apache.ibatis.annotations.Param;

public interface TbOrderMapper {
    int countByExample(TbOrderExample example);

    int deleteByExample(TbOrderExample example);

    int deleteByPrimaryKey(String orderId);

    int insert(TbOrder record);

    int insertSelective(TbOrder record);

    List<TbOrder> selectByExample(TbOrderExample example);

    TbOrder selectByPrimaryKey(String orderId);

    int updateByExampleSelective(@Param("record") TbOrder record, @Param("example") TbOrderExample example);

    int updateByExample(@Param("record") TbOrder record, @Param("example") TbOrderExample example);

    int updateByPrimaryKeySelective(TbOrder record);

    int updateByPrimaryKey(TbOrder record);

    /**
     * 通过userID和status来查询
     * @param userId
     * @param status
     * @return
     */
    List<TbOrder> findOrderListByUserIdAndStatus(@Param("id")String userId,@Param("status")String status);
}