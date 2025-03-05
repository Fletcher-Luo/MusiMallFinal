package com.ByteAndHeartDance.coin.controller;

import cn.hutool.core.util.StrUtil;
import com.ByteAndHeartDance.coin.model.Coin;
import com.ByteAndHeartDance.coin.service.CoinLogService;
import com.ByteAndHeartDance.coin.service.CoinService;
import com.ByteAndHeartDance.exception.BusinessException;
import com.ByteAndHeartDance.exception.ErrorCode;
import com.ByteAndHeartDance.utils.ApiResult;
import com.ByteAndHeartDance.utils.ApiResultUtil;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author Cool
 * @Date 2025/2/14 上午11:30
 * 金币表
 */
@RestController
@RequestMapping("/coin")
@Slf4j
public class CoinController {

    @Resource
    private CoinService coinService;
    @Resource
    private CoinLogService coinLogService;

    //根据用户id查询金币表(钱包)
    @GetMapping("/getCoin")
    public ApiResult<Coin> getCoin(@RequestParam("userId") int userId) {
        //判断userId是否为空
        if (userId == 0) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR,"用户id不能为空");
        }
        Coin coin = coinService.getById(userId);
        if (coin == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR,"未创建钱包，请先创建钱包");
        }
        return ApiResultUtil.success(coin);
    }
    //创建金币表(钱包)
    @GetMapping("/create")
    public String createCoin(@RequestParam("userId") int userId) {
        if (userId == 0) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR,"用户id不能为空");
        }
        boolean result = coinService.createCoin(userId);

        return "创建金币表成功";
    }
    //充值，传三个参数，充值订单号，金额，充值描述（充值类型），用户id
    @GetMapping("/recharge")
    public String recharge(String orderNo, int amount, String desc, int userId) {
        //调用充值接口
        return "充值成功";
    }
    //消费，传三个参数，消费订单号，金额，消费描述（消费类型），用户id
    @GetMapping("/consume")
    public String consume(String orderNo, int amount, String desc, int userId) {
        //调用消费接口
        return "消费成功";
    }
    //退款，传三个参数，退款订单号，金额，退款描述（退款类型），用户id
    @GetMapping("/refund")
    public String refund(String orderNo, int amount, String desc, int userId) {
        //调用退款接口
        return "退款成功";
    }

    //查询余额，传一个参数，用户id
    @GetMapping("/getMoney")
    public String queryBalance(int userId) {
        //调用查询余额接口
        return "余额为100";
    }
    //查询用户交易记录
    @GetMapping("/getTradeRecord")
    public String queryTradeRecord(int userId) {
        //调用查询交易记录接口
        return "交易记录为100";
    }
    //查询用户充值记录
    @GetMapping("/getRechargeRecord")
    public String queryRechargeRecord(int userId) {
        //调用查询充值记录接口
        return "充值记录为100";
    }
    //查询用户消费记录
    @GetMapping("/getConsumeRecord")
    public String queryConsumeRecord(int userId) {
        //调用查询消费记录接口
        return "消费记录为100";
    }
    //逻辑删除用户充值账号
    @GetMapping("/delete")
    public String deleteCoin(int userId) {
        //调用删除用户充值账号接口
        return "删除用户充值账号成功";
    }

}
