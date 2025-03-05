package com.ByteAndHeartDance.pay.model;

import lombok.Data;

@Data
public class AliPay {
    private String traceNo;
    private double totalAmount;
    private String subject;
    private String alipayTraceNo;

    @Override
    public String toString() {
        return "AliPay{" +
                "traceNo='" + traceNo + '\'' +
                ", totalAmount=" + totalAmount +
                ", subject='" + subject + '\'' +
                ", alipayTraceNo='" + alipayTraceNo + '\'' +
                '}';
    }
}
