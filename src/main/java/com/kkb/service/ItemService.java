package com.kkb.service;

import com.kkb.pojo.Item;

import java.util.List;

public interface ItemService {

    // 保存
    public void save(Item item);

    // 查询
    public List<Item> findAll(Item item);

}
