package com.kkb.dao;

import com.kkb.pojo.Item;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ItemDao extends JpaRepository<Item, Long > {
}
