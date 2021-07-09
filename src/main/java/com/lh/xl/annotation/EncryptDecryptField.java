package com.lh.xl.annotation;

import java.lang.annotation.*;

/**
 * @author lql
 * @date 2021/7/7 19:15
 */
@Documented
@Inherited
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface EncryptDecryptField {
}
