package com.mall.product_service.listener;

import com.mall.product_service.service.InventorySyncService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class ApplicationStartupListener {

    @Autowired
    private InventorySyncService inventorySyncService;

    @EventListener(ContextRefreshedEvent.class)
    public void onApplicationEvent(ContextRefreshedEvent event) {
        // 当 Spring 容器初始化完成后，同步商品库存信息到 Redis
        inventorySyncService.syncInventoryToRedis();
    }
}

