package com.ByteAndHeartDance.coin.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ByteAndHeartDance.coin.model.Coin;
import com.ByteAndHeartDance.coin.service.CoinService;
import com.ByteAndHeartDance.coin.mapper.CoinMapper;
import org.springframework.stereotype.Service;

/**
* @author Cool
* @description 针对表【coin】的数据库操作Service实现
* @createDate 2025-02-14 11:43:26
*/
@Service
public class CoinServiceImpl extends ServiceImpl<CoinMapper, Coin>
    implements CoinService{

    /**
     * 创建钱包
     * @param userId
     * @return
     */
    @Override
    public boolean createCoin(int userId) {
        Coin coin = new Coin();
        boolean result = save(coin);
        return false;
    }
}




