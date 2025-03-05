package com.ByteAndHeartDance.utils;

import com.ByteAndHeartDance.exception.BusinessException;
import org.springframework.lang.Nullable;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import java.util.Collection;

/**
 * 断言工具
 */
public abstract class AssertUtil {

    public static final int ASSERT_ERROR_CODE = 1;

    private AssertUtil() {
    }

    public static void state(boolean expression, String message) {
        if (!expression) {
            throw new BusinessException(ASSERT_ERROR_CODE, message);
        }
    }

    public static void isTrue(boolean expression, String message) {
        if (!expression) {
            throw new BusinessException(ASSERT_ERROR_CODE, message);
        }
    }

    public static void isNull(@Nullable Object object, String message) {
        if (object != null) {
            throw new BusinessException(ASSERT_ERROR_CODE, message);
        }
    }

    public static void notNull(@Nullable Object object, String message) {
        if (object == null) {
            throw new BusinessException(ASSERT_ERROR_CODE, message);
        }
    }

    public static void hasLength(@Nullable String text, String message) {
        if (!StringUtils.hasLength(text)) {
            throw new BusinessException(ASSERT_ERROR_CODE, message);
        }
    }

    public static void notEmpty(@Nullable Collection<?> collection, String message) {
        if (CollectionUtils.isEmpty(collection)) {
            throw new BusinessException(ASSERT_ERROR_CODE, message);
        }
    }

    public static void notEmpty(@Nullable Object[] array, String message) {
        if (ObjectUtils.isEmpty(array)) {
            throw new BusinessException(ASSERT_ERROR_CODE, message);
        }
    }

    public static void doesNotContain(@Nullable String textToSearch, String substring, String message) {
        if (StringUtils.hasLength(textToSearch) && StringUtils.hasLength(substring) && textToSearch.contains(substring)) {
            throw new BusinessException(ASSERT_ERROR_CODE, message);
        }
    }
}
