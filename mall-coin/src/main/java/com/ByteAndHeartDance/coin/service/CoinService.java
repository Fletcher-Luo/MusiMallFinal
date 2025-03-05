package com.ByteAndHeartDance.coin.service;

import com.ByteAndHeartDance.coin.model.Coin;
import com.baomidou.mybatisplus.extension.service.IService;

/**
* @author Cool
* @description 针对表【coin】的数据库操作Service
* @createDate 2025-02-14 11:43:26
*/
public interface CoinService extends IService<Coin> {

    boolean createCoin(int userId);
}
