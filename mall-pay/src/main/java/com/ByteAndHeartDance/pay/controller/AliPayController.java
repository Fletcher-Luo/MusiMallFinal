package com.ByteAndHeartDance.pay.controller;


import cn.hutool.json.JSONObject;
import com.ByteAndHeartDance.pay.common.ApiResult;
import com.ByteAndHeartDance.pay.common.ApiResultUtil;
import com.ByteAndHeartDance.pay.config.AliPayConfig;
import com.ByteAndHeartDance.pay.model.AliPay;
import com.ByteAndHeartDance.pay.utils.RedisUtil;
import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.internal.util.AlipaySignature;
import com.alipay.api.request.AlipayTradePagePayRequest;
import com.alipay.api.request.AlipayTradeRefundRequest;
import com.alipay.api.response.AlipayTradeRefundResponse;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;


@RestController
@RequestMapping("/alipay")
@Slf4j
public class AliPayController {

    private static final String GATEWAY_URL = "https://openapi-sandbox.dl.alipaydev.com/gateway.do";
    private static final String FORMAT = "JSON";
    private static final String CHARSET = "UTF-8";

    // 签名方式
    private static final String SIGN_TYPE = "RSA2";

    @Resource
    private AliPayConfig aliPayConfig;
    @Resource
    private RedisUtil redisUtil;

    private static final String PAY_ID = "payId:"; // 支付ID前缀（存入redis）
    private static final String ORDER_PAY_ID = "order:pay:"; // 支付ID前缀（存入redis）
    private static final Integer PAY_ID_PRE_CREATE_EXPIRE_TIME = 15 * 60; // 支付ID预分配过期时间（15分钟）
    private static final String PAY_STATUS_CANNEL = "已退款";

    @GetMapping("/pay") // &subject=xxx&traceNo=xxx&totalAmount=xxx
    public void pay(AliPay aliPay, HttpServletResponse httpResponse) throws Exception {
        log.info("开始pay方法");
        // 1. 创建Client，通用SDK提供的Client，负责调用支付宝的API
        AlipayClient alipayClient = new DefaultAlipayClient(GATEWAY_URL, aliPayConfig.getAppId(),
                aliPayConfig.getAppPrivateKey(), FORMAT, CHARSET, aliPayConfig.getAlipayPublicKey(), SIGN_TYPE);

        // 2. 创建 Request并设置Request参数
        AlipayTradePagePayRequest request = new AlipayTradePagePayRequest();  // 发送请求的 Request类
        request.setNotifyUrl(aliPayConfig.getNotifyUrl());
        request.setReturnUrl(aliPayConfig.getReturnUrl());
        log.info("Notify:======="+request.getNotifyUrl());
        log.info("Return:======="+request.getReturnUrl());
        JSONObject bizContent = new JSONObject();
        bizContent.set("out_trade_no", aliPay.getTraceNo());  // 我们自己生成的订单编号
        bizContent.set("total_amount", aliPay.getTotalAmount()); // 订单的总金额
        bizContent.set("subject", aliPay.getSubject());   // 支付的名称
        bizContent.set("timeout_express", aliPayConfig.getTimeoutExpress());  // 超时时间
        bizContent.set("product_code", "FAST_INSTANT_TRADE_PAY");  // 固定配置
        request.setBizContent(bizContent.toString());

        // 执行请求，拿到响应的结果，返回给浏览器
        String form = "";
        try {
            form = alipayClient.pageExecute(request).getBody(); // 调用SDK生成表单
        } catch (AlipayApiException e) {
            e.printStackTrace();
        }
        httpResponse.setContentType("text/html;charset=" + CHARSET);
        httpResponse.getWriter().write(form);// 直接将完整的表单html输出到页面
        httpResponse.getWriter().flush();
        httpResponse.getWriter().close();
        log.info("pay方法结束");
    }

    @PostMapping("/notify")  // 注意这里必须是POST接口
    public String payNotify(HttpServletRequest request) throws Exception {
        System.out.println("执行notify");
        if (request.getParameter("trade_status").equals("TRADE_SUCCESS")) {
            System.out.println("=========支付宝异步回调========");

            Map<String, String> params = new HashMap<>();
            Map<String, String[]> requestParams = request.getParameterMap();
            for (String name : requestParams.keySet()) {
                params.put(name, request.getParameter(name));
                // System.out.println(name + " = " + request.getParameter(name));
            }

            String outTradeNo = params.get("out_trade_no");
            String gmtPayment = params.get("gmt_payment");
            String alipayTradeNo = params.get("trade_no");

            String sign = params.get("sign");
            String content = AlipaySignature.getSignCheckContentV1(params);
            boolean checkSignature = AlipaySignature.rsa256CheckContent(content, sign, aliPayConfig.getAlipayPublicKey(), "UTF-8"); // 验证签名
            // 支付宝验签
            if (checkSignature) {
                // 验签通过
                System.out.println("交易名称: " + params.get("subject"));
                System.out.println("交易状态: " + params.get("trade_status"));
                System.out.println("支付宝交易凭证号: " + params.get("trade_no"));
                System.out.println("商户订单号: " + params.get("out_trade_no"));
                System.out.println("交易金额: " + params.get("total_amount"));
                System.out.println("买家在支付宝唯一id: " + params.get("buyer_id"));
                System.out.println("买家付款时间: " + params.get("gmt_payment"));
                System.out.println("买家付款金额: " + params.get("buyer_pay_amount"));

                // 更新订单未已支付
                redisUtil.set(ORDER_PAY_ID+outTradeNo, alipayTradeNo);
            }
        }
        return "success";
    }

    /**
     * 退款（调用这个接口之前需要判断支付是否超过七天减少流程的压力）
     *
     * @param aliPay 支付宝回调的订单信息
     * @return ApiResult 返回结果(支付后去找字段)
     * @throws AlipayApiException 异常
     */
    @GetMapping("/return")
    public ApiResult<String> returnPay(AliPay aliPay) throws AlipayApiException {
        log.info(aliPay.toString());
        // // 7天无理由退款
        // String now = DateUtil.now();
        // Order orders = orderMapper.getByNo(aliPay.getTraceNo());
        // if (orders != null) {
        //     // hutool工具类，判断时间间隔
        //     long between = DateUtil.between(DateUtil.parseDateTime(orders.getPaymentTime()), DateUtil.parseDateTime(now), DateUnit.DAY);
        //     if (between > 7) {
        //         return ApiResultUtil.error(50007, "该订单已超过7天，不支持退款");
        //     }
        // }
        // 1. 创建Client，通用SDK提供的Client，负责调用支付宝的API
        AlipayClient alipayClient = new DefaultAlipayClient(GATEWAY_URL,
                aliPayConfig.getAppId(), aliPayConfig.getAppPrivateKey(), FORMAT, CHARSET,
                aliPayConfig.getAlipayPublicKey(), SIGN_TYPE);
        // 2. 创建 Request，设置参数
        AlipayTradeRefundRequest request = new AlipayTradeRefundRequest();
        JSONObject bizContent = new JSONObject();
        bizContent.set("trade_no", aliPay.getAlipayTraceNo());  // 支付宝回调的订单流水号
        bizContent.set("refund_amount", aliPay.getTotalAmount());  // 订单的总金额
        bizContent.set("out_trade_no", aliPay.getTraceNo());   //  我的订单编号

        // 返回参数选项，按需传入
        // JSONArray queryOptions = new JSONArray();
        // queryOptions.add("refund_detail_item_list");
        // bizContent.put("query_options", queryOptions);

        request.setBizContent(bizContent.toString());

        // 3. 执行请求
        AlipayTradeRefundResponse response = alipayClient.execute(request);
        if (response.isSuccess()) {  // 退款成功，isSuccess 为true
            log.info("退款成功");

            // 4. 更新Redis状态
            // tradeId（订单id）, payState（状态）, refundTime（退款时间）
            redisUtil.set(PAY_ID + aliPay.getTraceNo(), PAY_STATUS_CANNEL);
            log.info("更新订单状态为已退款，存入redis");
            return ApiResultUtil.success();
        } else {   // 退款失败，isSuccess 为false
            System.out.println(response.getBody());
            return ApiResultUtil.error(50009, response.getBody());
        }
    }
}


