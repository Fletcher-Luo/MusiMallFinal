package com.ByteAndHeartDance.sensitive;

import cn.hutool.core.text.CharSequenceUtil;
import org.springframework.stereotype.Service;


@Service
public class CustomMaskService implements ICustomMaskService {
    @Override
    public String maskData(String data) {
        return CharSequenceUtil.hide(data, 1, data.length());
    }
}
