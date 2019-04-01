package com.pinyougou.page.service;

import java.util.List;

public interface ItemPageService  {

    /**
     * 根据goodsId来生成页面
     * @param goodsIds
     * @return
     */
    boolean genItemPage(Long[] goodsIds);

    /**
     * 根据goodsIds删除页面
     * @param goodsIds
     */
    void deleteItemPage(Long[] goodsIds);
}
