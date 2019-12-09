package com.kkb.service.impl;

import com.kkb.dao.ItemDao;
import com.kkb.pojo.Item;
import com.kkb.service.ItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ItemServiceImpl implements ItemService {

    @Autowired
    private ItemDao itemDao;

    @Override
    public void save(Item item) {
        itemDao.save(item);
    }

    @Override
    public List<Item> findAll(Item item) {

        Example<Item> example = Example.of(item);

        return itemDao.findAll(example);
    }
}
