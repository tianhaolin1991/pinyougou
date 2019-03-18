package com.pinyougou.page.service;

import java.util.List;

public interface ItemPageService  {

    /**
     * 根据goodsId来生成页面
     * @param goodsId
     * @return
     */
    boolean genItemPage(Long goodsId);
}
