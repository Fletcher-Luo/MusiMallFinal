### 支付端口 50010（暂未集成到网关）

### 买家信息(支付宝沙箱app登录的账号，也是扫码付款的账号)
- **买家账号**: kmtbdm5144@sandbox.com
- **登录密码**: 111111
- **支付密码**: 111111



### 接口和对应参数及描述信息
1. 支付宝支付

   支付需要传递参数（订单号、总金额（双精度浮点）、支付的名称、支付宝交易凭证号）

   接口：`alipay/pay` 请求方式：`GET`
   
   请求参数：
   ```json
    {
      "traceNo": "1",
      "totalAmount": 100.0,
      "subject": "123456789",
      "alipayTraceNo": null
    }
    ```
2. 支付宝退款
   退款需要传递参数（订单号、退款金额（双精度浮点）、退款的名称、支付宝交易凭证号）
   接口：`alipay/return` 请求方式：`GET`

   请求参数：
   ```json
    {
      "traceNo": "1",
      "totalAmount": 100.0,
      "subject": "123456789",
      "alipayTraceNo": null
    }
    ```
### 启动需要配置沙箱环境
已配置在yml中
```yaml
#  支付服务端口:50010
server:
  port: 50010

alipay:
  appId: 9021000143681075
  appPrivateKey: MIIEvQIBADANBgkqhkiG9w0BAQEFAASCBKcwggSjAgEAAoIBAQCuudibII1/RFe843p2R2qdXZ2xZDpyfHW0O2HaVSp9HjyuXFXKnmd7CmTAkS8HRPWbcEClPWdQwE5UVs3P7/pcisDEjytJhB4xHKsPFvth5zFvIvWa9HlJXUDTuxziXKA6Ra4OVYkQsgZJkEraJl+Yzzd9b1cANDpFdsBeg7GUNBmxDwISHzCXXdU7sFqZKKyZRcf7lfe99HtGWzZAf3kHMC7vmdRXOT93TMUxe1myGpeoVWub0FusZxdySTNeUgMTNJADb4bJwbFdDQtqMlQeyX6LAzDAVw4k6B6k6SDR4snzeaauJDtOqSQYLxgJ9OoTQYzipOm3Fo1rlFULdswLAgMBAAECggEAd7QlPKoBprZIscTa6lulJCi114lGLZctAIxKTs1YLZ3gjsf9krIRTQ9i1TbiFEXfSl2OPGaj+IO3ZjqOCC5txKbeyAsZ6eTU1mrAmrbxBDLjADPgNgfVbDiyKByh/kbdEu8X/wQEwmZkvlNXpLrzdXpJlusHnMErRtnDkz0AFPq2Gi0TGhr9jErZ5HI/E7EalrJ5j8PbTDOLJhi+3BULzsTAmaQNBqfd0sQmGj+yboI9BUVH09TIbZM7YbjV6goxwFGdcIWkcDc7r7M6ULSYyawVdI24GqWXEjbvSSbbAUSsT2KOuwndYkSdmdl22+WSoZxh6GEA3X2BbszeaGjWUQKBgQDkMz7TK29F8U0lbpRHPhG+Z0zfDa47kIu45fJzHK+rrl51Umq6iUJkoCdDTmJgcda9puMwQyC/qvUx+JGLhAwr/9ulsh6BWygDR8EchfsaGhHRYLSEjqn97SATG77k+dfxIPGtKZiCGsAhSNrS8kaD0lo+lVGkfQ+k4/D+3xh4YwKBgQDEAu3xxba7ahBmjMnEz0trJ4ycTwZ9Vrl7VHb8/AahU0YlSzZ8g4j7CjjtlX+uD8125ticWTxNf0EqEScnetlJdESomWcGNVE9mz25d5CdZyndBYZVnhhnp4Rv9BIe1UGOmmvu4XZ4igi/T4f91iNNxPoxTfy3qZKZnePWrplqOQKBgHmjCyjabRyWC64ZLDZxUlnwjBwVGbssSB3NjBdpwgW8EyNsQ/GcOmSSrXsF7BBGfdVngI/4KBuiT8JMIaCBSU+muPiwYexHn6Nn+vpuPDNkk/zMFEzYEqqEW+Vzk6RPvJgOHGRV2LH4+lKDmAh7FbUsSeg0BWmtM8qLqajhWBTLAoGAMQUiiOKPbpN6AouXMlc9X1EGw1ioINR9krBvCZnl9pai5lTHDaC/szi9P/2xbtCY6/GTnpZalG8M1v5vMlpw2QPw39FtiInxBV07tJSIYDUarSHilKrzI4krf+wkU/21ZXqNH7LXNBIhWecD9aGGNr7aa1Y7q5sHBNAN2PTVfVECgYEAkOdUJsl8du8O2wdjrrjhhXQ7Qz8wnDSFgxdLV6PFcC+3cho3hf5sIMfvh3Bmhd3eEit3hsr1T5ja9fb+ICHlftFrzxQMV0kOKz9yWrk5TX4xCVB9RlUO9qtjsa1xxbF57DaskDIi3M7ciD34nseHz9UJAjZduW3Zwg+bo5H7gCc=
  alipayPublicKey: MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEApNVCsVpNDGm31v1IwtBTPbOw+truUf9KRmOUoxloxW6DRTxkPJUMicUYx0i3g0DPKQncC3r14GSVxrRTJD3qOeE1UvCwWCfXz/bLby3agmTgbwMYrBuplAKCfLPAcRBstT4YopKEpDauyXMjvUsrUYNGHWyDgkGtImexk7aZ6IrY2QRPaMmQ4GlxBpNCuOgiTyDefshukNF+/l/JwfAgwe79DyaeLos5b5/JwOEHH6DPG5gbWJYRPWwfQlE5tsT82snlBPC6SGEN3MYg9Kvwf+77irMixbH1hvGi14v5KWWVTIG7MBMDVctOfFR1W48ZPDrDGVjLBoxKYPtuhpJ24QIDAQAB
  notifyUrl: http://124.221.142.75:50010/alipay/notify
  returnUrl: http://124.221.142.75:29839/orders

#  应用私钥:MIIEvQIBADANBgkqhkiG9w0BAQEFAASCBKcwggSjAgEAAoIBAQCuudibII1/RFe843p2R2qdXZ2xZDpyfHW0O2HaVSp9HjyuXFXKnmd7CmTAkS8HRPWbcEClPWdQwE5UVs3P7/pcisDEjytJhB4xHKsPFvth5zFvIvWa9HlJXUDTuxziXKA6Ra4OVYkQsgZJkEraJl+Yzzd9b1cANDpFdsBeg7GUNBmxDwISHzCXXdU7sFqZKKyZRcf7lfe99HtGWzZAf3kHMC7vmdRXOT93TMUxe1myGpeoVWub0FusZxdySTNeUgMTNJADb4bJwbFdDQtqMlQeyX6LAzDAVw4k6B6k6SDR4snzeaauJDtOqSQYLxgJ9OoTQYzipOm3Fo1rlFULdswLAgMBAAECggEAd7QlPKoBprZIscTa6lulJCi114lGLZctAIxKTs1YLZ3gjsf9krIRTQ9i1TbiFEXfSl2OPGaj+IO3ZjqOCC5txKbeyAsZ6eTU1mrAmrbxBDLjADPgNgfVbDiyKByh/kbdEu8X/wQEwmZkvlNXpLrzdXpJlusHnMErRtnDkz0AFPq2Gi0TGhr9jErZ5HI/E7EalrJ5j8PbTDOLJhi+3BULzsTAmaQNBqfd0sQmGj+yboI9BUVH09TIbZM7YbjV6goxwFGdcIWkcDc7r7M6ULSYyawVdI24GqWXEjbvSSbbAUSsT2KOuwndYkSdmdl22+WSoZxh6GEA3X2BbszeaGjWUQKBgQDkMz7TK29F8U0lbpRHPhG+Z0zfDa47kIu45fJzHK+rrl51Umq6iUJkoCdDTmJgcda9puMwQyC/qvUx+JGLhAwr/9ulsh6BWygDR8EchfsaGhHRYLSEjqn97SATG77k+dfxIPGtKZiCGsAhSNrS8kaD0lo+lVGkfQ+k4/D+3xh4YwKBgQDEAu3xxba7ahBmjMnEz0trJ4ycTwZ9Vrl7VHb8/AahU0YlSzZ8g4j7CjjtlX+uD8125ticWTxNf0EqEScnetlJdESomWcGNVE9mz25d5CdZyndBYZVnhhnp4Rv9BIe1UGOmmvu4XZ4igi/T4f91iNNxPoxTfy3qZKZnePWrplqOQKBgHmjCyjabRyWC64ZLDZxUlnwjBwVGbssSB3NjBdpwgW8EyNsQ/GcOmSSrXsF7BBGfdVngI/4KBuiT8JMIaCBSU+muPiwYexHn6Nn+vpuPDNkk/zMFEzYEqqEW+Vzk6RPvJgOHGRV2LH4+lKDmAh7FbUsSeg0BWmtM8qLqajhWBTLAoGAMQUiiOKPbpN6AouXMlc9X1EGw1ioINR9krBvCZnl9pai5lTHDaC/szi9P/2xbtCY6/GTnpZalG8M1v5vMlpw2QPw39FtiInxBV07tJSIYDUarSHilKrzI4krf+wkU/21ZXqNH7LXNBIhWecD9aGGNr7aa1Y7q5sHBNAN2PTVfVECgYEAkOdUJsl8du8O2wdjrrjhhXQ7Qz8wnDSFgxdLV6PFcC+3cho3hf5sIMfvh3Bmhd3eEit3hsr1T5ja9fb+ICHlftFrzxQMV0kOKz9yWrk5TX4xCVB9RlUO9qtjsa1xxbF57DaskDIi3M7ciD34nseHz9UJAjZduW3Zwg+bo5H7gCc=
#  应用公钥：MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEArrnYmyCNf0RXvON6dkdqnV2dsWQ6cnx1tDth2lUqfR48rlxVyp5newpkwJEvB0T1m3BApT1nUMBOVFbNz+/6XIrAxI8rSYQeMRyrDxb7YecxbyL1mvR5SV1A07sc4lygOkWuDlWJELIGSZBK2iZfmM83fW9XADQ6RXbAXoOxlDQZsQ8CEh8wl13VO7BamSismUXH+5X3vfR7Rls2QH95BzAu75nUVzk/d0zFMXtZshqXqFVrm9BbrGcXckkzXlIDEzSQA2+GycGxXQ0LajJUHsl+iwMwwFcOJOgepOkg0eLJ83mmriQ7TqkkGC8YCfTqE0GM4qTptxaNa5RVC3bMCwIDAQAB
#  支付宝公钥：MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAgdWRcUvzFWyW75KGuuSUXcozCEHFVfBK7ag8Lz/NhAAjJlMciL5wGTmVC7wzVOAC6W+BTMvRYUOmOmZYDnhtN2ZJO2HcL1ESvUP4b/YP6qmG5fpUp+6ukoRDu44Toxe9IqUVylzXh3j17inspf2QXoX8LexchTFY98m/X+odfHh2mKZ/8/xkYwY1M7yTud8plQCopmuzv3ybCrOJ5M+TyqwW40wcPT1Gj1OaGMQzpJPiolxdOtfIBSryiUQ4nvWfcmcLMk0PvNhIX/lxZVfFCGmM8ohobJstl7sFnjmtje+tUN++bzUcVBdjCaysNgG1l+Hk3NFqcaL/zGJr1Yb7uQIDAQAB

```